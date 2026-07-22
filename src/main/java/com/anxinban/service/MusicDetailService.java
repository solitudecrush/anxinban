package com.anxinban.service;

import com.anxinban.entity.MusicLog;
import com.anxinban.mapper.MusicLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 音乐详情服务。
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class MusicDetailService {

    private static final Logger log = LoggerFactory.getLogger(MusicDetailService.class);
    private final MusicLogRepository repository;

    @Autowired
    public MusicDetailService(MusicLogRepository repository) {
        this.repository = repository;
    }

    /**
     * 查询时间范围内的音乐播放数据：
     * - dailyList: 每日播放总时长
     * - topSongs: 最近 7 天内播放次数最多的 5 首歌
     */
    public Map<String, Object> getMusicDetail(String userId, LocalDate startDate, LocalDate endDate) {
        List<MusicLog> records = repository.findByUserIdAndDateBetweenOrderByDateAsc(userId, startDate, endDate);

        // 按日期聚合每日总时长
        Map<LocalDate, Integer> dailyMinutes = new LinkedHashMap<>();
        for (MusicLog r : records) {
            dailyMinutes.merge(r.getDate(), r.getDurationMinutes() != null ? r.getDurationMinutes() : 0, Integer::sum);
        }

        List<Map<String, Object>> dailyList = new ArrayList<>();
        for (Map.Entry<LocalDate, Integer> entry : dailyMinutes.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", entry.getKey().toString());
            item.put("totalMinutes", entry.getValue());
            dailyList.add(item);
        }

        // 最近 7 天内播放次数最多的 5 首歌
        LocalDate sevenDaysAgo = endDate.minusDays(6);
        List<MusicLog> recentRecords = records.stream()
                .filter(r -> !r.getDate().isBefore(sevenDaysAgo))
                .collect(Collectors.toList());

        // 如果时间范围内数据不足 7 天，用全部数据
        if (recentRecords.isEmpty()) {
            recentRecords = records;
        }

        Map<String, Long> songCount = recentRecords.stream()
                .collect(Collectors.groupingBy(MusicLog::getSongName, Collectors.counting()));

        List<Map<String, Object>> topSongs = songCount.entrySet().stream()
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
