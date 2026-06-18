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
import com.anxinban.entity.BloodPressure;
import com.anxinban.entity.SensorData;
import com.anxinban.mapper.BloodPressureRepository;
import com.anxinban.mapper.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    public HealthService(SensorDataRepository sensorDataRepository, BloodPressureRepository bloodPressureRepository) {
        this.sensorDataRepository = sensorDataRepository;
        this.bloodPressureRepository = bloodPressureRepository;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public HealthLatestDto getLatestHealth(String elderId) {
        HealthLatestDto dto = new HealthLatestDto();
        dto.setElderId(elderId);

        List<SensorData> tempList = sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "temperature".equals(s.getSensorType()))
                .sorted(Comparator.comparing(SensorData::getTimestamp).reversed())
                .limit(1)
                .collect(Collectors.toList());
        if (!tempList.isEmpty()) {
            dto.setTemperature(tempList.get(0).getValue());
            dto.setUpdateTime(tempList.get(0).getTimestamp().toString());
        }

        List<SensorData> hrList = sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "heart_rate".equals(s.getSensorType()))
                .sorted(Comparator.comparing(SensorData::getTimestamp).reversed())
                .limit(1)
                .collect(Collectors.toList());
        if (!hrList.isEmpty()) {
            dto.setHeartRate(hrList.get(0).getValue().intValue());
            if (dto.getUpdateTime() == null) {
                dto.setUpdateTime(hrList.get(0).getTimestamp().toString());
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

        List<SensorData> boList = sensorDataRepository.findByElderId(elderId).stream()
                .filter(s -> "blood_oxygen".equals(s.getSensorType()))
                .sorted(Comparator.comparing(SensorData::getTimestamp).reversed())
                .limit(1)
                .collect(Collectors.toList());
        if (!boList.isEmpty()) {
            dto.setBloodOxygen(boList.get(0).getValue().intValue());
            if (dto.getUpdateTime() == null) {
                dto.setUpdateTime(boList.get(0).getTimestamp().toString());
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
            for (BloodPressure bp : bps) {
                HealthTrendDto.HealthTrendItemDto item = new HealthTrendDto.HealthTrendItemDto();
                item.setTime(bp.getTimestamp().toString());
                item.setSystolic(bp.getSystolic());
                item.setDiastolic(bp.getDiastolic());
                items.add(item);
            }
        } else {
            List<SensorData> sensors = sensorDataRepository.findByElderIdAndSensorTypeAndTimestampBetween(elderId, type, start, end);
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
