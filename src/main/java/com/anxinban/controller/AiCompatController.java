package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.PageResult;
import com.anxinban.entity.AiAnalysisRecord;
import com.anxinban.entity.ElderUser;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.service.AiAnalysisRecordService;
import com.anxinban.service.AiForwardService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 比赛兼容层 - AI 模块接口。
 *
 * <p>提供与队长 AI Mock Cloud 格式兼容的 5 个 AI 接口。
 * 优先调用外部 Python AI 服务（通过 {@link AiForwardService}），
 * 如果 AI 服务不可用则自动回退到 Java 规则引擎/Mock 逻辑。</p>
 *
 * <p>接口列表：</p>
 * <ul>
 *   <li>POST /api/ai/health-analysis — AI 健康风险分析</li>
 *   <li>POST /api/ai/chat/quick — 本地即时陪伴回复</li>
 *   <li>POST /api/ai/chat/deep — 云端深度陪伴回复</li>
 *   <li>POST /api/ai/rag-query — RAG 养老知识问答</li>
 *   <li>POST /api/ai/vision-analysis — 视觉分析</li>
 *   <li>GET  /api/ai/service-status — AI 服务状态</li>
 * </ul>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/ai")
public class AiCompatController {

    private static final Logger log = LoggerFactory.getLogger(AiCompatController.class);

    @Autowired
    private ElderUserRepository elderUserRepository;

    @Autowired
    private AiForwardService aiForwardService;

    @Autowired
    private AiAnalysisRecordService aiAnalysisRecordService;

    // ==================== 0. AI 服务状态 ====================

    /**
     * AI 服务状态接口。
     *
     * <p>返回当前 AI 转发的配置状态以及外部 Python AI 服务的可达性。</p>
     */
    @GetMapping("/service-status")
    public ApiResponse<Map<String, Object>> serviceStatus() {
        Map<String, Object> data = new HashMap<>();
        data.put("ai_service_base_url", aiForwardService.getAiServiceBaseUrl());
        data.put("forward_enabled", aiForwardService.isForwardEnabled());

        boolean reachable = aiForwardService.isPythonAiReachable();
        data.put("python_ai_reachable", reachable);
        data.put("fallback_enabled", true);
        data.put("current_mode", reachable ? "python_ai_service" : "java_fallback");
        data.put("source", reachable ? "python_ai_service" : "java_fallback");

        return ApiResponse.success(data);
    }

    // ==================== 0.1 查询 AI 分析记录 ====================

    /**
     * 查询某个老人最近一次 AI 分析结果。
     *
     * <p>用于家属 APP 查看最近 AI 健康建议、社区大屏展示风险原因。</p>
     *
     * @param elderId 老人唯一标识
     * @return 最近一次 AI 分析记录
     */
    @GetMapping("/latest-analysis/{elder_id}")
    public ApiResponse<Map<String, Object>> latestAnalysis(@PathVariable("elder_id") String elderId) {
        log.info("查询最近 AI 分析: elderId={}", elderId);

        AiAnalysisRecord record = aiAnalysisRecordService.getLatestByElder(elderId);
        if (record == null) {
            return ApiResponse.error(404, "该老人暂无 AI 分析记录");
        }

        Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("elder_id", record.getElderId());
        data.put("risk_level", record.getRiskLevel());
        data.put("risk_reason", record.getRiskReason());
        data.put("suggestion", record.getSuggestion());
        data.put("family_notice", record.getFamilyNotice());
        data.put("community_suggestion", record.getCommunitySuggestion());
        data.put("need_alarm", record.getNeedAlarm());
        data.put("need_work_order", record.getNeedWorkOrder());
        data.put("work_order_type", record.getWorkOrderType());
        data.put("source", record.getSource());
        data.put("created_at", record.getCreatedAt() != null ? record.getCreatedAt().toString() : null);

        return ApiResponse.success(data);
    }

    /**
     * 分页查询 AI 分析历史记录。
     *
     * <p>用于家属 APP 查看历史 AI 建议、比赛演示证明 AI 分析结果已入库。</p>
     *
     * @param elderId  老人唯一标识（必填）
     * @param page     页码，从 1 开始，默认 1
     * @param pageSize 每页大小，默认 10
     * @return 分页列表
     */
    @GetMapping("/analysis-records")
    public ApiResponse<PageResult<Map<String, Object>>> analysisRecords(
            @RequestParam("elder_id") String elderId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        log.info("查询 AI 分析记录列表: elderId={}, page={}, pageSize={}", elderId, page, pageSize);

        org.springframework.data.domain.Page<AiAnalysisRecord> recordPage =
                aiAnalysisRecordService.listByElder(elderId, page, pageSize);

        java.util.List<Map<String, Object>> list = recordPage.getContent().stream()
                .map(r -> {
                    Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("elder_id", r.getElderId());
                    item.put("risk_level", r.getRiskLevel());
                    item.put("risk_reason", r.getRiskReason());
                    item.put("suggestion", r.getSuggestion());
                    item.put("family_notice", r.getFamilyNotice());
                    item.put("community_suggestion", r.getCommunitySuggestion());
                    item.put("need_alarm", r.getNeedAlarm());
                    item.put("need_work_order", r.getNeedWorkOrder());
                    item.put("work_order_type", r.getWorkOrderType());
                    item.put("source", r.getSource());
                    item.put("created_at", r.getCreatedAt() != null ? r.getCreatedAt().toString() : null);
                    return item;
                })
                .toList();

        PageResult<Map<String, Object>> result = new PageResult<>(
                list, recordPage.getTotalElements(), page, pageSize);

        return ApiResponse.success(result);
    }

    // ==================== 1. 健康风险分析 ====================

    /**
     * AI 健康风险分析接口。
     *
     * <p>优先转发到 Python AI 服务，不可用时使用 Java 规则引擎。</p>
     */
    @PostMapping("/health-analysis")
    public ApiResponse<Map<String, Object>> healthAnalysis(@RequestBody HealthAnalysisRequest request) {
        log.info("AI健康分析请求: elderId={}", request.getElderId());

        // 1) 尝试调用外部 Python AI 服务
        Map<String, Object> pythonResult = aiForwardService.forward("/api/ai/health-analysis", request);
        if (pythonResult != null) {
            pythonResult.put("source", "python_ai_service");
            return ApiResponse.success(pythonResult);
        }

        // 2) Java 规则引擎 fallback
        log.info("[java_fallback] 使用 Java 规则引擎: health-analysis");
        return ApiResponse.success(doHealthAnalysisFallback(request));
    }

    private Map<String, Object> doHealthAnalysisFallback(HealthAnalysisRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("elder_id", request.getElderId());

        // 查找老人信息
        String elderName = "";
        if (request.getElderId() != null) {
            ElderUser elder = elderUserRepository.findByElderId(request.getElderId());
            if (elder != null) elderName = elder.getName();
        }
        data.put("elder_name", elderName);

        // 规则分析
        List<String> risks = new ArrayList<>();
        List<String> riskReasons = new ArrayList<>();
        String riskLevel = "低风险";
        boolean hasHighRisk = false;

        if (request.getRecentHealth() != null) {
            Map<String, Object> health = request.getRecentHealth();
            Integer hr = health.containsKey("heart_rate") ? ((Number) health.get("heart_rate")).intValue() : null;
            Integer spo2 = health.containsKey("spo2") ? ((Number) health.get("spo2")).intValue() : null;
            Double temp = health.containsKey("temperature") ? ((Number) health.get("temperature")).doubleValue() : null;

            if (spo2 != null && spo2 < 92) {
                String reason = "血氧偏低(" + spo2 + "%)";
                risks.add(reason + "，存在呼吸系统风险");
                riskReasons.add(reason);
                riskLevel = "高风险";
                hasHighRisk = true;
            }
            if (hr != null && (hr > 110 || hr < 50)) {
                String reason = "心率异常(" + hr + "bpm)";
                risks.add(reason + "，需关注心血管健康");
                riskReasons.add(reason);
                if (!hasHighRisk) riskLevel = "中风险";
                if (hr > 130 || hr < 40) hasHighRisk = true;
            }
            if (temp != null && (temp > 38.0 || temp < 35.0)) {
                String reason = "体温异常(" + temp + "℃)";
                risks.add(reason + "，可能存在感染");
                riskReasons.add(reason);
                if (!hasHighRisk) riskLevel = "中风险";
            }

            // 两个及以上异常 → 高风险
            if (riskReasons.size() >= 2) {
                riskLevel = "高风险";
                hasHighRisk = true;
            }

            data.put("heart_rate", hr);
            data.put("spo2", spo2);
            data.put("temperature", temp);
        }

        if (risks.isEmpty()) {
            risks.add("各项指标均在正常范围内，继续保持良好的生活习惯");
            riskReasons.add("各项指标正常");
        }

        data.put("risk_level", riskLevel);
        data.put("risk_reason", String.join("、", riskReasons));
        data.put("risk_factors", risks);
        data.put("summary", String.join("；", risks));

        // 补齐队长验收脚本需要的字段
        if (hasHighRisk) {
            data.put("suggestion", "建议立即联系家属并安排社区人员确认老人状态");
            data.put("elder_reply", "请您先坐下休息，我已经帮您通知家属。");
            data.put("family_notice", "老人当前" + String.join("、", riskReasons) + "，建议尽快电话确认。");
            data.put("community_suggestion", "建议生成紧急巡检工单，安排社区人员上门确认。");
            data.put("need_alarm", true);
            data.put("need_work_order", true);
            data.put("work_order_type", "紧急巡检");
        } else {
            data.put("suggestion", generateRecommendation(riskLevel));
            data.put("elder_reply", "您的健康指标整体平稳，请继续保持。");
            data.put("family_notice", "");
            data.put("community_suggestion", "");
            data.put("need_alarm", false);
            data.put("need_work_order", false);
            data.put("work_order_type", "");
        }

        data.put("recommendation", generateRecommendation(riskLevel));
        data.put("need_follow_up", hasHighRisk);
        data.put("source", "java_rule_fallback");

        return data;
    }

    // ==================== 2. 本地即时陪伴回复 ====================

    /**
     * 本地即时陪伴回复接口（quick）。
     *
     * <p>优先转发到 Python AI 服务，不可用时使用 Java 本地规则。</p>
     */
    @PostMapping("/chat/quick")
    public ApiResponse<Map<String, Object>> chatQuick(@RequestBody ChatRequest request) {
        log.info("AI快速对话: elderId={}, message={}", request.getElderId(), request.getMessage());

        // 1) 尝试调用外部 Python AI 服务
        Map<String, Object> pythonResult = aiForwardService.forward("/api/ai/chat/quick", request);
        if (pythonResult != null) {
            pythonResult.put("source", "python_ai_service");
            return ApiResponse.success(pythonResult);
        }

        // 2) Java 本地规则 fallback
        log.info("[java_fallback] 使用 Java 本地规则: chat/quick");
        Map<String, Object> data = new HashMap<>();
        data.put("elder_id", request.getElderId());
        data.put("intent", detectIntent(request.getMessage()));
        data.put("quick_reply", generateQuickReply(request.getMessage(), request.getElderId()));
        data.put("need_alarm", isEmergency(request.getMessage()));
        data.put("need_family_notify", isEmergency(request.getMessage()));
        data.put("suggested_action", suggestAction(request.getMessage()));
        data.put("source", "java_local_rule");

        return ApiResponse.success(data);
    }

    // ==================== 3. 云端深度陪伴回复 ====================

    /**
     * 云端深度陪伴回复接口（deep）。
     *
     * <p>优先转发到 Python AI 服务，不可用时使用 Java mock deep。</p>
     */
    @PostMapping("/chat/deep")
    public ApiResponse<Map<String, Object>> chatDeep(@RequestBody ChatRequest request) {
        log.info("AI深度对话: elderId={}, message={}", request.getElderId(), request.getMessage());

        // 1) 尝试调用外部 Python AI 服务
        Map<String, Object> pythonResult = aiForwardService.forward("/api/ai/chat/deep", request);
        if (pythonResult != null) {
            pythonResult.put("source", "python_ai_service");
            return ApiResponse.success(pythonResult);
        }

        // 2) Java mock deep fallback
        log.info("[java_fallback] 使用 Java mock deep: chat/deep");
        Map<String, Object> data = new HashMap<>();
        data.put("elder_id", request.getElderId());
        data.put("intent", detectIntent(request.getMessage()));
        data.put("deep_reply", generateDeepReply(request.getMessage(), request.getElderId()));
        data.put("emotion_analysis", analyzeEmotion(request.getMessage()));
        data.put("risk_assessment", isEmergency(request.getMessage()) ? "高风险" : "低风险");
        data.put("knowledge_refs", List.of("中国老年健康管理指南", "社区养老服务规范"));
        data.put("source", "java_mock_deep");

        return ApiResponse.success(data);
    }

    // ==================== 4. RAG 养老知识问答 ====================

    /**
     * RAG 养老知识问答接口。
     *
     * <p>优先转发到 Python AI 服务，不可用时使用 Java mock rag。</p>
     */
    @PostMapping("/rag-query")
    public ApiResponse<Map<String, Object>> ragQuery(@RequestBody RagQueryRequest request) {
        log.info("RAG查询: query={}", request.getQuery());

        // 1) 尝试调用外部 Python AI 服务
        Map<String, Object> pythonResult = aiForwardService.forward("/api/ai/rag-query", request);
        if (pythonResult != null) {
            pythonResult.put("source", "python_ai_service");
            return ApiResponse.success(pythonResult);
        }

        // 2) Java mock rag fallback
        log.info("[java_fallback] 使用 Java mock rag: rag-query");
        int topK = request.getTopK() != null ? request.getTopK() : 3;

        Map<String, Object> data = new HashMap<>();
        data.put("query", request.getQuery());
        data.put("answer", generateRagAnswer(request.getQuery()));
        data.put("confidence", 0.85);
        data.put("top_k", topK);
        data.put("retrieved_knowledge", List.of(
                Map.of("title", "老年人健康管理手册", "content", "老年人应定期监测血压、血糖、心率等指标。预防跌倒需保持地面干燥、安装扶手、夜间保持照明。", "score", 0.92),
                Map.of("title", "社区养老服务指南", "content", "社区应为高龄老人提供定期上门巡访服务，紧急情况应在10分钟内响应。", "score", 0.87),
                Map.of("title", "老年康复护理手册", "content", "适当运动有助于增强肌肉力量和平衡能力，减少跌倒风险。推荐太极拳、散步等低强度运动。", "score", 0.81)
        ));
        data.put("sources", List.of(
                Map.of("title", "老年人健康管理手册", "page", "第3章 日常监测"),
                Map.of("title", "社区养老服务指南", "page", "第5节 紧急情况处理")
        ));
        data.put("source", "java_mock_rag");

        return ApiResponse.success(data);
    }

    // ==================== 5. 视觉分析 ====================

    /**
     * 视觉分析接口。
     *
     * <p>优先转发到 Python AI 服务，不可用时使用 Java mock vision。</p>
     */
    @PostMapping("/vision-analysis")
    public ApiResponse<Map<String, Object>> visionAnalysis(@RequestBody VisionAnalysisRequest request) {
        log.info("视觉分析请求: elderId={}", request.getElderId());

        // 1) 尝试调用外部 Python AI 服务
        Map<String, Object> pythonResult = aiForwardService.forward("/api/ai/vision-analysis", request);
        if (pythonResult != null) {
            pythonResult.put("source", "python_ai_service");
            return ApiResponse.success(pythonResult);
        }

        // 2) Java mock vision fallback
        log.info("[java_fallback] 使用 Java mock vision: vision-analysis");
        Map<String, Object> data = new HashMap<>();
        data.put("elder_id", request.getElderId());
        data.put("analysis_type", request.getAnalysisType() != null ? request.getAnalysisType() : "fall_detection");
        data.put("vision_result", "未检测到异常行为");
        data.put("confidence", 0.92);
        data.put("details", Map.of(
                "person_detected", true,
                "fall_detected", false,
                "motion_level", "normal",
                "timestamp", new java.util.Date().toString()
        ));
        data.put("need_alarm", false);
        data.put("source", "java_mock_vision");

        return ApiResponse.success(data);
    }

    // ==================== 辅助方法（Java fallback 逻辑） ====================

    private String detectIntent(String message) {
        if (message == null) return "chat";
        if (message.contains("不舒服") || message.contains("疼") || message.contains("喘") || message.contains("晕")) return "紧急求助";
        if (message.contains("血压") || message.contains("血糖") || message.contains("心率") || message.contains("健康")) return "健康咨询";
        if (message.contains("开灯") || message.contains("关灯") || message.contains("窗帘")) return "家居控制";
        if (message.contains("天气") || message.contains("几号") || message.contains("星期")) return "生活查询";
        if (message.contains("音乐") || message.contains("播放") || message.contains("歌")) return "娱乐陪伴";
        return "日常聊天";
    }

    private boolean isEmergency(String message) {
        if (message == null) return false;
        return message.contains("救命") || message.contains("不舒服") || message.contains("喘不上气")
                || message.contains("摔倒") || message.contains("不行了") || message.contains("急救");
    }

    private String generateQuickReply(String message, String elderId) {
        if (message == null) return "我在呢，您说吧。";
        if (message.contains("不舒服") || message.contains("喘")) return "我已经为您通知家属，请您先坐下休息，不要着急。";
        if (message.contains("疼")) return "您哪里疼？我马上帮您联系社区医生。";
        if (message.contains("血压")) return "您最近的血压记录在正常范围内，记得按时服药哦。";
        if (message.contains("天气")) return "今天天气不错，适合出门散步，记得带好钥匙和手机。";
        if (message.contains("吃药")) return "记得按时吃药哦，我已经帮您设了提醒。";
        return "好的，我在听着呢。有什么需要就告诉我。";
    }

    private String suggestAction(String message) {
        if (isEmergency(message)) return "立即通知家属和社区人员";
        return "无需特殊处理";
    }

    private String generateDeepReply(String message, String elderId) {
        if (message == null) return "您好，我是云端智能陪伴助手，很高兴为您服务。";
        if (isEmergency(message)) {
            return "检测到紧急情况！我已同步通知了您的家属和社区管理人员。"
                    + "请保持冷静，社区工作人员预计在5-10分钟内到达。"
                    + "在此期间请尽量保持静止，我会持续关注您的状态。";
        }
        return "根据您的问题，我为您进行了综合分析。"
                + "建议您保持规律作息，适当运动，注意饮食均衡。"
                + "如有需要，可以随时呼叫我。";
    }

    private String analyzeEmotion(String message) {
        if (message == null) return "平静";
        if (message.contains("不开心") || message.contains("难过") || message.contains("孤独")) return "低落";
        if (message.contains("烦") || message.contains("生气")) return "焦虑";
        return "平静";
    }

    private String generateRecommendation(String riskLevel) {
        switch (riskLevel) {
            case "高风险": return "建议立即通知家属和社区医护人员，安排上门检查";
            case "中风险": return "建议增加监测频率，密切关注指标变化趋势";
            default: return "建议继续保持目前的生活和监测习惯";
        }
    }

    private String generateRagAnswer(String query) {
        if (query == null) return "请提出您的问题。";
        if (query.contains("血压")) return "老年人正常血压范围：收缩压90-140mmHg，舒张压60-90mmHg。建议每日早晚各测量一次，保持记录。";
        if (query.contains("跌倒")) return "预防老人跌倒的措施包括：保持地面干燥、安装防滑垫、夜间保持照明、穿防滑鞋、定期进行平衡训练。";
        if (query.contains("糖尿病")) return "糖尿病老年人应注意：控制饮食糖分摄入、定期监测血糖、按时服药、适当运动、注意足部护理。";
        if (query.contains("痴呆") || query.contains("认知")) return "认知障碍早期表现包括记忆力减退、定向力下降、语言表达困难等。建议进行专业评估，并进行认知训练。";
        return "根据养老健康知识库，建议您咨询社区医生获取个性化的健康指导。";
    }

    // ==================== 请求/响应 DTO ====================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HealthAnalysisRequest {
        @JsonProperty("elder_id")
        private String elderId;

        @JsonProperty("recent_health")
        private Map<String, Object> recentHealth;

        public String getElderId() { return elderId; }
        public void setElderId(String elderId) { this.elderId = elderId; }

        public Map<String, Object> getRecentHealth() { return recentHealth; }
        public void setRecentHealth(Map<String, Object> recentHealth) { this.recentHealth = recentHealth; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatRequest {
        @JsonProperty("elder_id")
        private String elderId;

        @JsonProperty("message")
        private String message;

        @JsonProperty("recent_health")
        private Map<String, Object> recentHealth;

        public String getElderId() { return elderId; }
        public void setElderId(String elderId) { this.elderId = elderId; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Map<String, Object> getRecentHealth() { return recentHealth; }
        public void setRecentHealth(Map<String, Object> recentHealth) { this.recentHealth = recentHealth; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RagQueryRequest {
        @JsonProperty("query")
        private String query;

        @JsonProperty("elder_id")
        private String elderId;

        @JsonProperty("top_k")
        private Integer topK;

        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }

        public String getElderId() { return elderId; }
        public void setElderId(String elderId) { this.elderId = elderId; }

        public Integer getTopK() { return topK != null ? topK : 3; }
        public void setTopK(Integer topK) { this.topK = topK; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VisionAnalysisRequest {
        @JsonProperty("elder_id")
        private String elderId;

        @JsonProperty("image_url")
        private String imageUrl;

        @JsonProperty("analysis_type")
        private String analysisType;

        public String getElderId() { return elderId; }
        public void setElderId(String elderId) { this.elderId = elderId; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public String getAnalysisType() { return analysisType; }
        public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }
    }
}
