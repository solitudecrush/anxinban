package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 窗帘电机控制指令消息。
 *
 * 作用：云端或用户通过 App/语音智能体向窗帘电机下发控制指令，
 * 例如打开、关闭、暂停、设置开合百分比。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurtainCmd extends BaseDeviceMessage {
    private String command;
    private Integer targetPercent;
    private String room;

    /** 指令来源：app、voice、rule-engine、manual */
    private String source;

    public CurtainCmd() {
        super();
    }

    public CurtainCmd(String deviceId, String command, String room) {
        super(deviceId);
        this.command = command;
        this.room = room;
    }

    // Getters and Setters

        /**
         * 获取控制命令。
         *
         * @return 控制命令
         */
    public String getCommand() {
        return command;
    }

        /**
         * 设置控制命令。
         *
         * @param command 控制命令
         */
    public void setCommand(String command) {
        this.command = command;
    }

        /**
         * 获取目标。
         *
         * @return 目标
         */
    public Integer getTargetPercent() {
        return targetPercent;
    }

        /**
         * 设置目标。
         *
         * @param targetPercent 目标
         */
    public void setTargetPercent(Integer targetPercent) {
        this.targetPercent = targetPercent;
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
}
