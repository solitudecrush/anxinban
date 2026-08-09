package com.anxinban.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 文件元数据实体 — 记录网关上传的图片文件信息。
 *
 * <p>对应数据库表 file_metadata。每个上传的文件在此表中有一条记录，
 * 记录文件存储路径、关联的老人、网关、摄像头信息，以及可选的告警类型。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "file_metadata")
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 文件唯一业务 ID */
    @Column(name = "file_id", nullable = false, unique = true, length = 64)
    private String fileId;

    /** 关联老人 ID */
    @Column(name = "elder_id", length = 64)
    private String elderId;

    /** 网关设备 ID */
    @Column(name = "gateway_id", length = 64)
    private String gatewayId;

    /** 摄像头 ID（区分多路摄像头） */
    @Column(name = "camera_id", length = 64)
    private String cameraId;

    /** 原始文件名 */
    @Column(name = "original_name", length = 255)
    private String originalName;

    /** 相对存储路径（如 /uploads/snapshots/2026-08-09/xxx.jpg） */
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /** 文件大小（字节） */
    @Column(name = "file_size")
    private Long fileSize;

    /** MIME 类型 */
    @Column(name = "content_type", length = 100)
    private String contentType;

    /** 关联告警类型（intrusion/fall 等） */
    @Column(name = "alarm_type", length = 50)
    private String alarmType;

    /** 抓拍时间 */
    @Column(name = "snapshot_time")
    private LocalDateTime snapshotTime;

    /** 上传时间 */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ==================== Getters & Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }

    public String getElderId() { return elderId; }
    public void setElderId(String elderId) { this.elderId = elderId; }

    public String getGatewayId() { return gatewayId; }
    public void setGatewayId(String gatewayId) { this.gatewayId = gatewayId; }

    public String getCameraId() { return cameraId; }
    public void setCameraId(String cameraId) { this.cameraId = cameraId; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getAlarmType() { return alarmType; }
    public void setAlarmType(String alarmType) { this.alarmType = alarmType; }

    public LocalDateTime getSnapshotTime() { return snapshotTime; }
    public void setSnapshotTime(LocalDateTime snapshotTime) { this.snapshotTime = snapshotTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
