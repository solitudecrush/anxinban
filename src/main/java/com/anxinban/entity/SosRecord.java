package com.anxinban.entity;


/**
 * SosRecord 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sos_record")
public class SosRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "sos_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String sosId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "trigger_time")
    /** 触发时间 */
    private LocalDateTime triggerTime;

    /** 状态标识 */
    private String status;

    /** 位置信息 */
    private String location;

    @Column(name = "handler_id")
    /** 唯一标识，主键 */
    private String handlerId;

    @Column(name = "handled_time")
    /** 字段含义待补充 */
    private LocalDateTime handledTime;

    @Column(name = "created_at")
    /** 记录创建时间 */
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    /** 记录更新时间 */
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
    public String getSosId() { return sosId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param sosId 唯一标识，主键
     */
    public void setSosId(String sosId) { this.sosId = sosId; }

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
     * 获取触发时间。
     *
     * @return 触发时间
     */
    public LocalDateTime getTriggerTime() { return triggerTime; }
    /**
     * 设置触发时间。
     *
     * @param triggerTime 触发时间
     */
    public void setTriggerTime(LocalDateTime triggerTime) { this.triggerTime = triggerTime; }

    /**
     * 获取状态标识。
     *
     * @return 状态标识
     */
    public String getStatus() { return status; }
    /**
     * 设置状态标识。
     *
     * @param status 状态标识
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * 获取位置信息。
     *
     * @return 位置信息
     */
    public String getLocation() { return location; }
    /**
     * 设置位置信息。
     *
     * @param location 位置信息
     */
    public void setLocation(String location) { this.location = location; }

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getHandlerId() { return handlerId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param handlerId 唯一标识，主键
     */
    public void setHandlerId(String handlerId) { this.handlerId = handlerId; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public LocalDateTime getHandledTime() { return handledTime; }
    /**
     * 设置字段含义待补充。
     *
     * @param handledTime 字段含义待补充
     */
    public void setHandledTime(LocalDateTime handledTime) { this.handledTime = handledTime; }

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
     * 获取记录更新时间。
     *
     * @return 记录更新时间
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    /**
     * 设置记录更新时间。
     *
     * @param updatedAt 记录更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
