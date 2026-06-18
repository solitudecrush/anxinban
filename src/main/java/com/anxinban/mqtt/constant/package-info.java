/**
 * MQTT 主题常量包。
 *
 * <p>本包统一约定云端与家庭网关、硬件设备之间的 MQTT 主题格式，承担
 * <b>主题规范定义者</b> 的角色。主要提供：</p>
 * <ul>
 *   <li>主题层级分隔符与通配符常量；</li>
 *   <li>数据上报、状态反馈、指令下发、事件告警、摄像头图片、AI 智能体对话等主题模板；</li>
 *   <li>根据房屋 ID、房间、设备 ID 动态生成具体主题的工具方法；</li>
 *   <li>云端默认使用的通配订阅主题，用于一次性订阅某类所有设备消息。</li>
 * </ul>
 *
 * <p>所有主题均遵循 {@code house/{houseId}/{room}/{category}/{deviceId}/{action}} 的层级结构，
 * 是 MQTT 模块发布与订阅行为的基础约定。</p>
 */
package com.anxinban.mqtt.constant;
