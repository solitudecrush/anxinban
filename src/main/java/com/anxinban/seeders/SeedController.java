package com.anxinban.seeders;

import com.anxinban.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 种子数据手动触发控制器。
 *
 * <p>POST /api/seed/health — 清空并重新生成过去 7 天的健康模拟数据（音乐、陪伴、物品寻找）。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/seed")
public class SeedController {

    private final HealthDataSeeder seeder;

    @Autowired
    public SeedController(HealthDataSeeder seeder) {
        this.seeder = seeder;
    }

    /**
     * 触发健康数据种子生成。
     */
    @PostMapping("/health")
    public ApiResponse<String> seedHealthData() {
        seeder.seed();
        return ApiResponse.success("健康数据种子已生成（过去7天）");
    }
}
