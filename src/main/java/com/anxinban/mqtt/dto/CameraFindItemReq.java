package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * AI 查找物品请求消息。
 *
 * 对应业务：用户通过语音呼叫智能体“帮我找一下遥控器”，
 * 后端将查找请求发送到指定摄像头的 actuator 主题，摄像头抓拍当前画面后返回图片。
 * 后端只负责传输请求与图片，具体的视觉大模型识别逻辑由其他服务或硬件侧完成。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CameraFindItemReq extends BaseDeviceMessage {
    private String itemName;
    private String room;
    private String source;
    private String userId;

    /** 会话 ID，用于关联请求与响应 */
    private String sessionId;

    public CameraFindItemReq() {
        super();
    }

    public CameraFindItemReq(String deviceId, String itemName, String room, String sessionId) {
        super(deviceId);
        this.itemName = itemName;
        this.room = room;
        this.sessionId = sessionId;
    }

    // Getters and Setters

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
         * 获取来源。
         *
         * @return 来源
         */
    public String getSource() {
        return source;
    }

        /**
         * 设置来源。
         *
         * @param source 来源
         */
    public void setSource(String source) {
        this.source = source;
    }

        /**
         * 获取关联用户 ID。
         *
         * @return 关联用户 ID
         */
    public String getUserId() {
        return userId;
    }

        /**
         * 设置关联用户 ID。
         *
         * @param userId 关联用户 ID
         */
    public void setUserId(String userId) {
        this.userId = userId;
    }

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
}
