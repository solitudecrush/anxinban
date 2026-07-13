-- ============================================================
-- 监控申请表 (camera_request) 添加 can_view 字段
-- 1 = 允许查看监控，0 = 不允许查看监控
-- 日期: 2026-07-13
-- ============================================================

ALTER TABLE `camera_request`
    ADD COLUMN `can_view` INT DEFAULT 0 COMMENT '是否允许查看监控：1=允许，0=不允许' AFTER `status`;

-- 将历史已批准的申请设置为允许查看
UPDATE `camera_request` SET `can_view` = 1 WHERE `status` = 'approved';
