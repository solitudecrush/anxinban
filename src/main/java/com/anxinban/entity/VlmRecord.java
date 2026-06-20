package com.anxinban.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * VLM 找物品记录实体 — 存储视觉大模型找物品交互记录。
 *
 * <p>对应数据字典：vlm_record</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "vlm_record")
public class VlmRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联老人 ID */
    @Column(name = "elder_id", nullable = false, length = 64)
    private String elderId;

    /** 老人提问 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    /** 系统回答 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    /** 找到的物品 */
    @Column(length = 100)
    private String item;

    /** 物品位置 */
    @Column(length = 200)
    private String location;

    /** 结果：成功 / 失败 */
    @Column(nullable = false, length = 20)
    private String result;

    /** 交互时间 */
    @Column(name = "query_time", nullable = false)
    private LocalDateTime queryTime;

    /** 记录创建时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ==================== Getters and Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getElderId() { return elderId; }
    public void setElderId(String elderId) { this.elderId = elderId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public LocalDateTime getQueryTime() { return queryTime; }
    public void setQueryTime(LocalDateTime queryTime) { this.queryTime = queryTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
