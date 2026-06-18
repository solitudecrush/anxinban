package com.anxinban.service;

/**
 * Notification 业务服务类，处理 Notification 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.NotificationDto;
import com.anxinban.dto.PageResult;
import com.anxinban.entity.Notification;
import com.anxinban.mapper.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    /** 字段含义待补充 */

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

        /**
         * createNotification 方法。
         *
         * @param dto 字段含义待补充
         */
    public NotificationDto createNotification(NotificationDto dto) {
        Notification entity = convertToEntity(dto);
        entity.setIsRead(false);
        entity.setCreatedAt(LocalDateTime.now());
        Notification saved = notificationRepository.save(entity);
        return convertToDto(saved);
    }

        /**
         * listNotifications 方法。
         *
         * @param size 大小
         */
    public PageResult<NotificationDto> listNotifications(String userId, String userType, int page, int size) {
        List<Notification> entities;
        if (userType != null && !userType.isEmpty()) {
            entities = notificationRepository.findByUserIdAndUserType(userId, userType);
        } else {
            entities = notificationRepository.findByUserId(userId);
        }
        List<NotificationDto> dtos = entities.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
        long total = dtos.size();
        List<NotificationDto> paginated = paginate(dtos, page, size);
        return new PageResult<>(paginated, total, page, size);
    }

        /**
         * 获取数量。
         *
         * @return 数量
         */
    public long getUnreadCount(String userId, String userType) {
        if (userType != null && !userType.isEmpty()) {
            return notificationRepository.countByUserIdAndUserTypeAndIsRead(userId, userType, false);
        }
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

        /**
         * markAsRead 方法。
         *
         * @param notificationId 唯一标识，主键
         */
    public boolean markAsRead(String notificationId) {
        Notification existing = notificationRepository.findByNotificationId(notificationId);
        if (existing == null) return false;
        existing.setIsRead(true);
        notificationRepository.save(existing);
        return true;
    }

        /**
         * markAllAsRead 方法。
         *
         * @param userType 类型标识
         */
    public boolean markAllAsRead(String userId, String userType) {
        List<Notification> entities;
        if (userType != null && !userType.isEmpty()) {
            entities = notificationRepository.findByUserIdAndUserTypeAndIsRead(userId, userType, false);
        } else {
            entities = notificationRepository.findByUserIdAndIsRead(userId, false);
        }
        for (Notification n : entities) {
            n.setIsRead(true);
        }
        notificationRepository.saveAll(entities);
        return true;
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private NotificationDto convertToDto(Notification entity) {
        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(entity.getNotificationId());
        dto.setUserId(entity.getUserId());
        dto.setUserType(entity.getUserType());
        dto.setNotificationType(entity.getNotificationType());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setIsRead(entity.getIsRead());
        dto.setBuilding(entity.getBuilding());
        dto.setRoom(entity.getRoom());
        dto.setOrderId(entity.getOrderId());
        dto.setRequestId(entity.getRequestId());
        dto.setElderId(entity.getElderId());
        dto.setRelatedId(entity.getRelatedId());
        dto.setCreateTime(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        return dto;
    }

        /**
         * convertToEntity 方法。
         *
         * @param dto 字段含义待补充
         */
    private Notification convertToEntity(NotificationDto dto) {
        Notification entity = new Notification();
        entity.setNotificationId(dto.getNotificationId());
        entity.setUserId(dto.getUserId());
        entity.setUserType(dto.getUserType());
        entity.setNotificationType(dto.getNotificationType());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setIsRead(dto.getIsRead());
        entity.setBuilding(dto.getBuilding());
        entity.setRoom(dto.getRoom());
        entity.setOrderId(dto.getOrderId());
        entity.setRequestId(dto.getRequestId());
        entity.setElderId(dto.getElderId());
        entity.setRelatedId(dto.getRelatedId());
        return entity;
    }

        /**
         * paginate 方法。
         *
         * @param size 大小
         */
    private List<NotificationDto> paginate(List<NotificationDto> items, int page, int size) {
        int from = Math.max((page - 1) * size, 0);
        int to = Math.min(from + size, items.size());
        if (from > items.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(items.subList(from, to));
    }
}
