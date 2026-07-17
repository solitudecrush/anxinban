package com.anxinban.service;

import com.anxinban.entity.AiServiceRecord;
import com.anxinban.mapper.AiServiceRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * AI 服务记录服务 — 统一管理陪伴对话、VLM 找物品、音乐控制三类 AI 交互记录。
 *
 * <p>对应数据字典：ai_service_record（合并原 companion_record 与 vlm_record）</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class AiServiceRecordService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceRecordService.class);
    private final AiServiceRecordRepository repository;

    @Autowired
    public AiServiceRecordService(AiServiceRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * 同步保存 AI 服务记录。
     */
    public AiServiceRecord save(AiServiceRecord record) {
        try {
            if (record.getCreatedAt() == null) {
                record.setCreatedAt(LocalDateTime.now());
            }
            return repository.save(record);
        } catch (Exception e) {
            log.error("保存 AI 服务记录失败: elderId={}, serviceType={}, error={}",
                    record.getElderId(), record.getServiceType(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 异步保存 AI 服务记录，返回 CompletableFuture。
     */
    @Async
    public CompletableFuture<AiServiceRecord> saveAsync(AiServiceRecord record) {
        return CompletableFuture.completedFuture(save(record));
    }

    /**
     * 按老人 ID 查询全部记录。
     */
    public List<AiServiceRecord> listByElder(String elderId) {
        return repository.findByElderIdOrderByInteractionTimeDesc(elderId);
    }

    /**
     * 按老人 ID 和服务类型查询。
     */
    public List<AiServiceRecord> listByElderAndType(String elderId, String serviceType) {
        return repository.findByElderIdAndServiceTypeOrderByInteractionTimeDesc(elderId, serviceType);
    }

    /**
     * 查询全部记录，按交互时间倒序。
     */
    public List<AiServiceRecord> listAll() {
        List<AiServiceRecord> list = repository.findAll();
        list.sort(Comparator.comparing(AiServiceRecord::getInteractionTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return list;
    }

    /**
     * 按服务类型查询全部记录。
     */
    public List<AiServiceRecord> listByType(String serviceType) {
        return repository.findByServiceTypeOrderByInteractionTimeDesc(serviceType);
    }

    /**
     * 异步查询老人全部 AI 服务记录。
     */
    @Async
    public CompletableFuture<List<AiServiceRecord>> listByElderAsync(String elderId) {
        return CompletableFuture.completedFuture(listByElder(elderId));
    }

    /**
     * 异步查询老人指定类型的 AI 服务记录。
     */
    @Async
    public CompletableFuture<List<AiServiceRecord>> listByElderAndTypeAsync(String elderId, String serviceType) {
        return CompletableFuture.completedFuture(listByElderAndType(elderId, serviceType));
    }

    /**
     * 异步查询全部 AI 服务记录。
     */
    @Async
    public CompletableFuture<List<AiServiceRecord>> listAllAsync() {
        return CompletableFuture.completedFuture(listAll());
    }
}
