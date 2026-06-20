package com.anxinban.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * AI 分析记录实体 — 保存每次 AI 健康分析的完整结果。
 *
 * <p>用于家属 APP 查看 AI 健康建议、社区大屏展示风险原因、
 * 比赛演示证明 AI 分析结果已入库等场景。</p>
 *
 * <p>对应数据库表：ai_analysis_record</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "ai_analysis_record")
public class AiAnalysisRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 业务唯一标识 */
    @Column(name = "record_id", nullable = false, unique = true, length = 64)
    private String recordId;

    /** 关联老人用户 ID */
    @Column(name = "elder_id", nullable = false, length = 64)
    private String elderId;

    /** 分析范围：elder / community */
    @Column(name = "scope", length = 20)
    private String scope;

    /** 风险等级：高风险 / 中风险 / 低风险 / 正常 */
    @Column(name = "risk_level", length = 32)
    private String riskLevel;

    /** 风险原因描述（摘要，不超过 500 字符） */
    @Column(name = "risk_reason", length = 500)
    private String riskReason;

    /** AI 给出的综合建议 */
    @Column(name = "suggestion", length = 1000)
    private String suggestion;

    /** 面向老人的回复话术 */
    @Column(name = "elder_reply", length = 500)
    private String elderReply;

    /** 家属通知内容 */
    @Column(name = "family_notice", length = 1000)
    private String familyNotice;

    /** 社区处理建议 */
    @Column(name = "community_suggestion", length = 1000)
    private String communitySuggestion;

    /** 是否需要触发告警 */
    @Column(name = "need_alarm")
    private Boolean needAlarm;

    /** 是否需要生成工单 */
    @Column(name = "need_work_order")
    private Boolean needWorkOrder;

    /** 建议的工单类型（如"紧急巡检"） */
    @Column(name = "work_order_type", length = 64)
    private String workOrderType;

    /** 分析来源：python_ai_service / java_rule_fallback / java_fallback */
    @Column(name = "source", length = 64)
    private String source;

    /** AI 模型名称（如 deepseek-chat），仅在 python_ai_service 时有值 */
    @Column(name = "model", length = 64)
    private String model;

    /** 本次分析生成的告警编号 */
    @Column(name = "generated_alarm_id", length = 64)
    private String generatedAlarmId;

    /** 分析时间 */
    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    /** 记录创建时间 */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ==================== Getters and Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getElderId() { return elderId; }
    public void setElderId(String elderId) { this.elderId = elderId; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getRiskReason() { return riskReason; }
    public void setRiskReason(String riskReason) { this.riskReason = riskReason; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

    public String getElderReply() { return elderReply; }
    public void setElderReply(String elderReply) { this.elderReply = elderReply; }

    public String getFamilyNotice() { return familyNotice; }
    public void setFamilyNotice(String familyNotice) { this.familyNotice = familyNotice; }

    public String getCommunitySuggestion() { return communitySuggestion; }
    public void setCommunitySuggestion(String communitySuggestion) { this.communitySuggestion = communitySuggestion; }

    public Boolean getNeedAlarm() { return needAlarm; }
    public void setNeedAlarm(Boolean needAlarm) { this.needAlarm = needAlarm; }

    public Boolean getNeedWorkOrder() { return needWorkOrder; }
    public void setNeedWorkOrder(Boolean needWorkOrder) { this.needWorkOrder = needWorkOrder; }

    public String getWorkOrderType() { return workOrderType; }
    public void setWorkOrderType(String workOrderType) { this.workOrderType = workOrderType; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getGeneratedAlarmId() { return generatedAlarmId; }
    public void setGeneratedAlarmId(String generatedAlarmId) { this.generatedAlarmId = generatedAlarmId; }

    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
