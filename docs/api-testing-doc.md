# 安心伴智慧养老守护系统 — API 接口完整测试文档

> ⚠️ **本文档已严重过时，请勿作为对接依据！**  
> 以下接口路径大部分已不再使用，请以最新文档为准：  
> - **接口规范**：`docs/api文档.md`（v2.3，2026-07-31）  
> - **接口参考手册**：`docs/anxinban-api-reference-manual-v3.1.md`（v3.2，182个接口，含 curl 示例）  

> **基地址**：http://localhost:8080  
> **后端框架**：Spring Boot 2.7 / Java 17  
> **端口**：8080  
> **数据库**：MySQL 8.0+（35张表，生产环境 v20260704）  
> **文档版本**：v2.2（2026-07-31 标记过时）

---

## 一、接口总览（⚠️ 已过时，仅供参考）

以下为历史接口列表，实际可用接口请参考 `anxinban-api-reference-manual-v3.1.md`（182个接口）。

| 序号 | 原路径(已废弃) | 当前正确路径 | 说明 |
|------|------|------|------|
| 1 | GET /api/profile | GET /api/elder/{elderId} | 获取老人基本信息 |
| 2 | PATCH /api/profile | PUT /api/elder/{elderId} | 更新老人基本信息 |
| 3 | GET /api/vitals/latest | GET /api/vital-signs/latest/{elderId} | 获取最新体征 |
| 4 | GET /api/vitals/history | GET /api/vital-signs/heart-rate/list 等 | 体征历史趋势 |
| 5 | GET /api/alerts | GET /api/alarm/list | 获取告警列表 |
| 6 | GET /api/alerts/latest | GET /api/alarm/list?page=1&pageSize=1 | 最新告警 |
| 7 | POST /api/ai/analyze | POST /api/ai/health-analysis | 健康数据分析 |
| 8 | GET /api/elder/list | GET /api/elder/list | ✓ 仍可用 |
| 9 | GET /api/elder/{elderId} | GET /api/elder/{elderId} | ✓ 仍可用 |
| 10 | PUT /api/elder/{elderId} | PUT /api/elder/{elderId} | ✓ 仍可用 |
| 11 | GET /api/device/list | GET /api/device/list | ✓ 仍可用 |
| 12 | GET /api/device/{deviceId} | GET /api/device/{deviceId} | ✓ 仍可用 |
| 13 | PATCH /api/device/{deviceId}/status | PUT /api/device/{deviceId}/status | 更新设备状态 |
| 14 | GET /api/sensor-data/list | GET /api/device/{deviceId}/sensor-data | 传感器历史数据 |
| 15 | GET /api/blood-pressure/list | GET /api/vital-signs/blood-pressure/list | 血压历史记录 |
| 16 | GET /api/alarm/list | GET /api/alarm/list | ✓ 仍可用 |
| 17 | POST /api/alarm/{alarmId}/handle | PUT /api/alarm/{alarmId}/resolve | 处理告警 |
| 18 | POST /api/auth/login | POST /api/auth/login | ✓ 仍可用（推荐用 /login/web 或 /login/app） |
| 19 | GET /api/staff/list | GET /api/staff/list | ✓ 仍可用 |
| 20 | GET /api/family/list | 无此接口 | 家属信息通过 /api/auth/me 查询 |
| 21 | GET /api/emergency-contact/list | GET /api/emergency-contact/list | ✓ 仍可用 |
| 22 | GET /api/health-record/{elderId} | GET /api/health-record/by-elder/{elderId} | 健康档案 |
| 23 | PUT /api/health-record/{elderId} | POST /api/health-record | 保存档案（upsert） |
| 24 | GET /api/sos/list | GET /api/sos/list | ✓ 仍可用 |
| 25 | GET /api/work-order/list | GET /api/work-order/list | ✓ 仍可用 |
| 26 | POST /api/work-order/create | POST /api/work-order | 创建工单 |
| 27 | GET /api/service-request/list | GET /api/service-request/list | ✓ 仍可用 |
| 28 | POST /api/service-request/create | POST /api/service-request | 提交申请 |
| 29 | GET /api/monitor-request/list | 无此通用接口 | 使用 /list/family 或 /list/staff |
| 30 | POST /api/monitor-request/approve | POST /api/monitor-request/{id}/approve | 同意/拒绝 |
| 31 | GET /api/notification/list | GET /api/notification/list | ✓ 仍可用 |
| 32 | GET /api/ai-advice/list | 无此接口 | 使用 POST /api/cloud-agent/advice 写入 |
| 33 | GET /api/local-agent/list | 无此接口 | 仅心跳/状态上报 |
| 34 | GET /api/cloud-agent/list | 无此接口 | 仅注册/状态上报 |
| 35 | GET /api/music-intervention/list | GET /api/intervention/list | 干预记录 |
| 36 | GET /api/home-control-log/list | 无此接口 | 使用 POST /api/local-agent/control 写入 |
| 37 | GET /api/conversation/list | GET /api/cloud-agent/conversations | 智能体对话 |
| 38 | GET /api/intent-log/list | 无此接口 | 使用 POST /api/local-agent/intent 写入 |
| 39 | GET /api/alarm-process/list | 无此接口 | 告警处理记录已内嵌 AlarmDto |

---

## 二、已废弃的模拟接口（全部不可用）

> ⚠️ 以下 7 个接口曾为早期原型阶段使用的 mock 接口，**当前代码中已不存在**。请勿调用。

1. ~~GET /api/profile~~ → 使用 `GET /api/elder/{elderId}`
2. ~~PATCH /api/profile~~ → 使用 `PUT /api/elder/{elderId}`
3. ~~GET /api/vitals/latest~~ → 使用 `GET /api/vital-signs/latest/{elderId}`
4. ~~GET /api/vitals/history~~ → 使用 `GET /api/vital-signs/heart-rate/list` 等
5. ~~GET /api/alerts~~ → 使用 `GET /api/alarm/list`
6. ~~GET /api/alerts/latest~~ → 使用 `GET /api/alarm/list?page=1&pageSize=1`
7. ~~POST /api/ai/analyze~~ → 使用 `POST /api/ai/health-analysis`

---

## 三、数据库表与API完整映射（更新）

| 数据库表 | 接口数 | 主要接口路径 |
|---------|--------|---------|
| elder_user | 6 | /api/elder/list, /api/elder/{id}, /api/elder/detail/{id}, /api/elder/bound |
| device | 7 | /api/device/list, /api/device/{id}, /api/device/register, /api/device/{id}/status, /api/device/{id}/sensor-data |
| sensor_data | 5 | /api/device/{id}/sensor-data, /api/device/sensor/temperature-humidity, /api/device/sensor/{type} |
| blood_pressure | 2 | /api/vital-signs/blood-pressure/list, /api/vital-signs/blood-pressure/latest |
| alarm_event | 9 | /api/alarm/list, /api/alarm/{id}, /api/alarm/{id}/acknowledge, /api/alarm/{id}/resolve |
| local_agent | 7 | /api/local-agent/heartbeat, /api/local-agent/data, /api/local-agent/alarm-report |
| cloud_agent | 10 | /api/cloud-agent/register, /api/cloud-agent/report, /api/cloud-agent/chat |
| music_intervention | 5 | /api/intervention/list, /api/intervention/{id}, /api/intervention (合并到 intervention) |
| home_control_log | 1 | /api/local-agent/control (仅写入，无列表查询) |
| agent_conversation | 2 | /api/cloud-agent/conversations, /api/cloud-agent/conversations/{id} |
| agent_intent_log | 1 | /api/local-agent/intent (仅写入，无列表查询) |
| staff_user | 3 | /api/auth/login/web, /api/staff/list, /api/staff |
| family_user | 2 | /api/auth/login/app, /api/auth/me |
| emergency_contact | 5 | /api/emergency-contact/list, /api/emergency-contact, /api/emergency-contact/{id} |
| health_record | 3 | /api/health-record/by-elder/{id}, /api/health-record/{id}, /api/health-record |
| sos_record | 4 | /api/sos, /api/sos/{id}, /api/sos/list, /api/sos/{id}/handle |
| work_order | 5 | /api/work-order/list, /api/work-order/{id}, /api/work-order |
| service_request | 6 | /api/service-request, /api/service-request/my-list, /api/service-request/list |
| monitor_request | 8 | /api/monitor-request, /api/monitor-request/list/family, /api/monitor-request/list/staff |
| notification | 4 | /api/notification/list, /api/notification/{id}/read, /api/notification/read-all |
| ai_advice | 1 | /api/cloud-agent/advice (仅写入) |
| ai_service_record | 4 | /api/ai-service/record, /api/ai-service/record/list |
| **合计22张表** | **~110+个接口** | 详见 `anxinban-api-reference-manual-v3.1.md` |

---

## 四、通用响应格式

### 成功
```json
{"code": 200, "message": "success", "data": {...}}
```

### 分页
```json
{"code": 200, "message": "success", "data": {"total": 100, "page": 1, "size": 20, "list": [...]}}
```

### 错误
```json
{"code": 400, "message": "具体错误描述"}
```

---

## 五、错误码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 201 | 创建成功 |
| 204 | 无内容 |
| 400 | 参数错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 不存在 |
| 500 | 服务器错误 |

---

## 六、数据库数据验证

### 数据库脚本含22张表，每表均有>=3条完整模拟数据

```sql
-- 验证行数
SELECT 'elder_user', COUNT(*) FROM elder_user UNION ALL
SELECT 'device', COUNT(*) FROM device UNION ALL
SELECT 'sensor_data', COUNT(*) FROM sensor_data UNION ALL
SELECT 'blood_pressure', COUNT(*) FROM blood_pressure UNION ALL
SELECT 'alarm_event', COUNT(*) FROM alarm_event UNION ALL
SELECT 'local_agent', COUNT(*) FROM local_agent UNION ALL
SELECT 'cloud_agent', COUNT(*) FROM cloud_agent UNION ALL
SELECT 'music_intervention', COUNT(*) FROM music_intervention UNION ALL
SELECT 'home_control_log', COUNT(*) FROM home_control_log UNION ALL
SELECT 'agent_conversation', COUNT(*) FROM agent_conversation UNION ALL
SELECT 'agent_intent_log', COUNT(*) FROM agent_intent_log UNION ALL
SELECT 'staff_user', COUNT(*) FROM staff_user UNION ALL
SELECT 'family_user', COUNT(*) FROM family_user UNION ALL
SELECT 'emergency_contact', COUNT(*) FROM emergency_contact UNION ALL
SELECT 'health_record', COUNT(*) FROM health_record UNION ALL
SELECT 'sos_record', COUNT(*) FROM sos_record UNION ALL
SELECT 'work_order', COUNT(*) FROM work_order UNION ALL
SELECT 'service_request', COUNT(*) FROM service_request UNION ALL
SELECT 'monitor_request', COUNT(*) FROM monitor_request UNION ALL
SELECT 'notification', COUNT(*) FROM notification UNION ALL
SELECT 'ai_advice', COUNT(*) FROM ai_advice UNION ALL
SELECT 'alarm_process', COUNT(*) FROM alarm_process;
```

### 数据库脚本已包含所有模拟数据，直接导入MySQL即可使用。
