package com.anxinban.dto;

/**
 * 管理后台首页统计指标数据传输对象。
 *
 * <p>用于封装首页仪表盘展示的核心运营数据，包括老人总数、设备总数、告警总数、
 * 工单总数、员工总数、在线设备数、待处理告警数及待处理工单数。</p>
 */
public class DashboardStatsDto {

    /** 系统中老人总数量 */
    private long elderCount;

    /** 系统中设备总数量 */
    private long deviceCount;

    /** 告警总数量 */
    private long alarmCount;

    /** 工单总数量 */
    private long workOrderCount;

    /** 员工总数量 */
    private long staffCount;

    /** 当前在线的设备数量 */
    private long onlineDeviceCount;

    /** 当前待处理告警数量 */
    private long pendingAlarmCount;

    /** 当前待处理工单数量 */
    private long pendingOrderCount;

    public long getElderCount() {
        return elderCount;
    }

    public void setElderCount(long elderCount) {
        this.elderCount = elderCount;
    }

    public long getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(long deviceCount) {
        this.deviceCount = deviceCount;
    }

    public long getAlarmCount() {
        return alarmCount;
    }

    public void setAlarmCount(long alarmCount) {
        this.alarmCount = alarmCount;
    }

    public long getWorkOrderCount() {
        return workOrderCount;
    }

    public void setWorkOrderCount(long workOrderCount) {
        this.workOrderCount = workOrderCount;
    }

    public long getStaffCount() {
        return staffCount;
    }

    public void setStaffCount(long staffCount) {
        this.staffCount = staffCount;
    }

    public long getOnlineDeviceCount() {
        return onlineDeviceCount;
    }

    public void setOnlineDeviceCount(long onlineDeviceCount) {
        this.onlineDeviceCount = onlineDeviceCount;
    }

    public long getPendingAlarmCount() {
        return pendingAlarmCount;
    }

    public void setPendingAlarmCount(long pendingAlarmCount) {
        this.pendingAlarmCount = pendingAlarmCount;
    }

    public long getPendingOrderCount() {
        return pendingOrderCount;
    }

    public void setPendingOrderCount(long pendingOrderCount) {
        this.pendingOrderCount = pendingOrderCount;
    }
}
