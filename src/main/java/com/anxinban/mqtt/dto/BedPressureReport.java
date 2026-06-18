package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 床上压力传感器上报消息。
 *
 * 对应硬件：部署在床上的压力/称重传感器（如薄膜压力传感器），
 * 用于检测床上是否有人，支持夜间离床告警等业务。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BedPressureReport extends BaseDeviceMessage {
    private Boolean occupied;
    private Double pressureValue;
    private String pressureUnit;

    /** 房间名称，例如 bedroom */
    private String room;

    public BedPressureReport() {
        super();
    }

    public BedPressureReport(String deviceId, Boolean occupied, String room) {
        super(deviceId);
        this.occupied = occupied;
        this.room = room;
    }

    // Getters and Setters

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public Boolean getOccupied() {
        return occupied;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param occupied 字段含义待补充
         */
    public void setOccupied(Boolean occupied) {
        this.occupied = occupied;
    }

        /**
         * 获取数值。
         *
         * @return 数值
         */
    public Double getPressureValue() {
        return pressureValue;
    }

        /**
         * 设置数值。
         *
         * @param pressureValue 数值
         */
    public void setPressureValue(Double pressureValue) {
        this.pressureValue = pressureValue;
    }

        /**
         * 获取单位。
         *
         * @return 单位
         */
    public String getPressureUnit() {
        return pressureUnit;
    }

        /**
         * 设置单位。
         *
         * @param pressureUnit 单位
         */
    public void setPressureUnit(String pressureUnit) {
        this.pressureUnit = pressureUnit;
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
}
