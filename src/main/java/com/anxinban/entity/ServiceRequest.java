package com.anxinban.entity;


/**
 * Service 请求参数封装类，用于接收前端传入的数据。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_request")
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "request_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String requestId;

    @Column(name = "family_id", nullable = false)
    /** 关联家属用户 ID */
    private String familyId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "request_type")
    /** 类型标识 */
    private String requestType;

    /** 内容 */
    private String content;

    /** 状态标识 */
    private String status;

    @Column(name = "related_order_id")
    /** 唯一标识，主键 */
    private String relatedOrderId;

    @Column(name = "reject_reason")
    /** 字段含义待补充 */
    private String rejectReason;

    @Column(name = "created_at")
    /** 记录创建时间 */
    private LocalDateTime createdAt;

    @Column(name = "update_time")
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;

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
    public String getRequestId() { return requestId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param requestId 唯一标识，主键
     */
    public void setRequestId(String requestId) { this.requestId = requestId; }

    /**
     * 获取关联家属用户 ID。
     *
     * @return 关联家属用户 ID
     */
    public String getFamilyId() { return familyId; }
    /**
     * 设置关联家属用户 ID。
     *
     * @param familyId 关联家属用户 ID
     */
    public void setFamilyId(String familyId) { this.familyId = familyId; }

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
    public String getRequestType() { return requestType; }
    /**
     * 设置类型标识。
     *
     * @param requestType 类型标识
     */
    public void setRequestType(String requestType) { this.requestType = requestType; }

    /**
     * 获取内容。
     *
     * @return 内容
     */
    public String getContent() { return content; }
    /**
     * 设置内容。
     *
     * @param content 内容
     */
    public void setContent(String content) { this.content = content; }

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
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getRelatedOrderId() { return relatedOrderId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param relatedOrderId 唯一标识，主键
     */
    public void setRelatedOrderId(String relatedOrderId) { this.relatedOrderId = relatedOrderId; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getRejectReason() { return rejectReason; }
    /**
     * 设置字段含义待补充。
     *
     * @param rejectReason 字段含义待补充
     */
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

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
