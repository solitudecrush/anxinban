package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.entity.SensorData;
import com.anxinban.mapper.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 比赛兼容层 - 健康数据接口。
 *
 * <p>提供 snake_case 格式的最新健康数据查询，兼容前端 elderly.html。
 * 全部体征（心率/血氧/体温/血压）统一从 sensor_data 读取。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/health")
public class HealthCompatController {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    /**
     * 查询指定老人最新健康数据。
     *
     * @param elderId 老人唯一标识
     * @return 最新健康指标（snake_case）
     */
    @GetMapping("/latest/{elder_id}")
    public ApiResponse<Map<String, Object>> latestHealth(@PathVariable("elder_id") String elderId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("elder_id", elderId);

        // 心率：从 sensor_data 读取
        data.put("heart_rate", getLatestSensorInt(elderId, "heart_rate"));

        // 血氧：从 sensor_data 读取
        Double spo2 = getLatestSensorDouble(elderId, "spo2");
        data.put("spo2", spo2 != null ? spo2.intValue() : null);

        // 体温：从 sensor_data 读取
        data.put("temperature", getLatestSensorDouble(elderId, "temperature"));

        // 血压从 sensor_data 配对获取
        Integer sys = getLatestSensorInt(elderId, "blood_pressure_sys");
        Integer dia = getLatestSensorInt(elderId, "blood_pressure_dia");
        if (sys != null || dia != null) {
            data.put("blood_pressure", (sys != null ? sys : "-") + "/" + (dia != null ? dia : "-"));
        } else {
            data.put("blood_pressure", "");
        }

        // 活动状态和跌倒状态（从 sensor_data 读取并反向映射为文本）
        Double activityVal = getLatestSensorDouble(elderId, "activity_status");
        data.put("activity_status", mapActivityFromDouble(activityVal));

        Double fallVal = getLatestSensorDouble(elderId, "fall_status");
        data.put("fall_status", mapFallFromDouble(fallVal));

        // 最新数据时间
        String latestTime = getLatestSensorTime(elderId, "heart_rate");
        if (latestTime == null || latestTime.isEmpty()) {
            latestTime = getLatestSensorTime(elderId, "spo2");
        }
        if (latestTime == null || latestTime.isEmpty()) {
            latestTime = getLatestSensorTime(elderId, "temperature");
        }
        data.put("created_at", latestTime != null ? latestTime : "");

        return ApiResponse.success(data);
    }

    private Integer getLatestSensorInt(String elderId, String sensorType) {
        List<SensorData> list = sensorDataRepository.findByElderId(elderId);
        if (list == null) return null;
        return list.stream()
                .filter(s -> sensorType.equals(s.getSensorType()))
                .max(Comparator.comparing(SensorData::getTimestamp))
                .map(s -> s.getValue() != null ? s.getValue().intValue() : null)
                .orElse(null);
    }

    private Double getLatestSensorDouble(String elderId, String sensorType) {
        List<SensorData> list = sensorDataRepository.findByElderId(elderId);
        if (list == null) return null;
        return list.stream()
                .filter(s -> sensorType.equals(s.getSensorType()))
                .max(Comparator.comparing(SensorData::getTimestamp))
                .map(SensorData::getValue)
                .orElse(null);
    }

    private String getLatestSensorTime(String elderId, String sensorType) {
        List<SensorData> list = sensorDataRepository.findByElderId(elderId);
        if (list == null) return null;
        return list.stream()
                .filter(s -> sensorType.equals(s.getSensorType()))
                .max(Comparator.comparing(SensorData::getTimestamp))
                .map(s -> {
                    String ts = s.getTimestamp() != null ? s.getTimestamp().toString() : null;
                    return ts != null && ts.length() >= 19 ? ts.substring(0, 19) : ts;
                })
                .orElse(null);
    }

    private String mapActivityFromDouble(Double val) {
        if (val == null) return "";
        int v = val.intValue();
        switch (v) {
            case 1: return "行走";
            case 2: return "坐着";
            case 3: return "长时间静止";
            default: return "";
        }
    }

    private String mapFallFromDouble(Double val) {
        if (val == null) return "";
        int v = val.intValue();
        switch (v) {
            case 1: return "疑似跌倒";
            case 2: return "跌倒";
            default: return "正常";
        }
    }
}
