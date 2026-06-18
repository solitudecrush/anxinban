package com.anxinban.controller;

/**
 * ServiceRequest REST 控制器，提供 ServiceRequest 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.PageResult;
import com.anxinban.dto.ServiceRequestDto;
import com.anxinban.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-request")
public class ServiceRequestController {
    /** 字段含义待补充 */

    private final ServiceRequestService serviceRequestService;

    @Autowired
    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    /**
     * 处理 POST  请求。
     *
     * @param dto dto 参数
     * @return 处理结果
     */
    @PostMapping
    public ApiResponse<ServiceRequestDto> submitRequest(@RequestBody ServiceRequestDto dto) {
        return ApiResponse.created(serviceRequestService.createRequest(dto));
    }

    /**
     * 查询接口，处理 GET /my-list 请求。
     *
     * @param familyId familyId 参数
     * @return 处理结果
     */
    @GetMapping("/my-list")
    public ApiResponse<List<ServiceRequestDto>> getMyRequests(@RequestParam String familyId) {
        return ApiResponse.success(serviceRequestService.listRequestsByFamily(familyId));
    }

    @GetMapping("/list")
    public ApiResponse<PageResult<ServiceRequestDto>> getRequestList(
            @RequestParam(required = false) String requestType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResult<ServiceRequestDto> result = serviceRequestService.listRequests(requestType, status, page, pageSize);
        return ApiResponse.success(result);
    }

    /**
     * 查询接口，处理 GET /{requestId}/status 请求。
     *
     * @param requestId requestId 参数
     * @return 处理结果
     */
    @GetMapping("/{requestId}/status")
    public ApiResponse<ServiceRequestDto> getRequestStatus(@PathVariable String requestId) {
        ServiceRequestDto dto = serviceRequestService.getRequest(requestId);
        if (dto == null) {
            return ApiResponse.error(404, "申请不存在");
        }
        return ApiResponse.success(dto);
    }

    /**
     * 处理 POST /{requestId}/convert 请求。
     *
     * @param requestId requestId 参数
     * @param request request 参数
     * @return 处理结果
     */
    @PostMapping("/{requestId}/convert")
    public ApiResponse<ServiceRequestDto> convertToOrder(@PathVariable String requestId, @RequestBody ConvertRequest request) {
        ServiceRequestDto dto = serviceRequestService.convertToOrder(requestId, request.getOrderId());
        if (dto == null) {
            return ApiResponse.error(404, "申请不存在");
        }
        return ApiResponse.success(dto);
    }

    /**
     * 处理 POST /{requestId}/reject 请求。
     *
     * @param requestId requestId 参数
     * @param request request 参数
     * @return 处理结果
     */
    @PostMapping("/{requestId}/reject")
    public ApiResponse<ServiceRequestDto> rejectRequest(@PathVariable String requestId, @RequestBody RejectRequest request) {
        ServiceRequestDto dto = serviceRequestService.rejectRequest(requestId, request.getReason());
        if (dto == null) {
            return ApiResponse.error(404, "申请不存在");
        }
        return ApiResponse.success(dto);
    }

    public static class ConvertRequest {
        private String orderId;
            /**
             * 获取排序。
             *
             * @return 排序
             */
        public String getOrderId() { return orderId; }
            /**
             * 设置排序。
             *
             * @param orderId 排序
             */
        public void setOrderId(String orderId) { this.orderId = orderId; }
    }

    public static class RejectRequest {
        private String reason;
            /**
             * 获取原因。
             *
             * @return 原因
             */
        public String getReason() { return reason; }
            /**
             * 设置原因。
             *
             * @param reason 原因
             */
        public void setReason(String reason) { this.reason = reason; }
    }
}
