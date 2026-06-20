package com.anxinban.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 陪伴交互记录实体 — 存储 AI 陪伴机器人与老人的对话记录。
 *
 * <p>对应数据字典：companion_record</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "companion_record")
public class CompanionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联老人 ID */
    @Column(name = "elder_id", nullable = false, length = 64)
    private String elderId;

    /** 对话摘要 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    /** 情绪状态：愉悦 / 温馨 / 平静 / 思念 */
    @Column(nullable = false, length = 20)
    private String emotion;

    /** 前端显示颜色 */
    @Column(name = "emotion_color", length = 10)
    private String emotionColor;

    /** 交互时间 */
    @Column(name = "interaction_time", nullable = false)
    private LocalDateTime interactionTime;

    /** 记录创建时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ==================== Getters and Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getElderId() { return elderId; }
    public void setElderId(String elderId) { this.elderId = elderId; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getEmotion() { return emotion; }
    public void setEmotion(String emotion) { this.emotion = emotion; }

    public String getEmotionColor() { return emotionColor; }
    public void setEmotionColor(String emotionColor) { this.emotionColor = emotionColor; }

    public LocalDateTime getInteractionTime() { return interactionTime; }
    public void setInteractionTime(LocalDateTime interactionTime) { this.interactionTime = interactionTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
