package com.anxinban.entity;


/**
 * AgentIntentLog 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_intent_log")
public class AgentIntentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "intent_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String intentId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    /** 来源 */
    private String source;

    @Column(name = "user_text")
    /** 字段含义待补充 */
    private String userText;

    /** 字段含义待补充 */
    private String intent;

    /** 唯一标识，主键 */
    private Double confidence;

    @Column(name = "handled_by")
    /** 处理人 ID */
    private String handledBy;

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
    public String getIntentId() { return intentId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param intentId 唯一标识，主键
     */
    public void setIntentId(String intentId) { this.intentId = intentId; }

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
     * 获取来源。
     *
     * @return 来源
     */
    public String getSource() { return source; }
    /**
     * 设置来源。
     *
     * @param source 来源
     */
    public void setSource(String source) { this.source = source; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getUserText() { return userText; }
    /**
     * 设置字段含义待补充。
     *
     * @param userText 字段含义待补充
     */
    public void setUserText(String userText) { this.userText = userText; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getIntent() { return intent; }
    /**
     * 设置字段含义待补充。
     *
     * @param intent 字段含义待补充
     */
    public void setIntent(String intent) { this.intent = intent; }

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public Double getConfidence() { return confidence; }
    /**
     * 设置唯一标识，主键。
     *
     * @param confidence 唯一标识，主键
     */
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    /**
     * 获取处理人 ID。
     *
     * @return 处理人 ID
     */
    public String getHandledBy() { return handledBy; }
    /**
     * 设置处理人 ID。
     *
     * @param handledBy 处理人 ID
     */
    public void setHandledBy(String handledBy) { this.handledBy = handledBy; }

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
