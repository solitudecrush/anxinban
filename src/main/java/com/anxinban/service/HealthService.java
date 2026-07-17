package com.anxinban.service;

/**
 * Health 业务服务类，处理 Health 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.HealthAnalysisDto;
import com.anxinban.dto.HealthLatestDto;
import com.anxinban.dto.HealthTrendDto;
import com.anxinban.entity.BloodOxygen;
import com.anxinban.entity.BloodPressure;
import com.anxinban.entity.BodyTemperature;
import com.anxinban.entity.HeartRate;
import com.anxinban.entity.SensorData;
import com.anxinban.mapper.BloodOxygenRepository;
import com.anxinban.mapper.BloodPressureRepository;
import com.anxinban.mapper.BodyTemperatureRepository;
import com.anxinban.mapper.HeartRateRepository;
import com.anxinban.mapper.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthService {
    private final SensorDataRepository sensorDataRepository;
    /** 血压 */
    private final BloodPressureRepository bloodPressureRepository;
    /** 心率 */
    private final HeartRateRepository heartRateRepository;
    /** 血氧 */
    private final BloodOxygenRepository bloodOxygenRepository;
    /** 体温 */
    private final BodyTemperatureRepository bodyTemperatureRepository;

    @Autowired
    public HealthService(SensorDataRepository sensorDataRepository,
                         BloodPressureRepository bloodPressureRepository,
                         HeartRateRepository heartRateRepository,
                         BloodOxygenRepository bloodOxygenRepository,
                         BodyTemperatureRepository bodyTemperatureRepository) {
        this.sensorDataRepository = sensorDataRepository;
        this.bloodPressureRepository = bloodPressureRepository;
        this.heartRateRepository = heartRateRepository;
        this.bloodOxygenRepository = bloodOxygenRepository;
        this.bodyTemperatureRepository = bodyTemperatureRepository;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public HealthLatestDto getLatestHealth(String elderId) {
        HealthLatestDto dto = new HealthLatestDto();
        dto.setElderId(elderId);

        // 体温：优先从独立表 body_temperature 读取，fallback 到 sensor_data
        BodyTemperature latestBt = bodyTemperatureRepository.findFirstByElderIdOrderByTimestampDesc(elderId);
        if (latestBt != null) {
            dto.setTemperature(latestBt.getValue() != null ? latestBt.getValue().doubleValue() : null);
            dto.setUpdateTime(latestBt.getTimestamp().toString());
        } else {
            // 尝试多种 sensor_type：DeviceUpload 用 "temperature"，MQTT 用 "body-temperature"
            List<SensorData> tempList = sensorDataRepository.findByElderId(elderId).stream()
                    .filter(s -> "temperature".equals(s.getSensorType())
                            || "body-temperature".equals(s.getSensorType()))
                    .sorted(Comparator.comparing(SensorData::getTimestamp).reversed())
                    .limit(1)
                    .collect(Collectors.toList());
            if (!tempList.isEmpty()) {
                dto.setTemperature(tempList.get(0).getValue());
                dto.setUpdateTime(tempList.get(0).getTimestamp().toString());
            }
        }

        // 心率：优先从独立表 heart_rate 读取，fallback 到 sensor_data
        HeartRate latestHr = heartRateRepository.findFirstByElderIdOrderByTimestampDesc(elderId);
        if (latestHr != null) {
            dto.setHeartRate(latestHr.getValue());
            if (dto.getUpdateTime() == null) {
                dto.setUpdateTime(latestHr.getTimestamp().toString());
            }
        } else {
            // 尝试多种 sensor_type：DeviceUpload 用 "heart_rate"，MQTT 用 "heart-rate"
            List<SensorData> hrList = sensorDataRepository.findByElderId(elderId).stream()
                    .filter(s -> "heart_rate".equals(s.getSensorType())
                            || "heart-rate".equals(s.getSensorType()))
                    .sorted(Comparator.comparing(SensorData::getTimestamp).reversed())
                    .limit(1)
                    .collect(Collectors.toList());
            if (!hrList.isEmpty()) {
                dto.setHeartRate(hrList.get(0).getValue().intValue());
                if (dto.getUpdateTime() == null) {
                    dto.setUpdateTime(hrList.get(0).getTimestamp().toString());
                }
            }
        }

        BloodPressure latestBp = bloodPressureRepository.findFirstByElderIdOrderByTimestampDesc(elderId);
        if (latestBp != null) {
            dto.setSystolic(latestBp.getSystolic());
            dto.setDiastolic(latestBp.getDiastolic());
            if (dto.getUpdateTime() == null) {
                dto.setUpdateTime(latestBp.getTimestamp().toString());
            }
        }

        // 血氧：优先从独立表 blood_oxygen 读取，fallback 到 sensor_data
        BloodOxygen latestBo = bloodOxygenRepository.findFirstByElderIdOrderByTimestampDesc(elderId);
        if (latestBo != null) {
            dto.setBloodOxygen(latestBo.getValue() != null ? latestBo.getValue().intValue() : null);
            if (dto.getUpdateTime() == null) {
                dto.setUpdateTime(latestBo.getTimestamp().toString());
            }
        } else {
            // 尝试多种 sensor_type：DeviceUpload 用 "spo2"，MQTT 用 "blood-oxygen"
            List<SensorData> boList = sensorDataRepository.findByElderId(elderId).stream()
                    .filter(s -> "spo2".equals(s.getSensorType())
                            || "blood_oxygen".equals(s.getSensorType())
                            || "blood-oxygen".equals(s.getSensorType()))
                    .sorted(Comparator.comparing(SensorData::getTimestamp).reversed())
                    .limit(1)
                    .collect(Collectors.toList());
            if (!boList.isEmpty()) {
                dto.setBloodOxygen(boList.get(0).getValue().intValue());
                if (dto.getUpdateTime() == null) {
                    dto.setUpdateTime(boList.get(0).getTimestamp().toString());
                }
            }
        }

        List<SensorData> sleepList = sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "insomnia".equals(s.getSensorType()))
                .sorted(Comparator.comparing(SensorData::getTimestamp).reversed())
                .limit(1)
                .collect(Collectors.toList());
        if (!sleepList.isEmpty()) {
            dto.setInsomnia(mapInsomniaLevel(sleepList.get(0).getValue()));
            if (dto.getUpdateTime() == null) {
                dto.setUpdateTime(sleepList.get(0).getTimestamp().toString());
            }
        }

        List<SensorData> sleepTimeList = sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "sleep_time".equals(s.getSensorType()))
                .sorted(Comparator.comparing(SensorData::getTimestamp).reversed())
                .limit(1)
                .collect(Collectors.toList());
        if (!sleepTimeList.isEmpty()) {
            dto.setSleepTime(mapSleepTime(sleepTimeList.get(0).getValue()));
        }

        return dto;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public HealthTrendDto getHealthTrend(String elderId, String type, String period) {
        HealthTrendDto dto = new HealthTrendDto();
        dto.setElderId(elderId);
        dto.setType(type);
        dto.setPeriod(period);

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start;
        switch (period) {
            case "day":
                start = end.minus(1, ChronoUnit.DAYS);
                break;
            case "month":
                start = end.minus(30, ChronoUnit.DAYS);
                break;
            case "week":
            default:
                start = end.minus(7, ChronoUnit.DAYS);
                break;
        }

        List<HealthTrendDto.HealthTrendItemDto> items = new ArrayList<>();

        if ("blood_pressure".equals(type)) {
            List<BloodPressure> bps = bloodPressureRepository.findByElderIdAndTimestampBetween(elderId, start, end);
            bps.sort(Comparator.comparing(BloodPressure::getTimestamp));
            for (BloodPressure bp : bps) {
                HealthTrendDto.HealthTrendItemDto item = new HealthTrendDto.HealthTrendItemDto();
                item.setTime(bp.getTimestamp().toString());
                item.setSystolic(bp.getSystolic());
                item.setDiastolic(bp.getDiastolic());
                items.add(item);
            }
        } else if ("heart_rate".equals(type)) {
            List<HeartRate> hrs = heartRateRepository.findByElderIdAndTimestampBetween(elderId, start, end);
            hrs.sort(Comparator.comparing(HeartRate::getTimestamp));
            for (HeartRate hr : hrs) {
                HealthTrendDto.HealthTrendItemDto item = new HealthTrendDto.HealthTrendItemDto();
                item.setTime(hr.getTimestamp().toString());
                item.setValue(hr.getValue() != null ? hr.getValue().doubleValue() : null);
                items.add(item);
            }
            // Fallback to sensor_data: MQTT uses "heart-rate", DeviceUpload uses "heart_rate"
            if (items.isEmpty()) {
                items.addAll(querySensorData(elderId,
                        java.util.List.of("heart_rate", "heart-rate"), start, end));
            }
        } else if ("blood_oxygen".equals(type)) {
            List<BloodOxygen> bos = bloodOxygenRepository.findByElderIdAndTimestampBetween(elderId, start, end);
            bos.sort(Comparator.comparing(BloodOxygen::getTimestamp));
            for (BloodOxygen bo : bos) {
                HealthTrendDto.HealthTrendItemDto item = new HealthTrendDto.HealthTrendItemDto();
                item.setTime(bo.getTimestamp().toString());
                item.setValue(bo.getValue() != null ? bo.getValue().doubleValue() : null);
                items.add(item);
            }
            // Fallback to sensor_data: DeviceUpload uses "spo2", MQTT uses "blood-oxygen"
            if (items.isEmpty()) {
                items.addAll(querySensorData(elderId,
                        java.util.List.of("spo2", "blood_oxygen", "blood-oxygen"), start, end));
            }
        } else if ("temperature".equals(type)) {
            List<BodyTemperature> bts = bodyTemperatureRepository.findByElderIdAndTimestampBetween(elderId, start, end);
            bts.sort(Comparator.comparing(BodyTemperature::getTimestamp));
            for (BodyTemperature bt : bts) {
                HealthTrendDto.HealthTrendItemDto item = new HealthTrendDto.HealthTrendItemDto();
                item.setTime(bt.getTimestamp().toString());
                item.setValue(bt.getValue() != null ? bt.getValue().doubleValue() : null);
                items.add(item);
            }
            // Fallback to sensor_data: DeviceUpload uses "temperature", MQTT uses "body-temperature"
            if (items.isEmpty()) {
                items.addAll(querySensorData(elderId,
                        java.util.List.of("temperature", "body-temperature"), start, end));
            }
        } else {
            List<SensorData> sensors = sensorDataRepository.findByElderIdAndSensorTypeAndTimestampBetween(elderId, type, start, end);
            sensors.sort(Comparator.comparing(SensorData::getTimestamp));
            for (SensorData s : sensors) {
                HealthTrendDto.HealthTrendItemDto item = new HealthTrendDto.HealthTrendItemDto();
                item.setTime(s.getTimestamp().toString());
                item.setValue(s.getValue());
                items.add(item);
            }
        }

        dto.setData(items);
        return dto;
    }

    /**
     * 从 sensor_data 表查询健康数据（支持多 sensor_type 查询，解决 MQTT 和 DeviceUpload
     * 使用不同 sensor_type 命名的问题）。
     *
     * @param elderId     老人 ID
     * @param sensorTypes 要查询的 sensor_type 列表
     * @param start       时间范围起始
     * @param end         时间范围结束
     * @return 转换后的 HealthTrendItemDto 列表（按时间升序排列）
     */
    private List<HealthTrendDto.HealthTrendItemDto> querySensorData(
            String elderId, java.util.List<String> sensorTypes,
            LocalDateTime start, LocalDateTime end) {
        List<HealthTrendDto.HealthTrendItemDto> items = new ArrayList<>();
        for (String st : sensorTypes) {
            List<SensorData> sensors = sensorDataRepository
                    .findByElderIdAndSensorTypeAndTimestampBetween(elderId, st, start, end);
            for (SensorData s : sensors) {
                HealthTrendDto.HealthTrendItemDto item = new HealthTrendDto.HealthTrendItemDto();
                item.setTime(s.getTimestamp().toString());
                item.setValue(s.getValue());
                items.add(item);
            }
        }
        items.sort(Comparator.comparing(item -> {
            try {
                String t = item.getTime();
                if (t == null) return LocalDateTime.now();
                return LocalDateTime.parse(t.substring(0, Math.min(t.length(), 19)).replace(' ', 'T'));
            } catch (Exception e) {
                return LocalDateTime.now();
            }
        }));
        return items;
    }

        /**
         * mapInsomniaLevel 方法。
         *
         * @param value 数值
         */
    private String mapInsomniaLevel(Double value) {
        if (value == null) return null;
        int level = value.intValue();
        switch (level) {
            case 0: return "无";
            case 1: return "轻度";
            case 2: return "中度";
            case 3: return "重度";
            default: return String.valueOf(level);
        }
    }

        /**
         * mapSleepTime 方法。
         *
         * @param value 数值
         */
    private String mapSleepTime(Double value) {
        if (value == null) return null;
        int hour = value.intValue();
        int minute = (int) ((value - hour) * 60);
        return String.format("%02d:%02d", hour, minute);
    }

        /**
         * 获取分析结果。
         *
         * @return 分析结果
         */
    public HealthAnalysisDto getHealthAnalysis(String elderId, String period) {
        HealthAnalysisDto dto = new HealthAnalysisDto();
        dto.setElderId(elderId);
        dto.setPeriod(period);

        HealthTrendDto hrTrend = getHealthTrend(elderId, "heart_rate", period);
        HealthTrendDto bpTrend = getHealthTrend(elderId, "blood_pressure", period);
        HealthTrendDto tempTrend = getHealthTrend(elderId, "temperature", period);

        double avgHr = hrTrend.getData().stream().mapToDouble(i -> i.getValue() != null ? i.getValue() : 0).average().orElse(0);
        double avgSys = bpTrend.getData().stream().mapToDouble(i -> i.getSystolic() != null ? i.getSystolic() : 0).average().orElse(0);
        double avgDia = bpTrend.getData().stream().mapToDouble(i -> i.getDiastolic() != null ? i.getDiastolic() : 0).average().orElse(0);
        double avgTemp = tempTrend.getData().stream().mapToDouble(i -> i.getValue() != null ? i.getValue() : 0).average().orElse(0);

        String periodName = "week".equals(period) ? "本周" : ("month".equals(period) ? "本月" : "今日");
        String summary = String.format("%s老人健康状况良好，平均心率 %.0fbpm，平均血压 %.0f/%.0f mmHg，体温稳定在 %.1f-%.1f℃ 之间。",
                periodName, avgHr, avgSys, avgDia, avgTemp - 0.2, avgTemp + 0.2);
        String suggestion = "建议继续监测，保持适度活动。如血压持续升高，建议咨询医生。";

        dto.setSummary(summary);
        dto.setSuggestion(suggestion);
        return dto;
    }
}
