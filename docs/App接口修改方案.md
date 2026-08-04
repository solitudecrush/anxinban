# App 端接口修改方案

> 生成日期：2026-08-04（全面复核更新）
> 后端状态：已与 Web 端 `接口清单.md` 完全对齐，27 张数据表，编译通过
> 适用：家属 App 端开发者

---

## 一、后端本次变更总结

| 变更类别 | 变更内容 | 对 App 影响 |
|---------|---------|------------|
| 数据库精简 | 35→27 表，硬件数据统一写 `sensor_data` | **无影响**（API 不变） |
| 登录响应 | LoginResponse 新增 `token`、`userType` | 兼容（增量字段） |
| 服务申请状态 | `converted` → `approved` | **需配合修改** |
| 工单状态 | `待分配` → `待处理` | **需配合修改** |
| 告警转工单 | 新增单数路径 `/api/alarm/{id}/to-work-order` | 兼容 |
| 监控申请 | DTO 新增 `cameraType` | 兼容 |
| 仪表盘统计 | 字段重命名（8字段） | App 不使用 |

---

## 二、修改总览

| 接口 | 变更类型 | 当前 App 调用方式 | 需修改为 | 优先级 | 需配合 |
|------|---------|------------------|---------|--------|--------|
| `POST /api/service-request` | 状态枚举 | `status: "converted"` | `status: "approved"` | **P1** | 是 |
| `GET /api/service-request/my-list` | 状态值 | 读取 `converted` | 读取 `approved` | **P1** | 是 |
| `GET /api/service-request/{id}/status` | 状态值 | 读取 `converted` | 读取 `approved` | **P1** | 是 |
| `GET /api/work-order/list` | 状态枚举 | `待分配` | `待处理` | **P1** | 是（如有） |
| `POST /api/auth/login/app` | 新增字段 | 读取 `userId`,`role` | 可新增读 `token`,`userType` | P2 | 否 |
| `GET /api/monitor-request/list/family` | 新增字段 | 返回监控列表 | 可新增读 `cameraType` | P2 | 否 |

---

## 三、逐项修改详情

### 3.1 服务申请状态 `converted` → `approved`（P1 高）

- **变更接口**：`POST /api/service-request`、`GET /api/service-request/my-list`、`GET /api/service-request/{id}/status`
- **当前返回值**：`status: "pending" | "approved" | "rejected"`
- **之前返回值**：`status: "pending" | "converted" | "rejected"`
- **影响**：App 端如有 `if (status === "converted")` 的判断，改为 `status === "approved"`
- **示例响应**：

```json
{
  "code": 200,
  "data": {
    "requestId": "sr_001",
    "familyId": "family_001",
    "elderId": "elder_001",
    "requestType": "设备维修",
    "content": "请求安排人员上门检查客厅摄像头",
    "status": "approved",
    "relatedOrderId": "wo_xxx",
    "createTime": "2026-08-01 10:00:00"
  }
}
```

### 3.2 工单状态 `待分配` → `待处理`（P1 高）

- **变更接口**：`GET /api/work-order/list`
- **状态枚举**：`待处理` / `处理中` / `已完成`
- **影响**：App 端如有筛选或展示 `待分配`，改为 `待处理`

### 3.3 登录响应新增字段（P2 低）

- **变更接口**：`POST /api/auth/login/app`、`POST /api/auth/login`
- **新增字段**：
  - `token`：与 `accessToken` 同值，前端可直接使用
  - `userType`：`"family"`（家属登录时）
- **示例响应**：

```json
{
  "code": 200,
  "data": {
    "accessToken": "token-xxx",
    "token": "token-xxx",
    "refreshToken": "refresh-xxx",
    "userId": "family_001",
    "name": "张小明",
    "phone": "13800138011",
    "role": "family",
    "userType": "family",
    "communityId": "community_001",
    "avatar": "/uploads/avatar/family_001.jpg"
  }
}
```

### 3.4 监控申请新增 `cameraType`（P2 低）

- **变更接口**：`GET /api/monitor-request/list/family?familyId=xxx`
- **新增字段**：`cameraType` — 摄像头类型：`living`（客厅）/ `bedroom`（卧室）/ `door`（门口）
- **影响**：可选展示，便于家属了解申请查看的摄像头类型

### 3.5 告警转工单新路径（P2 低）

- **推荐**：`POST /api/alarm/{alarmId}/to-work-order`（单数）
- **兼容**：`POST /api/alarms/{alarm_id}/to-work-order`（仍可用）

---

## 四、App 端配合事项清单

| 序号 | 事项 | 涉及页面 | 优先级 | 必须修改 |
|------|------|---------|--------|---------|
| 1 | 服务申请状态 `converted`→`approved` | 我的申请列表、申请详情 | P1 | **是** |
| 2 | 工单状态 `待分配`→`待处理` | 工单列表（如有） | P1 | **是** |
| 3 | 登录新增 `token`/`userType` | 登录页 | P2 | 否 |
| 4 | 监控新增 `cameraType` | 监控审批 | P2 | 否 |
| 5 | 告警转工单单数路径 | 告警详情 | P2 | 否 |

---

## 五、时序建议

### 第一轮：无破坏性（App 无需改动）
- LoginResponse 新增字段
- MonitorRequestDto 新增 `cameraType`
- 告警转工单单数路径
- → **直接上线，App 端无需任何改动**

### 第二轮：破坏性变更（需同步上线）
- 服务申请状态 `converted`→`approved`（P1）
- 工单状态 `待分配`→`待处理`（P1）
- → **与 App 前端约定上线窗口，双方同步发布**

---

## 六、通知 App 前端的话术

> App 前端同学你好，
>
> 后端近期完成了数据库精简和接口对齐，以下变更需要 App 端配合：
>
> **必须修改（P1，影响功能）：**
> 1. 服务申请状态值从 `"converted"` 改为 `"approved"`。涉及接口：提交申请、我的申请列表、申请详情。请全局搜索 `"converted"` 替换为 `"approved"`。
> 2. 工单状态从 `"待分配"` 改为 `"待处理"`。如有按此筛选或展示的地方请替换。
>
> **可选关注（向后兼容）：**
> 3. 登录响应新增 `token`（等同 `accessToken`）和 `userType`（`"family"`）字段。
> 4. 监控申请列表新增 `cameraType` 字段。
>
> **建议**：下周一同步发布，确保前后端一致。详细文档见 `docs/App接口修改方案.md`。

---

## 七、App 端常用接口对照表

| 接口 | 方法 | 路径 | 状态 | 备注 |
|------|------|------|------|------|
| 家属登录 | POST | `/api/auth/login/app` | ✅ | 新增 token, userType |
| 通用登录 | POST | `/api/auth/login` | ✅ | 同上 |
| 注册 | POST | `/api/auth/register` | ✅ | |
| 重置密码 | POST | `/api/auth/reset-password` | ✅ | |
| 用户信息 | GET | `/api/auth/me?phone=` | ✅ | |
| 绑定老人 | GET | `/api/elder/bound?familyId=` | ✅ | |
| 老人详情 | GET | `/api/elder/{elderId}` | ✅ | |
| 老人设备 | GET | `/api/elder/{elderId}/devices` | ✅ | |
| 实时健康 | GET | `/api/elder/{elderId}/health/realtime` | ✅ | |
| 健康历史 | GET | `/api/elder/{elderId}/health/history` | ✅ | |
| 最新体征 | GET | `/api/vital-signs/latest/{elderId}` | ✅ | |
| 心率列表 | GET | `/api/vital-signs/heart-rate/list` | ✅ | |
| 血压列表 | GET | `/api/vital-signs/blood-pressure/list` | ✅ | |
| 血氧列表 | GET | `/api/vital-signs/blood-oxygen/list` | ✅ | |
| 体温列表 | GET | `/api/vital-signs/body-temperature/list` | ✅ | |
| 告警列表 | GET | `/api/alarm/list` | ✅ | |
| 未读告警数 | GET | `/api/alarm/unread-count` | ✅ | |
| 标记已读 | PUT | `/api/alarm/{alarmId}/read` | ✅ | |
| 告警转工单 | POST | `/api/alarm/{alarmId}/to-work-order` | ✅ | 新增单数路径 |
| 提交服务申请 | POST | `/api/service-request` | ⚠️ | status: approved |
| 我的申请 | GET | `/api/service-request/my-list` | ⚠️ | status: approved |
| 申请详情 | GET | `/api/service-request/{id}/status` | ⚠️ | status: approved |
| 监控申请列表 | GET | `/api/monitor-request/list/family` | ✅ | 新增 cameraType |
| 同意监控 | POST | `/api/monitor-request/{id}/approve` | ✅ | |
| 拒绝监控 | POST | `/api/monitor-request/{id}/reject` | ✅ | |
| 撤销授权 | POST | `/api/monitor-request/{id}/revoke` | ✅ | |
| 通知列表 | GET | `/api/notification/list` | ✅ | |
| 标记已读 | POST | `/api/notification/{id}/read` | ✅ | |
| 全部已读 | POST | `/api/notification/read-all` | ✅ | |
| 未读数 | GET | `/api/notification/unread-count` | ✅ | |
| 紧急联系人 | GET | `/api/emergency-contact/list` | ✅ | |
| 健康档案 | GET | `/api/health-record/by-elder/{elderId}` | ✅ | |
| 上传头像 | POST | `/api/upload/avatar` | ✅ | |
| SOS 呼救 | POST | `/api/sos` | ✅ | |
| SOS 列表 | GET | `/api/sos/list` | ✅ | |
| 工单列表 | GET | `/api/work-order/list` | ⚠️ | 状态: 待处理 |
| AI 对话 | POST | `/api/ai/chat` | ✅ | |
| AI 找物 | POST | `/api/ai/find-item` | ✅ | |
