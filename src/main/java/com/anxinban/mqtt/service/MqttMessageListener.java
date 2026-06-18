package com.anxinban.mqtt.service;

/**
 * MQTT 消息监听器接口。
 *
 * 作用：所有需要处理 MQTT 消息的服务（如数据持久化、规则引擎、AI 智能体、告警推送等）
 * 实现该接口并注册到 MqttClientService，即可收到来自 EMQX 的主题消息。
 */
public interface MqttMessageListener {

    /**
     * 收到 MQTT 消息时的回调。
     *
     * @param topic   消息主题
     * @param payload 消息负载（UTF-8 字符串，通常为 JSON）
     */
    void onMessage(String topic, String payload);
}
