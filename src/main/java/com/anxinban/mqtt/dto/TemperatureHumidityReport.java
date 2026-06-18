package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 温湿度传感器上报消息。
 *
 * 对应硬件：ESP32 连接的温湿度传感器（如 DHT11/DHT22/SHT30），
 * 通常部署在客厅、卧室等房间，周期性上报环境温湿度。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemperatureHumidityReport extends BaseDeviceMessage {
    private Double temperature;
    private Double humidity;
    private String room;

    /** 电池电量百分比，可选 */
    private Integer batteryPercent;

    public TemperatureHumidityReport() {
        super();
    }

    public TemperatureHumidityReport(String deviceId, Double temperature, Double humidity, String room) {
        super(deviceId);
        this.temperature = temperature;
        this.humidity = humidity;
        this.room = room;
    }

    // Getters and Setters

        /**
         * 获取体温。
         *
         * @return 体温
         */
    public Double getTemperature() {
        return temperature;
    }

        /**
         * 设置体温。
         *
         * @param temperature 体温
         */
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

        /**
         * 获取唯一标识，主键。
         *
         * @return 唯一标识，主键
         */
    public Double getHumidity() {
        return humidity;
    }

        /**
         * 设置唯一标识，主键。
         *
         * @param humidity 唯一标识，主键
         */
    public void setHumidity(Double humidity) {
        this.humidity = humidity;
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
         * 获取电池电量。
         *
         * @return 电池电量
         */
    public Integer getBatteryPercent() {
        return batteryPercent;
    }

        /**
         * 设置电池电量。
         *
         * @param batteryPercent 电池电量
         */
    public void setBatteryPercent(Integer batteryPercent) {
        this.batteryPercent = batteryPercent;
    }
}
