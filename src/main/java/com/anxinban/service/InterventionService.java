package com.anxinban.service;

/**
 * Intervention 业务服务类，处理 Intervention 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.InterventionDto;
import com.anxinban.entity.MusicIntervention;
import com.anxinban.mapper.MusicInterventionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterventionService {
    /** 字段含义待补充 */

    private final MusicInterventionRepository musicInterventionRepository;

    @Autowired
    public InterventionService(MusicInterventionRepository musicInterventionRepository) {
        this.musicInterventionRepository = musicInterventionRepository;
    }

        /**
         * createIntervention 方法。
         *
         * @param dto 字段含义待补充
         */
    public InterventionDto createIntervention(InterventionDto dto) {
        MusicIntervention entity = convertToEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        MusicIntervention saved = musicInterventionRepository.save(entity);
        return convertToDto(saved);
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public InterventionDto getIntervention(String interventionId) {
        MusicIntervention entity = musicInterventionRepository.findByInterventionId(interventionId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * listInterventions 方法。
         *
         * @param size 大小
         */
    public List<InterventionDto> listInterventions(String elderId, String status, String priority, int page, int size) {
        List<MusicIntervention> entities;
        if (elderId != null) {
            entities = musicInterventionRepository.findByElderId(elderId);
        } else {
            entities = musicInterventionRepository.findAll();
        }
        List<InterventionDto> dtos = entities.stream().map(this::convertToDto).collect(Collectors.toList());
        return paginate(dtos, page, size);
    }

        /**
         * updateIntervention 方法。
         *
         * @param dto 字段含义待补充
         */
    public InterventionDto updateIntervention(String interventionId, InterventionDto dto) {
        MusicIntervention existing = musicInterventionRepository.findByInterventionId(interventionId);
        if (existing == null) {
            return null;
        }
        if (dto.getStatus() != null) existing.setResult(dto.getStatus());
        if (dto.getRemark() != null) existing.setReason(dto.getRemark());
        if (dto.getOperator() != null) existing.setResult(dto.getOperator());
        existing.setUpdatedAt(LocalDateTime.now());
        MusicIntervention saved = musicInterventionRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * completeIntervention 方法。
         *
         * @param completeTime 时间
         */
    public InterventionDto completeIntervention(String interventionId, String status, String result, String completeTime) {
        MusicIntervention existing = musicInterventionRepository.findByInterventionId(interventionId);
        if (existing == null) {
            return null;
        }
        existing.setResult(result != null ? result : status);
        if (completeTime != null) {
            existing.setAfterState(completeTime);
        }
        existing.setUpdatedAt(LocalDateTime.now());
        MusicIntervention saved = musicInterventionRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private InterventionDto convertToDto(MusicIntervention entity) {
        InterventionDto dto = new InterventionDto();
        dto.setInterventionId(entity.getInterventionId());
        dto.setElderId(entity.getElderId());
        dto.setTriggeredBy(entity.getReason());
        dto.setType(entity.getMusicType());
        dto.setStatus(entity.getResult());
        dto.setCreateTime(entity.getPromptTime() != null ? entity.getPromptTime().toString() : null);
        dto.setUpdateTime(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        dto.setRemark(entity.getBeforeState());
        dto.setResult(entity.getResult());
        dto.setCompleteTime(entity.getAfterState());
        return dto;
    }

        /**
         * convertToEntity 方法。
         *
         * @param dto 字段含义待补充
         */
    private MusicIntervention convertToEntity(InterventionDto dto) {
        MusicIntervention entity = new MusicIntervention();
        entity.setInterventionId(dto.getInterventionId());
        entity.setElderId(dto.getElderId());
        entity.setReason(dto.getTriggeredBy());
        entity.setMusicType(dto.getType());
        if (dto.getCreateTime() != null) {
            entity.setPromptTime(LocalDateTime.parse(dto.getCreateTime().substring(0, 19)));
        }
        entity.setBeforeState(dto.getRemark());
        entity.setAfterState(dto.getCompleteTime());
        entity.setResult(dto.getResult());
        return entity;
    }

        /**
         * paginate 方法。
         *
         * @param size 大小
         */
    private List<InterventionDto> paginate(List<InterventionDto> items, int page, int size) {
        int from = Math.max((page - 1) * size, 0);
        int to = Math.min(from + size, items.size());
        if (from > items.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(items.subList(from, to));
    }
}
