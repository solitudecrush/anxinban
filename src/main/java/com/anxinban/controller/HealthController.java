package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * 比赛兼容层 - 健康检查接口。
 *
 * <p>提供 GET /api/health 端点，用于队长和前端快速判断后端是否启动、
 * 数据库是否可连接。不依赖任何业务 Service，直接检测数据源。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    /**
     * 健康检查接口。
     *
     * <p>返回服务名称、运行状态、运行时环境和数据库连接状态。
     * 数据库检测通过尝试获取连接来判断，失败时 database 返回 "disconnected"
     * 并附带错误原因。</p>
     *
     * @return 健康状态 JSON
     */
    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        Map<String, String> data = new HashMap<>();
        data.put("service", "anxinban-backend");
        data.put("status", "running");
        data.put("runtime", "spring-boot");

        try (Connection conn = dataSource.getConnection()) {
            if (conn != null && conn.isValid(3)) {
                data.put("database", "connected");
            } else {
                data.put("database", "disconnected");
                data.put("db_error", "connection invalid");
            }
        } catch (Exception e) {
            data.put("database", "disconnected");
            data.put("db_error", e.getMessage());
        }

        return ApiResponse.success(data);
    }
}
