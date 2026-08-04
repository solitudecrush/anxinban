package com.anxinban.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 设备上传请求体，兼容 snake_case 与 camelCase JSON 格式。
 *
 * <p>通过 {@link JsonProperty} + {@link JsonAlias} 同时支持两种命名风格，
 * 确保 Web 前端（camelCase）与 AI Mock Cloud（snake_case）均可正常调用。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceUploadRequest {

    @JsonProperty("elder_id")
    @JsonAlias({"elderId"})
    private String elderId;

    @JsonProperty("device_id")
    @JsonAlias({"deviceId"})
    private String deviceId;

    @JsonProperty("heart_rate")
    @JsonAlias({"heartRate"})
    private Integer heartRate;

    @JsonProperty("spo2")
    private Integer spo2;

    @JsonProperty("temperature")
    private Double temperature;

    @JsonProperty("systolic")
    private Integer systolic;

    @JsonProperty("diastolic")
    private Integer diastolic;

    @JsonProperty("activity_status")
    @JsonAlias({"activityStatus"})
    private String activityStatus;

    @JsonProperty("fall_status")
    @JsonAlias({"fallStatus"})
    private String fallStatus;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("location")
    private String location;

    // ==================== Getters & Setters ====================

    public String getElderId() {
        return elderId;
    }

    public void setElderId(String elderId) {
        this.elderId = elderId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public Integer getSystolic() {
        return systolic;
    }

    public void setSystolic(Integer systolic) {
        this.systolic = systolic;
    }

    public Integer getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(Integer diastolic) {
        this.diastolic = diastolic;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
