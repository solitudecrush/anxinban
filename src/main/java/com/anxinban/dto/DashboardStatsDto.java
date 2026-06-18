package com.anxinban.dto;

/**
 * 管理后台首页统计指标数据传输对象。
 *
 * <p>用于封装首页仪表盘展示的核心运营数据，包括老人总数、今日告警、在线设备、
 * 待处理工单、健康异常及今日入侵检测次数。</p>
 */
public class DashboardStatsDto {

    /** 系统中老人总数量，必填，示例：1280 */
    private long elderTotal;

    /** 今日产生的告警总次数，必填，示例：23 */
    private long todayAlarmCount;

    /** 当前在线的设备数量，必填，示例：856 */
    private long onlineDeviceCount;

    /** 当前待处理工单数量，必填，示例：15 */
    private long pendingOrderCount;

    /** 健康状态异常的老人数量，必填，示例：8 */
    private long healthAbnormalCount;

    /** 今日入侵检测触发次数，必填，示例：2 */
    private long todayIntrusionCount;

    /**
     * 获取总记录数。
     *
     * @return 总记录数
     */
    public long getElderTotal() {
        return elderTotal;
    }

    /**
     * 设置总记录数。
     *
     * @param elderTotal 总记录数
     */
    public void setElderTotal(long elderTotal) {
        this.elderTotal = elderTotal;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public long getTodayAlarmCount() {
        return todayAlarmCount;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param todayAlarmCount 字段含义待补充
     */
    public void setTodayAlarmCount(long todayAlarmCount) {
        this.todayAlarmCount = todayAlarmCount;
    }

    /**
     * 获取是否在线。
     *
     * @return 是否在线
     */
    public long getOnlineDeviceCount() {
        return onlineDeviceCount;
    }

    /**
     * 设置是否在线。
     *
     * @param onlineDeviceCount 是否在线
     */
    public void setOnlineDeviceCount(long onlineDeviceCount) {
        this.onlineDeviceCount = onlineDeviceCount;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public long getPendingOrderCount() {
        return pendingOrderCount;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param pendingOrderCount 字段含义待补充
     */
    public void setPendingOrderCount(long pendingOrderCount) {
        this.pendingOrderCount = pendingOrderCount;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public long getHealthAbnormalCount() {
        return healthAbnormalCount;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param healthAbnormalCount 字段含义待补充
     */
    public void setHealthAbnormalCount(long healthAbnormalCount) {
        this.healthAbnormalCount = healthAbnormalCount;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public long getTodayIntrusionCount() {
        return todayIntrusionCount;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param todayIntrusionCount 字段含义待补充
     */
    public void setTodayIntrusionCount(long todayIntrusionCount) {
        this.todayIntrusionCount = todayIntrusionCount;
    }
}
