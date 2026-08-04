package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.service.VitalSignsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 生命体征 REST 控制器 — 心率、血压、血氧、体温独立查询接口。
 * 全部体征（心率/血氧/体温/血压）统一从 sensor_data 表读取。
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/vital-signs")
public class VitalSignsController {

    private final VitalSignsService service;

    @Autowired
    public VitalSignsController(VitalSignsService service) {
        this.service = service;
    }

    // ==================== 心率（sensor_data） ====================

    @GetMapping("/heart-rate/list")
    public ApiResponse<List<Map<String, Object>>> listHeartRate(
            @RequestParam String elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (start != null && end != null) {
            return ApiResponse.success(service.listHeartRateByDateRange(elderId, start, end));
        }
        return ApiResponse.success(service.listHeartRate(elderId));
    }

    @GetMapping("/heart-rate/latest")
    public ApiResponse<Map<String, Object>> latestHeartRate(@RequestParam String elderId) {
        Map<String, Object> record = service.getLatestHeartRate(elderId);
        if (record == null) {
            return ApiResponse.error(404, "未找到该老人的心率记录");
        }
        return ApiResponse.success(record);
    }

    // ==================== 血压（sensor_data） ====================

    @GetMapping("/blood-pressure/list")
    public ApiResponse<List<Map<String, Object>>> listBloodPressure(
            @RequestParam String elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (start != null && end != null) {
            return ApiResponse.success(service.listBloodPressureByDateRange(elderId, start, end));
        }
        return ApiResponse.success(service.listBloodPressure(elderId));
    }

    @GetMapping("/blood-pressure/latest")
    public ApiResponse<Map<String, Object>> latestBloodPressure(@RequestParam String elderId) {
        Map<String, Object> record = service.getLatestBloodPressure(elderId);
        if (record == null) {
            return ApiResponse.error(404, "未找到该老人的血压记录");
        }
        return ApiResponse.success(record);
    }

    // ==================== 血氧（sensor_data） ====================

    @GetMapping("/blood-oxygen/list")
    public ApiResponse<List<Map<String, Object>>> listBloodOxygen(
            @RequestParam String elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (start != null && end != null) {
            return ApiResponse.success(service.listBloodOxygenByDateRange(elderId, start, end));
        }
        return ApiResponse.success(service.listBloodOxygen(elderId));
    }

    @GetMapping("/blood-oxygen/latest")
    public ApiResponse<Map<String, Object>> latestBloodOxygen(@RequestParam String elderId) {
        Map<String, Object> record = service.getLatestBloodOxygen(elderId);
        if (record == null) {
            return ApiResponse.error(404, "未找到该老人的血氧记录");
        }
        return ApiResponse.success(record);
    }

    // ==================== 体温（sensor_data） ====================

    @GetMapping("/body-temperature/list")
    public ApiResponse<List<Map<String, Object>>> listBodyTemperature(
            @RequestParam String elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (start != null && end != null) {
            return ApiResponse.success(service.listBodyTemperatureByDateRange(elderId, start, end));
        }
        return ApiResponse.success(service.listBodyTemperature(elderId));
    }

    @GetMapping("/body-temperature/latest")
    public ApiResponse<Map<String, Object>> latestBodyTemperature(@RequestParam String elderId) {
        Map<String, Object> record = service.getLatestBodyTemperature(elderId);
        if (record == null) {
            return ApiResponse.error(404, "未找到该老人的体温记录");
        }
        return ApiResponse.success(record);
    }

    // ==================== 综合最新 ====================

    @GetMapping("/latest/{elderId}")
    public ApiResponse<Map<String, Object>> latestAll(@PathVariable String elderId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("elderId", elderId);

        Map<String, Object> hr = service.getLatestHeartRate(elderId);
        data.put("heartRate", hr != null ? hr.get("value") : null);
        data.put("heartRateUnit", hr != null ? hr.get("unit") : "次/分");
        data.put("heartRateTime", hr != null ? hr.get("timestamp") : null);

        Map<String, Object> bp = service.getLatestBloodPressure(elderId);
        data.put("systolic", bp != null ? bp.get("systolic") : null);
        data.put("diastolic", bp != null ? bp.get("diastolic") : null);
        data.put("bloodPressureUnit", "mmHg");
        data.put("bloodPressureTime", bp != null ? bp.get("timestamp") : null);

        Map<String, Object> bo = service.getLatestBloodOxygen(elderId);
        data.put("bloodOxygen", bo != null ? bo.get("value") : null);
        data.put("bloodOxygenUnit", bo != null ? bo.get("unit") : "%");
        data.put("bloodOxygenTime", bo != null ? bo.get("timestamp") : null);

        Map<String, Object> bt = service.getLatestBodyTemperature(elderId);
        data.put("bodyTemperature", bt != null ? bt.get("value") : null);
        data.put("bodyTemperatureUnit", bt != null ? bt.get("unit") : "℃");
        data.put("bodyTemperatureTime", bt != null ? bt.get("timestamp") : null);

        return ApiResponse.success(data);
    }
}
