-- ============================================================
-- 健康数据看板 - 数据日期迁移脚本
-- 版本: 2026-08-08
-- 说明: 将 ai_service_record 和 sleep_record 中的旧数据日期
--       迁移到近7天（2026-08-01 ~ 2026-08-08）范围。
--       适用于已有旧种子数据但不想重新生成的情况。
-- 用法: 直接执行此脚本即可
-- ============================================================

-- 计算需要偏移的天数（以 2026-08-08 为今天，原数据最新日期约 2026-07-21，偏移约18天）
-- 如需调整偏移量，修改下方 INTERVAL 值
SET @shift_days = 18;

-- 1. 迁移 ai_service_record 数据日期
UPDATE ai_service_record
SET interaction_time = DATE_ADD(interaction_time, INTERVAL @shift_days DAY),
    created_at = DATE_ADD(created_at, INTERVAL @shift_days DAY)
WHERE interaction_time < '2026-08-01';

-- 2. 迁移 sleep_record 数据日期
UPDATE sleep_record
SET recorded_at = DATE_ADD(recorded_at, INTERVAL @shift_days DAY),
    created_at = DATE_ADD(created_at, INTERVAL @shift_days DAY)
WHERE recorded_at < '2026-08-01';

-- 验证迁移结果
SELECT 'ai_service_record 最新10条' AS info;
SELECT record_id, service_type, interaction_time FROM ai_service_record ORDER BY interaction_time DESC LIMIT 10;

SELECT 'sleep_record 最新7条' AS info;
SELECT elder_id, total_sleep_hours, recorded_at FROM sleep_record ORDER BY recorded_at DESC LIMIT 7;
