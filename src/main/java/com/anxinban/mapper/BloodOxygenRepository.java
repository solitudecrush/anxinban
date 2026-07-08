package com.anxinban.mapper;


/**
 * BloodOxygen 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.BloodOxygen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 血氧数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.BloodOxygen}。<br>
 * 主要职责：提供老年人血氧测量数据的持久化访问，支持按血氧记录编号、老年人编号以及测量时间范围检索，
 * 并能够获取某位老年人最新的一次血氧记录。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface BloodOxygenRepository extends JpaRepository<BloodOxygen, Long> {

    BloodOxygen findByBoId(String boId);

    List<BloodOxygen> findByElderId(String elderId);

    List<BloodOxygen> findByElderIdAndTimestampBetween(String elderId, LocalDateTime start, LocalDateTime end);

    List<BloodOxygen> findByElderIdOrderByTimestampDesc(String elderId);

    BloodOxygen findFirstByElderIdOrderByTimestampDesc(String elderId);
}
