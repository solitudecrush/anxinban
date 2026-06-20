package com.anxinban.dto;


/**
 * MonitorRequest 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

public class MonitorRequestDto {
    /** 唯一标识，主键 */
    private String requestId;
    /** 关联老人用户 ID */
    private String elderId;
    /** 名称 */
    private String elderName;
    /** 关联工作人员 ID */
    private String staffId;
    /** 名称 */
    private String staffName;
    /** 手机号 */
    private String staffPhone;
    /** 字段含义待补充 */
    private String reason;
    /** 状态标识 */
    private String status;
    /** 字段含义待补充 */
    private String approvedAt;
    /** 字段含义待补充 */
    private String expiredAt;
    /** 记录创建时间 */
    private String createTime;
    /** 记录最后更新时间 */
    private String updatedAt;

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
     * 获取关联工作人员 ID。
     *
     * @return 关联工作人员 ID
     */
    public String getStaffId() {
        return staffId;
    }

    /**
     * 设置关联工作人员 ID。
     *
     * @param staffId 关联工作人员 ID
     */
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getStaffName() {
        return staffName;
    }

    /**
     * 设置名称。
     *
     * @param staffName 名称
     */
    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    /**
     * 获取手机号。
     *
     * @return 手机号
     */
    public String getStaffPhone() {
        return staffPhone;
    }

    /**
     * 设置手机号。
     *
     * @param staffPhone 手机号
     */
    public void setStaffPhone(String staffPhone) {
        this.staffPhone = staffPhone;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param reason 字段含义待补充
     */
    public void setReason(String reason) {
        this.reason = reason;
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
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getApprovedAt() {
        return approvedAt;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param approvedAt 字段含义待补充
     */
    public void setApprovedAt(String approvedAt) {
        this.approvedAt = approvedAt;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getExpiredAt() {
        return expiredAt;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param expiredAt 字段含义待补充
     */
    public void setExpiredAt(String expiredAt) {
        this.expiredAt = expiredAt;
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
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置记录最后更新时间。
     *
     * @param updatedAt 记录最后更新时间
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
