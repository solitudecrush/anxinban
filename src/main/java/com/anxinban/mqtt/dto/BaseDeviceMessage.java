package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseDeviceMessage {
    private String deviceId;
    private String messageId;

    /** 消息产生时间戳，ISO-8601 格式，例如 2026-06-13T10:00:00Z */
    private String timestamp;

    public BaseDeviceMessage() {
        this.messageId = UUID.randomUUID().toString();
        this.timestamp = Instant.now().toString();
    }

    public BaseDeviceMessage(String deviceId) {
        this();
        this.deviceId = deviceId;
    }

    // Getters and Setters

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
