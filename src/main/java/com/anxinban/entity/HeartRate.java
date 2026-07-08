package com.anxinban.entity;


/**
 * HeartRate 实体类，对应数据库中的心率记录表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "heart_rate")
public class HeartRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "hr_id", nullable = false, unique = true)
    /** 心率记录业务ID */
    private String hrId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "value")
    /** 心率值（次/分） */
    private Integer value;

    @Column(name = "unit")
    /** 单位 */
    private String unit;

    @Column(nullable = false)
    /** 测量时间 */
    private LocalDateTime timestamp;

    @Column(name = "created_at")
    /** 记录创建时间 */
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHrId() { return hrId; }
    public void setHrId(String hrId) { this.hrId = hrId; }

    public String getElderId() { return elderId; }
    public void setElderId(String elderId) { this.elderId = elderId; }

    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
