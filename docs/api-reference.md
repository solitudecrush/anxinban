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

> **注意**：`userType=elder`（老人用户）不支持直接登录和注册。

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 2 | Web端登录 | POST | `/api/auth/login/web` | 工作人员登录（无需传userType） |
| 3 | App端登录 | POST | `/api/auth/login/app` | 家属登录（无需传userType） |
| 4 | 旧版登录（兼容） | POST | `/api/auth/login` | 需传userType，elder不支持 |
| 5 | 用户注册 | POST | `/api/auth/register` | staff/family注册，elder不支持 |
| 6 | 重置密码 | POST | `/api/auth/reset-password` | staff/family重置密码 |
| 7 | 用户登出 | POST | `/api/auth/logout` | 登出 |
| 8 | 当前用户信息 | GET | `/api/auth/me?phone=13900000000` | 根据手机号查用户信息 |

### 2.1 Web端工作人员登录（推荐）

> 前端无需传 `userType`，后端自动以 `staff` 身份校验。

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/login/web \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900000000","password":"123456"}'
```

请求体字段：`phone`（手机号）、`password`（密码）

成功返回：
```json
{
  "code": 200, "message": "success",
  "data": {
    "userId": "STF-abc12345",
    "name": "张工",
    "phone": "13900000000",
    "role": "社区管理员",
    "userType": "staff",
    "token": null
  }
}
```

### 2.2 App端家属登录（推荐）

> 前端无需传 `userType`，后端自动以 `family` 身份校验。

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/login/app \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","password":"123456"}'
```

请求体字段：`phone`（手机号）、`password`（密码）

### 2.3 旧版登录（兼容保留）

> `userType=elder` 不再支持，会返回 403。

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900000000","password":"123456","userType":"staff"}'
```

请求体字段：`phone`、`password`、`userType`（`staff` 或 `family`）

### 2.4 注册

> 仅支持 `staff` 和 `family`，`elder` 不支持自行注册。

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900000001","password":"123456","name":"测试用户","userType":"family"}'
```

### 2.5 重置密码

> 仅支持 `staff` 和 `family` 用户。

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900000000","newPassword":"654321"}'
```

### 2.6 登出

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/logout
```

### 2.7 当前用户信息

> 优先查 staff 表，未找到再查 family 表。

```bash
curl -s "http://120.27.129.78:8080/api/auth/me?phone=13900000000"
```

---

## 3. 仪表盘

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 9 | 仪表盘统计 | GET | `/api/dashboard/stats` | 核心统计指标 |
| 10 | 大屏摘要 | GET | `/api/dashboard/summary` | 兼容大屏展示格式 |
| 11 | 楼栋列表 | GET | `/api/dashboard/buildings` | 返回楼栋名称列表 |

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
| 12 | 创建告警 | POST | `/api/alarm` | 创建新告警 |
| 13 | 告警详情 | GET | `/api/alarm/{alarmId}` | 查询单条告警 |
| 14 | 告警列表（分页） | GET | `/api/alarm/list` | 支持多条件筛选 |
| 15 | 入侵告警列表 | GET | `/api/alarm/intrusion/list` | 入侵类告警分页 |
| 16 | 入侵抓拍快照 | GET | `/api/alarm/intrusion/{alarmId}/snapshot` | 获取入侵抓拍URL |
| 17 | 确认告警 | PUT | `/api/alarm/{alarmId}/acknowledge` | 确认处理告警 |
| 18 | 解决告警 | PUT | `/api/alarm/{alarmId}/resolve` | 标记告警已解决 |
| 19 | 标记已读 | PUT | `/api/alarm/{alarmId}/read` | 标记告警为已读 |
| 20 | 未读数量 | GET | `/api/alarm/unread-count?elderId=elder_001` | 查询未读告警数 |

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
# 按楼栋筛选
curl -s "http://120.27.129.78:8080/api/alarm/intrusion/list?building=1号楼&status=pending&page=1&pageSize=10"
```

可选参数：`status`, `building`, `page`, `pageSize`

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
| 21 | 告警列表（兼容） | GET | `/api/alarms` | 返回全部告警 |
| 22 | 告警详情（兼容） | GET | `/api/alarms/{alarm_id}` | 单条告警详情 |
| 23 | 告警转工单 | POST | `/api/alarms/{alarm_id}/to-work-order` | 告警转为工单 |
| 24 | 标记已读（兼容） | POST | `/api/alarms/{alarm_id}/mark-read` | 标记告警已读 |
| 25 | 处理告警（兼容） | POST | `/api/alarms/{alarm_id}/handle` | 标记告警已处理 |

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
| 26 | 工单列表（分页） | GET | `/api/work-order/list` | 支持关键词/状态/老人姓名筛选 |
| 27 | 工单详情 | GET | `/api/work-order/{orderId}` | 查询单条工单 |
| 28 | 创建工单 | POST | `/api/work-order` | 创建新工单 |
| 29 | 更新工单状态 | PUT | `/api/work-order/{orderId}/status` | 修改工单状态 |
| 30 | 指派处理人 | PUT | `/api/work-order/{orderId}/assign` | 分配处理人 |

### 6.1 工单列表

```bash
curl -s "http://120.27.129.78:8080/api/work-order/list?page=1&pageSize=10"
curl -s "http://120.27.129.78:8080/api/work-order/list?status=待处理&page=1&pageSize=10"
curl -s "http://120.27.129.78:8080/api/work-order/list?elderName=张&page=1&pageSize=10"
```

可选参数：`keyword`, `elderName`, `status`, `page`, `pageSize`

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
| 31 | 工单列表（兼容） | GET | `/api/work-orders` | 返回全部工单 |
| 32 | 创建工单（兼容） | POST | `/api/work-orders` | 创建新工单 |
| 33 | 指派处理人（兼容） | PUT | `/api/work-orders/{work_order_id}/assign` | 分配处理人 |
| 34 | 完成工单（兼容） | PUT | `/api/work-orders/{work_order_id}/complete` | 标记已完成 |

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
| 35 | 老人列表（分页） | GET | `/api/elder/list` | 支持筛选 |
| 36 | 创建老人 | POST | `/api/elder` | 新建老人档案 |
| 37 | 老人详情 | GET | `/api/elder/{elderId}` | 基本信息 |
| 38 | 更新老人 | PUT | `/api/elder/{elderId}` | 修改老人信息 |
| 39 | 删除老人 | DELETE | `/api/elder/{elderId}` | 删除老人档案 |
| 40 | 老人综合详情 | GET | `/api/elder/detail/{elderId}` | 含健康/设备/告警/工单 |
| 41 | 家属绑定老人 | GET | `/api/elder/bound?familyId=fam_001` | 家属查绑定老人（单条） |
| 42 | 老人名下设备 | GET | `/api/elder/{elderId}/devices` | 设备列表 |
| 43 | 实时健康数据 | GET | `/api/elder/{elderId}/health/realtime` | 最新健康指标 |
| 44 | 健康历史趋势 | GET | `/api/elder/{elderId}/health/history?type=heart_rate&range=week` | 趋势数据 |
| 45 | 摄像头流地址 | GET | `/api/elder/{elderId}/camera-stream?staffId=staff_001` | 获取摄像头流 |

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

> 返回家属绑定的单个老人信息（非列表）。

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
| 46 | 老人列表（兼容） | GET | `/api/elders?page=1&pageSize=20` | 分页，snake_case |

### 9.1 老人列表（兼容）

```bash
curl -s "http://120.27.129.78:8080/api/elders?page=1&pageSize=20"
```

---

## 10. 健康数据

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 47 | 最新健康数据 | GET | `/api/health/latest/{elder_id}` | 指定老人最新指标 |

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
| 48 | AI 服务状态 | GET | `/api/ai/service-status` | 查看 AI 服务可达性 |
| 49 | 最近 AI 分析 | GET | `/api/ai/latest-analysis/{elderId}` | 某老人最近一次AI分析 |
| 50 | AI 分析记录 | GET | `/api/ai/analysis-records?elder_id=elder_001&page=1&pageSize=10` | 分页历史记录 |
| 51 | AI 健康风险分析 | POST | `/api/ai/health-analysis` | 健康风险分析 |
| 52 | AI 快速对话 | POST | `/api/ai/chat/quick` | 本地即时陪伴 |
| 53 | AI 深度对话 | POST | `/api/ai/chat/deep` | 云端深度陪伴 |
| 54 | RAG 知识问答 | POST | `/api/ai/rag-query` | 养老知识问答 |
| 55 | AI 视觉分析 | POST | `/api/ai/vision-analysis` | 跌倒/行为检测 |
| 56 | AI 聊天（异步） | POST | `/api/ai/chat?userId=user_001&content=你好` | AI 智能体对话 |
| 57 | AI 查找物品 | POST | `/api/ai/find-item?userId=user_001&itemName=钥匙` | AI 查找物品 |

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
| 58 | 智能体上下文 | GET | `/api/agent/context?elderId=elder_001` | 获取老人聚合上下文，用于AI助手/智能管家 |

### 12.1 智能体上下文

```bash
curl -s "http://120.27.129.78:8080/api/agent/context?elderId=elder_001"
```

---

## 13. 设备管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 59 | 注册设备 | POST | `/api/device/register` | 注册新设备 |
| 60 | 设备详情 | GET | `/api/device/{deviceId}` | 查询设备 |
| 61 | 设备列表 | GET | `/api/device/list` | 分页筛选 |
| 62 | 更新设备状态 | PUT | `/api/device/{deviceId}/status` | 上线/离线 |
| 63 | 上传传感器数据 | POST | `/api/device/{deviceId}/sensor-data` | 设备上报数据 |
| 64 | 下发控制指令 | POST | `/api/device/{deviceId}/command` | 远程控制设备 |
| 65 | 查询传感器数据 | GET | `/api/device/{deviceId}/sensor-data` | 历史数据查询 |

### 13.1 注册设备

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

### 13.2 设备详情

```bash
curl -s http://120.27.129.78:8080/api/device/dev_001
```

### 13.3 设备列表

```bash
curl -s "http://120.27.129.78:8080/api/device/list?page=1&pageSize=10"
```

可选参数：`status` (`online`/`offline`), `deviceType`, `location`, `page`, `pageSize`

### 13.4 更新设备状态

```bash
curl -s -X PUT http://120.27.129.78:8080/api/device/dev_001/status \
  -H "Content-Type: application/json" \
  -d '{"status":"online","lastHeartbeat":"2026-06-20T10:00:00"}'
```

### 13.5 上传传感器数据

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

### 13.6 下发控制指令

```bash
curl -s -X POST http://120.27.129.78:8080/api/device/dev_001/command \
  -H "Content-Type: application/json" \
  -d '{"commandType":"light_on","parameters":{"brightness":80}}'
```

### 13.7 查询传感器数据

```bash
curl -s "http://120.27.129.78:8080/api/device/dev_001/sensor-data?sensorType=heart_rate&page=1&pageSize=20"
```

可选参数：`sensorType`, `startTime`, `endTime`, `page`, `pageSize`

---

## 14. 设备上传（兼容层）

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 66 | 设备健康数据上传 | POST | `/api/device/upload` | 上传健康数据，自动分析并生成告警 |

### 14.1 设备健康数据上传

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
| 67 | 温湿度历史 | GET | `/api/device/sensor/temperature-humidity` | 温湿度历史 |
| 68 | 指定类型传感器历史 | GET | `/api/device/sensor/{sensorType}` | 按类型查询 |
| 69 | 设备告警历史 | GET | `/api/device/alarm` | 告警事件分页 |

### 15.1 温湿度历史

```bash
curl -s "http://120.27.129.78:8080/api/device/sensor/temperature-humidity?limit=20"
# 按房间/设备筛选
curl -s "http://120.27.129.78:8080/api/device/sensor/temperature-humidity?room=living-room&limit=20"
```

可选参数：`room`, `deviceId`, `startTime`, `endTime`, `limit`

### 15.2 指定传感器类型历史

```bash
curl -s "http://120.27.129.78:8080/api/device/sensor/heart_rate?limit=20"
```

可选参数：`deviceId`, `startTime`, `endTime`, `limit`

### 15.3 设备告警历史

```bash
curl -s "http://120.27.129.78:8080/api/device/alarm?alarmStatus=pending&page=0&size=10"
```

可选参数：`alarmType`, `alarmLevel`, `alarmStatus`, `page`（0-based）, `size`

---

## 16. 设备模拟器

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 70 | 模拟器状态 | GET | `/api/device/simulator/status` | 查看运行状态 |
| 71 | 启动模拟器 | POST | `/api/device/simulator/start` | 启动 |
| 72 | 停止模拟器 | POST | `/api/device/simulator/stop` | 停止 |
| 73 | 触发指纹成功 | POST | `/api/device/simulator/trigger/fingerprint-success?userId=user_001` | 模拟指纹开门成功 |
| 74 | 触发指纹失败 | POST | `/api/device/simulator/trigger/fingerprint-fail` | 模拟指纹开门失败 |
| 75 | 触发窗帘控制 | POST | `/api/device/simulator/trigger/curtain?command=open&percent=50` | 模拟窗帘 |
| 76 | 触发蜂鸣器 | POST | `/api/device/simulator/trigger/buzzer?command=beep&reason=测试` | 模拟蜂鸣 |
| 77 | 触发灯光控制 | POST | `/api/device/simulator/trigger/light?command=on&brightness=80` | 模拟灯光 |
| 78 | 触发手表紧急呼救 | POST | `/api/device/simulator/trigger/watch-emergency` | 模拟手表SOS |
| 79 | 触发跌倒检测 | POST | `/api/device/simulator/trigger/fall-detection?room=living-room` | 模拟跌倒 |
| 80 | 触发查找物品 | POST | `/api/device/simulator/trigger/find-item?itemName=钥匙&room=living-room` | 模拟查找物品 |

### 16.1 模拟器状态

```bash
curl -s http://120.27.129.78:8080/api/device/simulator/status
```

### 16.2 启动/停止模拟器

```bash
curl -s -X POST http://120.27.129.78:8080/api/device/simulator/start
curl -s -X POST http://120.27.129.78:8080/api/device/simulator/stop
```

### 16.3 触发指纹事件

```bash
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/fingerprint-success?userId=user_001"
curl -s -X POST http://120.27.129.78:8080/api/device/simulator/trigger/fingerprint-fail
```

### 16.4 触发窗帘控制

```bash
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/curtain?command=open&percent=50"
```

### 16.5 触发蜂鸣器

```bash
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/buzzer?command=beep&reason=测试鸣响"
```

### 16.6 触发灯光控制

```bash
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/light?command=on&brightness=80"
```

### 16.7 触发手表紧急呼救

```bash
curl -s -X POST http://120.27.129.78:8080/api/device/simulator/trigger/watch-emergency
```

### 16.8 触发跌倒检测

```bash
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/fall-detection?room=living-room"
```

### 16.9 触发查找物品

```bash
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/find-item?itemName=钥匙&room=living-room"
```

---

## 17. 文件上传

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 81 | 上传头像 | POST | `/api/upload/avatar` | multipart/form-data |
| 82 | 上传抓拍快照 | POST | `/api/upload/snapshot` | multipart/form-data |

### 17.1 上传头像

```bash
curl -s -X POST http://120.27.129.78:8080/api/upload/avatar \
  -F "file=@/path/to/avatar.jpg"
```

### 17.2 上传抓拍快照

```bash
curl -s -X POST http://120.27.129.78:8080/api/upload/snapshot \
  -F "file=@/path/to/snapshot.jpg"
```

---

## 18. 紧急联系人

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 83 | 创建联系人 | POST | `/api/emergency-contact` | 新增紧急联系人 |
| 84 | 联系人详情 | GET | `/api/emergency-contact/{contactId}` | 查询单条 |
| 85 | 联系人列表 | GET | `/api/emergency-contact/list?elderId=elder_001` | 按老人查询 |
| 86 | 更新联系人 | PUT | `/api/emergency-contact/{contactId}` | 修改信息 |
| 87 | 删除联系人 | DELETE | `/api/emergency-contact/{contactId}` | 删除 |

### 18.1 创建联系人

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

### 18.2 联系人详情

```bash
curl -s http://120.27.129.78:8080/api/emergency-contact/ct_001
```

### 18.3 联系人列表

```bash
curl -s "http://120.27.129.78:8080/api/emergency-contact/list?elderId=elder_001"
```

### 18.4 更新联系人

```bash
curl -s -X PUT http://120.27.129.78:8080/api/emergency-contact/ct_001 \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900000001","isEmergency":true}'
```

### 18.5 删除联系人

```bash
curl -s -X DELETE http://120.27.129.78:8080/api/emergency-contact/ct_001
```

---

## 19. SOS 求救

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 88 | 触发 SOS | POST | `/api/sos` | 创建求救记录 |
| 89 | SOS 详情 | GET | `/api/sos/{sosId}` | 查询单条 |
| 90 | SOS 列表 | GET | `/api/sos/list?elderId=elder_001` | 按老人查询 |
| 91 | 处理 SOS | PUT | `/api/sos/{sosId}/handle?handlerId=staff_001` | 标记已处理 |

### 19.1 触发 SOS

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

### 19.2 SOS 详情

```bash
curl -s http://120.27.129.78:8080/api/sos/sos_001
```

### 19.3 SOS 列表

```bash
curl -s "http://120.27.129.78:8080/api/sos/list?elderId=elder_001"
```

### 19.4 处理 SOS

```bash
curl -s -X PUT "http://120.27.129.78:8080/api/sos/sos_001/handle?handlerId=staff_001"
```

---

## 20. 通知管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 92 | 通知列表 | GET | `/api/notification/list?userId=user_001&page=1&pageSize=20` | 分页查询 |
| 93 | 标记已读 | POST | `/api/notification/{notificationId}/read` | 单条已读 |
| 94 | 全部已读 | POST | `/api/notification/read-all?userId=user_001&userType=staff` | 批量已读 |
| 95 | 未读数量 | GET | `/api/notification/unread-count?userId=user_001&userType=staff` | 未读数 |

### 20.1 通知列表

```bash
curl -s "http://120.27.129.78:8080/api/notification/list?userId=user_001&page=1&pageSize=20"
```

可选参数：`userType`（可选，`staff` 或 `family`）

### 20.2 标记已读

```bash
curl -s -X POST http://120.27.129.78:8080/api/notification/noti_001/read
```

### 20.3 全部已读

```bash
curl -s -X POST "http://120.27.129.78:8080/api/notification/read-all?userId=user_001&userType=staff"
```

### 20.4 未读数量

```bash
curl -s "http://120.27.129.78:8080/api/notification/unread-count?userId=user_001&userType=staff"
```

---

## 21. 服务申请

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 96 | 提交申请 | POST | `/api/service-request` | 创建服务申请 |
| 97 | 我的申请 | GET | `/api/service-request/my-list?familyId=fam_001` | 家属查自己的申请 |
| 98 | 申请列表（管理） | GET | `/api/service-request/list` | 管理端分页 |
| 99 | 申请详情/状态 | GET | `/api/service-request/{requestId}/status` | 查询状态 |
| 100 | 转为工单 | POST | `/api/service-request/{requestId}/convert` | 审批转工单 |
| 101 | 驳回申请 | POST | `/api/service-request/{requestId}/reject` | 驳回 |

### 21.1 提交申请

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

### 21.2 我的申请

```bash
curl -s "http://120.27.129.78:8080/api/service-request/my-list?familyId=fam_001"
```

### 21.3 申请列表（管理端）

```bash
curl -s "http://120.27.129.78:8080/api/service-request/list?page=1&pageSize=10"
```

可选参数：`requestType`, `status`, `page`, `pageSize`

### 21.4 申请详情

```bash
curl -s http://120.27.129.78:8080/api/service-request/sr_001/status
```

### 21.5 转为工单

```bash
curl -s -X POST http://120.27.129.78:8080/api/service-request/sr_001/convert \
  -H "Content-Type: application/json" \
  -d '{"orderId":"wo_new_001"}'
```

### 21.6 驳回申请

```bash
curl -s -X POST http://120.27.129.78:8080/api/service-request/sr_001/reject \
  -H "Content-Type: application/json" \
  -d '{"reason":"当前资源不足，暂缓处理"}'
```

---

## 22. 监控申请

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 102 | 创建监控申请 | POST | `/api/monitor-request` | 家属申请查看监控 |
| 103 | 家属申请列表 | GET | `/api/monitor-request/list/family?familyId=fam_001` | 家属查自己 |
| 104 | 工作人员待审列表 | GET | `/api/monitor-request/list/staff?staffId=staff_001` | 工作人员待审 |
| 105 | 批准申请 | POST | `/api/monitor-request/{requestId}/approve` | 批准 |
| 106 | 驳回申请 | POST | `/api/monitor-request/{requestId}/reject` | 驳回 |
| 107 | 撤销权限 | POST | `/api/monitor-request/{requestId}/revoke` | 撤销已批准 |
| 108 | 申请结果查询 | GET | `/api/monitor-request/{requestId}/result` | 查询结果 |
| 109 | 权限检查 | GET | `/api/monitor-request/check?elderId=elder_001&staffId=staff_001` | 检查是否有权限 |

### 22.1 创建监控申请

```bash
curl -s -X POST http://120.27.129.78:8080/api/monitor-request \
  -H "Content-Type: application/json" \
  -d '{
    "familyId": "fam_001",
    "elderId": "elder_001",
    "reason": "老人三天未接电话，希望查看监控确认安全"
  }'
```

### 22.2 家属申请列表

```bash
curl -s "http://120.27.129.78:8080/api/monitor-request/list/family?familyId=fam_001"
```

### 22.3 工作人员待审列表

```bash
curl -s "http://120.27.129.78:8080/api/monitor-request/list/staff?staffId=staff_001"
```

### 22.4 批准申请

```bash
curl -s -X POST http://120.27.129.78:8080/api/monitor-request/mr_001/approve
```

### 22.5 驳回申请

```bash
curl -s -X POST http://120.27.129.78:8080/api/monitor-request/mr_001/reject
```

### 22.6 撤销权限

```bash
curl -s -X POST http://120.27.129.78:8080/api/monitor-request/mr_001/revoke
```

### 22.7 申请结果查询

```bash
curl -s http://120.27.129.78:8080/api/monitor-request/mr_001/result
```

### 22.8 权限检查

```bash
curl -s "http://120.27.129.78:8080/api/monitor-request/check?elderId=elder_001&staffId=staff_001"
```

---

## 23. 干预管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 110 | 创建干预 | POST | `/api/intervention` | 创建干预记录 |
| 111 | 干预详情 | GET | `/api/intervention/{interventionId}` | 查询单条 |
| 112 | 干预列表 | GET | `/api/intervention/list` | 支持筛选 |
| 113 | 更新干预 | PUT | `/api/intervention/{interventionId}` | 修改干预记录 |
| 114 | 完成干预 | PUT | `/api/intervention/{interventionId}/complete` | 标记完成 |

### 23.1 创建干预

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

### 23.2 干预详情

```bash
curl -s http://120.27.129.78:8080/api/intervention/int_001
```

### 23.3 干预列表

```bash
curl -s "http://120.27.129.78:8080/api/intervention/list?elderId=elder_001&status=pending&page=1&size=10"
```

可选参数：`elderId`, `status`, `priority`, `page`, `size`

### 23.4 更新干预

```bash
curl -s -X PUT http://120.27.129.78:8080/api/intervention/int_001 \
  -H "Content-Type: application/json" \
  -d '{"priority":"high","description":"更新描述"}'
```

### 23.5 完成干预

```bash
curl -s -X PUT http://120.27.129.78:8080/api/intervention/int_001/complete \
  -H "Content-Type: application/json" \
  -d '{"status":"completed","result":"已上门确认，老人状态良好","completeTime":"2026-06-20 11:00:00"}'
```

---

## 24. 工作人员管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 115 | 工作人员列表 | GET | `/api/staff/list?communityId=comm_001` | 按社区查询 |
| 116 | 添加工作人员 | POST | `/api/staff` | 新增 |
| 117 | 更新工作人员 | PUT | `/api/staff/{staffId}` | 修改信息 |
| 118 | 删除工作人员 | DELETE | `/api/staff/{staffId}` | 删除 |

### 24.1 工作人员列表

```bash
curl -s "http://120.27.129.78:8080/api/staff/list?communityId=comm_001"
curl -s http://120.27.129.78:8080/api/staff/list
```

### 24.2 添加工作人员

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

### 24.3 更新工作人员

```bash
curl -s -X PUT http://120.27.129.78:8080/api/staff/STF-abc12345 \
  -H "Content-Type: application/json" \
  -d '{"name":"李工改","role":"社区主管"}'
```

### 24.4 删除工作人员

```bash
curl -s -X DELETE http://120.27.129.78:8080/api/staff/STF-abc12345
```

---

## 25. 健康档案

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 119 | 创建/更新档案 | POST | `/api/health-record` | 保存健康档案 |
| 120 | 按老人查询 | GET | `/api/health-record/by-elder/{elderId}` | 查询档案 |
| 121 | 按档案ID查询 | GET | `/api/health-record/{recordId}` | 查询档案 |

### 25.1 创建/更新健康档案

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

### 25.2 按老人查询

```bash
curl -s http://120.27.129.78:8080/api/health-record/by-elder/elder_001
```

### 25.3 按档案ID查询

```bash
curl -s http://120.27.129.78:8080/api/health-record/hr_001
```

---

## 26. 云端智能体（`/api/cloud-agent`）

> 云端智能体通信接口，用于云端AI管家与后端的数据交互

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 122 | 注册云端智能体 | POST | `/api/cloud-agent/register` | 注册云端智能体 |
| 123 | 上报智能体数据 | POST | `/api/cloud-agent/report` | 上报运行数据 |
| 124 | 上报智能体告警 | POST | `/api/cloud-agent/alarm` | 上报云端告警 |
| 125 | 智能体配置 | GET | `/api/cloud-agent/{agentId}/config` | 获取配置 |
| 126 | 智能体干预任务 | GET | `/api/cloud-agent/{agentId}/interventions` | 获取干预任务列表 |
| 127 | 上报干预结果 | POST | `/api/cloud-agent/{agentId}/intervention-result` | 上报干预执行结果 |
| 128 | 云端聊天 | POST | `/api/cloud-agent/chat` | 云端智能体聊天 |
| 129 | 云端建议 | POST | `/api/cloud-agent/advice` | 获取云端建议 |

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

## 27. 本地智能体（`/api/local-agent`）

> 本地边缘智能体通信接口，用于本地AI盒子与后端的数据交互

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 130 | 本地心跳 | POST | `/api/local-agent/heartbeat` | 上报心跳 |
| 131 | 本地状态上报 | POST | `/api/local-agent/status` | 上报运行状态 |
| 132 | 本地数据上报 | POST | `/api/local-agent/data` | 上报传感器数据 |
| 133 | 本地告警上报 | POST | `/api/local-agent/alarm-report` | 上报本地告警 |
| 134 | 本地指令查询 | GET | `/api/local-agent/{agentId}/commands` | 获取待执行指令 |
| 135 | 本地意图上报 | POST | `/api/local-agent/intent` | 上报识别意图 |
| 136 | 本地设备控制 | POST | `/api/local-agent/control` | 上报控制结果 |

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

## 28. 健康体征记录

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 137 | 创建体征记录 | POST | `/api/health-vital` | 新增体征记录 |
| 138 | 体征记录列表 | GET | `/api/health-vital/list?elderId=elder_001` | 按老人查询 |
| 139 | 最新体征记录 | GET | `/api/health-vital/latest?elderId=elder_001` | 最新体征数据 |

```bash
# 创建体征记录
curl -s -X POST http://120.27.129.78:8080/api/health-vital \
  -H "Content-Type: application/json" \
  -d '{"elderId":"elder_001","heartRate":72,"spo2":98,"temperature":36.5}'

# 查询列表
curl -s "http://120.27.129.78:8080/api/health-vital/list?elderId=elder_001"
curl -s "http://120.27.129.78:8080/api/health-vital/list?elderId=elder_001&start=2026-06-01T00:00:00&end=2026-06-20T23:59:59"

# 最新记录
curl -s "http://120.27.129.78:8080/api/health-vital/latest?elderId=elder_001"
```

---

## 29. 睡眠记录

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 140 | 创建睡眠记录 | POST | `/api/sleep-record` | 新增睡眠记录 |
| 141 | 睡眠记录列表 | GET | `/api/sleep-record/list?elderId=elder_001` | 按老人查询 |

```bash
# 创建
curl -s -X POST http://120.27.129.78:8080/api/sleep-record \
  -H "Content-Type: application/json" \
  -d '{"elderId":"elder_001","sleepDuration":7.5,"sleepQuality":"良好"}'

# 查询列表
curl -s "http://120.27.129.78:8080/api/sleep-record/list?elderId=elder_001"
curl -s "http://120.27.129.78:8080/api/sleep-record/list?elderId=elder_001&start=2026-06-01T00:00:00&end=2026-06-20T23:59:59"
```

---

## 30. 陪伴交互记录

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 142 | 创建陪伴记录 | POST | `/api/companion-record` | 新增陪伴记录 |
| 143 | 陪伴记录列表 | GET | `/api/companion-record/list?elderId=elder_001` | 按老人查询 |

```bash
curl -s -X POST http://120.27.129.78:8080/api/companion-record \
  -H "Content-Type: application/json" \
  -d '{"elderId":"elder_001","interactionType":"聊天陪伴","content":"日常聊天"}'

curl -s "http://120.27.129.78:8080/api/companion-record/list?elderId=elder_001"
```

---

## 31. VLM 找物品记录

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 144 | 创建VLM记录 | POST | `/api/vlm-record` | 新增找物品记录 |
| 145 | VLM记录列表 | GET | `/api/vlm-record/list?elderId=elder_001` | 按老人查询 |

```bash
curl -s -X POST http://120.27.129.78:8080/api/vlm-record \
  -H "Content-Type: application/json" \
  -d '{"elderId":"elder_001","itemName":"钥匙","location":"客厅","found":true}'

curl -s "http://120.27.129.78:8080/api/vlm-record/list?elderId=elder_001"
```

---

## 32. 监控查看记录

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 146 | 创建查看记录 | POST | `/api/camera-view-record` | 新增查看记录 |
| 147 | 查看记录列表 | GET | `/api/camera-view-record/list?cameraRequestId=mr_001` | 按申请查询 |

```bash
curl -s -X POST http://120.27.129.78:8080/api/camera-view-record \
  -H "Content-Type: application/json" \
  -d '{"cameraRequestId":"mr_001","viewerId":"fam_001","duration":120}'

curl -s "http://120.27.129.78:8080/api/camera-view-record/list?cameraRequestId=mr_001"
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
| 403 | 无权限（如elder登录、无监控权限） |
| 404 | 资源不存在 |
| 409 | 冲突（如手机号已注册） |

---

## 附录 B：接口索引（按路径排序）

```
GET    /api/health                                      # 健康检查
POST   /api/auth/login/web                              # Web端工作人员登录
POST   /api/auth/login/app                              # App端家属登录
POST   /api/auth/login                                  # 旧版登录（兼容，elder不支持）
POST   /api/auth/register                               # 注册（staff/family）
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
POST   /api/health-vital                                # 创建体征记录
GET    /api/health-vital/list?elderId=                  # 体征记录列表
GET    /api/health-vital/latest?elderId=                # 最新体征记录
POST   /api/sleep-record                                # 创建睡眠记录
GET    /api/sleep-record/list?elderId=                  # 睡眠记录列表
POST   /api/companion-record                            # 创建陪伴记录
GET    /api/companion-record/list?elderId=              # 陪伴记录列表
POST   /api/vlm-record                                  # 创建VLM记录
GET    /api/vlm-record/list?elderId=                    # VLM记录列表
POST   /api/camera-view-record                          # 创建查看记录
GET    /api/camera-view-record/list?cameraRequestId=    # 查看记录列表
```

**总计：147 个 API 接口**
