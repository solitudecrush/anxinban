package com.anxinban.mapper;

import com.anxinban.entity.AiServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI 服务记录 Repository — 统一查询陪伴对话、VLM 找物品、音乐控制三类记录。
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface AiServiceRecordRepository extends JpaRepository<AiServiceRecord, Long> {

    /** 按老人 ID 查询，按交互时间倒序 */
    List<AiServiceRecord> findByElderIdOrderByInteractionTimeDesc(String elderId);

    /** 按老人 ID 和服务类型查询 */
    List<AiServiceRecord> findByElderIdAndServiceTypeOrderByInteractionTimeDesc(String elderId, String serviceType);

    /** 按服务类型查询 */
    List<AiServiceRecord> findByServiceTypeOrderByInteractionTimeDesc(String serviceType);
}
