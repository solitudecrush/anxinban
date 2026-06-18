package com.anxinban.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 数据源配置类。
 *
 * <p>Spring Boot 4.0.6 在特定依赖组合下无法从 {@code spring.datasource.url}
 * 自动推断 MySQL 驱动类（{@code Failed to determine a suitable driver class}）。
 * 本配置显式构造 {@link HikariDataSource}，指定驱动类、连接地址、用户名和密码，
 * 绕过自动推断逻辑，确保应用/测试能够正常启动。</p>
 *
 * <p>生产环境建议将密码等敏感信息通过环境变量或配置中心注入。</p>
 */
@Configuration
public class DataSourceConfig {

    /**
     * 创建 HikariCP 数据源。
     *
     * @param url      数据库 JDBC URL
     * @param username 数据库用户名
     * @param password 数据库密码
     * @return 配置完成的数据源
     */
    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {
        HikariConfig config = new HikariConfig();
        // 显式指定 MySQL 驱动类，避免 Spring Boot 4.0.6 自动推断失败
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        // 连接池基础配置
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        return new HikariDataSource(config);
    }
}
