package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.HealthDashboardDto;
import com.anxinban.service.HealthDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 健康数据看板 REST 控制器。
 *
 * <p>为前端"个人中心-健康数据看板"提供聚合接口，
 * 一次请求返回睡眠分析、陪伴记录、音乐疗法、物品寻找四项数据。</p>
 *
 * <p>路由：GET /api/health-dashboard?elderId=xxx</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/health-dashboard")
public class HealthDashboardController {

    private final HealthDashboardService healthDashboardService;

    @Autowired
    public HealthDashboardController(HealthDashboardService healthDashboardService) {
        this.healthDashboardService = healthDashboardService;
    }

    /**
     * 获取老人健康数据看板。
     *
     * @param elderId 老人 ID，不传默认 "1"
     * @return 包含四个面板的完整看板数据
     */
    @GetMapping
    public ApiResponse<HealthDashboardDto> getDashboard(
            @RequestParam(defaultValue = "1") String elderId) {

        HealthDashboardDto dto = healthDashboardService.buildDashboard(elderId);
        return ApiResponse.success(dto);
    }
}
