package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 摄像头摔倒检测事件消息。
 *
 * 对应硬件：部署在客厅、卧室的摄像头（ESP32-CAM 或带 AI 推理能力的摄像头）。
 * 该功能为全时段自动检测，无需用户呼叫；检测到老人摔倒后，
 * 上报图片和告警信息给云端，云端再推送给前端 App 和现场蜂鸣器/网关。
 * 注意：摄像头本身不传输实时视频流，仅在事件触发时上报图片。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CameraFallDetectionEvent extends BaseDeviceMessage {
    private String room;
    private Double confidence;
    private String imageBase64;
    private String imageUrl;
    private String imageName;
    private Boolean confirmed;

    /** 事件描述 */
    private String description;

    public CameraFallDetectionEvent() {
        super();
    }

    public CameraFallDetectionEvent(String deviceId, String room, Double confidence) {
        super(deviceId);
        this.room = room;
        this.confidence = confidence;
    }

    // Getters and Setters

        /**
         * 获取房间名称/编号。
         *
         * @return 房间名称/编号
         */
    public String getRoom() {
        return room;
    }

        /**
         * 设置房间名称/编号。
         *
         * @param room 房间名称/编号
         */
    public void setRoom(String room) {
        this.room = room;
    }

        /**
         * 获取置信度。
         *
         * @return 置信度
         */
    public Double getConfidence() {
        return confidence;
    }

        /**
         * 设置置信度。
         *
         * @param confidence 置信度
         */
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

        /**
         * 获取年龄。
         *
         * @return 年龄
         */
    public String getImageBase64() {
        return imageBase64;
    }

        /**
         * 设置年龄。
         *
         * @param imageBase64 年龄
         */
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

        /**
         * 获取图片 URL。
         *
         * @return 图片 URL
         */
    public String getImageUrl() {
        return imageUrl;
    }

        /**
         * 设置图片 URL。
         *
         * @param imageUrl 图片 URL
         */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

        /**
         * 获取名称。
         *
         * @return 名称
         */
    public String getImageName() {
        return imageName;
    }

        /**
         * 设置名称。
         *
         * @param imageName 名称
         */
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public Boolean getConfirmed() {
        return confirmed;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param confirmed 字段含义待补充
         */
    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

        /**
         * 获取描述。
         *
         * @return 描述
         */
    public String getDescription() {
        return description;
    }

        /**
         * 设置描述。
         *
         * @param description 描述
         */
    public void setDescription(String description) {
        this.description = description;
    }
}
