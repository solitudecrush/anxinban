package com.anxinban.entity;


/**
 * Notification 实体类 — APP 通知表（app_notification）。
 * 存储推送给家属 APP 的通知。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 主键，自增 */
    private Long id;

    @Column(name = "notification_id", nullable = false, unique = true)
    /** 通知业务编号 */
    private String notificationId;

    @Column(name = "user_id", nullable = false)
    /** 关联用户 ID */
    private String userId;

    @Column(name = "user_type")
    /** 用户类型 */
    private String userType;

    @Column(name = "type")
    /** 通知类型：intrusion / order / camera / sos / alert */
    private String type;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    @Column(name = "is_read")
    /** 是否已读 */
    private Boolean isRead = false;

    @Column(name = "building")
    /** 楼栋（intrusion 用） */
    private String building;

    @Column(name = "room")
    /** 房号（intrusion 用） */
    private String room;

    @Column(name = "work_order_id")
    /** 关联工单 ID */
    private String workOrderId;

    @Column(name = "camera_request_id")
    /** 关联监控申请 ID */
    private String cameraRequestId;

    @Column(name = "elder_id")
    /** 关联老人 ID */
    private String elderId;

    @Column(name = "notify_time")
    /** 通知时间 */
    private LocalDateTime notifyTime;

    @Column(name = "created_at")
    /** 创建时间 */
    private LocalDateTime createdAt;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public Long getId() { return id; }
    /**
     * 设置唯一标识，主键。
     *
     * @param id 唯一标识，主键
     */
    public void setId(Long id) { this.id = id; }

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getNotificationId() { return notificationId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param notificationId 唯一标识，主键
     */
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    /**
     * 获取关联用户 ID。
     *
     * @return 关联用户 ID
     */
    public String getUserId() { return userId; }
    /**
     * 设置关联用户 ID。
     *
     * @param userId 关联用户 ID
     */
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getUserType() { return userType; }
    /**
     * 设置类型标识。
     *
     * @param userType 类型标识
     */
    public void setUserType(String userType) { this.userType = userType; }

    /**
     * 获取通知类型。
     *
     * @return 通知类型
     */
    public String getType() { return type; }
    /**
     * 设置通知类型。
     *
     * @param type 通知类型
     */
    public void setType(String type) { this.type = type; }

    /**
     * 获取标题。
     *
     * @return 标题
     */
    public String getTitle() { return title; }
    /**
     * 设置标题。
     *
     * @param title 标题
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * 获取内容。
     *
     * @return 内容
     */
    public String getContent() { return content; }
    /**
     * 设置内容。
     *
     * @param content 内容
     */
    public void setContent(String content) { this.content = content; }

    /**
     * 获取是否已读。
     *
     * @return 是否已读
     */
    public Boolean getIsRead() { return isRead; }
    /**
     * 设置是否已读。
     *
     * @param isRead 是否已读
     */
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    /**
     * 获取楼栋。
     *
     * @return 楼栋
     */
    public String getBuilding() { return building; }
    /**
     * 设置楼栋。
     *
     * @param building 楼栋
     */
    public void setBuilding(String building) { this.building = building; }

    /**
     * 获取房号。
     *
     * @return 房号
     */
    public String getRoom() { return room; }
    /**
     * 设置房号。
     *
     * @param room 房号
     */
    public void setRoom(String room) { this.room = room; }

    /**
     * 获取关联工单 ID。
     *
     * @return 关联工单 ID
     */
    public String getWorkOrderId() { return workOrderId; }
    /**
     * 设置关联工单 ID。
     *
     * @param workOrderId 关联工单 ID
     */
    public void setWorkOrderId(String workOrderId) { this.workOrderId = workOrderId; }

    /**
     * 获取关联监控申请 ID。
     *
     * @return 关联监控申请 ID
     */
    public String getCameraRequestId() { return cameraRequestId; }
    /**
     * 设置关联监控申请 ID。
     *
     * @param cameraRequestId 关联监控申请 ID
     */
    public void setCameraRequestId(String cameraRequestId) { this.cameraRequestId = cameraRequestId; }

    /**
     * 获取关联老人 ID。
     *
     * @return 关联老人 ID
     */
    public String getElderId() { return elderId; }
    /**
     * 设置关联老人 ID。
     *
     * @param elderId 关联老人 ID
     */
    public void setElderId(String elderId) { this.elderId = elderId; }

    /**
     * 获取通知时间。
     *
     * @return 通知时间
     */
    public LocalDateTime getNotifyTime() { return notifyTime; }
    /**
     * 设置通知时间。
     *
     * @param notifyTime 通知时间
     */
    public void setNotifyTime(LocalDateTime notifyTime) { this.notifyTime = notifyTime; }

    /**
     * 获取创建时间。
     *
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * 设置创建时间。
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
