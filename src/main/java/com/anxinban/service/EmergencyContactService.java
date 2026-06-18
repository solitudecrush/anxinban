package com.anxinban.service;

/**
 * EmergencyContact 业务服务类，处理 EmergencyContact 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.EmergencyContactDto;
import com.anxinban.entity.EmergencyContact;
import com.anxinban.mapper.EmergencyContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmergencyContactService {
    /** 是否为紧急情况 */

    private final EmergencyContactRepository emergencyContactRepository;

    @Autowired
    public EmergencyContactService(EmergencyContactRepository emergencyContactRepository) {
        this.emergencyContactRepository = emergencyContactRepository;
    }

        /**
         * createContact 方法。
         *
         * @param dto 字段含义待补充
         */
    public EmergencyContactDto createContact(EmergencyContactDto dto) {
        EmergencyContact entity = convertToEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        EmergencyContact saved = emergencyContactRepository.save(entity);
        return convertToDto(saved);
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public EmergencyContactDto getContact(String contactId) {
        EmergencyContact entity = emergencyContactRepository.findByContactId(contactId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * listContactsByElder 方法。
         *
         * @param elderId 关联老人用户 ID
         */
    public List<EmergencyContactDto> listContactsByElder(String elderId) {
        return emergencyContactRepository.findByElderIdOrderBySortOrderAsc(elderId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

        /**
         * updateContact 方法。
         *
         * @param dto 字段含义待补充
         */
    public EmergencyContactDto updateContact(String contactId, EmergencyContactDto dto) {
        EmergencyContact existing = emergencyContactRepository.findByContactId(contactId);
        if (existing == null) {
            return null;
        }
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getPhone() != null) existing.setPhone(dto.getPhone());
        if (dto.getRelation() != null) existing.setRelation(dto.getRelation());
        if (dto.getIsPrimary() != null) existing.setIsPrimary(dto.getIsPrimary());
        if (dto.getSortOrder() != null) existing.setSortOrder(dto.getSortOrder());
        existing.setUpdateTime(LocalDateTime.now());
        EmergencyContact saved = emergencyContactRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * deleteContact 方法。
         *
         * @param contactId 唯一标识，主键
         */
    public boolean deleteContact(String contactId) {
        EmergencyContact existing = emergencyContactRepository.findByContactId(contactId);
        if (existing != null) {
            emergencyContactRepository.delete(existing);
            return true;
        }
        return false;
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private EmergencyContactDto convertToDto(EmergencyContact entity) {
        EmergencyContactDto dto = new EmergencyContactDto();
        dto.setContactId(entity.getContactId());
        dto.setElderId(entity.getElderId());
        dto.setName(entity.getName());
        dto.setPhone(entity.getPhone());
        dto.setRelation(entity.getRelation());
        dto.setIsPrimary(entity.getIsPrimary());
        dto.setSortOrder(entity.getSortOrder());
        return dto;
    }

        /**
         * convertToEntity 方法。
         *
         * @param dto 字段含义待补充
         */
    private EmergencyContact convertToEntity(EmergencyContactDto dto) {
        EmergencyContact entity = new EmergencyContact();
        entity.setContactId(dto.getContactId());
        entity.setElderId(dto.getElderId());
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setRelation(dto.getRelation());
        entity.setIsPrimary(dto.getIsPrimary());
        entity.setSortOrder(dto.getSortOrder());
        return entity;
    }
}
