package com.anxinban;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 安心伴后端应用上下文加载测试类。
 *
 * <p>测试目标：验证 Spring Boot 应用上下文能否正常启动并加载所有 Bean 配置，
 * 确保应用主类 {@link AnxinbanApplication}、MQTT 配置、JPA 配置、Web 配置等
 * 在测试环境中无冲突、可正常初始化。</p>
 *
 * <p>前置条件：application.properties 中的数据库、MQTT 等配置在测试环境可访问；
 * 若本地服务未启动，可通过 {@code @TestPropertySource} 或 {@code spring.profiles.active=test}
 * 覆盖相关配置。</p>
 */
@SpringBootTest(properties = "mqtt.enabled=false")
class AnxinbanApplicationTests {

    /**
     * 验证 Spring Boot 上下文加载成功。
     *
     * <p>该方法无任何显式断言，若上下文加载失败，测试框架会自动抛出异常导致用例失败；
     * 加载成功则表明应用整体配置正确。</p>
     */
    @Test
    void contextLoads() {
        // 空方法：仅用于触发 Spring Boot 上下文初始化
    }

}
