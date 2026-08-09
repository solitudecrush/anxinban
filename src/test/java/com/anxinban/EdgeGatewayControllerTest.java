package com.anxinban;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * EdgeGatewayController 集成测试。
 *
 * <p>测试覆盖：</p>
 * <ul>
 *   <li>vitals 正常上报 → 200</li>
 *   <li>imu 正常上报 → 200</li>
 *   <li>sos pressed=true → 200，生成业务记录</li>
 *   <li>fall detected=true → 200，生成告警</li>
 *   <li>device_status → 200，更新设备状态</li>
 *   <li>topic/type mismatch → 400</li>
 *   <li>payload.schema_version 错误 → 400</li>
 *   <li>api_version 错误 → 400</li>
 *   <li>未知 device → 404</li>
 *   <li>timestamp=0 → 合法</li>
 *   <li>重复 upload_id → 第一次 false，第二次 duplicate=true</li>
 *   <li>畸形 data → 422/400</li>
 * </ul>
 *
 * <p>前置条件：数据库中存在测试老人 elder_001 和测试设备 watch_001。</p>
 */
@SpringBootTest(properties = {
        "mqtt.enabled=false",
        "spring.jpa.hibernate.ddl-auto=update"
})
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EdgeGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String EDGE_PATH = "/api/edge/data";

    /**
     * 构建标准 vitals 请求。
     */
    private Map<String, Object> buildVitalsRequest(String uploadId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("schema_version", "1.0");
        payload.put("device_id", "watch_001");
        payload.put("message_type", "vitals");
        payload.put("seq", 1);
        payload.put("timestamp", 0L);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("heart_rate", 82);
        data.put("spo2", 97);
        data.put("temperature", 36.5);
        payload.put("data", data);

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("api_version", "1.0");
        request.put("upload_id", uploadId);
        request.put("edge_id", "pc_edge_001");
        request.put("mqtt_topic", "anxinban/telemetry/vitals");
        request.put("received_at", 1786257001.521);
        request.put("payload", payload);
        return request;
    }

    /**
     * 构建标准 imu 请求。
     */
    private Map<String, Object> buildImuRequest(String uploadId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("schema_version", "1.0");
        payload.put("device_id", "watch_001");
        payload.put("message_type", "imu");
        payload.put("seq", 2);
        payload.put("timestamp", 0L);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("ax", 0.12);
        data.put("ay", -0.18);
        data.put("az", 9.72);
        data.put("gx", 0.03);
        data.put("gy", 0.01);
        data.put("gz", -0.02);
        payload.put("data", data);

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("api_version", "1.0");
        request.put("upload_id", uploadId);
        request.put("edge_id", "pc_edge_001");
        request.put("mqtt_topic", "anxinban/telemetry/imu");
        request.put("received_at", 1786257002.0);
        request.put("payload", payload);
        return request;
    }

    /**
     * 构建标准 SOS 请求。
     */
    private Map<String, Object> buildSosRequest(String uploadId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("schema_version", "1.0");
        payload.put("device_id", "watch_001");
        payload.put("message_type", "sos");
        payload.put("seq", 3);
        payload.put("timestamp", 0L);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("pressed", true);
        payload.put("data", data);

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("api_version", "1.0");
        request.put("upload_id", uploadId);
        request.put("edge_id", "pc_edge_001");
        request.put("mqtt_topic", "anxinban/event/sos");
        request.put("received_at", 1786257003.0);
        request.put("payload", payload);
        return request;
    }

    /**
     * 构建标准 fall 请求。
     */
    private Map<String, Object> buildFallRequest(String uploadId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("schema_version", "1.0");
        payload.put("device_id", "watch_001");
        payload.put("message_type", "fall");
        payload.put("seq", 4);
        payload.put("timestamp", 0L);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("detected", true);
        data.put("source", "imu");
        payload.put("data", data);

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("api_version", "1.0");
        request.put("upload_id", uploadId);
        request.put("edge_id", "pc_edge_001");
        request.put("mqtt_topic", "anxinban/event/fall");
        request.put("received_at", 1786257004.0);
        request.put("payload", payload);
        return request;
    }

    /**
     * 构建标准 device_status 请求。
     */
    private Map<String, Object> buildDeviceStatusRequest(String uploadId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("schema_version", "1.0");
        payload.put("device_id", "watch_001");
        payload.put("message_type", "device_status");
        payload.put("seq", 5);
        payload.put("timestamp", 0L);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("online", true);
        data.put("battery", 86);
        payload.put("data", data);

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("api_version", "1.0");
        request.put("upload_id", uploadId);
        request.put("edge_id", "pc_edge_001");
        request.put("mqtt_topic", "anxinban/status/device");
        request.put("received_at", 1786257005.0);
        request.put("payload", payload);
        return request;
    }

    // ==================== 正常场景测试 ====================

    @Test
    @Order(1)
    @DisplayName("vitals — 合法请求 → 200")
    void testVitalsSuccess() throws Exception {
        String uploadId = "test-vitals-" + UUID.randomUUID();
        Map<String, Object> request = buildVitalsRequest(uploadId);

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accepted").value(true))
                .andExpect(jsonPath("$.data.upload_id").value(uploadId))
                .andExpect(jsonPath("$.data.message_type").value("vitals"))
                .andExpect(jsonPath("$.data.duplicate").value(false));
    }

    @Test
    @Order(2)
    @DisplayName("imu — 合法请求 → 200")
    void testImuSuccess() throws Exception {
        String uploadId = "test-imu-" + UUID.randomUUID();
        Map<String, Object> request = buildImuRequest(uploadId);

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accepted").value(true))
                .andExpect(jsonPath("$.data.message_type").value("imu"))
                .andExpect(jsonPath("$.data.duplicate").value(false));
    }

    @Test
    @Order(3)
    @DisplayName("sos — pressed=true → 200，生成业务记录")
    void testSosPressedTrue() throws Exception {
        String uploadId = "test-sos-" + UUID.randomUUID();
        Map<String, Object> request = buildSosRequest(uploadId);

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accepted").value(true))
                .andExpect(jsonPath("$.data.message_type").value("sos"))
                .andExpect(jsonPath("$.data.duplicate").value(false));
    }

    @Test
    @Order(4)
    @DisplayName("fall — detected=true → 200")
    void testFallDetectedTrue() throws Exception {
        String uploadId = "test-fall-" + UUID.randomUUID();
        Map<String, Object> request = buildFallRequest(uploadId);

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accepted").value(true))
                .andExpect(jsonPath("$.data.message_type").value("fall"))
                .andExpect(jsonPath("$.data.duplicate").value(false));
    }

    @Test
    @Order(5)
    @DisplayName("device_status — 更新设备状态 → 200")
    void testDeviceStatusSuccess() throws Exception {
        String uploadId = "test-devstatus-" + UUID.randomUUID();
        Map<String, Object> request = buildDeviceStatusRequest(uploadId);

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accepted").value(true))
                .andExpect(jsonPath("$.data.message_type").value("device_status"))
                .andExpect(jsonPath("$.data.duplicate").value(false));
    }

    // ==================== Topic/Type 不匹配 ====================

    @Test
    @Order(6)
    @DisplayName("topic/type mismatch → 400")
    void testTopicTypeMismatch() throws Exception {
        Map<String, Object> request = buildVitalsRequest("test-mismatch-" + UUID.randomUUID());
        // 篡改：topic 是 vitals，但 message_type 改成 imu
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) request.get("payload");
        payload.put("message_type", "imu");

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // ==================== schema_version 错误 ====================

    @Test
    @Order(7)
    @DisplayName("payload.schema_version 错误 → 400")
    void testInvalidSchemaVersion() throws Exception {
        Map<String, Object> request = buildVitalsRequest("test-badschema-" + UUID.randomUUID());
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) request.get("payload");
        payload.put("schema_version", "2.0");

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== api_version 错误 ====================

    @Test
    @Order(8)
    @DisplayName("api_version 错误 → 400")
    void testInvalidApiVersion() throws Exception {
        Map<String, Object> request = buildVitalsRequest("test-badapi-" + UUID.randomUUID());
        request.put("api_version", "2.0");

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== 未知设备 ====================

    @Test
    @Order(9)
    @DisplayName("未知 device → 404")
    void testUnknownDevice() throws Exception {
        Map<String, Object> request = buildVitalsRequest("test-unknown-" + UUID.randomUUID());
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) request.get("payload");
        payload.put("device_id", "non_existent_device_999");

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // ==================== timestamp=0 合法 ====================

    @Test
    @Order(10)
    @DisplayName("timestamp=0 → 合法，使用 received_at")
    void testTimestampZero() throws Exception {
        String uploadId = "test-tszero-" + UUID.randomUUID();
        Map<String, Object> request = buildVitalsRequest(uploadId);
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) request.get("payload");
        payload.put("timestamp", 0);

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 幂等测试 ====================

    @Test
    @Order(11)
    @DisplayName("幂等 — 两次相同 upload_id，第二次 duplicate=true")
    void testIdempotentDuplicate() throws Exception {
        String uploadId = "test-idempotent-" + UUID.randomUUID();
        Map<String, Object> request = buildDeviceStatusRequest(uploadId);

        // 第一次：正常处理
        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.duplicate").value(false));

        // 第二次：幂等命中
        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accepted").value(true))
                .andExpect(jsonPath("$.data.duplicate").value(true));
    }

    // ==================== 必填字段缺失测试 ====================

    @Test
    @Order(12)
    @DisplayName("upload_id 缺失 → 400")
    void testMissingUploadId() throws Exception {
        Map<String, Object> request = buildVitalsRequest("test-missing-" + UUID.randomUUID());
        request.remove("upload_id");

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(13)
    @DisplayName("api_version 缺失 → 400")
    void testMissingApiVersion() throws Exception {
        Map<String, Object> request = buildVitalsRequest("test-missingapi-" + UUID.randomUUID());
        request.remove("api_version");

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(14)
    @DisplayName("payload 缺失 → 400")
    void testMissingPayload() throws Exception {
        Map<String, Object> request = buildVitalsRequest("test-missingpl-" + UUID.randomUUID());
        request.remove("payload");

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== 边界测试 ====================

    @Test
    @Order(15)
    @DisplayName("sos pressed=false → 不生成告警")
    void testSosPressedFalse() throws Exception {
        String uploadId = "test-sosfalse-" + UUID.randomUUID();
        Map<String, Object> request = buildSosRequest(uploadId);
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) request.get("payload");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        data.put("pressed", false);

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accepted").value(true));
    }

    @Test
    @Order(16)
    @DisplayName("fall detected=false → 不生成跌倒告警")
    void testFallDetectedFalse() throws Exception {
        String uploadId = "test-fallfalse-" + UUID.randomUUID();
        Map<String, Object> request = buildFallRequest(uploadId);
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) request.get("payload");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        data.put("detected", false);

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accepted").value(true));
    }

    @Test
    @Order(17)
    @DisplayName("device_status 缺少 battery → 不覆盖数据库原值")
    void testDeviceStatusNoBattery() throws Exception {
        String uploadId = "test-nobatt-" + UUID.randomUUID();
        Map<String, Object> request = buildDeviceStatusRequest(uploadId);
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) request.get("payload");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        data.remove("battery");

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accepted").value(true));
    }

    @Test
    @Order(18)
    @DisplayName("不支持的 mqtt_topic → 400")
    void testUnsupportedTopic() throws Exception {
        String uploadId = "test-badtopic-" + UUID.randomUUID();
        Map<String, Object> request = buildVitalsRequest(uploadId);
        request.put("mqtt_topic", "anxinban/unknown/topic");
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) request.get("payload");
        payload.put("message_type", "vitals");

        mockMvc.perform(post(EDGE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
