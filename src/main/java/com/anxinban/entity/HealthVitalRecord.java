package com.anxinban.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康体征记录实体 — 存储老人日常体征数据（心率、血压、血氧、体温），支持趋势查询。
 *
 * <p>对应数据字典：health_record（体征部分，与 medical history 的 HealthRecord 分开）</p>
 * <p>前端 elderly.bp 格式 "130/85"，入库时拆分为 systolic / diastolic。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "health_vital_record")
public class HealthVitalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联老人 ID */
    @Column(name = "elder_id", nullable = false, length = 64)
    private String elderId;

    /** 心率（次/分） */
    @Column(name = "heart_rate")
    private Integer heartRate;

    /** 收缩压（高压） */
    @Column(name = "systolic")
    private Integer systolic;

    /** 舒张压（低压） */
    @Column(name = "diastolic")
    private Integer diastolic;

    /** 血氧饱和度 % */
    @Column(name = "spo2")
    private Integer spo2;

    /** 体温 °C */
    @Column(precision = 3, scale = 1)
    private BigDecimal temperature;

    /** 数据来源：手环 / 雷达 / 手动录入 */
    @Column(length = 50)
    private String source;

    /** 测量时间 */
    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    /** 记录创建时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 记录更新时间 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== Getters and Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getElderId() { return elderId; }
    public void setElderId(String elderId) { this.elderId = elderId; }

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Integer getSystolic() { return systolic; }
    public void setSystolic(Integer systolic) { this.systolic = systolic; }

    public Integer getDiastolic() { return diastolic; }
    public void setDiastolic(Integer diastolic) { this.diastolic = diastolic; }

    public Integer getSpo2() { return spo2; }
    public void setSpo2(Integer spo2) { this.spo2 = spo2; }

    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getMeasuredAt() { return measuredAt; }
    public void setMeasuredAt(LocalDateTime measuredAt) { this.measuredAt = measuredAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
