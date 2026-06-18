package com.anxinban.entity;


/**
 * SensorData 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_data")
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "device_id", nullable = false)
    /** 关联设备 ID */
    private String deviceId;

    @Column(name = "sensor_type", nullable = false)
    /** 类型标识 */
    private String sensorType;

    /** 数值 */
    private Double value;

    /** 单位 */
    private String unit;

    @Column(name = "is_abnormal")
    /** 字段含义待补充 */
    private Boolean isAbnormal;

    @Column(nullable = false)
    /** 时间戳 */
    private LocalDateTime timestamp;

    @Column(name = "created_at")
    /** 记录创建时间 */
    private LocalDateTime createdAt;

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
    public String getSensorType() { return sensorType; }
    /**
     * 设置类型标识。
     *
     * @param sensorType 类型标识
     */
    public void setSensorType(String sensorType) { this.sensorType = sensorType; }

    /**
     * 获取数值。
     *
     * @return 数值
     */
    public Double getValue() { return value; }
    /**
     * 设置数值。
     *
     * @param value 数值
     */
    public void setValue(Double value) { this.value = value; }

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
    public Boolean getIsAbnormal() { return isAbnormal; }
    /**
     * 设置字段含义待补充。
     *
     * @param isAbnormal 字段含义待补充
     */
    public void setIsAbnormal(Boolean isAbnormal) { this.isAbnormal = isAbnormal; }

    /**
     * 获取时间戳。
     *
     * @return 时间戳
     */
    public LocalDateTime getTimestamp() { return timestamp; }
    /**
     * 设置时间戳。
     *
     * @param timestamp 时间戳
     */
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

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
}