package com.anxinban.service;

import com.anxinban.entity.CameraViewRecord;
import com.anxinban.mapper.CameraViewRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 监控查看记录服务。
 *
 * <p>对应数据字典：camera_view_record</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class CameraViewRecordService {

    private static final Logger log = LoggerFactory.getLogger(CameraViewRecordService.class);
    private final CameraViewRecordRepository repository;

    @Autowired
    public CameraViewRecordService(CameraViewRecordRepository repository) {
        this.repository = repository;
    }

    public CameraViewRecord save(CameraViewRecord record) {
        try {
            if (record.getCreatedAt() == null) {
                record.setCreatedAt(LocalDateTime.now());
            }
            return repository.save(record);
        } catch (Exception e) {
            log.error("保存监控查看记录失败: cameraRequestId={}, error={}", record.getCameraRequestId(), e.getMessage(), e);
            return null;
        }
    }

    public List<CameraViewRecord> listByCameraRequest(String cameraRequestId) {
        return repository.findByCameraRequestIdOrderByViewTimeDesc(cameraRequestId);
    }

    public List<CameraViewRecord> listByStaff(String staffId) {
        return repository.findByStaffId(staffId);
    }
}
