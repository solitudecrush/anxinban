package com.anxinban.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 家庭 PC 边缘网关统一数据上报请求体（HTTP V1.0）。
 *
 * <p>PC 通过 MQTT 从 ESP32 手表收到数据后，以 HTTP POST 方式将原始 MQTT 消息
 * 包装后上传到云端。本 DTO 完整映射所有字段，并通过 Bean Validation 做基础校验。</p>
 *
 * <p>请求 JSON 结构：</p>
 * <pre>{@code
 * {
 *   "api_version": "1.0",
 *   "upload_id": "550e8400-e29b-41d4-a716-446655440000",
 *   "edge_id": "pc_edge_001",
 *   "mqtt_topic": "anxinban/telemetry/vitals",
 *   "received_at": 1786257001.521,
 *   "payload": {
 *     "schema_version": "1.0",
 *     "device_id": "watch_001",
 *     "message_type": "vitals",
 *     "seq": 25,
 *     "timestamp": 0,
 *     "data": { "heart_rate": 82, "spo2": 97, "temperature": 36.5 }
 *   }
 * }
 * }</pre>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdgeDataRequest {

    // ==================== HTTP Wrapper 层 ====================

    @NotBlank(message = "api_version 不能为空")
    @Pattern(regexp = "1\\.0", message = "api_version 仅支持 1.0")
    @JsonProperty("api_version")
    private String apiVersion;

    @NotBlank(message = "upload_id 不能为空")
    @JsonProperty("upload_id")
    private String uploadId;

    @NotBlank(message = "edge_id 不能为空")
    @JsonProperty("edge_id")
    private String edgeId;

    @NotBlank(message = "mqtt_topic 不能为空")
    @JsonProperty("mqtt_topic")
    private String mqttTopic;

    @NotNull(message = "received_at 不能为空")
    @JsonProperty("received_at")
    private BigDecimal receivedAt;

    @NotNull(message = "payload 不能为空")
    @Valid
    private Payload payload;

    // ==================== 内嵌 Payload（MQTT V1.0 层） ====================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {

        @NotBlank(message = "payload.schema_version 不能为空")
        @Pattern(regexp = "1\\.0", message = "payload.schema_version 仅支持 1.0")
        @JsonProperty("schema_version")
        private String schemaVersion;

        @NotBlank(message = "payload.device_id 不能为空")
        @JsonProperty("device_id")
        private String deviceId;

        @NotBlank(message = "payload.message_type 不能为空")
        @JsonProperty("message_type")
        private String messageType;

        @NotNull(message = "payload.seq 不能为空")
        private Long seq;

        @NotNull(message = "payload.timestamp 不能为空")
        private Long timestamp;

        @NotNull(message = "payload.data 不能为空")
        private Map<String, Object> data;

        // ==================== Getters & Setters ====================

        public String getSchemaVersion() {
            return schemaVersion;
        }

        public void setSchemaVersion(String schemaVersion) {
            this.schemaVersion = schemaVersion;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public Long getSeq() {
            return seq;
        }

        public void setSeq(Long seq) {
            this.seq = seq;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }
    }

    // ==================== Getters & Setters ====================

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(String edgeId) {
        this.edgeId = edgeId;
    }

    public String getMqttTopic() {
        return mqttTopic;
    }

    public void setMqttTopic(String mqttTopic) {
        this.mqttTopic = mqttTopic;
    }

    public BigDecimal getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(BigDecimal receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
