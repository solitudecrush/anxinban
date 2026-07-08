package com.anxinban.entity;


/**
 * BodyTemperature 实体类，对应数据库中的体温记录表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "body_temperature")
public class BodyTemperature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "bt_id", nullable = false, unique = true)
    /** 体温记录业务ID */
    private String btId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "value", precision = 4, scale = 1)
    /** 体温值（℃） */
    private BigDecimal value;

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

    public String getBtId() { return btId; }
    public void setBtId(String btId) { this.btId = btId; }

    public String getElderId() { return elderId; }
    public void setElderId(String elderId) { this.elderId = elderId; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
