package com.anxinban.entity;


/**
 * AlarmEvent 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alarm_event")
public class AlarmEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "alarm_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String alarmId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "device_id")
    /** 关联设备 ID */
    private String deviceId;

    @Column(name = "alarm_type", nullable = false)
    /** 类型标识 */
    private String alarmType;

    @Column(name = "alarm_level", nullable = false)
    /** 级别 */
    private String alarmLevel;

    @Column(name = "alarm_status")
    /** 状态标识 */
    private String alarmStatus;

    /** 描述 */
    private String description;

    @Column(name = "building")
    /** 字段含义待补充 */
    private String building;

    @Column(name = "room_number")
    /** 房间名称/编号 */
    private String roomNumber;

    @Column(name = "unit")
    /** 单位 */
    private String unit;

    @Column(name = "snapshot_url")
    /** 字段含义待补充 */
    private String snapshotUrl;

    @Column(name = "handler")
    /** 字段含义待补充 */
    private String handler;

    @Column(name = "handler_name")
    /** 名称 */
    private String handlerName;

    @Column(name = "handle_remark")
    /** 备注 */
    private String handleRemark;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "created_at")
    /** 记录创建时间 */
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    /** 字段含义待补充 */
    private LocalDateTime resolvedAt;

    @Column(name = "update_time")
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;

    // Getters and Setters
    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public Long getId() { return id; }
    /**
     * 设置唯一标识，主键。
     *
     * @param id 唯一标识，主键
     */
    public void setId(Long id) { this.id = id; }

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getAlarmId() { return alarmId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param alarmId 唯一标识，主键
     */
    public void setAlarmId(String alarmId) { this.alarmId = alarmId; }

    /**
     * 获取关联老人用户 ID。
     *
     * @return 关联老人用户 ID
     */
    public String getElderId() { return elderId; }
    /**
     * 设置关联老人用户 ID。
     *
     * @param elderId 关联老人用户 ID
     */
    public void setElderId(String elderId) { this.elderId = elderId; }

    /**
     * 获取关联设备 ID。
     *
     * @return 关联设备 ID
     */
    public String getDeviceId() { return deviceId; }
    /**
     * 设置关联设备 ID。
     *
     * @param deviceId 关联设备 ID
     */
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    /**
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getAlarmType() { return alarmType; }
    /**
     * 设置类型标识。
     *
     * @param alarmType 类型标识
     */
    public void setAlarmType(String alarmType) { this.alarmType = alarmType; }

    /**
     * 获取级别。
     *
     * @return 级别
     */
    public String getAlarmLevel() { return alarmLevel; }
    /**
     * 设置级别。
     *
     * @param alarmLevel 级别
     */
    public void setAlarmLevel(String alarmLevel) { this.alarmLevel = alarmLevel; }

    /**
     * 获取状态标识。
     *
     * @return 状态标识
     */
    public String getAlarmStatus() { return alarmStatus; }
    /**
     * 设置状态标识。
     *
     * @param alarmStatus 状态标识
     */
    public void setAlarmStatus(String alarmStatus) { this.alarmStatus = alarmStatus; }

    /**
     * 获取描述。
     *
     * @return 描述
     */
    public String getDescription() { return description; }
    /**
     * 设置描述。
     *
     * @param description 描述
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getBuilding() { return building; }
    /**
     * 设置字段含义待补充。
     *
     * @param building 字段含义待补充
     */
    public void setBuilding(String building) { this.building = building; }

    /**
     * 获取房间名称/编号。
     *
     * @return 房间名称/编号
     */
    public String getRoomNumber() { return roomNumber; }
    /**
     * 设置房间名称/编号。
     *
     * @param roomNumber 房间名称/编号
     */
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    /**
     * 获取单位。
     *
     * @return 单位
     */
    public String getUnit() { return unit; }
    /**
     * 设置单位。
     *
     * @param unit 单位
     */
    public void setUnit(String unit) { this.unit = unit; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getSnapshotUrl() { return snapshotUrl; }
    /**
     * 设置字段含义待补充。
     *
     * @param snapshotUrl 字段含义待补充
     */
    public void setSnapshotUrl(String snapshotUrl) { this.snapshotUrl = snapshotUrl; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getHandler() { return handler; }
    /**
     * 设置字段含义待补充。
     *
     * @param handler 字段含义待补充
     */
    public void setHandler(String handler) { this.handler = handler; }

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getHandlerName() { return handlerName; }
    /**
     * 设置名称。
     *
     * @param handlerName 名称
     */
    public void setHandlerName(String handlerName) { this.handlerName = handlerName; }

    /**
     * 获取备注。
     *
     * @return 备注
     */
    public String getHandleRemark() { return handleRemark; }
    /**
     * 设置备注。
     *
     * @param handleRemark 备注
     */
    public void setHandleRemark(String handleRemark) { this.handleRemark = handleRemark; }

    /**
     * 获取是否已读。
     *
     * @return 是否已读
     */
    public Boolean getIsRead() { return isRead; }
    /**
     * 设置是否已读。
     *
     * @param isRead 是否已读
     */
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    /**
     * 获取记录创建时间。
     *
     * @return 记录创建时间
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * 设置记录创建时间。
     *
     * @param createdAt 记录创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    /**
     * 设置字段含义待补充。
     *
     * @param resolvedAt 字段含义待补充
     */
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    /**
     * 获取记录最后更新时间。
     *
     * @return 记录最后更新时间
     */
    public LocalDateTime getUpdateTime() { return updateTime; }
    /**
     * 设置记录最后更新时间。
     *
     * @param updateTime 记录最后更新时间
     */
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
