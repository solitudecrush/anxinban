package com.anxinban.service;

import com.anxinban.dto.HealthDashboardDto;
import com.anxinban.dto.HealthDashboardDto.*;
import com.anxinban.entity.AiServiceRecord;
import com.anxinban.entity.ChatRecord;
import com.anxinban.entity.SleepRecord;
import com.anxinban.mapper.ChatRecordRepository;
import com.anxinban.mapper.SleepRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 健康数据看板服务 — 聚合睡眠分析、陪伴记录、音乐疗法、物品寻找四项数据，
 * 并生成描述性总结语（基于 if-else 规则，无需调用大模型）。
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class HealthDashboardService {

    private static final Logger log = LoggerFactory.getLogger(HealthDashboardService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final SleepRecordRepository sleepRecordRepository;
    private final AiServiceRecordService aiServiceRecordService;
    private final ChatRecordRepository chatRecordRepository;

    @Autowired
    public HealthDashboardService(SleepRecordRepository sleepRecordRepository,
                                  AiServiceRecordService aiServiceRecordService,
                                  ChatRecordRepository chatRecordRepository) {
        this.sleepRecordRepository = sleepRecordRepository;
        this.aiServiceRecordService = aiServiceRecordService;
        this.chatRecordRepository = chatRecordRepository;
    }

    /**
     * 构建健康数据看板全部数据。
     *
     * @param elderId 老人 ID
     * @return 包含四个面板的完整看板数据
     */
    public HealthDashboardDto buildDashboard(String elderId) {
        HealthDashboardDto dto = new HealthDashboardDto();
        dto.setSleepPanel(buildSleepPanel(elderId));
        dto.setCompanionPanel(buildCompanionPanel(elderId));
        dto.setMusicPanel(buildMusicPanel(elderId));
        dto.setItemFindingPanel(buildItemFindingPanel(elderId));
        return dto;
    }

    // ==================== 一、睡眠分析 ====================

    /**
     * 构建睡眠分析面板。
     *
     * <p>查询近 7 天数据，基于最新一条生成描述性总结语。</p>
     */
    private SleepPanel buildSleepPanel(String elderId) {
        SleepPanel panel = new SleepPanel();

        // 查询近 7 天睡眠记录（从7天前到此刻）
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        List<SleepRecord> records = sleepRecordRepository
                .findByElderIdAndRecordedAtBetween(elderId, sevenDaysAgo, now);

        // 转为 DTO 列表，按日期升序
        List<SleepDayItem> items = records.stream()
                .map(r -> new SleepDayItem(
                        r.getRecordedAt() != null ? r.getRecordedAt().format(DATE_FMT) : "",
                        r.getTotalSleepHours(),
                        r.getDeepSleepPercent(),
                        r.getWakeCount() != null ? r.getWakeCount() : 0))
                .collect(Collectors.toList());

        // 如果没有数据，返回空列表和默认文案
        if (items.isEmpty()) {
            panel.setSummary("暂无近7天睡眠数据，请佩戴睡眠监测设备");
            panel.setRecords(Collections.emptyList());
            return panel;
        }

        // 取最新一条（最后一条即最新，因为已按日期升序）
        SleepDayItem latest = items.get(items.size() - 1);
        panel.setSummary(generateSleepSummary(latest));
        panel.setRecords(items);
        return panel;
    }

    /**
     * 根据最新睡眠数据生成描述性总结语。
     *
     * <p>规则（仅依赖最新一天数据，用 if-else 判断）：</p>
     * <ul>
     *   <li>总时长 > 7h → "昨晚睡得不错，时长充足"</li>
     *   <li>总时长 < 5h → "昨晚睡眠偏少，建议白天小憩"</li>
     *   <li>总时长 >= 5h 且 <= 7h → "昨晚睡眠时长一般"</li>
     *   <li>深睡占比 > 40% → 追加"深度睡眠质量较高"</li>
     * </ul>
     */
    private String generateSleepSummary(SleepDayItem latest) {
        StringBuilder sb = new StringBuilder();

        Double hours = latest.getTotalSleepHours();
        Integer deepPercent = latest.getDeepSleepPercent();

        if (hours != null) {
            if (hours > 7.0) {
                sb.append("昨晚睡得不错，时长充足");
            } else if (hours < 5.0) {
                sb.append("昨晚睡眠偏少，建议白天小憩");
            } else {
                sb.append("昨晚睡眠时长一般");
            }
        } else {
            sb.append("暂无睡眠时长数据");
        }

        if (deepPercent != null && deepPercent > 40) {
            if (sb.length() > 0) sb.append("，");
            sb.append("深度睡眠质量较高");
        }

        return sb.toString();
    }

    // ==================== 二、陪伴记录（情感对话情绪趋势） ====================

    /**
     * 构建陪伴记录面板。
     *
     * <p>数据源已统一为 chat_records 表（情绪标签仅6种：开心、平静、低落、焦虑、孤单、思念）。
     * 查询近 30 天陪伴对话记录，基于情绪标签分布生成总结语。</p>
     */
    private CompanionPanel buildCompanionPanel(String elderId) {
        CompanionPanel panel = new CompanionPanel();

        // 查询近 30 天陪伴对话记录（从 chat_records 表，统一数据源）
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        LocalDate today = LocalDate.now();
        List<ChatRecord> recentRecords = chatRecordRepository
                .findByUserIdAndDateBetweenOrderByDateAsc(elderId, thirtyDaysAgo, today);

        if (recentRecords.isEmpty()) {
            panel.setSummary("暂无近期陪伴记录");
            panel.setEmotionStats(Collections.emptyMap());
            panel.setRecentRecords(Collections.emptyList());
            return panel;
        }

        // 情绪标签分布统计（仅统计新规范6种情绪标签）
        Map<String, Long> emotionStats = recentRecords.stream()
                .filter(r -> r.getEmotion() != null && !r.getEmotion().isEmpty())
                .collect(Collectors.groupingBy(ChatRecord::getEmotion, Collectors.counting()));

        panel.setEmotionStats(emotionStats);
        panel.setSummary(generateCompanionSummary(emotionStats));

        // 最近 5 条（按日期倒序），使用新规范情绪标签和颜色映射
        List<CompanionItem> recentItems = recentRecords.stream()
                .sorted(Comparator.comparing(ChatRecord::getDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(r -> new CompanionItem(
                        r.getDate() != null ? r.getDate().format(DATE_FMT) : "",
                        r.getMessage() != null ? (r.getMessage().length() > 30
                                ? r.getMessage().substring(0, 30) + "..." : r.getMessage()) : "",
                        r.getEmotion() != null ? r.getEmotion() : "",
                        getEmotionColor(r.getEmotion())))
                .collect(Collectors.toList());

        panel.setRecentRecords(recentItems);
        return panel;
    }

    /**
     * 新规范情绪标签 → 颜色映射。
     * 仅6种情绪：开心、平静、低落、焦虑、孤单、思念
     */
    private String getEmotionColor(String emotion) {
        if (emotion == null) return "#999999";
        switch (emotion) {
            case "开心": return "#52C41A";
            case "平静": return "#1890FF";
            case "低落": return "#722ED1";
            case "焦虑": return "#FF4D4F";
            case "孤单": return "#FA8C16";
            case "思念": return "#EB2F96";
            default:    return "#999999";
        }
    }

    /**
     * 根据情绪标签分布生成陪伴总结语。
     *
     * <p>规则（新规范仅6种情绪标签）：</p>
     * <ul>
     *   <li>如果"开心+平静"正面情绪占比 > 60% → "最近情绪稳定，状态良好"</li>
     *   <li>如果"焦虑+低落+孤单+思念"负面情绪占比 > 0 → "近期情绪有波动，建议多视频通话"</li>
     *   <li>其他 → "情绪状态有待关注"</li>
     * </ul>
     */
    private String generateCompanionSummary(Map<String, Long> emotionStats) {
        long total = emotionStats.values().stream().mapToLong(Long::longValue).sum();
        if (total == 0) return "暂无情绪数据";

        // 新规范：仅6种情绪标签
        Set<String> positiveEmotions = Set.of("开心", "平静");
        Set<String> negativeEmotions = Set.of("低落", "焦虑", "孤单", "思念");

        long positiveCount = emotionStats.entrySet().stream()
                .filter(e -> positiveEmotions.contains(e.getKey()))
                .mapToLong(Map.Entry::getValue)
                .sum();

        long negativeCount = emotionStats.entrySet().stream()
                .filter(e -> negativeEmotions.contains(e.getKey()))
                .mapToLong(Map.Entry::getValue)
                .sum();

        double positiveRatio = (double) positiveCount / total;

        if (negativeCount > 0) {
            return "近期情绪有波动，建议多视频通话";
        } else if (positiveRatio >= 0.6) {
            return "最近情绪稳定，状态良好";
        } else {
            return "情绪状态有待关注，建议多加陪伴";
        }
    }

    // ==================== 三、音乐疗法 ====================

    /**
     * 构建音乐疗法面板。
     *
     * <p>查询近 7 天音乐控制记录，基于是否有播放记录生成总结语。</p>
     */
    private MusicPanel buildMusicPanel(String elderId) {
        MusicPanel panel = new MusicPanel();

        List<AiServiceRecord> allRecords = aiServiceRecordService.listByElderAndType(elderId, "music_control");
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(7).atStartOfDay();
        List<AiServiceRecord> recentRecords = allRecords.stream()
                .filter(r -> r.getInteractionTime() != null && r.getInteractionTime().isAfter(sevenDaysAgo))
                .sorted(Comparator.comparing(AiServiceRecord::getInteractionTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        // 生成总结语
        if (recentRecords.isEmpty()) {
            panel.setSummary("近一周未使用音乐疗法，可尝试播放助眠白噪音");
            panel.setRecentRecords(Collections.emptyList());
        } else {
            panel.setSummary("近期有听音乐放松，继续保持");
            List<MusicItem> items = recentRecords.stream()
                    .map(r -> new MusicItem(
                            r.getInteractionTime() != null ? r.getInteractionTime().format(DATETIME_FMT) : "",
                            r.getMusicType() != null ? r.getMusicType() : "未知",
                            r.getUserText() != null ? r.getUserText() : ""))
                    .collect(Collectors.toList());
            panel.setRecentRecords(items);
        }

        return panel;
    }

    // ==================== 四、物品寻找 ====================

    /**
     * 构建物品寻找面板。
     *
     * <p>只显示最近 5 次寻找记录。</p>
     */
    private ItemFindingPanel buildItemFindingPanel(String elderId) {
        ItemFindingPanel panel = new ItemFindingPanel();

        List<AiServiceRecord> allRecords = aiServiceRecordService.listByElderAndType(elderId, "find_item");
        List<ItemFindingItem> items = allRecords.stream()
                .sorted(Comparator.comparing(AiServiceRecord::getInteractionTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(r -> new ItemFindingItem(
                        r.getInteractionTime() != null ? r.getInteractionTime().format(DATETIME_FMT) : "",
                        r.getItem() != null ? r.getItem() : "未知",
                        r.getLocation() != null ? r.getLocation() : "未知",
                        r.getResult() != null ? r.getResult() : "未知"))
                .collect(Collectors.toList());

        panel.setRecentRecords(items);
        return panel;
    }
}
