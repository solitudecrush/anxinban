package com.anxinban.entity;


/**
 * ServiceRequest 实体类 — 家属服务申请表（family_request）。
 * 存储家属通过 APP 提交的服务申请。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "family_request")
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 主键，自增 */
    private Long id;

    @Column(name = "request_id", nullable = false, unique = true)
    /** 申请业务编号 */
    private String requestId;

    @Column(name = "family_id", nullable = false)
    /** 关联家属用户 ID */
    private String familyId;

    @Column(name = "family_name")
    /** 申请人姓名 */
    private String familyName;

    @Column(name = "family_phone")
    /** 申请人电话 */
    private String familyPhone;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人 ID */
    private String elderId;

    @Column(name = "type")
    /** 申请类型：上门看护 / 设备维修 / 健康咨询 / 紧急求助 / 生活物资代购 */
    private String type;

    /** 申请内容 */
    private String content;

    /** 处理状态：pending / converted / completed / ignored */
    private String status;

    @Column(name = "converted_work_order_id")
    /** 转为工单后关联 work_order.id */
    private String convertedWorkOrderId;

    @Column(name = "reject_reason")
    /** 拒绝原因 */
    private String rejectReason;

    @Column(name = "request_time")
    /** 申请时间 */
    private LocalDateTime requestTime;

    @Column(name = "created_at")
    /** 创建时间 */
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    /** 更新时间 */
    private LocalDateTime updatedAt;

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

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public String getFamilyPhone() { return familyPhone; }
    public void setFamilyPhone(String familyPhone) { this.familyPhone = familyPhone; }

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
     * 获取申请类型。
     *
     * @return 申请类型
     */
    public String getType() { return type; }
    /**
     * 设置申请类型。
     *
     * @param type 申请类型
     */
    public void setType(String type) { this.type = type; }

    /**
     * 获取申请内容。
     *
     * @return 申请内容
     */
    public String getContent() { return content; }
    /**
     * 设置申请内容。
     *
     * @param content 申请内容
     */
    public void setContent(String content) { this.content = content; }

    /**
     * 获取处理状态。
     *
     * @return 处理状态
     */
    public String getStatus() { return status; }
    /**
     * 设置处理状态。
     *
     * @param status 处理状态
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * 获取转为工单后的关联 ID。
     *
     * @return 关联工单 ID
     */
    public String getConvertedWorkOrderId() { return convertedWorkOrderId; }
    /**
     * 设置转为工单后的关联 ID。
     *
     * @param convertedWorkOrderId 关联工单 ID
     */
    public void setConvertedWorkOrderId(String convertedWorkOrderId) { this.convertedWorkOrderId = convertedWorkOrderId; }

    /**
     * 获取拒绝原因。
     *
     * @return 拒绝原因
     */
    public String getRejectReason() { return rejectReason; }
    /**
     * 设置拒绝原因。
     *
     * @param rejectReason 拒绝原因
     */
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

    /**
     * 获取申请时间。
     *
     * @return 申请时间
     */
    public LocalDateTime getRequestTime() { return requestTime; }
    /**
     * 设置申请时间。
     *
     * @param requestTime 申请时间
     */
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }

    /**
     * 获取创建时间。
     *
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * 设置创建时间。
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * 获取更新时间。
     *
     * @return 更新时间
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    /**
     * 设置更新时间。
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
