package com.anxinban.mapper;


/**
 * AlarmEvent 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.AlarmEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 告警事件数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.AlarmEvent}。<br>
 * 主要职责：提供告警事件数据的持久化访问，支持按告警编号、老年人编号、设备编号、告警类型、告警状态、
 * 楼栋、已读状态以及时间范围等多维度查询，同时支持分页与统计计数。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface AlarmEventRepository extends JpaRepository<AlarmEvent, Long> {

    /**
     * 根据告警编号查询单条告警事件。
     *
     * @param alarmId 告警编号，业务唯一标识
     * @return 匹配的告警事件；若不存在则返回 {@code null}
     */
    AlarmEvent findByAlarmId(String alarmId);

    /**
     * 根据老年人编号查询其关联的所有告警事件。
     *
     * @param elderId 老年人编号
     * @return 该老年人的告警事件列表；无记录时返回空列表
     */
    List<AlarmEvent> findByElderId(String elderId);

    /**
     * 根据设备编号查询该设备产生的所有告警事件。
     *
     * @param deviceId 设备编号
     * @return 该设备的告警事件列表；无记录时返回空列表
     */
    List<AlarmEvent> findByDeviceId(String deviceId);

    /**
     * 根据告警类型查询告警事件。
     *
     * @param alarmType 告警类型，例如跌倒、火灾、心率异常等
     * @return 该类型下的告警事件列表；无记录时返回空列表
     */
    List<AlarmEvent> findByAlarmType(String alarmType);

    /**
     * 根据告警状态查询告警事件。
     *
     * @param alarmStatus 告警状态，例如待处理、已处理、已忽略等
     * @return 该状态下的告警事件列表；无记录时返回空列表
     */
    List<AlarmEvent> findByAlarmStatus(String alarmStatus);

    /**
     * 根据老年人编号与告警状态联合查询告警事件。
     *
     * @param elderId     老年人编号
     * @param alarmStatus 告警状态
     * @return 同时满足老年人编号与告警状态的告警事件列表；无记录时返回空列表
     */
    List<AlarmEvent> findByElderIdAndAlarmStatus(String elderId, String alarmStatus);

    /**
     * 根据创建时间区间查询告警事件。
     * <p>
     * 查询逻辑：筛选创建时间大于等于 {@code start} 且小于等于 {@code end} 的告警记录。
     * </p>
     *
     * @param start 起始时间（包含）
     * @param end   结束时间（包含）
     * @return 指定时间范围内的告警事件列表；无记录时返回空列表
     */
    List<AlarmEvent> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据老年人编号与已读状态查询告警事件。
     *
     * @param elderId 老年人编号
     * @param isRead  已读状态，{@code true} 表示已读，{@code false} 表示未读
     * @return 该老年人指定已读状态的告警事件列表；无记录时返回空列表
     */
    List<AlarmEvent> findByElderIdAndIsRead(String elderId, Boolean isRead);

    /**
     * 统计指定老年人的指定已读状态告警事件数量。
     *
     * @param elderId 老年人编号
     * @param isRead  已读状态
     * @return 符合条件的告警事件数量
     */
    long countByElderIdAndIsRead(String elderId, Boolean isRead);

    /**
     * 根据告警类型与告警状态联合查询告警事件。
     *
     * @param alarmType   告警类型
     * @param alarmStatus 告警状态
     * @return 同时满足告警类型与状态的告警事件列表；无记录时返回空列表
     */
    List<AlarmEvent> findByAlarmTypeAndAlarmStatus(String alarmType, String alarmStatus);

    /**
     * 根据楼栋编号查询该楼栋下的所有告警事件。
     *
     * @param building 楼栋编号
     * @return 该楼栋下的告警事件列表；无记录时返回空列表
     */
    List<AlarmEvent> findByBuilding(String building);

    /**
     * 根据告警类型与楼栋编号联合查询告警事件。
     *
     * @param alarmType 告警类型
     * @param building  楼栋编号
     * @return 同时满足告警类型与楼栋编号的告警事件列表；无记录时返回空列表
     */
    List<AlarmEvent> findByAlarmTypeAndBuilding(String alarmType, String building);

    /**
     * 统计指定类型告警在某一时间之后创建的事件数量。
     *
     * @param alarmType 告警类型
     * @param start     起始时间（不包含该时间之前的记录）
     * @return 符合条件的告警事件数量
     */
    long countByAlarmTypeAndCreatedAtAfter(String alarmType, LocalDateTime start);

    /**
     * 统计某一时间之后创建的所有告警事件数量。
     *
     * @param start 起始时间（不包含该时间之前的记录）
     * @return 符合条件的告警事件数量
     */
    long countByCreatedAtAfter(LocalDateTime start);

    /**
     * 根据告警类型分页查询告警事件。
     *
     * @param alarmType 告警类型
     * @param pageable  分页参数，包含页码、页大小及排序规则
     * @return 符合条件的告警事件分页结果
     */
    Page<AlarmEvent> findByAlarmType(String alarmType, Pageable pageable);

    /**
     * 根据告警等级分页查询告警事件。
     *
     * @param alarmLevel 告警等级，例如一般、严重、紧急等
     * @param pageable   分页参数，包含页码、页大小及排序规则
     * @return 符合条件的告警事件分页结果
     */
    Page<AlarmEvent> findByAlarmLevel(String alarmLevel, Pageable pageable);

    /**
     * 根据告警状态分页查询告警事件。
     *
     * @param alarmStatus 告警状态
     * @param pageable    分页参数，包含页码、页大小及排序规则
     * @return 符合条件的告警事件分页结果
     */
    Page<AlarmEvent> findByAlarmStatus(String alarmStatus, Pageable pageable);
}
