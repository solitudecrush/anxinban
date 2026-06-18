package com.anxinban.mapper;


/**
 * AgentIntentLog 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.AgentIntentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 智能体意图日志数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.AgentIntentLog}。<br>
 * 主要职责：持久化并检索智能体识别到的用户意图日志，支持按意图编号、老年人编号、意图内容以及处理人等维度查询。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface AgentIntentLogRepository extends JpaRepository<AgentIntentLog, Long> {

    /**
     * 根据意图编号查询单条意图日志。
     *
     * @param intentId 意图编号，业务唯一标识
     * @return 匹配的智能体意图日志；若不存在则返回 {@code null}
     */
    AgentIntentLog findByIntentId(String intentId);

    /**
     * 根据老年人编号查询其所有意图识别日志。
     *
     * @param elderId 老年人编号
     * @return 该老年人的意图日志列表；无记录时返回空列表
     */
    List<AgentIntentLog> findByElderId(String elderId);

    /**
     * 根据意图内容查询意图日志。
     *
     * @param intent 意图内容，例如“开空调”、“求助”等
     * @return 符合该意图内容的日志列表；无记录时返回空列表
     */
    List<AgentIntentLog> findByIntent(String intent);

    /**
     * 根据处理人标识查询由其处理的意图日志。
     *
     * @param handledBy 处理人标识
     * @return 该处理人处理过的意图日志列表；无记录时返回空列表
     */
    List<AgentIntentLog> findByHandledBy(String handledBy);
}
