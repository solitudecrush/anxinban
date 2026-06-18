package com.anxinban.controller;
import com.anxinban.dto.ApiResponse;
import com.anxinban.service.AgentContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 智能体上下文控制器。
 *
 * <p>功能说明：为前端提供老人相关智能体（Agent）上下文数据查询接口，
 * 支撑 AI 助手、云管家等模块根据老人 ID 获取聚合后的上下文信息。</p>
 *
 * <p>对应前端模块：AI 助手 / 智能管家 / 老人详情页智能推荐。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/agent")
public class AgentController {
    private final AgentContextService agentContextService;

    /**
     * 构造方法，注入智能体上下文服务。
     *
     * @param agentContextService 智能体上下文服务实例
     */
    @Autowired
    public AgentController(AgentContextService agentContextService) {
        this.agentContextService = agentContextService;
    }
    /**
     * 查询接口，处理 GET /context 请求。
     *
     * @param elderId elderId 参数
     * @return 处理结果
     */
    @GetMapping("/context")
    public ApiResponse<Map<String, Object>> getAgentContext(@RequestParam String elderId) {
        // 调用服务层获取聚合后的智能体上下文数据
        Map<String, Object> context = agentContextService.getAgentContext(elderId);
        return ApiResponse.success(context);
    }
}
