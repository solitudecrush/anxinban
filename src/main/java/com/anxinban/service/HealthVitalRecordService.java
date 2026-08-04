package com.anxinban.service;

import com.anxinban.entity.HealthVitalRecord;
import com.anxinban.entity.SensorData;
import com.anxinban.mapper.SensorDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 健康体征记录服务 — 全部写入 sensor_data，读取时聚合 sensor_data。
 * 保留 HealthVitalRecord 作为 DTO 保持 API 兼容。
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class HealthVitalRecordService {

    private static final Logger log = LoggerFactory.getLogger(HealthVitalRecordService.class);
    private final SensorDataRepository sensorDataRepository;

    @Autowired
    public HealthVitalRecordService(SensorDataRepository sensorDataRepository) {
        this.sensorDataRepository = sensorDataRepository;
    }

    public HealthVitalRecord save(HealthVitalRecord record) {
        try {
            LocalDateTime now = record.getMeasuredAt() != null ? record.getMeasuredAt() : LocalDateTime.now();
            String elderId = record.getElderId();

            if (record.getHeartRate() != null) {
                SensorData sd = new SensorData();
                sd.setElderId(elderId);
                sd.setDeviceId("health_vital");
                sd.setSensorType("heart_rate");
                sd.setValue(record.getHeartRate().doubleValue());
                sd.setUnit("bpm");
                sd.setTimestamp(now);
                sd.setCreatedAt(LocalDateTime.now());
                sensorDataRepository.save(sd);
            }
            if (record.getSpo2() != null) {
                SensorData sd = new SensorData();
                sd.setElderId(elderId);
                sd.setDeviceId("health_vital");
                sd.setSensorType("spo2");
                sd.setValue(record.getSpo2().doubleValue());
                sd.setUnit("%");
                sd.setTimestamp(now);
                sd.setCreatedAt(LocalDateTime.now());
                sensorDataRepository.save(sd);
            }
            if (record.getTemperature() != null) {
                SensorData sd = new SensorData();
                sd.setElderId(elderId);
                sd.setDeviceId("health_vital");
                sd.setSensorType("temperature");
                sd.setValue(record.getTemperature().doubleValue());
                sd.setUnit("℃");
                sd.setTimestamp(now);
                sd.setCreatedAt(LocalDateTime.now());
                sensorDataRepository.save(sd);
            }
            if (record.getSystolic() != null) {
                SensorData sd = new SensorData();
                sd.setElderId(elderId);
                sd.setDeviceId("health_vital");
                sd.setSensorType("blood_pressure_sys");
                sd.setValue(record.getSystolic().doubleValue());
                sd.setUnit("mmHg");
                sd.setTimestamp(now);
                sd.setCreatedAt(LocalDateTime.now());
                sensorDataRepository.save(sd);
            }
            if (record.getDiastolic() != null) {
                SensorData sd = new SensorData();
                sd.setElderId(elderId);
                sd.setDeviceId("health_vital");
                sd.setSensorType("blood_pressure_dia");
                sd.setValue(record.getDiastolic().doubleValue());
                sd.setUnit("mmHg");
                sd.setTimestamp(now);
                sd.setCreatedAt(LocalDateTime.now());
                sensorDataRepository.save(sd);
            }
            return record;
        } catch (Exception e) {
            log.error("保存体征记录失败: elderId={}, error={}", record.getElderId(), e.getMessage(), e);
            return null;
        }
    }

    public List<HealthVitalRecord> listByElder(String elderId) {
        return aggregateFromSources(elderId, null, null);
    }

    public List<HealthVitalRecord> listByElderAndDateRange(String elderId, LocalDateTime start, LocalDateTime end) {
        return aggregateFromSources(elderId, start, end);
    }

    public HealthVitalRecord getLatestByElder(String elderId) {
        List<HealthVitalRecord> all = aggregateFromSources(elderId, null, null);
        return all.isEmpty() ? null : all.get(0);
    }

    private List<HealthVitalRecord> aggregateFromSources(String elderId, LocalDateTime start, LocalDateTime end) {
        // 收集 sensor_data 中的心率/血氧/体温，按时间戳分组
        Map<String, HealthVitalRecord> map = new LinkedHashMap<>();

        List<SensorData> sensors = sensorDataRepository.findByElderId(elderId);
        for (SensorData s : sensors) {
            if (start != null && end != null) {
                if (s.getTimestamp() == null || s.getTimestamp().isBefore(start) || s.getTimestamp().isAfter(end)) {
                    continue;
                }
            }
            String key = s.getTimestamp() != null ? s.getTimestamp().toString().substring(0, 19) : UUID.randomUUID().toString();
            HealthVitalRecord r = map.computeIfAbsent(key, k -> {
                HealthVitalRecord hvr = new HealthVitalRecord();
                hvr.setElderId(elderId);
                hvr.setMeasuredAt(s.getTimestamp());
                return hvr;
            });
            String type = s.getSensorType();
            if ("heart_rate".equals(type)) {
                r.setHeartRate(s.getValue() != null ? s.getValue().intValue() : null);
            } else if ("spo2".equals(type)) {
                r.setSpo2(s.getValue() != null ? s.getValue().intValue() : null);
            } else if ("temperature".equals(type)) {
                r.setTemperature(s.getValue() != null ? new java.math.BigDecimal(s.getValue().toString()) : null);
            }
        }

        // 收集 blood_pressure_sys / blood_pressure_dia 按时间戳配对
        Map<String, Integer> sysMap = new LinkedHashMap<>();
        Map<String, Integer> diaMap = new LinkedHashMap<>();
        for (SensorData s : sensors) {
            if (start != null && end != null) {
                if (s.getTimestamp() == null || s.getTimestamp().isBefore(start) || s.getTimestamp().isAfter(end)) continue;
            }
            String key = s.getTimestamp() != null ? s.getTimestamp().toString().substring(0, 19) : UUID.randomUUID().toString();
            if ("blood_pressure_sys".equals(s.getSensorType())) sysMap.put(key, s.getValue() != null ? s.getValue().intValue() : null);
            else if ("blood_pressure_dia".equals(s.getSensorType())) diaMap.put(key, s.getValue() != null ? s.getValue().intValue() : null);
        }
        Set<String> bpKeys = new TreeSet<>(sysMap.keySet());
        bpKeys.addAll(diaMap.keySet());
        for (String key : bpKeys) {
            HealthVitalRecord r = map.computeIfAbsent(key, k -> {
                HealthVitalRecord hvr = new HealthVitalRecord();
                hvr.setElderId(elderId);
                return hvr;
            });
            if (sysMap.containsKey(key)) r.setSystolic(sysMap.get(key));
            if (diaMap.containsKey(key)) r.setDiastolic(diaMap.get(key));
        }

        List<HealthVitalRecord> result = new ArrayList<>(map.values());
        result.sort(Comparator.comparing(HealthVitalRecord::getMeasuredAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }
}
