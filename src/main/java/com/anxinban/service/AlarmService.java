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
import com.anxinban.mapper.AlarmEventRepository;
import com.anxinban.mapper.ElderUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlarmService {
    private final AlarmEventRepository alarmEventRepository;
    /** 数据访问仓库，用于持久化操作 */
    private final ElderUserRepository elderUserRepository;

    @Autowired
    public AlarmService(AlarmEventRepository alarmEventRepository, ElderUserRepository elderUserRepository) {
        this.alarmEventRepository = alarmEventRepository;
        this.elderUserRepository = elderUserRepository;
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
        entity.setAlarmStatus(alarm.getStatus() == null ? "pending" : alarm.getStatus());
        entity.setIsRead(alarm.getIsRead() != null ? alarm.getIsRead() : false);
        entity.setCreatedAt(alarm.getOccurTime() != null ? LocalDateTime.parse(alarm.getOccurTime().substring(0, 19)) : LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
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
    public PageResult<AlarmDto> listAlarms(String elderId, String deviceId, String alarmType, String status, String startTime, String endTime, int page, int size) {
        List<AlarmEvent> entities;
        if (elderId != null && !elderId.isEmpty()) {
            entities = alarmEventRepository.findByElderId(elderId);
        } else if (deviceId != null && !deviceId.isEmpty()) {
            entities = alarmEventRepository.findByDeviceId(deviceId);
        } else if (alarmType != null && !alarmType.isEmpty()) {
            entities = alarmEventRepository.findByAlarmType(alarmType);
        } else if (status != null && !status.isEmpty()) {
            entities = alarmEventRepository.findByAlarmStatus(status);
        } else if (startTime != null && endTime != null) {
            LocalDateTime start = LocalDateTime.parse(startTime.substring(0, 19));
            LocalDateTime end = LocalDateTime.parse(endTime.substring(0, 19));
            entities = alarmEventRepository.findByCreatedAtBetween(start, end);
        } else {
            entities = alarmEventRepository.findAll();
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
        List<AlarmEvent> entities = alarmEventRepository.findByElderId(elderId);
        List<AlarmDto> dtos = entities.stream()
                .filter(a -> a.getAlarmType() != null && (
                        a.getAlarmType().contains("heart_rate") ||
                        a.getAlarmType().contains("blood_pressure") ||
                        a.getAlarmType().contains("temperature") ||
                        a.getAlarmType().contains("fall") ||
                        a.getAlarmType().contains("inactive")))
                .filter(a -> status == null || status.isEmpty() || status.equals(a.getAlarmStatus()))
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
            entities = alarmEventRepository.findByAlarmTypeAndBuilding("intrusion", building);
            entities = entities.stream().filter(e -> status.equals(e.getAlarmStatus())).collect(Collectors.toList());
        } else if (status != null && !status.isEmpty()) {
            entities = alarmEventRepository.findByAlarmTypeAndAlarmStatus("intrusion", status);
        } else if (building != null && !building.isEmpty()) {
            entities = alarmEventRepository.findByAlarmTypeAndBuilding("intrusion", building);
        } else {
            entities = alarmEventRepository.findByAlarmType("intrusion");
        }
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
        existing.setAlarmStatus("handled");
        existing.setHandler(handler);
        existing.setUpdateTime(LocalDateTime.now());
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
        existing.setAlarmStatus("handled");
        existing.setHandler(handler);
        existing.setHandleRemark(remark);
        existing.setResolvedAt(handleTime != null ? LocalDateTime.parse(handleTime.substring(0, 19)) : LocalDateTime.now());
        existing.setUpdateTime(LocalDateTime.now());
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
        existing.setUpdateTime(LocalDateTime.now());
        AlarmEvent saved = alarmEventRepository.save(existing);
        return convertToDto(saved);
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
        dto.setAlarmType(entity.getAlarmType());
        dto.setSeverity(entity.getAlarmLevel());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getAlarmStatus());
        dto.setIsRead(entity.getIsRead());
        dto.setOccurTime(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setHandleTime(entity.getResolvedAt() != null ? entity.getResolvedAt().toString() : null);
        dto.setHandler(entity.getHandler());
        dto.setHandlerName(entity.getHandlerName());
        dto.setRemark(entity.getHandleRemark());
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
        entity.setAlarmType(dto.getAlarmType());
        entity.setAlarmLevel(dto.getSeverity());
        entity.setDescription(dto.getDescription());
        entity.setAlarmStatus(dto.getStatus());
        entity.setIsRead(dto.getIsRead());
        entity.setHandler(dto.getHandler());
        entity.setHandlerName(dto.getHandlerName());
        entity.setHandleRemark(dto.getRemark());
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
