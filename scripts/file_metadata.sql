-- ============================================================
-- 文件元数据表 — 记录网关上传的图片文件信息
-- ============================================================
CREATE TABLE IF NOT EXISTS `file_metadata` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `file_id` varchar(64) NOT NULL COMMENT '文件唯一ID',
    `elder_id` varchar(64) DEFAULT NULL COMMENT '老人ID',
    `gateway_id` varchar(64) DEFAULT NULL COMMENT '网关ID',
    `camera_id` varchar(64) DEFAULT NULL COMMENT '摄像头ID',
    `original_name` varchar(255) DEFAULT NULL COMMENT '原始文件名',
    `file_path` varchar(500) NOT NULL COMMENT '相对存储路径',
    `file_size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
    `content_type` varchar(100) DEFAULT NULL COMMENT 'MIME类型',
    `alarm_type` varchar(50) DEFAULT NULL COMMENT '关联告警类型',
    `snapshot_time` datetime DEFAULT NULL COMMENT '抓拍时间',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_file_id` (`file_id`),
    KEY `idx_elder_id` (`elder_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网关上传文件元数据表';
