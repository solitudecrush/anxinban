package com.anxinban.service;
import com.anxinban.dto.HealthLatestDto;
import com.anxinban.entity.AgentConversation;
import com.anxinban.entity.AgentIntentLog;
import com.anxinban.entity.AlarmEvent;
import com.anxinban.entity.MusicIntervention;
import com.anxinban.mapper.AgentConversationRepository;
import com.anxinban.mapper.AgentIntentLogRepository;
import com.anxinban.mapper.AlarmEventRepository;
import com.anxinban.mapper.MusicInterventionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 智能代理上下文服务。
 *
 * <p>负责为 AI Agent（语音/云/本地代理）组装指定老人的全景上下文信息，
 * 供模型网关生成更贴合老人当前状态的回复与决策。上下文数据覆盖最新健康指标、
 * 最近告警、最近音乐干预、最近对话记录与最近识别意图。</p>
 *
 * <p>事务边界：本类为只读聚合服务，不直接修改数据库；每条数据分别查询，
 * 建议调用方在需要强一致性时开启只读事务。</p>
 *
 * <p>依赖的 Mapper/Repository：</p>
 * <ul>
 *     <li>{@link AlarmEventRepository} — 告警事件查询</li>
 *     <li>{@link MusicInterventionRepository} — 音乐干预记录查询</li>
 *     <li>{@link AgentConversationRepository} — 代理对话记录查询</li>
 *     <li>{@link AgentIntentLogRepository} — 意图识别日志查询</li>
 * </ul>
 *
 * <p>依赖的 Service：</p>
 * <ul>
 *     <li>{@link HealthService} — 获取老人最新健康数据</li>
 * </ul>
 */
@Service
public class AgentContextService {
    private final HealthService healthService;
    private final AlarmEventRepository alarmEventRepository;
    private final MusicInterventionRepository musicInterventionRepository;
    private final AgentConversationRepository agentConversationRepository;
    private final AgentIntentLogRepository agentIntentLogRepository;

    /**
     * 构造器注入所需依赖。
     *
     * @param healthService                健康数据服务
     * @param alarmEventRepository         告警事件仓库
     * @param musicInterventionRepository  音乐干预仓库
     * @param agentConversationRepository  代理对话仓库
     * @param agentIntentLogRepository     意图日志仓库
     */
    @Autowired
    public AgentContextService(HealthService healthService, AlarmEventRepository alarmEventRepository,
                               MusicInterventionRepository musicInterventionRepository,
                               AgentConversationRepository agentConversationRepository,
                               AgentIntentLogRepository agentIntentLogRepository) {
        this.healthService = healthService;
        this.alarmEventRepository = alarmEventRepository;
        this.musicInterventionRepository = musicInterventionRepository;
        this.agentConversationRepository = agentConversationRepository;
        this.agentIntentLogRepository = agentIntentLogRepository;
    }

    /**
     * 获取指定老人的 Agent 上下文。
     *
     * <p>依次查询并组装如下信息：</p>
     * <ul>
     *     <li>elderId：目标老人标识</li>
     *     <li>recentHealth：最新非空健康指标（心率、体温、收缩压、舒张压）</li>
     *     <li>recentAlarms：最近 3 条告警事件（按创建时间降序）</li>
     *     <li>recentInterventions：最近 3 条音乐干预（按创建时间降序）</li>
     *     <li>recentConversations：最近 3 条代理对话（按创建时间降序）</li>
     *     <li>recentIntents：最近 3 条识别意图（按创建时间降序）</li>
     * </ul>
     *
     * @param elderId 老人唯一标识，不可为空
     * @return 包含多维度上下文的 Map，key 为字符串，value 视具体项而定
     */
    public Map<String, Object> getAgentContext(String elderId) {
        Map<String, Object> context = new HashMap<>();
        // 将老人标识放入上下文，便于模型识别当前服务对象
        context.put("elderId", elderId);

        // 查询最新健康数据，仅保留非空字段以避免上下文冗余
        HealthLatestDto latest = healthService.getLatestHealth(elderId);
        Map<String, Object> recentHealth = new HashMap<>();
        if (latest.getHeartRate() != null) recentHealth.put("heartRate", latest.getHeartRate());
        if (latest.getTemperature() != null) recentHealth.put("temperature", latest.getTemperature());
        if (latest.getSystolic() != null) recentHealth.put("systolic", latest.getSystolic());
        if (latest.getDiastolic() != null) recentHealth.put("diastolic", latest.getDiastolic());
        context.put("recentHealth", recentHealth);

        // 查询该老人最近 3 条告警，按创建时间倒排
        List<AlarmEvent> alarms = alarmEventRepository.findByElderId(elderId).stream()
                .sorted(Comparator.comparing(AlarmEvent::getCreatedAt).reversed())
                .limit(3)
                .collect(Collectors.toList());
        context.put("recentAlarms", alarms);

        // 查询该老人最近 3 条音乐干预记录，按创建时间倒排
        List<MusicIntervention> interventions = musicInterventionRepository.findByElderId(elderId).stream()
                .sorted(Comparator.comparing(MusicIntervention::getCreatedAt).reversed())
                .limit(3)
                .collect(Collectors.toList());
        context.put("recentInterventions", interventions);

        // 查询该老人最近 3 条代理对话，按创建时间倒排
        List<AgentConversation> conversations = agentConversationRepository.findByElderId(elderId).stream()
                .sorted(Comparator.comparing(AgentConversation::getCreatedAt).reversed())
                .limit(3)
                .collect(Collectors.toList());
        context.put("recentConversations", conversations);

        // 查询该老人最近 3 条意图识别日志，按创建时间倒排
        List<AgentIntentLog> intents = agentIntentLogRepository.findByElderId(elderId).stream()
                .sorted(Comparator.comparing(AgentIntentLog::getCreatedAt).reversed())
                .limit(3)
                .collect(Collectors.toList());
        context.put("recentIntents", intents);

        return context;
    }
}
