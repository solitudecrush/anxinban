package com.anxinban.mqtt.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * MQTT 连接配置类。
 *
 * 作用：从 application.properties / application.yml 中读取 EMQX Broker 的连接参数，
 * 包括地址、端口、认证信息、TLS 开关、QoS、自动重连等，供 MqttClientService 使用。
 * 配置前缀为：mqtt.*
 */
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {
    /**
     * 是否启用 MQTT 客户端。
     *
     * <p>开发/测试环境可设为 false 以避免连接 Broker 失败导致应用无法启动；
     * 生产环境必须设为 true 以接收设备上报消息。</p>
     */
    private boolean enabled = true;

    private String brokerHost = "localhost";
    private int brokerPort = 1883;
    private String clientId = "backend-anxinban";
    private String username;
    private String password;
    private int connectionTimeout = 30;
    private int keepAliveInterval = 60;
    private boolean cleanSession = true;
    private boolean automaticReconnect = true;
    private int qos = 1;
    private boolean sslEnabled = false;
    private String sslTrustStore;
    private String sslTrustStorePassword;
    private String sslKeyStore;
    private String sslKeyStorePassword;
    private List<String> subscribeTopics = new ArrayList<>();

        /**
         * 获取URL 地址。
         *
         * @return URL 地址
         */
    public String getBrokerUrl() {
        String protocol = sslEnabled ? "ssl://" : "tcp://";
        return protocol + brokerHost + ":" + brokerPort;
    }

    // Getters and Setters

    /**
     * 判断是否启用 MQTT 客户端。
     *
     * @return true 表示启用，false 表示禁用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用 MQTT 客户端。
     *
     * @param enabled true 表示启用，false 表示禁用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

        /**
         * 获取Broker 主机地址。
         *
         * @return Broker 主机地址
         */
    public String getBrokerHost() {
        return brokerHost;
    }

        /**
         * 设置Broker 主机地址。
         *
         * @param brokerHost Broker 主机地址
         */
    public void setBrokerHost(String brokerHost) {
        this.brokerHost = brokerHost;
    }

        /**
         * 获取Broker 端口。
         *
         * @return Broker 端口
         */
    public int getBrokerPort() {
        return brokerPort;
    }

        /**
         * 设置Broker 端口。
         *
         * @param brokerPort Broker 端口
         */
    public void setBrokerPort(int brokerPort) {
        this.brokerPort = brokerPort;
    }

        /**
         * 获取客户端 ID。
         *
         * @return 客户端 ID
         */
    public String getClientId() {
        return clientId;
    }

        /**
         * 设置客户端 ID。
         *
         * @param clientId 客户端 ID
         */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

        /**
         * 获取用户名。
         *
         * @return 用户名
         */
    public String getUsername() {
        return username;
    }

        /**
         * 设置用户名。
         *
         * @param username 用户名
         */
    public void setUsername(String username) {
        this.username = username;
    }

        /**
         * 获取密码。
         *
         * @return 密码
         */
    public String getPassword() {
        return password;
    }

        /**
         * 设置密码。
         *
         * @param password 密码
         */
    public void setPassword(String password) {
        this.password = password;
    }

        /**
         * 获取时间。
         *
         * @return 时间
         */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

        /**
         * 设置时间。
         *
         * @param connectionTimeout 时间
         */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

        /**
         * 获取间隔。
         *
         * @return 间隔
         */
    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

        /**
         * 设置间隔。
         *
         * @param keepAliveInterval 间隔
         */
    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

        /**
         * 判断是否字段含义待补充。
         *
         * @return 是否字段含义待补充
         */
    public boolean isCleanSession() {
        return cleanSession;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param cleanSession 字段含义待补充
         */
    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

        /**
         * 判断是否字段含义待补充。
         *
         * @return 是否字段含义待补充
         */
    public boolean isAutomaticReconnect() {
        return automaticReconnect;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param automaticReconnect 字段含义待补充
         */
    public void setAutomaticReconnect(boolean automaticReconnect) {
        this.automaticReconnect = automaticReconnect;
    }

        /**
         * 获取MQTT QoS 等级。
         *
         * @return MQTT QoS 等级
         */
    public int getQos() {
        return qos;
    }

        /**
         * 设置MQTT QoS 等级。
         *
         * @param qos MQTT QoS 等级
         */
    public void setQos(int qos) {
        this.qos = qos;
    }

        /**
         * 判断是否是否启用。
         *
         * @return 是否是否启用
         */
    public boolean isSslEnabled() {
        return sslEnabled;
    }

        /**
         * 设置是否启用。
         *
         * @param sslEnabled 是否启用
         */
    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public String getSslTrustStore() {
        return sslTrustStore;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param sslTrustStore 字段含义待补充
         */
    public void setSslTrustStore(String sslTrustStore) {
        this.sslTrustStore = sslTrustStore;
    }

        /**
         * 获取密码。
         *
         * @return 密码
         */
    public String getSslTrustStorePassword() {
        return sslTrustStorePassword;
    }

        /**
         * 设置密码。
         *
         * @param sslTrustStorePassword 密码
         */
    public void setSslTrustStorePassword(String sslTrustStorePassword) {
        this.sslTrustStorePassword = sslTrustStorePassword;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public String getSslKeyStore() {
        return sslKeyStore;
    }

        /**
         * 设置字段含义待补充。
         *
         * @param sslKeyStore 字段含义待补充
         */
    public void setSslKeyStore(String sslKeyStore) {
        this.sslKeyStore = sslKeyStore;
    }

        /**
         * 获取密码。
         *
         * @return 密码
         */
    public String getSslKeyStorePassword() {
        return sslKeyStorePassword;
    }

        /**
         * 设置密码。
         *
         * @param sslKeyStorePassword 密码
         */
    public void setSslKeyStorePassword(String sslKeyStorePassword) {
        this.sslKeyStorePassword = sslKeyStorePassword;
    }

        /**
         * 获取MQTT 主题。
         *
         * @return MQTT 主题
         */
    public List<String> getSubscribeTopics() {
        return subscribeTopics;
    }

        /**
         * 设置MQTT 主题。
         *
         * @param subscribeTopics MQTT 主题
         */
    public void setSubscribeTopics(List<String> subscribeTopics) {
        this.subscribeTopics = subscribeTopics;
    }
}
