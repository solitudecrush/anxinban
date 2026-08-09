package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.EdgeDataRequest;
import com.anxinban.dto.EdgeDataResponse;
import com.anxinban.service.EdgeGatewayService;
import com.anxinban.service.EdgeGatewayService.DeviceNotFoundException;
import com.anxinban.service.EdgeGatewayService.DuplicateUploadException;
import com.anxinban.service.EdgeGatewayService.TopicMismatchException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 家庭 PC 边缘网关统一数据接入控制器。
 *
 * <p>提供 {@code POST /api/edge/data} 端点，接收 PC 通过公网 HTTP 上传的
 * ESP32 手表数据。Controller 只负责参数校验和响应映射，业务逻辑全部委托给
 * {@link EdgeGatewayService}。</p>
 *
 * <p>本接口不修改现有 {@code POST /api/device/upload}，与现有系统完全解耦。</p>
 *
 * <h3>Topic 与 message_type 映射</h3>
 * <table>
 *   <tr><th>mqtt_topic</th><th>message_type</th></tr>
 *   <tr><td>anxinban/telemetry/vitals</td><td>vitals</td></tr>
 *   <tr><td>anxinban/telemetry/imu</td><td>imu</td></tr>
 *   <tr><td>anxinban/event/fall</td><td>fall</td></tr>
 *   <tr><td>anxinban/event/sos</td><td>sos</td></tr>
 *   <tr><td>anxinban/status/device</td><td>device_status</td></tr>
 * </table>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/edge")
public class EdgeGatewayController {

    private static final Logger log = LoggerFactory.getLogger(EdgeGatewayController.class);

    private final EdgeGatewayService edgeGatewayService;

    @Autowired
    public EdgeGatewayController(EdgeGatewayService edgeGatewayService) {
        this.edgeGatewayService = edgeGatewayService;
    }

    /**
     * 边缘网关统一数据上报入口。
     *
     * <p>PC 通过公网 {@code http://elderlyweb.cn/api/edge/data} 调用此接口，
     * 将 ESP32 经 MQTT 上报的数据以 HTTP POST JSON 方式传输到云端。</p>
     *
     * <p>响应状态码：</p>
     * <ul>
     *   <li>200 — 正常处理（含幂等重复）</li>
     *   <li>400 — 协议错误（api_version 不正确、topic/type 不匹配、必填字段缺失）</li>
     *   <li>404 — 设备未注册</li>
     *   <li>422 — 业务字段错误</li>
     *   <li>500 — 服务内部异常</li>
     * </ul>
     *
     * @param request 边缘网关请求体（含 HTTP wrapper + MQTT payload）
     * @return 统一 API 响应
     */
    @PostMapping("/data")
    public ResponseEntity<ApiResponse<EdgeDataResponse>> receiveData(@Valid @RequestBody EdgeDataRequest request) {
        log.info("边缘网关数据接入: upload_id={}, edge_id={}, topic={}, message_type={}",
                request.getUploadId(), request.getEdgeId(), request.getMqttTopic(),
                request.getPayload().getMessageType());

        try {
            // 1. Topic 与 message_type 一致性校验
            edgeGatewayService.validateTopicTypeMatch(
                    request.getMqttTopic(),
                    request.getPayload().getMessageType());

            // 2. 幂等快速路径：如果已处理过，直接返回（避免进入事务）
            if (edgeGatewayService.isDuplicate(request.getUploadId())) {
                EdgeDataResponse dupResponse = EdgeDataResponse.duplicate(
                        request.getUploadId(),
                        request.getPayload().getMessageType());
                return ResponseEntity.ok(ApiResponse.success(dupResponse));
            }

            // 3. 业务处理（事务内）
            EdgeDataResponse response = edgeGatewayService.process(request);
            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (TopicMismatchException e) {
            log.warn("Topic/type 不匹配: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));

        } catch (DeviceNotFoundException e) {
            log.warn("设备未注册: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));

        } catch (DuplicateUploadException e) {
            // 并发重复插入冲突 → 返回 duplicate 响应
            EdgeDataResponse dupResponse = EdgeDataResponse.duplicate(
                    e.getUploadId(), e.getMessageType());
            return ResponseEntity.ok(ApiResponse.success(dupResponse));

        } catch (IllegalArgumentException e) {
            log.warn("业务参数错误: {}", e.getMessage());
            return ResponseEntity.unprocessableEntity()
                    .body(ApiResponse.error(422, e.getMessage()));

        } catch (Exception e) {
            log.error("边缘网关数据处理异常: upload_id={}, error={}",
                    request.getUploadId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务内部异常: " + e.getMessage()));
        }
    }
}
