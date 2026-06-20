package com.anxinban.controller;

import com.anxinban.dto.AlarmDto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 比赛兼容层 - 告警兼容接口（复数路径 /api/alarms）。
 *
 * <p>提供：</p>
 * <ul>
 *   <li>GET /api/alarms — 告警列表（兼容格式）</li>
 *   <li>POST /api/alarms/{alarm_id}/to-work-order — 告警转工单</li>
 * </ul>
 *
 * <p>与原有 /api/alarm/* 共存，不冲突。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/alarms")
public class AlarmsCompatController {

    private static final Logger log = LoggerFactory.getLogger(AlarmsCompatController.class);

    @Autowired
    private AlarmEventRepository alarmEventRepository;

    @Autowired
    private ElderUserRepository elderUserRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    /**
     * 告警列表兼容接口（支持分页）。
     *
     * <p>返回简化的告警分页列表，字段名兼容 snake_case 格式。</p>
     *
     * @param page     页码，从 1 开始
     * @param pageSize 每页大小
     * @return 分页告警列表
     */
    @GetMapping
    public ApiResponse<PageResult<Map<String, Object>>> listAlarms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<AlarmEvent> alarms = alarmEventRepository.findAll();
        // 按创建时间倒序排列
        alarms.sort((a, b) -> {
            if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });

        List<Map<String, Object>> allItems = new ArrayList<>();
        for (AlarmEvent a : alarms) {
            allItems.add(buildListItem(a));
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
     * 构建单条告警列表项。
     */
    private Map<String, Object> buildListItem(AlarmEvent a) {
        Map<String, Object> item = new HashMap<>();
        item.put("alarm_id", a.getAlarmId());
        item.put("elder_id", a.getElderId());
        // 查找老人姓名
        String elderName = "";
        if (a.getElderId() != null) {
            ElderUser elder = elderUserRepository.findByElderId(a.getElderId());
            if (elder != null) elderName = elder.getName();
        }
        item.put("elder_name", elderName);
        item.put("risk_level", mapSeverityToRiskLevel(a.getRiskLevel()));
        item.put("alarm_type", a.getType());
        item.put("description", a.getDescription());
        item.put("status", a.getStatus());
        item.put("is_read", a.getIsRead() != null ? a.getIsRead() : false);
        String occurTime = "";
        if (a.getCreatedAt() != null) {
            String ts = a.getCreatedAt().toString();
            occurTime = ts.length() >= 19 ? ts.substring(0, 19) : ts;
        }
        item.put("occur_time", occurTime);
        // 位置：优先使用 location 字段，否则拼接 building + roomNumber
        String location = a.getLocation() != null && !a.getLocation().isEmpty()
                ? a.getLocation()
                : (a.getBuilding() != null ? a.getBuilding() + " " : "") + (a.getRoomNumber() != null ? a.getRoomNumber() : "");
        item.put("location", location.trim());
        return item;
    }

    /**
     * 告警详情兼容接口。
     *
     * <p>根据 alarm_id 返回单条告警的完整信息，字段兼容前端 snake_case 格式。</p>
     *
     * @param alarmId 告警ID
     * @return 告警详情
     */
    @GetMapping("/{alarm_id}")
    public ApiResponse<Map<String, Object>> getAlarmDetail(@PathVariable("alarm_id") String alarmId) {
        AlarmEvent a = alarmEventRepository.findByAlarmId(alarmId);
        if (a == null) {
            return ApiResponse.error(404, "告警不存在: " + alarmId);
        }

        Map<String, Object> item = new HashMap<>();
        item.put("alarm_id", a.getAlarmId());
        item.put("elder_id", a.getElderId());

        String elderName = "";
        if (a.getElderId() != null) {
            ElderUser elder = elderUserRepository.findByElderId(a.getElderId());
            if (elder != null) elderName = elder.getName();
        }
        item.put("elder_name", elderName);
        item.put("risk_level", mapSeverityToRiskLevel(a.getRiskLevel()));
        item.put("alarm_type", a.getType());
        item.put("description", a.getDescription());
        item.put("status", a.getStatus());
        item.put("is_read", a.getIsRead() != null ? a.getIsRead() : false);
        String occurTime = "";
        if (a.getCreatedAt() != null) {
            String ts = a.getCreatedAt().toString();
            occurTime = ts.length() >= 19 ? ts.substring(0, 19) : ts;
        }
        item.put("occur_time", occurTime);
        String handleTime = "";
        if (a.getHandleTime() != null) {
            String ts = a.getHandleTime().toString();
            handleTime = ts.length() >= 19 ? ts.substring(0, 19) : ts;
        }
        item.put("handle_time", handleTime);
        item.put("handler", a.getHandlerId() != null ? a.getHandlerId() : "");
        item.put("handler_name", a.getHandlerName() != null ? a.getHandlerName() : "");
        item.put("remark", a.getHandleNote() != null ? a.getHandleNote() : "");
        String location = a.getLocation() != null && !a.getLocation().isEmpty()
                ? a.getLocation()
                : (a.getBuilding() != null ? a.getBuilding() + " " : "") + (a.getRoomNumber() != null ? a.getRoomNumber() : "");
        item.put("location", location.trim());
        item.put("building", a.getBuilding() != null ? a.getBuilding() : "");
        item.put("room_number", a.getRoomNumber() != null ? a.getRoomNumber() : "");

        return ApiResponse.success(item);
    }

    /**
     * 告警转工单兼容接口。
     *
     * <p>根据 alarm_id 查询告警，生成对应的工单记录。
     * 如果已存在关联工单则返回已有工单（通过 service_request_id 关联）。</p>
     *
     * @param alarmId 告警ID
     * @return 生成的工单信息
     */
    @PostMapping("/{alarm_id}/to-work-order")
    public ApiResponse<Map<String, Object>> convertToWorkOrder(@PathVariable("alarm_id") String alarmId) {
        // 1. 查找告警
        AlarmEvent alarm = alarmEventRepository.findByAlarmId(alarmId);
        if (alarm == null) {
            return ApiResponse.error(404, "告警不存在: " + alarmId);
        }

        // 2. 检查是否已转工单（通过 service_request_id 关联）
        if (alarm.getAlarmId() != null) {
            List<WorkOrder> existingOrders = workOrderRepository.findByServiceRequestId(alarm.getAlarmId());
            if (existingOrders != null && !existingOrders.isEmpty()) {
                WorkOrder existing = existingOrders.get(0);
                Map<String, Object> workOrderData = buildWorkOrderResponse(
                        existing.getOrderId(), alarmId, existing.getElderId(),
                        existing.getType(), existing.getStatus());

                Map<String, Object> result = new HashMap<>();
                result.put("message", "告警已存在关联工单");
                result.put("work_order", workOrderData);
                return ApiResponse.success(result);
            }
        }

        // 3. 确定工单类型
        String orderType = determineOrderType(alarm.getType(), alarm.getRiskLevel());

        // 4. 创建工单
        WorkOrder wo = new WorkOrder();
        wo.setOrderId("wo_" + UUID.randomUUID().toString().substring(0, 8));
        wo.setElderId(alarm.getElderId());
        wo.setType(orderType);
        wo.setDescription(alarm.getDescription());
        wo.setStatus("待处理");
        wo.setCreatorId("system");
        wo.setHandlerId("");
        wo.setHandlerName("");
        wo.setHandlerPhone("");
        wo.setCompleteTime(LocalDateTime.of(1970, 1, 1, 0, 0, 0));
        wo.setServiceRequestId(alarm.getAlarmId()); // 通过此字段关联告警，防止重复转工单
        wo.setCreatedAt(LocalDateTime.now());
        wo.setUpdatedAt(LocalDateTime.now());

        WorkOrder saved = workOrderRepository.save(wo);

        // 5. 更新告警状态
        alarm.setStatus("handled");
        alarm.setUpdatedAt(LocalDateTime.now());
        alarmEventRepository.save(alarm);

        log.info("告警转工单: alarmId={}, orderId={}", alarmId, saved.getOrderId());

        // 6. 构建返回（三个别名：order_type / type / work_order_type）
        Map<String, Object> workOrderData = buildWorkOrderResponse(
                saved.getOrderId(), alarmId, saved.getElderId(),
                saved.getType(), saved.getStatus());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "告警已转为工单");
        result.put("work_order", workOrderData);

        return ApiResponse.success(result);
    }

    /**
     * 构建工单返回数据，同时提供 order_type / type / work_order_type 三个别名，
     * 以及 order_id / work_order_id 两个别名，兼容不同前端的字段命名。
     */
    private Map<String, Object> buildWorkOrderResponse(String orderId, String alarmId,
                                                        String elderId, String orderType, String status) {
        Map<String, Object> data = new HashMap<>();
        data.put("work_order_id", orderId);
        data.put("order_id", orderId);
        data.put("alarm_id", alarmId);
        data.put("elder_id", elderId);
        data.put("work_order_type", orderType);
        data.put("order_type", orderType);
        data.put("type", orderType);
        data.put("status", status);
        return data;
    }

    private String mapSeverityToRiskLevel(String severity) {
        if (severity == null) return "低风险";
        switch (severity) {
            case "critical": return "高风险";
            case "high": return "高风险";
            case "medium": return "中风险";
            default: return "低风险";
        }
    }

    /**
     * 标记告警已读兼容接口。
     *
     * <p>根据 alarm_id 将告警标记为已读状态。</p>
     *
     * @param alarmId 告警ID
     * @return 更新后的告警信息
     */
    @PostMapping("/{alarm_id}/mark-read")
    public ApiResponse<Map<String, Object>> markRead(@PathVariable("alarm_id") String alarmId) {
        AlarmEvent alarm = alarmEventRepository.findByAlarmId(alarmId);
        if (alarm == null) {
            return ApiResponse.error(404, "告警不存在: " + alarmId);
        }

        alarm.setIsRead(true);
        alarm.setUpdatedAt(LocalDateTime.now());
        alarmEventRepository.save(alarm);

        Map<String, Object> item = new HashMap<>();
        item.put("alarm_id", alarm.getAlarmId());
        item.put("is_read", alarm.getIsRead());
        item.put("status", alarm.getStatus());
        item.put("message", "告警已标记为已读");

        return ApiResponse.success(item);
    }

    /**
     * 处理告警兼容接口。
     *
     * <p>根据 alarm_id 将告警标记为已处理，记录处理人和备注信息。</p>
     *
     * @param alarmId 告警ID
     * @param body    处理请求体（handler_name, handler, remark, status）
     * @return 更新后的告警信息
     */
    @PostMapping("/{alarm_id}/handle")
    public ApiResponse<Map<String, Object>> handleAlarm(
            @PathVariable("alarm_id") String alarmId,
            @RequestBody HandleAlarmRequest body) {
        AlarmEvent alarm = alarmEventRepository.findByAlarmId(alarmId);
        if (alarm == null) {
            return ApiResponse.error(404, "告警不存在: " + alarmId);
        }

        // 更新处理人信息
        if (body.getHandlerName() != null) {
            alarm.setHandlerName(body.getHandlerName());
        }
        if (body.getHandler() != null) {
            alarm.setHandlerId(body.getHandler());
        }
        if (body.getRemark() != null) {
            alarm.setHandleNote(body.getRemark());
        }

        // 更新状态：默认 handled，可自定义
        String newStatus = body.getStatus() != null ? body.getStatus() : "handled";
        alarm.setStatus(newStatus);
        alarm.setHandleTime(LocalDateTime.now());
        alarm.setUpdatedAt(LocalDateTime.now());
        alarmEventRepository.save(alarm);

        log.info("告警已处理: alarmId={}, status={}, handler={}", alarmId, newStatus, alarm.getHandlerName());

        Map<String, Object> item = new HashMap<>();
        item.put("alarm_id", alarm.getAlarmId());
        item.put("elder_id", alarm.getElderId());
        item.put("status", alarm.getStatus());
        item.put("handler_name", alarm.getHandlerName());
        item.put("handler", alarm.getHandlerId());
        item.put("remark", alarm.getHandleNote());
        item.put("message", "告警已处理");

        return ApiResponse.success(item);
    }

    // ==================== 请求 DTO ====================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HandleAlarmRequest {
        @JsonProperty("handler_name")
        private String handlerName;
        @JsonProperty("handler")
        private String handler;
        @JsonProperty("remark")
        private String remark;
        @JsonProperty("status")
        private String status;

        public String getHandlerName() { return handlerName; }
        public void setHandlerName(String handlerName) { this.handlerName = handlerName; }
        public String getHandler() { return handler; }
        public void setHandler(String handler) { this.handler = handler; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    private String determineOrderType(String alarmType, String alarmLevel) {
        if (alarmType == null) return "健康关注";
        if ("critical".equals(alarmLevel)) return "紧急巡检";
        switch (alarmType) {
            case "fall":
            case "emergency-call":
                return "紧急巡检";
            case "heart_rate":
            case "blood_pressure":
            case "health_abnormal":
                return "健康关注";
            case "smoke":
                return "紧急巡检";
            case "intrusion":
            case "fingerprint-fail":
                return "紧急巡检";
            case "inactive":
                return "设备检查";
            default:
                return "健康关注";
        }
    }
}
