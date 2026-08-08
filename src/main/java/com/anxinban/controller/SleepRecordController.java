package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.entity.SleepRecord;
import com.anxinban.service.SleepRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 睡眠记录 REST 控制器。
 *
 * <p>对应数据字典：sleep_record</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/sleep-record")
public class SleepRecordController {

    private final SleepRecordService service;

    @Autowired
    public SleepRecordController(SleepRecordService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<SleepRecord> create(@RequestBody SleepRecord record) {
        SleepRecord saved = service.save(record);
        return saved != null ? ApiResponse.created(saved) : ApiResponse.error(500, "保存失败");
    }

    @GetMapping("/list")
    public ApiResponse<List<SleepRecord>> list(
            @RequestParam String elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (start != null && end != null) {
            return ApiResponse.success(service.listByElderAndDateRange(elderId, start, end));
        }
        return ApiResponse.success(service.listByElder(elderId));
    }

    /**
     * 获取老人最新睡眠记录（用于 AI 情绪分析等场景）。
     *
     * <p>返回最新一条睡眠记录，并附上失眠等级映射：</p>
     * <ul>
     *   <li>quality_score >= 80 → 正常</li>
     *   <li>quality_score >= 60 → 轻度失眠</li>
     *   <li>quality_score >= 40 → 中度失眠</li>
     *   <li>quality_score &lt; 40 → 重度失眠</li>
     * </ul>
     */
    @GetMapping("/latest")
    public ApiResponse<java.util.Map<String, Object>> latest(@RequestParam String elderId) {
        List<SleepRecord> records = service.listByElder(elderId);
        if (records.isEmpty()) {
            return ApiResponse.error(404, "该老人暂无睡眠记录");
        }
        SleepRecord latest = records.get(0); // 已按 recordedAt DESC 排序
        java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("elder_id", latest.getElderId());
        data.put("in_bed", latest.getInBed());
        data.put("bed_time", latest.getBedTime());
        data.put("wake_count", latest.getWakeCount() != null ? latest.getWakeCount() : 0);
        data.put("quality_score", latest.getQualityScore() != null ? latest.getQualityScore() : 0);
        data.put("total_sleep_hours", latest.getTotalSleepHours());
        data.put("deep_sleep_percent", latest.getDeepSleepPercent());
        data.put("insomnia_level", mapInsomniaLevel(latest.getQualityScore()));
        data.put("recorded_at", latest.getRecordedAt() != null ? latest.getRecordedAt().toString() : null);
        return ApiResponse.success(data);
    }

    /**
     * 根据睡眠质量评分映射失眠等级。
     */
    private String mapInsomniaLevel(Integer qualityScore) {
        if (qualityScore == null) return "未知";
        if (qualityScore >= 80) return "正常";
        if (qualityScore >= 60) return "轻度";
        if (qualityScore >= 40) return "中度";
        return "重度";
    }
}
