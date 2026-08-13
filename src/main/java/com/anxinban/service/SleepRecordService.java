package com.anxinban.service;

import com.anxinban.entity.SleepRecord;
import com.anxinban.mapper.SleepRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 睡眠记录服务。
 *
 * <p>对应数据字典：sleep_record</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class SleepRecordService {

    private static final Logger log = LoggerFactory.getLogger(SleepRecordService.class);
    private final SleepRecordRepository repository;

    @Autowired
    public SleepRecordService(SleepRecordRepository repository) {
        this.repository = repository;
    }

    public SleepRecord save(SleepRecord record) {
        try {
            if (record.getCreatedAt() == null) {
                record.setCreatedAt(LocalDateTime.now());
            }
            return repository.save(record);
        } catch (Exception e) {
            log.error("保存睡眠记录失败: elderId={}, error={}", record.getElderId(), e.getMessage(), e);
            return null;
        }
    }

    public List<SleepRecord> listByElder(String elderId) {
        return repository.findByElderIdOrderByRecordedAtDesc(elderId);
    }

    /**
     * 查询全部老人的睡眠记录（最新在前）。
     * 供 Web 端社区总览使用（不带 elderId 的全量查询）。
     */
    public List<SleepRecord> listAll() {
        List<SleepRecord> list = repository.findAll();
        list.sort(Comparator.comparing(SleepRecord::getRecordedAt,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return list;
    }

    public List<SleepRecord> listByElderAndDateRange(String elderId, LocalDateTime start, LocalDateTime end) {
        List<SleepRecord> list = repository.findByElderIdAndRecordedAtBetween(elderId, start, end);
        list.sort(Comparator.comparing(SleepRecord::getRecordedAt));
        return list;
    }
}
