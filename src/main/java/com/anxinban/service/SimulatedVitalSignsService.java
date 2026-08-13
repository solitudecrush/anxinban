package com.anxinban.service;

import com.anxinban.dto.HealthLatestDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * App 首页四体征模拟数据服务。
 *
 * <p>每 1 秒生成一组小幅波动的新值（随机游走），模拟正常老年人体征：
 * 心率偏高可至 130（70-130 次/分）、血压偏高但不太高（125-150 / 78-95 mmHg）、
 * 血氧正常（94-99%）、体温正常波动（36.4-37.4℃）。
 * 每次刷新数值必有变化，波动幅度保持在正常区间内，时间戳随每组数据同步刷新。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class SimulatedVitalSignsService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 刷新间隔：1 秒 */
    private static final long REFRESH_MILLIS = 1000;

    // 各体征波动区间
    private static final int HR_MIN = 70, HR_MAX = 130;             // 心率：可偏高至 130
    private static final int SYS_MIN = 125, SYS_MAX = 150;          // 收缩压：偏高但不超限
    private static final int DIA_MIN = 78, DIA_MAX = 95;            // 舒张压：偏高但不超限
    private static final int SPO2_MIN = 94, SPO2_MAX = 99;          // 血氧：正常，波动稍大（±2）
    private static final double TEMP_MIN = 36.4, TEMP_MAX = 37.4;   // 体温：正常波动

    private final Map<String, Reading> readings = new ConcurrentHashMap<>();

    /**
     * 获取指定老人最新一组模拟体征（每 1 秒波动一次）。
     * 返回结构与 /api/vital-signs/latest/{elderId} 一致。
     */
    public Map<String, Object> getLatest(String elderId) {
        Reading reading = getOrRefresh(elderId);
        String time = formatTime(reading);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("elderId", elderId);

        data.put("heartRate", reading.heartRate);
        data.put("heartRateUnit", "bpm");
        data.put("heartRateTime", time);

        data.put("systolic", reading.systolic);
        data.put("diastolic", reading.diastolic);
        data.put("bloodPressureUnit", "mmHg");
        data.put("bloodPressureTime", time);

        data.put("bloodOxygen", reading.spo2);
        data.put("bloodOxygenUnit", "%");
        data.put("bloodOxygenTime", time);

        data.put("bodyTemperature", reading.temperature);
        data.put("bodyTemperatureUnit", "℃");
        data.put("bodyTemperatureTime", time);
        return data;
    }

    /**
     * App 首页实时健康接口（/api/elder/{elderId}/health/realtime）使用的模拟数据。
     * 返回字段与原 HealthLatestDto 一致：temperature / heartRate / systolic / diastolic / bloodOxygen / updateTime。
     */
    public HealthLatestDto getRealtimeHealth(String elderId) {
        Reading reading = getOrRefresh(elderId);
        HealthLatestDto dto = new HealthLatestDto();
        dto.setElderId(elderId);
        dto.setTemperature(reading.temperature);
        dto.setHeartRate(reading.heartRate);
        dto.setSystolic(reading.systolic);
        dto.setDiastolic(reading.diastolic);
        dto.setBloodOxygen(reading.spo2);
        dto.setUpdateTime(formatTime(reading));
        return dto;
    }

    /** 取指定老人的模拟体征（满 1 秒则波动一次），并格式化为显示时间 */
    private Reading getOrRefresh(String elderId) {
        long now = System.currentTimeMillis();
        Reading reading = readings.computeIfAbsent(elderId, k -> new Reading());
        reading.refreshIfDue(now);
        return reading;
    }

    private static String formatTime(Reading reading) {
        return Instant.ofEpochMilli(reading.generatedAtMillis)
                .atZone(ZoneId.systemDefault()).toLocalDateTime().format(TIME_FMT);
    }

    /** 单个老人的一组模拟体征及生成时间 */
    private static class Reading {
        int heartRate;
        int systolic;
        int diastolic;
        int spo2;
        double temperature;
        volatile long generatedAtMillis;

        Reading() {
            seed();
        }

        /** 距上次生成满 1 秒则随机游走一次，保证数值必有变化，并刷新时间戳 */
        synchronized void refreshIfDue(long nowMillis) {
            if (nowMillis - generatedAtMillis < REFRESH_MILLIS) {
                return;
            }
            heartRate = walkInt(heartRate, HR_MIN, HR_MAX, 2);
            systolic = walkInt(systolic, SYS_MIN, SYS_MAX, 2);
            diastolic = walkInt(diastolic, DIA_MIN, DIA_MAX, 2);
            spo2 = walkInt(spo2, SPO2_MIN, SPO2_MAX, 2);
            temperature = walkTemp(temperature);
            generatedAtMillis = nowMillis;
        }

        /** 整数随机游走：步长 ±stepRange，边界饱和时向区间中心强制挪动，保证每次结果不同 */
        private static int walkInt(int current, int min, int max, int stepRange) {
            ThreadLocalRandom r = ThreadLocalRandom.current();
            int delta = r.nextBoolean() ? r.nextInt(1, stepRange + 1) : -r.nextInt(1, stepRange + 1);
            int candidate = clamp(current + delta, min, max);
            if (candidate == current) {
                candidate = clamp(current + (current < (min + max) / 2.0 ? 1 : -1), min, max);
            }
            return candidate;
        }

        /** 体温随机游走：步长 ±0.1，边界饱和时向内强制挪动，保证每次结果不同 */
        private static double walkTemp(double current) {
            ThreadLocalRandom r = ThreadLocalRandom.current();
            double delta = r.nextBoolean() ? 0.1 : -0.1;
            double candidate = clamp(Math.round((current + delta) * 10) / 10.0, TEMP_MIN, TEMP_MAX);
            if (candidate == current) {
                double center = (TEMP_MIN + TEMP_MAX) / 2.0;
                candidate = clamp(Math.round((current + (current < center ? 0.2 : -0.2)) * 10) / 10.0, TEMP_MIN, TEMP_MAX);
            }
            return candidate;
        }

        /** 首次生成：区间内随机取一组初始值 */
        private void seed() {
            ThreadLocalRandom r = ThreadLocalRandom.current();
            heartRate = (int) Math.round(r.nextDouble(HR_MIN, HR_MAX));
            systolic = r.nextInt(SYS_MIN, SYS_MAX + 1);
            diastolic = r.nextInt(DIA_MIN, DIA_MAX + 1);
            spo2 = r.nextInt(SPO2_MIN, SPO2_MAX + 1);
            temperature = Math.round(r.nextDouble(TEMP_MIN, TEMP_MAX) * 10) / 10.0;
            generatedAtMillis = System.currentTimeMillis();
        }

        private static int clamp(int v, int min, int max) {
            return Math.max(min, Math.min(max, v));
        }

        private static double clamp(double v, double min, double max) {
            return Math.max(min, Math.min(max, v));
        }
    }
}
