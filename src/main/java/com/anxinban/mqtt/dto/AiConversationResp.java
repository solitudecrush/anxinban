package com.anxinban.mqtt.dto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * AI 智能体对话响应消息。
 *
 * 对应业务：大模型处理完用户请求后返回的回复，以及需要执行的设备控制动作列表。
 * 后端将响应推送给前端 App、Web 或语音网关，完成一次对话闭环。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiConversationResp extends BaseDeviceMessage {
    private String sessionId;
    private String requestMessageId;
    private String replyText;
    private String intent;
    private List<DeviceAction> actions;
    private Boolean actionsExecuted;

    /** 房屋 ID */
    private String houseId;

    public AiConversationResp() {
        super();
    }

    public AiConversationResp(String deviceId, String sessionId, String replyText) {
        super(deviceId);
        this.sessionId = sessionId;
        this.replyText = replyText;
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
         * 获取提示信息。
         *
         * @return 提示信息
         */
    public String getRequestMessageId() {
        return requestMessageId;
    }

        /**
         * 设置提示信息。
         *
         * @param requestMessageId 提示信息
         */
    public void setRequestMessageId(String requestMessageId) {
        this.requestMessageId = requestMessageId;
    }

        /**
         * 获取扩展信息。
         *
         * @return 扩展信息
         */
    public String getReplyText() {
        return replyText;
    }

        /**
         * 设置扩展信息。
         *
         * @param replyText 扩展信息
         */
    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

        /**
         * 获取意图。
         *
         * @return 意图
         */
    public String getIntent() {
        return intent;
    }

        /**
         * 设置意图。
         *
         * @param intent 意图
         */
    public void setIntent(String intent) {
        this.intent = intent;
    }

        /**
         * 获取动作/操作类型。
         *
         * @return 动作/操作类型
         */
    public List<DeviceAction> getActions() {
        return actions;
    }

        /**
         * 设置动作/操作类型。
         *
         * @param actions 动作/操作类型
         */
    public void setActions(List<DeviceAction> actions) {
        this.actions = actions;
    }

        /**
         * 获取动作/操作类型。
         *
         * @return 动作/操作类型
         */
    public Boolean getActionsExecuted() {
        return actionsExecuted;
    }

        /**
         * 设置动作/操作类型。
         *
         * @param actionsExecuted 动作/操作类型
         */
    public void setActionsExecuted(Boolean actionsExecuted) {
        this.actionsExecuted = actionsExecuted;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DeviceAction {
        private String actionType;
        private String deviceId;
        private String room;

        /** 动作参数，例如亮度百分比、开合百分比 */
        private String parameters;

        public DeviceAction() {
        }

        public DeviceAction(String actionType, String deviceId, String room) {
            this.actionType = actionType;
            this.deviceId = deviceId;
            this.room = room;
        }

        // Getters and Setters

            /**
             * 获取动作/操作类型。
             *
             * @return 动作/操作类型
             */
        public String getActionType() {
            return actionType;
        }

            /**
             * 设置动作/操作类型。
             *
             * @param actionType 动作/操作类型
             */
        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

            /**
             * 获取关联设备 ID。
             *
             * @return 关联设备 ID
             */
        public String getDeviceId() {
            return deviceId;
        }

            /**
             * 设置关联设备 ID。
             *
             * @param deviceId 关联设备 ID
             */
        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
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
             * 获取字段含义待补充。
             *
             * @return 字段含义待补充
             */
        public String getParameters() {
            return parameters;
        }

            /**
             * 设置字段含义待补充。
             *
             * @param parameters 字段含义待补充
             */
        public void setParameters(String parameters) {
            this.parameters = parameters;
        }
    }
}
