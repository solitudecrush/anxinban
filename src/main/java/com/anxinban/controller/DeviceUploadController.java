package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.DeviceUploadRequest;
import com.anxinban.entity.AlarmEvent;
import com.anxinban.entity.Device;
import com.anxinban.entity.ElderUser;
import com.anxinban.entity.SensorData;
import com.anxinban.mapper.AlarmEventRepository;
import com.anxinban.mapper.DeviceRepository;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.mapper.SensorDataRepository;
import com.anxinban.entity.AiAnalysisRecord;
import com.anxinban.service.AiAnalysisRecordService;
import com.anxinban.service.AiForwardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 比赛兼容层 - 设备上传接口。
 *
 * <p>提供 POST /api/device/upload，兼容队长 AI Mock Cloud 的请求格式。
 * 根据 elder_id 查找设备，写入 sensor_data，并通过规则引擎判断是否生成告警。</p>
 *
 * <p>规则阈值（不依赖 AI 服务）：</p>
 * <ul>
 *   <li>spo2 &lt; 92 → 触发告警</li>
 *   <li>heart_rate &gt; 110 或 heart_rate &lt; 50 → 触发告警</li>
 *   <li>temperature &gt; 38.0 或 temperature &lt; 35.0 → 触发告警</li>
 *   <li>fall_status 包含"疑似跌倒" → 触发告警</li>
 *   <li>activity_status 为"长时间静止" 且 heart_rate 异常 → 触发告警</li>
 * </ul>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/device")
public class DeviceUploadController {

    private static final Logger log = LoggerFactory.getLogger(DeviceUploadController.class);

    @Autowired
    private ElderUserRepository elderUserRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Autowired
    private AlarmEventRepository alarmEventRepository;

    @Autowired
    private AiForwardService aiForwardService;

    @Autowired
    private AiAnalysisRecordService aiAnalysisRecordService;

    /**
     * 设备健康数据上传兼容接口。
     *
     * @param request 上传请求体
     * @return 保存结果及告警判断
     */
    @PostMapping("/upload")
    public ApiResponse<Map<String, Object>> uploadHealthData(@RequestBody DeviceUploadRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 1. 校验 elder_id
        if (request.getElderId() == null || request.getElderId().isEmpty()) {
            return ApiResponse.error(400, "elder_id 不能为空");
        }

        ElderUser elder = elderUserRepository.findByElderId(request.getElderId());
        if (elder == null) {
            return ApiResponse.error(404, "老人不存在: " + request.getElderId());
        }

        // 2. 查找该老人绑定的设备（优先手环/健康类设备）
        List<Device> devices = deviceRepository.findByElderId(request.getElderId());
        String deviceId;
        if (devices != null && !devices.isEmpty()) {
            Device healthDevice = devices.stream()
                    .filter(d -> d.getType() != null &&
                            (d.getType().contains("手环") || d.getType().contains("手錶")))
                    .findFirst()
                    .orElse(devices.get(0));
            deviceId = healthDevice.getDeviceId();
        } else {
            // 无设备时使用占位 ID，仍可记录数据
            deviceId = "dev_auto_" + request.getElderId();
        }

        // 3. 写入 sensor_data
        LocalDateTime now = LocalDateTime.now();
        List<SensorData> savedSensors = new ArrayList<>();

        if (request.getHeartRate() != null) {
            savedSensors.add(saveSensor(request.getElderId(), deviceId, "heart_rate",
                    request.getHeartRate().doubleValue(), "bpm", now));
        }
        if (request.getSpo2() != null) {
            savedSensors.add(saveSensor(request.getElderId(), deviceId, "spo2",
                    request.getSpo2().doubleValue(), "%", now));
        }
        if (request.getTemperature() != null) {
            savedSensors.add(saveSensor(request.getElderId(), deviceId, "temperature",
                    request.getTemperature().doubleValue(), "℃", now));
        }
        if (request.getActivityStatus() != null && !request.getActivityStatus().isEmpty()) {
            savedSensors.add(saveSensor(request.getElderId(), deviceId, "activity_status",
                    mapActivityToDouble(request.getActivityStatus()), "", now));
        }
        if (request.getFallStatus() != null && !request.getFallStatus().isEmpty()) {
            savedSensors.add(saveSensor(request.getElderId(), deviceId, "fall_status",
                    mapFallToDouble(request.getFallStatus()), "", now));
        }

        result.put("saved", true);
        result.put("sensor_count", savedSensors.size());

        // 4. 尝试调用 AI 健康分析，获取更准确的风险判断
        Map<String, Object> aiHealthResult = tryAiHealthAnalysis(request);

        String riskLevel;
        String riskReason;
        boolean needAlarm;
        String source;
        String alarmId = null;

        if (aiHealthResult != null) {
            // 使用 Python AI 返回的风险判断
            riskLevel = aiHealthResult.getOrDefault("risk_level", "正常").toString();
            riskReason = aiHealthResult.getOrDefault("risk_reason", "").toString();
            needAlarm = Boolean.TRUE.equals(aiHealthResult.get("need_alarm"));
            source = "python_ai_service";
            log.info("[python_ai_service] AI 健康分析结果: elderId={}, riskLevel={}, needAlarm={}",
                    request.getElderId(), riskLevel, needAlarm);
        } else {
            // 使用 Java 规则引擎
            RuleResult ruleResult = evaluateRules(request);
            riskLevel = ruleResult.riskLevel;
            riskReason = ruleResult.riskReason;
            needAlarm = ruleResult.needAlarm;
            source = "java_rule_fallback";
        }

        // 兜底升级：只要 fall_status 包含"跌倒"，强制 need_alarm=true，risk_level=高风险
        // 必须在告警生成之前执行，确保告警始终被创建
        if (request.getFallStatus() != null && request.getFallStatus().contains("跌倒")) {
            needAlarm = true;
            riskLevel = "高风险";
            if (riskReason == null || riskReason.isEmpty()) {
                riskReason = "检测到跌倒事件";
            } else if (!riskReason.contains("跌倒")) {
                riskReason = riskReason + "、疑似跌倒";
            }
            log.info("[fallback-upgrade] fall_status 包含跌倒，强制 upgrade: elderId={}, riskLevel={}, needAlarm={}",
                    request.getElderId(), riskLevel, needAlarm);
        }

        // 5. 生成告警
        if (needAlarm) {
            String alarmLevel = mapAlarmLevel(riskLevel);
            String alarmType = determineAlarmTypeFromResult(riskReason, request);

            AlarmEvent alarm = new AlarmEvent();
            alarm.setAlarmId("alarm_" + UUID.randomUUID().toString().substring(0, 8));
            alarm.setElderId(request.getElderId());
            alarm.setDeviceId(deviceId);
            alarm.setType(alarmType);
            alarm.setRiskLevel(alarmLevel);
            alarm.setStatus("pending");
            alarm.setDescription(riskReason);
            alarm.setBuilding(elder.getBuilding() != null ? elder.getBuilding() : "");
            alarm.setRoomNumber(elder.getRoom() != null ? elder.getRoom() : "");
            alarm.setUnit("");
            alarm.setLocation(request.getLocation() != null ? request.getLocation() : "");
            alarm.setSnapshotUrl("");
            alarm.setHandlerId("");
            alarm.setHandlerName("");
            alarm.setHandleNote("");
            alarm.setIsRead(false);
            alarm.setCreatedAt(now);
            alarm.setHandleTime(LocalDateTime.of(1970, 1, 1, 0, 0, 0));
            alarm.setUpdatedAt(now);

            AlarmEvent savedAlarm = alarmEventRepository.save(alarm);
            alarmId = savedAlarm.getAlarmId();

            log.info("自动生成告警: alarmId={}, elderId={}, level={}, reason={}, source={}",
                    alarmId, request.getElderId(), alarmLevel, riskReason, source);
        } else {
            log.info("设备上传正常: elderId={}, source={}", request.getElderId(), source);
        }

        result.put("risk_level", riskLevel);
        result.put("risk_reason", riskReason);
        result.put("need_alarm", needAlarm);
        result.put("alarm_id", alarmId != null ? alarmId : "");
        result.put("source", source);

        // 6. 保存 AI 分析记录到数据库（失败不影响上传接口）
        saveAiAnalysisRecord(request, aiHealthResult, riskLevel, riskReason, needAlarm, source);

        return ApiResponse.success(result);
    }

    /**
     * 尝试调用外部 Python AI 健康分析服务。
     * <p>构造兼容 health-analysis 接口的请求体，转发到 AI 服务。</p>
     *
     * @param request 设备上传请求
     * @return AI 分析结果 Map；失败或不可用时返回 null
     */
    private Map<String, Object> tryAiHealthAnalysis(DeviceUploadRequest request) {
        try {
            // 构造 health-analysis 请求体
            Map<String, Object> healthData = new HashMap<>();
            if (request.getHeartRate() != null) healthData.put("heart_rate", request.getHeartRate());
            if (request.getSpo2() != null) healthData.put("spo2", request.getSpo2());
            if (request.getTemperature() != null) healthData.put("temperature", request.getTemperature());

            Map<String, Object> aiRequest = new HashMap<>();
            aiRequest.put("elder_id", request.getElderId());
            aiRequest.put("recent_health", healthData);
            // 传递行为状态，让 Python AI 能感知跌倒和长时间静止
            if (request.getActivityStatus() != null) aiRequest.put("activity_status", request.getActivityStatus());
            if (request.getFallStatus() != null) aiRequest.put("fall_status", request.getFallStatus());

            return aiForwardService.forward("/api/ai/health-analysis", aiRequest);
        } catch (Exception e) {
            log.warn("AI 健康分析调用异常，回退到 Java 规则: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 保存 AI 分析记录到数据库。
     *
     * <p>保存失败不会抛出异常，仅记录日志，确保不影响上传接口的正常返回。</p>
     *
     * @param request        设备上传请求
     * @param aiHealthResult Python AI 返回结果（可能为 null，表示使用了 Java fallback）
     * @param riskLevel      风险等级
     * @param riskReason     风险原因
     * @param needAlarm      是否需要告警
     * @param source         分析来源
     */
    private void saveAiAnalysisRecord(DeviceUploadRequest request, Map<String, Object> aiHealthResult,
                                       String riskLevel, String riskReason, boolean needAlarm, String source) {
        try {
            AiAnalysisRecord record = new AiAnalysisRecord();
            record.setElderId(request.getElderId());
            record.setRiskLevel(riskLevel);
            record.setRiskReason(riskReason);
            record.setNeedAlarm(needAlarm);
            record.setSource(source);

            if (aiHealthResult != null) {
                // 从 Python AI 返回结果中提取详细字段
                String suggestion = getStringField(aiHealthResult, "suggestion");
                // Python AI 可能不返回 suggestion，回退到 community_suggestion 或 summary
                if (suggestion.isEmpty()) {
                    suggestion = getStringField(aiHealthResult, "community_suggestion");
                }
                if (suggestion.isEmpty()) {
                    suggestion = getStringField(aiHealthResult, "summary");
                }
                record.setSuggestion(suggestion);
                record.setElderReply(getStringField(aiHealthResult, "elder_reply"));
                record.setFamilyNotice(getStringField(aiHealthResult, "family_notice"));
                record.setCommunitySuggestion(getStringField(aiHealthResult, "community_suggestion"));
                record.setNeedWorkOrder(Boolean.TRUE.equals(aiHealthResult.get("need_work_order")));
                record.setWorkOrderType(getStringField(aiHealthResult, "work_order_type"));
                record.setModel(getStringField(aiHealthResult, "model"));
            } else {
                // Java fallback 时的默认建议
                if (needAlarm) {
                    record.setSuggestion("建议立即联系家属并安排社区人员确认老人状态");
                    record.setElderReply("请您先坐下休息，我已经帮您通知家属。");
                    record.setFamilyNotice("老人当前" + riskReason + "，建议尽快电话确认。");
                    record.setCommunitySuggestion("建议生成紧急巡检工单，安排社区人员上门确认。");
                    record.setNeedWorkOrder(true);
                    record.setWorkOrderType("紧急巡检");
                } else {
                    record.setSuggestion("建议继续保持目前的生活和监测习惯");
                    record.setElderReply("您的健康指标整体平稳，请继续保持。");
                    record.setFamilyNotice("");
                    record.setCommunitySuggestion("");
                    record.setNeedWorkOrder(false);
                    record.setWorkOrderType("");
                }
                record.setModel("");
            }

            aiAnalysisRecordService.save(record);
        } catch (Exception e) {
            log.error("保存 AI 分析记录异常（不影响上传接口）: elderId={}, error={}",
                    request.getElderId(), e.getMessage(), e);
        }
    }

    /**
     * 从 Map 中安全获取字符串字段值。
     */
    private String getStringField(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    /**
     * 使用 Java 规则引擎评估设备上传数据。
     *
     * @param request 设备上传请求
     * @return 规则评估结果
     */
    private RuleResult evaluateRules(DeviceUploadRequest request) {
        List<String> riskReasons = new ArrayList<>();
        String highestLevel = "normal";
        boolean needAlarm = false;

        // 血氧规则
        if (request.getSpo2() != null && request.getSpo2() < 92) {
            riskReasons.add("血氧偏低(" + request.getSpo2() + "%)");
            needAlarm = true;
            if (request.getSpo2() < 85) {
                highestLevel = "critical";
            } else if (highestLevel.equals("normal")) {
                highestLevel = "high";
            }
        }

        // 心率规则
        if (request.getHeartRate() != null && (request.getHeartRate() > 110 || request.getHeartRate() < 50)) {
            String desc = request.getHeartRate() > 110 ? "心率偏高(" + request.getHeartRate() + "bpm)" : "心率偏低(" + request.getHeartRate() + "bpm)";
            riskReasons.add(desc);
            needAlarm = true;
            if (request.getHeartRate() > 130 || request.getHeartRate() < 40) {
                highestLevel = "critical";
            } else if (highestLevel.equals("normal")) {
                highestLevel = "high";
            }
        }

        // 体温规则
        if (request.getTemperature() != null && (request.getTemperature() > 38.0 || request.getTemperature() < 35.0)) {
            riskReasons.add("体温异常(" + request.getTemperature() + "℃)");
            needAlarm = true;
            if (highestLevel.equals("normal")) {
                highestLevel = "high";
            }
        }

        // 跌倒规则
        if (request.getFallStatus() != null && request.getFallStatus().contains("疑似跌倒")) {
            riskReasons.add("疑似跌倒");
            needAlarm = true;
            highestLevel = "critical";
        }

        // 长时间静止 + 心率异常
        if (request.getActivityStatus() != null && request.getActivityStatus().contains("长时间静止")
                && request.getHeartRate() != null && (request.getHeartRate() > 110 || request.getHeartRate() < 50)) {
            if (!riskReasons.contains("长时间静止伴心率异常")) {
                riskReasons.add("长时间静止伴心率异常");
            }
            needAlarm = true;
            if (highestLevel.equals("normal")) {
                highestLevel = "high";
            }
        }

        String riskLevel;
        String riskReason;
        if (needAlarm) {
            riskLevel = mapRiskLevelForDisplay(highestLevel);
            riskReason = String.join("、", riskReasons);
        } else {
            riskLevel = "正常";
            riskReason = "各项指标正常";
        }

        return new RuleResult(riskLevel, riskReason, needAlarm);
    }

    /**
     * 将内部告警级别（critical/high/medium/normal）转换为显示用风险等级。
     */
    private String mapRiskLevelForDisplay(String level) {
        switch (level) {
            case "critical": return "高风险";
            case "high": return "高风险";
            case "medium": return "中风险";
            default: return "低风险";
        }
    }

    /**
     * 将风险等级文本映射为告警记录的 alarm_level 字段值。
     */
    private String mapAlarmLevel(String riskLevel) {
        if (riskLevel == null) return "normal";
        if (riskLevel.contains("高风险") || riskLevel.contains("critical")) return "critical";
        if (riskLevel.contains("中风险") || riskLevel.contains("medium")) return "high";
        return "normal";
    }

    /**
     * 根据风险原因和请求数据确定告警类型。
     */
    private String determineAlarmTypeFromResult(String riskReason, DeviceUploadRequest request) {
        if (request.getFallStatus() != null && request.getFallStatus().contains("疑似跌倒")) {
            return "fall";
        }
        if (riskReason != null) {
            if (riskReason.contains("血氧") || riskReason.contains("心率") || riskReason.contains("体温")) {
                return "health_abnormal";
            }
        }
        return "health_abnormal";
    }

    private SensorData saveSensor(String elderId, String deviceId, String type, Double value, String unit, LocalDateTime now) {
        SensorData sd = new SensorData();
        sd.setElderId(elderId);
        sd.setDeviceId(deviceId);
        sd.setSensorType(type);
        sd.setValue(value);
        sd.setUnit(unit);
        sd.setIsAbnormal(false);
        sd.setTimestamp(now);
        sd.setCreatedAt(now);
        return sensorDataRepository.save(sd);
    }

    /**
     * 将活动状态文本映射为数值索引，便于 sensor_data 存储。
     */
    private double mapActivityToDouble(String status) {
        if (status == null) return 0;
        if (status.contains("静止") || status.contains("静卧")) return 3;
        if (status.contains("行走") || status.contains("运动")) return 1;
        if (status.contains("坐着") || status.contains("坐姿")) return 2;
        return 0;
    }

    /**
     * 将跌倒状态文本映射为数值索引，便于 sensor_data 存储。
     */
    private double mapFallToDouble(String status) {
        if (status == null) return 0;
        if (status.contains("跌倒")) return 2;
        if (status.contains("疑似")) return 1;
        return 0;
    }

    /**
     * 规则引擎评估结果内部类。
     */
    private static class RuleResult {
        final String riskLevel;
        final String riskReason;
        final boolean needAlarm;

        RuleResult(String riskLevel, String riskReason, boolean needAlarm) {
            this.riskLevel = riskLevel;
            this.riskReason = riskReason;
            this.needAlarm = needAlarm;
        }
    }

}
