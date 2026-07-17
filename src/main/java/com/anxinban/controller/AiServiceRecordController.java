package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.entity.AiServiceRecord;
import com.anxinban.service.AiServiceRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * AI 服务记录 REST 控制器 — 统一管理陪伴对话、VLM 找物品、音乐控制三类 AI 交互记录。
 *
 * <p>所有查询接口返回 {@link CompletableFuture}，异步非阻塞。</p>
 * <p>对应数据字典：ai_service_record（合并原 companion_record 与 vlm_record）</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/ai-service")
public class AiServiceRecordController {

    private static final Logger log = LoggerFactory.getLogger(AiServiceRecordController.class);
    private final AiServiceRecordService service;

    @Autowired
    public AiServiceRecordController(AiServiceRecordService service) {
        this.service = service;
    }

    /**
     * 创建 AI 服务记录（同步）。
     */
    @PostMapping("/record")
    public ApiResponse<AiServiceRecord> create(@RequestBody AiServiceRecord record) {
        log.info("创建 AI 服务记录: elderId={}, serviceType={}", record.getElderId(), record.getServiceType());
        AiServiceRecord saved = service.save(record);
        return saved != null ? ApiResponse.created(saved) : ApiResponse.error(500, "保存失败");
    }

    /**
     * 异步创建 AI 服务记录。
     */
    @PostMapping("/record/async")
    public CompletableFuture<ApiResponse<AiServiceRecord>> createAsync(@RequestBody AiServiceRecord record) {
        log.info("异步创建 AI 服务记录: elderId={}, serviceType={}", record.getElderId(), record.getServiceType());
        return service.saveAsync(record).thenApply(r ->
                r != null ? ApiResponse.created(r) : ApiResponse.error(500, "保存失败"));
    }

    /**
     * 查询 AI 服务记录列表（同步）。
     *
     * @param elderId     老人 ID（可选）
     * @param serviceType 服务类型（可选）：companion_chat / find_item / music_control
     */
    @GetMapping("/record/list")
    public ApiResponse<List<AiServiceRecord>> list(
            @RequestParam(required = false) String elderId,
            @RequestParam(required = false) String serviceType) {

        List<AiServiceRecord> result;
        if (elderId != null && !elderId.isEmpty() && serviceType != null && !serviceType.isEmpty()) {
            result = service.listByElderAndType(elderId, serviceType);
        } else if (elderId != null && !elderId.isEmpty()) {
            result = service.listByElder(elderId);
        } else if (serviceType != null && !serviceType.isEmpty()) {
            result = service.listByType(serviceType);
        } else {
            result = service.listAll();
        }
        return ApiResponse.success(result);
    }

    /**
     * 异步查询 AI 服务记录列表。
     *
     * @param elderId     老人 ID（可选）
     * @param serviceType 服务类型（可选）：companion_chat / find_item / music_control
     */
    @GetMapping("/record/list/async")
    public CompletableFuture<ApiResponse<List<AiServiceRecord>>> listAsync(
            @RequestParam(required = false) String elderId,
            @RequestParam(required = false) String serviceType) {

        CompletableFuture<List<AiServiceRecord>> future;
        if (elderId != null && !elderId.isEmpty() && serviceType != null && !serviceType.isEmpty()) {
            future = service.listByElderAndTypeAsync(elderId, serviceType);
        } else if (elderId != null && !elderId.isEmpty()) {
            future = service.listByElderAsync(elderId);
        } else {
            future = service.listAllAsync();
        }
        return future.thenApply(ApiResponse::success);
    }
}
