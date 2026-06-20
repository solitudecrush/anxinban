package com.anxinban.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Jackson 2.x 配置 — 显式提供 ObjectMapper Bean。
 *
 * <p>Spring Boot 4.x 默认依赖 Jackson 3.x（tools.jackson），但本项目
 * 显式排除了 spring-boot-starter-jackson 并使用 Jackson 2.18.5。
 * Spring Boot 4.x 的 JacksonAutoConfiguration 仅对 Jackson 3.x 生效，
 * 因此必须手动提供 ObjectMapper 和 MappingJackson2HttpMessageConverter，
 * 确保 @RequestBody 反序列化正确识别 @JsonProperty 注解。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
public class JacksonConfig {

    /**
     * 提供全局 ObjectMapper Bean（Jackson 2.x）。
     *
     * <p>核心配置：忽略未知 JSON 属性，避免前端多传字段导致 400。</p>
     *
     * @return 配置好的 Jackson 2.x ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 忽略未知属性 — 前端多传字段不会导致 400
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 不序列化 null 值，减少响应体大小
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }

    /**
     * 提供 Jackson 2.x 消息转换器，确保 Spring MVC 使用我们配置的 ObjectMapper。
     *
     * @param objectMapper 上面定义的 ObjectMapper Bean
     * @return MappingJackson2HttpMessageConverter 实例
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}
