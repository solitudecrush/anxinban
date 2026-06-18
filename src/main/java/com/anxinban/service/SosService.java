package com.anxinban.service;

/**
 * Sos 业务服务类，处理 Sos 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.SosDto;
import com.anxinban.entity.SosRecord;
import com.anxinban.mapper.SosRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SosService {
    /** 字段含义待补充 */

    private final SosRecordRepository sosRecordRepository;

    @Autowired
    public SosService(SosRecordRepository sosRecordRepository) {
        this.sosRecordRepository = sosRecordRepository;
    }

        /**
         * triggerSos 方法。
         *
         * @param dto 字段含义待补充
         */
    public SosDto triggerSos(SosDto dto) {
        SosRecord entity = new SosRecord();
        entity.setSosId(dto.getSosId());
        entity.setElderId(dto.getElderId());
        entity.setTriggerTime(LocalDateTime.now());
        entity.setStatus("triggered");
        entity.setLocation(dto.getLocation());
        entity.setCreatedAt(LocalDateTime.now());
        SosRecord saved = sosRecordRepository.save(entity);
        return convertToDto(saved);
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public SosDto getSos(String sosId) {
        SosRecord entity = sosRecordRepository.findBySosId(sosId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * listSosByElder 方法。
         *
         * @param elderId 关联老人用户 ID
         */
    public List<SosDto> listSosByElder(String elderId) {
        return sosRecordRepository.findByElderIdOrderByTriggerTimeDesc(elderId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

        /**
         * handleSos 方法。
         *
         * @param handlerId 唯一标识，主键
         */
    public SosDto handleSos(String sosId, String handlerId) {
        SosRecord existing = sosRecordRepository.findBySosId(sosId);
        if (existing == null) {
            return null;
        }
        existing.setStatus("handled");
        existing.setHandlerId(handlerId);
        existing.setHandledTime(LocalDateTime.now());
        SosRecord saved = sosRecordRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private SosDto convertToDto(SosRecord entity) {
        SosDto dto = new SosDto();
        dto.setSosId(entity.getSosId());
        dto.setElderId(entity.getElderId());
        dto.setTriggerTime(entity.getTriggerTime() != null ? entity.getTriggerTime().toString() : null);
        dto.setStatus(entity.getStatus());
        dto.setLocation(entity.getLocation());
        dto.setHandlerId(entity.getHandlerId());
        dto.setHandledTime(entity.getHandledTime() != null ? entity.getHandledTime().toString() : null);
        return dto;
    }
}
