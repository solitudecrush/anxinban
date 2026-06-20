package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.PageResult;
import com.anxinban.entity.AlarmEvent;
import com.anxinban.entity.ElderUser;
import com.anxinban.entity.WorkOrder;
import com.anxinban.mapper.AlarmEventRepository;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.mapper.WorkOrderRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 比赛兼容层 - 工单兼容接口（复数路径 /api/work-orders）。
 *
 * <p>提供：</p>
 * <ul>
 *   <li>GET  /api/work-orders — 工单列表（兼容格式）</li>
 *   <li>POST /api/work-orders — 创建工单</li>
 *   <li>PUT  /api/work-orders/{work_order_id}/assign — 指派处理人</li>
 *   <li>PUT  /api/work-orders/{work_order_id}/complete — 完成工单</li>
 * </ul>
 *
 * <p>与原有 /api/work-order/* 共存，不冲突。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/work-orders")
public class WorkOrdersCompatController {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private ElderUserRepository elderUserRepository;

    @Autowired
    private AlarmEventRepository alarmEventRepository;

    /**
     * 工单列表兼容接口（支持分页，关联告警详情）。
     *
     * @param page     页码，从 1 开始
     * @param pageSize 每页大小
     * @return 分页工单列表（含关联告警信息）
     */
    @GetMapping
    public ApiResponse<PageResult<Map<String, Object>>> listWorkOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<WorkOrder> orders = workOrderRepository.findAll();
        // 按创建时间倒序
        orders.sort((a, b) -> {
            if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });

        List<Map<String, Object>> allItems = new ArrayList<>();
        for (WorkOrder wo : orders) {
            allItems.add(buildListItem(wo));
        }

        // 内存分页
        int total = allItems.size();
        int from = Math.max((page - 1) * pageSize, 0);
        int to = Math.min(from + pageSize, total);
        List<Map<String, Object>> paginated = from < total
                ? new ArrayList<>(allItems.subList(from, to))
                : new ArrayList<>();

        PageResult<Map<String, Object>> result = new PageResult<>(paginated, total, page, pageSize);
        return ApiResponse.success(result);
    }

    /**
     * 创建工单兼容接口。
     *
     * <p>接受前端 POST JSON，创建新工单。如果未提供 orderId 则自动生成。</p>
     *
     * @param body 创建请求体
     * @return 创建的工单
     */
    @PostMapping
    public ApiResponse<Map<String, Object>> createWorkOrder(@RequestBody CreateWorkOrderRequest body) {
        WorkOrder wo = new WorkOrder();
        wo.setOrderId("wo_" + UUID.randomUUID().toString().substring(0, 8));
        wo.setElderId(body.getElderId());
        wo.setType(body.getType() != null ? body.getType() : "健康关注");
        wo.setDescription(body.getDescription() != null ? body.getDescription() : "");
        wo.setStatus("待处理");
        wo.setCreatorId(body.getCreatorId() != null ? body.getCreatorId() : "system");
        wo.setHandlerId("");
        wo.setHandlerName("");
        wo.setHandlerPhone("");
        wo.setServiceRequestId(body.getAlarmId() != null ? body.getAlarmId() : "");
        wo.setCompleteTime(LocalDateTime.of(1970, 1, 1, 0, 0, 0));
        wo.setCreatedAt(LocalDateTime.now());
        wo.setUpdatedAt(LocalDateTime.now());

        WorkOrder saved = workOrderRepository.save(wo);
        return ApiResponse.created(buildListItem(saved));
    }

    /**
     * 指派工单处理人兼容接口。
     *
     * @param workOrderId 工单ID
     * @param body        指派请求体（handler_id, handler_name）
     * @return 更新后的工单
     */
    @PutMapping("/{work_order_id}/assign")
    public ApiResponse<Map<String, Object>> assignWorkOrder(
            @PathVariable("work_order_id") String workOrderId,
            @RequestBody AssignWorkOrderRequest body) {
        WorkOrder wo = workOrderRepository.findByOrderId(workOrderId);
        if (wo == null) {
            return ApiResponse.error(404, "工单不存在: " + workOrderId);
        }
        wo.setHandlerId(body.getHandlerId() != null ? body.getHandlerId() : "");
        wo.setHandlerName(body.getHandlerName() != null ? body.getHandlerName() : "");
        wo.setStatus("处理中");
        wo.setUpdatedAt(LocalDateTime.now());
        WorkOrder saved = workOrderRepository.save(wo);
        return ApiResponse.success(buildListItem(saved));
    }

    /**
     * 完成工单兼容接口。
     *
     * @param workOrderId 工单ID
     * @return 更新后的工单
     */
    @PutMapping("/{work_order_id}/complete")
    public ApiResponse<Map<String, Object>> completeWorkOrder(
            @PathVariable("work_order_id") String workOrderId) {
        WorkOrder wo = workOrderRepository.findByOrderId(workOrderId);
        if (wo == null) {
            return ApiResponse.error(404, "工单不存在: " + workOrderId);
        }
        wo.setStatus("已完成");
        wo.setCompleteTime(LocalDateTime.now());
        wo.setUpdatedAt(LocalDateTime.now());
        WorkOrder saved = workOrderRepository.save(wo);
        return ApiResponse.success(buildListItem(saved));
    }

    /**
     * 构建列表/详情返回的工单数据 Map，关联告警详情。
     */
    private Map<String, Object> buildListItem(WorkOrder wo) {
        Map<String, Object> item = new HashMap<>();
        item.put("work_order_id", wo.getOrderId());
        item.put("order_id", wo.getOrderId());
        // 关联告警 ID（从 service_request_id 或 alarm_id 字段）
        String linkedAlarmId = wo.getServiceRequestId() != null && !wo.getServiceRequestId().isEmpty()
                ? wo.getServiceRequestId()
                : (wo.getAlarmId() != null ? wo.getAlarmId() : "");
        item.put("alarm_id", linkedAlarmId);
        item.put("elder_id", wo.getElderId());
        // 查找老人姓名
        String elderName = "";
        if (wo.getElderId() != null) {
            ElderUser elder = elderUserRepository.findByElderId(wo.getElderId());
            if (elder != null) elderName = elder.getName();
        }
        item.put("elder_name", elderName);
        item.put("work_order_type", wo.getType());
        item.put("order_type", wo.getType());
        item.put("type", wo.getType());
        item.put("status", wo.getStatus());
        item.put("description", wo.getDescription() != null ? wo.getDescription() : "");
        item.put("assignee", wo.getHandlerName() != null && !wo.getHandlerName().isEmpty()
                ? wo.getHandlerName() : "社区工作人员");

        // 关联告警详情
        if (!linkedAlarmId.isEmpty()) {
            AlarmEvent alarm = alarmEventRepository.findByAlarmId(linkedAlarmId);
            if (alarm != null) {
                item.put("alarm_type", alarm.getType() != null ? alarm.getType() : "");
                item.put("alarm_risk_level", alarm.getRiskLevel() != null ? alarm.getRiskLevel() : "");
                String alarmLocation = alarm.getLocation() != null && !alarm.getLocation().isEmpty()
                        ? alarm.getLocation()
                        : (alarm.getBuilding() != null ? alarm.getBuilding() + " " : "")
                          + (alarm.getRoomNumber() != null ? alarm.getRoomNumber() : "");
                item.put("alarm_location", alarmLocation.trim());
                item.put("alarm_status", alarm.getStatus() != null ? alarm.getStatus() : "");
            } else {
                item.put("alarm_type", "");
                item.put("alarm_risk_level", "");
                item.put("alarm_location", "");
                item.put("alarm_status", "");
            }
        } else {
            item.put("alarm_type", "");
            item.put("alarm_risk_level", "");
            item.put("alarm_location", "");
            item.put("alarm_status", "");
        }

        String createTime = "";
        if (wo.getCreatedAt() != null) {
            String ts = wo.getCreatedAt().toString();
            createTime = ts.length() >= 19 ? ts.substring(0, 19) : ts;
        }
        item.put("create_time", createTime);
        return item;
    }

    // ==================== 请求 DTO ====================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreateWorkOrderRequest {
        @JsonProperty("elder_id")
        private String elderId;
        @JsonProperty("order_type")
        private String orderType;
        @JsonProperty("type")
        private String type;
        private String description;
        @JsonProperty("creator_id")
        private String creatorId;
        @JsonProperty("alarm_id")
        private String alarmId;

        public String getElderId() { return elderId; }
        public void setElderId(String elderId) { this.elderId = elderId; }
        public String getType() { return orderType != null ? orderType : type; }
        public void setType(String orderType) { this.orderType = orderType; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCreatorId() { return creatorId; }
        public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
        public String getAlarmId() { return alarmId; }
        public void setAlarmId(String alarmId) { this.alarmId = alarmId; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AssignWorkOrderRequest {
        @JsonProperty("handler_id")
        private String handlerId;
        @JsonProperty("handler_name")
        private String handlerName;

        public String getHandlerId() { return handlerId; }
        public void setHandlerId(String handlerId) { this.handlerId = handlerId; }
        public String getHandlerName() { return handlerName; }
        public void setHandlerName(String handlerName) { this.handlerName = handlerName; }
    }
}
