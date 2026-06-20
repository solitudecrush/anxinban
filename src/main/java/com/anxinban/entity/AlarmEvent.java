package com.anxinban.entity;


/**
 * AlarmEvent 实体类 — 告警表（alert）。
 * 存储 AI / 传感器产生的各类告警。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert")
public class AlarmEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 主键，自增 */
    private Long id;

    @Column(name = "alarm_id", nullable = false, unique = true, length = 64)
    /** 后端生成的告警唯一编号（云端返回如 alarm_367f136d） */
    private String alarmId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人 ID */
    private String elderId;

    @Column(name = "device_id")
    /** 关联设备 ID */
    private String deviceId;

    @Column(name = "type", nullable = false)
    /** 告警类型：跌倒检测 / 心率异常 / 血压异常 / 手表SOS / 烟雾告警 / 门锁抓拍 / 长时间无活动 / 睡眠困难 / 闯入告警 / 压力传感器异常 / VLM物品寻找记录 */
    private String type;

    @Column(name = "risk_level")
    /** 风险等级：高风险 / 中风险 / 低风险 */
    private String riskLevel;

    @Column(name = "status")
    /** 处理状态：pending（待处理）/ handled（已处理） */
    private String status;

    /** 告警详细描述 */
    private String description;

    @Column(name = "building")
    /** 楼栋 */
    private String building;

    @Column(name = "room_number")
    /** 房号 */
    private String roomNumber;

    @Column(name = "unit")
    /** 单元 */
    private String unit;

    @Column(name = "location")
    /** 告警位置，例如客厅、卧室、卫生间 */
    private String location;

    @Column(name = "snapshot_url")
    /** 抓拍图片 URL / data URI */
    private String snapshotUrl;

    @Column(name = "handler_id")
    /** 处理人 ID（staff.id） */
    private String handlerId;

    @Column(name = "handler_name")
    /** 处理人姓名（反规范化，便于展示） */
    private String handlerName;

    @Column(name = "handle_note", columnDefinition = "TEXT")
    /** 处理备注 */
    private String handleNote;

    @Column(name = "occur_time")
    /** 告警发生时间 */
    private LocalDateTime occurTime;

    @Column(name = "handle_time")
    /** 处理时间 */
    private LocalDateTime handleTime;

    @Column(name = "is_read")
    /** 是否已读 */
    private Boolean isRead = false;

    @Column(name = "created_at")
    /** 创建时间 */
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    /** 更新时间 */
    private LocalDateTime updatedAt;

    // Getters and Setters
    /**
     * 获取主键。
     *
     * @return 主键
     */
    public Long getId() { return id; }
    /**
     * 设置主键。
     *
     * @param id 主键
     */
    public void setId(Long id) { this.id = id; }

    /**
     * 获取告警唯一编号。
     *
     * @return 告警唯一编号
     */
    public String getAlarmId() { return alarmId; }
    /**
     * 设置告警唯一编号。
     *
     * @param alarmId 告警唯一编号
     */
    public void setAlarmId(String alarmId) { this.alarmId = alarmId; }

    /**
     * 获取关联老人 ID。
     *
     * @return 关联老人 ID
     */
    public String getElderId() { return elderId; }
    /**
     * 设置关联老人 ID。
     *
     * @param elderId 关联老人 ID
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
     * 获取告警类型。
     *
     * @return 告警类型
     */
    public String getType() { return type; }
    /**
     * 设置告警类型。
     *
     * @param type 告警类型
     */
    public void setType(String type) { this.type = type; }

    /**
     * 获取风险等级。
     *
     * @return 风险等级
     */
    public String getRiskLevel() { return riskLevel; }
    /**
     * 设置风险等级。
     *
     * @param riskLevel 风险等级
     */
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    /**
     * 获取处理状态。
     *
     * @return 处理状态
     */
    public String getStatus() { return status; }
    /**
     * 设置处理状态。
     *
     * @param status 处理状态
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * 获取告警详细描述。
     *
     * @return 告警详细描述
     */
    public String getDescription() { return description; }
    /**
     * 设置告警详细描述。
     *
     * @param description 告警详细描述
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * 获取楼栋。
     *
     * @return 楼栋
     */
    public String getBuilding() { return building; }
    /**
     * 设置楼栋。
     *
     * @param building 楼栋
     */
    public void setBuilding(String building) { this.building = building; }

    /**
     * 获取房号。
     *
     * @return 房号
     */
    public String getRoomNumber() { return roomNumber; }
    /**
     * 设置房号。
     *
     * @param roomNumber 房号
     */
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    /**
     * 获取单元。
     *
     * @return 单元
     */
    public String getUnit() { return unit; }
    /**
     * 设置单元。
     *
     * @param unit 单元
     */
    public void setUnit(String unit) { this.unit = unit; }

    /**
     * 获取告警位置。
     *
     * @return 告警位置
     */
    public String getLocation() { return location; }
    /**
     * 设置告警位置。
     *
     * @param location 告警位置
     */
    public void setLocation(String location) { this.location = location; }

    /**
     * 获取抓拍图片 URL。
     *
     * @return 抓拍图片 URL
     */
    public String getSnapshotUrl() { return snapshotUrl; }
    /**
     * 设置抓拍图片 URL。
     *
     * @param snapshotUrl 抓拍图片 URL
     */
    public void setSnapshotUrl(String snapshotUrl) { this.snapshotUrl = snapshotUrl; }

    /**
     * 获取处理人 ID。
     *
     * @return 处理人 ID
     */
    public String getHandlerId() { return handlerId; }
    /**
     * 设置处理人 ID。
     *
     * @param handlerId 处理人 ID
     */
    public void setHandlerId(String handlerId) { this.handlerId = handlerId; }

    /**
     * 获取处理人姓名。
     *
     * @return 处理人姓名
     */
    public String getHandlerName() { return handlerName; }
    /**
     * 设置处理人姓名。
     *
     * @param handlerName 处理人姓名
     */
    public void setHandlerName(String handlerName) { this.handlerName = handlerName; }

    /**
     * 获取处理备注。
     *
     * @return 处理备注
     */
    public String getHandleNote() { return handleNote; }
    /**
     * 设置处理备注。
     *
     * @param handleNote 处理备注
     */
    public void setHandleNote(String handleNote) { this.handleNote = handleNote; }

    /**
     * 获取告警发生时间。
     *
     * @return 告警发生时间
     */
    public LocalDateTime getOccurTime() { return occurTime; }
    /**
     * 设置告警发生时间。
     *
     * @param occurTime 告警发生时间
     */
    public void setOccurTime(LocalDateTime occurTime) { this.occurTime = occurTime; }

    /**
     * 获取处理时间。
     *
     * @return 处理时间
     */
    public LocalDateTime getHandleTime() { return handleTime; }
    /**
     * 设置处理时间。
     *
     * @param handleTime 处理时间
     */
    public void setHandleTime(LocalDateTime handleTime) { this.handleTime = handleTime; }

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
     * 获取创建时间。
     *
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * 设置创建时间。
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * 获取更新时间。
     *
     * @return 更新时间
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    /**
     * 设置更新时间。
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
