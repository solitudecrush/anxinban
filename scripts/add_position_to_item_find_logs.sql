-- ============================================================
-- 迁移：为 item_find_logs 表添加 position 字段
-- 日期：2026-07-22
-- 说明：物品寻找记录新增"位置"字段，前端展示时需同步显示。
-- ============================================================

ALTER TABLE item_find_logs
    ADD COLUMN position VARCHAR(100) DEFAULT NULL COMMENT '物品所在位置'
    AFTER found;
