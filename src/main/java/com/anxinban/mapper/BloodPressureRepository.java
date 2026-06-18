package com.anxinban.mapper;


/**
 * BloodPressure 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.BloodPressure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 血压数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.BloodPressure}。<br>
 * 主要职责：提供老年人血压测量数据的持久化访问，支持按血压记录编号、老年人编号以及测量时间范围检索，
 * 并能够获取某位老年人最新的一次血压记录。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface BloodPressureRepository extends JpaRepository<BloodPressure, Long> {

    /**
     * 根据血压记录编号查询单条血压数据。
     *
     * @param bpId 血压记录编号，业务唯一标识
     * @return 匹配的血压记录；若不存在则返回 {@code null}
     */
    BloodPressure findByBpId(String bpId);

    /**
     * 根据老年人编号查询其所有血压测量记录。
     *
     * @param elderId 老年人编号
     * @return 该老年人的血压记录列表；无记录时返回空列表
     */
    List<BloodPressure> findByElderId(String elderId);

    /**
     * 根据老年人编号与测量时间区间查询血压记录。
     * <p>
     * 查询逻辑：筛选某位老年人在 {@code start} 到 {@code end} 时间范围内测量的血压数据。
     * </p>
     *
     * @param elderId 老年人编号
     * @param start   起始时间（包含）
     * @param end     结束时间（包含）
     * @return 指定时间范围内该老年人的血压记录列表；无记录时返回空列表
     */
    List<BloodPressure> findByElderIdAndTimestampBetween(String elderId, LocalDateTime start, LocalDateTime end);

    /**
     * 查询某位老年人最新的一次血压记录。
     * <p>
     * 查询逻辑：按测量时间戳降序排列后取第一条记录。
     * </p>
     *
     * @param elderId 老年人编号
     * @return 该老年人最新一次的血压记录；若不存在则返回 {@code null}
     */
    BloodPressure findFirstByElderIdOrderByTimestampDesc(String elderId);
}
