package com.anxinban.dto;


/**
 * HealthLatest 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

public class HealthLatestDto {
    /** 关联老人用户 ID */
    private String elderId;
    /** 体温 */
    private Double temperature;
    /** 心率 */
    private Integer heartRate;
    /** 收缩压 */
    private Integer systolic;
    /** 舒张压 */
    private Integer diastolic;
    /** 血氧饱和度 */
    private Integer bloodOxygen;
    /** 字段含义待补充 */
    private String insomnia;
    /** 字段含义待补充 */
    private String sleepTime;
    /** 记录最后更新时间 */
    private String updateTime;

    /**
     * 获取关联老人用户 ID。
     *
     * @return 关联老人用户 ID
     */
    public String getElderId() {
        return elderId;
    }

    /**
     * 设置关联老人用户 ID。
     *
     * @param elderId 关联老人用户 ID
     */
    public void setElderId(String elderId) {
        this.elderId = elderId;
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
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getInsomnia() {
        return insomnia;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param insomnia 字段含义待补充
     */
    public void setInsomnia(String insomnia) {
        this.insomnia = insomnia;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getSleepTime() {
        return sleepTime;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param sleepTime 字段含义待补充
     */
    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * 获取记录最后更新时间。
     *
     * @return 记录最后更新时间
     */
    public String getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置记录最后更新时间。
     *
     * @param updateTime 记录最后更新时间
     */
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
