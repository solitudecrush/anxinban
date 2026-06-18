package com.anxinban.controller;

/**
 * Dashboard REST 控制器，提供 Dashboard 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.DashboardStatsDto;
import com.anxinban.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    /** 字段含义待补充 */

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 查询接口，处理 GET /stats 请求。
     * @return 处理结果
     */
    @GetMapping("/stats")
    public ApiResponse<DashboardStatsDto> getStats() {
        return ApiResponse.success(dashboardService.getStats());
    }

    /**
     * 查询接口，处理 GET /buildings 请求。
     * @return 处理结果
     */
    @GetMapping("/buildings")
    public ApiResponse<List<String>> getBuildings() {
        List<String> buildings = List.of("1号楼", "2号楼", "3号楼", "5号楼", "6号楼");
        return ApiResponse.success(buildings);
    }
}
