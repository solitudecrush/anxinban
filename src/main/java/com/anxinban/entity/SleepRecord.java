package com.anxinban.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 睡眠数据实体 — 存储老人睡眠监测数据。
 *
 * <p>对应数据字典：sleep_record</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "sleep_record")
public class SleepRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联老人 ID */
    @Column(name = "elder_id", nullable = false, length = 64)
    private String elderId;

    /** 是否在床上 */
    @Column(name = "in_bed", nullable = false)
    private Boolean inBed = false;

    /** 入睡时间（HH:mm 格式） */
    @Column(name = "bed_time", length = 10)
    private String bedTime;

    /** 夜间醒来次数 */
    @Column(name = "wake_count")
    private Integer wakeCount;

    /** 睡眠质量评分（0-100） */
    @Column(name = "quality_score")
    private Integer qualityScore;

    /** 深睡占比 % */
    @Column(name = "deep_sleep_percent")
    private Integer deepSleepPercent;

    /** 记录时间 */
    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    /** 记录创建时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ==================== Getters and Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getElderId() { return elderId; }
    public void setElderId(String elderId) { this.elderId = elderId; }

    public Boolean getInBed() { return inBed; }
    public void setInBed(Boolean inBed) { this.inBed = inBed; }

    public String getBedTime() { return bedTime; }
    public void setBedTime(String bedTime) { this.bedTime = bedTime; }

    public Integer getWakeCount() { return wakeCount; }
    public void setWakeCount(Integer wakeCount) { this.wakeCount = wakeCount; }

    public Integer getQualityScore() { return qualityScore; }
    public void setQualityScore(Integer qualityScore) { this.qualityScore = qualityScore; }

    public Integer getDeepSleepPercent() { return deepSleepPercent; }
    public void setDeepSleepPercent(Integer deepSleepPercent) { this.deepSleepPercent = deepSleepPercent; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
