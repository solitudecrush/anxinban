/**
 * 安心伴智慧家居系统 REST API 控制器层。
 *
 * <p>本包负责接收前端 App / Web 管理后台 / 第三方系统的 HTTP 请求，
 * 进行参数校验后调用对应的 Service 完成业务处理，并将结果封装为统一的 JSON 响应。</p>
 *
 * <p>控制器按业务领域划分：</p>
 * <ul>
 *   <li>用户认证：{@link com.anxinban.controller.AuthController}</li>
 *   <li>老人档案：{@link com.anxinban.controller.ElderController}</li>
 *   <li>健康数据：{@link com.anxinban.controller.HealthController}、{@link com.anxinban.controller.HealthRecordController}</li>
 *   <li>设备管理：{@link com.anxinban.controller.DeviceController}、{@link com.anxinban.controller.DeviceDataController}</li>
 *   <li>告警与 SOS：{@link com.anxinban.controller.AlarmController}、{@link com.anxinban.controller.SosController}</li>
 *   <li>AI 智能体：{@link com.anxinban.controller.AgentController}、{@link com.anxinban.controller.CloudAgentController}、{@link com.anxinban.controller.LocalAgentController}</li>
 *   <li>工单与服务：{@link com.anxinban.controller.WorkOrderController}、{@link com.anxinban.controller.ServiceRequestController}</li>
 *   <li>系统看板：{@link com.anxinban.controller.DashboardController}</li>
 * </ul>
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
package com.anxinban.controller;
