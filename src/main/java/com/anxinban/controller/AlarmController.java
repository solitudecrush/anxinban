package com.anxinban.controller;
import com.anxinban.dto.AlarmDto;
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.PageResult;
import com.anxinban.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 告警管理控制器。
 *
 * <p>功能说明：提供告警的创建、查询、确认、解决、标记已读以及未读数统计等接口，
 * 覆盖普通告警与入侵告警两类业务场景。</p>
 *
 * <p>对应前端模块：告警中心 / 实时监控 / 入侵检测 / 告警详情页。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/alarm")
public class AlarmController {
    private final AlarmService alarmService;

    /**
     * 构造方法，注入告警服务。
     *
     * @param alarmService 告警服务实例
     */
    @Autowired
    public AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }
    /**
     * 新增接口，处理 POST  请求。
     *
     * @param alarm alarm 参数
     * @return 处理结果
     */
    @PostMapping
    public ApiResponse<AlarmDto> createAlarm(@RequestBody AlarmDto alarm) {
        AlarmDto saved = alarmService.createAlarm(alarm);
        return ApiResponse.created(saved);
    }
    /**
     * 查询接口，处理 GET /{alarmId} 请求。
     *
     * @param alarmId alarmId 参数
     * @return 处理结果
     */
    @GetMapping("/{alarmId}")
    public ApiResponse<AlarmDto> getAlarm(@PathVariable String alarmId) {
        AlarmDto alarm = alarmService.getAlarm(alarmId);
        // 校验告警是否存在，不存在则返回业务 404
        if (alarm == null) {
            return ApiResponse.error(404, "告警不存在");
        }
        return ApiResponse.success(alarm);
    }
    @GetMapping("/list")
    public ApiResponse<PageResult<AlarmDto>> listAlarms(
            @RequestParam(required = false) String elderId,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String alarmType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResult<AlarmDto> result = alarmService.listAlarms(elderId, deviceId, alarmType, status, startTime, endTime, page, pageSize);
        return ApiResponse.success(result);
    }
    @GetMapping("/intrusion/list")
    public ApiResponse<PageResult<AlarmDto>> listIntrusionAlarms(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String building,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResult<AlarmDto> result = alarmService.listIntrusionAlarms(status, building, page, pageSize);
        return ApiResponse.success(result);
    }
    /**
     * 查询接口，处理 GET /intrusion/{alarmId}/snapshot 请求。
     *
     * @param alarmId alarmId 参数
     * @return 处理结果
     */
    @GetMapping("/intrusion/{alarmId}/snapshot")
    public ApiResponse<Map<String, String>> getIntrusionSnapshot(@PathVariable String alarmId) {
        AlarmDto alarm = alarmService.getAlarm(alarmId);
        // 校验告警是否存在
        if (alarm == null) {
            return ApiResponse.error(404, "告警不存在");
        }
        // 如果快照 URL 为空则返回空字符串，避免前端解析 null
        return ApiResponse.success(Map.of("snapshotUrl", alarm.getSnapshotUrl() != null ? alarm.getSnapshotUrl() : ""));
    }
    @PutMapping("/{alarmId}/acknowledge")
    public ApiResponse<AlarmDto> acknowledgeAlarm(@PathVariable String alarmId,
            @RequestBody AlarmHandleRequest request) {
        AlarmDto alarm = alarmService.acknowledgeAlarm(alarmId, request.getHandler(), request.getHandleTime());
        if (alarm == null) {
            return ApiResponse.error(404, "告警不存在");
        }
        return ApiResponse.success(alarm);
    }
    @PutMapping("/{alarmId}/resolve")
    public ApiResponse<AlarmDto> resolveAlarm(@PathVariable String alarmId,
            @RequestBody AlarmHandleRequest request) {
        AlarmDto alarm = alarmService.resolveAlarm(alarmId, request.getHandler(), request.getHandleTime(),
                request.getRemark());
        if (alarm == null) {
            return ApiResponse.error(404, "告警不存在");
        }
        return ApiResponse.success(alarm);
    }
    /**
     * 处理 PUT /{alarmId}/read 请求。
     *
     * @param alarmId alarmId 参数
     * @return 处理结果
     */
    @PutMapping("/{alarmId}/read")
    public ApiResponse<AlarmDto> markAlarmAsRead(@PathVariable String alarmId) {
        AlarmDto alarm = alarmService.markAsRead(alarmId);
        if (alarm == null) {
            return ApiResponse.error(404, "告警不存在");
        }
        return ApiResponse.success(alarm);
    }
    /**
     * 查询接口，处理 GET /unread-count 请求。
     *
     * @param elderId elderId 参数
     * @return 处理结果
     */
    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> getUnreadCount(@RequestParam String elderId) {
        long count = alarmService.getUnreadCount(elderId);
        return ApiResponse.success(Map.of("count", count));
    }
    public static class AlarmHandleRequest {
        private String handler;
        private String handleTime;
        private String remark;

        /**
         * 获取处理人。
         *
         * @return 处理人
         */
        /**
         * 查询接口，处理 请求  请求。
         * @return 处理结果
         */
        public String getHandler() {
            return handler;
        }

        /**
         * 设置处理人。
         *
         * @param handler 处理人
         */
        /**
         * 处理 请求  请求。
         *
         * @param handler handler 参数
         */
        public void setHandler(String handler) {
            this.handler = handler;
        }

        /**
         * 获取处理时间。
         *
         * @return 处理时间
         */
        /**
         * 查询接口，处理 请求  请求。
         * @return 处理结果
         */
        public String getHandleTime() {
            return handleTime;
        }

        /**
         * 设置处理时间。
         *
         * @param handleTime 处理时间
         */
        /**
         * 处理 请求  请求。
         *
         * @param handleTime handleTime 参数
         */
        public void setHandleTime(String handleTime) {
            this.handleTime = handleTime;
        }

        /**
         * 获取备注。
         *
         * @return 备注
         */
        /**
         * 查询接口，处理 请求  请求。
         * @return 处理结果
         */
        public String getRemark() {
            return remark;
        }

        /**
         * 设置备注。
         *
         * @param remark 备注
         */
        /**
         * 处理 请求  请求。
         *
         * @param remark remark 参数
         */
        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
