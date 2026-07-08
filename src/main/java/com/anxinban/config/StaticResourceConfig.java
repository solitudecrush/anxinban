package com.anxinban.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 静态资源配置。
 *
 * <p>将本地 uploads 目录映射为 HTTP 静态资源路径，使上传的头像、快照等文件
 * 可以通过 URL 直接访问。</p>
 *
 * <p>映射关系：{@code /uploads/**} → 文件系统 {@code {app.upload.dir}/}</p>
 *
 * <p>启动时自动创建 uploads/avatars 和 uploads/snapshots 子目录，
 * 并生成默认头像。</p>
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadRoot;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadRoot).toAbsolutePath();

        // 确保上传目录及子目录存在
        try {
            Files.createDirectories(uploadPath.resolve("avatars"));
            Files.createDirectories(uploadPath.resolve("snapshots"));

            // 生成默认头像（若不存在）
            Path defaultAvatar = uploadPath.resolve("avatars").resolve("default.png");
            if (!Files.exists(defaultAvatar)) {
                createDefaultAvatar(defaultAvatar);
            }
        } catch (Exception e) {
            // 目录创建失败不阻塞启动，上传时再处理
        }

        // 映射 uploads 目录为 HTTP 可访问路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath.toFile().getAbsolutePath() + "/")
                .setCachePeriod(3600); // 1 小时缓存
    }

    /**
     * 生成一个简单的默认头像（纯色 PNG，含"默认头像"字样）。
     *
     * <p>这是一个最小的 1x1 占位 PNG。如需美观的默认头像，
     * 手动放置一张 default.png 到 uploads/avatars/ 目录即可覆盖。</p>
     */
    private void createDefaultAvatar(Path path) throws Exception {
        // 最小有效 PNG（1x1 灰色像素，作为占位默认头像）
        byte[] minimalPng = new byte[] {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,       // IHDR chunk
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
            0x08, 0x02, 0x00, 0x00, 0x00, (byte) 0x90, 0x77, 0x53, (byte) 0xDE,
            0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41, 0x54,       // IDAT chunk
            0x08, (byte) 0xD7, 0x63, (byte) 0xF8, (byte) 0xCF, (byte) 0xC0, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,       // IEND chunk
            (byte) 0xAE, 0x42, 0x60, (byte) 0x82
        };
        Files.write(path, minimalPng);
    }
}
