package com.anxinban.mqtt.service;
import com.anxinban.entity.AlarmEvent;
import com.anxinban.entity.SensorData;
import com.anxinban.mqtt.constant.MqttTopicConstants;
import com.anxinban.mqtt.dto.*;
import com.anxinban.mapper.AlarmEventRepository;
import com.anxinban.mapper.SensorDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MQTT 消息消费者服务。
 *
 * 作用：作为 MQTT 消息监听器，接收来自设备的所有数据上报消息，
 * 解析消息内容并路由到对应的处理逻辑，将数据持久化到数据库。
 *
 * 支持的消息类型：
 * - 温湿度传感器数据 → 存入 SensorData 表
 * - 床压传感器数据 → 存入 SensorData 表
 * - 烟雾报警器数据 → 存入 SensorData 表，异常时生成告警
 * - 手表健康数据 → 存入 SensorData 表
 * - 指纹识别事件 → 存入 AlarmEvent 表（失败时）
 * - 门禁事件 → 存入 AlarmEvent 表（陌生人时）
 * - 摔倒检测事件 → 存入 AlarmEvent 表
 * - 紧急呼叫事件 → 存入 AlarmEvent 表
 * - 摄像头图片 → 记录图片URL到对应事件
 * - 设备状态 → 更新设备状态
 */
@Service
public class MqttMessageConsumer implements MqttMessageListener {

    private static final Logger log = LoggerFactory.getLogger(MqttMessageConsumer.class);
    private final MqttClientService mqttClientService;
    private final SensorDataRepository sensorDataRepository;
    private final AlarmEventRepository alarmEventRepository;
    /** 数据映射器实例 */
    private final ObjectMapper objectMapper;

    // 主题匹配模式
    private static final Pattern SENSOR_DATA_PATTERN = 
            Pattern.compile("house/([^/]+)/([^/]+)/sensor/([^/]+)/data");
    private static final Pattern EVENT_ALERT_PATTERN = 
            Pattern.compile("house/([^/]+)/([^/]+)/event/([^/]+)/alert");
    private static final Pattern DEVICE_STATUS_PATTERN = 
            Pattern.compile("house/([^/]+)/([^/]+)/device/([^/]+)/status");
    private static final Pattern CAMERA_IMAGE_PATTERN = 
            Pattern.compile("house/([^/]+)/([^/]+)/camera/([^/]+)/image");

    public MqttMessageConsumer(MqttClientService mqttClientService,
                               SensorDataRepository sensorDataRepository,
                               AlarmEventRepository alarmEventRepository,
                               ObjectMapper objectMapper) {
        this.mqttClientService = mqttClientService;
        this.sensorDataRepository = sensorDataRepository;
        this.alarmEventRepository = alarmEventRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        mqttClientService.addListener(this);
        log.info("MqttMessageConsumer 已注册到 MqttClientService");
    }

    @Override
    public void onMessage(String topic, String payload) {
        log.debug("收到 MQTT 消息，开始处理，主题：{}", topic);
        
        try {
            // 根据主题类型路由消息
            if (topic.matches("house/.+/sensor/.+/data")) {
                handleSensorData(topic, payload);
            } else if (topic.matches("house/.+/event/.+/alert")) {
                handleEventAlert(topic, payload);
            } else if (topic.matches("house/.+/device/.+/status")) {
                handleDeviceStatus(topic, payload);
            } else if (topic.matches("house/.+/camera/.+/image")) {
                handleCameraImage(topic, payload);
            } else if (topic.matches("house/.+/agent/response")) {
                handleAgentResponse(topic, payload);
            } else {
                log.warn("未识别的主题类型：{}", topic);
            }
        } catch (Exception e) {
            log.error("MQTT 消息处理失败，主题：{}，错误：{}", topic, e.getMessage(), e);
        }
    }

    // ==================== 传感器数据处理 ====================

        /**
         * handleSensorData 方法。
         *
         * @param payload 消息负载/原始数据
         */
    private void handleSensorData(String topic, String payload) throws JsonProcessingException {
        Matcher matcher = SENSOR_DATA_PATTERN.matcher(topic);
        if (!matcher.matches()) {
            log.warn("传感器数据主题格式不正确：{}", topic);
            return;
        }

        String houseId = matcher.group(1);
        String room = matcher.group(2);
        String sensorType = matcher.group(3);

        log.debug("处理传感器数据：house={}, room={}, type={}", houseId, room, sensorType);

        switch (sensorType) {
            case "temperature-humidity":
                TemperatureHumidityReport tempHum = objectMapper.readValue(payload, TemperatureHumidityReport.class);
                saveTemperatureHumidity(tempHum, houseId, room);
                break;
            case "bed-pressure":
                BedPressureReport bedPressure = objectMapper.readValue(payload, BedPressureReport.class);
                saveBedPressure(bedPressure, houseId, room);
                break;
            case "smoke":
                SmokeAlarmEvent smoke = objectMapper.readValue(payload, SmokeAlarmEvent.class);
                saveSmokeAlarm(smoke, houseId, room);
                break;
            case "watch-health":
                WatchHealthData watch = objectMapper.readValue(payload, WatchHealthData.class);
                saveWatchHealthData(watch, houseId);
                break;
            default:
                log.warn("未知传感器类型：{}", sensorType);
        }
    }

        /**
         * saveTemperatureHumidity 方法。
         *
         * @param room 房间名称/编号
         */
    private void saveTemperatureHumidity(TemperatureHumidityReport report, String houseId, String room) {
        // 保存温度
        SensorData tempData = new SensorData();
        tempData.setDeviceId(report.getDeviceId());
        tempData.setSensorType("temperature");
        tempData.setValue(report.getTemperature());
        tempData.setUnit("°C");
        tempData.setTimestamp(parseTimestamp(report.getTimestamp()));
        tempData.setCreatedAt(LocalDateTime.now());
        tempData.setIsAbnormal(report.getTemperature() > 35 || report.getTemperature() < 10);
        sensorDataRepository.save(tempData);

        // 保存湿度
        SensorData humData = new SensorData();
        humData.setDeviceId(report.getDeviceId());
        humData.setSensorType("humidity");
        humData.setValue(report.getHumidity());
        humData.setUnit("%");
        humData.setTimestamp(parseTimestamp(report.getTimestamp()));
        humData.setCreatedAt(LocalDateTime.now());
        humData.setIsAbnormal(report.getHumidity() > 90 || report.getHumidity() < 20);
        sensorDataRepository.save(humData);

        log.info("已保存温湿度数据：设备={}, 温度={}°C, 湿度={}%", 
                report.getDeviceId(), report.getTemperature(), report.getHumidity());
    }

        /**
         * saveBedPressure 方法。
         *
         * @param room 房间名称/编号
         */
    private void saveBedPressure(BedPressureReport report, String houseId, String room) {
        SensorData data = new SensorData();
        data.setDeviceId(report.getDeviceId());
        data.setSensorType("bed-pressure");
        data.setValue(report.getPressureValue());
        data.setUnit(report.getPressureUnit());
        data.setTimestamp(parseTimestamp(report.getTimestamp()));
        data.setCreatedAt(LocalDateTime.now());
        data.setIsAbnormal(false);
        sensorDataRepository.save(data);

        log.info("已保存床压数据：设备={}, 有人={}, 压力值={}{}", 
                report.getDeviceId(), report.getOccupied(), report.getPressureValue(), report.getPressureUnit());
    }

        /**
         * saveSmokeAlarm 方法。
         *
         * @param room 房间名称/编号
         */
    private void saveSmokeAlarm(SmokeAlarmEvent event, String houseId, String room) {
        SensorData data = new SensorData();
        data.setDeviceId(event.getDeviceId());
        data.setSensorType("smoke");
        data.setValue(event.getSmokeValue());
        data.setUnit("ppm");
        data.setTimestamp(parseTimestamp(event.getTimestamp()));
        data.setCreatedAt(LocalDateTime.now());
        data.setIsAbnormal("alarm".equals(event.getStatus()) || "low".equals(event.getStatus()));
        sensorDataRepository.save(data);

        // 如果是报警状态，生成告警事件
        if (event.isAlarm()) {
            AlarmEvent alarm = createAlarmEvent(event.getDeviceId(), "smoke", "high", 
                    event.getDescription(), room);
            alarmEventRepository.save(alarm);
            log.info("已生成烟雾告警：{}", alarm.getAlarmId());
        }

        log.info("已保存烟雾报警器数据：设备={}, 状态={}", event.getDeviceId(), event.getStatus());
    }

        /**
         * saveWatchHealthData 方法。
         *
         * @param houseId 房屋/家庭 ID
         */
    private void saveWatchHealthData(WatchHealthData data, String houseId) {
        // 保存心率
        SensorData heartRate = new SensorData();
        heartRate.setDeviceId(data.getDeviceId());
        heartRate.setSensorType("heart-rate");
        heartRate.setValue(data.getHeartRate().doubleValue());
        heartRate.setUnit("bpm");
        heartRate.setTimestamp(parseTimestamp(data.getTimestamp()));
        heartRate.setCreatedAt(LocalDateTime.now());
        heartRate.setIsAbnormal(data.getHeartRate() > 120 || data.getHeartRate() < 50);
        sensorDataRepository.save(heartRate);

        // 保存血氧
        SensorData bloodOxygen = new SensorData();
        bloodOxygen.setDeviceId(data.getDeviceId());
        bloodOxygen.setSensorType("blood-oxygen");
        bloodOxygen.setValue(data.getBloodOxygen().doubleValue());
        bloodOxygen.setUnit("%");
        bloodOxygen.setTimestamp(parseTimestamp(data.getTimestamp()));
        bloodOxygen.setCreatedAt(LocalDateTime.now());
        bloodOxygen.setIsAbnormal(data.getBloodOxygen() < 95);
        sensorDataRepository.save(bloodOxygen);

        // 保存体温
        SensorData bodyTemp = new SensorData();
        bodyTemp.setDeviceId(data.getDeviceId());
        bodyTemp.setSensorType("body-temperature");
        bodyTemp.setValue(data.getTemperature());
        bodyTemp.setUnit("°C");
        bodyTemp.setTimestamp(parseTimestamp(data.getTimestamp()));
        bodyTemp.setCreatedAt(LocalDateTime.now());
        bodyTemp.setIsAbnormal(data.getTemperature() > 37.5);
        sensorDataRepository.save(bodyTemp);

        log.info("已保存手表健康数据：设备={}, 心率={}bpm, 血氧={}%, 体温={}°C", 
                data.getDeviceId(), data.getHeartRate(), data.getBloodOxygen(), data.getTemperature());
    }

    // ==================== 事件告警处理 ====================

        /**
         * handleEventAlert 方法。
         *
         * @param payload 消息负载/原始数据
         */
    private void handleEventAlert(String topic, String payload) throws JsonProcessingException {
        Matcher matcher = EVENT_ALERT_PATTERN.matcher(topic);
        if (!matcher.matches()) {
            log.warn("事件告警主题格式不正确：{}", topic);
            return;
        }

        String houseId = matcher.group(1);
        String room = matcher.group(2);
        String eventType = matcher.group(3);

        log.debug("处理事件告警：house={}, room={}, type={}", houseId, room, eventType);

        switch (eventType) {
            case "fingerprint":
                FingerprintEvent fingerprint = objectMapper.readValue(payload, FingerprintEvent.class);
                handleFingerprintEvent(fingerprint, houseId, room);
                break;
            case "access":
                DoorAccessEvent doorEvent = objectMapper.readValue(payload, DoorAccessEvent.class);
                handleDoorAccessEvent(doorEvent, houseId);
                break;
            case "fall-detection":
                CameraFallDetectionEvent fall = objectMapper.readValue(payload, CameraFallDetectionEvent.class);
                handleFallDetectionEvent(fall, houseId);
                break;
            case "emergency-call":
                WatchEmergencyCall emergency = objectMapper.readValue(payload, WatchEmergencyCall.class);
                handleEmergencyCall(emergency, houseId, room);
                break;
            default:
                log.warn("未知事件类型：{}", eventType);
        }
    }

        /**
         * handleFingerprintEvent 方法。
         *
         * @param room 房间名称/编号
         */
    private void handleFingerprintEvent(FingerprintEvent event, String houseId, String room) {
        if ("fail".equals(event.getResult())) {
            AlarmEvent alarm = createAlarmEvent(event.getDeviceId(), "fingerprint-fail", "medium",
                    "指纹识别失败，重试次数：" + event.getRetryCount(), room);
            alarmEventRepository.save(alarm);
            log.info("已生成指纹失败告警：{}", alarm.getAlarmId());
        } else {
            log.info("指纹识别成功：设备={}, 用户={}", event.getDeviceId(), event.getUserId());
        }
    }

        /**
         * handleDoorAccessEvent 方法。
         *
         * @param houseId 房屋/家庭 ID
         */
    private void handleDoorAccessEvent(DoorAccessEvent event, String houseId) {
        if ("access-denied".equals(event.getEventType())) {
            AlarmEvent alarm = createAlarmEvent(event.getDeviceId(), "stranger", "high",
                    event.getDescription(), event.getRoom());
            alarm.setSnapshotUrl(event.getImageUrl());
            alarmEventRepository.save(alarm);
            log.info("已生成陌生人闯入告警：{}", alarm.getAlarmId());
        } else {
            log.info("门禁通过：设备={}, 用户={}", event.getDeviceId(), event.getFingerprintUserId());
        }
    }

        /**
         * handleFallDetectionEvent 方法。
         *
         * @param houseId 房屋/家庭 ID
         */
    private void handleFallDetectionEvent(CameraFallDetectionEvent event, String houseId) {
        AlarmEvent alarm = createAlarmEvent(event.getDeviceId(), "fall-detection", "high",
                event.getDescription(), event.getRoom());
        alarm.setSnapshotUrl(event.getImageUrl());
        alarmEventRepository.save(alarm);
        log.info("已生成摔倒检测告警：{}", alarm.getAlarmId());
    }

        /**
         * handleEmergencyCall 方法。
         *
         * @param room 房间名称/编号
         */
    private void handleEmergencyCall(WatchEmergencyCall event, String houseId, String room) {
        AlarmEvent alarm = createAlarmEvent(event.getDeviceId(), "emergency-call", "high",
                "紧急呼叫！佩戴者：" + event.getWearerName() + "，心率：" + event.getHeartRate() + "bpm，位置：" + event.getLocationDesc(),
                room);
        alarmEventRepository.save(alarm);
        log.info("已生成紧急呼叫告警：{}", alarm.getAlarmId());
    }

    // ==================== 设备状态处理 ====================

        /**
         * handleDeviceStatus 方法。
         *
         * @param payload 消息负载/原始数据
         */
    private void handleDeviceStatus(String topic, String payload) {
        Matcher matcher = DEVICE_STATUS_PATTERN.matcher(topic);
        if (!matcher.matches()) {
            log.warn("设备状态主题格式不正确：{}", topic);
            return;
        }

        String houseId = matcher.group(1);
        String room = matcher.group(2);
        String deviceId = matcher.group(3);

        log.info("设备状态更新：house={}, room={}, device={}, payload={}", 
                houseId, room, deviceId, payload);
        // 设备状态更新逻辑可在此扩展
    }

    // ==================== 摄像头图片处理 ====================

        /**
         * handleCameraImage 方法。
         *
         * @param payload 消息负载/原始数据
         */
    private void handleCameraImage(String topic, String payload) throws JsonProcessingException {
        Matcher matcher = CAMERA_IMAGE_PATTERN.matcher(topic);
        if (!matcher.matches()) {
            log.warn("摄像头图片主题格式不正确：{}", topic);
            return;
        }

        String houseId = matcher.group(1);
        String room = matcher.group(2);
        String cameraId = matcher.group(3);

        CameraFindItemResp resp = objectMapper.readValue(payload, CameraFindItemResp.class);
        log.info("收到摄像头图片：house={}, room={}, camera={}, 物品={}, 找到={}", 
                houseId, room, cameraId, resp.getItemName(), resp.getFound());
        // 图片处理逻辑可在此扩展（保存到MinIO等）
    }

    // ==================== 智能体响应处理 ====================

        /**
         * handleAgentResponse 方法。
         *
         * @param payload 消息负载/原始数据
         */
    private void handleAgentResponse(String topic, String payload) throws JsonProcessingException {
        AiConversationResp resp = objectMapper.readValue(payload, AiConversationResp.class);
        log.info("收到智能体响应：会话ID={}, 意图={}", resp.getSessionId(), resp.getIntent());
        // 智能体响应处理逻辑可在此扩展
    }

    // ==================== 辅助方法 ====================

        /**
         * createAlarmEvent 方法。
         */
    private AlarmEvent createAlarmEvent(String deviceId, String alarmType, String level, 
                                        String description, String room) {
        AlarmEvent alarm = new AlarmEvent();
        alarm.setAlarmId(UUID.randomUUID().toString());
        alarm.setDeviceId(deviceId);
        alarm.setAlarmType(alarmType);
        alarm.setAlarmLevel(level);
        alarm.setAlarmStatus("pending");
        alarm.setDescription(description);
        alarm.setRoomNumber(room);
        alarm.setCreatedAt(LocalDateTime.now());
        alarm.setIsRead(false);
        return alarm;
    }

        /**
         * parseTimestamp 方法。
         *
         * @param timestamp 时间戳
         */
    private LocalDateTime parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            // 尝试解析 ISO-8601 格式
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_INSTANT)
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(timestamp);
            } catch (DateTimeParseException e2) {
                return LocalDateTime.now();
            }
        }
    }
}
