package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 根路径控制器。
 *
 * <p>处理 {@code GET /} 请求，避免 Spring Boot 因找不到静态资源而返回
 * "No static resource for request '/'" 错误。返回 API 基本信息。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
public class IndexController {

    /**
     * 根路径接口。
     *
     * <p>返回服务名称、版本和可用接口提示，方便前端/运维快速确认后端是否启动。</p>
     *
     * @return API 基本信息
     */
    @GetMapping("/")
    public ApiResponse<Map<String, String>> index() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("service", "anxinban-backend");
        data.put("version", "0.0.1-SNAPSHOT");
        data.put("health", "/api/health");
        data.put("docs", "/api");
        return ApiResponse.success(data);
    }
}
