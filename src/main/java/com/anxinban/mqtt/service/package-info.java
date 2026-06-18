/**
 * MQTT 核心服务包。
 *
 * <p>本包包含 MQTT 模块的业务核心类，承担 <b>连接管理、消息消费、规则联动与智能交互</b> 的角色：
 * <ul>
 *   <li>{@link com.anxinban.mqtt.service.MqttClientService}：
 *       MQTT 客户端核心服务，既是 <b>发布者</b> 也是 <b>订阅者</b>，负责连接 EMQX、
 *       订阅主题、发布消息，并将收到的消息分发给所有监听器；</li>
 *   <li>{@link com.anxinban.mqtt.service.MqttMessageListener}：
 *       消息监听器接口，所有需要处理 MQTT 消息的服务实现该接口并注册到客户端；</li>
 *   <li>{@link com.anxinban.mqtt.service.MqttMessageConsumer}：
 *       消息消费者，订阅设备上报主题，按主题解析 JSON 载荷并持久化到数据库；</li>
 *   <li>{@link com.anxinban.mqtt.service.RuleEngineService}：
 *       规则引擎服务，监听设备事件并执行夜间离床、陌生人闯入、紧急呼叫、跌倒检测等联动规则；</li>
 *   <li>{@link com.anxinban.mqtt.service.AIAgentService}：
 *       AI 智能体服务，作为云端与前端/语音网关之间的对话入口，将请求发布到 MQTT 主题并执行意图联动。</li>
 * </ul>
 */
package com.anxinban.mqtt.service;
