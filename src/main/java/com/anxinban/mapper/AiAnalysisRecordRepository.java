package com.anxinban.mapper;

import com.anxinban.entity.AiAnalysisRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * AI 分析记录仓库。
 *
 * <p>提供对 ai_analysis_record 表的持久化与查询操作。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface AiAnalysisRecordRepository extends JpaRepository<AiAnalysisRecord, Long> {

    /**
     * 根据业务 recordId 查询。
     *
     * @param recordId 业务唯一标识
     * @return 匹配的记录（可能为空）
     */
    Optional<AiAnalysisRecord> findByRecordId(String recordId);

    /**
     * 查询指定老人的所有分析记录（按创建时间倒序）。
     *
     * @param elderId 老人唯一标识
     * @return 分析记录列表
     */
    List<AiAnalysisRecord> findByElderIdOrderByCreatedAtDesc(String elderId);

    /**
     * 分页查询指定老人的分析记录（按创建时间倒序）。
     *
     * @param elderId  老人唯一标识
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<AiAnalysisRecord> findByElderIdOrderByCreatedAtDesc(String elderId, Pageable pageable);

    /**
     * 查询指定老人最近一条分析记录。
     *
     * @param elderId  老人唯一标识
     * @param pageable 分页参数（取 top 1）
     * @return 分页结果（取第一条）
     */
    Page<AiAnalysisRecord> findTopByElderIdOrderByCreatedAtDesc(String elderId, Pageable pageable);
}
