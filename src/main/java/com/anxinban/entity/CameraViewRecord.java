package com.anxinban.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 监控查看记录实体 — camera_request 的从表，记录每次实际查看监控的审计日志。
 *
 * <p>对应数据字典：camera_view_record</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "camera_view_record")
public class CameraViewRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联监控申请 ID */
    @Column(name = "camera_request_id", nullable = false)
    private String cameraRequestId;

    /** 查看人 ID（staff.id） */
    @Column(name = "staff_id", nullable = false)
    private String staffId;

    /** 查看的摄像头类型：door / living / bedroom */
    @Column(name = "camera_type", nullable = false, length = 20)
    private String cameraType;

    /** 查看时长（秒） */
    @Column(nullable = false)
    private Integer duration;

    /** 查看时间 */
    @Column(name = "view_time", nullable = false)
    private LocalDateTime viewTime;

    /** 记录创建时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ==================== Getters and Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCameraRequestId() { return cameraRequestId; }
    public void setCameraRequestId(String cameraRequestId) { this.cameraRequestId = cameraRequestId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getCameraType() { return cameraType; }
    public void setCameraType(String cameraType) { this.cameraType = cameraType; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public LocalDateTime getViewTime() { return viewTime; }
    public void setViewTime(LocalDateTime viewTime) { this.viewTime = viewTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
