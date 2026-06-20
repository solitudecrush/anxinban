package com.anxinban.service;

import com.anxinban.entity.CompanionRecord;
import com.anxinban.mapper.CompanionRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 陪伴交互记录服务。
 *
 * <p>对应数据字典：companion_record</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class CompanionRecordService {

    private static final Logger log = LoggerFactory.getLogger(CompanionRecordService.class);
    private final CompanionRecordRepository repository;

    @Autowired
    public CompanionRecordService(CompanionRecordRepository repository) {
        this.repository = repository;
    }

    public CompanionRecord save(CompanionRecord record) {
        try {
            if (record.getCreatedAt() == null) {
                record.setCreatedAt(LocalDateTime.now());
            }
            return repository.save(record);
        } catch (Exception e) {
            log.error("保存陪伴记录失败: elderId={}, error={}", record.getElderId(), e.getMessage(), e);
            return null;
        }
    }

    public List<CompanionRecord> listByElder(String elderId) {
        return repository.findByElderIdOrderByInteractionTimeDesc(elderId);
    }

    public List<CompanionRecord> listAll() {
        return repository.findAll();
    }
}
