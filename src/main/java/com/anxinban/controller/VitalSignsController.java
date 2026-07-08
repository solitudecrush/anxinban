package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.entity.BloodOxygen;
import com.anxinban.entity.BloodPressure;
import com.anxinban.entity.BodyTemperature;
import com.anxinban.entity.HeartRate;
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

    // ==================== 心率 ====================

    @GetMapping("/heart-rate/list")
    public ApiResponse<List<HeartRate>> listHeartRate(
            @RequestParam String elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (start != null && end != null) {
            return ApiResponse.success(service.listHeartRateByDateRange(elderId, start, end));
        }
        return ApiResponse.success(service.listHeartRate(elderId));
    }

    @GetMapping("/heart-rate/latest")
    public ApiResponse<HeartRate> latestHeartRate(@RequestParam String elderId) {
        HeartRate record = service.getLatestHeartRate(elderId);
        if (record == null) {
            return ApiResponse.error(404, "未找到该老人的心率记录");
        }
        return ApiResponse.success(record);
    }

    // ==================== 血压 ====================

    @GetMapping("/blood-pressure/list")
    public ApiResponse<List<BloodPressure>> listBloodPressure(
            @RequestParam String elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (start != null && end != null) {
            return ApiResponse.success(service.listBloodPressureByDateRange(elderId, start, end));
        }
        return ApiResponse.success(service.listBloodPressure(elderId));
    }

    @GetMapping("/blood-pressure/latest")
    public ApiResponse<BloodPressure> latestBloodPressure(@RequestParam String elderId) {
        BloodPressure record = service.getLatestBloodPressure(elderId);
        if (record == null) {
            return ApiResponse.error(404, "未找到该老人的血压记录");
        }
        return ApiResponse.success(record);
    }

    // ==================== 血氧 ====================

    @GetMapping("/blood-oxygen/list")
    public ApiResponse<List<BloodOxygen>> listBloodOxygen(
            @RequestParam String elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (start != null && end != null) {
            return ApiResponse.success(service.listBloodOxygenByDateRange(elderId, start, end));
        }
        return ApiResponse.success(service.listBloodOxygen(elderId));
    }

    @GetMapping("/blood-oxygen/latest")
    public ApiResponse<BloodOxygen> latestBloodOxygen(@RequestParam String elderId) {
        BloodOxygen record = service.getLatestBloodOxygen(elderId);
        if (record == null) {
            return ApiResponse.error(404, "未找到该老人的血氧记录");
        }
        return ApiResponse.success(record);
    }

    // ==================== 体温 ====================

    @GetMapping("/body-temperature/list")
    public ApiResponse<List<BodyTemperature>> listBodyTemperature(
            @RequestParam String elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (start != null && end != null) {
            return ApiResponse.success(service.listBodyTemperatureByDateRange(elderId, start, end));
        }
        return ApiResponse.success(service.listBodyTemperature(elderId));
    }

    @GetMapping("/body-temperature/latest")
    public ApiResponse<BodyTemperature> latestBodyTemperature(@RequestParam String elderId) {
        BodyTemperature record = service.getLatestBodyTemperature(elderId);
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

        HeartRate hr = service.getLatestHeartRate(elderId);
        data.put("heartRate", hr != null ? hr.getValue() : null);
        data.put("heartRateUnit", hr != null ? hr.getUnit() : "次/分");
        data.put("heartRateTime", hr != null ? hr.getTimestamp().toString() : null);

        BloodPressure bp = service.getLatestBloodPressure(elderId);
        data.put("systolic", bp != null ? bp.getSystolic() : null);
        data.put("diastolic", bp != null ? bp.getDiastolic() : null);
        data.put("bloodPressureUnit", "mmHg");
        data.put("bloodPressureTime", bp != null ? bp.getTimestamp().toString() : null);

        BloodOxygen bo = service.getLatestBloodOxygen(elderId);
        data.put("bloodOxygen", bo != null ? bo.getValue() : null);
        data.put("bloodOxygenUnit", bo != null ? bo.getUnit() : "%");
        data.put("bloodOxygenTime", bo != null ? bo.getTimestamp().toString() : null);

        BodyTemperature bt = service.getLatestBodyTemperature(elderId);
        data.put("bodyTemperature", bt != null ? bt.getValue() : null);
        data.put("bodyTemperatureUnit", bt != null ? bt.getUnit() : "℃");
        data.put("bodyTemperatureTime", bt != null ? bt.getTimestamp().toString() : null);

        return ApiResponse.success(data);
    }
}
