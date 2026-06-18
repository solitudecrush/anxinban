package com.anxinban.controller;

/**
 * Elder REST 控制器，提供 Elder 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.*;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.mapper.FamilyUserRepository;
import com.anxinban.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/elder")
public class ElderController {
    private final ElderService elderService;
    private final HealthService healthService;
    private final DeviceService deviceService;
    private final AlarmService alarmService;
    private final WorkOrderService workOrderService;
    private final MonitorRequestService monitorRequestService;
    private final ElderUserRepository elderUserRepository;
    /** 数据访问仓库，用于持久化操作 */
    private final FamilyUserRepository familyUserRepository;

    @Autowired
    public ElderController(ElderService elderService, HealthService healthService, DeviceService deviceService,
                           AlarmService alarmService, WorkOrderService workOrderService, MonitorRequestService monitorRequestService,
                           ElderUserRepository elderUserRepository, FamilyUserRepository familyUserRepository) {
        this.elderService = elderService;
        this.healthService = healthService;
        this.deviceService = deviceService;
        this.alarmService = alarmService;
        this.workOrderService = workOrderService;
        this.monitorRequestService = monitorRequestService;
        this.elderUserRepository = elderUserRepository;
        this.familyUserRepository = familyUserRepository;
    }

    /**
     * 新增接口，处理 POST  请求。
     *
     * @param elder elder 参数
     * @return 处理结果
     */
    @PostMapping
    public ApiResponse<ElderDto> createElder(@RequestBody ElderDto elder) {
        return ApiResponse.created(elderService.createElder(elder));
    }

    /**
     * 查询接口，处理 GET /{elderId} 请求。
     *
     * @param elderId elderId 参数
     * @return 处理结果
     */
    @GetMapping("/{elderId}")
    public ApiResponse<ElderDto> getElder(@PathVariable String elderId) {
        ElderDto elder = elderService.getElder(elderId);
        if (elder == null) {
            return ApiResponse.error(404, "老人不存在");
        }
        return ApiResponse.success(elder);
    }

    /**
     * 查询接口，处理 GET /detail/{elderId} 请求。
     *
     * @param elderId elderId 参数
     * @return 处理结果
     */
    @GetMapping("/detail/{elderId}")
    public ApiResponse<Map<String, Object>> getElderDetail(@PathVariable String elderId) {
        ElderDto elder = elderService.getElder(elderId);
        if (elder == null) {
            return ApiResponse.error(404, "老人不存在");
        }
        Map<String, Object> detail = new java.util.HashMap<>();
        detail.put("basicInfo", elder);
        detail.put("healthData", healthService.getLatestHealth(elderId));
        detail.put("devices", deviceService.listDevicesByElder(elderId));
        detail.put("alarms", alarmService.listAlarms(elderId, null, null, null, null, null, 1, 10).getList());
        detail.put("workOrders", workOrderService.listOrdersByElder(elderId));
        return ApiResponse.success(detail);
    }

    @GetMapping("/list")
    public ApiResponse<PageResult<ElderDto>> listElders(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String building,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) String healthStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResult<ElderDto> result = elderService.listElders(name, building, roomNumber, healthStatus, page, pageSize);
        return ApiResponse.success(result);
    }

    /**
     * 查询接口，处理 GET /bound 请求。
     *
     * @param familyId familyId 参数
     * @return 处理结果
     */
    @GetMapping("/bound")
    public ApiResponse<ElderDto> getBoundElder(@RequestParam String familyId) {
        var family = familyUserRepository.findByFamilyId(familyId);
        if (family == null || family.getElderId() == null) {
            return ApiResponse.error(404, "未绑定老人");
        }
        ElderDto elder = elderService.getElder(family.getElderId());
        if (elder == null) {
            return ApiResponse.error(404, "老人不存在");
        }
        return ApiResponse.success(elder);
    }

    /**
     * 更新接口，处理 PUT /{elderId} 请求。
     *
     * @param elderId elderId 参数
     * @param elder elder 参数
     * @return 处理结果
     */
    @PutMapping("/{elderId}")
    public ApiResponse<ElderDto> updateElder(@PathVariable String elderId, @RequestBody ElderDto elder) {
        ElderDto updated = elderService.updateElder(elderId, elder);
        if (updated == null) {
            return ApiResponse.error(404, "老人不存在");
        }
        return ApiResponse.success(updated);
    }

    /**
     * 删除接口，处理 DELETE /{elderId} 请求。
     *
     * @param elderId elderId 参数
     * @return 处理结果
     */
    @DeleteMapping("/{elderId}")
    public ApiResponse<Void> deleteElder(@PathVariable String elderId) {
        boolean removed = elderService.deleteElder(elderId);
        if (!removed) {
            return ApiResponse.error(404, "老人不存在");
        }
        return ApiResponse.success(null);
    }

    /**
     * 查询接口，处理 GET /{elderId}/devices 请求。
     *
     * @param elderId elderId 参数
     * @return 处理结果
     */
    @GetMapping("/{elderId}/devices")
    public ApiResponse<List<DeviceDto>> getElderDevices(@PathVariable String elderId) {
        return ApiResponse.success(deviceService.listDevicesByElder(elderId));
    }

    /**
     * 查询接口，处理 GET /{elderId}/health/realtime 请求。
     *
     * @param elderId elderId 参数
     * @return 处理结果
     */
    @GetMapping("/{elderId}/health/realtime")
    public ApiResponse<HealthLatestDto> getElderRealtimeHealth(@PathVariable String elderId) {
        HealthLatestDto dto = healthService.getLatestHealth(elderId);
        return ApiResponse.success(dto);
    }

    @GetMapping("/{elderId}/health/history")
    public ApiResponse<HealthTrendDto> getElderHealthHistory(
            @PathVariable String elderId,
            @RequestParam String type,
            @RequestParam(defaultValue = "week") String range) {
        HealthTrendDto dto = healthService.getHealthTrend(elderId, type, range);
        return ApiResponse.success(dto);
    }

    /**
     * 查询接口，处理 GET /{elderId}/camera-stream 请求。
     *
     * @param elderId elderId 参数
     * @param staffId staffId 参数
     * @return 处理结果
     */
    @GetMapping("/{elderId}/camera-stream")
    public ApiResponse<Map<String, String>> getCameraStream(@PathVariable String elderId, @RequestParam(required = false) String staffId) {
        boolean hasPermission = monitorRequestService.checkMonitorPermission(elderId, staffId);
        if (!hasPermission) {
            return ApiResponse.error(403, "无监控查看权限");
        }
        String streamUrl = "rtsp://example.com/stream/" + elderId;
        return ApiResponse.success(Map.of("streamUrl", streamUrl));
    }
}
