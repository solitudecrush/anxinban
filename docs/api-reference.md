# 安心伴（Anxinban）后端 RESTful API 接口参考手册

> **公网地址**: `http://120.27.129.78:8080`
> **更新日期**: 2026-06-20
> **统一响应格式**: `{"code": 200, "message": "success", "data": {...}}`
> **Content-Type**: 所有 POST/PUT 请求需 `Content-Type: application/json`，文件上传用 `multipart/form-data`

---

## 1. 健康检查

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 1 | 健康检查 | GET | `/api/health` | 检测服务与数据库连接状态 |

```bash
curl -s http://120.27.129.78:8080/api/health
```

预期返回：
```json
{
  "code": 200, "message": "success",
  "data": {
    "service": "anxinban-backend",
    "status": "running",
    "runtime": "spring-boot",
    "database": "connected"
  }
}
```

---

## 2. 认证授权

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 2 | 用户登录 | POST | `/api/auth/login` | 手机号+密码登录 |
| 3 | 用户注册 | POST | `/api/auth/register` | 新用户注册 |
| 4 | 重置密码 | POST | `/api/auth/reset-password` | 根据手机号重置密码 |
| 5 | 用户登出 | POST | `/api/auth/logout` | 登出 |
| 6 | 当前用户信息 | GET | `/api/auth/me?phone=13900000000` | 根据手机号查用户信息 |

### 2.1 登录

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900000000","password":"123456","userType":"staff"}'
```

### 2.2 注册

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900000001","password":"123456","name":"测试用户","userType":"family"}'
```

### 2.3 重置密码

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900000000","newPassword":"654321"}'
```

### 2.4 登出

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/logout
```

### 2.5 当前用户信息

```bash
curl -s "http://120.27.129.78:8080/api/auth/me?phone=13900000000"
```

---

## 3. 仪表盘

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 7 | 仪表盘统计 | GET | `/api/dashboard/stats` | 核心统计指标 |
| 8 | 大屏摘要 | GET | `/api/dashboard/summary` | 兼容大屏展示格式 |
| 9 | 楼栋列表 | GET | `/api/dashboard/buildings` | 返回楼栋名称列表 |

### 3.1 仪表盘统计

```bash
curl -s http://120.27.129.78:8080/api/dashboard/stats
```

### 3.2 大屏摘要（实时统计）

```bash
curl -s http://120.27.129.78:8080/api/dashboard/summary
```

预期返回：
```json
{
  "code": 200, "message": "success",
  "data": {
    "elder_count": 6,
    "high_risk_count": 2,
    "pending_alarm_count": 16,
    "total_device_count": 17,
    "online_device_count": 16,
    "offline_device_count": 1,
    "work_order_count": 27,
    "month_work_order_count": 22
  }
}
```

### 3.3 楼栋列表

```bash
curl -s http://120.27.129.78:8080/api/dashboard/buildings
```

---

## 4. 告警管理（`/api/alarm` — 旧版）

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 10 | 创建告警 | POST | `/api/alarm` | 创建新告警 |
| 11 | 告警详情 | GET | `/api/alarm/{alarmId}` | 查询单条告警 |
| 12 | 告警列表（分页） | GET | `/api/alarm/list` | 支持多条件筛选 |
| 13 | 入侵告警列表 | GET | `/api/alarm/intrusion/list` | 入侵类告警分页 |
| 14 | 入侵抓拍快照 | GET | `/api/alarm/intrusion/{alarmId}/snapshot` | 获取入侵抓拍URL |
| 15 | 确认告警 | PUT | `/api/alarm/{alarmId}/acknowledge` | 确认处理告警 |
| 16 | 解决告警 | PUT | `/api/alarm/{alarmId}/resolve` | 标记告警已解决 |
| 17 | 标记已读 | PUT | `/api/alarm/{alarmId}/read` | 标记告警为已读 |
| 18 | 未读数量 | GET | `/api/alarm/unread-count?elderId=elder_001` | 查询未读告警数 |

### 4.1 创建告警

```bash
curl -s -X POST http://120.27.129.78:8080/api/alarm \
  -H "Content-Type: application/json" \
  -d '{
    "alarmId": "alarm_test_01",
    "elderId": "elder_001",
    "deviceId": "dev_001",
    "alarmType": "health_abnormal",
    "alarmLevel": "high",
    "alarmStatus": "pending",
    "description": "心率异常测试告警"
  }'
```

### 4.2 告警详情

```bash
curl -s http://120.27.129.78:8080/api/alarm/alarm_001
```

### 4.3 告警列表（分页筛选）

```bash
# 基本分页
curl -s "http://120.27.129.78:8080/api/alarm/list?page=1&pageSize=10"

# 按条件筛选
curl -s "http://120.27.129.78:8080/api/alarm/list?elderId=elder_001&status=pending&page=1&pageSize=10"
```

可选参数：`elderId`, `deviceId`, `alarmType`, `status`, `startTime`, `endTime`, `page`, `pageSize`

### 4.4 入侵告警列表

```bash
curl -s "http://120.27.129.78:8080/api/alarm/intrusion/list?page=1&pageSize=10"
```

### 4.5 入侵抓拍快照

```bash
curl -s http://120.27.129.78:8080/api/alarm/intrusion/alarm_003/snapshot
```

### 4.6 确认告警

```bash
curl -s -X PUT http://120.27.129.78:8080/api/alarm/alarm_001/acknowledge \
  -H "Content-Type: application/json" \
  -d '{"handler":"staff_001","handleTime":"2026-06-20 10:00:00"}'
```

### 4.7 解决告警

```bash
curl -s -X PUT http://120.27.129.78:8080/api/alarm/alarm_001/resolve \
  -H "Content-Type: application/json" \
  -d '{"handler":"staff_001","handleTime":"2026-06-20 10:30:00","remark":"已上门确认，老人无大碍"}'
```

### 4.8 标记已读

```bash
curl -s -X PUT http://120.27.129.78:8080/api/alarm/alarm_001/read
```

### 4.9 未读数量

```bash
curl -s "http://120.27.129.78:8080/api/alarm/unread-count?elderId=elder_001"
```

---

## 5. 告警管理（`/api/alarms` — 新版兼容层，snake_case）

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 19 | 告警列表（兼容） | GET | `/api/alarms` | 返回全部告警 |
| 20 | 告警详情（兼容） | GET | `/api/alarms/{alarm_id}` | 单条告警详情 |
| 21 | 告警转工单 | POST | `/api/alarms/{alarm_id}/to-work-order` | 告警转为工单 |
| 22 | 标记已读（兼容） | POST | `/api/alarms/{alarm_id}/mark-read` | 标记告警已读 |
| 23 | 处理告警（兼容） | POST | `/api/alarms/{alarm_id}/handle` | 标记告警已处理 |

### 5.1 告警列表（兼容）

```bash
curl -s http://120.27.129.78:8080/api/alarms
```

### 5.2 告警详情（兼容）

```bash
curl -s http://120.27.129.78:8080/api/alarms/alarm_001
```

### 5.3 告警转工单

```bash
curl -s -X POST http://120.27.129.78:8080/api/alarms/alarm_001/to-work-order
```

### 5.4 标记已读（兼容）

```bash
curl -s -X POST http://120.27.129.78:8080/api/alarms/alarm_001/mark-read
```

### 5.5 处理告警（兼容）

```bash
curl -s -X POST http://120.27.129.78:8080/api/alarms/alarm_001/handle \
  -H "Content-Type: application/json" \
  -d '{"handler_name":"张工","handler":"staff_001","remark":"已现场处理完毕","status":"handled"}'
```

---

## 6. 工单管理（`/api/work-order` — 旧版）

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 24 | 工单列表（分页） | GET | `/api/work-order/list` | 支持关键词/状态筛选 |
| 25 | 工单详情 | GET | `/api/work-order/{orderId}` | 查询单条工单 |
| 26 | 创建工单 | POST | `/api/work-order` | 创建新工单 |
| 27 | 更新工单状态 | PUT | `/api/work-order/{orderId}/status` | 修改工单状态 |
| 28 | 指派处理人 | PUT | `/api/work-order/{orderId}/assign` | 分配处理人 |

### 6.1 工单列表

```bash
curl -s "http://120.27.129.78:8080/api/work-order/list?page=1&pageSize=10"
curl -s "http://120.27.129.78:8080/api/work-order/list?status=待处理&page=1&pageSize=10"
```

### 6.2 工单详情

```bash
curl -s http://120.27.129.78:8080/api/work-order/wo_001
```

### 6.3 创建工单

```bash
curl -s -X POST http://120.27.129.78:8080/api/work-order \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "orderType": "设备维修",
    "description": "老人卧室智能灯不亮，需上门检修",
    "creatorId": "staff_001"
  }'
```

### 6.4 更新工单状态

```bash
curl -s -X PUT http://120.27.129.78:8080/api/work-order/wo_001/status \
  -H "Content-Type: application/json" \
  -d '{"status":"已完成"}'
```

### 6.5 指派处理人

```bash
curl -s -X PUT http://120.27.129.78:8080/api/work-order/wo_001/assign \
  -H "Content-Type: application/json" \
  -d '{"handlerId":"staff_003","handlerName":"王强"}'
```

---

## 7. 工单管理（`/api/work-orders` — 新版兼容层，snake_case）

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 29 | 工单列表（兼容） | GET | `/api/work-orders` | 返回全部工单 |
| 30 | 创建工单（兼容） | POST | `/api/work-orders` | 创建新工单 |
| 31 | 指派处理人（兼容） | PUT | `/api/work-orders/{work_order_id}/assign` | 分配处理人 |
| 32 | 完成工单（兼容） | PUT | `/api/work-orders/{work_order_id}/complete` | 标记已完成 |

### 7.1 工单列表（兼容）

```bash
curl -s http://120.27.129.78:8080/api/work-orders
```

### 7.2 创建工单（兼容）

```bash
curl -s -X POST http://120.27.129.78:8080/api/work-orders \
  -H "Content-Type: application/json" \
  -d '{
    "elder_id": "elder_001",
    "order_type": "健康关注",
    "description": "系统自动生成：心率持续偏高",
    "creator_id": "system"
  }'
```

### 7.3 指派处理人（兼容）

```bash
curl -s -X PUT http://120.27.129.78:8080/api/work-orders/wo_001/assign \
  -H "Content-Type: application/json" \
  -d '{"handler_id":"staff_003","handler_name":"王强"}'
```

### 7.4 完成工单（兼容）

```bash
curl -s -X PUT http://120.27.129.78:8080/api/work-orders/wo_001/complete
```

---

## 8. 老人管理（`/api/elder` — 旧版）

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 33 | 老人列表（分页） | GET | `/api/elder/list` | 支持筛选 |
| 34 | 创建老人 | POST | `/api/elder` | 新建老人档案 |
| 35 | 老人详情 | GET | `/api/elder/{elderId}` | 基本信息 |
| 36 | 更新老人 | PUT | `/api/elder/{elderId}` | 修改老人信息 |
| 37 | 删除老人 | DELETE | `/api/elder/{elderId}` | 删除老人档案 |
| 38 | 老人综合详情 | GET | `/api/elder/detail/{elderId}` | 含健康/设备/告警/工单 |
| 39 | 家属绑定老人 | GET | `/api/elder/bound?familyId=fam_001` | 家属查绑定老人 |
| 40 | 老人名下设备 | GET | `/api/elder/{elderId}/devices` | 设备列表 |
| 41 | 实时健康数据 | GET | `/api/elder/{elderId}/health/realtime` | 最新健康指标 |
| 42 | 健康历史趋势 | GET | `/api/elder/{elderId}/health/history?type=heart_rate&range=week` | 趋势数据 |
| 43 | 摄像头流地址 | GET | `/api/elder/{elderId}/camera-stream?staffId=staff_001` | 获取摄像头流 |

### 8.1 老人列表

```bash
curl -s "http://120.27.129.78:8080/api/elder/list?page=1&pageSize=10"
curl -s "http://120.27.129.78:8080/api/elder/list?healthStatus=danger&page=1&pageSize=10"
```

可选参数：`name`, `building`, `roomNumber`, `healthStatus`, `page`, `pageSize`

### 8.2 创建老人

```bash
curl -s -X POST http://120.27.129.78:8080/api/elder \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_010",
    "name": "周八",
    "age": 76,
    "gender": "男",
    "phone": "13900000100",
    "building": "2号楼",
    "roomNumber": "2-101",
    "healthStatus": "normal"
  }'
```

### 8.3 老人详情

```bash
curl -s http://120.27.129.78:8080/api/elder/elder_001
```

### 8.4 更新老人

```bash
curl -s -X PUT http://120.27.129.78:8080/api/elder/elder_010 \
  -H "Content-Type: application/json" \
  -d '{"healthStatus":"warning"}'
```

### 8.5 删除老人

```bash
curl -s -X DELETE http://120.27.129.78:8080/api/elder/elder_010
```

### 8.6 老人综合详情（含健康/设备/告警/工单）

```bash
curl -s http://120.27.129.78:8080/api/elder/detail/elder_001
```

### 8.7 家属绑定老人

```bash
curl -s "http://120.27.129.78:8080/api/elder/bound?familyId=fam_001"
```

### 8.8 老人名下设备

```bash
curl -s http://120.27.129.78:8080/api/elder/elder_001/devices
```

### 8.9 实时健康数据

```bash
curl -s http://120.27.129.78:8080/api/elder/elder_001/health/realtime
```

### 8.10 健康历史趋势

```bash
curl -s "http://120.27.129.78:8080/api/elder/elder_001/health/history?type=heart_rate&range=week"
```

`type`: `heart_rate`, `spo2`, `temperature`, `blood_pressure` | `range`: `day`, `week`, `month`

### 8.11 摄像头流地址

```bash
curl -s "http://120.27.129.78:8080/api/elder/elder_001/camera-stream?staffId=staff_001"
```

---

## 9. 老人管理（`/api/elders` — 新版兼容层，snake_case）

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 44 | 老人列表（兼容） | GET | `/api/elders?page=1&pageSize=20` | 分页，snake_case |

### 9.1 老人列表（兼容）

```bash
curl -s "http://120.27.129.78:8080/api/elders?page=1&pageSize=20"
```

---

## 10. 健康数据

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 45 | 最新健康数据 | GET | `/api/health/latest/{elder_id}` | 指定老人最新指标 |

### 10.1 最新健康数据

```bash
curl -s http://120.27.129.78:8080/api/health/latest/elder_001
```

预期返回：
```json
{
  "code": 200, "message": "success",
  "data": {
    "elder_id": "elder_001",
    "heart_rate": 112.0,
    "spo2": 91.0,
    "temperature": 36.5,
    "blood_pressure": "140/85",
    "activity_status": "长时间静止",
    "fall_status": "疑似跌倒",
    "created_at": "2026-06-20T09:23:12"
  }
}
```

---

## 11. AI 服务

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 46 | AI 服务状态 | GET | `/api/ai/service-status` | 查看 AI 服务可达性 |
| 47 | 最近 AI 分析 | GET | `/api/ai/latest-analysis/{elderId}` | 某老人最近一次AI分析 |
| 48 | AI 分析记录 | GET | `/api/ai/analysis-records?elder_id=elder_001&page=1&pageSize=10` | 分页历史记录 |
| 49 | AI 健康风险分析 | POST | `/api/ai/health-analysis` | 健康风险分析 |
| 50 | AI 快速对话 | POST | `/api/ai/chat/quick` | 本地即时陪伴 |
| 51 | AI 深度对话 | POST | `/api/ai/chat/deep` | 云端深度陪伴 |
| 52 | RAG 知识问答 | POST | `/api/ai/rag-query` | 养老知识问答 |
| 53 | AI 视觉分析 | POST | `/api/ai/vision-analysis` | 跌倒/行为检测 |
| 54 | AI 聊天（异步） | POST | `/api/ai/chat?userId=user_001&content=你好` | AI 智能体对话 |
| 55 | AI 查找物品 | POST | `/api/ai/find-item?userId=user_001&itemName=钥匙` | AI 查找物品 |

### 11.1 AI 服务状态

```bash
curl -s http://120.27.129.78:8080/api/ai/service-status
```

### 11.2 最近 AI 分析

```bash
curl -s http://120.27.129.78:8080/api/ai/latest-analysis/elder_001
```

### 11.3 AI 分析记录（分页）

```bash
curl -s "http://120.27.129.78:8080/api/ai/analysis-records?elder_id=elder_001&page=1&pageSize=10"
```

### 11.4 AI 健康风险分析

```bash
curl -s -X POST http://120.27.129.78:8080/api/ai/health-analysis \
  -H "Content-Type: application/json" \
  -d '{
    "elder_id": "elder_001",
    "recent_health": {
      "heart_rate": 112,
      "spo2": 91,
      "temperature": 36.5
    }
  }'
```

### 11.5 AI 快速对话

```bash
curl -s -X POST http://120.27.129.78:8080/api/ai/chat/quick \
  -H "Content-Type: application/json" \
  -d '{"elder_id":"elder_001","message":"我今天有点不舒服"}'
```

### 11.6 AI 深度对话

```bash
curl -s -X POST http://120.27.129.78:8080/api/ai/chat/deep \
  -H "Content-Type: application/json" \
  -d '{"elder_id":"elder_001","message":"今天天气怎么样"}'
```

### 11.7 RAG 知识问答

```bash
curl -s -X POST http://120.27.129.78:8080/api/ai/rag-query \
  -H "Content-Type: application/json" \
  -d '{"query":"老年人如何预防跌倒","elder_id":"elder_001","top_k":3}'
```

### 11.8 AI 视觉分析

```bash
curl -s -X POST http://120.27.129.78:8080/api/ai/vision-analysis \
  -H "Content-Type: application/json" \
  -d '{"elder_id":"elder_001","image_url":"http://example.com/img.jpg","analysis_type":"fall_detection"}'
```

### 11.9 AI 聊天（异步）

```bash
curl -s -X POST "http://120.27.129.78:8080/api/ai/chat?userId=user_001&content=你好"
```

### 11.10 AI 查找物品

```bash
curl -s -X POST "http://120.27.129.78:8080/api/ai/find-item?userId=user_001&itemName=钥匙&room=living-room"
```

---

## 12. 智能体上下文

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 56 | 智能体上下文 | GET | `/api/agent/context?elderId=elder_001` | 获取老人聚合上下文，用于AI助手/智能管家 |

### 12.1 智能体上下文

```bash
curl -s "http://120.27.129.78:8080/api/agent/context?elderId=elder_001"
```

---

## 13. 设备管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 56 | 注册设备 | POST | `/api/device/register` | 注册新设备 |
| 57 | 设备详情 | GET | `/api/device/{deviceId}` | 查询设备 |
| 58 | 设备列表 | GET | `/api/device/list` | 分页筛选 |
| 59 | 更新设备状态 | PUT | `/api/device/{deviceId}/status` | 上线/离线 |
| 60 | 上传传感器数据 | POST | `/api/device/{deviceId}/sensor-data` | 设备上报数据 |
| 61 | 下发控制指令 | POST | `/api/device/{deviceId}/command` | 远程控制设备 |
| 62 | 查询传感器数据 | GET | `/api/device/{deviceId}/sensor-data` | 历史数据查询 |

### 12.1 注册设备

```bash
curl -s -X POST http://120.27.129.78:8080/api/device/register \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": "dev_test_01",
    "deviceType": "手环",
    "elderId": "elder_001",
    "location": "1号楼 1-201"
  }'
```

### 12.2 设备详情

```bash
curl -s http://120.27.129.78:8080/api/device/dev_001
```

### 12.3 设备列表

```bash
curl -s "http://120.27.129.78:8080/api/device/list?page=1&pageSize=10"
```

可选参数：`status` (`online`/`offline`), `deviceType`, `location`, `page`, `pageSize`

### 12.4 更新设备状态

```bash
curl -s -X PUT http://120.27.129.78:8080/api/device/dev_001/status \
  -H "Content-Type: application/json" \
  -d '{"status":"online","lastHeartbeat":"2026-06-20T10:00:00"}'
```

### 12.5 上传传感器数据

```bash
curl -s -X POST http://120.27.129.78:8080/api/device/dev_001/sensor-data \
  -H "Content-Type: application/json" \
  -d '{
    "sensorDataList": [
      {"sensorType":"heart_rate","value":75,"unit":"bpm","timestamp":"2026-06-20T10:00:00"},
      {"sensorType":"spo2","value":98,"unit":"%","timestamp":"2026-06-20T10:00:00"}
    ]
  }'
```

### 12.6 下发控制指令

```bash
curl -s -X POST http://120.27.129.78:8080/api/device/dev_001/command \
  -H "Content-Type: application/json" \
  -d '{"commandType":"light_on","parameters":{"brightness":80}}'
```

### 12.7 查询传感器数据

```bash
curl -s "http://120.27.129.78:8080/api/device/dev_001/sensor-data?sensorType=heart_rate&page=1&pageSize=20"
```

---

## 14. 设备上传（兼容层）

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 63 | 设备健康数据上传 | POST | `/api/device/upload` | 上传健康数据，自动分析并生成告警 |

### 13.1 设备健康数据上传

```bash
curl -s -X POST http://120.27.129.78:8080/api/device/upload \
  -H "Content-Type: application/json" \
  -d '{
    "elder_id": "elder_001",
    "heart_rate": 112,
    "spo2": 91,
    "temperature": 36.5,
    "activity_status": "长时间静止",
    "fall_status": "正常"
  }'
```

---

## 15. 传感器历史数据

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 64 | 温湿度历史 | GET | `/api/device/sensor/temperature-humidity` | 温湿度历史 |
| 65 | 指定类型传感器历史 | GET | `/api/device/sensor/{sensorType}` | 按类型查询 |
| 66 | 设备告警历史 | GET | `/api/device/alarm` | 告警事件分页 |

### 14.1 温湿度历史

```bash
curl -s "http://120.27.129.78:8080/api/device/sensor/temperature-humidity?limit=20"
```

### 14.2 指定传感器类型历史

```bash
curl -s "http://120.27.129.78:8080/api/device/sensor/heart_rate?limit=20"
```

### 14.3 设备告警历史

```bash
curl -s "http://120.27.129.78:8080/api/device/alarm?alarmStatus=pending&page=0&size=10"
```

---

## 16. 设备模拟器

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 67 | 模拟器状态 | GET | `/api/device/simulator/status` | 查看运行状态 |
| 68 | 启动模拟器 | POST | `/api/device/simulator/start` | 启动 |
| 69 | 停止模拟器 | POST | `/api/device/simulator/stop` | 停止 |
| 70 | 触发指纹成功 | POST | `/api/device/simulator/trigger/fingerprint-success?userId=user_001` | 模拟指纹开门成功 |
| 71 | 触发指纹失败 | POST | `/api/device/simulator/trigger/fingerprint-fail` | 模拟指纹开门失败 |
| 72 | 触发窗帘控制 | POST | `/api/device/simulator/trigger/curtain?command=open&percent=50` | 模拟窗帘 |
| 73 | 触发蜂鸣器 | POST | `/api/device/simulator/trigger/buzzer?command=beep&reason=测试` | 模拟蜂鸣 |
| 74 | 触发灯光控制 | POST | `/api/device/simulator/trigger/light?command=on&brightness=80` | 模拟灯光 |
| 75 | 触发手表紧急呼救 | POST | `/api/device/simulator/trigger/watch-emergency` | 模拟手表SOS |
| 76 | 触发跌倒检测 | POST | `/api/device/simulator/trigger/fall-detection?room=living-room` | 模拟跌倒 |
| 77 | 触发查找物品 | POST | `/api/device/simulator/trigger/find-item?itemName=钥匙&room=living-room` | 模拟查找物品 |

### 15.1 模拟器状态

```bash
curl -s http://120.27.129.78:8080/api/device/simulator/status
```

### 15.2 启动/停止模拟器

```bash
curl -s -X POST http://120.27.129.78:8080/api/device/simulator/start
curl -s -X POST http://120.27.129.78:8080/api/device/simulator/stop
```

### 15.3 触发指纹事件

```bash
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/fingerprint-success?userId=user_001"
curl -s -X POST http://120.27.129.78:8080/api/device/simulator/trigger/fingerprint-fail
```

### 15.4 触发窗帘控制

```bash
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/curtain?command=open&percent=50"
```

### 15.5 触发蜂鸣器

```bash
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/buzzer?command=beep&reason=测试鸣响"
```

### 15.6 触发灯光控制

```bash
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/light?command=on&brightness=80"
```

### 15.7 触发手表紧急呼救

```bash
curl -s -X POST http://120.27.129.78:8080/api/device/simulator/trigger/watch-emergency
```

### 15.8 触发跌倒检测

```bash
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/fall-detection?room=living-room"
```

### 15.9 触发查找物品

```bash
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/find-item?itemName=钥匙&room=living-room"
```

---

## 17. 文件上传

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 78 | 上传头像 | POST | `/api/upload/avatar` | multipart/form-data |
| 79 | 上传抓拍快照 | POST | `/api/upload/snapshot` | multipart/form-data |

### 16.1 上传头像

```bash
curl -s -X POST http://120.27.129.78:8080/api/upload/avatar \
  -F "file=@/path/to/avatar.jpg"
```

### 16.2 上传抓拍快照

```bash
curl -s -X POST http://120.27.129.78:8080/api/upload/snapshot \
  -F "file=@/path/to/snapshot.jpg"
```

---

## 18. 紧急联系人

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 80 | 创建联系人 | POST | `/api/emergency-contact` | 新增紧急联系人 |
| 81 | 联系人详情 | GET | `/api/emergency-contact/{contactId}` | 查询单条 |
| 82 | 联系人列表 | GET | `/api/emergency-contact/list?elderId=elder_001` | 按老人查询 |
| 83 | 更新联系人 | PUT | `/api/emergency-contact/{contactId}` | 修改信息 |
| 84 | 删除联系人 | DELETE | `/api/emergency-contact/{contactId}` | 删除 |

### 17.1 创建联系人

```bash
curl -s -X POST http://120.27.129.78:8080/api/emergency-contact \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "name": "张小明",
    "phone": "13800138011",
    "relationship": "儿子",
    "isEmergency": true,
    "sortOrder": 1
  }'
```

### 17.2 联系人详情

```bash
curl -s http://120.27.129.78:8080/api/emergency-contact/ct_001
```

### 17.3 联系人列表

```bash
curl -s "http://120.27.129.78:8080/api/emergency-contact/list?elderId=elder_001"
```

### 17.4 更新联系人

```bash
curl -s -X PUT http://120.27.129.78:8080/api/emergency-contact/ct_001 \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900000001","isEmergency":true}'
```

### 17.5 删除联系人

```bash
curl -s -X DELETE http://120.27.129.78:8080/api/emergency-contact/ct_001
```

---

## 19. SOS 求救

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 85 | 触发 SOS | POST | `/api/sos` | 创建求救记录 |
| 86 | SOS 详情 | GET | `/api/sos/{sosId}` | 查询单条 |
| 87 | SOS 列表 | GET | `/api/sos/list?elderId=elder_001` | 按老人查询 |
| 88 | 处理 SOS | PUT | `/api/sos/{sosId}/handle?handlerId=staff_001` | 标记已处理 |

### 18.1 触发 SOS

```bash
curl -s -X POST http://120.27.129.78:8080/api/sos \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "deviceId": "dev_001",
    "location": "1号楼 1-201",
    "description": "手表紧急呼救"
  }'
```

### 18.2 SOS 详情

```bash
curl -s http://120.27.129.78:8080/api/sos/sos_001
```

### 18.3 SOS 列表

```bash
curl -s "http://120.27.129.78:8080/api/sos/list?elderId=elder_001"
```

### 18.4 处理 SOS

```bash
curl -s -X PUT "http://120.27.129.78:8080/api/sos/sos_001/handle?handlerId=staff_001"
```

---

## 20. 通知管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 89 | 通知列表 | GET | `/api/notification/list?userId=user_001&page=1&pageSize=20` | 分页查询 |
| 90 | 标记已读 | POST | `/api/notification/{notificationId}/read` | 单条已读 |
| 91 | 全部已读 | POST | `/api/notification/read-all?userId=user_001&userType=staff` | 批量已读 |
| 92 | 未读数量 | GET | `/api/notification/unread-count?userId=user_001&userType=staff` | 未读数 |

### 19.1 通知列表

```bash
curl -s "http://120.27.129.78:8080/api/notification/list?userId=user_001&page=1&pageSize=20"
```

### 19.2 标记已读

```bash
curl -s -X POST http://120.27.129.78:8080/api/notification/noti_001/read
```

### 19.3 全部已读

```bash
curl -s -X POST "http://120.27.129.78:8080/api/notification/read-all?userId=user_001&userType=staff"
```

### 19.4 未读数量

```bash
curl -s "http://120.27.129.78:8080/api/notification/unread-count?userId=user_001&userType=staff"
```

---

## 21. 服务申请

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 93 | 提交申请 | POST | `/api/service-request` | 创建服务申请 |
| 94 | 我的申请 | GET | `/api/service-request/my-list?familyId=fam_001` | 家属查自己的申请 |
| 95 | 申请列表（管理） | GET | `/api/service-request/list` | 管理端分页 |
| 96 | 申请详情/状态 | GET | `/api/service-request/{requestId}/status` | 查询状态 |
| 97 | 转为工单 | POST | `/api/service-request/{requestId}/convert` | 审批转工单 |
| 98 | 驳回申请 | POST | `/api/service-request/{requestId}/reject` | 驳回 |

### 20.1 提交申请

```bash
curl -s -X POST http://120.27.129.78:8080/api/service-request \
  -H "Content-Type: application/json" \
  -d '{
    "familyId": "fam_001",
    "elderId": "elder_001",
    "requestType": "健康咨询",
    "description": "希望安排社区医生上门为老人做体检"
  }'
```

### 20.2 我的申请

```bash
curl -s "http://120.27.129.78:8080/api/service-request/my-list?familyId=fam_001"
```

### 20.3 申请列表（管理端）

```bash
curl -s "http://120.27.129.78:8080/api/service-request/list?page=1&pageSize=10"
```

### 20.4 申请详情

```bash
curl -s http://120.27.129.78:8080/api/service-request/sr_001/status
```

### 20.5 转为工单

```bash
curl -s -X POST http://120.27.129.78:8080/api/service-request/sr_001/convert \
  -H "Content-Type: application/json" \
  -d '{"orderId":"wo_new_001"}'
```

### 20.6 驳回申请

```bash
curl -s -X POST http://120.27.129.78:8080/api/service-request/sr_001/reject \
  -H "Content-Type: application/json" \
  -d '{"reason":"当前资源不足，暂缓处理"}'
```

---

## 22. 监控申请

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 99 | 创建监控申请 | POST | `/api/monitor-request` | 家属申请查看监控 |
| 100 | 家属申请列表 | GET | `/api/monitor-request/list/family?familyId=fam_001` | 家属查自己 |
| 101 | 工作人员待审列表 | GET | `/api/monitor-request/list/staff?staffId=staff_001` | 工作人员待审 |
| 102 | 批准申请 | POST | `/api/monitor-request/{requestId}/approve` | 批准 |
| 103 | 驳回申请 | POST | `/api/monitor-request/{requestId}/reject` | 驳回 |
| 104 | 撤销权限 | POST | `/api/monitor-request/{requestId}/revoke` | 撤销已批准 |
| 105 | 申请结果查询 | GET | `/api/monitor-request/{requestId}/result` | 查询结果 |
| 106 | 权限检查 | GET | `/api/monitor-request/check?elderId=elder_001&staffId=staff_001` | 检查是否有权限 |

### 21.1 创建监控申请

```bash
curl -s -X POST http://120.27.129.78:8080/api/monitor-request \
  -H "Content-Type: application/json" \
  -d '{
    "familyId": "fam_001",
    "elderId": "elder_001",
    "reason": "老人三天未接电话，希望查看监控确认安全"
  }'
```

### 21.2 家属申请列表

```bash
curl -s "http://120.27.129.78:8080/api/monitor-request/list/family?familyId=fam_001"
```

### 21.3 工作人员待审列表

```bash
curl -s "http://120.27.129.78:8080/api/monitor-request/list/staff?staffId=staff_001"
```

### 21.4 批准申请

```bash
curl -s -X POST http://120.27.129.78:8080/api/monitor-request/mr_001/approve
```

### 21.5 驳回申请

```bash
curl -s -X POST http://120.27.129.78:8080/api/monitor-request/mr_001/reject
```

### 21.6 撤销权限

```bash
curl -s -X POST http://120.27.129.78:8080/api/monitor-request/mr_001/revoke
```

### 21.7 申请结果查询

```bash
curl -s http://120.27.129.78:8080/api/monitor-request/mr_001/result
```

### 21.8 权限检查

```bash
curl -s "http://120.27.129.78:8080/api/monitor-request/check?elderId=elder_001&staffId=staff_001"
```

---

## 23. 干预管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 107 | 创建干预 | POST | `/api/intervention` | 创建干预记录 |
| 108 | 干预详情 | GET | `/api/intervention/{interventionId}` | 查询单条 |
| 109 | 干预列表 | GET | `/api/intervention/list` | 支持筛选 |
| 110 | 更新干预 | PUT | `/api/intervention/{interventionId}` | 修改干预记录 |
| 111 | 完成干预 | PUT | `/api/intervention/{interventionId}/complete` | 标记完成 |

### 22.1 创建干预

```bash
curl -s -X POST http://120.27.129.78:8080/api/intervention \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "alarmId": "alarm_001",
    "type": "上门确认",
    "priority": "urgent",
    "status": "pending",
    "description": "心率异常告警，需上门确认"
  }'
```

### 22.2 干预详情

```bash
curl -s http://120.27.129.78:8080/api/intervention/int_001
```

### 22.3 干预列表

```bash
curl -s "http://120.27.129.78:8080/api/intervention/list?elderId=elder_001&status=pending&page=1&size=10"
```

### 22.4 更新干预

```bash
curl -s -X PUT http://120.27.129.78:8080/api/intervention/int_001 \
  -H "Content-Type: application/json" \
  -d '{"priority":"high","description":"更新描述"}'
```

### 22.5 完成干预

```bash
curl -s -X PUT http://120.27.129.78:8080/api/intervention/int_001/complete \
  -H "Content-Type: application/json" \
  -d '{"status":"completed","result":"已上门确认，老人状态良好","completeTime":"2026-06-20 11:00:00"}'
```

---

## 24. 工作人员管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 112 | 工作人员列表 | GET | `/api/staff/list?communityId=comm_001` | 按社区查询 |
| 113 | 添加工作人员 | POST | `/api/staff` | 新增 |
| 114 | 更新工作人员 | PUT | `/api/staff/{staffId}` | 修改信息 |
| 115 | 删除工作人员 | DELETE | `/api/staff/{staffId}` | 删除 |

### 23.1 工作人员列表

```bash
curl -s "http://120.27.129.78:8080/api/staff/list?communityId=comm_001"
curl -s http://120.27.129.78:8080/api/staff/list
```

### 23.2 添加工作人员

```bash
curl -s -X POST http://120.27.129.78:8080/api/staff \
  -H "Content-Type: application/json" \
  -d '{
    "name": "李工作",
    "phone": "13900000100",
    "role": "社区管理员",
    "communityId": "comm_001"
  }'
```

### 23.3 更新工作人员

```bash
curl -s -X PUT http://120.27.129.78:8080/api/staff/STF-abc12345 \
  -H "Content-Type: application/json" \
  -d '{"name":"李工改","role":"社区主管"}'
```

### 23.4 删除工作人员

```bash
curl -s -X DELETE http://120.27.129.78:8080/api/staff/STF-abc12345
```

---

## 25. 健康档案

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 116 | 创建/更新档案 | POST | `/api/health-record` | 保存健康档案 |
| 117 | 按老人查询 | GET | `/api/health-record/by-elder/{elderId}` | 查询档案 |
| 118 | 按档案ID查询 | GET | `/api/health-record/{recordId}` | 查询档案 |

### 24.1 创建/更新健康档案

```bash
curl -s -X POST http://120.27.129.78:8080/api/health-record \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "allergy": "青霉素过敏",
    "chronicDisease": "高血压、糖尿病",
    "medication": "氨氯地平 5mg/天",
    "remark": "需定期监测血压血糖"
  }'
```

### 24.2 按老人查询

```bash
curl -s http://120.27.129.78:8080/api/health-record/by-elder/elder_001
```

### 24.3 按档案ID查询

```bash
curl -s http://120.27.129.78:8080/api/health-record/hr_001
```

---

## 26. 设备模拟器（备用路径 `/api/simulator`）

> 与 `/api/device/simulator/*` 功能相同，提供备用访问路径

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 119 | 模拟器状态 | GET | `/api/simulator/status` | 查看运行状态 |
| 120 | 启动模拟器 | POST | `/api/simulator/start` | 启动 |
| 121 | 停止模拟器 | POST | `/api/simulator/stop` | 停止 |
| 122 | 指纹开锁成功 | POST | `/api/simulator/fingerprint/success?userId=user_001` | 模拟指纹开门成功 |
| 123 | 指纹开锁失败 | POST | `/api/simulator/fingerprint/fail` | 模拟指纹开门失败 |
| 124 | 窗帘控制 | POST | `/api/simulator/curtain?command=open&percent=50` | 模拟窗帘 |
| 125 | 蜂鸣器 | POST | `/api/simulator/buzzer?command=beep&reason=测试` | 模拟蜂鸣 |
| 126 | 灯光控制 | POST | `/api/simulator/light?command=on&brightness=80` | 模拟灯光 |
| 127 | 手表紧急呼救 | POST | `/api/simulator/watch/emergency` | 模拟手表SOS |
| 128 | 跌倒检测 | POST | `/api/simulator/fall?room=living-room` | 模拟跌倒 |
| 129 | 查找物品 | POST | `/api/simulator/find-item?itemName=钥匙&room=living-room` | 模拟查找物品 |

### 25.1 模拟器状态

```bash
curl -s http://120.27.129.78:8080/api/simulator/status
```

### 25.2 启动/停止

```bash
curl -s -X POST http://120.27.129.78:8080/api/simulator/start
curl -s -X POST http://120.27.129.78:8080/api/simulator/stop
```

### 25.3 指纹事件

```bash
curl -s -X POST "http://120.27.129.78:8080/api/simulator/fingerprint/success?userId=user_001"
curl -s -X POST http://120.27.129.78:8080/api/simulator/fingerprint/fail
```

### 25.4 窗帘/蜂鸣/灯光/手表/跌倒/找物

```bash
curl -s -X POST "http://120.27.129.78:8080/api/simulator/curtain?command=open&percent=50"
curl -s -X POST "http://120.27.129.78:8080/api/simulator/buzzer?command=beep&reason=测试"
curl -s -X POST "http://120.27.129.78:8080/api/simulator/light?command=on&brightness=80"
curl -s -X POST http://120.27.129.78:8080/api/simulator/watch/emergency
curl -s -X POST "http://120.27.129.78:8080/api/simulator/fall?room=living-room"
curl -s -X POST "http://120.27.129.78:8080/api/simulator/find-item?itemName=钥匙&room=living-room"
```

---

## 27. 云端智能体（`/api/cloud-agent`）

> 云端智能体通信接口，用于云端AI管家与后端的数据交互

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 130 | 注册云端智能体 | POST | `/api/cloud-agent/register` | 注册云端智能体 |
| 131 | 上报智能体数据 | POST | `/api/cloud-agent/report` | 上报运行数据 |
| 132 | 上报智能体告警 | POST | `/api/cloud-agent/alarm` | 上报云端告警 |
| 133 | 智能体配置 | GET | `/api/cloud-agent/{agentId}/config` | 获取配置 |
| 134 | 智能体干预任务 | GET | `/api/cloud-agent/{agentId}/interventions` | 获取干预任务列表 |
| 135 | 上报干预结果 | POST | `/api/cloud-agent/{agentId}/intervention-result` | 上报干预执行结果 |
| 136 | 云端聊天 | POST | `/api/cloud-agent/chat` | 云端智能体聊天 |
| 137 | 云端建议 | POST | `/api/cloud-agent/advice` | 获取云端建议 |

```bash
# 注册
curl -s -X POST http://120.27.129.78:8080/api/cloud-agent/register \
  -H "Content-Type: application/json" \
  -d '{"agentId":"agent_001","capabilities":["chat","health_analysis"]}'

# 获取配置
curl -s http://120.27.129.78:8080/api/cloud-agent/agent_001/config

# 获取干预任务
curl -s http://120.27.129.78:8080/api/cloud-agent/agent_001/interventions

# 上报干预结果
curl -s -X POST http://120.27.129.78:8080/api/cloud-agent/agent_001/intervention-result \
  -H "Content-Type: application/json" \
  -d '{"result":"completed","detail":"已完成"}'

# 云端聊天
curl -s -X POST http://120.27.129.78:8080/api/cloud-agent/chat \
  -H "Content-Type: application/json" \
  -d '{"elder_id":"elder_001","message":"你好"}'

# 云端建议
curl -s -X POST http://120.27.129.78:8080/api/cloud-agent/advice \
  -H "Content-Type: application/json" \
  -d '{"elder_id":"elder_001","context":"健康管理"}'
```

---

## 28. 本地智能体（`/api/local-agent`）

> 本地边缘智能体通信接口，用于本地AI盒子与后端的数据交互

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 138 | 本地心跳 | POST | `/api/local-agent/heartbeat` | 上报心跳 |
| 139 | 本地状态上报 | POST | `/api/local-agent/status` | 上报运行状态 |
| 140 | 本地数据上报 | POST | `/api/local-agent/data` | 上报传感器数据 |
| 141 | 本地告警上报 | POST | `/api/local-agent/alarm-report` | 上报本地告警 |
| 142 | 本地指令查询 | GET | `/api/local-agent/{agentId}/commands` | 获取待执行指令 |
| 143 | 本地意图上报 | POST | `/api/local-agent/intent` | 上报识别意图 |
| 144 | 本地设备控制 | POST | `/api/local-agent/control` | 上报控制结果 |

```bash
# 心跳
curl -s -X POST http://120.27.129.78:8080/api/local-agent/heartbeat \
  -H "Content-Type: application/json" \
  -d '{"agentId":"local_001","timestamp":"2026-06-20T10:00:00"}'

# 获取指令
curl -s http://120.27.129.78:8080/api/local-agent/local_001/commands

# 意图上报
curl -s -X POST http://120.27.129.78:8080/api/local-agent/intent \
  -H "Content-Type: application/json" \
  -d '{"elderId":"elder_001","intent":"健康咨询","confidence":0.9}'
```

---

## 附录 A：统一响应格式

所有接口返回统一结构：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证/密码错误 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 409 | 冲突（如手机号已注册） |

---

## 附录 B：接口索引（按路径排序）

```
GET    /api/health                                      # 健康检查
POST   /api/auth/login                                  # 登录
POST   /api/auth/register                               # 注册
POST   /api/auth/reset-password                         # 重置密码
POST   /api/auth/logout                                 # 登出
GET    /api/auth/me?phone=                              # 当前用户
GET    /api/dashboard/stats                             # 仪表盘统计
GET    /api/dashboard/summary                           # 大屏摘要
GET    /api/dashboard/buildings                         # 楼栋列表
POST   /api/alarm                                       # 创建告警
GET    /api/alarm/{alarmId}                             # 告警详情
GET    /api/alarm/list                                  # 告警列表
GET    /api/alarm/intrusion/list                        # 入侵告警列表
GET    /api/alarm/intrusion/{alarmId}/snapshot          # 入侵抓拍
PUT    /api/alarm/{alarmId}/acknowledge                 # 确认告警
PUT    /api/alarm/{alarmId}/resolve                     # 解决告警
PUT    /api/alarm/{alarmId}/read                        # 标记已读
GET    /api/alarm/unread-count?elderId=                 # 未读数
GET    /api/alarms                                      # 告警列表(兼容)
GET    /api/alarms/{alarm_id}                           # 告警详情(兼容)
POST   /api/alarms/{alarm_id}/to-work-order             # 告警转工单
POST   /api/alarms/{alarm_id}/mark-read                 # 标记已读(兼容)
POST   /api/alarms/{alarm_id}/handle                    # 处理告警(兼容)
GET    /api/work-order/list                             # 工单列表
GET    /api/work-order/{orderId}                        # 工单详情
POST   /api/work-order                                  # 创建工单
PUT    /api/work-order/{orderId}/status                 # 更新工单状态
PUT    /api/work-order/{orderId}/assign                 # 指派处理人
GET    /api/work-orders                                 # 工单列表(兼容)
POST   /api/work-orders                                 # 创建工单(兼容)
PUT    /api/work-orders/{work_order_id}/assign          # 指派(兼容)
PUT    /api/work-orders/{work_order_id}/complete        # 完成(兼容)
GET    /api/elder/list                                  # 老人列表
POST   /api/elder                                       # 创建老人
GET    /api/elder/{elderId}                             # 老人详情
PUT    /api/elder/{elderId}                             # 更新老人
DELETE /api/elder/{elderId}                             # 删除老人
GET    /api/elder/detail/{elderId}                      # 综合详情
GET    /api/elder/bound?familyId=                       # 家属绑定老人
GET    /api/elder/{elderId}/devices                     # 老人设备
GET    /api/elder/{elderId}/health/realtime             # 实时健康
GET    /api/elder/{elderId}/health/history              # 健康趋势
GET    /api/elder/{elderId}/camera-stream               # 摄像头流
GET    /api/elders?page=&pageSize=                      # 老人列表(兼容)
GET    /api/health/latest/{elder_id}                    # 最新健康数据
GET    /api/ai/service-status                           # AI服务状态
GET    /api/ai/latest-analysis/{elderId}                # 最近AI分析
GET    /api/ai/analysis-records?elder_id=               # AI分析记录
POST   /api/ai/health-analysis                          # AI健康分析
POST   /api/ai/chat/quick                               # AI快速对话
POST   /api/ai/chat/deep                                # AI深度对话
POST   /api/ai/rag-query                                # RAG问答
POST   /api/ai/vision-analysis                          # 视觉分析
POST   /api/ai/chat?userId=&content=                    # AI聊天(异步)
POST   /api/ai/find-item?userId=&itemName=              # AI找物品
GET    /api/agent/context?elderId=                      # 智能体上下文
POST   /api/device/register                             # 注册设备
GET    /api/device/{deviceId}                           # 设备详情
GET    /api/device/list                                 # 设备列表
PUT    /api/device/{deviceId}/status                    # 更新设备状态
POST   /api/device/{deviceId}/sensor-data               # 上传传感器数据
POST   /api/device/{deviceId}/command                   # 下发控制指令
GET    /api/device/{deviceId}/sensor-data               # 查询传感器数据
POST   /api/device/upload                               # 设备健康数据上传
GET    /api/device/sensor/temperature-humidity          # 温湿度历史
GET    /api/device/sensor/{sensorType}                  # 传感器历史
GET    /api/device/alarm                                # 设备告警
GET    /api/device/simulator/status                     # 模拟器状态
POST   /api/device/simulator/start                      # 启动模拟器
POST   /api/device/simulator/stop                       # 停止模拟器
POST   /api/device/simulator/trigger/fingerprint-success # 指纹成功
POST   /api/device/simulator/trigger/fingerprint-fail   # 指纹失败
POST   /api/device/simulator/trigger/curtain            # 窗帘
POST   /api/device/simulator/trigger/buzzer             # 蜂鸣器
POST   /api/device/simulator/trigger/light              # 灯光
POST   /api/device/simulator/trigger/watch-emergency    # 手表SOS
POST   /api/device/simulator/trigger/fall-detection     # 跌倒
POST   /api/device/simulator/trigger/find-item          # 找物品
POST   /api/upload/avatar                               # 上传头像
POST   /api/upload/snapshot                             # 上传快照
POST   /api/emergency-contact                           # 创建联系人
GET    /api/emergency-contact/{contactId}               # 联系人详情
GET    /api/emergency-contact/list?elderId=             # 联系人列表
PUT    /api/emergency-contact/{contactId}               # 更新联系人
DELETE /api/emergency-contact/{contactId}               # 删除联系人
POST   /api/sos                                         # 触发SOS
GET    /api/sos/{sosId}                                 # SOS详情
GET    /api/sos/list?elderId=                           # SOS列表
PUT    /api/sos/{sosId}/handle?handlerId=               # 处理SOS
GET    /api/notification/list?userId=                   # 通知列表
POST   /api/notification/{notificationId}/read          # 标记已读
POST   /api/notification/read-all?userId=               # 全部已读
GET    /api/notification/unread-count?userId=           # 未读数
POST   /api/service-request                             # 提交申请
GET    /api/service-request/my-list?familyId=           # 我的申请
GET    /api/service-request/list                        # 申请列表
GET    /api/service-request/{requestId}/status          # 申请详情
POST   /api/service-request/{requestId}/convert         # 转工单
POST   /api/service-request/{requestId}/reject          # 驳回
POST   /api/monitor-request                             # 创建监控申请
GET    /api/monitor-request/list/family?familyId=       # 家属列表
GET    /api/monitor-request/list/staff?staffId=         # 待审列表
POST   /api/monitor-request/{requestId}/approve         # 批准
POST   /api/monitor-request/{requestId}/reject          # 驳回
POST   /api/monitor-request/{requestId}/revoke          # 撤销
GET    /api/monitor-request/{requestId}/result          # 结果查询
GET    /api/monitor-request/check?elderId=&staffId=     # 权限检查
POST   /api/intervention                                # 创建干预
GET    /api/intervention/{interventionId}               # 干预详情
GET    /api/intervention/list                           # 干预列表
PUT    /api/intervention/{interventionId}               # 更新干预
PUT    /api/intervention/{interventionId}/complete      # 完成干预
GET    /api/staff/list?communityId=                     # 工作人员列表
POST   /api/staff                                       # 添加工作人员
PUT    /api/staff/{staffId}                             # 更新工作人员
DELETE /api/staff/{staffId}                             # 删除工作人员
POST   /api/health-record                               # 创建健康档案
GET    /api/health-record/by-elder/{elderId}            # 按老人查档案
GET    /api/health-record/{recordId}                    # 按ID查档案
GET    /api/simulator/status                            # 模拟器状态(备用)
POST   /api/simulator/start                             # 启动模拟器(备用)
POST   /api/simulator/stop                              # 停止模拟器(备用)
POST   /api/simulator/fingerprint/success               # 指纹成功(备用)
POST   /api/simulator/fingerprint/fail                  # 指纹失败(备用)
POST   /api/simulator/curtain                           # 窗帘(备用)
POST   /api/simulator/buzzer                            # 蜂鸣器(备用)
POST   /api/simulator/light                             # 灯光(备用)
POST   /api/simulator/watch/emergency                   # 手表SOS(备用)
POST   /api/simulator/fall                              # 跌倒(备用)
POST   /api/simulator/find-item                         # 找物品(备用)
POST   /api/cloud-agent/register                        # 注册云端智能体
POST   /api/cloud-agent/report                          # 云端上报
POST   /api/cloud-agent/alarm                           # 云端告警
GET    /api/cloud-agent/{agentId}/config                # 云端配置
GET    /api/cloud-agent/{agentId}/interventions         # 云端干预列表
POST   /api/cloud-agent/{agentId}/intervention-result   # 云端干预结果
POST   /api/cloud-agent/chat                            # 云端聊天
POST   /api/cloud-agent/advice                          # 云端建议
POST   /api/local-agent/heartbeat                       # 本地心跳
POST   /api/local-agent/status                          # 本地状态
POST   /api/local-agent/data                            # 本地数据
POST   /api/local-agent/alarm-report                    # 本地告警
GET    /api/local-agent/{agentId}/commands              # 本地指令
POST   /api/local-agent/intent                          # 本地意图
POST   /api/local-agent/control                         # 本地控制
```

**总计：145 个 API 接口**
