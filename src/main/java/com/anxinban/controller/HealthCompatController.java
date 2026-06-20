package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.entity.BloodPressure;
import com.anxinban.entity.SensorData;
import com.anxinban.mapper.BloodPressureRepository;
import com.anxinban.mapper.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 比赛兼容层 - 健康数据接口。
 *
 * <p>提供 snake_case 格式的最新健康数据查询，兼容前端 elderly.html。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/health")
public class HealthCompatController {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Autowired
    private BloodPressureRepository bloodPressureRepository;

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

        // 从 sensor_data 中获取各项最新值
        data.put("heart_rate", getLatestSensorDouble(elderId, "heart_rate"));
        data.put("spo2", getLatestSensorDouble(elderId, "spo2"));
        data.put("temperature", getLatestSensorDouble(elderId, "temperature"));

        // 血压从 blood_pressure 表获取
        BloodPressure latestBp = bloodPressureRepository.findFirstByElderIdOrderByTimestampDesc(elderId);
        if (latestBp != null) {
            data.put("blood_pressure", latestBp.getSystolic() + "/" + latestBp.getDiastolic());
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

    /**
     * 获取某个 elder 的指定 sensor_type 的最新值。
     */
    private Double getLatestSensorDouble(String elderId, String sensorType) {
        List<SensorData> list = sensorDataRepository.findByElderId(elderId);
        if (list == null) return null;
        return list.stream()
                .filter(s -> sensorType.equals(s.getSensorType()))
                .max(Comparator.comparing(SensorData::getTimestamp))
                .map(SensorData::getValue)
                .orElse(null);
    }

    /**
     * 获取某个 elder 的指定 sensor_type 的最新记录时间。
     */
    private String getLatestSensorTime(String elderId, String sensorType) {
        List<SensorData> list = sensorDataRepository.findByElderId(elderId);
        if (list == null) return null;
        return list.stream()
                .filter(s -> sensorType.equals(s.getSensorType()))
                .max(Comparator.comparing(SensorData::getTimestamp))
                .map(s -> {
                    String ts = s.getTimestamp().toString();
                    return ts.length() >= 19 ? ts.substring(0, 19) : ts;
                })
                .orElse(null);
    }

    /**
     * 将活动状态数值反向映射为中文文本。
     */
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

    /**
     * 将跌倒状态数值反向映射为中文文本。
     */
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
