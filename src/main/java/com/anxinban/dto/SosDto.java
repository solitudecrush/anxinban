package com.anxinban.dto;


/**
 * Sos 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

public class SosDto {
    /** 唯一标识，主键 */
    private String sosId;
    /** 关联老人用户 ID */
    private String elderId;
    /** 触发时间 */
    private String triggerTime;
    /** 状态标识 */
    private String status;
    /** 位置信息 */
    private String location;
    /** 唯一标识，主键 */
    private String handlerId;
    /** 字段含义待补充 */
    private String handledTime;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getSosId() {
        return sosId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param sosId 唯一标识，主键
     */
    public void setSosId(String sosId) {
        this.sosId = sosId;
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
     * 获取触发时间。
     *
     * @return 触发时间
     */
    public String getTriggerTime() {
        return triggerTime;
    }

    /**
     * 设置触发时间。
     *
     * @param triggerTime 触发时间
     */
    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
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
     * 获取位置信息。
     *
     * @return 位置信息
     */
    public String getLocation() {
        return location;
    }

    /**
     * 设置位置信息。
     *
     * @param location 位置信息
     */
    public void setLocation(String location) {
        this.location = location;
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
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getHandledTime() {
        return handledTime;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param handledTime 字段含义待补充
     */
    public void setHandledTime(String handledTime) {
        this.handledTime = handledTime;
    }
}
