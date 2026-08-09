package com.anxinban.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 家庭 PC 边缘网关数据上报统一响应体。
 *
 * <p>所有 {@code POST /api/edge/data} 请求均返回此结构。
 * 通过 {@code duplicate} 字段区分正常处理与幂等重复请求。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"accepted", "upload_id", "message_type", "duplicate"})
public class EdgeDataResponse {

    /** 云端是否已接受该消息 */
    @JsonProperty("accepted")
    private boolean accepted;

    /** 本次上传的幂等 ID（与请求中的 upload_id 一致） */
    @JsonProperty("upload_id")
    private String uploadId;

    /** MQTT 消息类型（vitals / imu / sos / fall / device_status） */
    @JsonProperty("message_type")
    private String messageType;

    /** 是否为重复请求（幂等命中） */
    @JsonProperty("duplicate")
    private boolean duplicate;

    // ==================== Constructors ====================

    public EdgeDataResponse() {
    }

    public EdgeDataResponse(boolean accepted, String uploadId, String messageType, boolean duplicate) {
        this.accepted = accepted;
        this.uploadId = uploadId;
        this.messageType = messageType;
        this.duplicate = duplicate;
    }

    /**
     * 快速构造正常响应（非重复）。
     */
    public static EdgeDataResponse accepted(String uploadId, String messageType) {
        return new EdgeDataResponse(true, uploadId, messageType, false);
    }

    /**
     * 快速构造重复请求响应（幂等命中）。
     */
    public static EdgeDataResponse duplicate(String uploadId, String messageType) {
        return new EdgeDataResponse(true, uploadId, messageType, true);
    }

    // ==================== Getters & Setters ====================

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }
}
