package com.anxinban.service;

/**
 * ServiceRequest 业务服务类，处理 ServiceRequest 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.PageResult;
import com.anxinban.dto.ServiceRequestDto;
import com.anxinban.entity.ServiceRequest;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.mapper.FamilyUserRepository;
import com.anxinban.mapper.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRequestService {
    private final ServiceRequestRepository serviceRequestRepository;
    private final FamilyUserRepository familyUserRepository;
    /** 数据访问仓库，用于持久化操作 */
    private final ElderUserRepository elderUserRepository;

    @Autowired
    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository, FamilyUserRepository familyUserRepository, ElderUserRepository elderUserRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.familyUserRepository = familyUserRepository;
        this.elderUserRepository = elderUserRepository;
    }

        /**
         * createRequest 方法。
         *
         * @param dto 字段含义待补充
         */
    public ServiceRequestDto createRequest(ServiceRequestDto dto) {
        ServiceRequest entity = convertToEntity(dto);
        if (entity.getRequestId() == null || entity.getRequestId().isEmpty()) {
            entity.setRequestId("SR" + System.currentTimeMillis());
        }
        entity.setStatus("pending");
        entity.setRejectReason(dto.getRejectReason() != null ? dto.getRejectReason() : "");
        entity.setRelatedOrderId(dto.getRelatedOrderId() != null ? dto.getRelatedOrderId() : "");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        ServiceRequest saved = serviceRequestRepository.save(entity);
        return convertToDto(saved);
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public ServiceRequestDto getRequest(String requestId) {
        ServiceRequest entity = serviceRequestRepository.findByRequestId(requestId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * listRequestsByFamily 方法。
         *
         * @param familyId 关联家属用户 ID
         */
    public List<ServiceRequestDto> listRequestsByFamily(String familyId) {
        return serviceRequestRepository.findByFamilyId(familyId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

        /**
         * listRequests 方法。
         *
         * @param size 大小
         */
    public PageResult<ServiceRequestDto> listRequests(String requestType, String status, int page, int size) {
        List<ServiceRequest> entities;
        if (status != null && !status.isEmpty()) {
            entities = serviceRequestRepository.findByStatus(status);
        } else if (requestType != null && !requestType.isEmpty()) {
            entities = serviceRequestRepository.findByRequestType(requestType);
        } else {
            entities = serviceRequestRepository.findAll();
        }
        List<ServiceRequestDto> dtos = entities.stream().map(this::convertToDto).collect(Collectors.toList());
        long total = dtos.size();
        List<ServiceRequestDto> paginated = paginate(dtos, page, size);
        return new PageResult<>(paginated, total, page, size);
    }

        /**
         * convertToOrder 方法。
         *
         * @param orderId 排序
         */
    public ServiceRequestDto convertToOrder(String requestId, String orderId) {
        ServiceRequest existing = serviceRequestRepository.findByRequestId(requestId);
        if (existing == null) return null;
        existing.setStatus("converted");
        existing.setRelatedOrderId(orderId);
        existing.setUpdateTime(LocalDateTime.now());
        ServiceRequest saved = serviceRequestRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * rejectRequest 方法。
         *
         * @param reason 原因
         */
    public ServiceRequestDto rejectRequest(String requestId, String reason) {
        ServiceRequest existing = serviceRequestRepository.findByRequestId(requestId);
        if (existing == null) return null;
        existing.setStatus("rejected");
        existing.setRejectReason(reason);
        existing.setUpdateTime(LocalDateTime.now());
        ServiceRequest saved = serviceRequestRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private ServiceRequestDto convertToDto(ServiceRequest entity) {
        ServiceRequestDto dto = new ServiceRequestDto();
        dto.setRequestId(entity.getRequestId());
        dto.setFamilyId(entity.getFamilyId());
        if (entity.getFamilyId() != null) {
            var family = familyUserRepository.findByFamilyId(entity.getFamilyId());
            if (family != null) {
                dto.setFamilyName(family.getName());
            }
        }
        dto.setElderId(entity.getElderId());
        if (entity.getElderId() != null) {
            var elder = elderUserRepository.findByElderId(entity.getElderId());
            if (elder != null) {
                dto.setElderName(elder.getName());
            }
        }
        dto.setRequestType(entity.getRequestType());
        dto.setContent(entity.getContent());
        dto.setStatus(entity.getStatus());
        dto.setRelatedOrderId(entity.getRelatedOrderId());
        dto.setRejectReason(entity.getRejectReason());
        dto.setCreateTime(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setUpdateTime(entity.getUpdateTime() != null ? entity.getUpdateTime().toString() : null);
        return dto;
    }

        /**
         * convertToEntity 方法。
         *
         * @param dto 字段含义待补充
         */
    private ServiceRequest convertToEntity(ServiceRequestDto dto) {
        ServiceRequest entity = new ServiceRequest();
        entity.setRequestId(dto.getRequestId());
        entity.setFamilyId(dto.getFamilyId());
        entity.setElderId(dto.getElderId());
        entity.setRequestType(dto.getRequestType());
        entity.setContent(dto.getContent());
        entity.setStatus(dto.getStatus());
        entity.setRelatedOrderId(dto.getRelatedOrderId());
        entity.setRejectReason(dto.getRejectReason());
        return entity;
    }

        /**
         * paginate 方法。
         *
         * @param size 大小
         */
    private List<ServiceRequestDto> paginate(List<ServiceRequestDto> items, int page, int size) {
        int from = Math.max((page - 1) * size, 0);
        int to = Math.min(from + size, items.size());
        if (from > items.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(items.subList(from, to));
    }
}
