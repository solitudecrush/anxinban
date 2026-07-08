package com.anxinban.controller;
import com.anxinban.dto.AgentInfoDto;
import com.anxinban.dto.AlarmDto;
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.InterventionDto;
import com.anxinban.dto.InterventionResultDto;
import com.anxinban.dto.PageResult;
import com.anxinban.entity.AgentConversation;
import com.anxinban.entity.AiAdvice;
import com.anxinban.service.AgentConversationService;
import com.anxinban.service.AiAdviceService;
import com.anxinban.service.CloudAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 云侧智能体（Cloud Agent）控制器。
 *
 * <p>功能说明：为云端 AI 智能体提供注册、状态上报、告警同步、配置下发、
 * 干预任务查询与结果上报、对话记录以及 AI 建议创建等接口。</p>
 *
 * <p>对应前端模块：云管家 / AI 助手 / 智能体管理后台。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/cloud-agent")
public class CloudAgentController {
    private final CloudAgentService cloudAgentService;
    private final AgentConversationService agentConversationService;
    private final AiAdviceService aiAdviceService;

    /**
     * 构造方法，注入相关服务。
     *
     * @param cloudAgentService         云智能体服务
     * @param agentConversationService  智能体对话服务
     * @param aiAdviceService           AI 建议服务
     */
    @Autowired
    public CloudAgentController(CloudAgentService cloudAgentService,
                                AgentConversationService agentConversationService,
                                AiAdviceService aiAdviceService) {
        this.cloudAgentService = cloudAgentService;
        this.agentConversationService = agentConversationService;
        this.aiAdviceService = aiAdviceService;
    }
    /**
     * 注册接口，处理 POST /register 请求。
     *
     * @param request request 参数
     * @return 处理结果
     */
    @PostMapping("/register")
    public ApiResponse<AgentInfoDto> registerAgent(@RequestBody AgentInfoDto request) {
        AgentInfoDto saved = cloudAgentService.registerAgent(request);
        return ApiResponse.created(saved);
    }
    /**
     * 处理 POST /report 请求。
     *
     * @param request request 参数
     * @return 处理结果
     */
    @PostMapping("/report")
    public ApiResponse<Void> reportStatus(@RequestBody AgentInfoDto request) {
        cloudAgentService.reportStatus(request);
        return ApiResponse.success(null);
    }
    /**
     * 处理 POST /alarm 请求。
     *
     * @param alarm alarm 参数
     * @return 处理结果
     */
    @PostMapping("/alarm")
    public ApiResponse<AlarmDto> syncAlarm(@RequestBody AlarmDto alarm) {
        AlarmDto saved = cloudAgentService.syncAlarm(alarm);
        return ApiResponse.created(saved);
    }
    /**
     * 查询接口，处理 GET /{agentId}/config 请求。
     *
     * @param agentId agentId 参数
     * @return 处理结果
     */
    @GetMapping("/{agentId}/config")
    public ApiResponse<Map<String, Object>> getConfig(@PathVariable String agentId) {
        return ApiResponse.success(cloudAgentService.getConfig(agentId));
    }
    /**
     * 查询接口，处理 GET /{agentId}/interventions 请求。
     *
     * @param agentId agentId 参数
     * @return 处理结果
     */
    @GetMapping("/{agentId}/interventions")
    public ApiResponse<List<InterventionDto>> getInterventions(@PathVariable String agentId) {
        return ApiResponse.success(cloudAgentService.getInterventions(agentId));
    }
    /**
     * 处理 POST /{agentId}/intervention-result 请求。
     *
     * @param agentId agentId 参数
     * @param request request 参数
     * @return 处理结果
     */
    @PostMapping("/{agentId}/intervention-result")
    public ApiResponse<Void> reportInterventionResult(@PathVariable String agentId, @RequestBody InterventionResultDto request) {
        cloudAgentService.reportInterventionResult(request);
        return ApiResponse.success(null);
    }
    /**
     * 处理 POST /chat 请求。
     *
     * @param request request 参数
     * @return 处理结果
     */
    @PostMapping("/chat")
    public ApiResponse<AgentConversation> chat(@RequestBody AgentConversation request) {
        AgentConversation saved = agentConversationService.createConversation(request);
        return ApiResponse.created(saved);
    }

    /**
     * 查询对话记录列表（支持按老人筛选和分页）。
     *
     * @param elderId  可选，按老人ID筛选
     * @param page     页码，从 1 开始，默认 1
     * @param pageSize 每页大小，默认 20
     * @return 分页对话记录列表
     */
    @GetMapping("/conversations")
    public ApiResponse<PageResult<AgentConversation>> listConversations(
            @RequestParam(required = false) String elderId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<AgentConversation> all;
        if (elderId != null && !elderId.isEmpty()) {
            all = agentConversationService.listByElder(elderId);
        } else {
            all = agentConversationService.listAll();
        }
        // 按创建时间倒序
        all.sort((a, b) -> {
            if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });
        int total = all.size();
        int from = Math.max((page - 1) * pageSize, 0);
        int to = Math.min(from + pageSize, total);
        List<AgentConversation> paginated = from < total
                ? new ArrayList<>(all.subList(from, to))
                : new ArrayList<>();
        PageResult<AgentConversation> result = new PageResult<>(paginated, total, page, pageSize);
        return ApiResponse.success(result);
    }

    /**
     * 查询单条对话记录详情。
     *
     * @param conversationId 对话ID
     * @return 对话记录
     */
    @GetMapping("/conversations/{conversationId}")
    public ApiResponse<AgentConversation> getConversation(@PathVariable String conversationId) {
        AgentConversation conv = agentConversationService.getConversation(conversationId);
        if (conv == null) {
            return ApiResponse.error(404, "对话记录不存在: " + conversationId);
        }
        return ApiResponse.success(conv);
    }
    /**
     * 新增接口，处理 POST /advice 请求。
     *
     * @param request request 参数
     * @return 处理结果
     */
    @PostMapping("/advice")
    public ApiResponse<AiAdvice> createAdvice(@RequestBody AiAdvice request) {
        AiAdvice saved = aiAdviceService.createAdvice(request);
        return ApiResponse.created(saved);
    }
}
