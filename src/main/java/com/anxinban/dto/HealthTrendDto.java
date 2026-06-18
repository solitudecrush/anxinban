package com.anxinban.dto;


/**
 * HealthTrend 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import java.util.List;

public class HealthTrendDto {
    /** 关联老人用户 ID */
    private String elderId;
    /** 类型标识 */
    private String type;
    /** 字段含义待补充 */
    private String period;
    /** 数据载荷 */
    private List<HealthTrendItemDto> data;

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
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getType() {
        return type;
    }

    /**
     * 设置类型标识。
     *
     * @param type 类型标识
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getPeriod() {
        return period;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param period 字段含义待补充
     */
    public void setPeriod(String period) {
        this.period = period;
    }

    /**
     * 获取数据载荷。
     *
     * @return 数据载荷
     */
    public List<HealthTrendItemDto> getData() {
        return data;
    }

    /**
     * 设置数据载荷。
     *
     * @param data 数据载荷
     */
    public void setData(List<HealthTrendItemDto> data) {
        this.data = data;
    }

    public static class HealthTrendItemDto {
    /** 字段含义待补充 */
        private String time;
    /** 数值 */
        private Double value;
    /** 收缩压 */
        private Integer systolic;
    /** 舒张压 */
        private Integer diastolic;

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
        public String getTime() {
            return time;
        }

    /**
     * 设置字段含义待补充。
     *
     * @param time 字段含义待补充
     */
        public void setTime(String time) {
            this.time = time;
        }

    /**
     * 获取数值。
     *
     * @return 数值
     */
        public Double getValue() {
            return value;
        }

    /**
     * 设置数值。
     *
     * @param value 数值
     */
        public void setValue(Double value) {
            this.value = value;
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
    }
}
