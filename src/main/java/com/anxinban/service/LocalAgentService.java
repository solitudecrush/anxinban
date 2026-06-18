package com.anxinban.service;

/**
 * LocalAgent 业务服务类，处理 LocalAgent 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.AgentInfoDto;
import com.anxinban.dto.AlarmDto;
import com.anxinban.dto.DeviceDto;
import com.anxinban.entity.LocalAgent;
import com.anxinban.mapper.LocalAgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class LocalAgentService {
    private final LocalAgentRepository localAgentRepository;
    /** 业务服务实例 */
    private final AlarmService alarmService;
    private final Map<String, List<AgentInfoDto>> heartbeatLogs = new ConcurrentHashMap<>();

    @Autowired
    public LocalAgentService(LocalAgentRepository localAgentRepository, AlarmService alarmService) {
        this.localAgentRepository = localAgentRepository;
        this.alarmService = alarmService;
    }

        /**
         * saveHeartbeat 方法。
         *
         * @param dto 字段含义待补充
         */
    public void saveHeartbeat(AgentInfoDto dto) {
        LocalAgent entity = localAgentRepository.findByAgentId(dto.getAgentId());
        if (entity == null) {
            entity = new LocalAgent();
            entity.setAgentId(dto.getAgentId());
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setAgentType(dto.getAgentType());
        entity.setStatus(dto.getStatus());
        entity.setIp(dto.getIp());
        entity.setDeviceCount(dto.getDeviceCount());
        entity.setConnectedDevices(dto.getConnectedDevices());
        if (dto.getLastHeartbeat() != null) {
            entity.setLastHeartbeat(LocalDateTime.parse(dto.getLastHeartbeat().substring(0, 19)));
        }
        entity.setUpdateTime(LocalDateTime.now());
        localAgentRepository.save(entity);
        heartbeatLogs.computeIfAbsent(dto.getAgentId(), k -> new ArrayList<>()).add(dto);
    }

        /**
         * saveStatus 方法。
         *
         * @param dto 字段含义待补充
         */
    public void saveStatus(AgentInfoDto dto) {
        saveHeartbeat(dto);
    }

        /**
         * saveData 方法。
         *
         * @param List 数据列表
         * @param sensorDataList 数据载荷
         */
    public void saveData(String agentId, String deviceId, List<DeviceDto.SensorData> sensorDataList) {
        // 数据已交由 DeviceService 处理，本地代理仅做日志记录
    }

        /**
         * reportAlarm 方法。
         *
         * @param alarm 字段含义待补充
         */
    public AlarmDto reportAlarm(String agentId, AlarmDto alarm) {
        if (alarm == null) {
            throw new IllegalArgumentException("alarm is required");
        }
        if (alarm.getAlarmId() == null || alarm.getAlarmId().isEmpty()) {
            String prefix = agentId != null && !agentId.isEmpty() ? agentId + "-" : "";
            alarm.setAlarmId("ALM-" + prefix + System.currentTimeMillis());
        }
        if (alarm.getSeverity() == null || alarm.getSeverity().isEmpty()) {
            alarm.setSeverity("medium");
        }
        return alarmService.createAlarm(alarm);
    }

        /**
         * 获取年龄。
         *
         * @return 年龄
         */
    public AgentInfoDto getAgent(String agentId) {
        LocalAgent entity = localAgentRepository.findByAgentId(agentId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * listAgents 方法。
         *
         * @param size 大小
         */
    public List<AgentInfoDto> listAgents(String status, String ip, int page, int size) {
        List<LocalAgent> entities;
        if (status != null) {
            entities = localAgentRepository.findByStatus(status);
        } else if (ip != null) {
            entities = localAgentRepository.findByIp(ip);
        } else {
            entities = localAgentRepository.findAll();
        }
        List<AgentInfoDto> dtos = entities.stream().map(this::convertToDto).collect(Collectors.toList());
        return paginate(dtos, page, size);
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private AgentInfoDto convertToDto(LocalAgent entity) {
        AgentInfoDto dto = new AgentInfoDto();
        dto.setAgentId(entity.getAgentId());
        dto.setAgentType(entity.getAgentType());
        dto.setStatus(entity.getStatus());
        dto.setIp(entity.getIp());
        dto.setDeviceCount(entity.getDeviceCount());
        dto.setConnectedDevices(entity.getConnectedDevices());
        dto.setLastHeartbeat(entity.getLastHeartbeat() != null ? entity.getLastHeartbeat().toString() : null);
        return dto;
    }

        /**
         * paginate 方法。
         *
         * @param size 大小
         */
    private <T> List<T> paginate(List<T> items, int page, int size) {
        int from = Math.max((page - 1) * size, 0);
        int to = Math.min(from + size, items.size());
        if (from > items.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(items.subList(from, to));
    }
}
