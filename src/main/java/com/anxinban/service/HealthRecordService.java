package com.anxinban.service;

/**
 * HealthRecord 业务服务类，处理 HealthRecord 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.HealthRecordDto;
import com.anxinban.entity.HealthRecord;
import com.anxinban.mapper.HealthRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HealthRecordService {
    /** 字段含义待补充 */

    private final HealthRecordRepository healthRecordRepository;

    @Autowired
    public HealthRecordService(HealthRecordRepository healthRecordRepository) {
        this.healthRecordRepository = healthRecordRepository;
    }

        /**
         * createOrUpdateRecord 方法。
         *
         * @param dto 字段含义待补充
         */
    public HealthRecordDto createOrUpdateRecord(HealthRecordDto dto) {
        HealthRecord existing = healthRecordRepository.findByElderId(dto.getElderId());
        if (existing == null) {
            existing = convertToEntity(dto);
            existing.setCreatedAt(LocalDateTime.now());
        }
        if (dto.getHospitalizationInfo() != null) existing.setHospitalizationInfo(dto.getHospitalizationInfo());
        if (dto.getMedicalHistory() != null) existing.setMedicalHistory(dto.getMedicalHistory());
        if (dto.getAllergyHistory() != null) existing.setAllergyHistory(dto.getAllergyHistory());
        if (dto.getCommonMedications() != null) existing.setCommonMedications(dto.getCommonMedications());
        if (dto.getBloodType() != null) existing.setBloodType(dto.getBloodType());
        if (dto.getRemarks() != null) existing.setRemarks(dto.getRemarks());
        existing.setUpdatedAt(LocalDateTime.now());
        HealthRecord saved = healthRecordRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * 获取关联老人用户 ID。
         *
         * @return 关联老人用户 ID
         */
    public HealthRecordDto getRecordByElderId(String elderId) {
        HealthRecord entity = healthRecordRepository.findByElderId(elderId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public HealthRecordDto getRecord(String recordId) {
        HealthRecord entity = healthRecordRepository.findByRecordId(recordId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private HealthRecordDto convertToDto(HealthRecord entity) {
        HealthRecordDto dto = new HealthRecordDto();
        dto.setRecordId(entity.getRecordId());
        dto.setElderId(entity.getElderId());
        dto.setHospitalizationInfo(entity.getHospitalizationInfo());
        dto.setMedicalHistory(entity.getMedicalHistory());
        dto.setAllergyHistory(entity.getAllergyHistory());
        dto.setCommonMedications(entity.getCommonMedications());
        dto.setBloodType(entity.getBloodType());
        dto.setRemarks(entity.getRemarks());
        return dto;
    }

        /**
         * convertToEntity 方法。
         *
         * @param dto 字段含义待补充
         */
    private HealthRecord convertToEntity(HealthRecordDto dto) {
        HealthRecord entity = new HealthRecord();
        entity.setRecordId(dto.getRecordId());
        entity.setElderId(dto.getElderId());
        entity.setHospitalizationInfo(dto.getHospitalizationInfo());
        entity.setMedicalHistory(dto.getMedicalHistory());
        entity.setAllergyHistory(dto.getAllergyHistory());
        entity.setCommonMedications(dto.getCommonMedications());
        entity.setBloodType(dto.getBloodType());
        entity.setRemarks(dto.getRemarks());
        return entity;
    }
}
