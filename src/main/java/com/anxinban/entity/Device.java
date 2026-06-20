package com.anxinban.entity;


/**
 * Device 实体类 — 设备表（device）。
 * 存储老人家中或社区的智能硬件。
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
    /** 主键，自增 */
    private Long id;

    @Column(name = "device_id", nullable = false, unique = true)
    /** 设备业务编号 */
    private String deviceId;

    @Column(name = "elder_id")
    /** 关联老人 ID */
    private String elderId;

    @Column(name = "type", nullable = false)
    /** 设备类型：手环 / 雷达 / 摄像头 / 门锁 */
    private String type;

    @Column(name = "name")
    /** 设备名称 */
    private String name;

    /** 位置信息 */
    private String location;

    @Column(name = "building")
    /** 所在楼栋 */
    private String building;

    @Column(name = "room")
    /** 所在房号 */
    private String room;

    /** 状态标识 */
    private String status;

    @Column(name = "battery")
    /** 电量百分比 */
    private Integer battery;

    @Column(name = "online")
    /** 在线状态：0=离线，1=在线 */
    private Boolean online = true;

    @Column(name = "last_online")
    /** 最后在线时间 */
    private LocalDateTime lastOnline;

    @Column(name = "created_at")
    /** 创建时间 */
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    /** 更新时间 */
    private LocalDateTime updatedAt;

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
     * 获取设备类型。
     *
     * @return 设备类型
     */
    public String getType() { return type; }
    /**
     * 设置设备类型。
     *
     * @param type 设备类型
     */
    public void setType(String type) { this.type = type; }

    /**
     * 获取设备名称。
     *
     * @return 设备名称
     */
    public String getName() { return name; }
    /**
     * 设置设备名称。
     *
     * @param name 设备名称
     */
    public void setName(String name) { this.name = name; }

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
     * 获取所在楼栋。
     *
     * @return 所在楼栋
     */
    public String getBuilding() { return building; }
    /**
     * 设置所在楼栋。
     *
     * @param building 所在楼栋
     */
    public void setBuilding(String building) { this.building = building; }

    /**
     * 获取所在房号。
     *
     * @return 所在房号
     */
    public String getRoom() { return room; }
    /**
     * 设置所在房号。
     *
     * @param room 所在房号
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
     * 获取电量百分比。
     *
     * @return 电量百分比
     */
    public Integer getBattery() { return battery; }
    /**
     * 设置电量百分比。
     *
     * @param battery 电量百分比
     */
    public void setBattery(Integer battery) { this.battery = battery; }

    /**
     * 获取在线状态。
     *
     * @return 在线状态
     */
    public Boolean getOnline() { return online; }
    /**
     * 设置在线状态。
     *
     * @param online 在线状态
     */
    public void setOnline(Boolean online) { this.online = online; }

    /**
     * 获取最后在线时间。
     *
     * @return 最后在线时间
     */
    public LocalDateTime getLastOnline() { return lastOnline; }
    /**
     * 设置最后在线时间。
     *
     * @param lastOnline 最后在线时间
     */
    public void setLastOnline(LocalDateTime lastOnline) { this.lastOnline = lastOnline; }

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
