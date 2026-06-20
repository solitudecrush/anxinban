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
}
