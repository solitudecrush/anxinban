package com.anxinban.controller;
import com.anxinban.dto.ApiResponse;
import com.anxinban.entity.AlarmEvent;
import com.anxinban.entity.SensorData;
import com.anxinban.mqtt.dto.CameraFindItemResp;
import com.anxinban.mqtt.dto.AiConversationResp;
import com.anxinban.mqtt.simulator.DeviceSimulator;
import com.anxinban.mqtt.service.AIAgentService;
import com.anxinban.mapper.AlarmEventRepository;
import com.anxinban.mapper.SensorDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 设备数据控制器。
 *
 * 作用：提供 REST API 接口，用于查询设备历史数据、告警事件，以及控制设备模拟器。
 *
 * API 列表：
 * - GET /api/device/sensor/temperature-humidity - 查询温湿度历史数据
 * - GET /api/device/alarm - 查询告警事件列表
 * - GET /api/device/simulator/status - 获取模拟器状态
 * - POST /api/device/simulator/start - 启动模拟器
 * - POST /api/device/simulator/stop - 停止模拟器
 * - POST /api/device/simulator/trigger/* - 触发模拟事件
 * - POST /api/ai/chat - AI 智能体对话
 * - POST /api/ai/find-item - AI 查找物品
 */
@RestController
@RequestMapping("/api")
public class DeviceDataController {

    private static final Logger log = LoggerFactory.getLogger(DeviceDataController.class);
    private final SensorDataRepository sensorDataRepository;
    private final AlarmEventRepository alarmEventRepository;
    private final DeviceSimulator deviceSimulator;
    /** 年龄 */
    private final AIAgentService aiAgentService;

    public DeviceDataController(SensorDataRepository sensorDataRepository,
                                AlarmEventRepository alarmEventRepository,
                                DeviceSimulator deviceSimulator,
                                AIAgentService aiAgentService) {
        this.sensorDataRepository = sensorDataRepository;
        this.alarmEventRepository = alarmEventRepository;
        this.deviceSimulator = deviceSimulator;
        this.aiAgentService = aiAgentService;
    }

    // ==================== 温湿度历史数据查询 ====================
    @GetMapping("/device/sensor/temperature-humidity")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTemperatureHumidityHistory(
            @RequestParam(required = false) String room,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "100") int limit) {

        log.info("查询温湿度历史数据：room={}, deviceId={}, startTime={}, endTime={}", 
                room, deviceId, startTime, endTime);

        // 默认时间范围：过去24小时
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        if (startTime == null) {
            startTime = endTime.minusHours(24);
        }

        // 查询温度数据
        List<SensorData> temperatureData = sensorDataRepository.findBySensorTypeAndTimestampBetweenOrderByTimestampDesc(
                "temperature", startTime, endTime, PageRequest.of(0, limit));

        // 查询湿度数据
        List<SensorData> humidityData = sensorDataRepository.findBySensorTypeAndTimestampBetweenOrderByTimestampDesc(
                "humidity", startTime, endTime, PageRequest.of(0, limit));

        Map<String, Object> result = new HashMap<>();
        result.put("temperature", temperatureData);
        result.put("humidity", humidityData);
        result.put("startTime", startTime);
        result.put("endTime", endTime);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ==================== 传感器数据查询 ====================
    @GetMapping("/device/sensor/{sensorType}")
    public ResponseEntity<ApiResponse<List<SensorData>>> getSensorDataHistory(
            @PathVariable String sensorType,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "100") int limit) {

        log.info("查询传感器历史数据：type={}, deviceId={}", sensorType, deviceId);

        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        if (startTime == null) {
            startTime = endTime.minusHours(24);
        }

        List<SensorData> data = sensorDataRepository.findBySensorTypeAndTimestampBetweenOrderByTimestampDesc(
                sensorType, startTime, endTime, PageRequest.of(0, limit));

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // ==================== 告警事件查询 ====================
    @GetMapping("/device/alarm")
    public ResponseEntity<ApiResponse<Page<AlarmEvent>>> getAlarmEvents(
            @RequestParam(required = false) String alarmType,
            @RequestParam(required = false) String alarmLevel,
            @RequestParam(required = false) String alarmStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("查询告警事件：type={}, level={}, status={}", alarmType, alarmLevel, alarmStatus);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AlarmEvent> events;

        if (alarmType != null && !alarmType.isEmpty()) {
            events = alarmEventRepository.findByType(alarmType, pageable);
        } else if (alarmLevel != null && !alarmLevel.isEmpty()) {
            events = alarmEventRepository.findByRiskLevel(alarmLevel, pageable);
        } else if (alarmStatus != null && !alarmStatus.isEmpty()) {
            events = alarmEventRepository.findByStatus(alarmStatus, pageable);
        } else {
            events = alarmEventRepository.findAll(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(events));
    }

    // ==================== 设备模拟器控制 ====================
    /**
     * 查询接口，处理 GET /device/simulator/status 请求。
     * @return 处理结果
     */
    @GetMapping("/device/simulator/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSimulatorStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("running", deviceSimulator.isRunning());
        return ResponseEntity.ok(ApiResponse.success(status));
    }
    /**
     * 处理 POST /device/simulator/start 请求。
     * @return 处理结果
     */
    @PostMapping("/device/simulator/start")
    public ResponseEntity<ApiResponse<String>> startSimulator() {
        log.info("启动设备模拟器");
        deviceSimulator.start();
        return ResponseEntity.ok(ApiResponse.success("模拟器已启动"));
    }
    /**
     * 处理 POST /device/simulator/stop 请求。
     * @return 处理结果
     */
    @PostMapping("/device/simulator/stop")
    public ResponseEntity<ApiResponse<String>> stopSimulator() {
        log.info("停止设备模拟器");
        deviceSimulator.stop();
        return ResponseEntity.ok(ApiResponse.success("模拟器已停止"));
    }
    @PostMapping("/device/simulator/trigger/fingerprint-success")
    public ResponseEntity<ApiResponse<String>> triggerFingerprintSuccess(
            @RequestParam(defaultValue = "user-001") String userId) {
        log.info("触发模拟指纹开锁成功：userId={}", userId);
        deviceSimulator.triggerFingerprintSuccess(userId);
        return ResponseEntity.ok(ApiResponse.success("已触发指纹开锁成功事件"));
    }
    /**
     * 处理 POST /device/simulator/trigger/fingerprint-fail 请求。
     * @return 处理结果
     */
    @PostMapping("/device/simulator/trigger/fingerprint-fail")
    public ResponseEntity<ApiResponse<String>> triggerFingerprintFail() {
        log.info("触发模拟指纹开锁失败");
        deviceSimulator.triggerFingerprintFail();
        return ResponseEntity.ok(ApiResponse.success("已触发指纹开锁失败事件"));
    }
    @PostMapping("/device/simulator/trigger/curtain")
    public ResponseEntity<ApiResponse<String>> triggerCurtain(
            @RequestParam String command,
            @RequestParam(required = false) Integer percent) {
        log.info("触发模拟窗帘控制：command={}, percent={}", command, percent);
        deviceSimulator.triggerCurtain(command, percent);
        return ResponseEntity.ok(ApiResponse.success("已触发窗帘控制事件"));
    }
    @PostMapping("/device/simulator/trigger/buzzer")
    public ResponseEntity<ApiResponse<String>> triggerBuzzer(
            @RequestParam(defaultValue = "beep") String command,
            @RequestParam(defaultValue = "测试鸣响") String reason) {
        log.info("触发模拟蜂鸣器鸣响：command={}, reason={}", command, reason);
        deviceSimulator.triggerBuzzer(command, reason);
        return ResponseEntity.ok(ApiResponse.success("已触发蜂鸣器鸣响事件"));
    }
    @PostMapping("/device/simulator/trigger/light")
    public ResponseEntity<ApiResponse<String>> triggerLight(
            @RequestParam String command,
            @RequestParam(required = false) Integer brightness) {
        log.info("触发模拟灯光控制：command={}, brightness={}", command, brightness);
        deviceSimulator.triggerLight(command, brightness);
        return ResponseEntity.ok(ApiResponse.success("已触发灯光控制事件"));
    }
    /**
     * 处理 POST /device/simulator/trigger/watch-emergency 请求。
     * @return 处理结果
     */
    @PostMapping("/device/simulator/trigger/watch-emergency")
    public ResponseEntity<ApiResponse<String>> triggerWatchEmergency() {
        log.info("触发模拟手表紧急呼救");
        deviceSimulator.triggerWatchEmergency();
        return ResponseEntity.ok(ApiResponse.success("已触发手表紧急呼救事件"));
    }
    @PostMapping("/device/simulator/trigger/fall-detection")
    public ResponseEntity<ApiResponse<String>> triggerFallDetection(
            @RequestParam(defaultValue = "living-room") String room) {
        log.info("触发模拟摔倒检测：room={}", room);
        deviceSimulator.triggerFallDetection(room);
        return ResponseEntity.ok(ApiResponse.success("已触发摔倒检测事件"));
    }
    @PostMapping("/device/simulator/trigger/find-item")
    public ResponseEntity<ApiResponse<String>> triggerFindItem(
            @RequestParam String itemName,
            @RequestParam(defaultValue = "living-room") String room) {
        log.info("触发模拟查找物品：item={}, room={}", itemName, room);
        deviceSimulator.triggerFindItem(itemName, room);
        return ResponseEntity.ok(ApiResponse.success("已触发查找物品请求"));
    }

    // ==================== AI 智能体接口 ====================
    @PostMapping("/ai/chat")
    public ResponseEntity<CompletableFuture<ApiResponse<AiConversationResp>>> chatWithAgent(
            @RequestParam String userId,
            @RequestParam String content,
            @RequestParam(required = false) String houseId) {

        log.info("AI对话请求：userId={}, content={}, houseId={}", userId, content, houseId);

        CompletableFuture<AiConversationResp> future = aiAgentService.sendMessageAsync(userId, content, houseId);
        CompletableFuture<ApiResponse<AiConversationResp>> responseFuture = future.thenApply(ApiResponse::success);

        return ResponseEntity.ok(responseFuture);
    }
    @PostMapping("/ai/find-item")
    public ResponseEntity<CompletableFuture<ApiResponse<CameraFindItemResp>>> findItem(
            @RequestParam String userId,
            @RequestParam String itemName,
            @RequestParam(defaultValue = "living-room") String room,
            @RequestParam(required = false) String houseId) {

        log.info("AI查找物品请求：userId={}, item={}, room={}, houseId={}", userId, itemName, room, houseId);

        CompletableFuture<CameraFindItemResp> future = aiAgentService.findItemAsync(userId, itemName, room, houseId);
        CompletableFuture<ApiResponse<CameraFindItemResp>> responseFuture = future.thenApply(ApiResponse::success);

        return ResponseEntity.ok(responseFuture);
    }
}
