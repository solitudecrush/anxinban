package com.anxinban.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson 序列化配置类。
 *
 * <p>项目显式依赖 Jackson 2.x（{@code com.fasterxml.jackson}），而 Spring Boot 4.x 默认自动装配
 * 的是 Jackson 3.x（{@code tools.jackson}）。本配置显式暴露一个 {@link ObjectMapper} Bean，
 * 供 MQTT 服务、规则引擎、模拟器等需要 Jackson 2.x 的组件注入使用。</p>
 *
 * <p>注意：若后续统一升级到 Jackson 3.x，可将此处 Bean 类型改为
 * {@code tools.jackson.databind.json.JsonMapper}，并同步修改各业务类的 import。</p>
 */
@Configuration
public class JacksonConfig {

    /**
     * 创建 Jackson 2.x 的 ObjectMapper Bean。
     *
     * <p>使用 {@link Primary} 标记，确保在存在多个候选时优先注入本 Bean。</p>
     *
     * @return Jackson 2.x ObjectMapper 实例
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
