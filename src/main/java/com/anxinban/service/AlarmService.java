package com.anxinban.service;

/**
 * Alarm 业务服务类，处理 Alarm 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.AlarmDto;
import com.anxinban.dto.PageResult;
import com.anxinban.entity.AlarmEvent;
import com.anxinban.entity.WorkOrder;
import com.anxinban.mapper.AlarmEventRepository;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.mapper.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlarmService {
    private final AlarmEventRepository alarmEventRepository;
    private final ElderUserRepository elderUserRepository;
    private final WorkOrderRepository workOrderRepository;

    @Autowired
    public AlarmService(AlarmEventRepository alarmEventRepository, ElderUserRepository elderUserRepository,
                        WorkOrderRepository workOrderRepository) {
        this.alarmEventRepository = alarmEventRepository;
        this.elderUserRepository = elderUserRepository;
        this.workOrderRepository = workOrderRepository;
    }

        /**
         * createAlarm 方法。
         *
         * @param alarm 字段含义待补充
         */
    public AlarmDto createAlarm(AlarmDto alarm) {
        if (alarm.getAlarmId() == null || alarm.getAlarmId().isEmpty()) {
            throw new IllegalArgumentException("alarmId is required");
        }
        AlarmEvent entity = convertToEntity(alarm);
        entity.setStatus(alarm.getStatus() == null ? "pending" : alarm.getStatus());
        entity.setIsRead(alarm.getIsRead() != null ? alarm.getIsRead() : false);
        entity.setCreatedAt(alarm.getOccurTime() != null ? LocalDateTime.parse(alarm.getOccurTime().substring(0, 19)) : LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        AlarmEvent saved = alarmEventRepository.save(entity);
        return convertToDto(saved);
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public AlarmDto getAlarm(String alarmId) {
        AlarmEvent entity = alarmEventRepository.findByAlarmId(alarmId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * listAlarms 方法。
         *
         * @param size 大小
         */
    public PageResult<AlarmDto> listAlarms(String elderId, String deviceId, String type, String status, String startTime, String endTime, int page, int size) {
        List<AlarmEvent> entities;
        if (elderId != null && !elderId.isEmpty()) {
            entities = alarmEventRepository.findByElderIdOrderByOccurTimeDesc(elderId);
        } else if (deviceId != null && !deviceId.isEmpty()) {
            entities = alarmEventRepository.findByDeviceId(deviceId);
        } else if (type != null && !type.isEmpty()) {
            entities = alarmEventRepository.findByType(type);
        } else if (status != null && !status.isEmpty()) {
            entities = alarmEventRepository.findByStatus(status);
        } else if (startTime != null && endTime != null) {
            LocalDateTime start = LocalDateTime.parse(startTime.substring(0, 19));
            LocalDateTime end = LocalDateTime.parse(endTime.substring(0, 19));
            entities = alarmEventRepository.findByCreatedAtBetween(start, end);
        } else {
            entities = alarmEventRepository.findAll();
        }
        // 按告警发生时间降序排列（最新在前），非 elderId 查询时补排序
        // 优先按 occurTime 排序，若为空则回退到 createdAt
        if (elderId == null || elderId.isEmpty()) {
            entities.sort((a, b) -> {
                LocalDateTime ta = a.getOccurTime() != null ? a.getOccurTime() : a.getCreatedAt();
                LocalDateTime tb = b.getOccurTime() != null ? b.getOccurTime() : b.getCreatedAt();
                if (ta == null) return 1;
                if (tb == null) return -1;
                return tb.compareTo(ta);
            });
        }
        List<AlarmDto> dtos = entities.stream().map(this::convertToDto).collect(Collectors.toList());
        long total = dtos.size();
        List<AlarmDto> paginated = paginate(dtos, page, size);
        return new PageResult<>(paginated, total, page, size);
    }

        /**
         * listHealthAbnormalAlarms 方法。
         *
         * @param size 大小
         */
    public PageResult<AlarmDto> listHealthAbnormalAlarms(String elderId, String status, int page, int size) {
        List<AlarmEvent> entities = alarmEventRepository.findByElderIdOrderByOccurTimeDesc(elderId);
        List<AlarmDto> dtos = entities.stream()
                .filter(a -> a.getType() != null && (
                        a.getType().contains("heart_rate") ||
                        a.getType().contains("blood_pressure") ||
                        a.getType().contains("temperature") ||
                        a.getType().contains("fall") ||
                        a.getType().contains("inactive")))
                .filter(a -> status == null || status.isEmpty() || status.equals(a.getStatus()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
        long total = dtos.size();
        List<AlarmDto> paginated = paginate(dtos, page, size);
        return new PageResult<>(paginated, total, page, size);
    }

        /**
         * listIntrusionAlarms 方法。
         *
         * @param size 大小
         */
    public PageResult<AlarmDto> listIntrusionAlarms(String status, String building, int page, int size) {
        List<AlarmEvent> entities;
        if (status != null && !status.isEmpty() && building != null && !building.isEmpty()) {
            entities = alarmEventRepository.findByTypeAndBuilding("intrusion", building);
            entities = entities.stream().filter(e -> status.equals(e.getStatus())).collect(Collectors.toList());
        } else if (status != null && !status.isEmpty()) {
            entities = alarmEventRepository.findByTypeAndStatus("intrusion", status);
        } else if (building != null && !building.isEmpty()) {
            entities = alarmEventRepository.findByTypeAndBuilding("intrusion", building);
        } else {
            entities = alarmEventRepository.findByType("intrusion");
        }
        // 按告警发生时间降序排列（最新在前）
        entities.sort((a, b) -> {
            LocalDateTime ta = a.getOccurTime() != null ? a.getOccurTime() : a.getCreatedAt();
            LocalDateTime tb = b.getOccurTime() != null ? b.getOccurTime() : b.getCreatedAt();
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });
        List<AlarmDto> dtos = entities.stream().map(this::convertToDto).collect(Collectors.toList());
        long total = dtos.size();
        List<AlarmDto> paginated = paginate(dtos, page, size);
        return new PageResult<>(paginated, total, page, size);
    }

        /**
         * acknowledgeAlarm 方法。
         *
         * @param handleTime 时间
         */
    public AlarmDto acknowledgeAlarm(String alarmId, String handler, String handleTime) {
        AlarmEvent existing = alarmEventRepository.findByAlarmId(alarmId);
        if (existing == null) {
            return null;
        }
        existing.setStatus("handled");
        existing.setHandlerId(handler);
        existing.setUpdatedAt(LocalDateTime.now());
        AlarmEvent saved = alarmEventRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * resolveAlarm 方法。
         *
         * @param remark 备注
         */
    public AlarmDto resolveAlarm(String alarmId, String handler, String handleTime, String remark) {
        AlarmEvent existing = alarmEventRepository.findByAlarmId(alarmId);
        if (existing == null) {
            return null;
        }
        existing.setStatus("handled");
        existing.setHandlerId(handler);
        existing.setHandleNote(remark);
        existing.setHandleTime(handleTime != null ? LocalDateTime.parse(handleTime.substring(0, 19)) : LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());
        AlarmEvent saved = alarmEventRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * 获取数量。
         *
         * @return 数量
         */
    public long getUnreadCount(String elderId) {
        return alarmEventRepository.countByElderIdAndIsRead(elderId, false);
    }

        /**
         * markAsRead 方法。
         *
         * @param alarmId 唯一标识，主键
         */
    public AlarmDto markAsRead(String alarmId) {
        AlarmEvent existing = alarmEventRepository.findByAlarmId(alarmId);
        if (existing == null) {
            return null;
        }
        existing.setIsRead(true);
        existing.setUpdatedAt(LocalDateTime.now());
        AlarmEvent saved = alarmEventRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * 告警转工单。
         *
         * @param alarmId 告警ID
         * @return 含工单信息的 Map，告警不存在时返回 null
         */
    public Map<String, Object> convertToWorkOrder(String alarmId) {
        AlarmEvent alarm = alarmEventRepository.findByAlarmId(alarmId);
        if (alarm == null) {
            return null;
        }

        // 检查是否已存在关联工单
        List<WorkOrder> existingOrders = workOrderRepository.findByServiceRequestId(alarm.getAlarmId());
        if (existingOrders != null && !existingOrders.isEmpty()) {
            WorkOrder existing = existingOrders.get(0);
            Map<String, Object> result = new HashMap<>();
            result.put("message", "告警已存在关联工单");
            result.put("workOrderId", existing.getOrderId());
            result.put("orderId", existing.getOrderId());
            result.put("alarmId", alarmId);
            return result;
        }

        // 根据告警类型确定工单类型
        String orderType = "日常关怀";
        if (alarm.getType() != null) {
            switch (alarm.getType()) {
                case "fall": orderType = "紧急巡检"; break;
                case "health_abnormal": case "heart_rate": case "blood_pressure":
                case "temperature": orderType = "健康关注"; break;
                case "smoke": case "intrusion": orderType = "紧急巡检"; break;
                case "fingerprint-fail": orderType = "设备检查"; break;
                default: orderType = "日常关怀"; break;
            }
        }

        // 创建工单
        WorkOrder wo = new WorkOrder();
        wo.setOrderId("wo_" + UUID.randomUUID().toString().substring(0, 8));
        wo.setElderId(alarm.getElderId());
        wo.setType(orderType);
        wo.setDescription(alarm.getDescription() != null ? alarm.getDescription() : "");
        wo.setStatus("待处理");
        wo.setCreatorId("system");
        wo.setHandlerId("");
        wo.setHandlerName("");
        wo.setHandlerPhone("");
        wo.setCompleteTime(LocalDateTime.of(1970, 1, 1, 0, 0, 0));
        wo.setServiceRequestId(alarm.getAlarmId());
        wo.setCreatedAt(LocalDateTime.now());
        wo.setUpdatedAt(LocalDateTime.now());
        WorkOrder saved = workOrderRepository.save(wo);

        // 更新告警状态
        alarm.setStatus("handled");
        alarm.setUpdatedAt(LocalDateTime.now());
        alarmEventRepository.save(alarm);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "success");
        result.put("workOrderId", saved.getOrderId());
        result.put("orderId", saved.getOrderId());
        result.put("alarmId", alarmId);
        return result;
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private AlarmDto convertToDto(AlarmEvent entity) {
        AlarmDto dto = new AlarmDto();
        dto.setAlarmId(entity.getAlarmId());
        dto.setElderId(entity.getElderId());
        dto.setDeviceId(entity.getDeviceId());
        dto.setAlarmType(entity.getType());
        dto.setSeverity(entity.getRiskLevel());
        dto.setDescription(entity.getDescription() != null && !entity.getDescription().isEmpty() ? entity.getDescription() : "暂无详情");
        dto.setStatus(entity.getStatus());
        dto.setIsRead(entity.getIsRead());
        // 优先使用告警发生时间 occurTime，若为空则回退到 createdAt
        dto.setOccurTime(entity.getOccurTime() != null ? entity.getOccurTime().toString()
                : (entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null));
        dto.setHandleTime(entity.getHandleTime() != null ? entity.getHandleTime().toString() : null);
        dto.setHandler(entity.getHandlerId());
        dto.setHandlerName(entity.getHandlerName());
        dto.setRemark(entity.getHandleNote());
        dto.setBuilding(entity.getBuilding());
        dto.setRoomNumber(entity.getRoomNumber());
        dto.setUnit(entity.getUnit());
        dto.setSnapshotUrl(entity.getSnapshotUrl());
        if (entity.getElderId() != null) {
            var elder = elderUserRepository.findByElderId(entity.getElderId());
            if (elder != null) {
                dto.setElderName(elder.getName());
            }
        }
        return dto;
    }

        /**
         * convertToEntity 方法。
         *
         * @param dto 字段含义待补充
         */
    private AlarmEvent convertToEntity(AlarmDto dto) {
        AlarmEvent entity = new AlarmEvent();
        entity.setAlarmId(dto.getAlarmId());
        entity.setElderId(dto.getElderId());
        entity.setDeviceId(dto.getDeviceId());
        entity.setType(dto.getAlarmType());
        entity.setRiskLevel(dto.getSeverity());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setIsRead(dto.getIsRead());
        entity.setHandlerId(dto.getHandler());
        entity.setHandlerName(dto.getHandlerName());
        entity.setHandleNote(dto.getRemark());
        entity.setBuilding(dto.getBuilding());
        entity.setRoomNumber(dto.getRoomNumber());
        entity.setUnit(dto.getUnit());
        entity.setSnapshotUrl(dto.getSnapshotUrl());
        return entity;
    }

        /**
         * paginate 方法。
         *
         * @param size 大小
         */
    private List<AlarmDto> paginate(List<AlarmDto> items, int page, int size) {
        int from = Math.max((page - 1) * size, 0);
        int to = Math.min(from + size, items.size());
        if (from > items.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(items.subList(from, to));
    }
}
