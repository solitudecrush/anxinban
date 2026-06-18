package com.anxinban.entity;


/**
 * Device 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "device_id", nullable = false, unique = true)
    /** 关联设备 ID */
    private String deviceId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "device_type", nullable = false)
    /** 设备类型 */
    private String deviceType;

    @Column(name = "device_name")
    /** 设备名称 */
    private String deviceName;

    /** 位置信息 */
    private String location;

    @Column(name = "building")
    /** 字段含义待补充 */
    private String building;

    @Column(name = "room")
    /** 房间名称/编号 */
    private String room;

    /** 状态标识 */
    private String status;

    @Column(name = "battery_level")
    /** 级别 */
    private Integer batteryLevel;

    @Column(name = "last_online_time")
    /** 是否在线 */
    private LocalDateTime lastOnlineTime;

    @Column(name = "created_at")
    /** 记录创建时间 */
    private LocalDateTime createdAt;

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
     * 获取设备类型。
     *
     * @return 设备类型
     */
    public String getDeviceType() { return deviceType; }
    /**
     * 设置设备类型。
     *
     * @param deviceType 设备类型
     */
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    /**
     * 获取设备名称。
     *
     * @return 设备名称
     */
    public String getDeviceName() { return deviceName; }
    /**
     * 设置设备名称。
     *
     * @param deviceName 设备名称
     */
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    /**
     * 获取位置信息。
     *
     * @return 位置信息
     */
    public String getLocation() { return location; }
    /**
     * 设置位置信息。
     *
     * @param location 位置信息
     */
    public void setLocation(String location) { this.location = location; }

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
    public String getRoom() { return room; }
    /**
     * 设置房间名称/编号。
     *
     * @param room 房间名称/编号
     */
    public void setRoom(String room) { this.room = room; }

    /**
     * 获取状态标识。
     *
     * @return 状态标识
     */
    public String getStatus() { return status; }
    /**
     * 设置状态标识。
     *
     * @param status 状态标识
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * 获取级别。
     *
     * @return 级别
     */
    public Integer getBatteryLevel() { return batteryLevel; }
    /**
     * 设置级别。
     *
     * @param batteryLevel 级别
     */
    public void setBatteryLevel(Integer batteryLevel) { this.batteryLevel = batteryLevel; }

    /**
     * 获取是否在线。
     *
     * @return 是否在线
     */
    public LocalDateTime getLastOnlineTime() { return lastOnlineTime; }
    /**
     * 设置是否在线。
     *
     * @param lastOnlineTime 是否在线
     */
    public void setLastOnlineTime(LocalDateTime lastOnlineTime) { this.lastOnlineTime = lastOnlineTime; }

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
