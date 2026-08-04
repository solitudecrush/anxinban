package com.anxinban.service;

import com.anxinban.entity.SensorData;
import com.anxinban.mapper.SensorDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 生命体征数据服务 — 全部体征（心率/血氧/体温/血压）统一从 sensor_data 读取。
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class VitalSignsService {

    private static final Logger log = LoggerFactory.getLogger(VitalSignsService.class);

    private final SensorDataRepository sensorDataRepository;

    @Autowired
    public VitalSignsService(SensorDataRepository sensorDataRepository) {
        this.sensorDataRepository = sensorDataRepository;
    }

    // ==================== 心率（sensor_data 中 sensor_type='heart_rate'） ====================

    public List<Map<String, Object>> listHeartRate(String elderId) {
        return sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "heart_rate".equals(s.getSensorType()))
                .sorted(Comparator.comparing(SensorData::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toHeartRateMap)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> listHeartRateByDateRange(String elderId, LocalDateTime start, LocalDateTime end) {
        return sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "heart_rate".equals(s.getSensorType()))
                .filter(s -> s.getTimestamp() != null && !s.getTimestamp().isBefore(start) && !s.getTimestamp().isAfter(end))
                .sorted(Comparator.comparing(SensorData::getTimestamp))
                .map(this::toHeartRateMap)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getLatestHeartRate(String elderId) {
        return sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "heart_rate".equals(s.getSensorType()))
                .max(Comparator.comparing(SensorData::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toHeartRateMap)
                .orElse(null);
    }

    private Map<String, Object> toHeartRateMap(SensorData s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getId());
        m.put("hrId", "hr_" + s.getId());
        m.put("elderId", s.getElderId());
        m.put("value", s.getValue() != null ? s.getValue().intValue() : null);
        m.put("unit", s.getUnit());
        m.put("timestamp", s.getTimestamp());
        m.put("createdAt", s.getCreatedAt());
        return m;
    }

    // ==================== 血氧（sensor_data 中 sensor_type='spo2'） ====================

    public List<Map<String, Object>> listBloodOxygen(String elderId) {
        return sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "spo2".equals(s.getSensorType()))
                .sorted(Comparator.comparing(SensorData::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toBloodOxygenMap)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> listBloodOxygenByDateRange(String elderId, LocalDateTime start, LocalDateTime end) {
        return sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "spo2".equals(s.getSensorType()))
                .filter(s -> s.getTimestamp() != null && !s.getTimestamp().isBefore(start) && !s.getTimestamp().isAfter(end))
                .sorted(Comparator.comparing(SensorData::getTimestamp))
                .map(this::toBloodOxygenMap)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getLatestBloodOxygen(String elderId) {
        return sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "spo2".equals(s.getSensorType()))
                .max(Comparator.comparing(SensorData::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toBloodOxygenMap)
                .orElse(null);
    }

    private Map<String, Object> toBloodOxygenMap(SensorData s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getId());
        m.put("boId", "bo_" + s.getId());
        m.put("elderId", s.getElderId());
        m.put("value", s.getValue());
        m.put("unit", s.getUnit());
        m.put("timestamp", s.getTimestamp());
        m.put("createdAt", s.getCreatedAt());
        return m;
    }

    // ==================== 体温（sensor_data 中 sensor_type='temperature'） ====================

    public List<Map<String, Object>> listBodyTemperature(String elderId) {
        return sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "temperature".equals(s.getSensorType()))
                .sorted(Comparator.comparing(SensorData::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toBodyTemperatureMap)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> listBodyTemperatureByDateRange(String elderId, LocalDateTime start, LocalDateTime end) {
        return sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "temperature".equals(s.getSensorType()))
                .filter(s -> s.getTimestamp() != null && !s.getTimestamp().isBefore(start) && !s.getTimestamp().isAfter(end))
                .sorted(Comparator.comparing(SensorData::getTimestamp))
                .map(this::toBodyTemperatureMap)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getLatestBodyTemperature(String elderId) {
        return sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "temperature".equals(s.getSensorType()))
                .max(Comparator.comparing(SensorData::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toBodyTemperatureMap)
                .orElse(null);
    }

    private Map<String, Object> toBodyTemperatureMap(SensorData s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getId());
        m.put("btId", "bt_" + s.getId());
        m.put("elderId", s.getElderId());
        m.put("value", s.getValue());
        m.put("unit", s.getUnit());
        m.put("timestamp", s.getTimestamp());
        m.put("createdAt", s.getCreatedAt());
        return m;
    }

    // ==================== 血压（sensor_data 中 sensor_type='blood_pressure_sys' + 'blood_pressure_dia'，按时间戳配对） ====================

    public List<Map<String, Object>> listBloodPressure(String elderId) {
        return pairBloodPressure(sensorDataRepository.findByElderId(elderId), null, null);
    }

    public List<Map<String, Object>> listBloodPressureByDateRange(String elderId, LocalDateTime start, LocalDateTime end) {
        return pairBloodPressure(sensorDataRepository.findByElderId(elderId), start, end);
    }

    public Map<String, Object> getLatestBloodPressure(String elderId) {
        List<Map<String, Object>> all = pairBloodPressure(sensorDataRepository.findByElderId(elderId), null, null);
        return all.isEmpty() ? null : all.get(0);
    }

    private List<Map<String, Object>> pairBloodPressure(List<SensorData> sensors, LocalDateTime start, LocalDateTime end) {
        // 按时间戳配对收缩压和舒张压
        Map<String, Integer> systolicMap = new LinkedHashMap<>();
        Map<String, Integer> diastolicMap = new LinkedHashMap<>();
        Map<String, LocalDateTime> timeMap = new LinkedHashMap<>();
        Map<String, Long> idMap = new LinkedHashMap<>();

        for (SensorData s : sensors) {
            if (s.getTimestamp() == null) continue;
            if (start != null && end != null && (s.getTimestamp().isBefore(start) || s.getTimestamp().isAfter(end))) continue;
            String key = s.getTimestamp().toString().substring(0, 19);
            if ("blood_pressure_sys".equals(s.getSensorType())) {
                systolicMap.put(key, s.getValue() != null ? s.getValue().intValue() : null);
            } else if ("blood_pressure_dia".equals(s.getSensorType())) {
                diastolicMap.put(key, s.getValue() != null ? s.getValue().intValue() : null);
            }
            timeMap.putIfAbsent(key, s.getTimestamp());
            idMap.putIfAbsent(key, s.getId());
        }

        // 合并 key -> bp record
        Set<String> allKeys = new TreeSet<>(Comparator.reverseOrder());
        allKeys.addAll(systolicMap.keySet());
        allKeys.addAll(diastolicMap.keySet());

        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : allKeys) {
            Map<String, Object> bp = new LinkedHashMap<>();
            bp.put("id", idMap.getOrDefault(key, 0L));
            bp.put("bpId", "bp_" + (idMap.containsKey(key) ? idMap.get(key) : key));
            bp.put("elderId", sensors.isEmpty() ? "" : sensors.get(0).getElderId());
            bp.put("systolic", systolicMap.getOrDefault(key, 0));
            bp.put("diastolic", diastolicMap.getOrDefault(key, 0));
            bp.put("timestamp", timeMap.get(key));
            bp.put("createdAt", timeMap.get(key));
            result.add(bp);
        }
        return result;
    }
}
