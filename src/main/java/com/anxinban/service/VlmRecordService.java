package com.anxinban.service;

import com.anxinban.entity.AiServiceRecord;
import com.anxinban.entity.VlmRecord;
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
 * VLM 找物品记录服务 — 底层数据存储在 ai_service_record 表（service_type='find_item'）。
 * API 接口保持兼容，仍返回 VlmRecord 格式。
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class VlmRecordService {

    private static final Logger log = LoggerFactory.getLogger(VlmRecordService.class);
    private final AiServiceRecordRepository repository;

    @Autowired
    public VlmRecordService(AiServiceRecordRepository repository) {
        this.repository = repository;
    }

    public VlmRecord save(VlmRecord record) {
        try {
            AiServiceRecord ai = new AiServiceRecord();
            ai.setRecordId("aisr_" + UUID.randomUUID().toString().substring(0, 8));
            ai.setElderId(record.getElderId());
            ai.setServiceType("find_item");
            ai.setItem(record.getItem());
            ai.setLocation(record.getLocation());
            ai.setUserText(record.getQuestion());
            ai.setAiReply(record.getAnswer());
            ai.setResult(record.getResult());
            ai.setInteractionTime(record.getQueryTime() != null ? record.getQueryTime() : LocalDateTime.now());
            ai.setCreatedAt(record.getCreatedAt() != null ? record.getCreatedAt() : LocalDateTime.now());
            AiServiceRecord saved = repository.save(ai);
            return toVlmRecord(saved);
        } catch (Exception e) {
            log.error("保存 VLM 记录失败: elderId={}, error={}", record.getElderId(), e.getMessage(), e);
            return null;
        }
    }

    public List<VlmRecord> listByElder(String elderId) {
        return repository.findByElderIdAndServiceTypeOrderByInteractionTimeDesc(elderId, "find_item").stream()
                .map(this::toVlmRecord)
                .collect(Collectors.toList());
    }

    public List<VlmRecord> listAll() {
        return repository.findByServiceTypeOrderByInteractionTimeDesc("find_item").stream()
                .map(this::toVlmRecord)
                .collect(Collectors.toList());
    }

    private VlmRecord toVlmRecord(AiServiceRecord ai) {
        VlmRecord r = new VlmRecord();
        r.setId(ai.getId());
        r.setElderId(ai.getElderId());
        r.setItem(ai.getItem());
        r.setLocation(ai.getLocation());
        r.setQuestion(ai.getUserText());
        r.setAnswer(ai.getAiReply());
        r.setResult(ai.getResult());
        r.setQueryTime(ai.getInteractionTime());
        r.setCreatedAt(ai.getCreatedAt());
        return r;
    }
}
