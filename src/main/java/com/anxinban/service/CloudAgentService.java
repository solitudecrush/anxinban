package com.anxinban.service;

/**
 * CloudAgent 业务服务类，处理 CloudAgent 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.AgentInfoDto;
import com.anxinban.dto.AlarmDto;
import com.anxinban.dto.InterventionDto;
import com.anxinban.dto.InterventionResultDto;
import com.anxinban.entity.CloudAgent;
import com.anxinban.mapper.CloudAgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CloudAgentService {
    private final CloudAgentRepository cloudAgentRepository;
    private final AlarmService alarmService;
    /** 业务服务实例 */
    private final InterventionService interventionService;

    @Autowired
    public CloudAgentService(CloudAgentRepository cloudAgentRepository, AlarmService alarmService, InterventionService interventionService) {
        this.cloudAgentRepository = cloudAgentRepository;
        this.alarmService = alarmService;
        this.interventionService = interventionService;
    }

        /**
         * registerAgent 方法。
         *
         * @param dto 字段含义待补充
         */
    public AgentInfoDto registerAgent(AgentInfoDto dto) {
        CloudAgent entity = convertToEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        CloudAgent saved = cloudAgentRepository.save(entity);
        return convertToDto(saved);
    }

        /**
         * reportStatus 方法。
         *
         * @param dto 字段含义待补充
         */
    public AgentInfoDto reportStatus(AgentInfoDto dto) {
        CloudAgent existing = cloudAgentRepository.findByAgentId(dto.getAgentId());
        if (existing == null) {
            return registerAgent(dto);
        }
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getIp() != null) existing.setIp(dto.getIp());
        if (dto.getDeviceCount() != null) existing.setDeviceCount(dto.getDeviceCount());
        if (dto.getConnectedDevices() != null) existing.setConnectedDevices(dto.getConnectedDevices());
        if (dto.getLastHeartbeat() != null) existing.setLastHeartbeat(LocalDateTime.parse(dto.getLastHeartbeat().substring(0, 19)));
        existing.setUpdateTime(LocalDateTime.now());
        CloudAgent saved = cloudAgentRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * syncAlarm 方法。
         *
         * @param alarm 字段含义待补充
         */
    public AlarmDto syncAlarm(AlarmDto alarm) {
        return alarmService.createAlarm(alarm);
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public Map<String, Object> getConfig(String agentId) {
        Map<String, Object> config = new HashMap<>();
        config.put("heartbeatInterval", 30);
        config.put("sensorUploadInterval", 10);
        Map<String, Object> thresholds = new HashMap<>();
        thresholds.put("fall", true);
        thresholds.put("heartRateHigh", 120);
        thresholds.put("heartRateLow", 40);
        config.put("alarmThresholds", thresholds);
        return config;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public List<InterventionDto> getInterventions(String agentId) {
        return interventionService.listInterventions(null, "pending", null, 1, 100);
    }

        /**
         * reportInterventionResult 方法。
         *
         * @param dto 字段含义待补充
         */
    public void reportInterventionResult(InterventionResultDto dto) {
        if (dto.getInterventionId() != null && dto.getStatus() != null) {
            interventionService.completeIntervention(dto.getInterventionId(), dto.getStatus(), dto.getResult(), dto.getTimestamp());
        }
    }

        /**
         * 获取年龄。
         *
         * @return 年龄
         */
    public AgentInfoDto getAgent(String agentId) {
        CloudAgent entity = cloudAgentRepository.findByAgentId(agentId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private AgentInfoDto convertToDto(CloudAgent entity) {
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
         * convertToEntity 方法。
         *
         * @param dto 字段含义待补充
         */
    private CloudAgent convertToEntity(AgentInfoDto dto) {
        CloudAgent entity = new CloudAgent();
        entity.setAgentId(dto.getAgentId());
        entity.setAgentType(dto.getAgentType());
        entity.setStatus(dto.getStatus());
        entity.setIp(dto.getIp());
        entity.setDeviceCount(dto.getDeviceCount());
        entity.setConnectedDevices(dto.getConnectedDevices());
        if (dto.getLastHeartbeat() != null) {
            entity.setLastHeartbeat(LocalDateTime.parse(dto.getLastHeartbeat().substring(0, 19)));
        }
        return entity;
    }
}
