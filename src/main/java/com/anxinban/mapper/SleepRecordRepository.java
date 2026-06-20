package com.anxinban.mapper;

import com.anxinban.entity.SleepRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 睡眠记录 Repository
 */
@Repository
public interface SleepRecordRepository extends JpaRepository<SleepRecord, Long> {

    List<SleepRecord> findByElderId(String elderId);

    List<SleepRecord> findByElderIdOrderByRecordedAtDesc(String elderId);

    List<SleepRecord> findByElderIdAndRecordedAtBetween(String elderId, LocalDateTime start, LocalDateTime end);
}
