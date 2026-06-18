/**
 * MQTT 配置包。
 *
 * <p>本包集中管理 MQTT 通信所需的配置参数，包括：</p>
 * <ul>
 *   <li>EMQX Broker 连接参数：地址、端口、客户端 ID、用户名/密码；</li>
 *   <li>连接行为参数：QoS、Clean Session、KeepAlive、自动重连、连接超时；</li>
 *   <li>TLS/SSL 单向或双向认证所需的信任库与密钥库路径；</li>
 *   <li>启动时默认订阅的主题列表；</li>
 *   <li>设备模拟器的开关、默认房屋/房间、发送间隔等参数。</li>
 * </ul>
 *
 * <p>所有配置类均使用 Spring Boot {@code @ConfigurationProperties} 绑定，前缀为
 * {@code mqtt.*} 与 {@code mqtt.simulator.*}，供
 * {@link com.anxinban.mqtt.service.MqttClientService} 与
 * {@link com.anxinban.mqtt.simulator.DeviceSimulator} 读取使用。</p>
 */
package com.anxinban.mqtt.config;
