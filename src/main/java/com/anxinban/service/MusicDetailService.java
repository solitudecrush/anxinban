package com.anxinban.service;

import com.anxinban.entity.AiServiceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 音乐详情服务。
 *
 * <p>数据来源：ai_service_record 表（service_type=music_control）。
 * 每次播放默认按20分钟计算时长，歌曲名取自 music_type 字段。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class MusicDetailService {

    private static final Logger log = LoggerFactory.getLogger(MusicDetailService.class);
    private static final int DEFAULT_DURATION_MINUTES = 20;

    private final AiServiceRecordService aiServiceRecordService;

    @Autowired
    public MusicDetailService(AiServiceRecordService aiServiceRecordService) {
        this.aiServiceRecordService = aiServiceRecordService;
    }

    /**
     * 查询时间范围内的音乐播放数据（来自 ai_service_record）：
     * - dailyList: 每日播放总时长（每次播放默认20分钟）
     * - topSongs: 播放次数最多的音乐类型（取前5）
     *
     * @param elderId   老人 ID
     * @param startDate 开始日期（含）
     * @param endDate   结束日期（含）
     */
    public Map<String, Object> getMusicDetail(String elderId, LocalDate startDate, LocalDate endDate) {
        List<AiServiceRecord> allRecords = aiServiceRecordService.listByElderAndType(elderId, "music_control");

        LocalDateTime startDt = startDate.atStartOfDay();
        LocalDateTime endDt = endDate.plusDays(1).atStartOfDay();

        // 过滤时间范围内的记录
        List<AiServiceRecord> records = allRecords.stream()
                .filter(r -> r.getInteractionTime() != null
                        && !r.getInteractionTime().isBefore(startDt)
                        && r.getInteractionTime().isBefore(endDt))
                .sorted(Comparator.comparing(AiServiceRecord::getInteractionTime))
                .collect(Collectors.toList());

        // ===== dailyList: 按日期聚合每日总时长（每次播放默认20分钟） =====
        Map<LocalDate, Integer> dailyMinutes = new LinkedHashMap<>();
        for (AiServiceRecord r : records) {
            LocalDate date = r.getInteractionTime().toLocalDate();
            dailyMinutes.merge(date, DEFAULT_DURATION_MINUTES, Integer::sum);
        }

        List<Map<String, Object>> dailyList = new ArrayList<>();
        for (Map.Entry<LocalDate, Integer> entry : dailyMinutes.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", entry.getKey().toString());
            item.put("totalMinutes", entry.getValue());
            dailyList.add(item);
        }

        // ===== topSongs: 按 music_type 统计播放次数，取前5 =====
        Map<String, Long> typeCount = records.stream()
                .filter(r -> r.getMusicType() != null && !r.getMusicType().isEmpty())
                .collect(Collectors.groupingBy(AiServiceRecord::getMusicType, Collectors.counting()));

        List<Map<String, Object>> topSongs = typeCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("songName", e.getKey());
                    item.put("count", e.getValue().intValue());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dailyList", dailyList);
        result.put("topSongs", topSongs);
        return result;
    }
}
