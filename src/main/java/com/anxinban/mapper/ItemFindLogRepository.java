package com.anxinban.mapper;

import com.anxinban.entity.ItemFindLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 物品寻找记录 Repository
 */
@Repository
public interface ItemFindLogRepository extends JpaRepository<ItemFindLog, Long> {

    List<ItemFindLog> findByUserIdAndDateBetweenOrderByDateAsc(String userId, LocalDate start, LocalDate end);

    List<ItemFindLog> findByUserIdOrderByDateAsc(String userId);
}
