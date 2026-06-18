package com.anxinban.dto;


/**
 * Intervention 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

public class InterventionDto {
    /** 唯一标识，主键 */
    private String interventionId;
    /** 关联老人用户 ID */
    private String elderId;
    /** 字段含义待补充 */
    private String triggeredBy;
    /** 类型标识 */
    private String type;
    /** 字段含义待补充 */
    private String priority;
    /** 状态标识 */
    private String status;
    /** 记录创建时间 */
    private String createTime;
    /** 记录最后更新时间 */
    private String updateTime;
    /** 字段含义待补充 */
    private String operator;
    /** 备注 */
    private String remark;
    /** 结果 */
    private String result;
    /** 字段含义待补充 */
    private String completeTime;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getInterventionId() {
        return interventionId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param interventionId 唯一标识，主键
     */
    public void setInterventionId(String interventionId) {
        this.interventionId = interventionId;
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
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getTriggeredBy() {
        return triggeredBy;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param triggeredBy 字段含义待补充
     */
    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    /**
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getType() {
        return type;
    }

    /**
     * 设置类型标识。
     *
     * @param type 类型标识
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getPriority() {
        return priority;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param priority 字段含义待补充
     */
    public void setPriority(String priority) {
        this.priority = priority;
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

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getOperator() {
        return operator;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param operator 字段含义待补充
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * 获取备注。
     *
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注。
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取结果。
     *
     * @return 结果
     */
    public String getResult() {
        return result;
    }

    /**
     * 设置结果。
     *
     * @param result 结果
     */
    public void setResult(String result) {
        this.result = result;
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
}
