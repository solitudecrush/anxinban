package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 智能手表健康数据上报消息。
 *
 * 对应硬件：老人佩戴的智能手表，通过 WiFi 与家庭网关通信。
 * 周期性上报心率、血氧、体温、步数、睡眠等健康数据，供云端健康监测与告警分析。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WatchHealthData extends BaseDeviceMessage {
    private Integer heartRate;
    private Integer bloodOxygen;
    private Double temperature;
    private Integer systolic;
    private Integer diastolic;
    private Integer steps;
    private String sleepState;
    private String wearerId;

    /** 电量百分比 */
    private Integer batteryPercent;

    public WatchHealthData() {
        super();
    }

    public WatchHealthData(String deviceId, Integer heartRate, Integer bloodOxygen, Double temperature) {
        super(deviceId);
        this.heartRate = heartRate;
        this.bloodOxygen = bloodOxygen;
        this.temperature = temperature;
    }

    // Getters and Setters

        /**
         * 获取心率。
         *
         * @return 心率
         */
    public Integer getHeartRate() {
        return heartRate;
    }

        /**
         * 设置心率。
         *
         * @param heartRate 心率
         */
    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

        /**
         * 获取血氧饱和度。
         *
         * @return 血氧饱和度
         */
    public Integer getBloodOxygen() {
        return bloodOxygen;
    }

        /**
         * 设置血氧饱和度。
         *
         * @param bloodOxygen 血氧饱和度
         */
    public void setBloodOxygen(Integer bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

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
         * 获取收缩压。
         *
         * @return 收缩压
         */
    public Integer getSystolic() {
        return systolic;
    }

        /**
         * 设置收缩压。
         *
         * @param systolic 收缩压
         */
    public void setSystolic(Integer systolic) {
        this.systolic = systolic;
    }

        /**
         * 获取舒张压。
         *
         * @return 舒张压
         */
    public Integer getDiastolic() {
        return diastolic;
    }

        /**
         * 设置舒张压。
         *
         * @param diastolic 舒张压
         */
    public void setDiastolic(Integer diastolic) {
        this.diastolic = diastolic;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public Integer getSteps() {
        return steps;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param steps 字段含义待补充
         */
    public void setSteps(Integer steps) {
        this.steps = steps;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public String getSleepState() {
        return sleepState;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param sleepState 字段含义待补充
         */
    public void setSleepState(String sleepState) {
        this.sleepState = sleepState;
    }

        /**
         * 获取唯一标识，主键。
         *
         * @return 唯一标识，主键
         */
    public String getWearerId() {
        return wearerId;
    }

        /**
         * 设置唯一标识，主键。
         *
         * @param wearerId 唯一标识，主键
         */
    public void setWearerId(String wearerId) {
        this.wearerId = wearerId;
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
