package com.anxinban.mapper;


/**
 * AlarmProcess 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.AlarmProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 告警处理记录数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.AlarmProcess}。<br>
 * 主要职责：记录告警事件的处理流转信息，支持按处理编号、告警编号、处理人标识等维度检索处理记录。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface AlarmProcessRepository extends JpaRepository<AlarmProcess, Long> {

    /**
     * 根据处理编号查询单条处理记录。
     *
     * @param processId 处理编号，业务唯一标识
     * @return 匹配的告警处理记录；若不存在则返回 {@code null}
     */
    AlarmProcess findByProcessId(String processId);

    /**
     * 根据告警编号查询该告警的所有处理记录。
     *
     * @param alarmId 告警编号
     * @return 该告警关联的处理记录列表；无记录时返回空列表
     */
    List<AlarmProcess> findByAlarmId(String alarmId);

    /**
     * 根据处理人标识查询其处理的所有告警记录。
     *
     * @param handlerId 处理人标识
     * @return 该处理人处理过的告警记录列表；无记录时返回空列表
     */
    List<AlarmProcess> findByHandlerId(String handlerId);
}
