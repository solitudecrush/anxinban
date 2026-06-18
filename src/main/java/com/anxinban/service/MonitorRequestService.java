package com.anxinban.service;

/**
 * MonitorRequest 业务服务类，处理 MonitorRequest 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.MonitorRequestDto;
import com.anxinban.entity.MonitorRequest;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.mapper.MonitorRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MonitorRequestService {
    private final MonitorRequestRepository monitorRequestRepository;
    /** 数据访问仓库，用于持久化操作 */
    private final ElderUserRepository elderUserRepository;

    @Autowired
    public MonitorRequestService(MonitorRequestRepository monitorRequestRepository, ElderUserRepository elderUserRepository) {
        this.monitorRequestRepository = monitorRequestRepository;
        this.elderUserRepository = elderUserRepository;
    }

        /**
         * createRequest 方法。
         *
         * @param dto 字段含义待补充
         */
    public MonitorRequestDto createRequest(MonitorRequestDto dto) {
        MonitorRequest entity = convertToEntity(dto);
        entity.setStatus("pending");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        MonitorRequest saved = monitorRequestRepository.save(entity);
        return convertToDto(saved);
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public MonitorRequestDto getRequest(String requestId) {
        MonitorRequest entity = monitorRequestRepository.findByRequestId(requestId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * listRequestsByElder 方法。
         *
         * @param elderId 关联老人用户 ID
         */
    public List<MonitorRequestDto> listRequestsByElder(String elderId) {
        return monitorRequestRepository.findByElderId(elderId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

        /**
         * listRequestsByStaff 方法。
         *
         * @param staffId 关联工作人员 ID
         */
    public List<MonitorRequestDto> listRequestsByStaff(String staffId) {
        return monitorRequestRepository.findByStaffId(staffId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

        /**
         * approveRequest 方法。
         *
         * @param requestId 请求 ID
         */
    public MonitorRequestDto approveRequest(String requestId) {
        MonitorRequest existing = monitorRequestRepository.findByRequestId(requestId);
        if (existing == null) return null;
        existing.setStatus("approved");
        existing.setApprovedAt(System.currentTimeMillis());
        existing.setExpiredAt(LocalDateTime.now().plusHours(24));
        existing.setUpdateTime(LocalDateTime.now());
        MonitorRequest saved = monitorRequestRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * rejectRequest 方法。
         *
         * @param requestId 请求 ID
         */
    public MonitorRequestDto rejectRequest(String requestId) {
        MonitorRequest existing = monitorRequestRepository.findByRequestId(requestId);
        if (existing == null) return null;
        existing.setStatus("rejected");
        existing.setUpdateTime(LocalDateTime.now());
        MonitorRequest saved = monitorRequestRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * revokePermission 方法。
         *
         * @param requestId 请求 ID
         */
    public MonitorRequestDto revokePermission(String requestId) {
        MonitorRequest existing = monitorRequestRepository.findByRequestId(requestId);
        if (existing == null) return null;
        existing.setStatus("none");
        existing.setExpiredAt(LocalDateTime.now());
        existing.setUpdateTime(LocalDateTime.now());
        MonitorRequest saved = monitorRequestRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * checkMonitorPermission 方法。
         *
         * @param staffId 关联工作人员 ID
         */
    public boolean checkMonitorPermission(String elderId, String staffId) {
        List<MonitorRequest> list = monitorRequestRepository.findByElderIdAndStatusAndExpiredAtAfter(elderId, "approved", LocalDateTime.now());
        if (staffId != null) {
            list = list.stream().filter(r -> staffId.equals(r.getStaffId())).collect(Collectors.toList());
        }
        return !list.isEmpty();
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private MonitorRequestDto convertToDto(MonitorRequest entity) {
        MonitorRequestDto dto = new MonitorRequestDto();
        dto.setRequestId(entity.getRequestId());
        dto.setElderId(entity.getElderId());
        if (entity.getElderId() != null) {
            var elder = elderUserRepository.findByElderId(entity.getElderId());
            if (elder != null) {
                dto.setElderName(elder.getName());
            }
        }
        dto.setStaffId(entity.getStaffId());
        dto.setStaffName(entity.getStaffName());
        dto.setStaffPhone(entity.getStaffPhone());
        dto.setReason(entity.getReason());
        dto.setStatus(entity.getStatus());
        dto.setApprovedAt(entity.getApprovedAt() != null ? String.valueOf(entity.getApprovedAt()) : null);
        dto.setExpiredAt(entity.getExpiredAt() != null ? entity.getExpiredAt().toString() : null);
        dto.setCreateTime(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setUpdateTime(entity.getUpdateTime() != null ? entity.getUpdateTime().toString() : null);
        return dto;
    }

        /**
         * convertToEntity 方法。
         *
         * @param dto 字段含义待补充
         */
    private MonitorRequest convertToEntity(MonitorRequestDto dto) {
        MonitorRequest entity = new MonitorRequest();
        entity.setRequestId(dto.getRequestId());
        entity.setElderId(dto.getElderId());
        entity.setStaffId(dto.getStaffId());
        entity.setStaffName(dto.getStaffName());
        entity.setStaffPhone(dto.getStaffPhone());
        entity.setReason(dto.getReason());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
