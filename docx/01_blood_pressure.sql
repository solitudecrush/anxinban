-- ============================================================
-- 安心伴智慧养老守护系统 - 血压记录表 (blood_pressure)
-- 版本: 2026-07-06
-- 适用: MySQL 8.0+
-- 说明: 存储老人血压测量记录（收缩压/舒张压）
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 血压记录表 (blood_pressure)
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

SET FOREIGN_KEY_CHECKS = 1;
