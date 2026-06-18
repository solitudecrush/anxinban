/**
 * MQTT 设备模拟器包。
 *
 * <p>本包在缺乏真实硬件时模拟 ESP32 传感器、摄像头、智能手表、门锁等设备的行为，
 * 承担 <b>模拟设备客户端（发布者/订阅者）</b> 的角色。模拟器作为独立 MQTT 客户端连接到
 * EMQX Broker，按固定间隔向云端上报温湿度、床压、烟雾、健康数据等传感器数据，
 * 并随机触发门禁、摔倒等事件；同时提供 REST API 手动触发各类场景，方便开发调试与链路验证。</p>
 *
 * <p>主要类：</p>
 * <ul>
 *   <li>{@link com.anxinban.mqtt.simulator.SimulatorProperties}：模拟器开关、默认房屋/房间、发送间隔等配置；</li>
 *   <li>{@link com.anxinban.mqtt.simulator.DeviceSimulator}：模拟器核心，负责 MQTT 连接、定时任务与手动事件发布；</li>
 *   <li>{@link com.anxinban.mqtt.simulator.SimulatorController}：REST 控制层，暴露 {@code /api/simulator/*} 接口控制模拟器。</li>
 * </ul>
 */
package com.anxinban.mqtt.simulator;
