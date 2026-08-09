package com.anxinban.service;

/**
 * Sos 业务服务类，处理 Sos 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.SosDto;
import com.anxinban.dto.SosDto.SmsRecipient;
import com.anxinban.entity.ElderUser;
import com.anxinban.entity.EmergencyContact;
import com.anxinban.entity.SosRecord;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.mapper.EmergencyContactRepository;
import com.anxinban.mapper.SosRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SosService {
    private static final DateTimeFormatter SMS_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SosRecordRepository sosRecordRepository;
    private final ElderUserRepository elderUserRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final AmapGeocodeService amapGeocodeService;

    @Autowired
    public SosService(SosRecordRepository sosRecordRepository,
                      ElderUserRepository elderUserRepository,
                      EmergencyContactRepository emergencyContactRepository,
                      AmapGeocodeService amapGeocodeService) {
        this.sosRecordRepository = sosRecordRepository;
        this.elderUserRepository = elderUserRepository;
        this.emergencyContactRepository = emergencyContactRepository;
        this.amapGeocodeService = amapGeocodeService;
    }

        /**
         * triggerSos 方法。
         *
         * @param dto 字段含义待补充
         */
    public SosDto triggerSos(SosDto dto) {
        SosRecord entity = new SosRecord();
        entity.setSosId(dto.getSosId());
        entity.setElderId(dto.getElderId());
        entity.setTriggerTime(LocalDateTime.now());
        entity.setStatus("triggered");
        entity.setLocation(dto.getLocation());
        entity.setCreatedAt(LocalDateTime.now());
        SosRecord saved = sosRecordRepository.save(entity);
        SosDto result = convertToDto(saved);

        // 拼接短信内容
        buildSmsInfo(result, dto.getElderId(), dto.getLocation());

        return result;
    }

    /**
     * 构建短信信息：查询老人资料和紧急联系人，拼接短信内容。
     */
    private void buildSmsInfo(SosDto dto, String elderId, String location) {
        // 查询老人信息
        ElderUser elder = elderUserRepository.findByElderId(elderId);
        String elderName = elder != null ? elder.getName() : "";
        Integer elderAge = elder != null ? elder.getAge() : null;

        // 查询紧急联系人列表
        List<EmergencyContact> contacts = emergencyContactRepository.findByElderIdOrderBySortOrderAsc(elderId);
        List<SmsRecipient> recipients = new ArrayList<>();
        if (contacts != null) {
            for (EmergencyContact c : contacts) {
                SmsRecipient r = new SmsRecipient();
                r.setName(c.getName());
                r.setPhone(c.getPhone());
                recipients.add(r);
            }
        }
        // 如果紧急联系人为空，尝试使用老人档案中的家属电话
        if (recipients.isEmpty() && elder != null) {
            String familyPhone = elder.getFamilyPhone();
            if (familyPhone != null && !familyPhone.isBlank()) {
                SmsRecipient r = new SmsRecipient();
                r.setName("家属");
                r.setPhone(familyPhone);
                recipients.add(r);
            }
        }
        dto.setSmsRecipients(recipients);

        // 拼接短信内容
        String triggerTime = dto.getTriggerTime() != null ? dto.getTriggerTime() : LocalDateTime.now().format(SMS_TIME_FORMATTER);
        String positionDesc = amapGeocodeService.getApproximateLocation(location);

        StringBuilder sms = new StringBuilder();
        sms.append("【银龄智护紧急求助】\n");
        sms.append("老人 ");
        sms.append(elderName != null && !elderName.isBlank() ? elderName : "未知");
        if (elderAge != null) {
            sms.append("（").append(elderAge).append("岁）");
        }
        sms.append("于 ").append(triggerTime).append(" 触发紧急求助！\n");
        sms.append("位置：").append(positionDesc).append("\n");
        sms.append("请尽快确认老人安全！");

        dto.setSmsContent(sms.toString());
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public SosDto getSos(String sosId) {
        SosRecord entity = sosRecordRepository.findBySosId(sosId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * listSosByElder 方法。
         *
         * @param elderId 关联老人用户 ID
         */
    public List<SosDto> listSosByElder(String elderId) {
        return sosRecordRepository.findByElderIdOrderByTriggerTimeDesc(elderId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

        /**
         * handleSos 方法。
         *
         * @param handlerId 唯一标识，主键
         */
    public SosDto handleSos(String sosId, String handlerId) {
        SosRecord existing = sosRecordRepository.findBySosId(sosId);
        if (existing == null) {
            return null;
        }
        existing.setStatus("handled");
        existing.setHandlerId(handlerId);
        existing.setHandledTime(LocalDateTime.now());
        SosRecord saved = sosRecordRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private SosDto convertToDto(SosRecord entity) {
        SosDto dto = new SosDto();
        dto.setSosId(entity.getSosId());
        dto.setElderId(entity.getElderId());
        dto.setTriggerTime(entity.getTriggerTime() != null ? entity.getTriggerTime().toString() : null);
        dto.setStatus(entity.getStatus());
        dto.setLocation(entity.getLocation());
        dto.setHandlerId(entity.getHandlerId());
        dto.setHandledTime(entity.getHandledTime() != null ? entity.getHandledTime().toString() : null);
        return dto;
    }
}
