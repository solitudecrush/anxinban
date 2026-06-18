package com.anxinban.mapper;


/**
 * Notification 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Notification findByNotificationId(String notificationId);
    List<Notification> findByUserId(String userId);
    List<Notification> findByUserIdAndUserType(String userId, String userType);
    List<Notification> findByUserIdAndIsRead(String userId, Boolean isRead);
    List<Notification> findByUserIdAndUserTypeAndIsRead(String userId, String userType, Boolean isRead);
    long countByUserIdAndIsRead(String userId, Boolean isRead);
    long countByUserIdAndUserTypeAndIsRead(String userId, String userType, Boolean isRead);
}
