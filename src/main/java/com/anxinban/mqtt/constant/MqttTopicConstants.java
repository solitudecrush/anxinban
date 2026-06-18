package com.anxinban.mqtt.constant;

/**
 * MQTT 主题规范常量类。
 *
 * 作用：统一约定云端与本地网关/硬件设备之间的 MQTT 主题格式。
 * 所有设备（ESP32、摄像头、手表等）通过 WiFi 接入家庭网关，
 * 网关再通过这些主题与云端 EMQX Broker 交互。
 */
public final class MqttTopicConstants {

    private MqttTopicConstants() {
    }
    public static final String SEPARATOR = "/";
    public static final String SINGLE_LEVEL_WILDCARD = "+";
    public static final String MULTI_LEVEL_WILDCARD = "#";

    // ==================== 主题前缀 ====================
    public static final String HOUSE_PREFIX = "house";

    // ==================== 数据上报主题 ====================
    public static final String SENSOR_DATA_TEMPLATE = "house/%s/%s/sensor/%s/data";
    public static final String DEVICE_STATUS_TEMPLATE = "house/%s/%s/device/%s/status";
    public static final String GATEWAY_STATUS_TEMPLATE = "house/%s/gateway/status";

    // ==================== 指令下发主题 ====================
    public static final String ACTUATOR_CMD_TEMPLATE = "house/%s/%s/actuator/%s/cmd";

    // ==================== 事件告警主题 ====================
    public static final String EVENT_ALERT_TEMPLATE = "house/%s/%s/event/%s/alert";

    // ==================== 摄像头相关主题 ====================
    public static final String CAMERA_IMAGE_TEMPLATE = "house/%s/%s/camera/%s/image";
    // ==================== 智能体对话主题 ====================
    public static final String AGENT_REQUEST_TEMPLATE = "house/%s/agent/request";

    /**
     * AI 智能体响应主题：house/{houseId}/agent/response
     */
    public static final String AGENT_RESPONSE_TEMPLATE = "house/%s/agent/response";

    // ==================== 主题工厂方法 ====================

    public static String sensorData(String houseId, String room, String sensorType) {
        return String.format(SENSOR_DATA_TEMPLATE, houseId, room, sensorType);
    }

    public static String deviceStatus(String houseId, String room, String deviceId) {
        return String.format(DEVICE_STATUS_TEMPLATE, houseId, room, deviceId);
    }

    public static String gatewayStatus(String houseId) {
        return String.format(GATEWAY_STATUS_TEMPLATE, houseId);
    }

    public static String actuatorCmd(String houseId, String room, String deviceId) {
        return String.format(ACTUATOR_CMD_TEMPLATE, houseId, room, deviceId);
    }

    public static String eventAlert(String houseId, String room, String eventType) {
        return String.format(EVENT_ALERT_TEMPLATE, houseId, room, eventType);
    }

    public static String cameraImage(String houseId, String room, String cameraId) {
        return String.format(CAMERA_IMAGE_TEMPLATE, houseId, room, cameraId);
    }

    public static String agentRequest(String houseId) {
        return String.format(AGENT_REQUEST_TEMPLATE, houseId);
    }

    public static String agentResponse(String houseId) {
        return String.format(AGENT_RESPONSE_TEMPLATE, houseId);
    }

    // ==================== 通配订阅主题 ====================
    public static final String ALL_SENSOR_DATA = "house/+/+/sensor/+/data";
    public static final String ALL_DEVICE_STATUS = "house/+/+/device/+/status";
    public static final String ALL_ACTUATOR_STATUS = "house/+/+/actuator/+/status";
    public static final String ALL_EVENT_ALERTS = "house/+/+/event/+/alert";
    public static final String ALL_CAMERA_IMAGES = "house/+/+/camera/+/image";
    public static final String ALL_AGENT_REQUESTS = "house/+/agent/request";
    public static final String ALL_AGENT_RESPONSES = "house/+/agent/response";

    /**
     * 订阅某个房屋下所有消息：house/{houseId}/#
     */
    public static String houseAll(String houseId) {
        return String.format("house/%s/#", houseId);
    }
}
