package com.anxinban.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 物品寻找记录实体 — 存储每日物品寻找记录，用于健康详情页。
 *
 * <p>对应数据表：item_find_logs</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "item_find_logs")
public class ItemFindLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联用户 ID */
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    /** 记录日期 */
    @Column(nullable = false)
    private LocalDate date;

    /** 寻找的物品名称 */
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    /** 寻找耗时（秒） */
    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    /** 是否找到 */
    @Column(nullable = false)
    private Boolean found;

    /** 物品所在位置 */
    @Column(length = 100)
    private String position;

    /** 记录创建时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ==================== Getters and Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public Boolean getFound() { return found; }
    public void setFound(Boolean found) { this.found = found; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
