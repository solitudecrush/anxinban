package com.anxinban.service;

import com.anxinban.entity.BloodOxygen;
import com.anxinban.entity.BloodPressure;
import com.anxinban.entity.BodyTemperature;
import com.anxinban.entity.HeartRate;
import com.anxinban.mapper.BloodOxygenRepository;
import com.anxinban.mapper.BloodPressureRepository;
import com.anxinban.mapper.BodyTemperatureRepository;
import com.anxinban.mapper.HeartRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 生命体征数据服务 — 统一管理心率、血压、血氧、体温的独立数据表查询。
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class VitalSignsService {

    private static final Logger log = LoggerFactory.getLogger(VitalSignsService.class);

    private final HeartRateRepository heartRateRepository;
    private final BloodPressureRepository bloodPressureRepository;
    private final BloodOxygenRepository bloodOxygenRepository;
    private final BodyTemperatureRepository bodyTemperatureRepository;

    @Autowired
    public VitalSignsService(HeartRateRepository heartRateRepository,
                             BloodPressureRepository bloodPressureRepository,
                             BloodOxygenRepository bloodOxygenRepository,
                             BodyTemperatureRepository bodyTemperatureRepository) {
        this.heartRateRepository = heartRateRepository;
        this.bloodPressureRepository = bloodPressureRepository;
        this.bloodOxygenRepository = bloodOxygenRepository;
        this.bodyTemperatureRepository = bodyTemperatureRepository;
    }

    // ==================== 心率 ====================

    public List<HeartRate> listHeartRate(String elderId) {
        return heartRateRepository.findByElderIdOrderByTimestampDesc(elderId);
    }

    public List<HeartRate> listHeartRateByDateRange(String elderId, LocalDateTime start, LocalDateTime end) {
        List<HeartRate> list = heartRateRepository.findByElderIdAndTimestampBetween(elderId, start, end);
        list.sort(Comparator.comparing(HeartRate::getTimestamp));
        return list;
    }

    public HeartRate getLatestHeartRate(String elderId) {
        return heartRateRepository.findFirstByElderIdOrderByTimestampDesc(elderId);
    }

    // ==================== 血压 ====================

    public List<BloodPressure> listBloodPressure(String elderId) {
        return bloodPressureRepository.findByElderIdOrderByTimestampDesc(elderId);
    }

    public List<BloodPressure> listBloodPressureByDateRange(String elderId, LocalDateTime start, LocalDateTime end) {
        List<BloodPressure> list = bloodPressureRepository.findByElderIdAndTimestampBetween(elderId, start, end);
        list.sort(Comparator.comparing(BloodPressure::getTimestamp));
        return list;
    }

    public BloodPressure getLatestBloodPressure(String elderId) {
        return bloodPressureRepository.findFirstByElderIdOrderByTimestampDesc(elderId);
    }

    // ==================== 血氧 ====================

    public List<BloodOxygen> listBloodOxygen(String elderId) {
        return bloodOxygenRepository.findByElderIdOrderByTimestampDesc(elderId);
    }

    public List<BloodOxygen> listBloodOxygenByDateRange(String elderId, LocalDateTime start, LocalDateTime end) {
        List<BloodOxygen> list = bloodOxygenRepository.findByElderIdAndTimestampBetween(elderId, start, end);
        list.sort(Comparator.comparing(BloodOxygen::getTimestamp));
        return list;
    }

    public BloodOxygen getLatestBloodOxygen(String elderId) {
        return bloodOxygenRepository.findFirstByElderIdOrderByTimestampDesc(elderId);
    }

    // ==================== 体温 ====================

    public List<BodyTemperature> listBodyTemperature(String elderId) {
        return bodyTemperatureRepository.findByElderIdOrderByTimestampDesc(elderId);
    }

    public List<BodyTemperature> listBodyTemperatureByDateRange(String elderId, LocalDateTime start, LocalDateTime end) {
        List<BodyTemperature> list = bodyTemperatureRepository.findByElderIdAndTimestampBetween(elderId, start, end);
        list.sort(Comparator.comparing(BodyTemperature::getTimestamp));
        return list;
    }

    public BodyTemperature getLatestBodyTemperature(String elderId) {
        return bodyTemperatureRepository.findFirstByElderIdOrderByTimestampDesc(elderId);
    }
}
