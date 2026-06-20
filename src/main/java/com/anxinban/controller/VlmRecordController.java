package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.entity.VlmRecord;
import com.anxinban.service.VlmRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * VLM 找物品记录 REST 控制器。
 *
 * <p>对应数据字典：vlm_record</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/vlm-record")
public class VlmRecordController {

    private final VlmRecordService service;

    @Autowired
    public VlmRecordController(VlmRecordService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<VlmRecord> create(@RequestBody VlmRecord record) {
        VlmRecord saved = service.save(record);
        return saved != null ? ApiResponse.created(saved) : ApiResponse.error(500, "保存失败");
    }

    @GetMapping("/list")
    public ApiResponse<List<VlmRecord>> list(@RequestParam(required = false) String elderId) {
        if (elderId != null && !elderId.isEmpty()) {
            return ApiResponse.success(service.listByElder(elderId));
        }
        return ApiResponse.success(service.listAll());
    }
}
