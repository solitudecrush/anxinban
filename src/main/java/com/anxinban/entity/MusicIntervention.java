package com.anxinban.entity;


/**
 * MusicIntervention 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "music_intervention")
public class MusicIntervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "intervention_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String interventionId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "trigger_reason")
    /** 字段含义待补充 */
    private String triggerReason;

    @Column(name = "music_type")
    /** 类型标识 */
    private String musicType;

    @Column(name = "start_time")
    /** 开始时间 */
    private LocalDateTime startTime;

    @Column(name = "duration_minutes")
    /** 字段含义待补充 */
    private Integer durationMinutes;

    @Column(name = "before_state")
    /** 字段含义待补充 */
    private String beforeState;

    @Column(name = "after_state")
    /** 字段含义待补充 */
    private String afterState;

    /** 结果 */
    private String result;

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
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getInterventionId() { return interventionId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param interventionId 唯一标识，主键
     */
    public void setInterventionId(String interventionId) { this.interventionId = interventionId; }

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
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getTriggerReason() { return triggerReason; }
    /**
     * 设置字段含义待补充。
     *
     * @param triggerReason 字段含义待补充
     */
    public void setTriggerReason(String triggerReason) { this.triggerReason = triggerReason; }

    /**
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getMusicType() { return musicType; }
    /**
     * 设置类型标识。
     *
     * @param musicType 类型标识
     */
    public void setMusicType(String musicType) { this.musicType = musicType; }

    /**
     * 获取开始时间。
     *
     * @return 开始时间
     */
    public LocalDateTime getStartTime() { return startTime; }
    /**
     * 设置开始时间。
     *
     * @param startTime 开始时间
     */
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Integer getDurationMinutes() { return durationMinutes; }
    /**
     * 设置字段含义待补充。
     *
     * @param durationMinutes 字段含义待补充
     */
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getBeforeState() { return beforeState; }
    /**
     * 设置字段含义待补充。
     *
     * @param beforeState 字段含义待补充
     */
    public void setBeforeState(String beforeState) { this.beforeState = beforeState; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getAfterState() { return afterState; }
    /**
     * 设置字段含义待补充。
     *
     * @param afterState 字段含义待补充
     */
    public void setAfterState(String afterState) { this.afterState = afterState; }

    /**
     * 获取结果。
     *
     * @return 结果
     */
    public String getResult() { return result; }
    /**
     * 设置结果。
     *
     * @param result 结果
     */
    public void setResult(String result) { this.result = result; }

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
