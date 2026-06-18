package com.anxinban.dto;


/**
 * ServiceRequest 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

public class ServiceRequestDto {
    /** 唯一标识，主键 */
    private String requestId;
    /** 关联家属用户 ID */
    private String familyId;
    /** 名称 */
    private String familyName;
    /** 关联老人用户 ID */
    private String elderId;
    /** 名称 */
    private String elderName;
    /** 类型标识 */
    private String requestType;
    /** 内容 */
    private String content;
    /** 状态标识 */
    private String status;
    /** 唯一标识，主键 */
    private String relatedOrderId;
    /** 字段含义待补充 */
    private String rejectReason;
    /** 记录创建时间 */
    private String createTime;
    /** 记录最后更新时间 */
    private String updateTime;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param requestId 唯一标识，主键
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * 获取关联家属用户 ID。
     *
     * @return 关联家属用户 ID
     */
    public String getFamilyId() {
        return familyId;
    }

    /**
     * 设置关联家属用户 ID。
     *
     * @param familyId 关联家属用户 ID
     */
    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * 设置名称。
     *
     * @param familyName 名称
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
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
    public String getRequestType() {
        return requestType;
    }

    /**
     * 设置类型标识。
     *
     * @param requestType 类型标识
     */
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    /**
     * 获取内容。
     *
     * @return 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容。
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
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
    public String getRelatedOrderId() {
        return relatedOrderId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param relatedOrderId 唯一标识，主键
     */
    public void setRelatedOrderId(String relatedOrderId) {
        this.relatedOrderId = relatedOrderId;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getRejectReason() {
        return rejectReason;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param rejectReason 字段含义待补充
     */
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
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
}
