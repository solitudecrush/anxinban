package com.anxinban.service;

import com.anxinban.entity.VlmRecord;
import com.anxinban.mapper.VlmRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * VLM 找物品记录服务。
 *
 * <p>对应数据字典：vlm_record</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class VlmRecordService {

    private static final Logger log = LoggerFactory.getLogger(VlmRecordService.class);
    private final VlmRecordRepository repository;

    @Autowired
    public VlmRecordService(VlmRecordRepository repository) {
        this.repository = repository;
    }

    public VlmRecord save(VlmRecord record) {
        try {
            if (record.getCreatedAt() == null) {
                record.setCreatedAt(LocalDateTime.now());
            }
            return repository.save(record);
        } catch (Exception e) {
            log.error("保存 VLM 记录失败: elderId={}, error={}", record.getElderId(), e.getMessage(), e);
            return null;
        }
    }

    public List<VlmRecord> listByElder(String elderId) {
        return repository.findByElderIdOrderByQueryTimeDesc(elderId);
    }

    public List<VlmRecord> listAll() {
        return repository.findAll();
    }
}
