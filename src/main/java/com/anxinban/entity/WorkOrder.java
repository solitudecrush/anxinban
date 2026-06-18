package com.anxinban.entity;


/**
 * WorkOrder 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_order")
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String orderId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "order_type")
    /** 类型标识 */
    private String orderType;

    /** 描述 */
    private String description;

    /** 状态标识 */
    private String status;

    @Column(name = "creator_id")
    /** 唯一标识，主键 */
    private String creatorId;

    @Column(name = "handler_id")
    /** 唯一标识，主键 */
    private String handlerId;

    @Column(name = "handler_name")
    /** 名称 */
    private String handlerName;

    @Column(name = "handler_phone")
    /** 手机号 */
    private String handlerPhone;

    @Column(name = "complete_time")
    /** 字段含义待补充 */
    private LocalDateTime completeTime;

    @Column(name = "service_request_id")
    /** 唯一标识，主键 */
    private String serviceRequestId;

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
    public String getOrderId() { return orderId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param orderId 唯一标识，主键
     */
    public void setOrderId(String orderId) { this.orderId = orderId; }

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
    public String getOrderType() { return orderType; }
    /**
     * 设置类型标识。
     *
     * @param orderType 类型标识
     */
    public void setOrderType(String orderType) { this.orderType = orderType; }

    /**
     * 获取描述。
     *
     * @return 描述
     */
    public String getDescription() { return description; }
    /**
     * 设置描述。
     *
     * @param description 描述
     */
    public void setDescription(String description) { this.description = description; }

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
    public String getCreatorId() { return creatorId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param creatorId 唯一标识，主键
     */
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

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
     * 获取名称。
     *
     * @return 名称
     */
    public String getHandlerName() { return handlerName; }
    /**
     * 设置名称。
     *
     * @param handlerName 名称
     */
    public void setHandlerName(String handlerName) { this.handlerName = handlerName; }

    /**
     * 获取手机号。
     *
     * @return 手机号
     */
    public String getHandlerPhone() { return handlerPhone; }
    /**
     * 设置手机号。
     *
     * @param handlerPhone 手机号
     */
    public void setHandlerPhone(String handlerPhone) { this.handlerPhone = handlerPhone; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public LocalDateTime getCompleteTime() { return completeTime; }
    /**
     * 设置字段含义待补充。
     *
     * @param completeTime 字段含义待补充
     */
    public void setCompleteTime(LocalDateTime completeTime) { this.completeTime = completeTime; }

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getServiceRequestId() { return serviceRequestId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param serviceRequestId 唯一标识，主键
     */
    public void setServiceRequestId(String serviceRequestId) { this.serviceRequestId = serviceRequestId; }

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
