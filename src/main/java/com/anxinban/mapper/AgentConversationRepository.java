package com.anxinban.mapper;


/**
 * AgentConversation 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.AgentConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 智能体对话记录数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.AgentConversation}。<br>
 * 主要职责：提供智能体对话记录的持久化访问能力，支持按对话编号、老年人编号、智能体类型等维度检索数据。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface AgentConversationRepository extends JpaRepository<AgentConversation, Long> {

    /**
     * 根据对话编号查询单条对话记录。
     *
     * @param conversationId 对话编号，业务唯一标识
     * @return 匹配的智能体对话记录；若不存在则返回 {@code null}
     */
    AgentConversation findByConversationId(String conversationId);

    /**
     * 根据老年人编号查询其所有对话记录。
     *
     * @param elderId 老年人编号
     * @return 该老年人关联的对话记录列表；无记录时返回空列表
     */
    List<AgentConversation> findByElderId(String elderId);

    /**
     * 根据智能体类型查询对话记录。
     *
     * @param agentType 智能体类型，例如本地智能体、云端智能体等
     * @return 该类型下的对话记录列表；无记录时返回空列表
     */
    List<AgentConversation> findByAgentType(String agentType);

    /**
     * 根据老年人编号与智能体类型联合查询对话记录。
     *
     * @param elderId   老年人编号
     * @param agentType 智能体类型
     * @return 同时满足老年人编号与智能体类型的对话记录列表；无记录时返回空列表
     */
    List<AgentConversation> findByElderIdAndAgentType(String elderId, String agentType);
}
