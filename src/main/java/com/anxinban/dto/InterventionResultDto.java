package com.anxinban.dto;


/**
 * InterventionResult 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

public class InterventionResultDto {
    /** 唯一标识，主键 */
    private String agentId;
    /** 唯一标识，主键 */
    private String interventionId;
    /** 状态标识 */
    private String status;
    /** 结果 */
    private String result;
    /** 时间戳 */
    private String timestamp;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getAgentId() {
        return agentId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param agentId 唯一标识，主键
     */
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

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
     * 获取时间戳。
     *
     * @return 时间戳
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * 设置时间戳。
     *
     * @param timestamp 时间戳
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
