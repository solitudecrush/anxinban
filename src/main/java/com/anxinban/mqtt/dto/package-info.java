/**
 * MQTT 数据传输对象（DTO）包。
 *
 * <p>本包定义 MQTT 消息在云端与设备之间传输时的 JSON 载荷结构，承担
 * <b>消息载荷模型</b> 的角色。所有 DTO 均继承
 * {@link com.anxinban.mqtt.dto.BaseDeviceMessage}，统一包含 deviceId、messageId、timestamp
 * 等公共字段。</p>
 *
 * <p>DTO 与 MQTT 主题、设备上报协议之间的对应关系如下：</p>
 * <ul>
 *   <li>传感器数据上报 {@code house/{houseId}/{room}/sensor/{type}/data}：
 *       {@link TemperatureHumidityReport}、{@link BedPressureReport}、
 *       {@link SmokeAlarmEvent}、{@link WatchHealthData}；</li>
 *   <li>事件告警 {@code house/{houseId}/{room}/event/{type}/alert}：
 *       {@link FingerprintEvent}、{@link DoorAccessEvent}、
 *       {@link CameraFallDetectionEvent}、{@link WatchEmergencyCall}；</li>
 *   <li>设备状态反馈 {@code house/{houseId}/{room}/device/{deviceId}/status}：
 *       {@link LightStatus}、{@link CurtainStatus}；</li>
 *   <li>指令下发 {@code house/{houseId}/{room}/actuator/{deviceId}/cmd}：
 *       {@link LightCmd}、{@link CurtainCmd}、{@link BuzzerCmd}、
 *       {@link CameraFindItemReq}；</li>
 *   <li>摄像头图片上报 {@code house/{houseId}/{room}/camera/{cameraId}/image}：
 *       {@link CameraFindItemResp}；</li>
 *   <li>AI 智能体对话 {@code house/{houseId}/agent/request|response}：
 *       {@link AiConversationReq}、{@link AiConversationResp}。</li>
 * </ul>
 */
package com.anxinban.mqtt.dto;
