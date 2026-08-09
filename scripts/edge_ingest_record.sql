-- ============================================================
-- 边缘网关数据接入 — 幂等记录表
-- 说明：用于家庭 PC 边缘网关 HTTP 重试时的幂等去重
-- 通过 upload_id UNIQUE 约束保证同一请求不会重复执行业务副作用
-- ============================================================
CREATE TABLE IF NOT EXISTS `edge_ingest_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `upload_id` VARCHAR(64) NOT NULL COMMENT 'PC 生成的幂等上传 UUID',
    `edge_id` VARCHAR(64) NOT NULL COMMENT '边缘网关标识（如 pc_edge_001）',
    `device_id` VARCHAR(64) DEFAULT NULL COMMENT '上报数据的设备 ID',
    `message_type` VARCHAR(32) NOT NULL COMMENT 'MQTT 消息类型：vitals/imu/sos/fall/device_status',
    `received_at` DATETIME NOT NULL COMMENT 'PC 实际收到 MQTT 消息的时间',
    `created_at` DATETIME NOT NULL COMMENT '云端入库时间',
    UNIQUE KEY `uk_upload_id` (`upload_id`),
    INDEX `idx_device_id` (`device_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='边缘网关数据接入幂等记录表';
