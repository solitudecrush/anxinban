package com.anxinban.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 设备上传请求体，兼容队长 AI Mock Cloud 的 snake_case JSON 格式。
 *
 * <p>使用 {@link JsonProperty} 映射 snake_case JSON key 到 Java 驼峰字段，
 * 并通过 {@link JsonIgnoreProperties#ignoreUnknown()} 忽略前端可能发送的额外字段，
 * 确保不会因为字段不匹配而返回 400。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceUploadRequest {

    @JsonProperty("elder_id")
    private String elderId;

    @JsonProperty("heart_rate")
    private Integer heartRate;

    @JsonProperty("spo2")
    private Integer spo2;

    @JsonProperty("temperature")
    private Double temperature;

    @JsonProperty("activity_status")
    private String activityStatus;

    @JsonProperty("fall_status")
    private String fallStatus;

    @JsonProperty("location")
    private String location;

    // ==================== Getters & Setters ====================

    public String getElderId() {
        return elderId;
    }

    public void setElderId(String elderId) {
        this.elderId = elderId;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getSpo2() {
        return spo2;
    }

    public void setSpo2(Integer spo2) {
        this.spo2 = spo2;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    public String getFallStatus() {
        return fallStatus;
    }

    public void setFallStatus(String fallStatus) {
        this.fallStatus = fallStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
