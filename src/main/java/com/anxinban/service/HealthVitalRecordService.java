package com.anxinban.service;

import com.anxinban.entity.HealthVitalRecord;
import com.anxinban.mapper.HealthVitalRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康体征记录服务 — 存储心率、血压、血氧、体温等日常体征数据。
 *
 * <p>对应数据字典：health_record（体征部分）</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class HealthVitalRecordService {

    private static final Logger log = LoggerFactory.getLogger(HealthVitalRecordService.class);
    private final HealthVitalRecordRepository repository;

    @Autowired
    public HealthVitalRecordService(HealthVitalRecordRepository repository) {
        this.repository = repository;
    }

    public HealthVitalRecord save(HealthVitalRecord record) {
        try {
            if (record.getCreatedAt() == null) {
                record.setCreatedAt(LocalDateTime.now());
            }
            return repository.save(record);
        } catch (Exception e) {
            log.error("保存体征记录失败: elderId={}, error={}", record.getElderId(), e.getMessage(), e);
            return null;
        }
    }

    public List<HealthVitalRecord> listByElder(String elderId) {
        return repository.findByElderIdOrderByMeasuredAtDesc(elderId);
    }

    public List<HealthVitalRecord> listByElderAndDateRange(String elderId, LocalDateTime start, LocalDateTime end) {
        return repository.findByElderIdAndMeasuredAtBetween(elderId, start, end);
    }

    public HealthVitalRecord getLatestByElder(String elderId) {
        return repository.findTopByElderIdOrderByMeasuredAtDesc(elderId);
    }
}
