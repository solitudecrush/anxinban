package com.anxinban.mapper;

import com.anxinban.entity.MusicLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 音乐播放日志 Repository
 */
@Repository
public interface MusicLogRepository extends JpaRepository<MusicLog, Long> {

    List<MusicLog> findByUserIdAndDateBetweenOrderByDateAsc(String userId, LocalDate start, LocalDate end);

    List<MusicLog> findByUserIdAndDateAfterOrderByDateAsc(String userId, LocalDate after);

    List<MusicLog> findByUserIdOrderByDateAsc(String userId);
}
