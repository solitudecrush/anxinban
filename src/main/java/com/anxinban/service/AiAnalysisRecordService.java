package com.anxinban.service;

import com.anxinban.entity.AiAnalysisRecord;
import com.anxinban.mapper.AiAnalysisRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * AI 分析记录服务。
 *
 * <p>负责 AI 健康分析结果的持久化与查询。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class AiAnalysisRecordService {

    private static final Logger log = LoggerFactory.getLogger(AiAnalysisRecordService.class);

    private final AiAnalysisRecordRepository repository;

    @Autowired
    public AiAnalysisRecordService(AiAnalysisRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * 保存一条 AI 分析记录。
     *
     * <p>自动生成 recordId 并设置 createdAt。保存失败时仅记录日志，不抛出异常。</p>
     *
     * @param record 待保存的分析记录
     * @return 保存成功返回记录，失败返回 null
     */
    public AiAnalysisRecord save(AiAnalysisRecord record) {
        try {
            if (record.getRecordId() == null || record.getRecordId().isEmpty()) {
                record.setRecordId("ar_" + UUID.randomUUID().toString().substring(0, 8));
            }
            if (record.getCreatedAt() == null) {
                record.setCreatedAt(LocalDateTime.now());
            }
            AiAnalysisRecord saved = repository.save(record);
            log.info("AI 分析记录已保存: recordId={}, elderId={}, riskLevel={}, source={}",
                    saved.getRecordId(), saved.getElderId(), saved.getRiskLevel(), saved.getSource());
            return saved;
        } catch (Exception e) {
            log.error("保存 AI 分析记录失败: elderId={}, error={}", record.getElderId(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 查询指定老人最近一次 AI 分析记录。
     *
     * @param elderId 老人唯一标识
     * @return 最近一条分析记录，不存在时返回 null
     */
    public AiAnalysisRecord getLatestByElder(String elderId) {
        try {
            Page<AiAnalysisRecord> page = repository.findTopByElderIdOrderByCreatedAtDesc(
                    elderId, PageRequest.of(0, 1));
            if (page.hasContent()) {
                return page.getContent().get(0);
            }
            return null;
        } catch (Exception e) {
            log.error("查询最近 AI 分析记录失败: elderId={}, error={}", elderId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 分页查询指定老人的 AI 分析历史记录。
     *
     * @param elderId  老人唯一标识
     * @param page     页码（从 1 开始）
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public Page<AiAnalysisRecord> listByElder(String elderId, int page, int pageSize) {
        return repository.findByElderIdOrderByCreatedAtDesc(
                elderId, PageRequest.of(page - 1, pageSize));
    }
}
