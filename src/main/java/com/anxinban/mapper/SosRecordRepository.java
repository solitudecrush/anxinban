package com.anxinban.mapper;


/**
 * SosRecord 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.SosRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SosRecordRepository extends JpaRepository<SosRecord, Long> {
    SosRecord findBySosId(String sosId);
    List<SosRecord> findByElderId(String elderId);
    List<SosRecord> findByElderIdOrderByTriggerTimeDesc(String elderId);
}
