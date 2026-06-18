package com.anxinban.mqtt.simulator;
import com.anxinban.mqtt.constant.MqttTopicConstants;
import com.anxinban.mqtt.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 硬件设备模拟器。
 *
 * 作用：在真实 ESP32、摄像头、手表等硬件无法联调时，模拟这些设备通过家庭网关向云端上报数据，
 * 并接收云端下发的控制指令。模拟器作为独立 MQTT 客户端运行，连接同一个 EMQX Broker。
 *
 * 模拟内容包括：
 * - 每 30 秒上报客厅温湿度、卧室床压、烟雾报警器状态
 * - 每 30 秒模拟一次大门指纹/陌生人事件
 * - 支持手动触发：指纹开锁、窗帘开关、蜂鸣器鸣响、灯光开关、手表 SOS、摔倒检测、查找物品
 */
@Component
public class DeviceSimulator {

    private static final Logger log = LoggerFactory.getLogger(DeviceSimulator.class);
    private final SimulatorProperties simulatorProperties;
    private final com.anxinban.mqtt.config.MqttProperties mqttProperties;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();
    private MqttClient mqttClient;
    /** 字段含义待补充 */
    private ScheduledExecutorService scheduler;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public DeviceSimulator(SimulatorProperties simulatorProperties,
                           com.anxinban.mqtt.config.MqttProperties mqttProperties,
                           ObjectMapper objectMapper) {
        this.simulatorProperties = simulatorProperties;
        this.mqttProperties = mqttProperties;
        this.objectMapper = objectMapper;
    }
    @PostConstruct
    public void init() {
        if (simulatorProperties.isEnabled()) {
            start();
        } else {
            log.info("设备模拟器未启用，可通过 mqtt.simulator.enabled=true 开启");
        }
    }
    @PreDestroy
    public void destroy() {
        stop();
    }

    /**
     * 启动模拟器：连接 MQTT 并开启定时任务。
     */
    public synchronized void start() {
        if (running.get()) {
            log.info("设备模拟器已在运行");
            return;
        }

        try {
            connect();
            scheduler = Executors.newScheduledThreadPool(2);
            int interval = simulatorProperties.getIntervalSeconds();

            // 周期性传感器上报任务
            scheduler.scheduleAtFixedRate(this::publishSensorData, 5, interval, TimeUnit.SECONDS);

            // 周期性事件模拟任务（门禁陌生人、烟雾等）
            scheduler.scheduleAtFixedRate(this::publishRandomEvents, 10, interval, TimeUnit.SECONDS);

            running.set(true);
            log.info("设备模拟器已启动，房屋：{}，房间：{}，间隔：{} 秒",
                    simulatorProperties.getHouseId(), simulatorProperties.getRoom(), interval);
        } catch (Exception e) {
            log.error("设备模拟器启动失败", e);
        }
    }

    /**
     * 停止模拟器。
     */
    public synchronized void stop() {
        running.set(false);
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
                mqttClient.close();
            } catch (MqttException e) {
                log.error("模拟器断开 MQTT 失败", e);
            }
        }
        log.info("设备模拟器已停止");
    }

    /**
     * 当前是否在运行。
     */
    public boolean isRunning() {
        return running.get();
    }

    // ==================== 连接管理 ====================

        /**
         * connect 方法。
         */
    private void connect() throws MqttException {
        String brokerUrl = mqttProperties.getBrokerUrl();
        String clientId = simulatorProperties.getClientId() + "-" + System.currentTimeMillis();
        mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(mqttProperties.getConnectionTimeout());
        options.setKeepAliveInterval(mqttProperties.getKeepAliveInterval());
        if (mqttProperties.getUsername() != null && !mqttProperties.getUsername().isEmpty()) {
            options.setUserName(mqttProperties.getUsername());
        }
        if (mqttProperties.getPassword() != null && !mqttProperties.getPassword().isEmpty()) {
            options.setPassword(mqttProperties.getPassword().toCharArray());
        }

        mqttClient.connect(options);
        log.info("设备模拟器已连接 Broker：{}，客户端 ID：{}", brokerUrl, clientId);
    }

        /**
         * publish 方法。
         *
         * @param payload 消息负载/原始数据
         */
    private void publish(String topic, Object payload) throws Exception {
        if (mqttClient == null || !mqttClient.isConnected()) {
            log.warn("模拟器 MQTT 未连接，无法发布到 {}", topic);
            return;
        }
        String json = objectMapper.writeValueAsString(payload);
        MqttMessage message = new MqttMessage(json.getBytes(StandardCharsets.UTF_8));
        message.setQos(mqttProperties.getQos());
        mqttClient.publish(topic, message);
        log.debug("模拟器发布：{} -> {}", topic, json);
    }

    // ==================== 定时上报任务 ====================

        /**
         * publishSensorData 方法。
         */
    private void publishSensorData() {
        String houseId = simulatorProperties.getHouseId();
        String room = simulatorProperties.getRoom();
        String bedroom = "bedroom";

        try {
            // 1. 客厅温湿度
            TemperatureHumidityReport tempHum = new TemperatureHumidityReport(
                    "temp-humi-living-001",
                    20.0 + random.nextDouble() * 10,
                    40.0 + random.nextDouble() * 30,
                    room
            );
            publish(MqttTopicConstants.sensorData(houseId, room, "temperature-humidity"), tempHum);

            // 2. 卧室床压（夜间场景：22:00-06:00 模拟有人/无人切换）
            boolean occupied = simulateBedOccupied();
            BedPressureReport bedPressure = new BedPressureReport(
                    "bed-pressure-001",
                    occupied,
                    bedroom
            );
            bedPressure.setPressureValue(occupied ? 45.0 + random.nextDouble() * 15 : 0.0);
            bedPressure.setPressureUnit("kg");
            publish(MqttTopicConstants.sensorData(houseId, bedroom, "bed-pressure"), bedPressure);

            // 3. 烟雾报警器（大部分时间正常，偶尔低浓度）
            boolean smokeAlarm = random.nextInt(100) < 5; // 5% 概率触发
            SmokeAlarmEvent smoke = new SmokeAlarmEvent(
                    "smoke-detector-001",
                    smokeAlarm ? "low" : "normal",
                    room,
                    smokeAlarm
            );
            if (smokeAlarm) {
                smoke.setSmokeValue(random.nextDouble() * 200);
            }
            publish(MqttTopicConstants.sensorData(houseId, room, "smoke"), smoke);

            // 4. 手表健康数据
            WatchHealthData watch = new WatchHealthData(
                    "watch-001",
                    60 + random.nextInt(30),
                    95 + random.nextInt(5),
                    36.0 + random.nextDouble() * 1.5
            );
            watch.setWearerId("elder-001");
            watch.setSteps(random.nextInt(5000));
            watch.setBatteryPercent(50 + random.nextInt(50));
            publish(MqttTopicConstants.sensorData(houseId, room, "watch-health"), watch);

        } catch (Exception e) {
            log.error("模拟器传感器数据上报失败", e);
        }
    }

        /**
         * publishRandomEvents 方法。
         */
    private void publishRandomEvents() {
        String houseId = simulatorProperties.getHouseId();
        String room = simulatorProperties.getRoom();

        try {
            // 模拟大门指纹事件：80% 通过，20% 失败并抓拍陌生人
            boolean accessGranted = random.nextInt(100) < 80;
            DoorAccessEvent doorEvent = new DoorAccessEvent(
                    "camera-door-001",
                    accessGranted ? "access-granted" : "access-denied",
                    "door"
            );
            if (accessGranted) {
                doorEvent.setFingerprintUserId("user-001");
                doorEvent.setDescription("指纹识别通过，门锁已打开");
            } else {
                doorEvent.setDescription("指纹识别失败，已抓拍陌生人");
                doorEvent.setImageName("stranger_" + System.currentTimeMillis() + ".jpg");
                doorEvent.setImageUrl("file:///tmp/simulator/stranger_" + System.currentTimeMillis() + ".jpg");
            }
            publish(MqttTopicConstants.eventAlert(houseId, "door", "access"), doorEvent);

        } catch (Exception e) {
            log.error("模拟器随机事件上报失败", e);
        }
    }

    // ==================== 手动触发接口 ====================

    /**
     * 模拟指纹开锁成功。
     */
    public void triggerFingerprintSuccess(String userId) {
        String houseId = simulatorProperties.getHouseId();
        FingerprintEvent event = new FingerprintEvent("fingerprint-door-001", "success", "door");
        event.setUserId(userId);
        event.setUserName("测试用户");
        publishEvent(MqttTopicConstants.eventAlert(houseId, "door", "fingerprint"), event);
    }

    /**
     * 模拟指纹开锁失败。
     */
    public void triggerFingerprintFail() {
        String houseId = simulatorProperties.getHouseId();
        FingerprintEvent event = new FingerprintEvent("fingerprint-door-001", "fail", "door");
        event.setRetryCount(3);
        publishEvent(MqttTopicConstants.eventAlert(houseId, "door", "fingerprint"), event);

        // 联动门口摄像头抓拍陌生人
        DoorAccessEvent doorEvent = new DoorAccessEvent("camera-door-001", "access-denied", "door");
        doorEvent.setDescription("指纹识别连续失败，已抓拍陌生人");
        doorEvent.setImageName("stranger_" + System.currentTimeMillis() + ".jpg");
        doorEvent.setImageUrl("file:///tmp/simulator/stranger_" + System.currentTimeMillis() + ".jpg");
        publishEvent(MqttTopicConstants.eventAlert(houseId, "door", "access"), doorEvent);
    }

    /**
     * 模拟窗帘开关。
     */
    public void triggerCurtain(String command, Integer percent) {
        String houseId = simulatorProperties.getHouseId();
        CurtainStatus status = new CurtainStatus("curtain-001", command, percent, simulatorProperties.getRoom());
        publishEvent(MqttTopicConstants.deviceStatus(houseId, simulatorProperties.getRoom(), "curtain-001"), status);
    }

    /**
     * 模拟蜂鸣器鸣响。
     */
    public void triggerBuzzer(String command, String reason) {
        String houseId = simulatorProperties.getHouseId();
        BuzzerCmd cmd = new BuzzerCmd("buzzer-001", command, simulatorProperties.getRoom());
        cmd.setReason(reason);
        cmd.setDurationMs(3000);
        cmd.setBeepCount(3);
        publishEvent(MqttTopicConstants.deviceStatus(houseId, simulatorProperties.getRoom(), "buzzer-001"), cmd);
    }

    /**
     * 模拟灯光开关。
     */
    public void triggerLight(String command, Integer brightness) {
        String houseId = simulatorProperties.getHouseId();
        LightStatus status = new LightStatus("light-001", "turnOn".equals(command), simulatorProperties.getRoom());
        status.setBrightness(brightness);
        publishEvent(MqttTopicConstants.deviceStatus(houseId, simulatorProperties.getRoom(), "light-001"), status);
    }

    /**
     * 模拟手表紧急呼救。
     */
    public void triggerWatchEmergency() {
        String houseId = simulatorProperties.getHouseId();
        WatchEmergencyCall emergency = new WatchEmergencyCall("watch-001", "sos-button", "elder-001");
        emergency.setWearerName("测试老人");
        emergency.setHeartRate(110);
        emergency.setLocationDesc("客厅");
        publishEvent(MqttTopicConstants.eventAlert(houseId, simulatorProperties.getRoom(), "emergency-call"), emergency);
    }

    /**
     * 模拟摄像头摔倒检测事件。
     */
    public void triggerFallDetection(String room) {
        String houseId = simulatorProperties.getHouseId();
        CameraFallDetectionEvent event = new CameraFallDetectionEvent("camera-" + room + "-001", room, 0.92);
        event.setDescription("检测到老人摔倒，置信度 92%");
        event.setImageName("fall_" + System.currentTimeMillis() + ".jpg");
        event.setImageUrl("file:///tmp/simulator/fall_" + System.currentTimeMillis() + ".jpg");
        publishEvent(MqttTopicConstants.eventAlert(houseId, room, "fall-detection"), event);
    }

    /**
     * 模拟 AI 查找物品请求。
     */
    public void triggerFindItem(String itemName, String room) {
        String houseId = simulatorProperties.getHouseId();
        String sessionId = UUID.randomUUID().toString();

        // 下发抓图指令到摄像头
        CameraFindItemReq req = new CameraFindItemReq("camera-" + room + "-001", itemName, room, sessionId);
        req.setSource("voice");
        req.setUserId("user-001");
        publishEvent(MqttTopicConstants.actuatorCmd(houseId, room, "camera-" + room + "-001"), req);

        // 模拟摄像头响应：返回抓图结果
        CameraFindItemResp resp = new CameraFindItemResp("camera-" + room + "-001", sessionId, itemName, room);
        resp.setStatus("completed");
        resp.setFound(true);
        resp.setImageUrl("file:///tmp/simulator/find_item_" + System.currentTimeMillis() + ".jpg");
        resp.setLocationDescription("在" + room + "的茶几上");
        publishEvent(MqttTopicConstants.cameraImage(houseId, room, "camera-" + room + "-001"), resp);
    }

        /**
         * publishEvent 方法。
         *
         * @param payload 消息负载/原始数据
         */
    private void publishEvent(String topic, Object payload) {
        try {
            publish(topic, payload);
            log.info("模拟器手动触发事件已发布：{}", topic);
        } catch (Exception e) {
            log.error("模拟器手动触发事件失败", e);
        }
    }

    // ==================== 场景辅助 ====================

    /**
     * 模拟床压：夜间 22:00-06:00 随机有人/无人，白天默认无人。
     */
    private boolean simulateBedOccupied() {
        LocalTime now = LocalTime.now();
        boolean night = now.isAfter(LocalTime.of(22, 0)) || now.isBefore(LocalTime.of(6, 0));
        if (night) {
            return random.nextInt(100) < 70; // 夜间 70% 概率有人
        }
        return random.nextInt(100) < 10; // 白天 10% 概率有人
    }
}
