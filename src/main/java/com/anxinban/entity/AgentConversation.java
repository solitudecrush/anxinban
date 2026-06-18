package com.anxinban.entity;


/**
 * AgentConversation 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_conversation")
public class AgentConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "conversation_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String conversationId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "agent_type")
    /** 类型标识 */
    private String agentType;

    @Column(name = "user_text")
    /** 字段含义待补充 */
    private String userText;

    /** 字段含义待补充 */
    private String intent;

    @Column(name = "agent_reply")
    /** 年龄 */
    private String agentReply;

    @Column(name = "risk_level")
    /** 级别 */
    private String riskLevel;

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
    public String getConversationId() { return conversationId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param conversationId 唯一标识，主键
     */
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

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
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getAgentType() { return agentType; }
    /**
     * 设置类型标识。
     *
     * @param agentType 类型标识
     */
    public void setAgentType(String agentType) { this.agentType = agentType; }

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
     * 获取年龄。
     *
     * @return 年龄
     */
    public String getAgentReply() { return agentReply; }
    /**
     * 设置年龄。
     *
     * @param agentReply 年龄
     */
    public void setAgentReply(String agentReply) { this.agentReply = agentReply; }

    /**
     * 获取级别。
     *
     * @return 级别
     */
    public String getRiskLevel() { return riskLevel; }
    /**
     * 设置级别。
     *
     * @param riskLevel 级别
     */
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

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
