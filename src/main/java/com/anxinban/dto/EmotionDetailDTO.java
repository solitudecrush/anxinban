package com.anxinban.dto;

import java.util.List;
import java.util.Map;

/**
 * 情绪分析详情 DTO — 为前端"图表"页面情绪分析板块的"查看详情"提供完整数据。
 *
 * <p>包含：情绪标签、评分、分析文本、情绪分布、关键事件、建议提醒等。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
public class EmotionDetailDTO {

    /** 时间维度：day / week / month */
    private String dimension;

    /** 时间范围，如 "2026-07-16 ~ 2026-07-22" */
    private String dateRange;

    /** 情绪标签，如 "平稳" 或 "焦虑" */
    private String emotionLabel;

    /** 情绪评分（1-10 分制） */
    private Double emotionScore;

    /** AI 分析文本描述 */
    private String analysisText;

    /** 情绪占比分布，如 {"开心": 10, "平静": 20, "低落": 30, "焦虑": 40} */
    private Map<String, Integer> emotionDistribution;

    /** 情绪颜色映射，与 HealthDashboardService 保持一致 */
    private Map<String, String> emotionColors;

    /** 关键事件/触发因素列表 */
    private List<String> keyEvents;

    /** 建议/提醒 */
    private String suggestions;

    // ==================== Getters and Setters ====================

    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }

    public String getDateRange() { return dateRange; }
    public void setDateRange(String dateRange) { this.dateRange = dateRange; }

    public String getEmotionLabel() { return emotionLabel; }
    public void setEmotionLabel(String emotionLabel) { this.emotionLabel = emotionLabel; }

    public Double getEmotionScore() { return emotionScore; }
    public void setEmotionScore(Double emotionScore) { this.emotionScore = emotionScore; }

    public String getAnalysisText() { return analysisText; }
    public void setAnalysisText(String analysisText) { this.analysisText = analysisText; }

    public Map<String, Integer> getEmotionDistribution() { return emotionDistribution; }
    public void setEmotionDistribution(Map<String, Integer> emotionDistribution) { this.emotionDistribution = emotionDistribution; }

    public Map<String, String> getEmotionColors() { return emotionColors; }
    public void setEmotionColors(Map<String, String> emotionColors) { this.emotionColors = emotionColors; }

    public List<String> getKeyEvents() { return keyEvents; }
    public void setKeyEvents(List<String> keyEvents) { this.keyEvents = keyEvents; }

    public String getSuggestions() { return suggestions; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }
}
