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
        /**
         * addCorsMappings 方法。
         *
         * @param registry 字段含义待补充
         */
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")              // 只对 /api/ 开头的接口启用跨域
                .allowedOrigins("*")                // 允许任意来源（生产环境请改为具体域名）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 方法
                .allowedHeaders("*")                // 允许任意请求头
                .maxAge(3600);                      // 预检请求缓存 1 小时
    }
}
