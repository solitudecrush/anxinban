package com.anxinban.mapper;


/**
 * SensorData 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.SensorData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    List<SensorData> findByDeviceId(String deviceId);
    List<SensorData> findByElderId(String elderId);
    List<SensorData> findBySensorType(String sensorType);
    List<SensorData> findByDeviceIdAndSensorType(String deviceId, String sensorType);
    List<SensorData> findByDeviceIdAndTimestampBetween(String deviceId, LocalDateTime start, LocalDateTime end);
    List<SensorData> findByElderIdAndSensorTypeAndTimestampBetween(String elderId, String sensorType, LocalDateTime start, LocalDateTime end);
    List<SensorData> findByDeviceIdAndSensorTypeAndTimestampBetween(String deviceId, String sensorType, LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据传感器类型和时间范围查询，按时间戳降序排列
     */
    List<SensorData> findBySensorTypeAndTimestampBetweenOrderByTimestampDesc(
            String sensorType, LocalDateTime start, LocalDateTime end, Pageable pageable);
}