/**
 * MQTT 规则引擎包（预留扩展）。
 *
 * <p>本包用于放置配置化规则定义与规则执行相关类，承担
 * <b>业务规则中枢</b> 的角色。当前联动规则由
 * {@link com.anxinban.mqtt.service.RuleEngineService} 以硬编码方式实现；
 * 未来可在此包引入规则模型（Rule）、规则加载器（RuleLoader）与规则执行器（RuleExecutor），
 * 实现设备事件与联动动作的解耦，支持通过配置文件或数据库动态调整规则。</p>
 *
 * <p>预期能力：</p>
 * <ul>
 *   <li>定义规则条件（主题匹配、字段阈值、时间窗口等）；</li>
 *   <li>定义规则动作（下发指令、生成告警、调用 AI 服务等）；</li>
 *   <li>支持规则的动态加载、启用/禁用与优先级排序。</li>
 * </ul>
 */
package com.anxinban.mqtt.rule;
