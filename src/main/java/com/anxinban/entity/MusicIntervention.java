package com.anxinban.entity;


/**
 * VoicePrompt 实体类 — 语音/音乐疗法提醒表（voice_prompt）
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voice_prompt")
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

    @Column(name = "reason")
    /** 触发原因 */
    private String reason;

    @Column(name = "music_type")
    /** 类型标识 */
    private String musicType;

    @Column(name = "music")
    /** 音乐/音频名称 */
    private String music;

    @Column(name = "prompt_time")
    /** 提醒时间 */
    private LocalDateTime promptTime;

    @Column(name = "duration")
    /** 持续时间（分钟） */
    private Integer duration;

    @Column(name = "closed")
    /** 是否已关闭 */
    private Boolean closed = false;

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

    @Column(name = "updated_at")
    /** 记录最后更新时间 */
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
     * 获取触发原因。
     *
     * @return 触发原因
     */
    public String getReason() { return reason; }
    /**
     * 设置触发原因。
     *
     * @param reason 触发原因
     */
    public void setReason(String reason) { this.reason = reason; }

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

    public String getMusic() { return music; }
    public void setMusic(String music) { this.music = music; }

    /**
     * 获取提醒时间。
     *
     * @return 提醒时间
     */
    public LocalDateTime getPromptTime() { return promptTime; }
    /**
     * 设置提醒时间。
     *
     * @param promptTime 提醒时间
     */
    public void setPromptTime(LocalDateTime promptTime) { this.promptTime = promptTime; }

    /**
     * 获取持续时间（分钟）。
     *
     * @return 持续时间
     */
    public Integer getDuration() { return duration; }
    /**
     * 设置持续时间（分钟）。
     *
     * @param duration 持续时间
     */
    public void setDuration(Integer duration) { this.duration = duration; }

    public Boolean getClosed() { return closed; }
    public void setClosed(Boolean closed) { this.closed = closed; }

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
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    /**
     * 设置记录最后更新时间。
     *
     * @param updatedAt 记录最后更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
