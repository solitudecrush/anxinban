package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 智能灯光状态消息。
 *
 * 作用：灯光设备响应控制指令后，上报当前状态到状态主题。
 * 包含开关状态、亮度、色温、颜色等信息。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LightStatus extends BaseDeviceMessage {
    private Boolean on;
    private Integer brightness;
    private Integer colorTemperature;
    private String color;
    private String room;
    private Double powerConsumption;

    /** 设备在线状态 */
    private Boolean online;

    public LightStatus() {
        super();
    }

    public LightStatus(String deviceId, Boolean on, String room) {
        super(deviceId);
        this.on = on;
        this.room = room;
    }

    // Getters and Setters

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public Boolean getOn() {
        return on;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param on 字段含义待补充
         */
    public void setOn(Boolean on) {
        this.on = on;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public Integer getBrightness() {
        return brightness;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param brightness 字段含义待补充
         */
    public void setBrightness(Integer brightness) {
        this.brightness = brightness;
    }

        /**
         * 获取体温。
         *
         * @return 体温
         */
    public Integer getColorTemperature() {
        return colorTemperature;
    }

        /**
         * 设置体温。
         *
         * @param colorTemperature 体温
         */
    public void setColorTemperature(Integer colorTemperature) {
        this.colorTemperature = colorTemperature;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public String getColor() {
        return color;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param color 字段含义待补充
         */
    public void setColor(String color) {
        this.color = color;
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
    public Double getPowerConsumption() {
        return powerConsumption;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param powerConsumption 字段含义待补充
         */
    public void setPowerConsumption(Double powerConsumption) {
        this.powerConsumption = powerConsumption;
    }

        /**
         * 获取是否在线。
         *
         * @return 是否在线
         */
    public Boolean getOnline() {
        return online;
    }

        /**
         * 设置是否在线。
         *
         * @param online 是否在线
         */
    public void setOnline(Boolean online) {
        this.online = online;
    }
}
