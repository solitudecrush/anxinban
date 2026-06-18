package com.anxinban.dto;


/**
 * HealthAnalysis 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

public class HealthAnalysisDto {
    /** 关联老人用户 ID */
    private String elderId;
    /** 字段含义待补充 */
    private String period;
    /** 字段含义待补充 */
    private String summary;
    /** 字段含义待补充 */
    private String suggestion;

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
    public String getPeriod() {
        return period;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param period 字段含义待补充
     */
    public void setPeriod(String period) {
        this.period = period;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getSummary() {
        return summary;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param summary 字段含义待补充
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getSuggestion() {
        return suggestion;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param suggestion 字段含义待补充
     */
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
