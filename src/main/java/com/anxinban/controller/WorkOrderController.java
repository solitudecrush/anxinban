package com.anxinban.controller;

/**
 * WorkOrder REST 控制器，提供 WorkOrder 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.PageResult;
import com.anxinban.dto.WorkOrderDto;
import com.anxinban.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/work-order")
public class WorkOrderController {
    /** 排序 */

    private final WorkOrderService workOrderService;

    @Autowired
    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping("/list")
    public ApiResponse<PageResult<WorkOrderDto>> listOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String elderName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResult<WorkOrderDto> result = workOrderService.listOrders(keyword, elderName, status, page, pageSize);
        return ApiResponse.success(result);
    }

    /**
     * 查询接口，处理 GET /{orderId} 请求。
     *
     * @param orderId orderId 参数
     * @return 处理结果
     */
    @GetMapping("/{orderId}")
    public ApiResponse<WorkOrderDto> getOrder(@PathVariable String orderId) {
        WorkOrderDto dto = workOrderService.getOrder(orderId);
        if (dto == null) {
            return ApiResponse.error(404, "工单不存在");
        }
        return ApiResponse.success(dto);
    }

    /**
     * 新增接口，处理 POST  请求。
     *
     * @param dto dto 参数
     * @return 处理结果
     */
    @PostMapping
    public ApiResponse<WorkOrderDto> createOrder(@RequestBody WorkOrderDto dto) {
        return ApiResponse.created(workOrderService.createOrder(dto));
    }

    /**
     * 更新接口，处理 PUT /{orderId}/status 请求。
     *
     * @param orderId orderId 参数
     * @param request request 参数
     * @return 处理结果
     */
    @PutMapping("/{orderId}/status")
    public ApiResponse<WorkOrderDto> updateStatus(@PathVariable String orderId, @RequestBody StatusRequest request) {
        WorkOrderDto updated = workOrderService.updateOrderStatus(orderId, request.getStatus());
        if (updated == null) {
            return ApiResponse.error(404, "工单不存在");
        }
        return ApiResponse.success(updated);
    }

    /**
     * 处理 PUT /{orderId}/assign 请求。
     *
     * @param orderId orderId 参数
     * @param request request 参数
     * @return 处理结果
     */
    @PutMapping("/{orderId}/assign")
    public ApiResponse<WorkOrderDto> assignHandler(@PathVariable String orderId, @RequestBody AssignRequest request) {
        WorkOrderDto updated = workOrderService.assignHandler(orderId, request.getHandlerId(), request.getHandlerName());
        if (updated == null) {
            return ApiResponse.error(404, "工单不存在");
        }
        return ApiResponse.success(updated);
    }

    public static class StatusRequest {
        private String status;
            /**
             * 获取状态标识。
             *
             * @return 状态标识
             */
        public String getStatus() { return status; }
            /**
             * 设置状态标识。
             *
             * @param status 状态标识
             */
        public void setStatus(String status) { this.status = status; }
    }

    public static class AssignRequest {
        private String handlerId;
        private String handlerName;
            /**
             * 获取唯一标识，主键。
             *
             * @return 唯一标识，主键
             */
        public String getHandlerId() { return handlerId; }
            /**
             * 设置唯一标识，主键。
             *
             * @param handlerId 唯一标识，主键
             */
        public void setHandlerId(String handlerId) { this.handlerId = handlerId; }
            /**
             * 获取名称。
             *
             * @return 名称
             */
        public String getHandlerName() { return handlerName; }
            /**
             * 设置名称。
             *
             * @param handlerName 名称
             */
        public void setHandlerName(String handlerName) { this.handlerName = handlerName; }
    }
}
