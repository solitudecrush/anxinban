package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 智能手表紧急呼救事件消息。
 *
 * 对应硬件：老人佩戴的智能手表，具备 SOS 一键呼叫功能。
 * 当老人长按 SOS 按键时触发，云端需要立即推送告警给前端，并联动摄像头、蜂鸣器等设备。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WatchEmergencyCall extends BaseDeviceMessage {
    private String triggerType;
    private String wearerId;
    private String wearerName;
    private Integer heartRate;
    private Double latitude;
    private Double longitude;
    private String locationDesc;

    /** 是否已经处理 */
    private Boolean handled;

    public WatchEmergencyCall() {
        super();
    }

    public WatchEmergencyCall(String deviceId, String triggerType, String wearerId) {
        super(deviceId);
        this.triggerType = triggerType;
        this.wearerId = wearerId;
    }

    // Getters and Setters

        /**
         * 获取类型标识。
         *
         * @return 类型标识
         */
    public String getTriggerType() {
        return triggerType;
    }

        /**
         * 设置类型标识。
         *
         * @param triggerType 类型标识
         */
    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

        /**
         * 获取唯一标识，主键。
         *
         * @return 唯一标识，主键
         */
    public String getWearerId() {
        return wearerId;
    }

        /**
         * 设置唯一标识，主键。
         *
         * @param wearerId 唯一标识，主键
         */
    public void setWearerId(String wearerId) {
        this.wearerId = wearerId;
    }

        /**
         * 获取名称。
         *
         * @return 名称
         */
    public String getWearerName() {
        return wearerName;
    }

        /**
         * 设置名称。
         *
         * @param wearerName 名称
         */
    public void setWearerName(String wearerName) {
        this.wearerName = wearerName;
    }

        /**
         * 获取心率。
         *
         * @return 心率
         */
    public Integer getHeartRate() {
        return heartRate;
    }

        /**
         * 设置心率。
         *
         * @param heartRate 心率
         */
    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

        /**
         * 获取纬度。
         *
         * @return 纬度
         */
    public Double getLatitude() {
        return latitude;
    }

        /**
         * 设置纬度。
         *
         * @param latitude 纬度
         */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

        /**
         * 获取经度。
         *
         * @return 经度
         */
    public Double getLongitude() {
        return longitude;
    }

        /**
         * 设置经度。
         *
         * @param longitude 经度
         */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

        /**
         * 获取位置信息。
         *
         * @return 位置信息
         */
    public String getLocationDesc() {
        return locationDesc;
    }

        /**
         * 设置位置信息。
         *
         * @param locationDesc 位置信息
         */
    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public Boolean getHandled() {
        return handled;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param handled 字段含义待补充
         */
    public void setHandled(Boolean handled) {
        this.handled = handled;
    }
}
