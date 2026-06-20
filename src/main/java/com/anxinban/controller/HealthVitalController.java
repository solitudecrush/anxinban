package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.entity.HealthVitalRecord;
import com.anxinban.service.HealthVitalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康体征记录 REST 控制器。
 *
 * <p>对应数据字典：health_record（体征部分）</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/health-vital")
public class HealthVitalController {

    private final HealthVitalRecordService service;

    @Autowired
    public HealthVitalController(HealthVitalRecordService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<HealthVitalRecord> create(@RequestBody HealthVitalRecord record) {
        HealthVitalRecord saved = service.save(record);
        return saved != null ? ApiResponse.created(saved) : ApiResponse.error(500, "保存失败");
    }

    @GetMapping("/list")
    public ApiResponse<List<HealthVitalRecord>> list(
            @RequestParam String elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (start != null && end != null) {
            return ApiResponse.success(service.listByElderAndDateRange(elderId, start, end));
        }
        return ApiResponse.success(service.listByElder(elderId));
    }

    @GetMapping("/latest")
    public ApiResponse<HealthVitalRecord> latest(@RequestParam String elderId) {
        HealthVitalRecord record = service.getLatestByElder(elderId);
        if (record == null) {
            return ApiResponse.error(404, "未找到该老人的体征记录");
        }
        return ApiResponse.success(record);
    }
}
