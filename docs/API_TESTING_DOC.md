# 安心伴智慧养老守护系统 — API 接口完整测试文档

> **基地址**：http://localhost:8080  
> **后端框架**：Spring Boot 2.7 / Java 17  
> **端口**：8080  
> **数据库**：MySQL 8.0+（22张表）  
> **文档版本**：v2.0（完整版，覆盖全部数据库表）

---

## 一、接口总览

| 序号 | 模块 | 方法 | 路径 | 说明 | 对应数据库表 |
|------|------|------|------|------|-------------|
| 1 | 个人信息 | GET | /api/profile | 获取老人基本信息 | elder_user |
| 2 | 个人信息 | PATCH | /api/profile | 更新老人基本信息 | elder_user |
| 3 | 实时体征 | GET | /api/vitals/latest | 获取最新体征 | sensor_data |
| 4 | 体征趋势 | GET | /api/vitals/history?period= | 体征历史趋势 | sensor_data |
| 5 | 告警列表 | GET | /api/alerts | 获取所有告警 | alarm_event |
| 6 | 最新告警 | GET | /api/alerts/latest | 获取最新告警 | alarm_event |
| 7 | AI 分析 | POST | /api/ai/analyze | 健康数据分析 | sensor_data, blood_pressure |
| 8 | 老人列表 | GET | /api/elder/list | 获取所有老人 | elder_user |
| 9 | 老人详情 | GET | /api/elder/{elderId} | 单个老人信息 | elder_user |
| 10 | 更新老人 | PUT | /api/elder/{elderId} | 更新老人信息 | elder_user |
| 11 | 设备列表 | GET | /api/device/list | 获取所有设备 | device |
| 12 | 设备详情 | GET | /api/device/{deviceId} | 单个设备信息 | device |
| 13 | 设备状态 | PATCH | /api/device/{deviceId}/status | 更新设备状态 | device |
| 14 | 传感器数据 | GET | /api/sensor-data/list | 传感器历史数据 | sensor_data |
| 15 | 血压记录 | GET | /api/blood-pressure/list | 血压历史记录 | blood_pressure |
| 16 | 告警列表(分页) | GET | /api/alarm/list | 分页告警列表 | alarm_event |
| 17 | 处理告警 | POST | /api/alarm/{alarmId}/handle | 处理告警 | alarm_event |
| 18 | 用户登录 | POST | /api/auth/login | 登录认证 | staff_user, family_user |
| 19 | 员工列表 | GET | /api/staff/list | 获取员工 | staff_user |
| 20 | 家属列表 | GET | /api/family/list | 获取家属 | family_user |
| 21 | 紧急联系人 | GET | /api/emergency-contact/list | 紧急联系人 | emergency_contact |
| 22 | 健康档案 | GET | /api/health-record/{elderId} | 健康档案 | health_record |
| 23 | 保存健康档案 | PUT | /api/health-record/{elderId} | 保存档案 | health_record |
| 24 | SOS记录 | GET | /api/sos/list | SOS记录列表 | sos_record |
| 25 | 工单列表 | GET | /api/work-order/list | 工单管理 | work_order |
| 26 | 创建工单 | POST | /api/work-order/create | 创建工单 | work_order |
| 27 | 服务申请 | GET | /api/service-request/list | 服务申请列表 | service_request |
| 28 | 提交服务申请 | POST | /api/service-request/create | 提交申请 | service_request |
| 29 | 监控申请 | GET | /api/monitor-request/list | 监控申请列表 | monitor_request |
| 30 | 审批监控申请 | POST | /api/monitor-request/approve | 同意/拒绝 | monitor_request |
| 31 | 通知列表 | GET | /api/notification/list | 通知列表 | notification |
| 32 | AI建议历史 | GET | /api/ai-advice/list | AI建议记录 | ai_advice |
| 33 | 本地智能体 | GET | /api/local-agent/list | 本地智能体列表 | local_agent |
| 34 | 云端智能体 | GET | /api/cloud-agent/list | 云端智能体列表 | cloud_agent |
| 35 | 音乐干预记录 | GET | /api/music-intervention/list | 干预记录 | music_intervention |
| 36 | 家居控制日志 | GET | /api/home-control-log/list | 控制日志 | home_control_log |
| 37 | 对话记录 | GET | /api/conversation/list | 智能体对话 | agent_conversation |
| 38 | 意图日志 | GET | /api/intent-log/list | 意图识别日志 | agent_intent_log |
| 39 | 告警处理记录 | GET | /api/alarm-process/list | 处理记录 | alarm_process |

---

## 二、已实现接口（7个）

### 1. GET /api/profile
> 获取老人基本信息。无参数。

**响应示例**（200 OK）：
```json
{
  "name": "张大爷",
  "age": 78,
  "gender": "男",
  "familyPhone": "13800000000",
  "address": "未填写"
}
```

### 2. PATCH /api/profile
> 更新个人信息字段。

**请求体**：
```json
{"field": "name", "value": "李大爷"}
```

**field可选值**：name / age / gender / familyPhone / address  
**校验规则**：name(2-20字符), age(1-120), gender(男/女), familyPhone(11位手机号), address(2-120字符)

### 3. GET /api/vitals/latest
> 最新体征数据（模拟生成，每次不同）。

**响应**：
```json
{
  "temperature": 36.5,
  "heartRate": 72,
  "systolic": 125,
  "diastolic": 82,
  "measuredAt": "2026-06-09T14:30:00Z"
}
```

### 4. GET /api/vitals/history?period=week
> 体征历史趋势。period = day / week / month

### 5. GET /api/alerts
> 所有告警列表（内置3条模拟数据）。

### 6. GET /api/alerts/latest
> 最新告警；无告警返回204。

### 7. POST /api/ai/analyze
> AI健康分析。请求体：{"period": "week"}
> 配置 OPENAI_API_KEY 后可调用ChatGPT。

---

## 三、数据库表与API完整映射

| 数据库表 | 接口数 | 接口路径 |
|---------|--------|---------|
| elder_user | 3 | /api/profile, /api/elder/list, /api/elder/{id} |
| device | 3 | /api/device/list, /api/device/{id}, /api/device/{id}/status |
| sensor_data | 3 | /api/vitals/latest, /api/vitals/history, /api/sensor-data/list |
| blood_pressure | 1 | /api/blood-pressure/list |
| alarm_event | 3 | /api/alerts, /api/alarm/list, /api/alarm/{id}/handle |
| local_agent | 1 | /api/local-agent/list |
| cloud_agent | 1 | /api/cloud-agent/list |
| music_intervention | 1 | /api/music-intervention/list |
| home_control_log | 1 | /api/home-control-log/list |
| agent_conversation | 1 | /api/conversation/list |
| agent_intent_log | 1 | /api/intent-log/list |
| staff_user | 2 | /api/auth/login, /api/staff/list |
| family_user | 2 | /api/auth/login, /api/family/list |
| emergency_contact | 1 | /api/emergency-contact/list |
| health_record | 2 | /api/health-record/{elderId} |
| sos_record | 1 | /api/sos/list |
| work_order | 2 | /api/work-order/list, /api/work-order/create |
| service_request | 2 | /api/service-request/list, /api/service-request/create |
| monitor_request | 2 | /api/monitor-request/list, /api/monitor-request/approve |
| notification | 1 | /api/notification/list |
| ai_advice | 1 | /api/ai-advice/list |
| alarm_process | 1 | /api/alarm-process/list |
| **合计22张表** | **38个接口** | |

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
