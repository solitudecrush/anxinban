package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * AI 智能体对话请求消息。
 *
 * 对应业务：用户通过语音或文字与智能体交互，例如询问老人状态、控制灯光/窗帘、查找物品等。
 * 后端收到请求后，结合当前家庭设备状态构造 Prompt，调用大模型生成回复。
 * 该 DTO 既可以通过 MQTT 从网关/硬件侧接收，也可以通过 REST API 从 App/Web 接收。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiConversationReq extends BaseDeviceMessage {
    private String userId;
    private String text;
    private String inputType;
    private String sessionId;
    private String houseId;
    private String deviceStatusSnapshot;

    /** 是否期望执行设备控制指令 */
    private Boolean expectAction;

    public AiConversationReq() {
        super();
    }

    public AiConversationReq(String deviceId, String userId, String text, String sessionId) {
        super(deviceId);
        this.userId = userId;
        this.text = text;
        this.sessionId = sessionId;
    }

    // Getters and Setters

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
         * 获取扩展信息。
         *
         * @return 扩展信息
         */
    public String getText() {
        return text;
    }

        /**
         * 设置扩展信息。
         *
         * @param text 扩展信息
         */
    public void setText(String text) {
        this.text = text;
    }

        /**
         * 获取类型标识。
         *
         * @return 类型标识
         */
    public String getInputType() {
        return inputType;
    }

        /**
         * 设置类型标识。
         *
         * @param inputType 类型标识
         */
    public void setInputType(String inputType) {
        this.inputType = inputType;
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

        /**
         * 获取房屋/家庭 ID。
         *
         * @return 房屋/家庭 ID
         */
    public String getHouseId() {
        return houseId;
    }

        /**
         * 设置房屋/家庭 ID。
         *
         * @param houseId 房屋/家庭 ID
         */
    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

        /**
         * 获取设备状态。
         *
         * @return 设备状态
         */
    public String getDeviceStatusSnapshot() {
        return deviceStatusSnapshot;
    }

        /**
         * 设置设备状态。
         *
         * @param deviceStatusSnapshot 设备状态
         */
    public void setDeviceStatusSnapshot(String deviceStatusSnapshot) {
        this.deviceStatusSnapshot = deviceStatusSnapshot;
    }

        /**
         * 获取动作/操作类型。
         *
         * @return 动作/操作类型
         */
    public Boolean getExpectAction() {
        return expectAction;
    }

        /**
         * 设置动作/操作类型。
         *
         * @param expectAction 动作/操作类型
         */
    public void setExpectAction(Boolean expectAction) {
        this.expectAction = expectAction;
    }
}
