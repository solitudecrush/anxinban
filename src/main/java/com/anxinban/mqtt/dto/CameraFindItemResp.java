package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * AI 查找物品响应消息。
 *
 * 对应业务：摄像头收到查找物品请求后，抓拍当前画面并返回图片，
 * 后端将图片交给视觉大模型处理，最终把识别结果返回给前端/语音智能体。
 * 该 DTO 用于摄像头端上报抓拍图片，也用于后端向前端返回查找结果。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CameraFindItemResp extends BaseDeviceMessage {
    private String sessionId;
    private String itemName;
    private String room;
    private String imageBase64;
    private String imageUrl;
    private String locationDescription;
    private Boolean found;

    /** 处理状态：capturing（抓拍中）、recognizing（识别中）、completed（完成）、failed（失败） */
    private String status;

    public CameraFindItemResp() {
        super();
    }

    public CameraFindItemResp(String deviceId, String sessionId, String itemName, String room) {
        super(deviceId);
        this.sessionId = sessionId;
        this.itemName = itemName;
        this.room = room;
    }

    // Getters and Setters

        /**
         * 获取会话 ID。
         *
         * @return 会话 ID
         */
    public String getSessionId() {
        return sessionId;
    }

        /**
         * 设置会话 ID。
         *
         * @param sessionId 会话 ID
         */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

        /**
         * 获取名称。
         *
         * @return 名称
         */
    public String getItemName() {
        return itemName;
    }

        /**
         * 设置名称。
         *
         * @param itemName 名称
         */
    public void setItemName(String itemName) {
        this.itemName = itemName;
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
         * 获取描述。
         *
         * @return 描述
         */
    public String getLocationDescription() {
        return locationDescription;
    }

        /**
         * 设置描述。
         *
         * @param locationDescription 描述
         */
    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public Boolean getFound() {
        return found;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param found 字段含义待补充
         */
    public void setFound(Boolean found) {
        this.found = found;
    }

        /**
         * 获取状态标识。
         *
         * @return 状态标识
         */
    public String getStatus() {
        return status;
    }

        /**
         * 设置状态标识。
         *
         * @param status 状态标识
         */
    public void setStatus(String status) {
        this.status = status;
    }
}
