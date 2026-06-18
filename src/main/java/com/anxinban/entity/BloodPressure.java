package com.anxinban.entity;


/**
 * BloodPressure 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_pressure")
public class BloodPressure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "bp_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String bpId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "systolic")
    /** 收缩压 */
    private Integer systolic;

    @Column(name = "diastolic")
    /** 舒张压 */
    private Integer diastolic;

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
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getBpId() { return bpId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param bpId 唯一标识，主键
     */
    public void setBpId(String bpId) { this.bpId = bpId; }

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
     * 获取收缩压。
     *
     * @return 收缩压
     */
    public Integer getSystolic() { return systolic; }
    /**
     * 设置收缩压。
     *
     * @param systolic 收缩压
     */
    public void setSystolic(Integer systolic) { this.systolic = systolic; }

    /**
     * 获取舒张压。
     *
     * @return 舒张压
     */
    public Integer getDiastolic() { return diastolic; }
    /**
     * 设置舒张压。
     *
     * @param diastolic 舒张压
     */
    public void setDiastolic(Integer diastolic) { this.diastolic = diastolic; }

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
