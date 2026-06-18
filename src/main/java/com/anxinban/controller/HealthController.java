package com.anxinban.controller;

/**
 * Health REST 控制器，提供 Health 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.HealthAnalysisDto;
import com.anxinban.dto.HealthLatestDto;
import com.anxinban.dto.HealthTrendDto;
import com.anxinban.dto.PageResult;
import com.anxinban.dto.AlarmDto;
import com.anxinban.service.HealthService;
import com.anxinban.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    private final HealthService healthService;
    /** 业务服务实例 */
    private final AlarmService alarmService;

    @Autowired
    public HealthController(HealthService healthService, AlarmService alarmService) {
        this.healthService = healthService;
        this.alarmService = alarmService;
    }

    /**
     * 查询接口，处理 GET /latest/{elderId} 请求。
     *
     * @param elderId elderId 参数
     * @return 处理结果
     */
    @GetMapping("/latest/{elderId}")
    public ApiResponse<HealthLatestDto> getLatestHealth(@PathVariable String elderId) {
        HealthLatestDto dto = healthService.getLatestHealth(elderId);
        return ApiResponse.success(dto);
    }

    @GetMapping("/trend/{elderId}")
    public ApiResponse<HealthTrendDto> getHealthTrend(
            @PathVariable String elderId,
            @RequestParam String type,
            @RequestParam(defaultValue = "week") String period) {
        HealthTrendDto dto = healthService.getHealthTrend(elderId, type, period);
        return ApiResponse.success(dto);
    }

    @GetMapping("/analysis/{elderId}")
    public ApiResponse<HealthAnalysisDto> getHealthAnalysis(
            @PathVariable String elderId,
            @RequestParam(defaultValue = "week") String period) {
        HealthAnalysisDto dto = healthService.getHealthAnalysis(elderId, period);
        return ApiResponse.success(dto);
    }

    @GetMapping("/abnormal/{elderId}")
    public ApiResponse<PageResult<AlarmDto>> getHealthAbnormalAlarms(
            @PathVariable String elderId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResult<AlarmDto> result = alarmService.listHealthAbnormalAlarms(elderId, status, page, pageSize);
        return ApiResponse.success(result);
    }
}
