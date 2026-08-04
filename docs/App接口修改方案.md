# App 端接口修改方案

> 生成日期：2026-07-31（更新：数据库精简后复核）
> 依据文档：`接口清单.md`（Web 前端整理）、`api文档.md`（v2.3）、`anxinban-api-reference-manual-v3.1.md`（v3.2，184个接口）
> 适用范围：家属 App 端
>
> **2026-07-31 数据库精简更新**：数据库从 35 表精简为 28 表，所有 API 接口路径、参数、响应格式均不变。
> 硬件数据统一写入 `sensor_data` 表，AI 记录统一写 `ai_service_record` 表，消除多表重复存储。

---

## 一、修改总览

本次 App 端涉及的接口变更共 **5 项**，其中 **代码无需改动 2 项**（本次已完成后端修改），**需 App 前端配合 3 项**。

| 接口 | 变更类型 | 当前 App 调用方式 | 需修改为 | 优先级 | 是否需 App 前端配合 |
|------|---------|------------------|---------|--------|-----------------|
| `POST /api/auth/login` / `/login/app` | 响应新增字段 | 读取 `data.userId`, `data.role` | 可新增读取 `data.token`, `data.userType` | P2 低 | 否（兼容） |
| `POST /api/service-request` (提交申请) | 状态枚举变更 | 状态值 `converted` | 状态值 `approved` | P1 高 | 是 |
| `GET /api/service-request/my-list` (我的申请) | 状态值变化 | 读取 `converted` 状态 | 读取 `approved` 状态 | P1 高 | 是 |
| `GET /api/work-order/list` (工单列表) | 状态枚举变更 | 筛选 `待分配` | 筛选 `待处理` | P1 高 | 是 |
| `GET /api/dashboard/stats` | 不做变更（App 不使用） | — | — | — | 否 |

---

## 二、逐项修改详情

### 2.1 登录响应新增字段（兼容变更）

- **后端变更**：LoginResponse 新增 `token`（与 `accessToken` 同值）和 `userType`（`"staff"` 或 `"family"`）两个字段
- **当前 App 端调用**：App 端家属登录 `POST /api/auth/login/app`，读取响应中 `userId`、`name`、`phone`、`role`
- **需修改为**：无需修改，新增字段为增量添加，不影响旧字段
- **修改原因**：Web 端登录时通过 `token` 字段获取令牌，统一命名；`userType` 可帮助 App 明确当前用户类型
- **App 端可用字段**：`accessToken`, `token`（同值）, `refreshToken`, `userId`, `name`, `phone`, `role`, `userType`, `communityId`, `avatar`
- **影响范围**：仅登录接口，向后兼容
- **是否需 App 前端配合**：否，兼容变更
- **修改状态**：已修改（后端已完成）

---

### 2.2 服务申请状态枚举变更 `converted` → `approved`

- **后端变更**：`ServiceRequestService.java` 中转工单后状态从 `"converted"` 改为 `"approved"`
- **当前 App 端调用**：App 提交服务申请后，通过 `GET /api/service-request/my-list?familyId=xxx` 查询申请列表，状态可能展示为 `converted`（已转工单）
- **后端正解**：

  ```
  POST /api/service-request          — 提交申请，初始状态 pending
  GET  /api/service-request/my-list  — 返回状态值：pending（待处理）/ approved（已转工单）/ rejected（已拒绝）
  GET  /api/service-request/{id}/status — 同上
  ```

- **需修改为**：App 端状态判断中 `"converted"` → `"approved"`
- **修改原因**：与 Web 管理端保持统一的状态值体系
- **影响范围**：App 端「服务申请」模块（我的申请列表、申请详情状态展示）
- **是否需 App 前端配合**：是，需修改状态值判断逻辑
- **修改状态**：已修改（后端已完成）

---

### 2.3 工单状态枚举变更 `待分配` → `待处理`

- **后端变更**：工单默认状态从 `"待分配"` 改为 `"待处理"`
- **当前 App 端调用**：若 App 端有工单列表展示，工单默认状态应显示为 `"待处理"`
- **需修改为**：App 端如硬编码了 `"待分配"`，需改为 `"待处理"`
- **修改原因**：与 Web 管理端统一
- **影响范围**：App 端工单相关模块（如有）
- **是否需 App 前端配合**：是（若 App 端有工单状态展示）
- **修改状态**：已修改（后端已完成）

---

### 2.4 监控申请响应新增 `cameraType` 字段（兼容变更）

- **后端变更**：`MonitorRequestDto` 新增 `cameraType` 字段（door / living / bedroom）
- **当前 App 端调用**：`GET /api/monitor-request/list/family?familyId=xxx` 返回监控申请列表
- **需修改为**：无需修改，新增字段为增量添加
- **修改原因**：Web 端创建监控申请时传入 `cameraType`，App 审批时可展示摄像头类型
- **影响范围**：监控审批页面（如有展示可增强体验）
- **是否需 App 前端配合**：否，兼容变更
- **修改状态**：已修改（后端已完成）

---

### 2.5 告警转工单新增单数路径

- **后端变更**：新增 `POST /api/alarm/{alarmId}/to-work-order` 端点（单数路径）
- **当前 App 端调用**：若 App 端有告警转工单，需使用正确路径
- **后端正解**：
  - 推荐路径：`POST /api/alarm/{alarmId}/to-work-order`
  - 兼容路径：`POST /api/alarms/{alarm_id}/to-work-order`
- **修改原因**：与 Web 端统一使用单数路径
- **是否需 App 前端配合**：否（新旧路径均可用）
- **修改状态**：已修改（后端已完成）

---

## 三、App 端配合事项清单

| 序号 | 事项 | 涉及页面/模块 | 优先级 | 是否告知 App 前端 |
|------|------|-------------|--------|-----------------|
| 1 | 服务申请状态 `converted` → `approved` | 我的申请列表、申请详情 | **P1 高** | 是 |
| 2 | 工单状态 `待分配` → `待处理` | 工单列表（如有） | **P1 高** | 是 |
| 3 | 登录响应增加 `token` / `userType`，可选择性使用 | 登录页 | P2 低 | 告知即可 |
| 4 | 监控申请增加 `cameraType` 字段 | 监控审批 | P2 低 | 告知即可 |
| 5 | 告警转工单路径双路径可用 | 告警详情 | P2 低 | 告知即可 |

---

## 四、修改时序建议

建议按以下顺序修改：

### 第一轮：无破坏性变更（App 端无需配合）
- LoginResponse 新增 `token`、`userType` 字段
- MonitorRequestDto 新增 `cameraType` 字段
- 告警转工单新增单数路径
- _以上变更 App 端无需任何改动，可直接上线_

### 第二轮：需 App 前端配合的非破坏性变更
- 告警类型枚举新增 `sos`、`door_lock`、`door_snapshot`
- _App 端如有告警类型筛选，可新增这三个选项，非强制_

### 第三轮：破坏性变更（需 App 前端同步上线）
- 服务申请状态 `converted` → `approved`（**P1 高**）
- 工单状态 `待分配` → `待处理`（**P1 高**）
- **建议：本次与 App 前端约定一个上线时间窗口，双方同步发布**

---

## 五、通知 App 前端的话术

> App 前端同学，你好。
>
> 近期后端对接口进行了以下调整，部分变更需要 App 端配合修改：
>
> **必须修改（P1 高优先级）：**
> 1. **服务申请状态值变更**：`POST /api/service-request` 转工单后，状态值从原来的 `"converted"` 改为 `"approved"`。你的代码中如有判断 `status === "converted"` 的地方，请改为 `"approved"`。
> 2. **工单状态值变更**：`POST /api/work-order` 创建的工单，默认状态从 `"待分配"` 改为 `"待处理"`。如有按 `"待分配"` 筛选或展示的地方，请改为 `"待处理"`。
>
> **可选关注（兼容变更，不影响现有逻辑）：**
> 3. 登录接口响应新增 `token`（与 `accessToken` 同值）和 `userType`（`"family"`）字段。
> 4. 监控申请接口响应新增 `cameraType` 字段（door/living/bedroom）。
> 5. 告警类型新增 `sos`、`door_lock`、`door_snapshot` 三种类型。
>
> **建议上线时间**：下周一（2026-08-03）共同发布，确保前后端同步。
>
> 如有疑问随时沟通。文档详见 `docs/App接口修改方案.md`。

---

## 六、当前后端接口对照（App 端常用）

| App 端接口 | 方法 | 路径 | 状态 |
|-----------|------|------|------|
| 家属登录 | POST | `/api/auth/login/app` | ✅ 正常 |
| 通用登录 | POST | `/api/auth/login` | ✅ 正常 |
| 注册 | POST | `/api/auth/register` | ✅ 正常 |
| 重置密码 | POST | `/api/auth/reset-password` | ✅ 正常 |
| 用户信息 | GET | `/api/auth/me?phone=` | ✅ 正常 |
| 绑定老人 | GET | `/api/elder/bound?familyId=` | ✅ 正常 |
| 老人详情 | GET | `/api/elder/{elderId}` | ✅ 正常 |
| 老人设备 | GET | `/api/elder/{elderId}/devices` | ✅ 正常 |
| 实时健康 | GET | `/api/elder/{elderId}/health/realtime` | ✅ 正常 |
| 健康历史 | GET | `/api/elder/{elderId}/health/history` | ✅ 正常 |
| 最新体征 | GET | `/api/vital-signs/latest/{elderId}` | ✅ 正常 |
| 告警列表 | GET | `/api/alarm/list` | ✅ 正常 |
| 未读告警数 | GET | `/api/alarm/unread-count` | ✅ 正常 |
| 标记已读 | PUT | `/api/alarm/{alarmId}/read` | ✅ 正常 |
| 提交服务申请 | POST | `/api/service-request` | ⚠️ 状态值变更 |
| 我的申请 | GET | `/api/service-request/my-list` | ⚠️ 状态值变更 |
| 申请详情 | GET | `/api/service-request/{id}/status` | ⚠️ 状态值变更 |
| 监控申请列表 | GET | `/api/monitor-request/list/family` | ✅ 正常（新增 cameraType） |
| 同意监控 | POST | `/api/monitor-request/{id}/approve` | ✅ 正常 |
| 拒绝监控 | POST | `/api/monitor-request/{id}/reject` | ✅ 正常 |
| 撤销授权 | POST | `/api/monitor-request/{id}/revoke` | ✅ 正常 |
| 通知列表 | GET | `/api/notification/list` | ✅ 正常 |
| 标记已读 | POST | `/api/notification/{id}/read` | ✅ 正常 |
| 全部已读 | POST | `/api/notification/read-all` | ✅ 正常 |
| 未读数 | GET | `/api/notification/unread-count` | ✅ 正常 |
| 紧急联系人 | GET | `/api/emergency-contact/list` | ✅ 正常 |
| 健康档案 | GET | `/api/health-record/by-elder/{elderId}` | ✅ 正常 |
| 上传头像 | POST | `/api/upload/avatar` | ✅ 正常 |
| SOS 呼救 | POST | `/api/sos` | ✅ 正常 |
| SOS 列表 | GET | `/api/sos/list` | ✅ 正常 |
| 工单列表 | GET | `/api/work-order/list` | ⚠️ 状态值变更 |
| AI 对话 | POST | `/api/ai/chat` | ✅ 正常 |
| AI 找物 | POST | `/api/ai/find-item` | ✅ 正常 |
