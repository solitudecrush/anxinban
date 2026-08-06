package com.anxinban.mapper;

import com.anxinban.entity.ElderDailyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ElderDailyStatsRepository extends JpaRepository<ElderDailyStats, Long> {
    List<ElderDailyStats> findByElderIdAndStatDateBetweenOrderByStatDateAsc(
            String elderId, LocalDate start, LocalDate end);
}
