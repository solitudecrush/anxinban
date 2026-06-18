/**
 * MQTT 消息处理器包（预留扩展）。
 *
 * <p>本包用于放置按主题或按设备类型拆分的细粒度消息处理器，承担
 * <b>消息处理单元</b> 的角色。当前模块中，消息路由主要由
 * {@link com.anxinban.mqtt.service.MqttMessageConsumer} 统一处理；未来若业务复杂，
 * 可将摄像头图片处理、健康数据分析、告警事件处理等逻辑拆分为独立 Handler，
 * 由消费者进行委托调用，以降低单个类的复杂度。</p>
 *
 * <p>预期处理器示例：</p>
 * <ul>
 *   <li>{@code CameraImageHandler}：处理摄像头抓拍图片，保存到对象存储并触发识别；</li>
 *   <li>{@code HealthDataHandler}：解析手表健康数据并写入健康档案；</li>
 *   <li>{@code AlarmEventHandler}：将告警事件持久化并推送通知。</li>
 * </ul>
 */
package com.anxinban.mqtt.handler;
