package com.anxinban.mapper;

import com.anxinban.entity.CompanionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 陪伴交互记录 Repository
 */
@Repository
public interface CompanionRecordRepository extends JpaRepository<CompanionRecord, Long> {

    List<CompanionRecord> findByElderId(String elderId);

    List<CompanionRecord> findByElderIdOrderByInteractionTimeDesc(String elderId);

    List<CompanionRecord> findByEmotion(String emotion);
}
