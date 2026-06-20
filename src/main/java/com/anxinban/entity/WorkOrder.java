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

    @Column(name = "work_order_id")
    /** 工单显示编号，如 WO20260618001 */
    private String workOrderId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "alarm_id")
    /** 关联告警 ID（alert.id） */
    private String alarmId;

    @Column(name = "type")
    /** 工单类型：上门护理 / 健康巡检 / 设备维修 / 紧急巡检 */
    private String type;

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

    @Column(name = "from_family")
    /** 是否由家属申请转来 */
    private Boolean fromFamily = false;

    @Column(name = "family_request_id")
    /** 关联家属申请 ID */
    private String familyRequestId;

    @Column(name = "finish_time")
    /** 完成时间 */
    private LocalDateTime finishTime;

    @Column(name = "created_at")
    /** 记录创建时间 */
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
    public String getOrderId() { return orderId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param orderId 唯一标识，主键
     */
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(String workOrderId) { this.workOrderId = workOrderId; }

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

    public String getAlarmId() { return alarmId; }
    public void setAlarmId(String alarmId) { this.alarmId = alarmId; }

    /**
     * 获取工单类型。
     *
     * @return 工单类型
     */
    public String getType() { return type; }
    /**
     * 设置工单类型。
     *
     * @param type 工单类型
     */
    public void setType(String type) { this.type = type; }

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

    public Boolean getFromFamily() { return fromFamily; }
    public void setFromFamily(Boolean fromFamily) { this.fromFamily = fromFamily; }

    public String getFamilyRequestId() { return familyRequestId; }
    public void setFamilyRequestId(String familyRequestId) { this.familyRequestId = familyRequestId; }

    public LocalDateTime getFinishTime() { return finishTime; }
    public void setFinishTime(LocalDateTime finishTime) { this.finishTime = finishTime; }

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
