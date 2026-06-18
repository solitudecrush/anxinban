package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 蜂鸣器控制指令消息。
 *
 * 对应硬件：部署在大门、卧室等位置的蜂鸣器报警器。
 * 典型联动场景：当陌生人长时间逗留或指纹验证失败时，云端下发 beep 指令提醒。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuzzerCmd extends BaseDeviceMessage {
    private String command;
    private Integer durationMs;
    private Integer beepCount;
    private String room;

    /** 触发原因 */
    private String reason;

    public BuzzerCmd() {
        super();
    }

    public BuzzerCmd(String deviceId, String command, String room) {
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
         * 获取持续时长。
         *
         * @return 持续时长
         */
    public Integer getDurationMs() {
        return durationMs;
    }

        /**
         * 设置持续时长。
         *
         * @param durationMs 持续时长
         */
    public void setDurationMs(Integer durationMs) {
        this.durationMs = durationMs;
    }

        /**
         * 获取数量。
         *
         * @return 数量
         */
    public Integer getBeepCount() {
        return beepCount;
    }

        /**
         * 设置数量。
         *
         * @param beepCount 数量
         */
    public void setBeepCount(Integer beepCount) {
        this.beepCount = beepCount;
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
         * 获取原因。
         *
         * @return 原因
         */
    public String getReason() {
        return reason;
    }

        /**
         * 设置原因。
         *
         * @param reason 原因
         */
    public void setReason(String reason) {
        this.reason = reason;
    }
}
