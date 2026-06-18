package com.anxinban.mapper;


/**
 * HealthRecord 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 健康档案数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.HealthRecord}。<br>
 * 主要职责：提供老年人健康档案信息的持久化访问，支持按档案编号、老年人编号检索健康档案。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    /**
     * 根据档案编号查询单条健康档案。
     *
     * @param recordId 档案编号，业务唯一标识
     * @return 匹配的健康档案记录；若不存在则返回 {@code null}
     */
    HealthRecord findByRecordId(String recordId);

    /**
     * 根据老年人编号查询其健康档案。
     *
     * @param elderId 老年人编号
     * @return 该老年人的健康档案记录；若不存在则返回 {@code null}
     */
    HealthRecord findByElderId(String elderId);
}
