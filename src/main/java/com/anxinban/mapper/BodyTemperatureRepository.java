package com.anxinban.mapper;


/**
 * BodyTemperature 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.BodyTemperature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 体温数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.BodyTemperature}。<br>
 * 主要职责：提供老年人体温测量数据的持久化访问，支持按体温记录编号、老年人编号以及测量时间范围检索，
 * 并能够获取某位老年人最新的一次体温记录。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface BodyTemperatureRepository extends JpaRepository<BodyTemperature, Long> {

    BodyTemperature findByBtId(String btId);

    List<BodyTemperature> findByElderId(String elderId);

    List<BodyTemperature> findByElderIdAndTimestampBetween(String elderId, LocalDateTime start, LocalDateTime end);

    List<BodyTemperature> findByElderIdOrderByTimestampDesc(String elderId);

    BodyTemperature findFirstByElderIdOrderByTimestampDesc(String elderId);
}
