package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 窗帘电机状态上报消息。
 *
 * 对应硬件：智能窗帘电机，部署在卧室或客厅。
 * 上报当前开合状态、开合百分比、故障信息等。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurtainStatus extends BaseDeviceMessage {
    private String state;
    private Integer openPercent;
    private String room;
    private Boolean fault;

    /** 故障信息 */
    private String faultInfo;

    public CurtainStatus() {
        super();
    }

    public CurtainStatus(String deviceId, String state, Integer openPercent, String room) {
        super(deviceId);
        this.state = state;
        this.openPercent = openPercent;
        this.room = room;
    }

    // Getters and Setters

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public String getState() {
        return state;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param state 字段含义待补充
         */
    public void setState(String state) {
        this.state = state;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public Integer getOpenPercent() {
        return openPercent;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param openPercent 字段含义待补充
         */
    public void setOpenPercent(Integer openPercent) {
        this.openPercent = openPercent;
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
    public Boolean getFault() {
        return fault;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param fault 字段含义待补充
         */
    public void setFault(Boolean fault) {
        this.fault = fault;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public String getFaultInfo() {
        return faultInfo;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param faultInfo 字段含义待补充
         */
    public void setFaultInfo(String faultInfo) {
        this.faultInfo = faultInfo;
    }
}
