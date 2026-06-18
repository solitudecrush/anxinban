package com.anxinban.mqtt.service;
import com.anxinban.entity.AlarmEvent;
import com.anxinban.mqtt.constant.MqttTopicConstants;
import com.anxinban.mqtt.dto.*;
import com.anxinban.mapper.AlarmEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则引擎服务。
 *
 * 作用：作为 MQTT 消息监听器，监听特定设备事件并执行预设的业务联动规则。
 * 当前为硬编码规则实现，预留了扩展为配置化规则引擎的接口。
 *
 * 已实现规则：
 * 1. 夜间离床检测：22:00-06:00 期间床压从有人变为无人时，生成告警
 * 2. 陌生人闯入联动：门禁陌生人告警触发时，自动向蜂鸣器发送鸣响指令，并向摄像头下发抓图指令
 * 3. 紧急呼叫联动：收到手表紧急呼叫时，向客厅摄像头下发抓图指令
 * 4. 跌倒检测联动：检测到跌倒事件时，向对应位置的摄像头下发抓图指令
 */
@Service
public class RuleEngineService implements MqttMessageListener {

    private static final Logger log = LoggerFactory.getLogger(RuleEngineService.class);
    private final MqttClientService mqttClientService;
    private final AlarmEventRepository alarmEventRepository;
    private final ObjectMapper objectMapper;
    private final Map<String, Map<String, DeviceState>> deviceStateCache = new ConcurrentHashMap<>();
    private static final LocalTime NIGHT_START = LocalTime.of(22, 0);
    /** 夜间时段结束（06:00） */
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);

    public RuleEngineService(MqttClientService mqttClientService,
                             AlarmEventRepository alarmEventRepository,
                             ObjectMapper objectMapper) {
        this.mqttClientService = mqttClientService;
        this.alarmEventRepository = alarmEventRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        mqttClientService.addListener(this);
        log.info("RuleEngineService 已注册到 MqttClientService");
    }

    @Override
    public void onMessage(String topic, String payload) {
        log.debug("规则引擎收到消息：{}", topic);

        try {
            // 根据主题类型路由到对应规则
            if (topic.matches("house/.+/sensor/bed-pressure/data")) {
                processBedPressureRule(topic, payload);
            } else if (topic.matches("house/.+/event/access/alert")) {
                processDoorAccessRule(topic, payload);
            } else if (topic.matches("house/.+/event/emergency-call/alert")) {
                processEmergencyCallRule(topic, payload);
            } else if (topic.matches("house/.+/event/fall/alert")) {
                processFallDetectionRule(topic, payload);
            }
        } catch (Exception e) {
            log.error("规则引擎处理消息失败，主题：{}，错误：{}", topic, e.getMessage(), e);
        }
    }

    // ==================== 规则1：夜间离床检测 ====================

    /**
     * 处理床压传感器数据，检测夜间离床事件。
     * 规则：22:00-06:00 期间，床压从有人变为无人时，生成"老人夜间离床"告警。
     */
    private void processBedPressureRule(String topic, String payload) throws Exception {
        // 解析主题获取房屋ID和房间
        String[] parts = topic.split("/");
        String houseId = parts[1];
        String room = parts[2];

        // 解析床压数据
        BedPressureReport report = objectMapper.readValue(payload, BedPressureReport.class);
        boolean currentOccupied = report.getOccupied();

        // 获取上一次状态
        DeviceState previousState = getPreviousState(houseId, room, report.getDeviceId());
        boolean previousOccupied = previousState != null && previousState.bedOccupied;

        // 更新缓存状态
        updateDeviceState(houseId, room, report.getDeviceId(), currentOccupied);

        // 检查是否在夜间时段
        LocalTime now = LocalTime.now();
        boolean isNight = isNightTime(now);

        // 规则判断：夜间时段 + 从有人变为无人
        if (isNight && previousOccupied && !currentOccupied) {
            log.warn("触发夜间离床规则：房屋={}, 房间={}, 设备={}", houseId, room, report.getDeviceId());
            
            // 生成告警
            AlarmEvent alarm = new AlarmEvent();
            alarm.setAlarmId(java.util.UUID.randomUUID().toString());
            alarm.setDeviceId(report.getDeviceId());
            alarm.setAlarmType("night-leave-bed");
            alarm.setAlarmLevel("medium");
            alarm.setAlarmStatus("pending");
            alarm.setDescription("老人夜间离床提醒");
            alarm.setRoomNumber(room);
            alarm.setCreatedAt(java.time.LocalDateTime.now());
            alarm.setIsRead(false);
            alarmEventRepository.save(alarm);

            log.info("已生成夜间离床告警：{}", alarm.getAlarmId());

            // 向蜂鸣器发送提示音（轻微提醒）
            sendBuzzerCommand(houseId, room, "beep", "夜间离床提醒");
        }
    }

    // ==================== 规则2：陌生人闯入联动蜂鸣器和摄像头 ====================

    /**
     * 处理门禁事件，陌生人闯入时联动蜂鸣器和摄像头。
     * 规则：当门禁上报陌生人事件时，自动向蜂鸣器发送鸣响指令，并向门口摄像头下发抓图指令。
     */
    private void processDoorAccessRule(String topic, String payload) throws Exception {
        String[] parts = topic.split("/");
        String houseId = parts[1];

        DoorAccessEvent event = objectMapper.readValue(payload, DoorAccessEvent.class);

        // 检查是否为陌生人闯入
        if ("access-denied".equals(event.getEventType())) {
            log.warn("触发陌生人闯入规则：房屋={}, 事件={}", houseId, event.getDescription());

            // 向蜂鸣器发送鸣响指令
            sendBuzzerCommand(houseId, "living-room", "beep", "陌生人闯入告警");

            // 向门口摄像头下发抓图指令，记录陌生人截图
            sendCameraCaptureCommand(houseId, "entrance", "stranger-detection");

            log.info("已向蜂鸣器下发鸣响指令，并向摄像头下发抓图指令");
        }
    }

    // ==================== 规则3：紧急呼叫联动摄像头 ====================

    /**
     * 处理紧急呼叫事件，联动摄像头。
     * 规则：收到手表紧急呼叫时，向客厅摄像头下发抓图指令。
     */
    private void processEmergencyCallRule(String topic, String payload) throws Exception {
        String[] parts = topic.split("/");
        String houseId = parts[1];
        String room = parts[2];

        WatchEmergencyCall emergency = objectMapper.readValue(payload, WatchEmergencyCall.class);

        log.warn("触发紧急呼叫规则：房屋={}, 佩戴者={}, 位置={}", 
                houseId, emergency.getWearerName(), emergency.getLocationDesc());

        // 向客厅摄像头下发抓图指令
        sendCameraCaptureCommand(houseId, "living-room", "emergency");

        // 向卧室摄像头下发抓图指令（如果位置在卧室）
        if ("bedroom".equals(room) || emergency.getLocationDesc().contains("卧室")) {
            sendCameraCaptureCommand(houseId, "bedroom", "emergency");
        }

        log.info("已向摄像头下发紧急抓图指令");
    }

    // ==================== 规则4：跌倒检测联动摄像头 ====================

    /**
     * 处理跌倒检测事件，联动摄像头抓图。
     * 规则：检测到跌倒事件时，向对应位置的摄像头下发抓图指令。
     */
    private void processFallDetectionRule(String topic, String payload) throws Exception {
        String[] parts = topic.split("/");
        String houseId = parts[1];
        String room = parts[2];

        CameraFallDetectionEvent fallEvent = objectMapper.readValue(payload, CameraFallDetectionEvent.class);

        log.warn("触发跌倒检测规则：房屋={}, 房间={}, 检测时间={}", 
                houseId, room, fallEvent.getTimestamp());

        // 向当前房间的摄像头下发抓图指令
        sendCameraCaptureCommand(houseId, room, "fall-detection");

        // 向客厅摄像头也下发抓图指令（确保能拍到老人情况）
        sendCameraCaptureCommand(houseId, "living-room", "fall-detection");

        log.info("已向摄像头下发跌倒抓图指令");
    }

    // ==================== 设备控制指令下发 ====================

    /**
     * 向蜂鸣器发送控制指令。
     */
    private void sendBuzzerCommand(String houseId, String room, String command, String reason) {
        try {
            BuzzerCmd cmd = new BuzzerCmd("buzzer-001", command, room);
            cmd.setReason(reason);
            cmd.setDurationMs(5000);
            cmd.setBeepCount(5);

            String topic = MqttTopicConstants.actuatorCmd(houseId, room, "buzzer-001");
            mqttClientService.publishJson(topic, cmd);

            log.debug("已发送蜂鸣器指令：{} -> {}", topic, command);
        } catch (MqttException e) {
            log.error("发送蜂鸣器指令失败", e);
        }
    }

    /**
     * 向摄像头发送抓图指令。
     */
    private void sendCameraCaptureCommand(String houseId, String room, String reason) {
        try {
            CameraFindItemReq req = new CameraFindItemReq(
                    "camera-" + room + "-001",
                    "emergency-capture",
                    room,
                    java.util.UUID.randomUUID().toString()
            );
            req.setSource("system");
            req.setUserId("system");

            String topic = MqttTopicConstants.actuatorCmd(houseId, room, "camera-" + room + "-001");
            mqttClientService.publishJson(topic, req);

            log.debug("已发送摄像头抓图指令：{} -> {}", topic, reason);
        } catch (MqttException e) {
            log.error("发送摄像头抓图指令失败", e);
        }
    }

    // ==================== 状态缓存管理 ====================

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    private DeviceState getPreviousState(String houseId, String room, String deviceId) {
        return deviceStateCache.computeIfAbsent(houseId, k -> new ConcurrentHashMap<>())
                .get(deviceId);
    }

        /**
         * updateDeviceState 方法。
         *
         * @param bedOccupied 字段含义待补充
         */
    private void updateDeviceState(String houseId, String room, String deviceId, boolean bedOccupied) {
        DeviceState state = new DeviceState();
        state.houseId = houseId;
        state.room = room;
        state.deviceId = deviceId;
        state.bedOccupied = bedOccupied;
        state.timestamp = System.currentTimeMillis();

        deviceStateCache.computeIfAbsent(houseId, k -> new ConcurrentHashMap<>())
                .put(deviceId, state);
    }

    /**
     * 判断当前时间是否在夜间时段（22:00-06:00，含边界）。
     */
    private boolean isNightTime(LocalTime time) {
        // 22:00 及之后，或 06:00 之前，均视为夜间
        return !time.isBefore(NIGHT_START) || time.isBefore(NIGHT_END);
    }
    private static class DeviceState {
        String houseId;
        String room;
        String deviceId;
        boolean bedOccupied;
        long timestamp;
    }

    // ==================== 规则引擎扩展接口（预留） ====================

    /**
     * 动态添加规则（预留接口，当前未实现）。
     */
    public void addRule(Rule rule) {
        // 预留：未来可实现规则配置化
        log.info("规则添加接口已调用，规则：{}", rule.getName());
    }

    /**
     * 规则接口定义（预留）。
     */
    public interface Rule {
        String getName();
        boolean matches(String topic);
        void execute(String topic, String payload);
    }
}
