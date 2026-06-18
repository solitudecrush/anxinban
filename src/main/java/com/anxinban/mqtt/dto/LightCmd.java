package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 智能灯光控制指令消息。
 *
 * 作用：云端或语音智能体向灯具下发开关、调光、调色等指令。
 * 例如用户说“开灯”或“把卧室灯调暗一点”时，后端生成该指令并发送到 actuator 主题。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LightCmd extends BaseDeviceMessage {
    private String command;
    private Integer brightness;
    private Integer colorTemperature;
    private String color;
    private String room;

    /** 指令来源：app、voice、rule-engine */
    private String source;

    public LightCmd() {
        super();
    }

    public LightCmd(String deviceId, String command, String room) {
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
