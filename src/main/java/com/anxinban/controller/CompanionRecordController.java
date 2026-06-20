package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.entity.CompanionRecord;
import com.anxinban.service.CompanionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 陪伴交互记录 REST 控制器。
 *
 * <p>对应数据字典：companion_record</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/companion-record")
public class CompanionRecordController {

    private final CompanionRecordService service;

    @Autowired
    public CompanionRecordController(CompanionRecordService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<CompanionRecord> create(@RequestBody CompanionRecord record) {
        CompanionRecord saved = service.save(record);
        return saved != null ? ApiResponse.created(saved) : ApiResponse.error(500, "保存失败");
    }

    @GetMapping("/list")
    public ApiResponse<List<CompanionRecord>> list(@RequestParam(required = false) String elderId) {
        if (elderId != null && !elderId.isEmpty()) {
            return ApiResponse.success(service.listByElder(elderId));
        }
        return ApiResponse.success(service.listAll());
    }
}
