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
 * 并生成描述性总结语（基于规则计算，无需调用大模型）。
 *
 * <p>数据来源对照：</p>
 * <ul>
 *   <li>睡眠面板 → sleep_record 表（近7天）</li>
 *   <li>陪伴面板-情绪统计 → chat_records 表（近7天）</li>
 *   <li>陪伴面板-摘要记录 → chat_records 表（近7天，与详情页同源）</li>
 *   <li>音乐面板 → ai_service_record 表（service_type=music_control，近7天）</li>
 *   <li>物品寻找面板 → ai_service_record 表（service_type=find_item，近7天）</li>
 * </ul>
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
     * 构建健康数据看板全部数据（近7天）。
     *
     * @param elderId 老人 ID，用于查询各数据表
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
     * <p>查询近 7 天数据，基于7天平均值生成描述性总结语。</p>
     */
    private SleepPanel buildSleepPanel(String elderId) {
        SleepPanel panel = new SleepPanel();

        // 查询近 7 天睡眠记录（窗口与详情页一致：今天-6 起）
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(6).atStartOfDay();
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
            panel.setSummary("暂无近7天睡眠数据，请安装睡眠监测设备");
            panel.setRecords(Collections.emptyList());
            return panel;
        }

        // 基于近7天平均值生成总结语
        panel.setSummary(generateSleepSummary(items));
        panel.setRecords(items);
        return panel;
    }

    /**
     * 根据近7天睡眠数据生成描述性总结语。
     *
     * <p>基于7天平均值进行判断：</p>
     * <ul>
     *   <li>avgSleep >= 7h 且 avgDeep >= 30% → "近7天睡眠质量良好…"</li>
     *   <li>avgSleep < 5h → "近7天睡眠严重不足…"</li>
     *   <li>中间状态 → "近7天睡眠质量一般…"</li>
     * </ul>
     */
    private String generateSleepSummary(List<SleepDayItem> items) {
        double avgSleep = items.stream()
                .filter(i -> i.getTotalSleepHours() != null)
                .mapToDouble(SleepDayItem::getTotalSleepHours)
                .average().orElse(0);
        double avgDeep = items.stream()
                .filter(i -> i.getDeepSleepPercent() != null)
                .mapToInt(SleepDayItem::getDeepSleepPercent)
                .average().orElse(0);
        int totalWake = items.stream()
                .filter(i -> i.getWakeCount() != null)
                .mapToInt(SleepDayItem::getWakeCount)
                .sum();

        StringBuilder sb = new StringBuilder();
        sb.append("近7天睡眠");

        if (avgSleep >= 7.0 && avgDeep >= 30) {
            sb.append("质量良好");
        } else if (avgSleep < 5.0) {
            sb.append("严重不足");
        } else {
            sb.append("质量一般");
        }

        sb.append(String.format("，平均睡眠时长约 %.1f 小时", avgSleep));

        if (avgDeep < 25) {
            sb.append("，深睡占比偏低");
        } else if (avgDeep >= 35) {
            sb.append("，深睡比例较好");
        }

        if (totalWake > 14) {
            sb.append("，夜间醒来较为频繁");
        } else if (totalWake <= 7) {
            sb.append("，夜间醒来次数较少");
        }

        sb.append("。");

        // 追加建议
        if (avgSleep < 6.0) {
            sb.append("建议控制晚间饮水量、保持卧室温度适宜，必要时咨询医生。");
        } else if (avgDeep < 20) {
            sb.append("建议睡前避免咖啡因摄入，可尝试助眠白噪音。");
        } else {
            sb.append("请继续保持规律的作息习惯。");
        }

        return sb.toString();
    }

    // ==================== 二、陪伴记录（情感对话情绪趋势） ====================

    /**
     * 构建陪伴记录面板。
     *
     * <p>情绪统计（emotionStats + summary）和摘要记录（recentRecords）
     * 均来自 chat_records 表（近7天），与详情页 /api/health/companion/detail 同源同窗口。</p>
     */
    private CompanionPanel buildCompanionPanel(String elderId) {
        CompanionPanel panel = new CompanionPanel();
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(6);
        LocalDate today = LocalDate.now();

        // ===== 情绪统计：来自 chat_records（近7天，窗口与详情页一致：今天-6 起） =====
        List<ChatRecord> chatRecords = chatRecordRepository
                .findByUserIdAndDateBetweenOrderByDateAsc(elderId, sevenDaysAgo, today);

        if (chatRecords.isEmpty()) {
            panel.setSummary("暂无近7天陪伴记录");
            panel.setEmotionStats(Collections.emptyMap());
            panel.setRecentRecords(Collections.emptyList());
            return panel;
        }

        // 情绪标签分布统计
        Map<String, Long> emotionStats = chatRecords.stream()
                .filter(r -> r.getEmotion() != null && !r.getEmotion().isEmpty())
                .collect(Collectors.groupingBy(ChatRecord::getEmotion, Collectors.counting()));

        panel.setEmotionStats(emotionStats);
        panel.setSummary(generateCompanionSummary(emotionStats));

        // ===== 摘要记录：与详情页同源同窗口，直接来自 chat_records（近7天，取 message 前30字） =====
        List<CompanionItem> recentItems = chatRecords.stream()
                .sorted(Comparator.comparing(ChatRecord::getDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(14)
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
     * 情绪标签 → 颜色映射。
     * 覆盖 ai_service_record 中的15种情绪 + chat_records 中的6种情绪。
     */
    private String getEmotionColor(String emotion) {
        if (emotion == null) return "#999999";
        switch (emotion) {
            // 正向情绪
            case "开心": return "#4CAF50";
            case "满足": return "#4CAF50";
            case "感动": return "#E91E63";
            case "自豪": return "#4CAF50";
            case "期待": return "#FF9800";
            case "安心": return "#8BC34A";
            case "温暖": return "#4CAF50";
            case "欣慰": return "#8BC34A";
            case "放松": return "#00BCD4";
            // 中性情绪
            case "平静": return "#2196F3";
            // 负向情绪
            case "低落": return "#607D8B";
            case "焦虑": return "#FF9800";
            case "孤单": return "#9C27B0";
            case "思念": return "#9C27B0";
            case "疲惫": return "#795548";
            case "着急": return "#FF5722";
            case "恐惧": return "#F44336";
            case "困惑": return "#FF9800";
            default:    return "#999999";
        }
    }

    /**
     * 根据情绪标签分布生成陪伴总结语（基于 chat_records 近7天数据）。
     *
     * <p>规则：</p>
     * <ul>
     *   <li>找到占比最高的情绪，描述主要情绪状态</li>
     *   <li>如果"开心+平静"正面情绪占比 > 50% → "情绪整体平稳"</li>
     *   <li>如果"焦虑+低落+孤单+思念"负向情绪占比 > 50% → "近期情绪有波动"</li>
     *   <li>其他 → "情绪状态有待关注"</li>
     * </ul>
     */
    private String generateCompanionSummary(Map<String, Long> emotionStats) {
        long total = emotionStats.values().stream().mapToLong(Long::longValue).sum();
        if (total == 0) return "暂无情绪数据";

        // 找到占比最高的情绪
        String topEmotion = emotionStats.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("未知");

        Set<String> positiveEmotions = Set.of("开心", "平静", "满足", "感动", "自豪", "期待", "安心", "温暖", "欣慰", "放松");
        Set<String> negativeEmotions = Set.of("低落", "焦虑", "孤单", "思念", "疲惫", "着急", "恐惧", "困惑");

        long positiveCount = emotionStats.entrySet().stream()
                .filter(e -> positiveEmotions.contains(e.getKey()))
                .mapToLong(Map.Entry::getValue)
                .sum();

        long negativeCount = emotionStats.entrySet().stream()
                .filter(e -> negativeEmotions.contains(e.getKey()))
                .mapToLong(Map.Entry::getValue)
                .sum();

        double positiveRatio = (double) positiveCount / total;
        double negativeRatio = (double) negativeCount / total;

        StringBuilder sb = new StringBuilder();
        sb.append("近7天情绪以").append(topEmotion).append("为主");

        if (positiveRatio >= 0.5) {
            sb.append("，情绪整体平稳，状态良好");
        } else if (negativeRatio >= 0.5) {
            sb.append("，主要表现为对健康的担忧");
            if (topEmotion.equals("焦虑")) {
                sb.append("，建议增加陪伴频次，适当进行社交活动");
            } else if (topEmotion.equals("低落")) {
                sb.append("，建议多视频通话，鼓励参与社区活动");
            }
        } else {
            sb.append("，情绪状态有待关注，建议多加陪伴");
        }

        return sb.toString();
    }

    // ==================== 三、音乐疗法 ====================

    /**
     * 构建音乐疗法面板。
     *
     * <p>查询近 7 天音乐控制记录（ai_service_record，service_type=music_control），
     * 基于播放次数生成总结语。</p>
     */
    private MusicPanel buildMusicPanel(String elderId) {
        MusicPanel panel = new MusicPanel();

        List<AiServiceRecord> allRecords = aiServiceRecordService.listByElderAndType(elderId, "music_control");
        // 窗口与详情页一致：今天-6 起
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(6).atStartOfDay();
        List<AiServiceRecord> recentRecords = allRecords.stream()
                .filter(r -> r.getInteractionTime() != null && r.getInteractionTime().isAfter(sevenDaysAgo))
                .sorted(Comparator.comparing(AiServiceRecord::getInteractionTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5) // 卡片只呈现最近 5 条
                .collect(Collectors.toList());

        // 生成总结语
        if (recentRecords.isEmpty()) {
            panel.setSummary("近7天未使用音乐疗法，可尝试播放助眠白噪音帮助放松");
            panel.setRecentRecords(Collections.emptyList());
        } else {
            int totalMinutes = recentRecords.size() * 20; // 每次默认20分钟
            panel.setSummary(String.format("近7天共进行音乐疗法 %d 次，累计约 %d 分钟，继续保持每日聆听习惯",
                    recentRecords.size(), totalMinutes));
            List<MusicItem> items = recentRecords.stream()
                    .map(r -> new MusicItem(
                            r.getInteractionTime() != null ? r.getInteractionTime().format(DATE_FMT) : "",
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
     * <p>查询近 7 天寻找记录（ai_service_record，service_type=find_item），
     * 按时间倒序显示最近记录。</p>
     */
    private ItemFindingPanel buildItemFindingPanel(String elderId) {
        ItemFindingPanel panel = new ItemFindingPanel();

        List<AiServiceRecord> allRecords = aiServiceRecordService.listByElderAndType(elderId, "find_item");
        // 窗口与详情页一致：今天-6 起
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(6).atStartOfDay();
        List<ItemFindingItem> items = allRecords.stream()
                .filter(r -> r.getInteractionTime() != null && r.getInteractionTime().isAfter(sevenDaysAgo))
                .sorted(Comparator.comparing(AiServiceRecord::getInteractionTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(r -> new ItemFindingItem(
                        r.getInteractionTime() != null ? r.getInteractionTime().format(DATE_FMT) : "",
                        r.getItem() != null ? r.getItem() : "未知",
                        r.getLocation() != null ? r.getLocation() : "未知",
                        r.getResult() != null ? r.getResult() : "未知"))
                .collect(Collectors.toList());

        panel.setRecentRecords(items);
        return panel;
    }
}
