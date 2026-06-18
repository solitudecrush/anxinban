package com.anxinban.dto;

/**
 * 告警信息数据传输对象。
 *
 * <p>用于封装系统中产生的各类告警事件，包括触发老人、告警类型、严重程度、处理状态、
 * 处理人员及位置信息，常见于告警列表、告警详情等接口。</p>
 */
public class AlarmDto {

    /** 告警记录唯一标识，必填，示例：ALM202406150001 */
    private String alarmId;

    /** 触发告警的老人唯一标识，必填，示例：ELD202406150001 */
    private String elderId;

    /** 触发告警的老人姓名，示例：张爷爷 */
    private String elderName;

    /** 触发告警的设备唯一标识，示例：DEV202406150001 */
    private String deviceId;

    /** 告警类型，如 FALL、SOS、ABNORMAL_VITALS 等，必填，示例：FALL */
    private String alarmType;

    /** 告警严重程度，如 HIGH、MEDIUM、LOW，必填，示例：HIGH */
    private String severity;

    /** 告警详细描述，示例：检测到老人在客厅发生跌倒 */
    private String description;

    /** 告警处理状态，如 PENDING、HANDLED、IGNORED，必填，示例：PENDING */
    private String status;

    /** 是否已读，true 表示已读，false 表示未读，必填，示例：false */
    private Boolean isRead;

    /** 告警发生时间，格式 yyyy-MM-dd HH:mm:ss，必填，示例：2024-06-15 14:30:00 */
    private String occurTime;

    /** 告警处理时间，格式 yyyy-MM-dd HH:mm:ss，示例：2024-06-15 14:35:00 */
    private String handleTime;

    /** 处理人用户唯一标识，示例：USR202406150001 */
    private String handler;

    /** 处理人姓名，示例：李护士 */
    private String handlerName;

    /** 处理备注信息，示例：已联系家属并上门查看 */
    private String remark;

    /** 老人所在楼栋，示例：A 栋 */
    private String building;

    /** 老人所在房间号，示例：1201 */
    private String roomNumber;

    /** 单元号，示例：1 单元 */
    private String unit;

    /** 告警现场快照图片 URL，示例：https://example.com/snap/xxx.jpg */
    private String snapshotUrl;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getAlarmId() {
        return alarmId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param alarmId 唯一标识，主键
     */
    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

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
     * 获取名称。
     *
     * @return 名称
     */
    public String getElderName() {
        return elderName;
    }

    /**
     * 设置名称。
     *
     * @param elderName 名称
     */
    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    /**
     * 获取关联设备 ID。
     *
     * @return 关联设备 ID
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * 设置关联设备 ID。
     *
     * @param deviceId 关联设备 ID
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getAlarmType() {
        return alarmType;
    }

    /**
     * 设置类型标识。
     *
     * @param alarmType 类型标识
     */
    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param severity 字段含义待补充
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * 获取描述。
     *
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述。
     *
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取状态标识。
     *
     * @return 状态标识
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态标识。
     *
     * @param status 状态标识
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取是否已读。
     *
     * @return 是否已读
     */
    public Boolean getIsRead() {
        return isRead;
    }

    /**
     * 设置是否已读。
     *
     * @param isRead 是否已读
     */
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getOccurTime() {
        return occurTime;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param occurTime 字段含义待补充
     */
    public void setOccurTime(String occurTime) {
        this.occurTime = occurTime;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getHandleTime() {
        return handleTime;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param handleTime 字段含义待补充
     */
    public void setHandleTime(String handleTime) {
        this.handleTime = handleTime;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getHandler() {
        return handler;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param handler 字段含义待补充
     */
    public void setHandler(String handler) {
        this.handler = handler;
    }

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getHandlerName() {
        return handlerName;
    }

    /**
     * 设置名称。
     *
     * @param handlerName 名称
     */
    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    /**
     * 获取备注。
     *
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注。
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getBuilding() {
        return building;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param building 字段含义待补充
     */
    public void setBuilding(String building) {
        this.building = building;
    }

    /**
     * 获取房间名称/编号。
     *
     * @return 房间名称/编号
     */
    public String getRoomNumber() {
        return roomNumber;
    }

    /**
     * 设置房间名称/编号。
     *
     * @param roomNumber 房间名称/编号
     */
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    /**
     * 获取单位。
     *
     * @return 单位
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 设置单位。
     *
     * @param unit 单位
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getSnapshotUrl() {
        return snapshotUrl;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param snapshotUrl 字段含义待补充
     */
    public void setSnapshotUrl(String snapshotUrl) {
        this.snapshotUrl = snapshotUrl;
    }
}
