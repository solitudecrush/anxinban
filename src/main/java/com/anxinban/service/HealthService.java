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
import java.time.LocalDate;
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
                // 查询昨天完整一天的数据（00:00:00 ~ 23:59:59）
                LocalDate yesterday = LocalDate.now().minusDays(1);
                start = yesterday.atStartOfDay();
                end = yesterday.atTime(23, 59, 59);
                break;
            case "month":
                start = end.minus(30, ChronoUnit.DAYS);
                break;
            case "week":
            default:
                // 最近7天（含今天），前端图表使用7个 X 轴标签
                start = end.minus(6, ChronoUnit.DAYS);
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

        // 填补空缺时间点：确保所有预期时间槽位都有数据（插值填充）
        List<HealthTrendDto.HealthTrendItemDto> filledItems = fillGaps(items, type, period, start, end);
        dto.setData(filledItems);
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
     * 填补空缺时间点：确保所有预期时间槽位都有数据。
     *
     * <p>对于没有实际数据的时间槽位，使用相邻数据点进行线性插值。
     * 如果前后都没有数据，则使用最近的已知数据点填充。
     * 保证前端折线图在所有 X 轴位置都有对应的 Y 值，不会出现断线。</p>
     *
     * @param rawItems 原始数据列表（已按时间排序）
     * @param type     数据类型（blood_pressure / heart_rate / blood_oxygen / temperature）
     * @param period   时间周期（day / week / month）
     * @param start    时间范围起始
     * @param end      时间范围结束
     * @return 填补后的完整数据列表
     */
    private List<HealthTrendDto.HealthTrendItemDto> fillGaps(
            List<HealthTrendDto.HealthTrendItemDto> rawItems,
            String type, String period,
            LocalDateTime start, LocalDateTime end) {

        // 生成所有预期的时间槽位
        List<LocalDateTime> slots = generateTimeSlots(period, start, end);

        // 将原始数据按时间槽位分组（同一槽位内的数据取平均）
        java.util.Map<Integer, List<HealthTrendDto.HealthTrendItemDto>> slotDataMap = new java.util.LinkedHashMap<>();
        for (HealthTrendDto.HealthTrendItemDto item : rawItems) {
            LocalDateTime t = parseTimestamp(item.getTime());
            if (t == null) continue;
            int slotIndex = findSlotIndex(t, slots, period);
            if (slotIndex >= 0) {
                slotDataMap.computeIfAbsent(slotIndex, k -> new ArrayList<>()).add(item);
            }
        }

        // 为每个槽位生成聚合数据（同一槽位多条数据取平均）
        List<HealthTrendDto.HealthTrendItemDto> slotAggregated = new ArrayList<>();
        for (int i = 0; i < slots.size(); i++) {
            List<HealthTrendDto.HealthTrendItemDto> slotItems = slotDataMap.get(i);
            if (slotItems != null && !slotItems.isEmpty()) {
                // 有实际数据：聚合（平均）
                HealthTrendDto.HealthTrendItemDto agg = aggregateSlotItems(slotItems, type);
                agg.setTime(formatSlotTime(slots.get(i), period));
                slotAggregated.add(agg);
            } else {
                // 无数据：标记为 null，后续插值填充
                slotAggregated.add(null);
            }
        }

        // 对空槽位进行线性插值
        List<HealthTrendDto.HealthTrendItemDto> result = new ArrayList<>();
        for (int i = 0; i < slots.size(); i++) {
            if (slotAggregated.get(i) != null) {
                result.add(slotAggregated.get(i));
            } else {
                // 查找前后最近的有数据点
                HealthTrendDto.HealthTrendItemDto before = findNearestBefore(slotAggregated, i);
                HealthTrendDto.HealthTrendItemDto after = findNearestAfter(slotAggregated, i);

                HealthTrendDto.HealthTrendItemDto filled = new HealthTrendDto.HealthTrendItemDto();
                filled.setTime(formatSlotTime(slots.get(i), period));

                if (before != null && after != null) {
                    // 线性插值
                    interpolateItem(filled, before, after, type);
                } else if (before != null) {
                    // 只有前面的数据：使用前面的值
                    copyItemValues(filled, before, type);
                } else if (after != null) {
                    // 只有后面的数据：使用后面的值
                    copyItemValues(filled, after, type);
                }
                // 如果前后都没有数据，保持 null 值（前端显示为 0 或空）
                result.add(filled);
            }
        }

        return result;
    }

    /**
     * 复制数据项的值字段。
     */
    private void copyItemValues(HealthTrendDto.HealthTrendItemDto target,
                                 HealthTrendDto.HealthTrendItemDto source, String type) {
        if ("blood_pressure".equals(type)) {
            target.setSystolic(source.getSystolic());
            target.setDiastolic(source.getDiastolic());
        } else {
            target.setValue(source.getValue());
        }
    }

    /**
     * 在两个数据点之间线性插值。
     */
    private void interpolateItem(HealthTrendDto.HealthTrendItemDto target,
                                  HealthTrendDto.HealthTrendItemDto before,
                                  HealthTrendDto.HealthTrendItemDto after, String type) {
        // 使用简单的均值插值（前后等权重）
        if ("blood_pressure".equals(type)) {
            Integer sysBefore = before.getSystolic();
            Integer sysAfter = after.getSystolic();
            Integer diaBefore = before.getDiastolic();
            Integer diaAfter = after.getDiastolic();
            if (sysBefore != null && sysAfter != null) {
                target.setSystolic((sysBefore + sysAfter) / 2);
            } else if (sysBefore != null) {
                target.setSystolic(sysBefore);
            } else if (sysAfter != null) {
                target.setSystolic(sysAfter);
            }
            if (diaBefore != null && diaAfter != null) {
                target.setDiastolic((diaBefore + diaAfter) / 2);
            } else if (diaBefore != null) {
                target.setDiastolic(diaBefore);
            } else if (diaAfter != null) {
                target.setDiastolic(diaAfter);
            }
        } else {
            Double vBefore = before.getValue();
            Double vAfter = after.getValue();
            if (vBefore != null && vAfter != null) {
                target.setValue((vBefore + vAfter) / 2.0);
            } else if (vBefore != null) {
                target.setValue(vBefore);
            } else if (vAfter != null) {
                target.setValue(vAfter);
            }
        }
    }

    /**
     * 聚合同一时间槽位内的多条数据（取平均值）。
     */
    private HealthTrendDto.HealthTrendItemDto aggregateSlotItems(
            List<HealthTrendDto.HealthTrendItemDto> items, String type) {
        HealthTrendDto.HealthTrendItemDto result = new HealthTrendDto.HealthTrendItemDto();
        if ("blood_pressure".equals(type)) {
            int sysSum = 0, diaSum = 0, sysCount = 0, diaCount = 0;
            for (HealthTrendDto.HealthTrendItemDto item : items) {
                if (item.getSystolic() != null) { sysSum += item.getSystolic(); sysCount++; }
                if (item.getDiastolic() != null) { diaSum += item.getDiastolic(); diaCount++; }
            }
            if (sysCount > 0) result.setSystolic(sysSum / sysCount);
            if (diaCount > 0) result.setDiastolic(diaSum / diaCount);
        } else {
            double sum = 0;
            int count = 0;
            for (HealthTrendDto.HealthTrendItemDto item : items) {
                if (item.getValue() != null) { sum += item.getValue(); count++; }
            }
            if (count > 0) result.setValue(sum / count);
        }
        return result;
    }

    /**
     * 查找索引 i 之前最近的非 null 数据项。
     */
    private HealthTrendDto.HealthTrendItemDto findNearestBefore(
            List<HealthTrendDto.HealthTrendItemDto> items, int i) {
        for (int j = i - 1; j >= 0; j--) {
            if (items.get(j) != null) return items.get(j);
        }
        return null;
    }

    /**
     * 查找索引 i 之后最近的非 null 数据项。
     */
    private HealthTrendDto.HealthTrendItemDto findNearestAfter(
            List<HealthTrendDto.HealthTrendItemDto> items, int i) {
        for (int j = i + 1; j < items.size(); j++) {
            if (items.get(j) != null) return items.get(j);
        }
        return null;
    }

    /**
     * 生成指定周期的所有时间槽位。
     * day: 每小时一个槽位 (0:00 ~ 23:00)
     * week: 每天一个槽位 (最近7天)
     * month: 每天一个槽位 (最近30天)
     */
    private List<LocalDateTime> generateTimeSlots(String period, LocalDateTime start, LocalDateTime end) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime current;

        switch (period) {
            case "day":
                // 每小时一个槽位：从 start 的整点开始
                current = start.withMinute(0).withSecond(0).withNano(0);
                while (!current.isAfter(end)) {
                    slots.add(current);
                    current = current.plusHours(1);
                }
                break;
            case "month":
                // 每天一个槽位
                current = start.withHour(0).withMinute(0).withSecond(0).withNano(0);
                while (!current.isAfter(end)) {
                    slots.add(current);
                    current = current.plusDays(1);
                }
                break;
            case "week":
            default:
                // 每天一个槽位
                current = start.withHour(0).withMinute(0).withSecond(0).withNano(0);
                while (!current.isAfter(end)) {
                    slots.add(current);
                    current = current.plusDays(1);
                }
                break;
        }
        return slots;
    }

    /**
     * 找到时间戳对应的槽位索引。
     */
    private int findSlotIndex(LocalDateTime timestamp, List<LocalDateTime> slots, String period) {
        for (int i = 0; i < slots.size(); i++) {
            LocalDateTime slot = slots.get(i);
            switch (period) {
                case "day":
                    // 同一天同一小时即为同一个槽位
                    if (timestamp.toLocalDate().equals(slot.toLocalDate())
                            && timestamp.getHour() == slot.getHour()) {
                        return i;
                    }
                    break;
                case "week":
                case "month":
                default:
                    // 同一天即为同一个槽位
                    if (timestamp.toLocalDate().equals(slot.toLocalDate())) {
                        return i;
                    }
                    break;
            }
        }
        return -1;
    }

    /**
     * 格式化槽位时间为前端可解析的字符串。
     */
    private String formatSlotTime(LocalDateTime slot, String period) {
        switch (period) {
            case "day":
                return String.format("%02d:%02d", slot.getHour(), slot.getMinute());
            case "week":
                return String.format("%04d-%02d-%02d", slot.getYear(), slot.getMonthValue(), slot.getDayOfMonth());
            case "month":
                return slot.getDayOfMonth() + "日";
            default:
                return slot.toString();
        }
    }

    /**
     * 解析时间字符串为 LocalDateTime，支持多种格式。
     */
    private LocalDateTime parseTimestamp(String timeStr) {
        if (timeStr == null) return null;
        try {
            String normalized = timeStr.trim().replace(' ', 'T');
            if (normalized.length() > 19) {
                normalized = normalized.substring(0, 19);
            }
            return LocalDateTime.parse(normalized);
        } catch (Exception e) {
            // 尝试其他格式
            try {
                return LocalDateTime.parse(timeStr.trim().substring(0, Math.min(timeStr.trim().length(), 19)).replace(' ', 'T'));
            } catch (Exception e2) {
                return null;
            }
        }
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
