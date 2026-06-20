package com.anxinban.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局 CORS 跨域配置。
 *
 * <p>对所有路径 {@code /**} 启用跨域资源共享，允许任意来源通过
 * {@code allowedOriginPatterns} 模式访问，支持携带凭据（Cookie/Authorization）。</p>
 *
 * <p>注意：与 {@link WebConfig} 中针对 {@code /api/**} 的 CORS 规则互补，
 * 此配置提供更宽泛的全路径跨域支持。</p>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                    "http://elderlyweb.cn",
                    "https://elderlyweb.cn",
                    "http://localhost:*",
                    "https://localhost:*",
                    "http://127.0.0.1:*",
                    "http://127.0.0.1",
                    "http://120.27.129.78:*",
                    "http://120.27.129.78"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
