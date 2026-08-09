package com.anxinban.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 边缘网关数据接入记录 — 用于 HTTP 幂等去重。
 *
 * <p>每条记录对应一次唯一的 {@code upload_id}，通过数据库 UNIQUE 约束保证：
 * PC 因 HTTP 超时/5xx 重试时，同一 upload_id 的业务副作用不会重复执行。</p>
 *
 * <p>表结构：</p>
 * <pre>{@code
 * CREATE TABLE IF NOT EXISTS edge_ingest_record (
 *     id BIGINT AUTO_INCREMENT PRIMARY KEY,
 *     upload_id VARCHAR(64) NOT NULL UNIQUE,
 *     edge_id VARCHAR(64) NOT NULL,
 *     device_id VARCHAR(64) DEFAULT NULL,
 *     message_type VARCHAR(32) NOT NULL,
 *     received_at DATETIME NOT NULL,
 *     created_at DATETIME NOT NULL,
 *     INDEX idx_device_id (device_id),
 *     INDEX idx_created_at (created_at)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 * }</pre>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "edge_ingest_record")
public class EdgeIngestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** PC 生成的幂等上传 ID（UUID），UNIQUE 约束保证去重 */
    @Column(name = "upload_id", nullable = false, unique = true, length = 64)
    private String uploadId;

    /** 边缘网关标识（如 pc_edge_001） */
    @Column(name = "edge_id", nullable = false, length = 64)
    private String edgeId;

    /** 上报数据的设备 ID（来自 payload.device_id） */
    @Column(name = "device_id", length = 64)
    private String deviceId;

    /** MQTT 消息类型：vitals / imu / sos / fall / device_status */
    @Column(name = "message_type", nullable = false, length = 32)
    private String messageType;

    /** PC 实际收到 MQTT 消息的时间 */
    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    /** 云端入库时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ==================== 工厂方法 ====================

    public static EdgeIngestRecord create(String uploadId, String edgeId, String deviceId,
                                          String messageType, double receivedAtEpochSeconds) {
        EdgeIngestRecord record = new EdgeIngestRecord();
        record.uploadId = uploadId;
        record.edgeId = edgeId;
        record.deviceId = deviceId;
        record.messageType = messageType;
        // 将 epoch 秒（含小数）转为 LocalDateTime
        long epochMillis = (long) (receivedAtEpochSeconds * 1000);
        record.receivedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.of("Asia/Shanghai"));
        record.createdAt = LocalDateTime.now();
        return record;
    }

    // ==================== Getters & Setters ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(String edgeId) {
        this.edgeId = edgeId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
