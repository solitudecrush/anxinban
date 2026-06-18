package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 烟雾报警器事件消息。
 *
 * 对应硬件：ESP32 连接的烟雾传感器（MQ-2 等），
 * 当检测到烟雾浓度超标时触发告警，通过事件主题上报云端。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmokeAlarmEvent extends BaseDeviceMessage {
    private String level;
    private String status;
    private Double smokeValue;
    private String room;
    private Boolean alarm;
    private Boolean alarming;

    /** 告警描述信息 */
    private String description;

    public SmokeAlarmEvent() {
        super();
    }

    public SmokeAlarmEvent(String deviceId, String level, String room, Boolean alarming) {
        super(deviceId);
        this.level = level;
        this.room = room;
        this.alarming = alarming;
        this.status = level;
        this.alarm = alarming;
    }

    // Getters and Setters

        /**
         * 获取级别。
         *
         * @return 级别
         */
    public String getLevel() {
        return level;
    }

        /**
         * 设置级别。
         *
         * @param level 级别
         */
    public void setLevel(String level) {
        this.level = level;
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

        /**
         * 获取数值。
         *
         * @return 数值
         */
    public Double getSmokeValue() {
        return smokeValue;
    }

        /**
         * 设置数值。
         *
         * @param smokeValue 数值
         */
    public void setSmokeValue(Double smokeValue) {
        this.smokeValue = smokeValue;
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
    public Boolean getAlarm() {
        return alarm;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param alarm 字段含义待补充
         */
    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public Boolean getAlarming() {
        return alarming;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param alarming 字段含义待补充
         */
    public void setAlarming(Boolean alarming) {
        this.alarming = alarming;
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

    /**
     * 判断是否处于报警状态
     */
    public boolean isAlarm() {
        return Boolean.TRUE.equals(alarm) || Boolean.TRUE.equals(alarming);
    }
}
