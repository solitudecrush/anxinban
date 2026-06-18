package com.anxinban.service;

/**
 * Elder 业务服务类，处理 Elder 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ElderDto;
import com.anxinban.dto.PageResult;
import com.anxinban.entity.ElderUser;
import com.anxinban.mapper.ElderUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElderService {
    /** 字段含义待补充 */

    private final ElderUserRepository elderUserRepository;

    @Autowired
    public ElderService(ElderUserRepository elderUserRepository) {
        this.elderUserRepository = elderUserRepository;
    }

        /**
         * createElder 方法。
         *
         * @param elder 字段含义待补充
         */
    public ElderDto createElder(ElderDto elder) {
        ElderUser entity = convertToEntity(elder);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        ElderUser saved = elderUserRepository.save(entity);
        return convertToDto(saved);
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public ElderDto getElder(String elderId) {
        ElderUser entity = elderUserRepository.findByElderId(elderId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * listElders 方法。
         *
         * @param size 大小
         */
    public PageResult<ElderDto> listElders(String name, String building, String roomNumber, String healthStatus, int page, int size) {
        List<ElderUser> entities = elderUserRepository.findAll();
        List<ElderDto> filtered = entities.stream()
                .filter(e -> name == null || name.isEmpty() || (e.getName() != null && e.getName().contains(name)))
                .filter(e -> building == null || building.isEmpty() || building.equals(e.getBuilding()))
                .filter(e -> roomNumber == null || roomNumber.isEmpty() || (e.getRoomNumber() != null && e.getRoomNumber().contains(roomNumber)))
                .filter(e -> healthStatus == null || healthStatus.isEmpty() || healthStatus.equals(e.getHealthStatus()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
        long total = filtered.size();
        List<ElderDto> paginated = paginate(filtered, page, size);
        return new PageResult<>(paginated, total, page, size);
    }

        /**
         * updateElder 方法。
         *
         * @param update 日期
         */
    public ElderDto updateElder(String elderId, ElderDto update) {
        ElderUser existing = elderUserRepository.findByElderId(elderId);
        if (existing == null) {
            return null;
        }
        if (update.getName() != null) existing.setName(update.getName());
        if (update.getGender() != null) existing.setGender(update.getGender());
        if (update.getAge() != null) existing.setAge(update.getAge());
        if (update.getAddress() != null) existing.setAddress(update.getAddress());
        if (update.getBuilding() != null) existing.setBuilding(update.getBuilding());
        if (update.getRoomNumber() != null) existing.setRoomNumber(update.getRoomNumber());
        if (update.getPhone() != null) existing.setPhone(update.getPhone());
        if (update.getHealthNote() != null) existing.setHealthNote(update.getHealthNote());
        if (update.getHealthStatus() != null) existing.setHealthStatus(update.getHealthStatus());
        if (update.getHealthStatusText() != null) existing.setHealthStatusText(update.getHealthStatusText());
        if (update.getFamilyPhone() != null) existing.setFamilyPhone(update.getFamilyPhone());
        if (update.getGuardianPhone() != null) existing.setFamilyPhone(update.getGuardianPhone());
        if (update.getAvatar() != null) existing.setAvatar(update.getAvatar());
        if (update.getHasCamera() != null) existing.setHasCamera(update.getHasCamera());
        if (update.getCameraAuthUntil() != null) existing.setCameraAuthUntil(update.getCameraAuthUntil());
        if (update.getCameraPending() != null) existing.setCameraPending(update.getCameraPending());
        if (update.getTags() != null) existing.setHealthNote(update.getTags().stream().collect(Collectors.joining(",")));
        existing.setUpdateTime(LocalDateTime.now());
        ElderUser saved = elderUserRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * deleteElder 方法。
         *
         * @param elderId 关联老人用户 ID
         */
    public boolean deleteElder(String elderId) {
        ElderUser existing = elderUserRepository.findByElderId(elderId);
        if (existing != null) {
            elderUserRepository.delete(existing);
            return true;
        }
        return false;
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private ElderDto convertToDto(ElderUser entity) {
        ElderDto dto = new ElderDto();
        dto.setElderId(entity.getElderId());
        dto.setName(entity.getName());
        dto.setAge(entity.getAge());
        dto.setGender(entity.getGender());
        dto.setAddress(entity.getAddress());
        dto.setBuilding(entity.getBuilding());
        dto.setRoomNumber(entity.getRoomNumber());
        dto.setPhone(entity.getPhone());
        dto.setHealthNote(entity.getHealthNote());
        dto.setHealthStatus(entity.getHealthStatus());
        dto.setHealthStatusText(entity.getHealthStatusText());
        dto.setFamilyPhone(entity.getFamilyPhone());
        dto.setGuardianPhone(entity.getFamilyPhone());
        dto.setAvatar(entity.getAvatar());
        dto.setHasCamera(entity.getHasCamera());
        dto.setCameraAuthUntil(entity.getCameraAuthUntil());
        dto.setCameraPending(entity.getCameraPending());
        dto.setLastOnline(entity.getLastOnline() != null ? entity.getLastOnline().toString() : null);
        if (entity.getHealthNote() != null && !entity.getHealthNote().isEmpty()) {
            dto.setTags(List.of(entity.getHealthNote().split(",")));
        }
        return dto;
    }

        /**
         * convertToEntity 方法。
         *
         * @param dto 字段含义待补充
         */
    private ElderUser convertToEntity(ElderDto dto) {
        ElderUser entity = new ElderUser();
        entity.setElderId(dto.getElderId());
        entity.setName(dto.getName());
        entity.setAge(dto.getAge());
        entity.setGender(dto.getGender());
        entity.setAddress(dto.getAddress());
        entity.setBuilding(dto.getBuilding());
        entity.setRoomNumber(dto.getRoomNumber());
        entity.setPhone(dto.getPhone());
        entity.setHealthNote(dto.getHealthNote() != null ? dto.getHealthNote() : dto.getHealthStatus());
        entity.setHealthStatus(dto.getHealthStatus());
        entity.setHealthStatusText(dto.getHealthStatusText());
        entity.setFamilyPhone(dto.getFamilyPhone() != null ? dto.getFamilyPhone() : dto.getGuardianPhone());
        entity.setAvatar(dto.getAvatar());
        entity.setHasCamera(dto.getHasCamera());
        entity.setCameraAuthUntil(dto.getCameraAuthUntil());
        entity.setCameraPending(dto.getCameraPending());
        if (dto.getTags() != null) {
            entity.setHealthNote(dto.getTags().stream().collect(Collectors.joining(",")));
        }
        return entity;
    }

        /**
         * paginate 方法。
         *
         * @param size 大小
         */
    private List<ElderDto> paginate(List<ElderDto> items, int page, int size) {
        int from = Math.max((page - 1) * size, 0);
        int to = Math.min(from + size, items.size());
        if (from > items.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(items.subList(from, to));
    }
}
