package com.anxinban.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * AI 服务记录实体 — 统一存储陪伴对话、VLM 找物品、音乐控制三类 AI 交互记录。
 *
 * <p>对应数据字典：ai_service_record（合并原 companion_record 与 vlm_record）</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "ai_service_record")
public class AiServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** AI 服务记录业务 ID */
    @Column(name = "record_id", nullable = false, unique = true, length = 50)
    private String recordId;

    /** 关联老人 ID */
    @Column(name = "elder_id", nullable = false, length = 64)
    private String elderId;

    /**
     * 服务类型：
     * <ul>
     *   <li>companion_chat — 陪伴对话</li>
     *   <li>find_item — VLM 找物品</li>
     *   <li>music_control — 音乐控制</li>
     * </ul>
     */
    @Column(name = "service_type", nullable = false, length = 50)
    private String serviceType;

    /** 用户输入 / 老人提问 */
    @Column(name = "user_text", columnDefinition = "TEXT")
    private String userText;

    /** AI 回复 / 系统回答 */
    @Column(name = "ai_reply", columnDefinition = "TEXT")
    private String aiReply;

    /** 情绪标签（陪伴对话） */
    @Column(length = 50)
    private String emotion;

    /** 情绪标签颜色 */
    @Column(name = "emotion_color", length = 20)
    private String emotionColor;

    /** 寻找的物品名称（VLM 找物品） */
    @Column(length = 100)
    private String item;

    /** 物品所在位置（VLM 找物品） */
    @Column(length = 200)
    private String location;

    /** 查找结果：found / not_found（VLM 找物品） */
    @Column(length = 50)
    private String result;

    /** 对话摘要（陪伴对话） */
    @Column(length = 255)
    private String summary;

    /** 音乐类型（音乐控制） */
    @Column(name = "music_type", length = 50)
    private String musicType;

    /** 交互时间 */
    @Column(name = "interaction_time", nullable = false)
    private LocalDateTime interactionTime;

    /** 记录创建时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ==================== Getters and Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getElderId() { return elderId; }
    public void setElderId(String elderId) { this.elderId = elderId; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getUserText() { return userText; }
    public void setUserText(String userText) { this.userText = userText; }

    public String getAiReply() { return aiReply; }
    public void setAiReply(String aiReply) { this.aiReply = aiReply; }

    public String getEmotion() { return emotion; }
    public void setEmotion(String emotion) { this.emotion = emotion; }

    public String getEmotionColor() { return emotionColor; }
    public void setEmotionColor(String emotionColor) { this.emotionColor = emotionColor; }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getMusicType() { return musicType; }
    public void setMusicType(String musicType) { this.musicType = musicType; }

    public LocalDateTime getInteractionTime() { return interactionTime; }
    public void setInteractionTime(LocalDateTime interactionTime) { this.interactionTime = interactionTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
