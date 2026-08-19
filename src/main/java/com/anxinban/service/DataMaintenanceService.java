package com.anxinban.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * 数据自动维护服务 — 保持测试数据的时间不过期、高频表不爆量。
 *
 * <h3>功能</h3>
 * <ul>
 *   <li><b>时间推移</b>：超过 30 天的记录自动推移到 7 天前（保持时/分/秒）</li>
 *   <li><b>每小时上限</b>：高频表每小时最多保留 30 条，超出删最早</li>
 * </ul>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class DataMaintenanceService {

    private static final Logger log = LoggerFactory.getLogger(DataMaintenanceService.class);

    private final JdbcTemplate jdbc;

    public DataMaintenanceService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ==================== 功能 1：时间推移 ====================

    /**
     * 表时间列配置 — 定义每张表需要推移的时间列。
     */
    private static class TableTimeConfig {
        final String table;
        final String primaryCol;   // 主业务时间列（用于判断"超 30 天"）
        final List<String> extraCols; // 需要同步推移的附加列

        TableTimeConfig(String table, String primaryCol, String... extraCols) {
            this.table = table;
            this.primaryCol = primaryCol;
            this.extraCols = List.of(extraCols);
        }

        /** 所有需要 UPDATE 的列 */
        List<String> allCols() {
            List<String> all = new ArrayList<>();
            all.add(primaryCol);
            all.addAll(extraCols);
            return all;
        }
    }

    /**
     * 需要时间推移的表配置。
     */
    private static final List<TableTimeConfig> TIME_SHIFT_TABLES = List.of(
        // 传感器 / 体征高频表
        new TableTimeConfig("sensor_data",          "timestamp",   "created_at"),
        new TableTimeConfig("body_temperature",     "timestamp",   "created_at"),
        new TableTimeConfig("heart_rate",           "timestamp",   "created_at"),
        new TableTimeConfig("blood_oxygen",         "timestamp",   "created_at"),
        new TableTimeConfig("blood_pressure",       "timestamp",   "created_at"),
        new TableTimeConfig("health_vital_record",  "measured_at", "created_at"),
        // 边缘接入
        new TableTimeConfig("edge_ingest_record",   "received_at", "created_at"),
        // 告警
        new TableTimeConfig("alarm_event",          "occur_time",  "created_at", "resolved_at", "update_time"),
        new TableTimeConfig("alert",                "occur_time",  "created_at", "resolved_at", "update_time", "handle_time", "updated_at"),
        // SOS
        new TableTimeConfig("sos_record",           "trigger_time","created_at", "handled_time"),
        // 伴聊
        new TableTimeConfig("companion_record",     "interaction_time", "created_at"),
        // AI 服务
        new TableTimeConfig("ai_service_record",    "interaction_time", "created_at"),
        // 睡眠
        new TableTimeConfig("sleep_record",         "recorded_at", "created_at"),
        // 摄像头
        new TableTimeConfig("camera_view_record",   "view_time",   "created_at"),
        // VLM
        new TableTimeConfig("vlm_record",           "query_time",  "created_at"),
        // 通知
        new TableTimeConfig("notification",         "notify_time", "created_at"),
        new TableTimeConfig("app_notification",     "notify_time", "created_at"),
        // 文件元数据
        new TableTimeConfig("file_metadata",        "snapshot_time", "created_at"),
        // 音乐干预
        new TableTimeConfig("music_intervention",   "start_time",  "created_at"),
        // 智体对话
        new TableTimeConfig("agent_conversation",   "created_at"),
        new TableTimeConfig("agent_intent_log",     "created_at"),
        // AI 记录
        new TableTimeConfig("ai_advice",            "created_at"),
        new TableTimeConfig("ai_analysis_record",   "created_at"),
        // 服务请求 / 工单 / 监控申请
        new TableTimeConfig("service_request",      "created_at",  "update_time"),
        new TableTimeConfig("work_order",           "created_at",  "update_time", "complete_time", "finish_time", "updated_at"),
        new TableTimeConfig("camera_request",       "created_at",  "updated_at", "request_time", "approved_at", "expired_at"),
        new TableTimeConfig("family_request",       "created_at",  "updated_at", "request_time"),
        new TableTimeConfig("monitor_request",      "created_at",  "update_time", "approved_at", "expired_at"),
        // 家居控制日志
        new TableTimeConfig("home_control_log",     "created_at"),
        // 告警处理
        new TableTimeConfig("alarm_process",        "process_time"),
        // 健康档案
        new TableTimeConfig("health_record",        "created_at",  "updated_at", "update_time"),
        // 语音提示
        new TableTimeConfig("voice_prompt",         "created_at",  "updated_at", "prompt_time")
    );

    /**
     * 每 5 分钟执行一次时间推移。
     * 将超过 30 天的记录推移到 7 天前，保持时/分/秒不变。
     */
    @Scheduled(fixedRate = 300_000)
    public void shiftOldData() {
        int totalShifted = 0;
        for (TableTimeConfig cfg : TIME_SHIFT_TABLES) {
            try {
                int shifted = shiftTable(cfg);
                if (shifted > 0) {
                    totalShifted += shifted;
                    log.info("[数据维护] {} 推移 {} 条记录", cfg.table, shifted);
                }
            } catch (Exception e) {
                log.warn("[数据维护] {} 推移失败: {}", cfg.table, e.getMessage());
            }
        }
        if (totalShifted > 0) {
            log.info("[数据维护] 时间推移完成，共处理 {} 条", totalShifted);
        }
    }

    private int shiftTable(TableTimeConfig cfg) {
        // 检查表是否存在
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?",
            Integer.class, cfg.table);
        if (count == null || count == 0) return 0;

        // 检查主列是否存在
        count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
            Integer.class, cfg.table, cfg.primaryCol);
        if (count == null || count == 0) return 0;

        int totalAffected = 0;
        for (String col : cfg.allCols()) {
            // 检查列是否存在且为 datetime/timestamp 类型（跳过 bigint 等非时间类型）
            Integer colExists = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ? " +
                "AND DATA_TYPE IN ('datetime','timestamp')",
                Integer.class, cfg.table, col);
            if (colExists == null || colExists == 0) continue;

            String sql = String.format(
                "UPDATE %s SET %s = TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 7 DAY), TIME(%s)) WHERE %s < DATE_SUB(NOW(), INTERVAL 30 DAY)",
                cfg.table, col, col, col);
            int affected = jdbc.update(sql);
            totalAffected += affected;
        }
        return totalAffected;
    }

    // ==================== 功能 2：每小时上限 ====================

    /**
     * 需要限制每小时数据量的高频表。
     */
    private static final List<HighFreqTable> HIGH_FREQ_TABLES = List.of(
        new HighFreqTable("sensor_data",         "timestamp"),
        new HighFreqTable("body_temperature",    "timestamp"),
        new HighFreqTable("heart_rate",          "timestamp"),
        new HighFreqTable("blood_oxygen",        "timestamp"),
        new HighFreqTable("blood_pressure",      "timestamp"),
        new HighFreqTable("health_vital_record", "measured_at"),
        new HighFreqTable("edge_ingest_record",  "received_at")
    );

    private static class HighFreqTable {
        final String table;
        final String timeCol;

        HighFreqTable(String table, String timeCol) {
            this.table = table;
            this.timeCol = timeCol;
        }
    }

    /** 每小时最多保留条数（调大以容纳昼夜节律曲线数据 + 设备上报） */
    private static final int MAX_PER_HOUR = 30;

    /**
     * 每 1 分钟执行一次每小时上限检查。
     * 删除各高频表中每个小时超出 30 条的最旧记录。
     */
    @Scheduled(fixedRate = 60_000)
    public void trimPerHourData() {
        int totalDeleted = 0;
        for (HighFreqTable ht : HIGH_FREQ_TABLES) {
            try {
                int deleted = trimTable(ht);
                if (deleted > 0) {
                    totalDeleted += deleted;
                    log.info("[数据维护] {} 每小时上限删除 {} 条", ht.table, deleted);
                }
            } catch (Exception e) {
                log.warn("[数据维护] {} 上限检查失败: {}", ht.table, e.getMessage());
            }
        }
        if (totalDeleted > 0) {
            log.info("[数据维护] 每小时上限清理完成，共删除 {} 条", totalDeleted);
        }
    }

    private int trimTable(HighFreqTable ht) {
        // 检查表是否存在
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?",
            Integer.class, ht.table);
        if (count == null || count == 0) return 0;

        // 找出所有超标的 (日期, 小时) 组合
        String findSql = String.format(
            "SELECT DATE(%s) AS d, HOUR(%s) AS h FROM %s GROUP BY d, h HAVING COUNT(*) > ?",
            ht.timeCol, ht.timeCol, ht.table);
        List<Map<String, Object>> overflows;
        try {
            overflows = jdbc.queryForList(findSql, MAX_PER_HOUR);
        } catch (Exception e) {
            return 0;
        }

        int totalDeleted = 0;
        for (Map<String, Object> row : overflows) {
            Object dObj = row.get("d");
            Object hObj = row.get("h");
            if (dObj == null || hObj == null) continue;

            String dateStr = dObj.toString(); // e.g. "2026-08-11"
            int hour = ((Number) hObj).intValue();

            // 删除该小时内最旧的记录，保留最新的 MAX_PER_HOUR 条
            // MySQL 不支持 LIMIT 子查询直接 DELETE，用 JOIN 方式
            String deleteSql = String.format(
                "DELETE FROM %s WHERE id NOT IN (" +
                "  SELECT id FROM (" +
                "    SELECT id FROM %s WHERE DATE(%s) = ? AND HOUR(%s) = ? ORDER BY %s DESC LIMIT ?" +
                "  ) AS keep" +
                ") AND DATE(%s) = ? AND HOUR(%s) = ?",
                ht.table, ht.table, ht.timeCol, ht.timeCol, ht.timeCol,
                ht.timeCol, ht.timeCol);

            int deleted = jdbc.update(deleteSql, dateStr, hour, MAX_PER_HOUR, dateStr, hour);
            totalDeleted += deleted;
        }
        return totalDeleted;
    }
}
