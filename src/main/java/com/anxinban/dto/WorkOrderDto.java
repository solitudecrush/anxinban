package com.anxinban.dto;


/**
 * WorkOrder 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

public class WorkOrderDto {
    /** 唯一标识，主键 */
    private String orderId;
    /** 关联老人用户 ID */
    private String elderId;
    /** 名称 */
    private String elderName;
    /** 类型标识 */
    private String orderType;
    /** 描述 */
    private String description;
    /** 状态标识 */
    private String status;
    /** 唯一标识，主键 */
    private String creatorId;
    /** 唯一标识，主键 */
    private String handlerId;
    /** 名称 */
    private String handlerName;
    /** 手机号 */
    private String handlerPhone;
    /** 记录创建时间 */
    private String createTime;
    /** 字段含义待补充 */
    private String completeTime;
    /** 记录最后更新时间 */
    private String updateTime;
    /** 唯一标识，主键 */
    private String serviceRequestId;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param orderId 唯一标识，主键
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取关联老人用户 ID。
     *
     * @return 关联老人用户 ID
     */
    public String getElderId() {
        return elderId;
    }

    /**
     * 设置关联老人用户 ID。
     *
     * @param elderId 关联老人用户 ID
     */
    public void setElderId(String elderId) {
        this.elderId = elderId;
    }

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getElderName() {
        return elderName;
    }

    /**
     * 设置名称。
     *
     * @param elderName 名称
     */
    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    /**
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getOrderType() {
        return orderType;
    }

    /**
     * 设置类型标识。
     *
     * @param orderType 类型标识
     */
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    /**
     * 获取描述。
     *
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述。
     *
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取状态标识。
     *
     * @return 状态标识
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态标识。
     *
     * @param status 状态标识
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getCreatorId() {
        return creatorId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param creatorId 唯一标识，主键
     */
    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getHandlerId() {
        return handlerId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param handlerId 唯一标识，主键
     */
    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getHandlerName() {
        return handlerName;
    }

    /**
     * 设置名称。
     *
     * @param handlerName 名称
     */
    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    /**
     * 获取手机号。
     *
     * @return 手机号
     */
    public String getHandlerPhone() {
        return handlerPhone;
    }

    /**
     * 设置手机号。
     *
     * @param handlerPhone 手机号
     */
    public void setHandlerPhone(String handlerPhone) {
        this.handlerPhone = handlerPhone;
    }

    /**
     * 获取记录创建时间。
     *
     * @return 记录创建时间
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * 设置记录创建时间。
     *
     * @param createTime 记录创建时间
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getCompleteTime() {
        return completeTime;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param completeTime 字段含义待补充
     */
    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    /**
     * 获取记录最后更新时间。
     *
     * @return 记录最后更新时间
     */
    public String getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置记录最后更新时间。
     *
     * @param updateTime 记录最后更新时间
     */
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getServiceRequestId() {
        return serviceRequestId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param serviceRequestId 唯一标识，主键
     */
    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }
}
