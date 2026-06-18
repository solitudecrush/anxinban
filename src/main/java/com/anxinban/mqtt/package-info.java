/**
 * 安心班项目 MQTT 物联网模块根包。
 *
 * <p>本模块作为云端后端与家庭网关、ESP32 传感器、摄像头、智能手表、门锁等硬件之间的
 * MQTT 通信桥梁。核心职责包括：</p>
 * <ul>
 *   <li>通过 EMQX Broker 建立 MQTT 连接，充当 <b>发布者（Publisher）</b> 与 <b>订阅者（Subscriber）</b>；</li>
 *   <li>订阅并解析设备上报的传感器数据、状态、事件告警与图片消息；</li>
 *   <li>向指定主题下发控制指令，实现云端对灯光、窗帘、蜂鸣器、摄像头等执行器的远程控制；</li>
 *   <li>基于规则引擎处理设备事件并触发联动动作；</li>
 *   <li>集成 AI 智能体，提供语音/文字对话、查找物品等高级交互；</li>
 *   <li>提供设备模拟器，用于开发测试阶段模拟真实硬件数据上报。</li>
 * </ul>
 *
 * <p>主题层级统一遵循 {@code house/{houseId}/{room}/...} 规范，具体定义见
 * {@link com.anxinban.mqtt.constant.MqttTopicConstants}。</p>
 */
package com.anxinban.mqtt;
