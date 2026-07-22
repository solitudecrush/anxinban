package com.anxinban.service;

import com.anxinban.entity.ChatRecord;
import com.anxinban.mapper.ChatRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 陪伴对话详情服务。
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class CompanionDetailService {

    private static final Logger log = LoggerFactory.getLogger(CompanionDetailService.class);
    private final ChatRecordRepository repository;

    @Autowired
    public CompanionDetailService(ChatRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * 查询时间范围内的陪伴对话数据：
     * - dailyEmotions: 每日情绪分布
     * - recentChats: 最近 10 条对话记录（message 取前 20 字符）
     */
    public Map<String, Object> getCompanionDetail(String userId, LocalDate startDate, LocalDate endDate) {
        List<ChatRecord> records = repository.findByUserIdAndDateBetweenOrderByDateAsc(userId, startDate, endDate);

        // 按日期聚合情绪分布
        Map<LocalDate, Map<String, Long>> dailyEmotionMap = new LinkedHashMap<>();
        for (ChatRecord r : records) {
            dailyEmotionMap.computeIfAbsent(r.getDate(), k -> new LinkedHashMap<>())
                    .merge(r.getEmotion(), 1L, Long::sum);
        }

        List<Map<String, Object>> dailyEmotions = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<String, Long>> entry : dailyEmotionMap.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", entry.getKey().toString());
            item.put("emotions", entry.getValue());
            dailyEmotions.add(item);
        }

        // 最近 10 条对话（按创建时间倒序），只取 message 前 20 字符
        List<ChatRecord> recentRecords = records.stream()
                .sorted(Comparator.comparing(ChatRecord::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .collect(Collectors.toList());

        List<Map<String, Object>> recentChats = new ArrayList<>();
        for (ChatRecord r : recentRecords) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", r.getDate().toString());
            String message = r.getMessage() != null ? r.getMessage() : "";
            item.put("preview", message.length() > 20 ? message.substring(0, 20) + "..." : message);
            item.put("emotion", r.getEmotion());
            recentChats.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dailyEmotions", dailyEmotions);
        result.put("recentChats", recentChats);
        return result;
    }
}
