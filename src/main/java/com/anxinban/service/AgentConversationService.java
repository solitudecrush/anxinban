package com.anxinban.service;
import com.anxinban.entity.AgentConversation;
import com.anxinban.mapper.AgentConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 智能代理对话服务。
 *
 * <p>负责管理老人与 AI Agent 之间的对话记录，包括创建对话、查询单条对话、
 * 按老人查询以及按老人与代理类型组合查询。当传入对话未包含代理回复时，
 * 会调用模型网关基于意图生成默认回复。</p>
 *
 * <p>事务边界：当前未显式开启事务，单次 Repository 保存由 Spring Data JPA
 * 隐式事务保证；若后续扩展为批量写入，建议加 {@code @Transactional}。</p>
 *
 * <p>依赖的 Mapper/Repository：</p>
 * <ul>
 *     <li>{@link AgentConversationRepository} — 代理对话记录的持久化与查询</li>
 * </ul>
 *
 * <p>依赖的 Service：</p>
 * <ul>
 *     <li>{@link ModelGatewayService} — 基于规则生成默认代理回复</li>
 * </ul>
 */
@Service
public class AgentConversationService {
    private final AgentConversationRepository agentConversationRepository;
    private final ModelGatewayService modelGatewayService;

    /**
     * 构造器注入对话仓库与模型网关服务。
     *
     * @param agentConversationRepository 代理对话仓库
     * @param modelGatewayService         模型网关服务
     */
    @Autowired
    public AgentConversationService(AgentConversationRepository agentConversationRepository, ModelGatewayService modelGatewayService) {
        this.agentConversationRepository = agentConversationRepository;
        this.modelGatewayService = modelGatewayService;
    }

    /**
     * 创建一条代理对话记录。
     *
     * <p>业务规则：</p>
     * <ul>
     *     <li>conversationId 必须非空，否则抛出 {@link IllegalArgumentException}</li>
     *     <li>若 agentReply 为空，则基于 intent 调用规则模型生成默认回复；
     *         intent 为空时使用 "default"</li>
     *     <li>自动设置 createdAt 为当前时间</li>
     * </ul>
     *
     * @param conversation 待保存的对话实体
     * @return 保存后的对话实体
     * @throws IllegalArgumentException 当 conversationId 为空时抛出
     */
    public AgentConversation createConversation(AgentConversation conversation) {
        // 业务规则校验：conversationId 为必填字段
        if (conversation.getConversationId() == null || conversation.getConversationId().isEmpty()) {
            throw new IllegalArgumentException("conversationId is required");
        }
        // 如果未提供回复，调用模型网关生成
        if (conversation.getAgentReply() == null || conversation.getAgentReply().isEmpty()) {
            String intent = conversation.getIntent() != null ? conversation.getIntent() : "default";
            String reply = modelGatewayService.ruleBasedResponse(intent);
            conversation.setAgentReply(reply);
        }
        conversation.setCreatedAt(LocalDateTime.now());
        return agentConversationRepository.save(conversation);
    }

    /**
     * 根据对话标识查询单条对话。
     *
     * @param conversationId 对话唯一标识
     * @return 对话实体；不存在时返回 {@code null}
     */
    public AgentConversation getConversation(String conversationId) {
        return agentConversationRepository.findByConversationId(conversationId);
    }

    /**
     * 查询指定老人的全部对话记录。
     *
     * @param elderId 老人唯一标识
     * @return 对话列表，不会为 {@code null}
     */
    public List<AgentConversation> listByElder(String elderId) {
        return agentConversationRepository.findByElderId(elderId);
    }

    /**
     * 按老人与代理类型组合查询对话记录。
     *
     * @param elderId   老人唯一标识
     * @param agentType 代理类型
     * @return 符合条件的对话列表，不会为 {@code null}
     */
    public List<AgentConversation> listByElderAndAgentType(String elderId, String agentType) {
        return agentConversationRepository.findByElderIdAndAgentType(elderId, agentType);
    }
}
