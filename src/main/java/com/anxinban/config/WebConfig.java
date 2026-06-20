package com.anxinban.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 层全局配置类。
 *
 * <p>实现 {@link WebMvcConfigurer} 接口，用于集中配置 Spring MVC 相关行为。
 * 当前主要配置跨域资源共享（CORS），允许前端/APP 在浏览器端跨域访问以 {@code /api/} 开头的 REST 接口。</p>
 *
 * <p>生产环境建议将 {@code allowedOrigins("*")} 收紧为具体域名，避免安全风险。</p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置跨域映射规则。
     *
     * <p>对所有以 {@code /api/} 开头的请求路径启用 CORS，具体配置项含义如下：</p>
     * <ul>
     *   <li>{@code addMapping("/api/**")}：仅对 {@code /api/} 及其子路径生效；</li>
     *   <li>{@code allowedOrigins("*")}：允许任意来源访问，开发/演示环境使用，生产环境应替换为具体域名；</li>
     *   <li>{@code allowedMethods(...)}：允许的 HTTP 方法，覆盖常见的 CRUD 及预检请求；</li>
     *   <li>{@code allowedHeaders("*")}：允许携带任意请求头，便于携带 Token、Content-Type 等；</li>
     *   <li>{@code maxAge(3600)}：预检请求（OPTIONS）结果缓存 3600 秒，减少重复预检。</li>
     * </ul>
     *
     * @param registry Spring 提供的 CORS 注册器，用于添加跨域规则
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")              // 只对 /api/ 开头的接口启用跨域
                .allowedOriginPatterns(
                    // 正式域名
                    "http://elderlyweb.cn",
                    "https://elderlyweb.cn",
                    // 本地开发
                    "http://localhost:*",
                    "https://localhost:*",
                    "http://127.0.0.1:*",
                    "http://127.0.0.1",
                    // 服务器 IP
                    "http://120.27.129.78:*",
                    "http://120.27.129.78",
                    // EdgeOne CDN 临时域名（支持多级子域名，后续可能会变）
                    "https://*.edgeone.cool",
                    "https://*.*.edgeone.cool",
                    "https://*.*.*.edgeone.cool",
                    "https://webadmin-dp2zudh6dpbn.zh-cn.edgeone.cool",
                    // EdgeOne Pages 预览域名
                    "https://*.edgeone.app",
                    "https://*.*.edgeone.app"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 方法
                .allowedHeaders("*")                // 允许任意请求头
                .allowCredentials(true)
                .maxAge(3600);                      // 预检请求缓存 1 小时
    }
}
