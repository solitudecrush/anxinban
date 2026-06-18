-- ============================================================
-- 安心伴智慧养老守护系统 - 数据库建表及初始数据脚本
-- 版本: 2026-06-14
-- 适用: MySQL 8.0+
-- 说明: 表结构与 JPA Entity 对齐；初始数据无 NULL 空字段（业务待处理项除外）
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. 老人用户表 (elder_user)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `elder_user`;
CREATE TABLE `elder_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `elder_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '老人业务ID',
  `name` VARCHAR(100) NOT NULL COMMENT '姓名',
  `age` INT NOT NULL COMMENT '年龄',
  `gender` VARCHAR(10) NOT NULL COMMENT '性别',
  `address` VARCHAR(255) NOT NULL COMMENT '住址',
  `building` VARCHAR(50) NOT NULL COMMENT '所在楼栋',
  `room_number` VARCHAR(50) NOT NULL COMMENT '房间号',
  `phone` VARCHAR(50) NOT NULL COMMENT '老人手机号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `health_status` VARCHAR(50) NOT NULL COMMENT '健康状态(normal/warning/danger)',
  `health_status_text` VARCHAR(50) NOT NULL COMMENT '健康状态文本',
  `health_note` VARCHAR(255) NOT NULL COMMENT '健康备注/标签',
  `family_phone` VARCHAR(50) NOT NULL COMMENT '家属电话',
  `community_id` VARCHAR(50) NOT NULL COMMENT '所属社区ID',
  `avatar` VARCHAR(255) NOT NULL COMMENT '头像URL',
  `has_camera` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否有摄像头',
  `camera_auth_until` BIGINT NOT NULL DEFAULT 0 COMMENT '监控授权到期时间戳',
  `camera_pending` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否有待审批监控申请',
  `last_online` DATETIME NOT NULL COMMENT '最后在线时间',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='老人用户表';

INSERT INTO `elder_user` VALUES
(NULL, 'elder_001', '张三', 78, '男', '北京市朝阳区建国路88号', '1号楼', '1-201', '13912345601', '123456', 'warning', '关注', '高血压,糖尿病', '13800138002', 'community_001', '/uploads/avatar/elder_001.jpg', 1, 0, 0, '2025-05-17 09:00:00', '2025-01-10 09:00:00', '2025-05-17 10:00:00'),
(NULL, 'elder_002', '李四', 82, '女', '北京市海淀区中关村大街1号', '2号楼', '2-301', '13912345602', '123456', 'danger', '高危', '心脏病,骨质疏松', '13900139002', 'community_001', '/uploads/avatar/elder_002.jpg', 1, 0, 1, '2025-05-17 08:30:00', '2025-01-15 10:30:00', '2025-05-17 10:00:00'),
(NULL, 'elder_003', '王五', 75, '男', '北京市西城区金融街2号', '3号楼', '3-401', '13912345603', '123456', 'normal', '正常', '轻度认知障碍', '13700137002', 'community_001', '/uploads/avatar/elder_003.jpg', 1, 0, 0, '2025-05-17 08:30:00', '2025-02-01 14:00:00', '2025-05-17 10:00:00'),
(NULL, 'elder_004', '赵六', 80, '女', '北京市东城区东直门外大街10号', '4号楼', '4-102', '13912345604', '123456', 'warning', '关注', '糖尿病,关节炎', '13600136002', 'community_001', '/uploads/avatar/elder_004.jpg', 1, 0, 0, '2025-05-17 07:00:00', '2025-03-01 09:00:00', '2025-05-17 10:00:00'),
(NULL, 'elder_005', '孙七', 76, '男', '北京市丰台区南三环中路12号', '5号楼', '5-502', '13912345605', '123456', 'danger', '高危', '脑梗后遗症,高血压', '13500135002', 'community_001', '/uploads/avatar/elder_005.jpg', 0, 0, 1, '2025-05-16 22:00:00', '2025-03-15 10:00:00', '2025-05-17 09:00:00');

-- ------------------------------------------------------------
-- 2. 设备表 (device)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `device_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '设备业务ID',
  `elder_id` VARCHAR(50) NOT NULL COMMENT '所属老人ID',
  `device_type` VARCHAR(50) NOT NULL COMMENT '设备类型',
  `device_name` VARCHAR(100) NOT NULL COMMENT '设备名称',
  `location` VARCHAR(255) NOT NULL COMMENT '安装位置',
  `building` VARCHAR(50) NOT NULL COMMENT '所在楼栋',
  `room` VARCHAR(50) NOT NULL COMMENT '房间号',
  `status` VARCHAR(50) NOT NULL COMMENT 'online/offline',
  `battery_level` INT NOT NULL DEFAULT 0 COMMENT '电量(摄像头等有线设备填0)',
  `last_online_time` DATETIME NOT NULL COMMENT '最后在线时间',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

INSERT INTO `device` VALUES
(NULL, 'dev_001', 'elder_001', '手环', '智能手环', '随身佩戴', '1号楼', '1-201', 'online', 78, '2025-05-17 09:45:00', '2025-01-10 09:00:00', '2025-05-17 09:45:00'),
(NULL, 'dev_002', 'elder_001', '摄像头', '客厅摄像头', '客厅', '1号楼', '1-201', 'online', 0, '2025-05-17 09:00:00', '2025-01-10 09:00:00', '2025-05-17 09:00:00'),
(NULL, 'dev_003', 'elder_001', '摄像头', '门口摄像头', '门口', '1号楼', '1-201', 'online', 0, '2025-05-17 08:30:00', '2025-01-10 09:00:00', '2025-05-17 08:30:00'),
(NULL, 'dev_004', 'elder_002', '手环', '智能手环', '随身佩戴', '2号楼', '2-301', 'online', 62, '2025-05-17 09:50:00', '2025-01-15 10:30:00', '2025-05-17 09:50:00'),
(NULL, 'dev_005', 'elder_002', '摄像头', '客厅摄像头', '客厅', '2号楼', '2-301', 'online', 0, '2025-05-17 09:15:00', '2025-01-15 10:30:00', '2025-05-17 09:15:00'),
(NULL, 'dev_006', 'elder_002', '烟感', '厨房烟感', '厨房', '2号楼', '2-301', 'online', 0, '2025-05-17 09:00:00', '2025-01-15 10:30:00', '2025-05-17 09:00:00'),
(NULL, 'dev_007', 'elder_003', '手环', '智能手环', '随身佩戴', '3号楼', '3-401', 'online', 45, '2025-05-17 08:30:00', '2025-02-01 14:00:00', '2025-05-17 08:30:00'),
(NULL, 'dev_008', 'elder_003', '摄像头', '客厅摄像头', '客厅', '3号楼', '3-401', 'online', 0, '2025-05-17 09:15:00', '2025-02-01 14:00:00', '2025-05-17 09:15:00'),
(NULL, 'dev_009', 'elder_003', '床垫传感器', '智能床垫', '卧室', '3号楼', '3-401', 'offline', 0, '2025-05-15 22:00:00', '2025-02-01 14:00:00', '2025-05-15 22:00:00'),
(NULL, 'dev_010', 'elder_004', '手环', '智能手环', '随身佩戴', '4号楼', '4-102', 'online', 55, '2025-05-17 08:00:00', '2025-03-01 09:00:00', '2025-05-17 08:00:00'),
(NULL, 'dev_011', 'elder_004', '摄像头', '卧室摄像头', '卧室', '4号楼', '4-102', 'online', 0, '2025-05-17 07:30:00', '2025-03-01 09:00:00', '2025-05-17 07:30:00'),
(NULL, 'dev_012', 'elder_005', '手环', '智能手环', '随身佩戴', '5号楼', '5-502', 'online', 30, '2025-05-16 22:00:00', '2025-03-15 10:00:00', '2025-05-16 22:00:00'),
(NULL, 'dev_013', 'elder_001', '门锁', '智能指纹门锁', '大门', '1号楼', '1-201', 'online', 85, '2025-05-17 09:30:00', '2025-01-10 09:00:00', '2025-05-17 09:30:00'),
(NULL, 'dev_014', 'elder_002', '窗帘', '智能窗帘电机', '卧室', '2号楼', '2-301', 'online', 0, '2025-05-17 09:00:00', '2025-01-15 10:30:00', '2025-05-17 09:00:00'),
(NULL, 'dev_015', 'elder_001', '灯光', '智能吸顶灯', '客厅', '1号楼', '1-201', 'online', 0, '2025-05-17 09:00:00', '2025-01-10 09:00:00', '2025-05-17 09:00:00'),
(NULL, 'dev_016', 'elder_004', '蜂鸣器', '紧急报警蜂鸣器', '客厅', '4号楼', '4-102', 'online', 0, '2025-05-17 08:00:00', '2025-03-01 09:00:00', '2025-05-17 08:00:00'),
(NULL, 'dev_017', 'elder_005', '烟感', '客厅烟感', '客厅', '5号楼', '5-502', 'online', 0, '2025-05-16 22:00:00', '2025-03-15 10:00:00', '2025-05-16 22:00:00');

-- ------------------------------------------------------------
-- 3. 传感器数据表 (sensor_data)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sensor_data`;
CREATE TABLE `sensor_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `elder_id` VARCHAR(50) NOT NULL COMMENT '老人ID',
  `device_id` VARCHAR(50) NOT NULL COMMENT '设备ID',
  `sensor_type` VARCHAR(50) NOT NULL COMMENT '传感器类型',
  `value` DOUBLE NOT NULL COMMENT '数值',
  `unit` VARCHAR(50) NOT NULL COMMENT '单位',
  `is_abnormal` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否异常',
  `timestamp` DATETIME NOT NULL COMMENT '数据时间戳',
  `created_at` DATETIME NOT NULL COMMENT '入库时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='传感器数据表';

INSERT INTO `sensor_data` (`elder_id`, `device_id`, `sensor_type`, `value`, `unit`, `is_abnormal`, `timestamp`, `created_at`) VALUES
('elder_001', 'dev_001', 'heart_rate', 72, 'bpm', 0, '2025-05-17 08:00:00', '2025-05-17 08:01:00'),
('elder_001', 'dev_001', 'heart_rate', 85, 'bpm', 0, '2025-05-17 09:00:00', '2025-05-17 09:01:00'),
('elder_001', 'dev_001', 'heart_rate', 95, 'bpm', 1, '2025-05-17 09:30:00', '2025-05-17 09:31:00'),
('elder_001', 'dev_001', 'temperature', 36.5, '℃', 0, '2025-05-17 09:00:00', '2025-05-17 09:01:00'),
('elder_001', 'dev_001', 'blood_oxygen', 98, '%', 0, '2025-05-17 09:00:00', '2025-05-17 09:01:00'),
('elder_001', 'dev_001', 'insomnia', 0, '等级', 0, '2025-05-17 09:00:00', '2025-05-17 09:01:00'),
('elder_001', 'dev_001', 'sleep_time', 22.5, '小时', 0, '2025-05-17 09:00:00', '2025-05-17 09:01:00'),
('elder_002', 'dev_004', 'heart_rate', 68, 'bpm', 0, '2025-05-17 07:30:00', '2025-05-17 07:31:00'),
('elder_002', 'dev_004', 'heart_rate', 92, 'bpm', 1, '2025-05-17 09:20:00', '2025-05-17 09:21:00'),
('elder_002', 'dev_004', 'temperature', 36.8, '℃', 0, '2025-05-17 09:20:00', '2025-05-17 09:21:00'),
('elder_002', 'dev_004', 'blood_oxygen', 96, '%', 0, '2025-05-17 09:20:00', '2025-05-17 09:21:00'),
('elder_002', 'dev_004', 'insomnia', 1, '等级', 0, '2025-05-17 09:00:00', '2025-05-17 09:01:00'),
('elder_002', 'dev_004', 'sleep_time', 23.25, '小时', 0, '2025-05-17 09:00:00', '2025-05-17 09:01:00'),
('elder_003', 'dev_007', 'heart_rate', 70, 'bpm', 0, '2025-05-17 08:00:00', '2025-05-17 08:01:00'),
('elder_003', 'dev_007', 'temperature', 36.4, '℃', 0, '2025-05-17 08:00:00', '2025-05-17 08:01:00'),
('elder_003', 'dev_007', 'blood_oxygen', 97, '%', 0, '2025-05-17 08:00:00', '2025-05-17 08:01:00'),
('elder_003', 'dev_007', 'insomnia', 2, '等级', 0, '2025-05-17 08:00:00', '2025-05-17 08:01:00'),
('elder_003', 'dev_007', 'sleep_time', 21.75, '小时', 0, '2025-05-17 08:00:00', '2025-05-17 08:01:00'),
('elder_001', 'dev_001', 'temperature', 24.5, '℃', 0, '2025-05-17 10:00:00', '2025-05-17 10:01:00'),
('elder_001', 'dev_001', 'humidity', 55.0, '%', 0, '2025-05-17 10:00:00', '2025-05-17 10:01:00'),
('elder_001', 'dev_001', 'body_temperature', 36.2, '℃', 0, '2025-05-17 10:00:00', '2025-05-17 10:01:00'),
('elder_002', 'dev_004', 'temperature', 23.8, '℃', 0, '2025-05-17 10:00:00', '2025-05-17 10:01:00'),
('elder_002', 'dev_004', 'humidity', 60.0, '%', 0, '2025-05-17 10:00:00', '2025-05-17 10:01:00'),
('elder_003', 'dev_007', 'temperature', 25.0, '℃', 0, '2025-05-17 10:00:00', '2025-05-17 10:01:00'),
('elder_003', 'dev_007', 'humidity', 52.0, '%', 0, '2025-05-17 10:00:00', '2025-05-17 10:01:00'),
('elder_004', 'dev_010', 'heart_rate', 65, 'bpm', 0, '2025-05-17 08:00:00', '2025-05-17 08:01:00'),
('elder_004', 'dev_010', 'blood_oxygen', 99, '%', 0, '2025-05-17 08:00:00', '2025-05-17 08:01:00'),
('elder_004', 'dev_010', 'temperature', 23.5, '℃', 0, '2025-05-17 08:00:00', '2025-05-17 08:01:00'),
('elder_005', 'dev_012', 'heart_rate', 110, 'bpm', 1, '2025-05-16 23:00:00', '2025-05-16 23:01:00'),
('elder_005', 'dev_012', 'blood_oxygen', 94, '%', 1, '2025-05-16 23:00:00', '2025-05-16 23:01:00'),
('elder_005', 'dev_012', 'temperature', 22.0, '℃', 0, '2025-05-16 23:00:00', '2025-05-16 23:01:00');

-- ------------------------------------------------------------
-- 4. 血压记录表 (blood_pressure)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `blood_pressure`;
CREATE TABLE `blood_pressure` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `bp_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '血压记录业务ID',
  `elder_id` VARCHAR(50) NOT NULL COMMENT '老人ID',
  `systolic` INT NOT NULL COMMENT '收缩压',
  `diastolic` INT NOT NULL COMMENT '舒张压',
  `timestamp` DATETIME NOT NULL COMMENT '测量时间',
  `created_at` DATETIME NOT NULL COMMENT '入库时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='血压记录表';

INSERT INTO `blood_pressure` VALUES
(NULL, 'bp_001', 'elder_001', 135, 85, '2025-05-17 08:30:00', '2025-05-17 08:31:00'),
(NULL, 'bp_002', 'elder_001', 132, 84, '2025-05-16 08:30:00', '2025-05-16 08:31:00'),
(NULL, 'bp_003', 'elder_002', 145, 90, '2025-05-17 07:00:00', '2025-05-17 07:01:00'),
(NULL, 'bp_004', 'elder_002', 148, 92, '2025-05-16 07:00:00', '2025-05-16 07:01:00'),
(NULL, 'bp_005', 'elder_003', 128, 82, '2025-05-17 08:00:00', '2025-05-17 08:01:00'),
(NULL, 'bp_006', 'elder_003', 125, 80, '2025-05-16 08:00:00', '2025-05-16 08:01:00'),
(NULL, 'bp_007', 'elder_004', 138, 88, '2025-05-17 07:30:00', '2025-05-17 07:31:00'),
(NULL, 'bp_008', 'elder_005', 150, 95, '2025-05-16 22:00:00', '2025-05-16 22:01:00'),
(NULL, 'bp_009', 'elder_004', 140, 86, '2025-05-16 07:30:00', '2025-05-16 07:31:00');

-- ------------------------------------------------------------
-- 5. 报警事件表 (alarm_event)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `alarm_event`;
CREATE TABLE `alarm_event` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `alarm_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '报警业务ID',
  `elder_id` VARCHAR(50) NOT NULL COMMENT '老人ID',
  `device_id` VARCHAR(50) NOT NULL COMMENT '关联设备ID',
  `alarm_type` VARCHAR(50) NOT NULL COMMENT '报警类型',
  `alarm_level` VARCHAR(50) NOT NULL COMMENT '报警等级',
  `alarm_status` VARCHAR(50) NOT NULL COMMENT 'pending/handled',
  `description` VARCHAR(255) NOT NULL COMMENT '描述',
  `building` VARCHAR(50) NOT NULL COMMENT '楼栋',
  `room_number` VARCHAR(50) NOT NULL COMMENT '房间号',
  `unit` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '单元号',
  `snapshot_url` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '抓拍快照URL',
  `handler` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '处理人ID',
  `handler_name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '处理人姓名',
  `handle_remark` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '处理备注',
  `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
  `created_at` DATETIME NOT NULL COMMENT '发生时间',
  `resolved_at` DATETIME NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '解决时间(未处理用占位)',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警事件表';

INSERT INTO `alarm_event` (`alarm_id`, `elder_id`, `device_id`, `alarm_type`, `alarm_level`, `alarm_status`, `description`, `building`, `room_number`, `unit`, `snapshot_url`, `handler`, `handler_name`, `handle_remark`, `is_read`, `created_at`, `resolved_at`, `update_time`) VALUES
('alarm_001', 'elder_001', 'dev_001', 'heart_rate', 'high', 'pending', '心率持续偏高，超过阈值120bpm', '1号楼', '1-201', '1单元', '', '', '', '', 0, '2025-05-17 09:20:00', '1970-01-01 00:00:00', '2025-05-17 09:25:00'),
('alarm_002', 'elder_002', 'dev_004', 'fall', 'critical', 'pending', '检测到跌倒行为，请立即确认', '2号楼', '2-301', '2单元', '', '', '', '', 0, '2025-05-17 09:15:00', '1970-01-01 00:00:00', '2025-05-17 09:15:00'),
('alarm_003', 'elder_001', 'dev_002', 'intrusion', 'high', 'handled', '客厅检测到陌生人闯入', '1号楼', '1-201', '1单元', '/uploads/snapshot/alarm_003.jpg', 'staff_001', '张建国', '已确认是访客', 1, '2025-05-16 20:00:00', '2025-05-17 08:30:00', '2025-05-17 08:30:00'),
('alarm_004', 'elder_003', 'dev_009', 'inactive', 'low', 'handled', '床垫传感器离线超过12小时', '3号楼', '3-401', '3单元', '', 'staff_002', '李秀英', '已上门检查，设备恢复在线', 1, '2025-05-16 10:00:00', '2025-05-17 08:00:00', '2025-05-17 08:00:00'),
('alarm_005', 'elder_002', 'dev_006', 'smoke', 'critical', 'pending', '厨房烟感检测到烟雾浓度超标', '2号楼', '2-301', '2单元', '/uploads/snapshot/alarm_005.jpg', '', '', '', 0, '2025-05-17 08:00:00', '1970-01-01 00:00:00', '2025-05-17 08:05:00'),
('alarm_006', 'elder_005', 'dev_012', 'emergency-call', 'critical', 'pending', '手表紧急呼叫触发，心率异常110bpm', '5号楼', '5-502', '5单元', '', '', '', '', 0, '2025-05-16 23:00:00', '1970-01-01 00:00:00', '2025-05-16 23:01:00'),
('alarm_007', 'elder_004', 'dev_011', 'fall', 'high', 'pending', '卧室摄像头检测到疑似摔倒行为', '4号楼', '4-102', '4单元', '/uploads/snapshot/alarm_007.jpg', '', '', '', 0, '2025-05-17 07:00:00', '1970-01-01 00:00:00', '2025-05-17 07:05:00'),
('alarm_008', 'elder_001', 'dev_003', 'fingerprint-fail', 'medium', 'pending', '指纹连续识别失败3次，已抓拍陌生人', '1号楼', '1-201', '1单元', '/uploads/snapshot/alarm_008.jpg', '', '', '', 0, '2025-05-17 06:30:00', '1970-01-01 00:00:00', '2025-05-17 06:35:00');

-- ------------------------------------------------------------
-- 6. 本地智能体表 (local_agent)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `local_agent`;
CREATE TABLE `local_agent` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `agent_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '智能体业务ID',
  `agent_type` VARCHAR(50) NOT NULL COMMENT 'local_gateway',
  `status` VARCHAR(50) NOT NULL COMMENT 'online/offline',
  `last_heartbeat` DATETIME NOT NULL COMMENT '最后心跳时间',
  `ip` VARCHAR(50) NOT NULL COMMENT 'IP地址',
  `device_count` INT NOT NULL DEFAULT 0 COMMENT '设备总数',
  `connected_devices` INT NOT NULL DEFAULT 0 COMMENT '已连接设备数',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本地智能体表';

INSERT INTO `local_agent` VALUES
(NULL, 'LOCAL-1001', 'local_gateway', 'online', '2025-05-17 09:50:00', '192.168.1.101', 3, 3, '2025-01-10 09:00:00', '2025-05-17 09:50:00'),
(NULL, 'LOCAL-1002', 'local_gateway', 'online', '2025-05-17 09:48:00', '192.168.1.102', 3, 2, '2025-01-15 10:30:00', '2025-05-17 09:48:00'),
(NULL, 'LOCAL-1003', 'local_gateway', 'offline', '2025-05-15 22:00:00', '192.168.1.103', 2, 0, '2025-02-01 14:00:00', '2025-05-15 22:00:00'),
(NULL, 'LOCAL-1004', 'local_gateway', 'online', '2025-05-17 09:55:00', '192.168.1.104', 3, 3, '2025-03-01 09:00:00', '2025-05-17 09:55:00'),
(NULL, 'LOCAL-1005', 'local_gateway', 'online', '2025-05-17 09:40:00', '192.168.1.105', 2, 2, '2025-03-15 10:00:00', '2025-05-17 09:40:00');

-- ------------------------------------------------------------
-- 7. 云端智能体表 (cloud_agent)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `cloud_agent`;
CREATE TABLE `cloud_agent` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `agent_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '智能体业务ID',
  `agent_type` VARCHAR(50) NOT NULL COMMENT 'cloud_agent',
  `status` VARCHAR(50) NOT NULL COMMENT 'online/offline',
  `last_heartbeat` DATETIME NOT NULL COMMENT '最后心跳时间',
  `ip` VARCHAR(50) NOT NULL COMMENT 'IP地址',
  `device_count` INT NOT NULL DEFAULT 0 COMMENT '设备总数',
  `connected_devices` INT NOT NULL DEFAULT 0 COMMENT '已连接设备数',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='云端智能体表';

INSERT INTO `cloud_agent` VALUES
(NULL, 'CLOUD-01', 'cloud_agent', 'online', '2025-05-17 09:55:00', '10.0.0.1', 17, 14, '2025-01-05 08:00:00', '2025-05-17 09:55:00'),
(NULL, 'CLOUD-02', 'cloud_agent', 'online', '2025-05-17 09:50:00', '10.0.0.2', 0, 0, '2025-01-05 08:00:00', '2025-05-17 09:50:00'),
(NULL, 'CLOUD-03', 'cloud_agent', 'offline', '2025-05-16 18:00:00', '10.0.0.3', 5, 0, '2025-02-01 08:00:00', '2025-05-16 18:00:00');

-- ------------------------------------------------------------
-- 8. 音乐干预表 (music_intervention)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `music_intervention`;
CREATE TABLE `music_intervention` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `intervention_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '干预业务ID',
  `elder_id` VARCHAR(50) NOT NULL COMMENT '老人ID',
  `trigger_reason` VARCHAR(255) NOT NULL COMMENT '触发原因',
  `music_type` VARCHAR(50) NOT NULL COMMENT '干预类型/音乐类型',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `duration_minutes` INT NOT NULL DEFAULT 0 COMMENT '持续时长(分钟)',
  `before_state` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '干预前状态',
  `after_state` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '干预后状态',
  `result` VARCHAR(50) NOT NULL DEFAULT 'pending' COMMENT '执行结果/状态',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='音乐干预表';

INSERT INTO `music_intervention` VALUES
(NULL, 'int_001', 'elder_001', '情绪识别-焦虑', 'music', '2025-05-16 15:00:00', 15, '焦虑不安、心率加快', '平静放松、心率恢复正常', 'completed', '2025-05-16 15:00:00', '2025-05-16 15:15:00'),
(NULL, 'int_002', 'elder_002', '夜间唤醒', 'music', '2025-05-15 02:30:00', 10, '睡眠中断、辗转反侧', '重新入睡、呼吸平稳', 'completed', '2025-05-15 02:30:00', '2025-05-15 02:40:00'),
(NULL, 'int_003', 'elder_003', '久坐提醒', 'music', '2025-05-17 10:00:00', 5, '久坐不动超过2小时', '起身活动、舒展身体', 'completed', '2025-05-17 10:00:00', '2025-05-17 10:05:00'),
(NULL, 'int_004', 'elder_002', '心率异常告警联动', 'music', '2025-05-17 09:30:00', 0, '心率92bpm', '', 'pending', '2025-05-17 09:30:00', '2025-05-17 09:30:00'),
(NULL, 'int_005', 'elder_004', '情绪安抚', 'music', '2025-05-17 08:00:00', 20, '情绪低落、不愿交流', '心情好转、愿意配合', 'completed', '2025-05-17 08:00:00', '2025-05-17 08:20:00');

-- ------------------------------------------------------------
-- 9. 家居控制日志表 (home_control_log)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `home_control_log`;
CREATE TABLE `home_control_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `control_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '控制记录业务ID',
  `elder_id` VARCHAR(50) NOT NULL COMMENT '老人ID',
  `device_id` VARCHAR(50) NOT NULL COMMENT '设备ID',
  `command` VARCHAR(255) NOT NULL COMMENT '控制指令',
  `source_agent` VARCHAR(50) NOT NULL COMMENT '来源智能体',
  `result` VARCHAR(255) NOT NULL COMMENT '执行结果',
  `created_at` DATETIME NOT NULL COMMENT '执行时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家居控制日志表';

INSERT INTO `home_control_log` VALUES
(NULL, 'ctrl_001', 'elder_001', 'dev_002', 'alarmMute', 'LOCAL-1001', 'success', '2025-05-16 20:00:00'),
(NULL, 'ctrl_002', 'elder_002', 'dev_005', 'startRecord', 'CLOUD-01', 'success', '2025-05-15 09:00:00'),
(NULL, 'ctrl_003', 'elder_003', 'dev_008', 'enableMotionDetect', 'LOCAL-1003', 'success', '2025-05-17 08:30:00'),
(NULL, 'ctrl_004', 'elder_001', 'dev_015', 'turnOn', 'CLOUD-01', 'success', '2025-05-17 09:00:00'),
(NULL, 'ctrl_005', 'elder_002', 'dev_014', 'close', 'LOCAL-1002', 'success', '2025-05-17 08:00:00'),
(NULL, 'ctrl_006', 'elder_004', 'dev_016', 'beep', 'CLOUD-01', 'success', '2025-05-17 07:30:00');

-- ------------------------------------------------------------
-- 10. 智能体对话表 (agent_conversation)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `agent_conversation`;
CREATE TABLE `agent_conversation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `conversation_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '对话业务ID',
  `elder_id` VARCHAR(50) NOT NULL COMMENT '老人ID',
  `agent_type` VARCHAR(50) NOT NULL COMMENT 'local_agent/cloud_agent',
  `user_text` TEXT NOT NULL COMMENT '用户输入',
  `intent` VARCHAR(50) NOT NULL COMMENT '识别意图',
  `agent_reply` TEXT NOT NULL COMMENT '智能体回复',
  `risk_level` VARCHAR(50) NOT NULL DEFAULT 'low' COMMENT '风险等级',
  `created_at` DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能体对话表';

INSERT INTO `agent_conversation` VALUES
(NULL, 'conv_001', 'elder_001', 'local_agent', '今天血压怎么样？', 'query_health', '您今天血压为135/85，处于正常范围。记得按时服药哦。', 'low', '2025-05-17 08:30:00'),
(NULL, 'conv_002', 'elder_002', 'local_agent', '我有点不舒服，胸口闷。', 'report_discomfort', '已为您联系医护人员，请保持冷静，深呼吸。', 'high', '2025-05-17 09:10:00'),
(NULL, 'conv_003', 'elder_003', 'cloud_agent', '给我播放点轻音乐。', 'control_music', '正在为您播放轻音乐，音量已调至适中。', 'low', '2025-05-17 10:00:00'),
(NULL, 'conv_004', 'elder_001', 'cloud_agent', '帮我打开客厅的灯。', 'light-control', '好的，已为您打开客厅灯光。', 'low', '2025-05-17 09:30:00'),
(NULL, 'conv_005', 'elder_004', 'local_agent', '我的眼镜找不到了。', 'find-item', '已为您启动摄像头查找，请在摄像头可视范围内。', 'low', '2025-05-17 08:15:00'),
(NULL, 'conv_006', 'elder_005', 'cloud_agent', '救命！', 'emergency', '已收到紧急呼叫，正在通知工作人员和家属。', 'critical', '2025-05-16 23:00:00');

-- ------------------------------------------------------------
-- 11. 意图识别日志表 (agent_intent_log)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `agent_intent_log`;
CREATE TABLE `agent_intent_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `intent_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '意图记录业务ID',
  `elder_id` VARCHAR(50) NOT NULL COMMENT '老人ID',
  `source` VARCHAR(50) NOT NULL COMMENT '输入来源',
  `user_text` TEXT NOT NULL COMMENT '用户输入',
  `intent` VARCHAR(50) NOT NULL COMMENT '识别意图',
  `confidence` DOUBLE NOT NULL COMMENT '置信度',
  `handled_by` VARCHAR(50) NOT NULL COMMENT '处理智能体',
  `created_at` DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='意图识别日志表';

INSERT INTO `agent_intent_log` (`intent_id`, `elder_id`, `source`, `user_text`, `intent`, `confidence`, `handled_by`, `created_at`) VALUES
('intent_001', 'elder_001', 'voice', '今天血压怎么样？', 'query_health', 0.95, 'local_agent', '2025-05-17 08:30:00'),
('intent_002', 'elder_002', 'voice', '我有点不舒服，胸口闷。', 'report_discomfort', 0.88, 'local_agent', '2025-05-17 09:10:00'),
('intent_003', 'elder_003', 'app', '给我播放点轻音乐。', 'control_music', 0.92, 'cloud_agent', '2025-05-17 10:00:00'),
('intent_004', 'elder_001', 'voice', '帮我打开客厅的灯。', 'light-control', 0.96, 'cloud_agent', '2025-05-17 09:30:00'),
('intent_005', 'elder_004', 'voice', '我的眼镜找不到了。', 'find-item', 0.85, 'local_agent', '2025-05-17 08:15:00'),
('intent_006', 'elder_005', 'voice', '救命！', 'emergency', 0.98, 'cloud_agent', '2025-05-16 23:00:00'),
('intent_007', 'elder_001', 'app', '今天天气怎么样？', 'chat', 0.78, 'cloud_agent', '2025-05-17 07:00:00'),
('intent_008', 'elder_002', 'voice', '把窗帘打开。', 'curtain-control', 0.91, 'local_agent', '2025-05-17 08:00:00');

-- ============================================================
-- 以下为新增表，对应后端实体类
-- ============================================================

-- ------------------------------------------------------------
-- 12. 员工表 (staff_user)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `staff_user`;
CREATE TABLE `staff_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `staff_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '员工业务ID',
  `username` VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
  `name` VARCHAR(100) NOT NULL COMMENT '姓名',
  `phone` VARCHAR(50) NOT NULL UNIQUE COMMENT '手机号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `role` VARCHAR(50) COMMENT '角色(admin/supervisor/staff)',
  `community_id` VARCHAR(50) COMMENT '所属社区ID',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

INSERT INTO `staff_user` (`staff_id`, `username`, `name`, `phone`, `password`, `role`, `community_id`, `avatar`, `created_at`, `update_time`) VALUES
('staff_001', 'zhangjianguo', '张建国', '13800138001', '123456', 'supervisor', 'community_001', '/uploads/avatar/staff_001.jpg', '2025-01-05 08:00:00', '2025-05-17 10:00:00'),
('staff_002', 'lixiuying', '李秀英', '13800138002', '123456', 'staff', 'community_001', '/uploads/avatar/staff_002.jpg', '2025-01-08 09:00:00', '2025-05-17 09:30:00'),
('staff_003', 'wangqiang', '王强', '13800138003', '123456', 'staff', 'community_001', '/uploads/avatar/staff_003.jpg', '2025-01-10 10:00:00', '2025-05-17 08:00:00'),
('staff_004', 'admin', '系统管理员', '13800138000', '123456', 'admin', 'community_001', '/uploads/avatar/admin.jpg', '2025-01-01 08:00:00', '2025-05-17 10:00:00');

-- ------------------------------------------------------------
-- 13. 家属表 (family_user)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `family_user`;
CREATE TABLE `family_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `family_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '家属业务ID',
  `name` VARCHAR(100) NOT NULL COMMENT '姓名',
  `phone` VARCHAR(50) NOT NULL UNIQUE COMMENT '手机号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `elder_id` VARCHAR(50) COMMENT '关联老人ID',
  `relation` VARCHAR(50) COMMENT '与老人关系',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家属表';

INSERT INTO `family_user` (`family_id`, `name`, `phone`, `password`, `elder_id`, `relation`, `avatar`, `created_at`, `update_time`) VALUES
('family_001', '张小明', '13800138011', '123456', 'elder_001', '儿子', '/uploads/avatar/family_001.jpg', '2025-01-10 09:00:00', '2025-05-17 10:00:00'),
('family_002', '李小红', '13900139012', '123456', 'elder_002', '女儿', '/uploads/avatar/family_002.jpg', '2025-01-15 10:30:00', '2025-05-17 10:00:00'),
('family_003', '王小刚', '13700137013', '123456', 'elder_003', '侄子', '/uploads/avatar/family_003.jpg', '2025-02-01 14:00:00', '2025-05-17 10:00:00'),
('family_004', '赵小花', '13600136014', '123456', 'elder_004', '女儿', '/uploads/avatar/family_004.jpg', '2025-03-01 09:00:00', '2025-05-17 10:00:00'),
('family_005', '孙小强', '13500135015', '123456', 'elder_005', '孙子', '/uploads/avatar/family_005.jpg', '2025-03-15 10:00:00', '2025-05-17 10:00:00');

-- ------------------------------------------------------------
-- 14. 紧急联系人表 (emergency_contact)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `emergency_contact`;
CREATE TABLE `emergency_contact` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `contact_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '联系人业务ID',
  `elder_id` VARCHAR(50) NOT NULL COMMENT '老人ID',
  `name` VARCHAR(100) NOT NULL COMMENT '姓名',
  `phone` VARCHAR(50) NOT NULL COMMENT '手机号',
  `relation` VARCHAR(50) COMMENT '关系',
  `is_primary` TINYINT(1) DEFAULT 0 COMMENT '是否主要联系人',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='紧急联系人表';

INSERT INTO `emergency_contact` (`contact_id`, `elder_id`, `name`, `phone`, `relation`, `is_primary`, `sort_order`, `created_at`, `update_time`) VALUES
('contact_001', 'elder_001', '张小明', '13800138011', '儿子', 1, 0, '2025-01-10 09:00:00', '2025-05-17 10:00:00'),
('contact_002', 'elder_001', '张小红', '13800138012', '女儿', 0, 1, '2025-01-10 09:00:00', '2025-05-17 10:00:00'),
('contact_003', 'elder_002', '李小红', '13900139012', '女儿', 1, 0, '2025-01-15 10:30:00', '2025-05-17 10:00:00'),
('contact_004', 'elder_002', '李小刚', '13900139013', '儿子', 0, 1, '2025-01-15 10:30:00', '2025-05-17 10:00:00'),
('contact_005', 'elder_003', '王小刚', '13700137013', '侄子', 1, 0, '2025-02-01 14:00:00', '2025-05-17 10:00:00'),
('contact_006', 'elder_004', '赵小花', '13600136014', '女儿', 1, 0, '2025-03-01 09:00:00', '2025-05-17 10:00:00'),
('contact_007', 'elder_005', '孙小强', '13500135015', '孙子', 1, 0, '2025-03-15 10:00:00', '2025-05-17 10:00:00');

-- ------------------------------------------------------------
-- 15. 健康记录表 (health_record)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `health_record`;
CREATE TABLE `health_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `record_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '记录业务ID',
  `elder_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '老人ID',
  `hospitalization_info` TEXT COMMENT '住院信息',
  `medical_history` TEXT COMMENT '既往病史',
  `allergy_history` TEXT COMMENT '过敏史',
  `common_medications` TEXT COMMENT '常用药物',
  `blood_type` VARCHAR(10) COMMENT '血型',
  `remarks` TEXT COMMENT '备注',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康记录表';

INSERT INTO `health_record` (`record_id`, `elder_id`, `hospitalization_info`, `medical_history`, `allergy_history`, `common_medications`, `blood_type`, `remarks`, `created_at`, `update_time`) VALUES
('hr_001', 'elder_001', '2024年3月因高血压住院7天', '高血压10年,糖尿病5年', '对青霉素过敏', '降压药,降糖药,阿司匹林', 'A', '需定期监测血糖血压', '2025-01-10 09:00:00', '2025-05-17 10:00:00'),
('hr_002', 'elder_002', '2024年8月因心脏病住院15天', '心脏病3年,骨质疏松', '无', '硝酸甘油,钙片', 'B', '需注意跌倒风险', '2025-01-15 10:30:00', '2025-05-17 10:00:00'),
('hr_003', 'elder_003', '无住院记录', '轻度认知障碍', '对花粉过敏', '银杏叶片', 'O', '记忆力减退需关注', '2025-02-01 14:00:00', '2025-05-17 10:00:00'),
('hr_004', 'elder_004', '2025年1月因糖尿病足住院5天', '糖尿病8年,关节炎', '无', '胰岛素,止痛药', 'AB', '行动不便需辅助', '2025-03-01 09:00:00', '2025-05-17 10:00:00'),
('hr_005', 'elder_005', '2024年12月因脑梗住院30天', '脑梗后遗症,高血压', '对磺胺类药物过敏', '降压药,抗凝药', 'A', '需定期康复训练', '2025-03-15 10:00:00', '2025-05-17 10:00:00');

-- ------------------------------------------------------------
-- 16. SOS记录表 (sos_record)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sos_record`;
CREATE TABLE `sos_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `sos_id` VARCHAR(50) NOT NULL UNIQUE COMMENT 'SOS业务ID',
  `elder_id` VARCHAR(50) NOT NULL COMMENT '老人ID',
  `trigger_time` DATETIME COMMENT '触发时间',
  `status` VARCHAR(50) COMMENT '状态',
  `location` VARCHAR(255) COMMENT '位置',
  `handler_id` VARCHAR(50) COMMENT '处理人ID',
  `handled_time` DATETIME COMMENT '处理时间',
  `created_at` DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SOS记录表';

INSERT INTO `sos_record` (`sos_id`, `elder_id`, `trigger_time`, `status`, `location`, `handler_id`, `handled_time`, `created_at`) VALUES
('sos_001', 'elder_002', '2025-05-17 09:15:00', 'handled', '2号楼2-301客厅', 'staff_001', '2025-05-17 09:20:00', '2025-05-17 09:15:00'),
('sos_002', 'elder_005', '2025-05-16 23:00:00', 'pending', '5号楼5-502卧室', '', '1970-01-01 00:00:00', '2025-05-16 23:00:00'),
('sos_003', 'elder_001', '2025-05-16 14:00:00', 'handled', '1号楼1-201厨房', 'staff_002', '2025-05-16 14:10:00', '2025-05-16 14:00:00'),
('sos_004', 'elder_004', '2025-05-17 06:00:00', 'handled', '4号楼4-102卫生间', 'staff_003', '2025-05-17 06:15:00', '2025-05-17 06:00:00'),
('sos_005', 'elder_003', '2025-05-15 20:00:00', 'handled', '3号楼3-401客厅', 'staff_002', '2025-05-15 20:05:00', '2025-05-15 20:00:00');

-- ------------------------------------------------------------
-- 17. 工单表 (work_order)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `work_order`;
CREATE TABLE `work_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `order_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '工单业务ID',
  `elder_id` VARCHAR(50) NOT NULL COMMENT '老人ID',
  `order_type` VARCHAR(50) COMMENT '工单类型',
  `description` VARCHAR(255) COMMENT '描述',
  `status` VARCHAR(50) COMMENT '状态',
  `creator_id` VARCHAR(50) COMMENT '创建人ID',
  `handler_id` VARCHAR(50) COMMENT '处理人ID',
  `handler_name` VARCHAR(100) COMMENT '处理人姓名',
  `handler_phone` VARCHAR(50) COMMENT '处理人电话',
  `complete_time` DATETIME COMMENT '完成时间',
  `service_request_id` VARCHAR(50) COMMENT '关联服务请求ID',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';

INSERT INTO `work_order` (`order_id`, `elder_id`, `order_type`, `description`, `status`, `creator_id`, `handler_id`, `handler_name`, `handler_phone`, `complete_time`, `service_request_id`, `created_at`, `update_time`) VALUES
('wo_001', 'elder_001', '设备维修', '客厅摄像头画面模糊，需要检修', 'completed', 'staff_001', 'staff_003', '王强', '13800138003', '2025-05-16 16:00:00', 'sr_001', '2025-05-16 09:00:00', '2025-05-16 16:00:00'),
('wo_002', 'elder_002', '紧急处理', '老人摔倒，需上门检查', 'completed', 'staff_001', 'staff_001', '张建国', '13800138001', '2025-05-17 09:30:00', 'sr_002', '2025-05-17 09:15:00', '2025-05-17 09:30:00'),
('wo_003', 'elder_003', '设备巡检', '床垫传感器离线，需上门恢复', 'completed', 'staff_002', 'staff_002', '李秀英', '13800138002', '2025-05-17 08:00:00', 'sr_003', '2025-05-16 10:00:00', '2025-05-17 08:00:00'),
('wo_004', 'elder_005', '紧急处理', '手表紧急呼叫未处理，需上门查看', 'pending', 'staff_001', '', '', '', '1970-01-01 00:00:00', 'sr_004', '2025-05-16 23:00:00', '2025-05-16 23:00:00'),
('wo_005', 'elder_004', '日常关怀', '老人情绪低落，需心理疏导', 'in_progress', 'staff_003', 'staff_003', '王强', '13800138003', '1970-01-01 00:00:00', 'sr_005', '2025-05-17 08:00:00', '2025-05-17 08:00:00');

-- ------------------------------------------------------------
-- 18. 服务请求表 (service_request)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `service_request`;
CREATE TABLE `service_request` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `request_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '请求业务ID',
  `family_id` VARCHAR(50) NOT NULL COMMENT '家属ID',
  `elder_id` VARCHAR(50) NOT NULL COMMENT '老人ID',
  `request_type` VARCHAR(50) COMMENT '请求类型',
  `content` TEXT COMMENT '请求内容',
  `status` VARCHAR(50) COMMENT '状态',
  `related_order_id` VARCHAR(50) COMMENT '关联工单ID',
  `reject_reason` VARCHAR(255) COMMENT '拒绝原因',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务请求表';

INSERT INTO `service_request` (`request_id`, `family_id`, `elder_id`, `request_type`, `content`, `status`, `related_order_id`, `reject_reason`, `created_at`, `update_time`) VALUES
('sr_001', 'family_001', 'elder_001', '设备维修', '请求安排人员上门检查客厅摄像头', 'completed', 'wo_001', '', '2025-05-16 09:00:00', '2025-05-16 16:00:00'),
('sr_002', 'family_002', 'elder_002', '紧急协助', '收到摔倒告警，请求工作人员上门确认', 'completed', 'wo_002', '', '2025-05-17 09:10:00', '2025-05-17 09:30:00'),
('sr_003', 'family_003', 'elder_003', '设备维修', '床垫传感器离线，请求恢复', 'completed', 'wo_003', '', '2025-05-16 10:00:00', '2025-05-17 08:00:00'),
('sr_004', 'family_005', 'elder_005', '紧急协助', '收到紧急呼叫，请求立即上门', 'pending', 'wo_004', '', '2025-05-16 23:00:00', '2025-05-16 23:00:00'),
('sr_005', 'family_004', 'elder_004', '日常关怀', '老人今天情绪低落，请求工作人员关怀', 'in_progress', 'wo_005', '', '2025-05-17 08:00:00', '2025-05-17 08:00:00');

-- ------------------------------------------------------------
-- 19. 监控申请/授权表 (monitor_request)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `monitor_request`;
CREATE TABLE `monitor_request` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `request_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '申请业务ID',
  `elder_id` VARCHAR(50) NOT NULL COMMENT '老人ID',
  `staff_id` VARCHAR(50) NOT NULL COMMENT '申请员工ID',
  `staff_name` VARCHAR(100) COMMENT '员工姓名',
  `staff_phone` VARCHAR(50) COMMENT '员工电话',
  `reason` VARCHAR(255) COMMENT '申请原因',
  `status` VARCHAR(50) COMMENT '状态',
  `approved_at` BIGINT COMMENT '审批时间戳',
  `expired_at` DATETIME COMMENT '过期时间',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监控申请/授权表';

INSERT INTO `monitor_request` (`request_id`, `elder_id`, `staff_id`, `staff_name`, `staff_phone`, `reason`, `status`, `approved_at`, `expired_at`, `created_at`, `update_time`) VALUES
('mr_001', 'elder_001', 'staff_001', '张建国', '13800138001', '日常巡查需要查看老人状态', 'approved', 1715904000000, '2025-06-01 00:00:00', '2025-05-10 09:00:00', '2025-05-10 09:30:00'),
('mr_002', 'elder_002', 'staff_002', '李秀英', '13800138002', '老人摔倒后需持续观察', 'approved', 1715904000000, '2025-06-15 00:00:00', '2025-05-15 10:00:00', '2025-05-15 10:30:00'),
('mr_003', 'elder_003', 'staff_003', '王强', '13800138003', '设备维护期间需查看监控', 'pending', 0, '1970-01-01 00:00:00', '2025-05-17 08:00:00', '2025-05-17 08:00:00'),
('mr_004', 'elder_004', 'staff_001', '张建国', '13800138001', '新入住老人需要了解情况', 'approved', 1715904000000, '2025-05-31 00:00:00', '2025-05-01 09:00:00', '2025-05-01 09:30:00'),
('mr_005', 'elder_005', 'staff_002', '李秀英', '13800138002', '紧急呼叫后需查看现场', 'approved', 1715904000000, '2025-05-18 00:00:00', '2025-05-16 23:00:00', '2025-05-16 23:05:00');

-- ------------------------------------------------------------
-- 20. 通知表 (notification)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `notification_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '通知业务ID',
  `user_id` VARCHAR(50) NOT NULL COMMENT '接收用户ID',
  `user_type` VARCHAR(50) COMMENT '用户类型(staff/family/elder)',
  `notification_type` VARCHAR(50) COMMENT '通知类型',
  `title` VARCHAR(255) COMMENT '标题',
  `content` VARCHAR(255) COMMENT '内容',
  `is_read` TINYINT(1) DEFAULT 0 COMMENT '是否已读',
  `building` VARCHAR(50) COMMENT '楼栋',
  `room` VARCHAR(50) COMMENT '房间号',
  `order_id` VARCHAR(50) COMMENT '关联工单ID',
  `request_id` VARCHAR(50) COMMENT '关联请求ID',
  `elder_id` VARCHAR(50) COMMENT '关联老人ID',
  `related_id` VARCHAR(50) COMMENT '关联ID',
  `created_at` DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

INSERT INTO `notification` (`notification_id`, `user_id`, `user_type`, `notification_type`, `title`, `content`, `is_read`, `building`, `room`, `order_id`, `request_id`, `elder_id`, `related_id`, `created_at`) VALUES
('notif_001', 'staff_001', 'staff', 'alarm', '新告警通知', '1号楼1-201张三心率持续偏高', 0, '1号楼', '1-201', '', '', 'elder_001', 'alarm_001', '2025-05-17 09:20:00'),
('notif_002', 'staff_001', 'staff', 'alarm', '紧急告警', '2号楼2-301李四检测到跌倒', 0, '2号楼', '2-301', '', '', 'elder_002', 'alarm_002', '2025-05-17 09:15:00'),
('notif_003', 'family_001', 'family', 'alarm', '告警通知', '您的家属张三家中检测到陌生人闯入', 1, '1号楼', '1-201', '', '', 'elder_001', 'alarm_003', '2025-05-16 20:00:00'),
('notif_004', 'staff_002', 'staff', 'device', '设备离线', '3号楼3-401床垫传感器离线', 1, '3号楼', '3-401', 'wo_003', '', 'elder_003', 'dev_009', '2025-05-16 10:00:00'),
('notif_005', 'family_005', 'family', 'emergency', '紧急呼叫', '您的家属孙七触发紧急呼叫', 0, '5号楼', '5-502', '', '', 'elder_005', 'sos_002', '2025-05-16 23:00:00'),
('notif_006', 'staff_003', 'staff', 'alarm', '摔倒检测', '4号楼4-102赵六卧室疑似摔倒', 0, '4号楼', '4-102', '', '', 'elder_004', 'alarm_007', '2025-05-17 07:00:00'),
('notif_007', 'family_002', 'family', 'service', '服务请求处理', '您的服务请求已处理完成', 1, '2号楼', '2-301', 'wo_002', 'sr_002', 'elder_002', '', '2025-05-17 09:30:00'),
('notif_008', 'staff_001', 'staff', 'monitor', '监控授权申请', '新入住老人赵六监控查看申请', 1, '4号楼', '4-102', '', '', 'elder_004', 'mr_004', '2025-05-01 09:00:00');

-- ------------------------------------------------------------
-- 21. AI建议表 (ai_advice)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `ai_advice`;
CREATE TABLE `ai_advice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `advice_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '建议业务ID',
  `elder_id` VARCHAR(50) NOT NULL COMMENT '老人ID',
  `advice_type` VARCHAR(50) COMMENT '建议类型',
  `input_summary` TEXT COMMENT '输入摘要',
  `advice_content` TEXT COMMENT '建议内容',
  `created_at` DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI建议表';

INSERT INTO `ai_advice` (`advice_id`, `elder_id`, `advice_type`, `input_summary`, `advice_content`, `created_at`) VALUES
('adv_001', 'elder_001', 'health', '近期血压偏高，血糖波动较大', '建议增加血压监测频率，每日早晚各一次；调整饮食结构，减少盐分摄入；适当增加户外散步时间。', '2025-05-17 08:30:00'),
('adv_002', 'elder_002', 'fall_prevention', '近期有跌倒记录，骨质疏松', '建议在卫生间和卧室安装防滑垫；夜间保持走廊照明；穿戴防滑鞋；定期进行骨密度检查。', '2025-05-17 09:30:00'),
('adv_003', 'elder_003', 'cognitive', '记忆力减退，轻度认知障碍', '建议进行认知训练，如记忆卡片游戏；保持社交活动；规律作息；定期复查认知功能。', '2025-05-17 10:00:00'),
('adv_004', 'elder_004', 'diabetes', '糖尿病足恢复期，血糖控制', '建议每日检查足部皮肤状况；保持足部清洁干燥；穿宽松舒适的鞋子；定期测量血糖。', '2025-05-17 08:00:00'),
('adv_005', 'elder_005', 'rehabilitation', '脑梗后遗症，行动不便', '建议坚持每日康复训练；家属协助进行肢体活动；定期复查脑部影像；注意血压控制。', '2025-05-17 09:00:00');

-- ------------------------------------------------------------
-- 22. 告警处理表 (alarm_process)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `alarm_process`;
CREATE TABLE `alarm_process` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `process_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '处理记录业务ID',
  `alarm_id` VARCHAR(50) NOT NULL COMMENT '告警ID',
  `handler_id` VARCHAR(50) COMMENT '处理人ID',
  `handler_type` VARCHAR(50) COMMENT '处理人类型',
  `action` VARCHAR(255) COMMENT '处理动作',
  `result` VARCHAR(255) COMMENT '处理结果',
  `remark` VARCHAR(255) COMMENT '备注',
  `process_time` DATETIME COMMENT '处理时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警处理表';

INSERT INTO `alarm_process` (`process_id`, `alarm_id`, `handler_id`, `handler_type`, `action`, `result`, `remark`, `process_time`) VALUES
('proc_001', 'alarm_003', 'staff_001', 'staff', '上门核实', 'confirmed', '已确认是访客，非陌生人闯入', '2025-05-17 08:30:00'),
('proc_002', 'alarm_004', 'staff_002', 'staff', '上门检修', 'resolved', '已更换床垫传感器电池，设备恢复在线', '2025-05-17 08:00:00'),
('proc_003', 'alarm_002', 'staff_001', 'staff', '紧急上门', 'in_progress', '正在赶往现场，老人意识清醒', '2025-05-17 09:20:00'),
('proc_004', 'alarm_001', 'staff_003', 'staff', '电话确认', 'pending', '已电话联系老人，老人表示无不适，继续观察', '2025-05-17 09:25:00'),
('proc_005', 'alarm_005', 'staff_002', 'staff', '紧急处理', 'pending', '已赶往现场，烟感已停止报警，正在排查原因', '2025-05-17 08:05:00');

SET FOREIGN_KEY_CHECKS = 1;