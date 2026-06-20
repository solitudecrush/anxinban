package com.anxinban.mapper;

import com.anxinban.entity.CameraViewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 监控查看记录 Repository
 */
@Repository
public interface CameraViewRecordRepository extends JpaRepository<CameraViewRecord, Long> {

    List<CameraViewRecord> findByCameraRequestId(String cameraRequestId);

    List<CameraViewRecord> findByStaffId(String staffId);

    List<CameraViewRecord> findByCameraRequestIdOrderByViewTimeDesc(String cameraRequestId);
}
