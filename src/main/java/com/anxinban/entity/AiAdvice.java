package com.anxinban.entity;


/**
 * AiAdvice 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_advice")
public class AiAdvice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "advice_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String adviceId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "advice_type")
    /** 类型标识 */
    private String adviceType;

    @Column(name = "input_summary")
    /** 字段含义待补充 */
    private String inputSummary;

    @Column(name = "advice_content")
    /** 内容 */
    private String adviceContent;

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
    public String getAdviceId() { return adviceId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param adviceId 唯一标识，主键
     */
    public void setAdviceId(String adviceId) { this.adviceId = adviceId; }

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
    public String getAdviceType() { return adviceType; }
    /**
     * 设置类型标识。
     *
     * @param adviceType 类型标识
     */
    public void setAdviceType(String adviceType) { this.adviceType = adviceType; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getInputSummary() { return inputSummary; }
    /**
     * 设置字段含义待补充。
     *
     * @param inputSummary 字段含义待补充
     */
    public void setInputSummary(String inputSummary) { this.inputSummary = inputSummary; }

    /**
     * 获取内容。
     *
     * @return 内容
     */
    public String getAdviceContent() { return adviceContent; }
    /**
     * 设置内容。
     *
     * @param adviceContent 内容
     */
    public void setAdviceContent(String adviceContent) { this.adviceContent = adviceContent; }

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
