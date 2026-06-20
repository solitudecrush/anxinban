package com.anxinban.mapper;

import com.anxinban.entity.HealthVitalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康体征记录 Repository
 */
@Repository
public interface HealthVitalRecordRepository extends JpaRepository<HealthVitalRecord, Long> {

    List<HealthVitalRecord> findByElderId(String elderId);

    List<HealthVitalRecord> findByElderIdOrderByMeasuredAtDesc(String elderId);

    List<HealthVitalRecord> findByElderIdAndMeasuredAtBetween(String elderId, LocalDateTime start, LocalDateTime end);

    HealthVitalRecord findTopByElderIdOrderByMeasuredAtDesc(String elderId);
}
