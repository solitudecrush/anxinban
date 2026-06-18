package com.anxinban.service;
import com.anxinban.entity.AgentIntentLog;
import com.anxinban.mapper.AgentIntentLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 智能代理意图日志服务。
 *
 * <p>负责持久化与查询老人与 AI Agent 交互过程中识别出的意图日志，
 * 包括保存意图、按意图标识查询以及按老人查询最近意图。</p>
 *
 * <p>事务边界：当前未显式开启事务，单次 Repository 保存由 Spring Data JPA
 * 隐式事务保证。</p>
 *
 * <p>依赖的 Mapper/Repository：</p>
 * <ul>
 *     <li>{@link AgentIntentLogRepository} — 意图日志的持久化与查询</li>
 * </ul>
 */
@Service
public class AgentIntentLogService {
    private final AgentIntentLogRepository agentIntentLogRepository;

    /**
     * 构造器注入意图日志仓库。
     *
     * @param agentIntentLogRepository 意图日志仓库
     */
    @Autowired
    public AgentIntentLogService(AgentIntentLogRepository agentIntentLogRepository) {
        this.agentIntentLogRepository = agentIntentLogRepository;
    }

    /**
     * 保存一条意图识别日志。
     *
     * <p>业务规则：</p>
     * <ul>
     *     <li>intentId 必须非空，否则抛出 {@link IllegalArgumentException}</li>
     *     <li>自动设置 createdAt 为当前时间</li>
     * </ul>
     *
     * @param log 待保存的意图日志
     * @return 保存后的意图日志
     * @throws IllegalArgumentException 当 intentId 为空时抛出
     */
    public AgentIntentLog saveIntentLog(AgentIntentLog log) {
        // 业务规则校验：intentId 为必填字段
        if (log.getIntentId() == null || log.getIntentId().isEmpty()) {
            throw new IllegalArgumentException("intentId is required");
        }
        log.setCreatedAt(LocalDateTime.now());
        return agentIntentLogRepository.save(log);
    }

    /**
     * 根据意图标识查询单条意图日志。
     *
     * @param intentId 意图唯一标识
     * @return 意图日志；不存在时返回 {@code null}
     */
    public AgentIntentLog getIntentLog(String intentId) {
        return agentIntentLogRepository.findByIntentId(intentId);
    }

    /**
     * 查询指定老人的全部意图日志。
     *
     * @param elderId 老人唯一标识
     * @return 意图日志列表，不会为 {@code null}
     */
    public List<AgentIntentLog> listByElder(String elderId) {
        return agentIntentLogRepository.findByElderId(elderId);
    }
}
