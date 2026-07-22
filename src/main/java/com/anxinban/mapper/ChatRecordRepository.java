package com.anxinban.mapper;

import com.anxinban.entity.ChatRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 陪伴对话记录 Repository
 */
@Repository
public interface ChatRecordRepository extends JpaRepository<ChatRecord, Long> {

    List<ChatRecord> findByUserIdAndDateBetweenOrderByDateAsc(String userId, LocalDate start, LocalDate end);

    List<ChatRecord> findByUserIdOrderByCreatedAtDesc(String userId);

    List<ChatRecord> findByUserIdAndDateBetweenOrderByCreatedAtDesc(String userId, LocalDate start, LocalDate end);

    List<ChatRecord> findByUserIdOrderByDateAsc(String userId);
}
