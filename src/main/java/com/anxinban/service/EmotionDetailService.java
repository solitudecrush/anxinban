package com.anxinban.service;

import com.anxinban.dto.EmotionDetailDTO;
import com.anxinban.entity.ChatRecord;
import com.anxinban.mapper.ChatRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 情绪分析详情服务 — 为前端"图表"页面情绪分析板块提供日/周/月维度的
 * 情绪分析结论、情绪分布、关键事件及建议等详情数据。
 *
 * <p>数据来源：chat_records 表（统一数据源，日/周/月三个维度均从此表计算）。</p>
 *
 * <p>情绪标签规范（仅6种）：开心、平静、低落、焦虑、孤单、思念</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class EmotionDetailService {

    private static final Logger log = LoggerFactory.getLogger(EmotionDetailService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter EVENT_DATE_FMT = DateTimeFormatter.ofPattern("M月d日");

    /**
     * 新规范情绪标签集合（仅6种）。
     */
    private static final Set<String> POSITIVE_EMOTIONS = Set.of("开心", "平静");
    private static final Set<String> NEGATIVE_EMOTIONS = Set.of("焦虑", "低落", "孤单", "思念");
    private static final Set<String> ALL_VALID_EMOTIONS;

    static {
        Set<String> all = new HashSet<>();
        all.addAll(POSITIVE_EMOTIONS);
        all.addAll(NEGATIVE_EMOTIONS);
        ALL_VALID_EMOTIONS = Collections.unmodifiableSet(all);
    }

    private final ChatRecordRepository chatRecordRepository;

    @Autowired
    public EmotionDetailService(ChatRecordRepository chatRecordRepository) {
        this.chatRecordRepository = chatRecordRepository;
    }

    /**
     * 获取情绪分析详情。
     *
     * @param elderId   老人 ID（对应 chat_records.user_id）
     * @param dimension 时间维度：day / week / month
     * @return 情绪分析详情 DTO
     */
    public EmotionDetailDTO getEmotionDetail(String elderId, String dimension) {
        EmotionDetailDTO dto = new EmotionDetailDTO();

        // 1) 确定时间范围
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate = today;

        switch (dimension.toLowerCase()) {
            case "day":
            case "daily":
                startDate = today;
                dto.setDimension("day");
                break;
            case "week":
            case "weekly":
                startDate = today.minusDays(6);  // 近7天（含今天）
                dto.setDimension("week");
                break;
            case "month":
            case "monthly":
                startDate = today.minusDays(29);  // 近30天（含今天）
                dto.setDimension("month");
                break;
            default:
                startDate = today.minusDays(6);
                dto.setDimension("week");
        }

        dto.setDateRange(startDate.format(DATE_FMT) + " ~ " + endDate.format(DATE_FMT));

        // 2) 从 chat_records 表查询时间范围内的陪伴对话记录（统一数据源）
        List<ChatRecord> records = chatRecordRepository
                .findByUserIdAndDateBetweenOrderByDateAsc(elderId, startDate, endDate);

        // 3) 情绪标签分布统计（从 chat_records 表实时聚合计算）
        Map<String, Long> rawStats = records.stream()
                .filter(r -> r.getEmotion() != null && !r.getEmotion().isEmpty()
                        && ALL_VALID_EMOTIONS.contains(r.getEmotion()))
                .collect(Collectors.groupingBy(ChatRecord::getEmotion, Collectors.counting()));

        Map<String, Integer> distribution = new LinkedHashMap<>();

        // 按固定顺序排列情绪标签
        String[] orderedEmotions = {"开心", "平静", "低落", "焦虑", "孤单", "思念"};
        if (!rawStats.isEmpty()) {
            long total = rawStats.values().stream().mapToLong(Long::longValue).sum();
            for (String emotion : orderedEmotions) {
                Long count = rawStats.getOrDefault(emotion, 0L);
                if (count > 0) {
                    int percent = (int) Math.round((double) count / total * 100);
                    distribution.put(emotion, percent);
                }
            }
        } else {
            // 无数据时填充空分布（让前端自行处理）
            log.warn("chat_records 表中无数据: elderId={}, startDate={}, endDate={}",
                    elderId, startDate, endDate);
        }
        dto.setEmotionDistribution(distribution);

        // 3.5) 情绪颜色映射（与 HealthDashboardService 保持一致）
        dto.setEmotionColors(buildEmotionColors());

        // 4) 根据实际分布数据确定情绪标签和评分
        setEmotionByDistribution(dto, dimension, distribution);

        // 5) 生成分析文本
        dto.setAnalysisText(generateAnalysisText(dto.getEmotionLabel(), dto.getDimension(),
                startDate, endDate, distribution));

        // 6) 提取关键事件（按维度过滤日期范围）
        dto.setKeyEvents(extractKeyEvents(records, startDate, endDate, dimension));

        // 7) 生成建议
        dto.setSuggestions(generateSuggestions(dto.getEmotionLabel()));

        log.info("情绪详情计算完成: dimension={}, emotionLabel={}, emotionScore={}, distribution={}",
                dto.getDimension(), dto.getEmotionLabel(), dto.getEmotionScore(), distribution);

        return dto;
    }

    /**
     * 根据实际情绪分布数据计算情绪标签和评分。
     *
     * <p>核心原则：emotionLabel 和 emotionDistribution 必须逻辑一致。
     * <ul>
     *   <li>正向情绪（开心+平静）占比 > 60% → "平稳"，评分 ≥ 7.0</li>
     *   <li>负向情绪（焦虑+低落+孤单+思念）占比 > 50% → "焦虑"，评分 ≤ 4.0</li>
     *   <li>其他情况 → 综合评分判断</li>
     * </ul>
     *
     * @param dto          情绪详情 DTO
     * @param dimension    时间维度
     * @param distribution 情绪分布（情绪名 → 百分比）
     */
    private void setEmotionByDistribution(EmotionDetailDTO dto, String dimension,
                                          Map<String, Integer> distribution) {
        if (distribution.isEmpty()) {
            // 无数据时的默认值
            dto.setEmotionLabel("平稳");
            dto.setEmotionScore(7.5);
            return;
        }

        long total = distribution.values().stream().mapToLong(Integer::longValue).sum();
        if (total == 0) {
            dto.setEmotionLabel("平稳");
            dto.setEmotionScore(7.5);
            return;
        }

        // 计算正向和负向情绪占比
        long positiveCount = POSITIVE_EMOTIONS.stream()
                .mapToLong(e -> distribution.getOrDefault(e, 0))
                .sum();
        long negativeCount = NEGATIVE_EMOTIONS.stream()
                .mapToLong(e -> distribution.getOrDefault(e, 0))
                .sum();

        double positiveRatio = (double) positiveCount / total;
        double negativeRatio = (double) negativeCount / total;

        // 评分公式：10 - (负向占比 * 10)，范围 [1, 10]，保留1位小数
        double score = 10.0 - (negativeRatio * 10.0);
        score = Math.max(1.0, Math.min(10.0, score));
        score = Math.round(score * 10.0) / 10.0;
        dto.setEmotionScore(score);

        // 根据实际分布确定标签
        if (positiveRatio > 0.6) {
            // 正向情绪占主导 → 平稳
            dto.setEmotionLabel("平稳");
        } else if (negativeRatio > 0.5) {
            // 负向情绪占主导 → 焦虑
            dto.setEmotionLabel("焦虑");
        } else {
            // 中间状态：根据分数判断
            if (score >= 5.0) {
                dto.setEmotionLabel("平稳");
            } else {
                dto.setEmotionLabel("焦虑");
            }
        }

        log.debug("情绪标签计算: positiveRatio={:.2f}, negativeRatio={:.2f}, label={}, score={}",
                positiveRatio, negativeRatio, dto.getEmotionLabel(), dto.getEmotionScore());
    }

    /**
     * 根据情绪结论和维度生成 AI 分析文本。
     */
    private String generateAnalysisText(String emotionLabel, String dimension,
                                         LocalDate startDate, LocalDate endDate,
                                         Map<String, Integer> distribution) {
        String dimText;
        switch (dimension.toLowerCase()) {
            case "day":
            case "daily":
                dimText = "今日";
                break;
            case "week":
            case "weekly":
                dimText = "近7天";
                break;
            case "month":
            case "monthly":
                dimText = "近30天";
                break;
            default:
                dimText = "近期";
        }

        if ("平稳".equals(emotionLabel)) {
            return dimText + "老人情绪整体平稳，无明显波动。" +
                    "各项情绪指标均在正常范围内，老人身心状态良好。" +
                    "建议家属保持现有的沟通频率，定期关注老人状态。";
        } else {
            // 焦虑
            return dimText + "老人情绪呈现焦虑倾向，可能由身体不适或生活变化引起。" +
                    "数据显示焦虑和低落情绪占比较高，需引起家属关注。" +
                    "建议家属增加视频通话频率，密切关注老人睡眠和饮食情况，必要时安排健康检查。";
        }
    }

    /**
     * 从 chat_records 记录中提取关键事件，按维度过滤日期范围。
     *
     * <p>日维度：只返回当天的关键事件；
     * 周维度：返回最近7天的关键事件；
     * 月维度：返回最近30天的关键事件。</p>
     *
     * @param records   时间范围内的全部 chat_records
     * @param startDate 维度起始日期
     * @param endDate   维度结束日期
     * @param dimension 时间维度
     * @return 关键事件列表（最多5条）
     */
    private List<String> extractKeyEvents(List<ChatRecord> records,
                                           LocalDate startDate, LocalDate endDate,
                                           String dimension) {
        List<String> events = new ArrayList<>();

        // 从 chat_records 的 message 字段提取关键事件
        // 仅选取负向情绪（低落、焦虑、孤单、思念）的记录，按日期倒序（最新在前），最多取5条
        // 若时间范围内无负向情绪记录（如日维度当日情绪平稳），则返回空数组
        List<ChatRecord> notableRecords = records.stream()
                .filter(r -> r.getMessage() != null && !r.getMessage().isEmpty())
                .filter(r -> {
                    String emotion = r.getEmotion();
                    return emotion != null && NEGATIVE_EMOTIONS.contains(emotion);
                })
                .sorted(Comparator.comparing(ChatRecord::getDate).reversed())
                .limit(5)
                .collect(Collectors.toList());

        for (ChatRecord r : notableRecords) {
            if (r.getDate() != null) {
                String dateStr = r.getDate().format(EVENT_DATE_FMT);
                String message = r.getMessage();
                if (message != null && message.length() > 50) {
                    message = message.substring(0, 50) + "...";
                }
                events.add(dateStr + "：" + message);
            }
        }

        // 日维度无负向事件时返回空数组，不再从正向记录中回退
        // 周/月维度如有负向事件则已在上方填充，无负向事件也如实返回空

        return events;
    }

    /**
     * 根据情绪标签生成建议。
     */
    private String generateSuggestions(String emotionLabel) {
        if ("平稳".equals(emotionLabel)) {
            return "老人今日情绪平稳，建议家属保持日常沟通，适当安排户外活动或兴趣娱乐，维持良好身心状态。";
        } else {
            return "老人近期情绪偏焦虑，建议家属每天视频通话，关注老人用药情况，" +
                    "适当安排轻松的家庭聚会或陪伴活动。" +
                    "如焦虑情绪持续，建议咨询社区心理医生或安排全面体检。";
        }
    }

    /**
     * 构建情绪颜色映射（固定6种）。
     * 与 {@link HealthDashboardService#getEmotionColor(String)} 颜色保持一致。
     */
    private Map<String, String> buildEmotionColors() {
        Map<String, String> colors = new LinkedHashMap<>();
        colors.put("开心", "#52C41A");
        colors.put("平静", "#1890FF");
        colors.put("低落", "#722ED1");
        colors.put("焦虑", "#FF4D4F");
        colors.put("孤单", "#FA8C16");
        colors.put("思念", "#EB2F96");
        return colors;
    }
}
