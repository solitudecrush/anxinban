package com.anxinban.mapper;

import com.anxinban.entity.VlmRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * VLM 找物品记录 Repository
 */
@Repository
public interface VlmRecordRepository extends JpaRepository<VlmRecord, Long> {

    List<VlmRecord> findByElderId(String elderId);

    List<VlmRecord> findByElderIdOrderByQueryTimeDesc(String elderId);

    List<VlmRecord> findByResult(String result);
}
