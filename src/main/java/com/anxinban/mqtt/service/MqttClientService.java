package com.anxinban.mqtt.service;
import com.anxinban.mqtt.config.MqttProperties;
import com.anxinban.mqtt.constant.MqttTopicConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MQTT 客户端核心服务。
 *
 * 作用：作为云端后端的 MQTT 客户端，连接到 EMQX Broker；
 * 负责订阅设备数据/状态/事件/图片主题，接收消息后分发给所有注册的监听器；
 * 同时提供向指定主题发布消息的能力，用于向网关/硬件下发指令。
 */
@Service
public class MqttClientService implements InitializingBean, DisposableBean, MqttCallback {

    private static final Logger log = LoggerFactory.getLogger(MqttClientService.class);
    private final MqttProperties mqttProperties;
    private final ObjectMapper objectMapper;
    private final List<MqttMessageListener> listeners = new CopyOnWriteArrayList<>();
    /** 客户端实例 */
    private MqttClient mqttClient;
    private final AtomicBoolean connected = new AtomicBoolean(false);

    public MqttClientService(MqttProperties mqttProperties, ObjectMapper objectMapper) {
        this.mqttProperties = mqttProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * 注册消息监听器。
     */
    public void addListener(MqttMessageListener listener) {
        listeners.add(listener);
        log.info("已注册 MQTT 消息监听器：{}", listener.getClass().getSimpleName());
    }

    /**
     * 移除消息监听器。
     */
    public void removeListener(MqttMessageListener listener) {
        listeners.remove(listener);
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        // 未启用 MQTT 时直接跳过连接，避免 Broker 不可用时导致应用启动失败
        if (!mqttProperties.isEnabled()) {
            log.warn("MQTT 功能已禁用（mqtt.enabled=false），跳过 Broker 连接");
            return;
        }
        try {
            connect();
        } catch (MqttException e) {
            // 连接失败仅记录警告，不阻塞 Spring 上下文启动；后续发布/订阅操作会再次检查连接状态
            log.warn("MQTT Broker 连接失败：{}。应用将继续启动，MQTT 相关功能不可用。", e.getMessage());
        }
    }
    @Override
    public void destroy() throws Exception {
        disconnect();
    }

    /**
     * 连接到 EMQX Broker。
     *
     * <p>如果 MQTT 功能被禁用，则直接返回；已连接时也不会重复连接。</p>
     */
    public synchronized void connect() throws MqttException {
        if (!mqttProperties.isEnabled()) {
            log.warn("MQTT 功能已禁用，取消连接");
            return;
        }
        if (connected.get() && mqttClient != null && mqttClient.isConnected()) {
            log.info("MQTT 已连接，无需重复连接");
            return;
        }

        String brokerUrl = mqttProperties.getBrokerUrl();
        String clientId = buildClientId();

        log.info("正在连接 MQTT Broker：{}，客户端 ID：{}", brokerUrl, clientId);

        mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
        mqttClient.setCallback(this);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(mqttProperties.isCleanSession());
        options.setAutomaticReconnect(mqttProperties.isAutomaticReconnect());
        options.setConnectionTimeout(mqttProperties.getConnectionTimeout());
        options.setKeepAliveInterval(mqttProperties.getKeepAliveInterval());

        if (mqttProperties.getUsername() != null && !mqttProperties.getUsername().isEmpty()) {
            options.setUserName(mqttProperties.getUsername());
        }
        if (mqttProperties.getPassword() != null && !mqttProperties.getPassword().isEmpty()) {
            options.setPassword(mqttProperties.getPassword().toCharArray());
        }

        if (mqttProperties.isSslEnabled()) {
            try {
                options.setSocketFactory(buildSslContext().getSocketFactory());
            } catch (Exception e) {
                throw new MqttException(new Throwable("MQTT TLS 配置失败：" + e.getMessage()));
            }
        }

        mqttClient.connect(options);
        connected.set(true);
        log.info("MQTT 连接成功");

        subscribeDefaultTopics();
    }

    /**
     * 断开 MQTT 连接。
     */
    public synchronized void disconnect() {
        connected.set(false);
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
                mqttClient.close();
                log.info("MQTT 已断开连接");
            } catch (MqttException e) {
                log.error("MQTT 断开连接失败", e);
            }
        }
    }

    /**
     * 订阅默认主题集合。
     */
    public void subscribeDefaultTopics() throws MqttException {
        List<String> topics = mqttProperties.getSubscribeTopics();
        if (topics == null || topics.isEmpty()) {
            // 默认订阅所有数据、状态、事件、图片主题
            subscribe(MqttTopicConstants.ALL_SENSOR_DATA);
            subscribe(MqttTopicConstants.ALL_DEVICE_STATUS);
            subscribe(MqttTopicConstants.ALL_EVENT_ALERTS);
            subscribe(MqttTopicConstants.ALL_CAMERA_IMAGES);
            subscribe(MqttTopicConstants.ALL_AGENT_REQUESTS);
            subscribe(MqttTopicConstants.ALL_AGENT_RESPONSES);
        } else {
            for (String topic : topics) {
                subscribe(topic);
            }
        }
    }

    /**
     * 订阅单个主题，使用配置中的默认 QoS。
     */
    public void subscribe(String topic) throws MqttException {
        if (mqttClient == null || !mqttClient.isConnected()) {
            log.warn("MQTT 未连接，无法订阅主题：{}", topic);
            return;
        }
        mqttClient.subscribe(topic, mqttProperties.getQos());
        log.info("已订阅 MQTT 主题：{}，QoS：{}", topic, mqttProperties.getQos());
    }

    /**
     * 取消订阅主题。
     */
    public void unsubscribe(String topic) throws MqttException {
        if (mqttClient != null && mqttClient.isConnected()) {
            mqttClient.unsubscribe(topic);
            log.info("已取消订阅 MQTT 主题：{}", topic);
        }
    }

    /**
     * 向指定主题发布 JSON 消息，使用默认 QoS，不保留。
     */
    public void publish(String topic, String payload) throws MqttException {
        publish(topic, payload, mqttProperties.getQos(), false);
    }

    /**
     * 向指定主题发布消息。
     *
     * @param topic    目标主题
     * @param payload  消息内容
     * @param qos      QoS 等级
     * @param retained 是否保留消息
     */
    public void publish(String topic, String payload, int qos, boolean retained) throws MqttException {
        if (mqttClient == null || !mqttClient.isConnected()) {
            log.warn("MQTT 未连接，无法发布消息到主题：{}", topic);
            throw new MqttException(MqttException.REASON_CODE_CLIENT_NOT_CONNECTED);
        }
        MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
        message.setQos(qos);
        message.setRetained(retained);
        mqttClient.publish(topic, message);
        log.debug("已发布消息到主题：{}，内容：{}", topic, payload);
    }

    /**
     * 向指定主题发布 JSON 消息（便捷方法）。
     */
    public void publishJson(String topic, Object payload) throws MqttException {
        try {
            String json = objectMapper.writeValueAsString(payload);
            publish(topic, json);
        } catch (JsonProcessingException e) {
            throw new MqttException(new Throwable("MQTT 消息 JSON 序列化失败：" + e.getMessage()));
        }
    }

    /**
     * 当前是否已连接。
     */
    public boolean isConnected() {
        return connected.get() && mqttClient != null && mqttClient.isConnected();
    }

    // ==================== MqttCallback 回调 ====================

    @Override
    public void connectionLost(Throwable cause) {
        connected.set(false);
        log.error("MQTT 连接断开：{}", cause.getMessage(), cause);
        // Paho 已配置 automaticReconnect，此处无需手动重连；如未启用可在此触发重连逻辑
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
        log.debug("收到 MQTT 消息，主题：{}，QoS：{}，内容：{}", topic, message.getQos(), payload);

        for (MqttMessageListener listener : listeners) {
            try {
                listener.onMessage(topic, payload);
            } catch (Exception e) {
                log.error("MQTT 消息监听器处理失败：{}", listener.getClass().getSimpleName(), e);
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 消息发布完成回调，一般无需处理
    }

    // ==================== 私有方法 ====================

        /**
         * buildClientId 方法。
         */
    private String buildClientId() {
        String clientId = mqttProperties.getClientId();
        // 保证客户端 ID 唯一，避免多实例冲突
        return clientId + "-" + System.currentTimeMillis();
    }

    /**
     * 构建 TLS SSLContext（单向或双向认证）。
     */
    private SSLContext buildSslContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        TrustManagerFactory trustManagerFactory = null;
        KeyManagerFactory keyManagerFactory = null;

        if (mqttProperties.getSslTrustStore() != null && !mqttProperties.getSslTrustStore().isEmpty()) {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (FileInputStream fis = new FileInputStream(mqttProperties.getSslTrustStore())) {
                trustStore.load(fis, mqttProperties.getSslTrustStorePassword().toCharArray());
            }
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
        }

        if (mqttProperties.getSslKeyStore() != null && !mqttProperties.getSslKeyStore().isEmpty()) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (FileInputStream fis = new FileInputStream(mqttProperties.getSslKeyStore())) {
                keyStore.load(fis, mqttProperties.getSslKeyStorePassword().toCharArray());
            }
            keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, mqttProperties.getSslKeyStorePassword().toCharArray());
        }

        sslContext.init(
                keyManagerFactory != null ? keyManagerFactory.getKeyManagers() : null,
                trustManagerFactory != null ? trustManagerFactory.getTrustManagers() : null,
                null
        );
        return sslContext;
    }
}
