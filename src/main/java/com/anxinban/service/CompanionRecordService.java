package com.anxinban.service;

import com.anxinban.entity.AiServiceRecord;
import com.anxinban.entity.CompanionRecord;
import com.anxinban.mapper.AiServiceRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 陪伴交互记录服务 — 底层数据存储在 ai_service_record 表（service_type='companion_chat'）。
 * API 接口保持兼容，仍返回 CompanionRecord 格式。
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class CompanionRecordService {

    private static final Logger log = LoggerFactory.getLogger(CompanionRecordService.class);
    private final AiServiceRecordRepository repository;

    @Autowired
    public CompanionRecordService(AiServiceRecordRepository repository) {
        this.repository = repository;
    }

    public CompanionRecord save(CompanionRecord record) {
        try {
            AiServiceRecord ai = new AiServiceRecord();
            ai.setRecordId("aisr_" + UUID.randomUUID().toString().substring(0, 8));
            ai.setElderId(record.getElderId());
            ai.setServiceType("companion_chat");
            ai.setEmotion(record.getEmotion());
            ai.setEmotionColor(record.getEmotionColor());
            ai.setSummary(record.getSummary());
            ai.setInteractionTime(record.getInteractionTime() != null ? record.getInteractionTime() : LocalDateTime.now());
            ai.setCreatedAt(record.getCreatedAt() != null ? record.getCreatedAt() : LocalDateTime.now());
            AiServiceRecord saved = repository.save(ai);
            return toCompanionRecord(saved);
        } catch (Exception e) {
            log.error("保存陪伴记录失败: elderId={}, error={}", record.getElderId(), e.getMessage(), e);
            return null;
        }
    }

    public List<CompanionRecord> listByElder(String elderId) {
        return repository.findByElderIdAndServiceTypeOrderByInteractionTimeDesc(elderId, "companion_chat").stream()
                .map(this::toCompanionRecord)
                .collect(Collectors.toList());
    }

    public List<CompanionRecord> listAll() {
        return repository.findByServiceTypeOrderByInteractionTimeDesc("companion_chat").stream()
                .map(this::toCompanionRecord)
                .sorted(Comparator.comparing(CompanionRecord::getInteractionTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private CompanionRecord toCompanionRecord(AiServiceRecord ai) {
        CompanionRecord r = new CompanionRecord();
        r.setId(ai.getId());
        r.setElderId(ai.getElderId());
        r.setEmotion(ai.getEmotion());
        r.setEmotionColor(ai.getEmotionColor());
        r.setSummary(ai.getSummary());
        r.setInteractionTime(ai.getInteractionTime());
        r.setCreatedAt(ai.getCreatedAt());
        return r;
    }
}
