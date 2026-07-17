# 安心伴（Anxinban）后端 RESTful API 接口参考手册 v3.1

> **公网地址**: `http://120.27.129.78:8080`
> **本地地址**: `http://localhost:8080`
> **生成日期**: 2026-07-17（v3.1修订）
> **统一响应格式**: `{"code": 200, "message": "success", "data": {...}}`
> **Content-Type**: 所有 POST/PUT 请求需 `Content-Type: application/json`，文件上传用 `multipart/form-data`
> **HTTP状态码映射**: `200`-成功, `201`-创建成功, `400`-请求参数错误, `401`-未认证, `403`-无权限, `404`-资源不存在, `500`-服务器内部错误

---

## 目录

1. [健康检查](#1-健康检查)
2. [认证授权](#2-认证授权)
3. [老人管理](#3-老人管理)
4. [工作人员管理](#4-工作人员管理)
5. [仪表盘](#5-仪表盘)
6. [告警管理](#6-告警管理)
7. [告警管理-兼容版](#7-告警管理-兼容版)
8. [工单管理](#8-工单管理)
9. [工单管理-兼容版](#9-工单管理-兼容版)
10. [设备管理](#10-设备管理)
11. [设备数据与模拟器](#11-设备数据与模拟器)
12. [设备数据上传](#12-设备数据上传)
13. [健康管理](#13-健康管理)
14. [健康管理-兼容版](#14-健康管理-兼容版)
15. [健康记录](#15-健康记录)
16. [健康体征记录](#16-健康体征记录)
17. [睡眠记录](#17-睡眠记录)
18. [陪伴记录](#18-陪伴记录)
19. [VLM记录](#19-vlm记录)
20. [摄像头查看记录](#20-摄像头查看记录)
21. [SOS紧急求助](#21-sos紧急求助)
22. [干预管理](#22-干预管理)
23. [通知管理](#23-通知管理)
24. [服务请求](#24-服务请求)
25. [监控申请/授权](#25-监控申请授权)
26. [紧急联系人](#26-紧急联系人)
27. [文件上传](#27-文件上传)
28. [智能体管理](#28-智能体管理)
29. [云端智能体](#29-云端智能体)
30. [本地智能体](#30-本地智能体)
31. [AI服务](#31-ai服务)
32. [AI兼容版](#32-ai兼容版)
33. [生命体征独立查询](#33-生命体征独立查询)
34. [模拟器](#34-模拟器)
35. [老人管理-兼容版](#35-老人管理-兼容版)
36. [附录：通用数据结构](#附录通用数据结构)

---

## 1. 健康检查

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 1 | 根路径 | GET | `/` | 返回服务基本信息 |
| 2 | 健康检查 | GET | `/api/health` | 检测服务与数据库连接状态 |

### 1.1 根路径

```bash
curl -s http://120.27.129.78:8080/
```

预期返回：
```json
{
  "code": 200, "message": "success",
  "data": {
    "service": "anxinban-backend",
    "version": "1.0.0",
    "health": "/api/health",
    "docs": "/docs"
  }
}
```

### 1.2 健康检查

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
| 3 | Web端登录 | POST | `/api/auth/login/web` | 工作人员登录 |
| 4 | App端登录 | POST | `/api/auth/login/app` | 家属登录 |
| 5 | 旧版登录（兼容） | POST | `/api/auth/login` | 需传userType，elder不支持 |
| 6 | 用户注册 | POST | `/api/auth/register` | staff/family注册 |
| 7 | 重置密码 | POST | `/api/auth/reset-password` | staff/family重置密码 |
| 8 | 用户登出 | POST | `/api/auth/logout` | 登出 |
| 9 | 当前用户信息 | GET | `/api/auth/me?phone=xxx` | 根据手机号查用户信息 |

### 2.1 Web端工作人员登录（推荐）

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/login/web \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900000000","password":"123456"}'
```

请求体字段：
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号 |
| password | String | 是 | 密码 |

成功返回：
```json
{
  "code": 200, "message": "success",
  "data": {
    "userId": "STF-f65acafd",
    "name": "李新成",
    "phone": "13900000000",
    "role": "staff",
    "userType": "staff",
    "token": null
  }
}
```

### 2.2 App端家属登录（推荐）

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/login/app \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138011","password":"123456"}'
```

请求体字段同 2.1。

### 2.3 旧版登录（兼容保留）

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900000000","password":"123456","userType":"staff"}'
```

请求体字段：`phone`、`password`、`userType`（`staff` 或 `family`，`elder`不支持）

### 2.4 注册

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"phone":"13911111111","password":"123456","userType":"staff","name":"新员工"}'
```

请求体字段：
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号 |
| password | String | 是 | 密码 |
| userType | String | 是 | staff 或 family |
| name | String | 否 | 姓名 |

### 2.5 重置密码

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900000000","newPassword":"654321"}'
```

### 2.6 登出

```bash
curl -s -X POST http://120.27.129.78:8080/api/auth/logout
```

### 2.7 获取当前用户信息

```bash
curl -s "http://120.27.129.78:8080/api/auth/me?phone=13900000000"
```

---

## 3. 老人管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 10 | 创建老人 | POST | `/api/elder` | 创建老人档案 |
| 11 | 获取老人详情 | GET | `/api/elder/{elderId}` | 根据ID获取老人信息 |
| 12 | 获取老人完整详情 | GET | `/api/elder/detail/{elderId}` | 含健康/设备/告警/工单 |
| 13 | 老人列表(分页) | GET | `/api/elder/list` | 支持多条件筛选分页 |
| 14 | 获取家属绑定的老人 | GET | `/api/elder/bound?familyId=xxx` | 家属查看绑定老人 |
| 15 | 更新老人信息 | PUT | `/api/elder/{elderId}` | 更新老人档案 |
| 16 | 删除老人 | DELETE | `/api/elder/{elderId}` | 删除老人（软删除） |
| 17 | 获取老人设备列表 | GET | `/api/elder/{elderId}/devices` | 老人绑定的所有设备 |
| 18 | 实时健康数据 | GET | `/api/elder/{elderId}/health/realtime` | 老人最新健康体征 |
| 19 | 健康历史趋势 | GET | `/api/elder/{elderId}/health/history` | 健康数据历史趋势 |
| 20 | 获取摄像头流地址 | GET | `/api/elder/{elderId}/camera-stream` | 监控摄像头推流地址 |

### 3.1 创建老人

```bash
curl -s -X POST http://120.27.129.78:8080/api/elder \
  -H "Content-Type: application/json" \
  -d '{
    "name": "测试老人",
    "age": 75,
    "gender": "男",
    "address": "北京市朝阳区测试路1号",
    "building": "1号楼",
    "roomNumber": "1-101",
    "phone": "13900000001",
    "password": "123456",
    "healthStatus": "normal",
    "healthNote": "无",
    "familyPhone": "13800000001",
    "communityId": "community_001"
  }'
```

### 3.2 获取老人详情

```bash
curl -s http://120.27.129.78:8080/api/elder/elder_001
```

返回示例：
```json
{
  "code": 200, "message": "success",
  "data": {
    "elderId": "elder_001",
    "name": "张三",
    "age": 78,
    "gender": "男",
    "address": "北京市朝阳区建国路88号",
    "building": "1号楼",
    "roomNumber": "1-201",
    "phone": "13912345601",
    "healthStatus": "warning",
    "healthStatusText": "关注",
    "healthNote": "高血压,糖尿病",
    "familyPhone": "13800138002",
    "communityId": "community_001",
    "avatar": "/uploads/avatar/elder_001.jpg",
    "hasCamera": true,
    "cameraAuthUntil": 0,
    "cameraPending": false,
    "lastOnline": "2025-05-17 09:00:00"
  }
}
```

### 3.3 获取老人完整详情

```bash
curl -s http://120.27.129.78:8080/api/elder/detail/elder_001
```

返回 `data` 中包含：`elder`（基础信息）、`health`（健康数据）、`devices`（设备列表）、`alarms`（告警列表）、`orders`（工单列表）。

### 3.4 老人列表（分页）

```bash
curl -s "http://120.27.129.78:8080/api/elder/list?page=1&pageSize=10"

# 带筛选条件
curl -s "http://120.27.129.78:8080/api/elder/list?building=1号楼&healthStatus=warning&page=1&pageSize=10"
```

请求参数（均为可选）：

| 参数 | 类型 | 说明 |
|------|------|------|
| name | String | 老人姓名（模糊搜索） |
| building | String | 楼栋号 |
| roomNumber | String | 房间号 |
| healthStatus | String | 健康状态：normal/warning/danger |
| page | int | 页码，默认1 |
| pageSize | int | 每页条数，默认20 |

### 3.5 获取家属绑定的老人

```bash
curl -s "http://120.27.129.78:8080/api/elder/bound?familyId=family_001"
```

### 3.6 更新老人信息

```bash
curl -s -X PUT http://120.27.129.78:8080/api/elder/elder_001 \
  -H "Content-Type: application/json" \
  -d '{"healthNote": "高血压,糖尿病,新增:轻度关节炎"}'
```

### 3.7 删除老人

```bash
curl -s -X DELETE http://120.27.129.78:8080/api/elder/elder_001
```

### 3.8 获取老人设备列表

```bash
curl -s http://120.27.129.78:8080/api/elder/elder_001/devices
```

### 3.9 实时健康数据

```bash
curl -s http://120.27.129.78:8080/api/elder/elder_001/health/realtime
```

返回示例：
```json
{
  "code": 200, "message": "success",
  "data": {
    "heartRate": 112,
    "spo2": 91,
    "temperature": 37.4,
    "systolic": 135,
    "diastolic": 85
  }
}
```

### 3.10 健康历史趋势

```bash
curl -s "http://120.27.129.78:8080/api/elder/elder_001/health/history?type=heart_rate&range=7d"
```

请求参数：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | String | 是 | heart_rate / spo2 / temperature / blood_pressure |
| range | String | 是 | 1d / 7d / 30d |

### 3.11 获取摄像头流地址

```bash
curl -s "http://120.27.129.78:8080/api/elder/elder_001/camera-stream?staffId=staff_001"
```

> **权限**: 需要有效的监控授权，否则返回 403。

---

## 4. 工作人员管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 21 | 工作人员列表 | GET | `/api/staff/list` | 按社区查列表 |
| 22 | 创建工作人员 | POST | `/api/staff` | 创建工作人员 |
| 23 | 更新工作人员 | PUT | `/api/staff/{staffId}` | 更新工作人员信息 |
| 24 | 删除工作人员 | DELETE | `/api/staff/{staffId}` | 删除工作人员 |

### 4.1 工作人员列表

```bash
curl -s "http://120.27.129.78:8080/api/staff/list?communityId=community_001"
```

### 4.2 创建工作人员

```bash
curl -s -X POST http://120.27.129.78:8080/api/staff \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newstaff",
    "name": "新员工",
    "phone": "13900000099",
    "password": "123456",
    "role": "staff",
    "communityId": "community_001"
  }'
```

### 4.3 更新工作人员

```bash
curl -s -X PUT http://120.27.129.78:8080/api/staff/staff_003 \
  -H "Content-Type: application/json" \
  -d '{"role": "supervisor"}'
```

### 4.4 删除工作人员

```bash
curl -s -X DELETE http://120.27.129.78:8080/api/staff/staff_003
```

---

## 5. 仪表盘

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 25 | 仪表盘统计 | GET | `/api/dashboard/stats` | 首页统计数据 |
| 26 | 楼栋列表 | GET | `/api/dashboard/buildings` | 获取楼栋列表 |
| 27 | 仪表盘摘要(兼容) | GET | `/api/dashboard/summary` | 兼容旧版仪表盘 |

### 5.1 仪表盘统计

```bash
curl -s http://120.27.129.78:8080/api/dashboard/stats
```

返回示例：
```json
{
  "code": 200, "message": "success",
  "data": {
    "elderCount": 6,
    "deviceCount": 17,
    "alarmCount": 40,
    "workOrderCount": 27,
    "staffCount": 5,
    "onlineDeviceCount": 15,
    "pendingAlarmCount": 15,
    "pendingOrderCount": 22
  }
}
```

### 5.2 楼栋列表

```bash
curl -s http://120.27.129.78:8080/api/dashboard/buildings
```

### 5.3 仪表盘摘要(兼容)

```bash
curl -s http://120.27.129.78:8080/api/dashboard/summary
```

---

## 6. 告警管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 28 | 创建告警 | POST | `/api/alarm` | 创建告警事件 |
| 29 | 告警详情 | GET | `/api/alarm/{alarmId}` | 告警详细信息 |
| 30 | 告警列表(分页) | GET | `/api/alarm/list` | 多条件筛选分页 |
| 31 | 闯入告警列表 | GET | `/api/alarm/intrusion/list` | 闯入类告警 |
| 32 | 闯入快照 | GET | `/api/alarm/intrusion/{alarmId}/snapshot` | 闯入抓拍图片URL |
| 33 | 确认告警 | PUT | `/api/alarm/{alarmId}/acknowledge` | 确认收到告警 |
| 34 | 处理告警 | PUT | `/api/alarm/{alarmId}/resolve` | 解决告警 |
| 35 | 标记已读 | PUT | `/api/alarm/{alarmId}/read` | 标记告警已读 |
| 36 | 未读告警数 | GET | `/api/alarm/unread-count?elderId=xxx` | 老人未读告警数 |

### 6.1 创建告警

```bash
curl -s -X POST http://120.27.129.78:8080/api/alarm \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "deviceId": "dev_001",
    "alarmType": "heart_rate",
    "alarmLevel": "high",
    "description": "心率持续偏高超过120bpm",
    "building": "1号楼",
    "roomNumber": "1-201"
  }'
```

### 6.2 告警详情

```bash
curl -s http://120.27.129.78:8080/api/alarm/alarm_001
```

### 6.3 告警列表（分页）

```bash
# 全部告警
curl -s "http://120.27.129.78:8080/api/alarm/list?page=1&pageSize=20"

# 按条件筛选
curl -s "http://120.27.129.78:8080/api/alarm/list?elderId=elder_001&alarmType=fall&status=pending&page=1&pageSize=20"
```

请求参数（均为可选）：
| 参数 | 类型 | 说明 |
|------|------|------|
| elderId | String | 老人ID |
| deviceId | String | 设备ID |
| alarmType | String | 告警类型 |
| status | String | 状态：pending/handled |
| startTime | String | 开始时间 |
| endTime | String | 结束时间 |
| page | int | 页码 |
| pageSize | int | 每页条数 |

### 6.4 确认告警

```bash
curl -s -X PUT http://120.27.129.78:8080/api/alarm/alarm_002/acknowledge \
  -H "Content-Type: application/json" \
  -d '{"handler": "staff_001", "handleTime": "2025-05-17 09:20:00"}'
```

### 6.5 处理告警

```bash
curl -s -X PUT http://120.27.129.78:8080/api/alarm/alarm_003/resolve \
  -H "Content-Type: application/json" \
  -d '{"handler": "staff_001", "handleTime": "2025-05-17 08:30:00", "remark": "已确认是访客"}'
```

### 6.6 标记已读

```bash
curl -s -X PUT http://120.27.129.78:8080/api/alarm/alarm_003/read
```

### 6.7 未读告警数

```bash
curl -s "http://120.27.129.78:8080/api/alarm/unread-count?elderId=elder_001"
```

---

## 7. 告警管理-兼容版

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 37 | 告警列表(兼容) | GET | `/api/alarms?page=1&pageSize=20` | snake_case字段 |
| 38 | 告警详情(兼容) | GET | `/api/alarms/{alarm_id}` | snake_case字段 |
| 39 | 告警转工单 | POST | `/api/alarms/{alarm_id}/to-work-order` | 转换为工单 |
| 40 | 标记已读(兼容) | POST | `/api/alarms/{alarm_id}/mark-read` | 标记已读 |
| 41 | 处理告警(兼容) | POST | `/api/alarms/{alarm_id}/handle` | 处理告警 |

### 7.1 告警列表(兼容)

```bash
curl -s "http://120.27.129.78:8080/api/alarms?page=1&pageSize=10"
```

### 7.2 告警转工单

```bash
curl -s -X POST http://120.27.129.78:8080/api/alarms/alarm_001/to-work-order
```

### 7.3 处理告警(兼容)

```bash
curl -s -X POST http://120.27.129.78:8080/api/alarms/alarm_001/handle \
  -H "Content-Type: application/json" \
  -d '{"handler_id": "staff_001", "handler_name": "张建国", "handle_note": "已处理"}'
```

---

## 8. 工单管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 42 | 工单列表(分页) | GET | `/api/work-order/list` | 多条件筛选分页 |
| 43 | 工单详情 | GET | `/api/work-order/{orderId}` | 工单详细信息 |
| 44 | 创建工单 | POST | `/api/work-order` | 创建工单 |
| 45 | 更新工单状态 | PUT | `/api/work-order/{orderId}/status` | 更改状态 |
| 46 | 指派处理人 | PUT | `/api/work-order/{orderId}/assign` | 分配处理人 |

### 8.1 工单列表

```bash
curl -s "http://120.27.129.78:8080/api/work-order/list?page=1&pageSize=10"

# 按状态筛选
curl -s "http://120.27.129.78:8080/api/work-order/list?status=待处理&page=1&pageSize=10"

# 按关键词搜索
curl -s "http://120.27.129.78:8080/api/work-order/list?keyword=跌倒&page=1&pageSize=10"
```

请求参数（均为可选）：

| 参数 | 类型 | 说明 |
|------|------|------|
| keyword | String | 关键词搜索 |
| elderName | String | 老人姓名 |
| status | String | 状态：待处理/处理中/已完成 |
| page | int | 页码 |
| pageSize | int | 每页条数 |

### 8.2 工单详情

```bash
curl -s http://120.27.129.78:8080/api/work-order/wo_001
```

### 8.3 创建工单

```bash
curl -s -X POST http://120.27.129.78:8080/api/work-order \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "orderType": "设备检查",
    "description": "手环电量异常需检查",
    "creatorId": "staff_001"
  }'
```

### 8.4 更新工单状态

```bash
curl -s -X PUT http://120.27.129.78:8080/api/work-order/wo_001/status \
  -H "Content-Type: application/json" \
  -d '{"status": "已完成"}'
```

### 8.5 指派处理人

```bash
curl -s -X PUT http://120.27.129.78:8080/api/work-order/wo_001/assign \
  -H "Content-Type: application/json" \
  -d '{"handlerId": "staff_002", "handlerName": "李秀英"}'
```

---

## 9. 工单管理-兼容版

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 47 | 工单列表(兼容) | GET | `/api/work-orders?page=1&pageSize=20` | snake_case |
| 48 | 创建工单(兼容) | POST | `/api/work-orders` | snake_case |
| 49 | 指派处理人(兼容) | PUT | `/api/work-orders/{work_order_id}/assign` | 分配处理人 |
| 50 | 完成工单(兼容) | PUT | `/api/work-orders/{work_order_id}/complete` | 完成工单 |

### 9.1 工单列表(兼容)

```bash
curl -s "http://120.27.129.78:8080/api/work-orders?page=1&pageSize=10"
```

### 9.2 创建工单(兼容)

```bash
curl -s -X POST http://120.27.129.78:8080/api/work-orders \
  -H "Content-Type: application/json" \
  -d '{
    "elder_id": "elder_001",
    "order_type": "紧急巡检",
    "description": "老人设备报警，需要上门检查"
  }'
```

### 9.3 完成工单(兼容)

```bash
curl -s -X PUT http://120.27.129.78:8080/api/work-orders/wo_001/complete
```

---

## 10. 设备管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 51 | 注册设备 | POST | `/api/device/register` | 注册新设备 |
| 52 | 设备详情 | GET | `/api/device/{deviceId}` | 设备详细信息 |
| 53 | 设备列表(分页) | GET | `/api/device/list` | 多条件分页 |
| 54 | 更新设备状态 | PUT | `/api/device/{deviceId}/status` | 更新在线状态 |
| 55 | 上传传感器数据 | POST | `/api/device/{deviceId}/sensor-data` | 批量上传传感器数据 |
| 56 | 发送设备指令 | POST | `/api/device/{deviceId}/command` | 下发控制命令 |
| 57 | 查询传感器历史 | GET | `/api/device/{deviceId}/sensor-data` | 查传感器历史数据 |

### 10.1 注册设备

```bash
curl -s -X POST http://120.27.129.78:8080/api/device/register \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": "dev_018",
    "elderId": "elder_001",
    "deviceType": "手环",
    "deviceName": "智能手环2代",
    "location": "随身佩戴",
    "building": "1号楼",
    "room": "1-201"
  }'
```

### 10.2 设备详情

```bash
curl -s http://120.27.129.78:8080/api/device/dev_001
```

### 10.3 设备列表(分页)

```bash
curl -s "http://120.27.129.78:8080/api/device/list?page=1&pageSize=20"

# 按类型筛选
curl -s "http://120.27.129.78:8080/api/device/list?deviceType=摄像头&status=online&page=1&pageSize=10"
```

### 10.4 更新设备状态

```bash
curl -s -X PUT http://120.27.129.78:8080/api/device/dev_001/status \
  -H "Content-Type: application/json" \
  -d '{"status": "offline", "lastHeartbeat": "2025-05-17 18:00:00"}'
```

### 10.5 上传传感器数据

```bash
curl -s -X POST http://120.27.129.78:8080/api/device/dev_001/sensor-data \
  -H "Content-Type: application/json" \
  -d '{
    "sensorDataList": [
      {"sensorType": "heart_rate", "value": 72, "unit": "bpm", "timestamp": "2025-05-17 10:30:00"},
      {"sensorType": "spo2", "value": 98, "unit": "%", "timestamp": "2025-05-17 10:30:00"}
    ]
  }'
```

### 10.6 发送设备指令

```bash
curl -s -X POST http://120.27.129.78:8080/api/device/dev_002/command \
  -H "Content-Type: application/json" \
  -d '{"commandId": "cmd_001", "commandType": "startRecord", "parameters": {"duration": 30}}'
```

### 10.7 查询传感器历史数据

```bash
curl -s "http://120.27.129.78:8080/api/device/dev_001/sensor-data?sensorType=heart_rate&startTime=2025-05-17&endTime=2025-05-18&page=1&pageSize=50"
```

---

## 11. 设备数据与模拟器

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 58 | 温湿度历史 | GET | `/api/device/sensor/temperature-humidity` | 温湿度历史数据 |
| 59 | 传感器分类查询 | GET | `/api/device/sensor/{sensorType}` | 按类型查传感器数据 |
| 60 | 设备告警事件 | GET | `/api/device/alarm` | 设备告警分页 |
| 61 | 模拟器状态 | GET | `/api/device/simulator/status` | 模拟器运行状态 |
| 62 | 启动模拟器 | POST | `/api/device/simulator/start` | 启动Mqtt模拟器 |
| 63 | 停止模拟器 | POST | `/api/device/simulator/stop` | 停止Mqtt模拟器 |
| 64 | 触发指纹成功 | POST | `/api/device/simulator/trigger/fingerprint-success` | 模拟指纹验证成功 |
| 65 | 触发指纹失败 | POST | `/api/device/simulator/trigger/fingerprint-fail` | 模拟指纹验证失败 |
| 66 | 触发窗帘控制 | POST | `/api/device/simulator/trigger/curtain` | 模拟窗帘操作 |
| 67 | 触发蜂鸣器 | POST | `/api/device/simulator/trigger/buzzer` | 模拟蜂鸣器报警 |
| 68 | 触发灯光控制 | POST | `/api/device/simulator/trigger/light` | 模拟灯光操作 |
| 69 | 触发手表紧急呼叫 | POST | `/api/device/simulator/trigger/watch-emergency` | 模拟手表SOS |
| 70 | 触发跌倒检测 | POST | `/api/device/simulator/trigger/fall-detection` | 模拟跌倒检测 |
| 71 | 触找物 | POST | `/api/device/simulator/trigger/find-item` | 模拟找物 |
| 72 | AI异步对话 | POST | `/api/ai/chat` | AI对话(异步) |
| 73 | AI异步找物 | POST | `/api/ai/find-item` | AI找物(异步) |

### 11.1 温湿度历史

```bash
curl -s "http://120.27.129.78:8080/api/device/sensor/temperature-humidity?room=living-room&limit=10"
```

### 11.2 传感器分类查询

```bash
curl -s "http://120.27.129.78:8080/api/device/sensor/heart_rate?deviceId=dev_001&startTime=2025-05-17&endTime=2025-05-18&limit=50"
```

### 11.3 设备告警事件

```bash
curl -s "http://120.27.129.78:8080/api/device/alarm?alarmType=fall&alarmLevel=critical&page=0&size=10"
```

### 11.4 模拟器操作

```bash
# 启动模拟器
curl -s -X POST http://120.27.129.78:8080/api/device/simulator/start

# 停止模拟器
curl -s -X POST http://120.27.129.78:8080/api/device/simulator/stop

# 查看状态
curl -s http://120.27.129.78:8080/api/device/simulator/status

# 触发跌倒检测
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/fall-detection?room=bedroom"

# 触发手表紧急呼叫
curl -s -X POST http://120.27.129.78:8080/api/device/simulator/trigger/watch-emergency

# 触发找物
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/find-item?itemName=眼镜&room=living-room"

# 触发窗帘控制
curl -s -X POST "http://120.27.129.78:8080/api/device/simulator/trigger/curtain?command=close&percent=100"
```

### 11.5 AI异步对话

```bash
curl -s -X POST "http://120.27.129.78:8080/api/ai/chat?userId=elder_001&content=今天天气怎么样"

# 可选参数 houseId
curl -s -X POST "http://120.27.129.78:8080/api/ai/chat?userId=elder_001&content=今天天气怎么样&houseId=house_001"
```

请求参数：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户/老人ID |
| content | String | 是 | 对话内容 |
| houseId | String | 否 | 房屋ID(用于多房屋场景) |

### 11.6 AI异步找物

```bash
curl -s -X POST "http://120.27.129.78:8080/api/ai/find-item?userId=elder_001&itemName=眼镜&room=living-room"

# 可选参数 houseId
curl -s -X POST "http://120.27.129.78:8080/api/ai/find-item?userId=elder_001&itemName=眼镜&room=living-room&houseId=house_001"
```

请求参数：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户/老人ID |
| itemName | String | 是 | 物品名称 |
| room | String | 否 | 房间名，默认living-room |
| houseId | String | 否 | 房屋ID(用于多房屋场景) |

---

## 12. 设备数据上传

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 75 | 设备数据上传 | POST | `/api/device/upload` | 上传健康数据并自动AI分析+告警生成 |

### 12.1 设备数据上传（含AI分析）

> 核心接口：设备上传健康数据后，系统自动调用AI分析服务，根据分析结果生成告警和工单。

```bash
curl -s -X POST http://120.27.129.78:8080/api/device/upload \
  -H "Content-Type: application/json" \
  -d '{
    "elder_id": "elder_001",
    "heart_rate": 112,
    "spo2": 91,
    "temperature": 37.4,
    "activity_status": "长时间静止",
    "fall_status": "正常",
    "location": "1号楼1-201客厅"
  }'
```

请求体字段（snake_case格式）：
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| elder_id | String | 是 | 老人ID |
| heart_rate | Integer | 否 | 心率(bpm) |
| spo2 | Integer | 否 | 血氧饱和度(%) |
| temperature | Double | 否 | 体温(℃) |
| activity_status | String | 否 | 活动状态：行走/坐着/长时间静止等 |
| fall_status | String | 否 | 跌倒状态：正常/疑似跌倒 |
| location | String | 否 | 位置描述 |

返回示例：
```json
{
  "code": 200, "message": "success",
  "data": {
    "saved": true,
    "sensor_count": 5,
    "risk_level": "高风险",
    "risk_reason": "检测到跌倒事件",
    "need_alarm": true,
    "alarm_id": "alarm_a1b2c3d4",
    "source": "python_ai_service"
  }
}
```

> 设备数据上传是整个系统最核心的数据入口，所有健康监测数据通过此接口上报后，自动触发：
> 1. 传感器数据存储
> 2. Python AI服务风险分析（失败时回退到Java规则引擎）
> 3. 风险告警生成（高风险/中风险）
> 4. AI分析记录持久化
> 5. 家属通知生成
> 6. 社区建议生成

---

## 13. 健康管理

（仅含 `/api/health` 根路径，已在第1节覆盖）

---

## 14. 健康管理-兼容版

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 75 | 最新健康数据(兼容) | GET | `/api/health/latest/{elder_id}` | snake_case版 |

### 14.1 最新健康数据(兼容)

```bash
curl -s http://120.27.129.78:8080/api/health/latest/elder_001
```

---

## 15. 健康记录

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 76 | 创建/更新健康记录 | POST | `/api/health-record` | 创建或更新 |
| 77 | 按老人查询健康记录 | GET | `/api/health-record/by-elder/{elderId}` | 查老人健康档案 |
| 78 | 按ID查询健康记录 | GET | `/api/health-record/{recordId}` | 按记录ID查询 |

### 15.1 创建/更新健康记录

```bash
curl -s -X POST http://120.27.129.78:8080/api/health-record \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "medicalHistory": "高血压10年,糖尿病5年",
    "allergyHistory": "对青霉素过敏",
    "commonMedications": "降压药,降糖药,阿司匹林",
    "bloodType": "A",
    "hospitalizationInfo": "2024年3月因高血压住院7天",
    "remarks": "需定期监测血糖血压"
  }'
```

### 15.2 按老人查询健康记录

```bash
curl -s http://120.27.129.78:8080/api/health-record/by-elder/elder_001
```

---

## 16. 健康体征记录

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 79 | 创建体征记录 | POST | `/api/health-vital` | 记录健康体征 |
| 80 | 体征记录列表 | GET | `/api/health-vital/list` | 按老人和时间范围查询 |
| 81 | 最新体征记录 | GET | `/api/health-vital/latest` | 老人最新一条体征 |

### 16.1 创建体征记录

```bash
curl -s -X POST http://120.27.129.78:8080/api/health-vital \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "heartRate": 75,
    "spo2": 98,
    "systolic": 130,
    "diastolic": 82,
    "temperature": 36.5,
    "measuredAt": "2025-05-17 10:30:00",
    "source": "手环"
  }'
```

### 16.2 体征记录列表

```bash
curl -s "http://120.27.129.78:8080/api/health-vital/list?elderId=elder_001&start=2025-05-01&end=2025-05-18"
```

### 16.3 最新体征记录

```bash
curl -s "http://120.27.129.78:8080/api/health-vital/latest?elderId=elder_001"
```

---

## 17. 睡眠记录

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 82 | 创建睡眠记录 | POST | `/api/sleep-record` | 创建睡眠记录 |
| 83 | 睡眠记录列表 | GET | `/api/sleep-record/list` | 按老人和时间范围查询 |

### 17.1 创建睡眠记录

```bash
curl -s -X POST http://120.27.129.78:8080/api/sleep-record \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "inBed": true,
    "bedTime": "22:30",
    "deepSleepPercent": 35,
    "wakeCount": 2,
    "qualityScore": 78,
    "recordedAt": "2025-05-17 07:00:00"
  }'
```

### 17.2 睡眠记录列表

```bash
curl -s "http://120.27.129.78:8080/api/sleep-record/list?elderId=elder_001&start=2025-05-01&end=2025-05-18"
```

---

## 18. 陪伴记录

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 84 | 创建陪伴记录 | POST | `/api/companion-record` | 创建陪伴记录 |
| 85 | 陪伴记录列表 | GET | `/api/companion-record/list` | 按老人查询 |

### 18.1 创建陪伴记录

```bash
curl -s -X POST http://120.27.129.78:8080/api/companion-record \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "emotion": "开心",
    "emotionColor": "#4CAF50",
    "interactionTime": "2025-05-17 09:00:00",
    "summary": "老人与AI助手聊天，情绪愉悦"
  }'
```

### 18.2 陪伴记录列表

```bash
curl -s "http://120.27.129.78:8080/api/companion-record/list?elderId=elder_001"
```

---

## 19. VLM记录

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 86 | 创建VLM找物记录 | POST | `/api/vlm-record` | 视觉找物记录 |
| 87 | VLM记录列表 | GET | `/api/vlm-record/list` | 按老人查询 |

### 19.1 创建VLM记录

```bash
curl -s -X POST http://120.27.129.78:8080/api/vlm-record \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "item": "眼镜",
    "location": "客厅",
    "question": "我的眼镜在哪？",
    "answer": "眼镜在客厅茶几上",
    "result": "found",
    "queryTime": "2025-05-17 08:15:00"
  }'
```

### 19.2 VLM记录列表

```bash
curl -s "http://120.27.129.78:8080/api/vlm-record/list?elderId=elder_001"
```

---

## 20. 摄像头查看记录

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 88 | 创建查看记录 | POST | `/api/camera-view-record` | 创建摄像头查看记录 |
| 89 | 查看记录列表 | GET | `/api/camera-view-record/list` | 按请求ID查询 |

### 20.1 创建查看记录

```bash
curl -s -X POST http://120.27.129.78:8080/api/camera-view-record \
  -H "Content-Type: application/json" \
  -d '{
    "cameraRequestId": "cr_001",
    "cameraType": "客厅摄像头",
    "staffId": "staff_001",
    "viewTime": "2025-05-17 09:30:00",
    "duration": 120
  }'
```

### 20.2 查看记录列表

```bash
curl -s "http://120.27.129.78:8080/api/camera-view-record/list?cameraRequestId=cr_001"
```

---

## 21. SOS紧急求助

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 90 | 触发SOS | POST | `/api/sos` | 创建SOS求助 |
| 91 | SOS详情 | GET | `/api/sos/{sosId}` | 查看SOS详情 |
| 92 | SOS列表 | GET | `/api/sos/list?elderId=xxx` | 老人SOS记录 |
| 93 | 处理SOS | PUT | `/api/sos/{sosId}/handle?handlerId=xxx` | 处理SOS求助 |

### 21.1 触发SOS

```bash
curl -s -X POST http://120.27.129.78:8080/api/sos \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "triggerTime": "2025-05-17 10:30:00",
    "location": "1号楼1-201客厅"
  }'
```

### 21.2 SOS列表

```bash
curl -s "http://120.27.129.78:8080/api/sos/list?elderId=elder_001"
```

### 21.3 处理SOS

```bash
curl -s -X PUT "http://120.27.129.78:8080/api/sos/sos_001/handle?handlerId=staff_001"
```

---

## 22. 干预管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 94 | 创建干预 | POST | `/api/intervention` | 创建干预任务 |
| 95 | 干预详情 | GET | `/api/intervention/{interventionId}` | 干预详细信息 |
| 96 | 干预列表 | GET | `/api/intervention/list` | 多条件筛选 |
| 97 | 更新干预 | PUT | `/api/intervention/{interventionId}` | 更新干预信息 |
| 98 | 完成干预 | PUT | `/api/intervention/{interventionId}/complete` | 完成干预并记录结果 |

### 22.1 创建干预

```bash
curl -s -X POST http://120.27.129.78:8080/api/intervention \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "triggerReason": "情绪识别-焦虑",
    "musicType": "music",
    "startTime": "2025-05-17 15:00:00",
    "durationMinutes": 15,
    "beforeState": "焦虑不安、心率加快"
  }'
```

### 22.2 干预列表

```bash
curl -s "http://120.27.129.78:8080/api/intervention/list?elderId=elder_001&status=completed&page=1&size=10"
```

### 22.3 完成干预

```bash
curl -s -X PUT http://120.27.129.78:8080/api/intervention/int_001/complete \
  -H "Content-Type: application/json" \
  -d '{"status": "completed", "result": "completed", "completeTime": "2025-05-17 15:15:00"}'
```

---

## 23. 通知管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 99 | 通知列表(分页) | GET | `/api/notification/list` | 用户通知分页 |
| 100 | 标记单条已读 | POST | `/api/notification/{notificationId}/read` | 标记已读 |
| 101 | 全部已读 | POST | `/api/notification/read-all` | 标记全部已读 |
| 102 | 未读数量 | GET | `/api/notification/unread-count` | 未读通知数 |

### 23.1 通知列表

```bash
curl -s "http://120.27.129.78:8080/api/notification/list?userId=staff_001&userType=staff&page=1&pageSize=10"
```

### 23.2 标记已读

```bash
curl -s -X POST http://120.27.129.78:8080/api/notification/notif_001/read
```

### 23.3 全部已读

```bash
curl -s -X POST "http://120.27.129.78:8080/api/notification/read-all?userId=staff_001&userType=staff"
```

### 23.4 未读数量

```bash
curl -s "http://120.27.129.78:8080/api/notification/unread-count?userId=staff_001&userType=staff"
```

---

## 24. 服务请求

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 103 | 提交服务请求 | POST | `/api/service-request` | 家属提交服务请求 |
| 104 | 我的请求列表 | GET | `/api/service-request/my-list` | 家属查看自己请求 |
| 105 | 全部请求列表(管理员) | GET | `/api/service-request/list` | 管理员查看全部 |
| 106 | 请求状态 | GET | `/api/service-request/{requestId}/status` | 查看请求状态 |
| 107 | 转工单 | POST | `/api/service-request/{requestId}/convert` | 服务请求转工单 |
| 108 | 拒绝请求 | POST | `/api/service-request/{requestId}/reject` | 拒绝服务请求 |

### 24.1 提交服务请求

```bash
curl -s -X POST http://120.27.129.78:8080/api/service-request \
  -H "Content-Type: application/json" \
  -d '{
    "familyId": "family_001",
    "elderId": "elder_001",
    "requestType": "设备维修",
    "content": "请求安排人员上门检查客厅摄像头"
  }'
```

### 24.2 我的请求列表

```bash
curl -s "http://120.27.129.78:8080/api/service-request/my-list?familyId=family_001"
```

### 24.3 全部请求列表(管理员)

```bash
curl -s "http://120.27.129.78:8080/api/service-request/list?requestType=设备维修&status=pending&page=1&pageSize=10"
```

### 24.4 转工单

```bash
curl -s -X POST http://120.27.129.78:8080/api/service-request/sr_001/convert \
  -H "Content-Type: application/json" \
  -d '{"orderId": "wo_010"}'
```

### 24.5 拒绝请求

```bash
curl -s -X POST http://120.27.129.78:8080/api/service-request/sr_002/reject \
  -H "Content-Type: application/json" \
  -d '{"reason": "不在服务范围内"}'
```

---

## 25. 监控申请/授权

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 109 | 创建监控申请 | POST | `/api/monitor-request` | 人员提交监控申请 |
| 110 | 家属申请列表 | GET | `/api/monitor-request/list/family` | 家属查看申请列表 |
| 111 | 员工申请列表 | GET | `/api/monitor-request/list/staff` | 员工查看申请列表 |
| 112 | 批准申请 | POST | `/api/monitor-request/{requestId}/approve` | 批准监控授权 |
| 113 | 拒绝申请 | POST | `/api/monitor-request/{requestId}/reject` | 拒绝监控申请 |
| 114 | 撤销授权 | POST | `/api/monitor-request/{requestId}/revoke` | 撤销已有授权 |
| 115 | 查看申请结果 | GET | `/api/monitor-request/{requestId}/result` | 查看处理结果 |
| 116 | 权限检查 | GET | `/api/monitor-request/check` | 检查是否有监控权限 |

### 25.1 创建监控申请

```bash
curl -s -X POST http://120.27.129.78:8080/api/monitor-request \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "staffId": "staff_001",
    "staffName": "张建国",
    "staffPhone": "13800138001",
    "reason": "日常巡查需要查看老人状态"
  }'
```

### 25.2 批准/拒绝/撤销

```bash
# 批准
curl -s -X POST http://120.27.129.78:8080/api/monitor-request/mr_001/approve

# 拒绝
curl -s -X POST http://120.27.129.78:8080/api/monitor-request/mr_002/reject

# 撤销
curl -s -X POST http://120.27.129.78:8080/api/monitor-request/mr_001/revoke
```

### 25.3 权限检查

```bash
curl -s "http://120.27.129.78:8080/api/monitor-request/check?elderId=elder_001&staffId=staff_001"
```

---

## 26. 紧急联系人

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 117 | 创建联系人 | POST | `/api/emergency-contact` | 创建紧急联系人 |
| 118 | 联系人详情 | GET | `/api/emergency-contact/{contactId}` | 联系人详细信息 |
| 119 | 按老人查询 | GET | `/api/emergency-contact/list?elderId=xxx` | 老人所有联系人 |
| 120 | 更新联系人 | PUT | `/api/emergency-contact/{contactId}` | 更新联系人信息 |
| 121 | 删除联系人 | DELETE | `/api/emergency-contact/{contactId}` | 删除联系人 |

### 26.1 创建紧急联系人

```bash
curl -s -X POST http://120.27.129.78:8080/api/emergency-contact \
  -H "Content-Type: application/json" \
  -d '{
    "elderId": "elder_001",
    "name": "张小明",
    "phone": "13800138011",
    "relation": "儿子",
    "isPrimary": true,
    "sortOrder": 0
  }'
```

### 26.2 按老人查询

```bash
curl -s "http://120.27.129.78:8080/api/emergency-contact/list?elderId=elder_001"
```

---

## 27. 文件上传

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 122 | 上传头像 | POST | `/api/upload/avatar` | 上传头像图片(multipart) |
| 123 | 删除头像 | DELETE | `/api/upload/avatar` | 恢复默认头像 |
| 124 | 上传快照 | POST | `/api/upload/snapshot` | 上传告警抓拍(multipart) |

### 27.1 上传头像

```bash
curl -s -X POST "http://120.27.129.78:8080/api/upload/avatar?userId=staff_001&role=staff" \
  -F "file=@/path/to/avatar.jpg"
```

请求参数：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | MultipartFile | 是 | 图片文件(表单字段名"file") |
| userId | String | 是 | 用户ID(查询参数) |
| role | String | 否 | 用户角色：staff/family，默认family(查询参数) |

返回：
```json
{"code": 200, "message": "success", "data": {"url": "/uploads/avatars/xxx.jpg"}}
```

### 27.2 删除头像

```bash
curl -s -X DELETE "http://120.27.129.78:8080/api/upload/avatar?userId=staff_001&role=staff"
```

请求参数：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户ID(查询参数) |
| role | String | 否 | 用户角色：staff/family，默认family(查询参数) |

返回：
```json
{"code": 200, "message": "success", "data": {"url": "/uploads/avatars/default.png"}}
```

### 27.3 上传快照

```bash
curl -s -X POST http://120.27.129.78:8080/api/upload/snapshot \
  -F "file=@/path/to/snapshot.jpg"
```

---

## 28. 智能体管理

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 124 | 获取智能体上下文 | GET | `/api/agent/context?elderId=xxx` | 获取老人完整智能体上下文 |

### 28.1 获取智能体上下文

```bash
curl -s "http://120.27.129.78:8080/api/agent/context?elderId=elder_001"
```

返回老人基础信息、健康数据、设备列表、最近告警、工单等完整上下文信息。

---

## 29. 云端智能体

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 126 | 注册云端智能体 | POST | `/api/cloud-agent/register` | 注册/上线云端智能体 |
| 127 | 上报智能体状态 | POST | `/api/cloud-agent/report` | 上报状态心跳 |
| 128 | 同步云端告警 | POST | `/api/cloud-agent/alarm` | 云端智能体同步告警 |
| 129 | 获取智能体配置 | GET | `/api/cloud-agent/{agentId}/config` | 获取配置 |
| 130 | 获取干预任务 | GET | `/api/cloud-agent/{agentId}/interventions` | 获取待执行干预 |
| 131 | 上报干预结果 | POST | `/api/cloud-agent/{agentId}/intervention-result` | 回报干预结果 |
| 132 | 云端对话 | POST | `/api/cloud-agent/chat` | 云端智能体对话记录 |
| 133 | 对话列表(分页) | GET | `/api/cloud-agent/conversations` | 查询对话记录列表 |
| 134 | 对话详情 | GET | `/api/cloud-agent/conversations/{conversationId}` | 查询单条对话详情 |
| 135 | 创建AI建议 | POST | `/api/cloud-agent/advice` | 创建AI建议 |

### 29.1 注册云端智能体

```bash
curl -s -X POST http://120.27.129.78:8080/api/cloud-agent/register \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": "CLOUD-04",
    "agentType": "cloud_agent",
    "status": "online",
    "lastHeartbeat": "2025-05-17 10:00:00",
    "ip": "10.0.0.4",
    "deviceCount": 10,
    "connectedDevices": 8
  }'
```

### 29.2 云端对话

```bash
curl -s -X POST http://120.27.129.78:8080/api/cloud-agent/chat \
  -H "Content-Type: application/json" \
  -d '{
    "conversationId": "conv_007",
    "elderId": "elder_001",
    "agentType": "cloud_agent",
    "userText": "帮我查一下今天的天气",
    "intent": "chat",
    "agentReply": "今天北京晴，气温22-30℃",
    "riskLevel": "low"
  }'
```

### 29.3 对话记录列表（分页）

```bash
curl -s "http://120.27.129.78:8080/api/cloud-agent/conversations?elderId=elder_001&page=1&pageSize=20"
```

请求参数：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| elderId | String | 否 | 老人ID筛选 |
| page | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页条数，默认20 |

### 29.4 对话记录详情

```bash
curl -s http://120.27.129.78:8080/api/cloud-agent/conversations/conv_007
```

### 29.5 创建AI建议

```bash
curl -s -X POST http://120.27.129.78:8080/api/cloud-agent/advice \
  -H "Content-Type: application/json" \
  -d '{
    "adviceId": "adv_006",
    "elderId": "elder_001",
    "adviceType": "health",
    "inputSummary": "近期血压偏高",
    "adviceContent": "建议每日早晚测量血压，控制盐分摄入"
  }'
```

---

## 30. 本地智能体

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 133 | 心跳上报 | POST | `/api/local-agent/heartbeat` | 本地智能体心跳 |
| 134 | 状态上报 | POST | `/api/local-agent/status` | 上报本地智能体状态 |
| 135 | 上传传感器数据 | POST | `/api/local-agent/data` | 通过本地智能体上传数据 |
| 136 | 上报告警 | POST | `/api/local-agent/alarm-report` | 本地智能体上报告警 |
| 137 | 获取待执行命令 | GET | `/api/local-agent/{agentId}/commands` | 获取下发命令 |
| 138 | 上报意图日志 | POST | `/api/local-agent/intent` | 上报意图识别日志 |
| 139 | 上报控制日志 | POST | `/api/local-agent/control` | 上报家居控制日志 |

### 30.1 心跳上报

```bash
curl -s -X POST http://120.27.129.78:8080/api/local-agent/heartbeat \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": "LOCAL-1001",
    "agentType": "local_gateway",
    "status": "online",
    "lastHeartbeat": "2025-05-17 10:00:00",
    "ip": "192.168.1.101",
    "deviceCount": 3,
    "connectedDevices": 3
  }'
```

### 30.2 上传传感器数据（批量）

```bash
curl -s -X POST http://120.27.129.78:8080/api/local-agent/data \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": "LOCAL-1001",
    "deviceId": "dev_001",
    "sensorDataList": [
      {"sensorType": "heart_rate", "value": 72, "unit": "bpm", "timestamp": "2025-05-17 10:00:00"},
      {"sensorType": "spo2", "value": 98, "unit": "%", "timestamp": "2025-05-17 10:00:00"}
    ]
  }'
```

### 30.3 上报告警

```bash
curl -s -X POST http://120.27.129.78:8080/api/local-agent/alarm-report \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": "LOCAL-1001",
    "alarm": {
      "elderId": "elder_001",
      "deviceId": "dev_001",
      "alarmType": "fall",
      "alarmLevel": "critical",
      "description": "检测到跌倒行为",
      "building": "1号楼",
      "roomNumber": "1-201"
    }
  }'
```

### 30.4 获取待执行命令

```bash
curl -s http://120.27.129.78:8080/api/local-agent/LOCAL-1001/commands
```

### 30.5 上报意图日志

```bash
curl -s -X POST http://120.27.129.78:8080/api/local-agent/intent \
  -H "Content-Type: application/json" \
  -d '{
    "intentId": "intent_009",
    "elderId": "elder_001",
    "source": "voice",
    "userText": "打开窗帘",
    "intent": "curtain-control",
    "confidence": 0.95,
    "handledBy": "LOCAL-1001"
  }'
```

### 30.6 上报控制日志

```bash
curl -s -X POST http://120.27.129.78:8080/api/local-agent/control \
  -H "Content-Type: application/json" \
  -d '{
    "controlId": "ctrl_007",
    "elderId": "elder_001",
    "deviceId": "dev_014",
    "command": "close",
    "sourceAgent": "LOCAL-1001",
    "result": "success"
  }'
```

---

## 31. AI服务

> 异步接口，返回 `CompletableFuture`。

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 72 | AI异步对话 | POST | `/api/ai/chat` | (已在第11节) |
| 73 | AI异步找物 | POST | `/api/ai/find-item` | (已在第11节) |

---

## 32. AI兼容版

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 140 | AI服务状态 | GET | `/api/ai/service-status` | Python AI服务运行状态 |
| 141 | 最新AI分析 | GET | `/api/ai/latest-analysis/{elder_id}` | 老人最新AI分析结果 |
| 142 | AI分析记录分页 | GET | `/api/ai/analysis-records` | AI分析历史分页 |
| 143 | 健康风险分析 | POST | `/api/ai/health-analysis` | 提交健康数据做风险分析 |
| 144 | 快速对话(本地) | POST | `/api/ai/chat/quick` | 本地AI快速对话 |
| 145 | 深度对话(云端) | POST | `/api/ai/chat/deep` | 云端AI深度对话 |
| 146 | RAG知识库查询 | POST | `/api/ai/rag-query` | 知识库检索 |
| 147 | 视觉分析 | POST | `/api/ai/vision-analysis` | AI视觉图像分析 |

### 32.1 AI服务状态

```bash
curl -s http://120.27.129.78:8080/api/ai/service-status
```

### 32.2 最新AI分析

```bash
curl -s http://120.27.129.78:8080/api/ai/latest-analysis/elder_001
```

### 32.3 AI分析记录分页

```bash
curl -s "http://120.27.129.78:8080/api/ai/analysis-records?elder_id=elder_001&page=1&pageSize=10"
```

### 32.4 健康风险分析

```bash
curl -s -X POST http://120.27.129.78:8080/api/ai/health-analysis \
  -H "Content-Type: application/json" \
  -d '{
    "elder_id": "elder_001",
    "recent_health": {
      "heart_rate": [72, 85, 95],
      "spo2": [98, 97, 96],
      "temperature": [36.5, 36.6, 36.8]
    }
  }'
```

### 32.5 快速对话(本地AI)

```bash
curl -s -X POST http://120.27.129.78:8080/api/ai/chat/quick \
  -H "Content-Type: application/json" \
  -d '{"elder_id": "elder_001", "message": "今天天气怎么样？"}'
```

### 32.6 深度对话(云端AI)

```bash
curl -s -X POST http://120.27.129.78:8080/api/ai/chat/deep \
  -H "Content-Type: application/json" \
  -d '{"elder_id": "elder_001", "message": "我最近血压控制得怎么样？"}'
```

### 32.7 RAG知识库查询

```bash
curl -s -X POST http://120.27.129.78:8080/api/ai/rag-query \
  -H "Content-Type: application/json" \
  -d '{"query": "高血压饮食应该注意什么？", "elder_id": "elder_001", "top_k": 3}'
```

### 32.8 视觉分析

```bash
curl -s -X POST http://120.27.129.78:8080/api/ai/vision-analysis \
  -H "Content-Type: application/json" \
  -d '{
    "elder_id": "elder_001",
    "image_url": "/uploads/snapshot/alarm_003.jpg",
    "analysis_type": "fall_detection"
  }'
```

---

## 33. 生命体征独立查询

> 独立体征数据查询接口（`/api/vital-signs`），支持心率、血压、血氧、体温的分类查询和综合查询。

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 148 | 心率历史列表 | GET | `/api/vital-signs/heart-rate/list` | 按老人和时间范围查询心率 |
| 149 | 最新心率 | GET | `/api/vital-signs/heart-rate/latest` | 最新一条心率记录 |
| 150 | 血压历史列表 | GET | `/api/vital-signs/blood-pressure/list` | 按老人和时间范围查询血压 |
| 151 | 最新血压 | GET | `/api/vital-signs/blood-pressure/latest` | 最新一条血压记录 |
| 152 | 血氧历史列表 | GET | `/api/vital-signs/blood-oxygen/list` | 按老人和时间范围查询血氧 |
| 153 | 最新血氧 | GET | `/api/vital-signs/blood-oxygen/latest` | 最新一条血氧记录 |
| 154 | 体温历史列表 | GET | `/api/vital-signs/body-temperature/list` | 按老人和时间范围查询体温 |
| 155 | 最新体温 | GET | `/api/vital-signs/body-temperature/latest` | 最新一条体温记录 |
| 156 | 综合最新体征 | GET | `/api/vital-signs/latest/{elderId}` | 老人所有体征最新值汇总 |

### 33.1 心率历史列表

```bash
# 查询所有心率记录
curl -s "http://120.27.129.78:8080/api/vital-signs/heart-rate/list?elderId=elder_001"

# 按时间范围查询
curl -s "http://120.27.129.78:8080/api/vital-signs/heart-rate/list?elderId=elder_001&start=2025-05-01T00:00:00&end=2025-05-18T23:59:59"
```

请求参数：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| elderId | String | 是 | 老人ID |
| start | DateTime(ISO) | 否 | 开始时间(ISO格式) |
| end | DateTime(ISO) | 否 | 结束时间(ISO格式) |

### 33.2 最新心率

```bash
curl -s "http://120.27.129.78:8080/api/vital-signs/heart-rate/latest?elderId=elder_001"
```

### 33.3 血压历史列表

```bash
curl -s "http://120.27.129.78:8080/api/vital-signs/blood-pressure/list?elderId=elder_001"
```

### 33.4 最新血压

```bash
curl -s "http://120.27.129.78:8080/api/vital-signs/blood-pressure/latest?elderId=elder_001"
```

### 33.5 血氧历史列表

```bash
curl -s "http://120.27.129.78:8080/api/vital-signs/blood-oxygen/list?elderId=elder_001"
```

### 33.6 最新血氧

```bash
curl -s "http://120.27.129.78:8080/api/vital-signs/blood-oxygen/latest?elderId=elder_001"
```

### 33.7 体温历史列表

```bash
curl -s "http://120.27.129.78:8080/api/vital-signs/body-temperature/list?elderId=elder_001"
```

### 33.8 最新体温

```bash
curl -s "http://120.27.129.78:8080/api/vital-signs/body-temperature/latest?elderId=elder_001"
```

### 33.9 综合最新体征

```bash
curl -s http://120.27.129.78:8080/api/vital-signs/latest/elder_001
```

返回示例：
```json
{
  "code": 200, "message": "success",
  "data": {
    "elderId": "elder_001",
    "heartRate": 72,
    "heartRateUnit": "次/分",
    "heartRateTime": "2025-05-17T10:00:00",
    "systolic": 130,
    "diastolic": 82,
    "bloodPressureUnit": "mmHg",
    "bloodPressureTime": "2025-05-17T09:00:00",
    "bloodOxygen": 98,
    "bloodOxygenUnit": "%",
    "bloodOxygenTime": "2025-05-17T10:00:00",
    "bodyTemperature": 36.5,
    "bodyTemperatureUnit": "℃",
    "bodyTemperatureTime": "2025-05-17T08:00:00"
  }
}
```

---

## 34. 模拟器

> 独立模拟器控制器（`/api/simulator`），功能与 11 节的 DeviceDataController 模拟器基本一致。

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 157 | 启动模拟器 | POST | `/api/simulator/start` | 启动模拟器 |
| 158 | 停止模拟器 | POST | `/api/simulator/stop` | 停止模拟器 |
| 159 | 模拟器状态 | GET | `/api/simulator/status` | 运行状态 |
| 160 | 指纹成功 | POST | `/api/simulator/fingerprint/success` | 模拟指纹验证成功 |
| 161 | 指纹失败 | POST | `/api/simulator/fingerprint/fail` | 模拟指纹验证失败 |
| 162 | 窗帘控制 | POST | `/api/simulator/curtain` | 模拟窗帘 |
| 163 | 蜂鸣器 | POST | `/api/simulator/buzzer` | 模拟蜂鸣器 |
| 164 | 灯光控制 | POST | `/api/simulator/light` | 模拟灯光 |
| 165 | 手表紧急呼叫 | POST | `/api/simulator/watch/emergency` | 模拟手表SOS |
| 166 | 跌倒检测 | POST | `/api/simulator/fall` | 模拟跌倒 |
| 167 | 找物 | POST | `/api/simulator/find-item` | 模拟找物 |

```bash
# 启动模拟器
curl -s -X POST http://120.27.129.78:8080/api/simulator/start

# 模拟跌倒
curl -s -X POST "http://120.27.129.78:8080/api/simulator/fall?room=bedroom"

# 模拟找物
curl -s -X POST "http://120.27.129.78:8080/api/simulator/find-item?itemName=遥控器&room=living-room"

# 模拟指纹失败
curl -s -X POST http://120.27.129.78:8080/api/simulator/fingerprint/fail
```

---

## 35. 老人管理-兼容版

| # | 接口名称 | 方法 | URL | 说明 |
|---|---------|------|-----|------|
| 168 | 老人列表(兼容) | GET | `/api/elders?page=1&pageSize=20` | snake_case版分页 |

```bash
curl -s "http://120.27.129.78:8080/api/elders?page=1&pageSize=10&building=1号楼"
```

---

## 附录：通用数据结构

### ApiResponse<T> — 统一响应

```json
{
  "code": 200,
  "message": "success",
  "data": { }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 业务状态码：200-成功, 201-创建, 400-参数错误, 401-未认证, 403-无权限, 404-不存在, 500-异常 |
| message | String | 状态描述 |
| data | T | 泛型数据体 |

### PageResult<T> — 分页响应

```json
{
  "code": 200, "message": "success",
  "data": {
    "list": [],
    "total": 100,
    "page": 1,
    "pageSize": 20
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| list | List\<T\> | 数据列表 |
| total | long | 总记录数 |
| page | int | 当前页码 |
| pageSize | int | 每页条数 |

### ElderDto — 老人数据结构

| 字段 | 类型 | 说明 |
|------|------|------|
| elderId | String | 老人唯一ID |
| name | String | 姓名 |
| age | int | 年龄 |
| gender | String | 性别 |
| address | String | 住址 |
| building | String | 楼栋 |
| roomNumber | String | 房间号 |
| phone | String | 手机号 |
| password | String | 密码 |
| healthStatus | String | 健康状态：normal/warning/danger |
| healthStatusText | String | 健康状态文本：正常/关注/高危 |
| healthNote | String | 健康备注/标签 |
| familyPhone | String | 家属手机号 |
| communityId | String | 社区ID |
| avatar | String | 头像URL |
| hasCamera | Boolean | 是否有摄像头 |
| cameraAuthUntil | Long | 监控授权到期时间戳 |
| cameraPending | Boolean | 是否有待审批监控申请 |
| lastOnline | String | 最后在线时间 |

### AlarmDto — 告警数据结构

| 字段 | 类型 | 说明 |
|------|------|------|
| alarmId | String | 告警唯一ID |
| elderId | String | 老人ID |
| deviceId | String | 设备ID |
| alarmType | String | 告警类型 |
| alarmLevel | String | 告警级别：normal/low/medium/high/critical |
| alarmStatus | String | 状态：pending/handled |
| description | String | 告警描述 |
| building | String | 楼栋 |
| roomNumber | String | 房间号 |
| snapshotUrl | String | 抓拍快照URL |
| handler | String | 处理人ID |
| handlerName | String | 处理人姓名 |
| handleRemark | String | 处理备注 |
| isRead | Boolean | 是否已读 |
| location | String | 位置描述 |

### WorkOrderDto — 工单数据结构

| 字段 | 类型 | 说明 |
|------|------|------|
| orderId | String | 工单唯一ID |
| elderId | String | 老人ID |
| orderType | String | 工单类型：紧急巡检/健康关注/设备检查/设备维修/上门护理/日常关怀 |
| type | String | 工单类型(兼容字段) |
| description | String | 工单描述 |
| status | String | 状态：待处理/处理中/已完成 |
| creatorId | String | 创建人ID |
| handlerId | String | 处理人ID |
| handlerName | String | 处理人姓名 |
| handlerPhone | String | 处理人手机号 |
| completeTime | String | 完成时间 |
| alarmId | String | 关联告警ID |
| serviceRequestId | String | 关联服务请求ID |

### DeviceDto — 设备数据结构

| 字段 | 类型 | 说明 |
|------|------|------|
| deviceId | String | 设备唯一ID |
| elderId | String | 老人ID |
| deviceType | String | 设备类型：手环/摄像头/烟感/床垫传感器/门锁/窗帘/灯光/蜂鸣器 |
| deviceName | String | 设备名称 |
| location | String | 安装位置 |
| building | String | 楼栋 |
| room | String | 房间 |
| status | String | 状态：online/offline |
| batteryLevel | int | 电量(有线设备填0) |
| lastOnlineTime | String | 最后在线时间 |
| online | Boolean | 是否在线(兼容) |

### StaffDto — 工作人员数据结构

| 字段 | 类型 | 说明 |
|------|------|------|
| staffId | String | 员工唯一ID |
| username | String | 用户名 |
| name | String | 姓名 |
| phone | String | 手机号 |
| password | String | 密码 |
| role | String | 角色：admin/supervisor/staff |
| communityId | String | 社区ID |
| avatar | String | 头像URL |

### LoginResponse — 登录响应

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | String | 用户ID |
| name | String | 姓名 |
| phone | String | 手机号 |
| role | String | 角色 |
| userType | String | 用户类型：staff/family |
| token | String | 令牌(当前为null) |

### 告警类型枚举

| 值 | 说明 |
|------|------|
| heart_rate | 心率异常 |
| fall | 跌倒检测 |
| health_abnormal | 健康指标异常 |
| blood_pressure | 血压异常 |
| emergency-call | 紧急呼叫/SOS |
| intrusion | 陌生人闯入 |
| smoke | 烟雾报警 |
| temperature | 体温异常 |
| inactive | 长时间无活动 |
| fingerprint-fail | 指纹验证失败 |

### 风险等级枚举

| 值 | 说明 |
|------|------|
| normal | 正常 |
| low | 低风险 |
| medium | 中风险 |
| high | 高风险 |
| critical | 紧急/危急 |

### 工单类型枚举

| 值 | 说明 |
|------|------|
| 紧急巡检 | 高风险告警触发，需立即上门 |
| 健康关注 | 中风险告警触发，24小时内随访 |
| 设备检查 | 设备异常触发 |
| 设备维修 | 设备故障维修 |
| 上门护理 | 护理服务 |
| 日常关怀 | 情绪关怀/心理疏导 |

---

## 接口统计

| 模块 | 接口数 |
|------|--------|
| 健康检查 | 2 |
| 认证授权 | 7 |
| 老人管理 | 11 |
| 工作人员 | 4 |
| 仪表盘 | 3 |
| 告警管理 | 9 |
| 告警管理-兼容 | 5 |
| 工单管理 | 5 |
| 工单管理-兼容 | 4 |
| 设备管理 | 7 |
| 设备数据与模拟器 | 16 |
| 设备数据上传 | 1 |
| 健康管理-兼容 | 1 |
| 健康记录 | 3 |
| 健康体征 | 3 |
| 睡眠记录 | 2 |
| 陪伴记录 | 2 |
| VLM记录 | 2 |
| 摄像头查看 | 2 |
| SOS求助 | 4 |
| 干预管理 | 5 |
| 通知管理 | 4 |
| 服务请求 | 6 |
| 监控授权 | 8 |
| 紧急联系人 | 5 |
| 文件上传 | 3 |
| 智能体管理 | 1 |
| 云端智能体 | 10 |
| 本地智能体 | 7 |
| AI兼容 | 8 |
| 生命体征独立查询 | 9 |
| 模拟器 | 11 |
| 老人管理-兼容 | 1 |
| **总计** | **171** |

---

> **文档版本**: v3.1
> **生成日期**: 2026-07-17
> **对应数据库**: `anxinban` (36张表, 430+条记录)
> **SQL脚本**: `anxinban_database_full_20260704.sql`
> **框架**: Spring Boot 2.7, Java 17, MySQL 8.0
