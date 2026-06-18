package com.anxinban.mqtt;
import com.anxinban.mqtt.dto.TemperatureHumidityReport;
import com.anxinban.mqtt.dto.BedPressureReport;
import com.anxinban.mqtt.dto.SmokeAlarmEvent;
import com.anxinban.mqtt.dto.WatchHealthData;
import com.anxinban.mqtt.dto.FingerprintEvent;
import com.anxinban.mqtt.dto.DoorAccessEvent;
import com.anxinban.mqtt.dto.CameraFallDetectionEvent;
import com.anxinban.mqtt.dto.WatchEmergencyCall;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MQTT 消息解析单元测试。
 * 
 * 测试各种设备消息的序列化和反序列化是否正确。
 */
public class MqttMessageConsumerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testTemperatureHumidityReportSerialization() throws Exception {
        // 创建测试对象
        TemperatureHumidityReport report = new TemperatureHumidityReport(
                "temp-humi-001",
                25.5,
                60.0,
                "living-room"
        );

        // 序列化
        String json = objectMapper.writeValueAsString(report);
        assertNotNull(json);
        assertTrue(json.contains("deviceId"));
        assertTrue(json.contains("temperature"));
        assertTrue(json.contains("humidity"));

        // 反序列化
        TemperatureHumidityReport parsed = objectMapper.readValue(json, TemperatureHumidityReport.class);
        assertEquals("temp-humi-001", parsed.getDeviceId());
        assertEquals(25.5, parsed.getTemperature(), 0.01);
        assertEquals(60.0, parsed.getHumidity(), 0.01);
        assertEquals("living-room", parsed.getRoom());
    }

    @Test
    public void testBedPressureReportSerialization() throws Exception {
        BedPressureReport report = new BedPressureReport("bed-pressure-001", true, "bedroom");
        report.setPressureValue(55.5);
        report.setPressureUnit("kg");

        String json = objectMapper.writeValueAsString(report);
        assertTrue(json.contains("occupied"));
        assertTrue(json.contains("pressureValue"));

        BedPressureReport parsed = objectMapper.readValue(json, BedPressureReport.class);
        assertTrue(parsed.getOccupied());
        assertEquals(55.5, parsed.getPressureValue(), 0.01);
        assertEquals("kg", parsed.getPressureUnit());
    }

    @Test
    public void testSmokeAlarmEventSerialization() throws Exception {
        SmokeAlarmEvent event = new SmokeAlarmEvent("smoke-001", "alarm", "living-room", true);
        event.setSmokeValue(150.0);
        event.setDescription("检测到烟雾，浓度超标");

        String json = objectMapper.writeValueAsString(event);
        assertTrue(json.contains("level"));
        assertTrue(json.contains("smokeValue"));

        SmokeAlarmEvent parsed = objectMapper.readValue(json, SmokeAlarmEvent.class);
        assertEquals("alarm", parsed.getLevel());
        assertTrue(parsed.isAlarm());
    }

    @Test
    public void testWatchHealthDataSerialization() throws Exception {
        WatchHealthData data = new WatchHealthData("watch-001", 75, 98, 36.5);
        data.setWearerId("elder-001");
        data.setSteps(3500);

        String json = objectMapper.writeValueAsString(data);
        assertTrue(json.contains("heartRate"));
        assertTrue(json.contains("bloodOxygen"));
        assertTrue(json.contains("temperature"));

        WatchHealthData parsed = objectMapper.readValue(json, WatchHealthData.class);
        assertEquals(75, parsed.getHeartRate());
        assertEquals(98, parsed.getBloodOxygen());
        assertEquals(36.5, parsed.getTemperature(), 0.01);
    }

    @Test
    public void testFingerprintEventSerialization() throws Exception {
        FingerprintEvent event = new FingerprintEvent("fingerprint-001", "success", "door");
        event.setUserId("user-001");
        event.setUserName("张三");

        String json = objectMapper.writeValueAsString(event);
        assertTrue(json.contains("result"));
        assertTrue(json.contains("userId"));

        FingerprintEvent parsed = objectMapper.readValue(json, FingerprintEvent.class);
        assertEquals("success", parsed.getResult());
        assertEquals("user-001", parsed.getUserId());
    }

    @Test
    public void testDoorAccessEventSerialization() throws Exception {
        DoorAccessEvent event = new DoorAccessEvent("camera-door-001", "access-denied", "door");
        event.setDescription("指纹识别失败，已抓拍陌生人");
        event.setImageUrl("file:///tmp/test.jpg");

        String json = objectMapper.writeValueAsString(event);
        assertTrue(json.contains("eventType"));
        assertTrue(json.contains("imageUrl"));

        DoorAccessEvent parsed = objectMapper.readValue(json, DoorAccessEvent.class);
        assertEquals("access-denied", parsed.getEventType());
        assertEquals("file:///tmp/test.jpg", parsed.getImageUrl());
    }

    @Test
    public void testFallDetectionEventSerialization() throws Exception {
        CameraFallDetectionEvent event = new CameraFallDetectionEvent("camera-living-001", "living-room", 0.92);
        event.setDescription("检测到老人摔倒");
        event.setImageUrl("file:///tmp/fall.jpg");

        String json = objectMapper.writeValueAsString(event);
        assertTrue(json.contains("confidence"));
        assertTrue(json.contains("room"));

        CameraFallDetectionEvent parsed = objectMapper.readValue(json, CameraFallDetectionEvent.class);
        assertEquals(0.92, parsed.getConfidence(), 0.01);
        assertEquals("living-room", parsed.getRoom());
    }

    @Test
    public void testWatchEmergencyCallSerialization() throws Exception {
        WatchEmergencyCall call = new WatchEmergencyCall("watch-001", "sos-button", "elder-001");
        call.setWearerName("李四");
        call.setHeartRate(110);
        call.setLocationDesc("客厅");

        String json = objectMapper.writeValueAsString(call);
        assertTrue(json.contains("triggerType"));
        assertTrue(json.contains("heartRate"));
        assertTrue(json.contains("locationDesc"));

        WatchEmergencyCall parsed = objectMapper.readValue(json, WatchEmergencyCall.class);
        assertEquals("sos-button", parsed.getTriggerType());
        assertEquals(110, parsed.getHeartRate());
        assertEquals("客厅", parsed.getLocationDesc());
    }

    @Test
    public void testTopicPatternMatching() {
        // 测试传感器数据主题
        String sensorTopic = "house/demo-house/living-room/sensor/temperature-humidity/data";
        assertTrue(sensorTopic.matches("house/.+/sensor/.+/data"));

        // 测试事件告警主题
        String eventTopic = "house/demo-house/door/event/access/alert";
        assertTrue(eventTopic.matches("house/.+/event/.+/alert"));

        // 测试设备状态主题
        String statusTopic = "house/demo-house/living-room/device/curtain-001/status";
        assertTrue(statusTopic.matches("house/.+/device/.+/status"));

        // 测试摄像头图片主题
        String cameraTopic = "house/demo-house/living-room/camera/camera-001/image";
        assertTrue(cameraTopic.matches("house/.+/camera/.+/image"));
    }
}
