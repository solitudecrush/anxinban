package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 大门门禁事件消息。
 *
 * 对应硬件：大门处的指纹模块 + 摄像头。
 * 指纹验证通过时上报 access-granted 事件，门锁打开；
 * 指纹验证失败时上报 access-denied 事件，并附带抓拍到的陌生人图片信息。
 * 该门口摄像头不具备实时视频流能力，仅在事件触发时抓拍单张图片。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoorAccessEvent extends BaseDeviceMessage {
    private String eventType;
    private String room;
    private String fingerprintUserId;
    private String imageBase64;
    private String imageUrl;
    private String imageName;

    /** 事件发生时的描述 */
    private String description;

    public DoorAccessEvent() {
        super();
    }

    public DoorAccessEvent(String deviceId, String eventType, String room) {
        super(deviceId);
        this.eventType = eventType;
        this.room = room;
    }

    // Getters and Setters

        /**
         * 获取类型标识。
         *
         * @return 类型标识
         */
    public String getEventType() {
        return eventType;
    }

        /**
         * 设置类型标识。
         *
         * @param eventType 类型标识
         */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

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
         * 获取关联用户 ID。
         *
         * @return 关联用户 ID
         */
    public String getFingerprintUserId() {
        return fingerprintUserId;
    }

        /**
         * 设置关联用户 ID。
         *
         * @param fingerprintUserId 关联用户 ID
         */
    public void setFingerprintUserId(String fingerprintUserId) {
        this.fingerprintUserId = fingerprintUserId;
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
