package com.anxinban.controller;

/**
 * MonitorRequest REST 控制器，提供 MonitorRequest 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.MonitorRequestDto;
import com.anxinban.mapper.FamilyUserRepository;
import com.anxinban.service.MonitorRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor-request")
public class MonitorRequestController {
    private final MonitorRequestService monitorRequestService;
    /** 数据访问仓库，用于持久化操作 */
    private final FamilyUserRepository familyUserRepository;

    @Autowired
    public MonitorRequestController(MonitorRequestService monitorRequestService, FamilyUserRepository familyUserRepository) {
        this.monitorRequestService = monitorRequestService;
        this.familyUserRepository = familyUserRepository;
    }

    /**
     * 新增接口，处理 POST  请求。
     *
     * @param dto dto 参数
     * @return 处理结果
     */
    @PostMapping
    public ApiResponse<MonitorRequestDto> createRequest(@RequestBody MonitorRequestDto dto) {
        return ApiResponse.created(monitorRequestService.createRequest(dto));
    }

    /**
     * 查询接口，处理 GET /list/family 请求。
     *
     * @param familyId familyId 参数
     * @return 处理结果
     */
    @GetMapping("/list/family")
    public ApiResponse<List<MonitorRequestDto>> getFamilyRequests(@RequestParam String familyId) {
        var family = familyUserRepository.findByFamilyId(familyId);
        if (family == null || family.getElderId() == null) {
            return ApiResponse.error(404, "未绑定老人");
        }
        return ApiResponse.success(monitorRequestService.listRequestsByElder(family.getElderId()));
    }

    /**
     * 查询接口，处理 GET /list/staff 请求。
     *
     * @param staffId staffId 参数
     * @return 处理结果
     */
    @GetMapping("/list/staff")
    public ApiResponse<List<MonitorRequestDto>> getStaffRequests(@RequestParam String staffId) {
        return ApiResponse.success(monitorRequestService.listRequestsByStaff(staffId));
    }

    /**
     * 处理 POST /{requestId}/approve 请求。
     *
     * @param requestId requestId 参数
     * @return 处理结果
     */
    @PostMapping("/{requestId}/approve")
    public ApiResponse<MonitorRequestDto> approveRequest(@PathVariable String requestId) {
        MonitorRequestDto dto = monitorRequestService.approveRequest(requestId);
        if (dto == null) {
            return ApiResponse.error(404, "申请不存在");
        }
        return ApiResponse.success(dto);
    }

    /**
     * 处理 POST /{requestId}/reject 请求。
     *
     * @param requestId requestId 参数
     * @return 处理结果
     */
    @PostMapping("/{requestId}/reject")
    public ApiResponse<MonitorRequestDto> rejectRequest(@PathVariable String requestId) {
        MonitorRequestDto dto = monitorRequestService.rejectRequest(requestId);
        if (dto == null) {
            return ApiResponse.error(404, "申请不存在");
        }
        return ApiResponse.success(dto);
    }

    /**
     * 处理 POST /{requestId}/revoke 请求。
     *
     * @param requestId requestId 参数
     * @return 处理结果
     */
    @PostMapping("/{requestId}/revoke")
    public ApiResponse<MonitorRequestDto> revokeRequest(@PathVariable String requestId) {
        MonitorRequestDto dto = monitorRequestService.revokePermission(requestId);
        if (dto == null) {
            return ApiResponse.error(404, "申请不存在");
        }
        return ApiResponse.success(dto);
    }

    /**
     * 查询接口，处理 GET /{requestId}/result 请求。
     *
     * @param requestId requestId 参数
     * @return 处理结果
     */
    @GetMapping("/{requestId}/result")
    public ApiResponse<MonitorRequestDto> getRequestResult(@PathVariable String requestId) {
        MonitorRequestDto dto = monitorRequestService.getRequest(requestId);
        if (dto == null) {
            return ApiResponse.error(404, "申请不存在");
        }
        return ApiResponse.success(dto);
    }

    /**
     * 处理 GET /check 请求。
     *
     * @param elderId elderId 参数
     * @param staffId staffId 参数
     * @return 处理结果
     */
    @GetMapping("/check")
    public ApiResponse<Map<String, Boolean>> checkPermission(@RequestParam String elderId, @RequestParam(required = false) String staffId) {
        boolean hasPermission = monitorRequestService.checkMonitorPermission(elderId, staffId);
        return ApiResponse.success(Map.of("hasPermission", hasPermission));
    }
}
