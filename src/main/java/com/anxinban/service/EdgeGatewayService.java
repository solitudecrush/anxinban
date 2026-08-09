package com.anxinban.service;

import com.anxinban.dto.EdgeDataRequest;
import com.anxinban.dto.EdgeDataResponse;
import com.anxinban.entity.*;
import com.anxinban.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 家庭 PC 边缘网关数据接入核心业务服务。
 *
 * <p>职责：</p>
 * <ul>
 *   <li>校验 mqtt_topic 与 payload.message_type 一致性</li>
 *   <li>通过 device_id 查找 elder_id（Device 表映射）</li>
 *   <li>幂等去重（edge_ingest_record.upload_id UNIQUE）</li>
 *   <li>按 message_type 路由到对应的处理逻辑</li>
 *   <li>在事务边界内完成幂等记录 + 业务数据 + 告警写入</li>
 * </ul>
 *
 * <p>本服务不重新实现告警/通知/AI 分析系统，而是尽可能复用已有的
 * {@link DeviceService}、{@link SosService}、{@link AiForwardService} 等。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class EdgeGatewayService {

    private static final Logger log = LoggerFactory.getLogger(EdgeGatewayService.class);

    /** topic → 期望的 message_type 映射 */
    private static final Map<String, String> TOPIC_TYPE_MAP = Map.of(
            "anxinban/telemetry/vitals", "vitals",
            "anxinban/telemetry/imu", "imu",
            "anxinban/event/fall", "fall",
            "anxinban/event/sos", "sos",
            "anxinban/status/device", "device_status"
    );

    private final DeviceRepository deviceRepository;
    private final SensorDataRepository sensorDataRepository;
    private final AlarmEventRepository alarmEventRepository;
    private final NotificationRepository notificationRepository;
    private final SosRecordRepository sosRecordRepository;
    private final EdgeIngestRecordRepository edgeIngestRecordRepository;
    private final AiForwardService aiForwardService;

    @Autowired
    public EdgeGatewayService(DeviceRepository deviceRepository,
                              SensorDataRepository sensorDataRepository,
                              AlarmEventRepository alarmEventRepository,
                              NotificationRepository notificationRepository,
                              SosRecordRepository sosRecordRepository,
                              EdgeIngestRecordRepository edgeIngestRecordRepository,
                              AiForwardService aiForwardService) {
        this.deviceRepository = deviceRepository;
        this.sensorDataRepository = sensorDataRepository;
        this.alarmEventRepository = alarmEventRepository;
        this.notificationRepository = notificationRepository;
        this.sosRecordRepository = sosRecordRepository;
        this.edgeIngestRecordRepository = edgeIngestRecordRepository;
        this.aiForwardService = aiForwardService;
    }

    // ==================== 公开入口 ====================

    /**
     * 处理边缘网关上传请求。
     *
     * <p>整体在一个事务中执行：幂等记录 → 业务数据写入 → 告警生成。
     * 若业务失败，整个事务回滚（含幂等记录），PC 可安全重试。</p>
     *
     * @param request 边缘网关请求体
     * @return 处理响应
     */
    @Transactional(rollbackFor = Exception.class)
    public EdgeDataResponse process(EdgeDataRequest request) {
        EdgeDataRequest.Payload payload = request.getPayload();
        String messageType = payload.getMessageType();
        String deviceId = payload.getDeviceId();
        String uploadId = request.getUploadId();

        // 1. 幂等检查 + 记录插入（UNIQUE 约束保证并发安全）
        if (edgeIngestRecordRepository.existsByUploadId(uploadId)) {
            log.info("幂等命中 — upload_id={} 已处理，直接返回成功", uploadId);
            return EdgeDataResponse.duplicate(uploadId, messageType);
        }

        // 2. device_id → elder_id 查找
        Device device = deviceRepository.findByDeviceId(deviceId);
        if (device == null) {
            throw new DeviceNotFoundException("设备未注册: " + deviceId);
        }
        String elderId = device.getElderId();

        // 3. 计算业务时间（payload.timestamp > 0 → 使用设备时间，否则使用 received_at）
        LocalDateTime businessTime = resolveBusinessTime(payload.getTimestamp(), request.getReceivedAt().doubleValue());

        // 4. 按 message_type 路由
        switch (messageType) {
            case "vitals" -> handleVitals(elderId, deviceId, payload.getData(), businessTime);
            case "imu" -> handleImu(elderId, deviceId, payload.getData(), businessTime);
            case "sos" -> handleSos(elderId, deviceId, payload.getData(), businessTime);
            case "fall" -> handleFall(elderId, deviceId, payload.getData(), businessTime);
            case "device_status" -> handleDeviceStatus(device, payload.getData(), businessTime);
            default -> throw new IllegalArgumentException("不支持的消息类型: " + messageType);
        }

        // 5. 写入幂等记录（事务内，UNIQUE 约束防并发重复）
        persistIngestRecord(request, deviceId);

        log.info("边缘数据接入成功: upload_id={}, message_type={}, device_id={}, elder_id={}",
                uploadId, messageType, deviceId, elderId);
        return EdgeDataResponse.accepted(uploadId, messageType);
    }

    /**
     * 仅检查重复（不执行业务逻辑），用于返回 duplicate 响应。
     * 此方法在事务外被 Controller 调用来区分 409-duplicate 场景。
     */
    public boolean isDuplicate(String uploadId) {
        return edgeIngestRecordRepository.existsByUploadId(uploadId);
    }

    // ==================== Topic 校验 ====================

    /**
     * 校验 mqtt_topic 与 payload.message_type 是否匹配。
     *
     * @param mqttTopic   PC 收到的 MQTT topic
     * @param messageType payload 中的 message_type
     * @throws TopicMismatchException 如果不匹配
     */
    public void validateTopicTypeMatch(String mqttTopic, String messageType) {
        String expected = TOPIC_TYPE_MAP.get(mqttTopic);
        if (expected == null) {
            throw new TopicMismatchException("不支持的 mqtt_topic: " + mqttTopic);
        }
        if (!expected.equals(messageType)) {
            throw new TopicMismatchException(
                    String.format("mqtt_topic '%s' 期望 message_type='%s'，实际为 '%s'",
                            mqttTopic, expected, messageType));
        }
    }

    // ==================== 时间解析 ====================

    /**
     * 解析业务时间。
     *
     * @param deviceTimestamp  设备时间戳（秒），可能为 0
     * @param receivedAtEpoch  PC 收到时的 epoch 秒（含小数）
     * @return 用于存储的 LocalDateTime
     */
    private LocalDateTime resolveBusinessTime(Long deviceTimestamp, double receivedAtEpoch) {
        long epochSeconds;
        if (deviceTimestamp != null && deviceTimestamp > 0) {
            epochSeconds = deviceTimestamp;
        } else {
            epochSeconds = (long) receivedAtEpoch;
        }
        long epochMillis = epochSeconds * 1000;
        // 如果小数部分有意义，补上
        if (deviceTimestamp == null || deviceTimestamp == 0) {
            epochMillis = (long) (receivedAtEpoch * 1000);
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.of("Asia/Shanghai"));
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.of("Asia/Shanghai"));
    }

    // ==================== vitals 处理 ====================

    private void handleVitals(String elderId, String deviceId, Map<String, Object> data, LocalDateTime businessTime) {
        log.info("处理 vitals: elderId={}, deviceId={}, data={}", elderId, deviceId, data);

        Integer heartRate = getIntFromData(data, "heart_rate");
        Integer spo2 = getIntFromData(data, "spo2");
        Double temperature = getDoubleFromData(data, "temperature");

        if (heartRate != null) {
            saveSensorData(elderId, deviceId, "heart_rate", heartRate.doubleValue(), "bpm", businessTime);
        }
        if (spo2 != null) {
            saveSensorData(elderId, deviceId, "spo2", spo2.doubleValue(), "%", businessTime);
        }
        if (temperature != null) {
            saveSensorData(elderId, deviceId, "temperature", temperature, "℃", businessTime);
        }

        // 复用现有 AI 健康分析 + 规则引擎告警逻辑
        tryTriggerHealthAnalysis(elderId, deviceId, heartRate, spo2, temperature, businessTime);
    }

    // ==================== IMU 处理 ====================

    private void handleImu(String elderId, String deviceId, Map<String, Object> data, LocalDateTime businessTime) {
        log.info("处理 imu: elderId={}, deviceId={}", elderId, deviceId);

        String[] imuKeys = {"ax", "ay", "az", "gx", "gy", "gz"};
        for (String key : imuKeys) {
            Double value = getDoubleFromData(data, key);
            if (value != null) {
                saveSensorData(elderId, deviceId, "imu_" + key, value, "raw", businessTime);
            }
        }
    }

    // ==================== SOS 处理 ====================

    private void handleSos(String elderId, String deviceId, Map<String, Object> data, LocalDateTime businessTime) {
        Boolean pressed = getBooleanFromData(data, "pressed");
        log.info("处理 sos: elderId={}, deviceId={}, pressed={}", elderId, deviceId, pressed);

        if (!Boolean.TRUE.equals(pressed)) {
            log.info("sos pressed=false，不生成 SOS 告警");
            return;
        }

        // 创建 SOS Record
        String sosId = "sos_" + UUID.randomUUID().toString().substring(0, 8);
        SosRecord sosRecord = new SosRecord();
        sosRecord.setSosId(sosId);
        sosRecord.setElderId(elderId);
        sosRecord.setTriggerTime(businessTime);
        sosRecord.setStatus("triggered");
        sosRecord.setLocation("");
        sosRecord.setCreatedAt(LocalDateTime.now());
        sosRecord.setUpdatedAt(LocalDateTime.now());
        sosRecordRepository.save(sosRecord);

        // 创建告警
        String alarmId = "alarm_" + UUID.randomUUID().toString().substring(0, 8);
        AlarmEvent alarm = new AlarmEvent();
        alarm.setAlarmId(alarmId);
        alarm.setElderId(elderId);
        alarm.setDeviceId(deviceId);
        alarm.setType("emergency-call");
        alarm.setRiskLevel("critical");
        alarm.setStatus("pending");
        alarm.setDescription("手表 SOS 紧急求助");
        alarm.setBuilding("");
        alarm.setRoomNumber("");
        alarm.setUnit("");
        alarm.setLocation("");
        alarm.setSnapshotUrl("");
        alarm.setHandlerId("");
        alarm.setHandlerName("");
        alarm.setHandleNote("");
        alarm.setOccurTime(businessTime);
        alarm.setHandleTime(LocalDateTime.of(1970, 1, 1, 0, 0, 0));
        alarm.setIsRead(false);
        alarm.setCreatedAt(LocalDateTime.now());
        alarm.setUpdatedAt(LocalDateTime.now());
        alarmEventRepository.save(alarm);

        // 创建通知
        createNotification(elderId, alarmId, "SOS紧急求助", "手表触发紧急求助信号", "sos", businessTime);

        log.info("SOS 处理完成: sosId={}, alarmId={}, elderId={}", sosId, alarmId, elderId);
    }

    // ==================== Fall 处理 ====================

    private void handleFall(String elderId, String deviceId, Map<String, Object> data, LocalDateTime businessTime) {
        Boolean detected = getBooleanFromData(data, "detected");
        String source = getStringFromData(data, "source");
        log.info("处理 fall: elderId={}, deviceId={}, detected={}, source={}", elderId, deviceId, detected, source);

        // 保存 fall 状态到 sensor_data
        double fallValue = Boolean.TRUE.equals(detected) ? 2.0 : 0.0;
        saveSensorData(elderId, deviceId, "fall_status", fallValue, "", businessTime);

        if (!Boolean.TRUE.equals(detected)) {
            log.info("fall detected=false，不生成跌倒告警");
            return;
        }

        // 创建告警
        String alarmId = "alarm_" + UUID.randomUUID().toString().substring(0, 8);
        String sourceDesc = (source != null && !source.isEmpty()) ? "（来源: " + source + "）" : "";
        AlarmEvent alarm = new AlarmEvent();
        alarm.setAlarmId(alarmId);
        alarm.setElderId(elderId);
        alarm.setDeviceId(deviceId);
        alarm.setType("fall");
        alarm.setRiskLevel("critical");
        alarm.setStatus("pending");
        alarm.setDescription("手表检测到跌倒事件" + sourceDesc);
        alarm.setBuilding("");
        alarm.setRoomNumber("");
        alarm.setUnit("");
        alarm.setLocation("");
        alarm.setSnapshotUrl("");
        alarm.setHandlerId("");
        alarm.setHandlerName("");
        alarm.setHandleNote("");
        alarm.setOccurTime(businessTime);
        alarm.setHandleTime(LocalDateTime.of(1970, 1, 1, 0, 0, 0));
        alarm.setIsRead(false);
        alarm.setCreatedAt(LocalDateTime.now());
        alarm.setUpdatedAt(LocalDateTime.now());
        alarmEventRepository.save(alarm);

        // 创建通知
        createNotification(elderId, alarmId, "跌倒告警", "手表检测到跌倒事件" + sourceDesc, "alert", businessTime);

        log.info("Fall 处理完成: alarmId={}, elderId={}", alarmId, elderId);
    }

    // ==================== device_status 处理 ====================

    private void handleDeviceStatus(Device device, Map<String, Object> data, LocalDateTime businessTime) {
        log.info("处理 device_status: deviceId={}, data={}", device.getDeviceId(), data);

        Boolean online = getBooleanFromData(data, "online");
        Integer battery = getIntFromData(data, "battery");

        if (online != null) {
            device.setOnline(online);
            device.setStatus(Boolean.TRUE.equals(online) ? "online" : "offline");
            device.setLastOnline(businessTime);
        }
        if (battery != null) {
            device.setBattery(battery);
        }
        device.setUpdatedAt(LocalDateTime.now());
        deviceRepository.save(device);

        log.info("device_status 更新完成: deviceId={}, online={}, battery={}", device.getDeviceId(), online, battery);
    }

    // ==================== 内部辅助方法 ====================

    /**
     * 写入幂等记录。利用 UNIQUE 约束保证并发安全：
     * 若并发重复导致 DataIntegrityViolationException，外层事务回滚后 PC 可重试。
     */
    private void persistIngestRecord(EdgeDataRequest request, String deviceId) {
        EdgeIngestRecord record = EdgeIngestRecord.create(
                request.getUploadId(),
                request.getEdgeId(),
                deviceId,
                request.getPayload().getMessageType(),
                request.getReceivedAt().doubleValue()
        );
        try {
            edgeIngestRecordRepository.save(record);
        } catch (DataIntegrityViolationException e) {
            // 并发重复插入 → 视为幂等命中
            log.warn("幂等记录并发冲突，upload_id={} 已存在", request.getUploadId());
            throw new DuplicateUploadException(request.getUploadId(), request.getPayload().getMessageType());
        }
    }

    private void saveSensorData(String elderId, String deviceId, String sensorType, Double value, String unit, LocalDateTime businessTime) {
        SensorData sd = new SensorData();
        sd.setElderId(elderId);
        sd.setDeviceId(deviceId);
        sd.setSensorType(sensorType);
        sd.setValue(value);
        sd.setUnit(unit);
        sd.setIsAbnormal(false);
        sd.setTimestamp(businessTime);
        sd.setCreatedAt(LocalDateTime.now());
        sensorDataRepository.save(sd);
    }

    private void createNotification(String elderId, String alarmId, String title, String content, String type, LocalDateTime businessTime) {
        try {
            Notification notification = new Notification();
            notification.setNotificationId("notif_" + alarmId);
            notification.setUserId(elderId);
            notification.setUserType("family");
            notification.setType(type);
            notification.setTitle(title);
            notification.setContent(content != null ? content : "暂无详情");
            notification.setIsRead(false);
            notification.setRoom("");
            notification.setElderId(elderId);
            notification.setNotifyTime(businessTime);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
        } catch (Exception e) {
            log.warn("创建通知失败（不影响主流程）: alarmId={}, error={}", alarmId, e.getMessage());
        }
    }

    /**
     * 尝试调用 AI 健康分析 + 规则引擎，与现有 {@code DeviceUploadController} 保持一致。
     * 如果 AI 服务不可用，回退到 Java 本地规则判断。
     */
    private void tryTriggerHealthAnalysis(String elderId, String deviceId,
                                          Integer heartRate, Integer spo2, Double temperature,
                                          LocalDateTime businessTime) {
        boolean needAlarm = false;
        String riskLevel = "正常";
        String riskReason = "各项指标正常";
        String source = "java_rule_fallback";

        // 先尝试 Python AI
        Map<String, Object> aiResult = null;
        try {
            Map<String, Object> healthData = new HashMap<>();
            if (heartRate != null) healthData.put("heart_rate", heartRate);
            if (spo2 != null) healthData.put("spo2", spo2);
            if (temperature != null) healthData.put("temperature", temperature);

            Map<String, Object> aiRequest = new HashMap<>();
            aiRequest.put("elder_id", elderId);
            aiRequest.put("recent_health", healthData);

            aiResult = aiForwardService.forward("/api/ai/health-analysis", aiRequest);
        } catch (Exception e) {
            log.debug("AI 健康分析调用失败，回退到 Java 规则: {}", e.getMessage());
        }

        if (aiResult != null) {
            riskLevel = aiResult.getOrDefault("risk_level", "正常").toString();
            riskReason = aiResult.getOrDefault("risk_reason", "").toString();
            needAlarm = Boolean.TRUE.equals(aiResult.get("need_alarm"));
            source = "python_ai_service";
        } else {
            // Java 规则引擎
            StringBuilder reasons = new StringBuilder();
            if (spo2 != null && spo2 < 92) {
                reasons.append("血氧偏低(").append(spo2).append("%); ");
                needAlarm = true;
                riskLevel = spo2 < 85 ? "高风险" : "中风险";
            }
            if (heartRate != null && (heartRate > 110 || heartRate < 50)) {
                reasons.append(heartRate > 110 ? "心率偏高(" : "心率偏低(").append(heartRate).append("bpm); ");
                needAlarm = true;
                if (heartRate > 130 || heartRate < 40) riskLevel = "高风险";
                else if (riskLevel.equals("正常")) riskLevel = "中风险";
            }
            if (temperature != null && (temperature > 38.0 || temperature < 35.0)) {
                reasons.append("体温异常(").append(temperature).append("℃); ");
                needAlarm = true;
                if (riskLevel.equals("正常")) riskLevel = "中风险";
            }
            if (needAlarm) {
                riskReason = reasons.toString().trim();
            }
        }

        if (needAlarm) {
            String alarmId = "alarm_" + UUID.randomUUID().toString().substring(0, 8);
            AlarmEvent alarm = new AlarmEvent();
            alarm.setAlarmId(alarmId);
            alarm.setElderId(elderId);
            alarm.setDeviceId(deviceId);
            alarm.setType("health_abnormal");
            alarm.setRiskLevel(mapRiskLevel(riskLevel));
            alarm.setStatus("pending");
            alarm.setDescription(riskReason);
            alarm.setBuilding("");
            alarm.setRoomNumber("");
            alarm.setUnit("");
            alarm.setLocation("");
            alarm.setSnapshotUrl("");
            alarm.setHandlerId("");
            alarm.setHandlerName("");
            alarm.setHandleNote("");
            alarm.setOccurTime(businessTime);
            alarm.setHandleTime(LocalDateTime.of(1970, 1, 1, 0, 0, 0));
            alarm.setIsRead(false);
            alarm.setCreatedAt(LocalDateTime.now());
            alarm.setUpdatedAt(LocalDateTime.now());
            alarmEventRepository.save(alarm);

            createNotification(elderId, alarmId, "健康异常告警", riskReason, "alert", businessTime);
            log.info("健康异常告警生成: alarmId={}, level={}, reason={}, source={}", alarmId, riskLevel, riskReason, source);
        }
    }

    private String mapRiskLevel(String displayLevel) {
        if (displayLevel == null) return "normal";
        if (displayLevel.contains("高风险") || displayLevel.contains("critical")) return "critical";
        if (displayLevel.contains("中风险") || displayLevel.contains("medium")) return "high";
        return "normal";
    }

    private Integer getIntFromData(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Number) return ((Number) value).intValue();
        return null;
    }

    private Double getDoubleFromData(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Number) return ((Number) value).doubleValue();
        return null;
    }

    private Boolean getBooleanFromData(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Boolean) return (Boolean) value;
        return null;
    }

    private String getStringFromData(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    // ==================== 业务异常 ====================

    /**
     * 设备未在系统中注册。
     */
    public static class DeviceNotFoundException extends RuntimeException {
        public DeviceNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Topic 与 message_type 不匹配。
     */
    public static class TopicMismatchException extends RuntimeException {
        public TopicMismatchException(String message) {
            super(message);
        }
    }

    /**
     * 幂等重复上传（用于事务内捕获并发冲突）。
     */
    public static class DuplicateUploadException extends RuntimeException {
        private final String uploadId;
        private final String messageType;

        public DuplicateUploadException(String uploadId, String messageType) {
            super("重复上传: " + uploadId);
            this.uploadId = uploadId;
            this.messageType = messageType;
        }

        public String getUploadId() {
            return uploadId;
        }

        public String getMessageType() {
            return messageType;
        }
    }
}
