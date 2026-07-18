# 安心伴 (anxinban) 数据库表结构文档

> 数据库：`anxinban` | 引擎：MySQL 8.0 | 字符集：utf8mb4 | 总表数：35 张（生产环境 v20260704） | 更新时间：2026-07-17

---

## 📊 数据库整体架构图

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                           anxinban 数据库架构                                  │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  elder_user  │  │ family_user  │  │  staff_user  │  │emergency_ctct│     │
│  │   (老人档案)  │  │   (家属用户)  │  │   (工作人员)  │  │  (紧急联系人) │     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
│         │                 │                 │                 │              │
│  ┌──────┴─────────────────┴─────────────────┴─────────────────┴──────┐      │
│  │                        核心业务关联                                  │      │
│  ├────────────────────────────────────────────────────────────────────┤      │
│  │                                                                    │      │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                │      │
│  │  │   device     │  │ sensor_data │  │health_record│                │      │
│  │  │   (设备管理)  │  │ (传感器数据) │  │ (健康档案)   │                │      │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                │      │
│  │                                                                    │      │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                │      │
│  │  │ alarm_event  │  │  work_order │  │ notification│                │      │
│  │  │  (告警事件)  │  │   (工单)    │  │   (通知)    │                │      │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                │      │
│  │                                                                    │      │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                │      │
│  │  │local_agent  │  │cloud_agent  │  │ai_analysis  │                │      │
│  │  │ (本地智能体) │  │ (云端智能体) │  │ (AI分析记录) │                │      │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                │      │
│  │                                                                    │      │
│  └────────────────────────────────────────────────────────────────────┘      │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## 一、用户与权限模块 (6 张表)

### 1.1 elder_user — 老人档案表

核心实体表，存储老人基本信息、健康状态、摄像头授权状态。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `elder_id` | VARCHAR(255) UNIQUE | 老人业务ID |
| `name` | VARCHAR(255) | 姓名 |
| `age` | INT | 年龄 |
| `gender` | VARCHAR(255) | 性别 |
| `address` | VARCHAR(255) | 住址 |
| `building` | VARCHAR(255) | 所在楼栋 |
| `room_number` | VARCHAR(255) | 房间号 |
| `phone` | VARCHAR(255) | 手机号 |
| `password` | VARCHAR(255) | 密码 |
| `health_status` | VARCHAR(255) | 健康状态 (normal/warning/danger) |
| `health_note` | VARCHAR(255) | 健康备注/标签 |
| `family_phone` | VARCHAR(255) | 家属电话 |
| `community_id` | VARCHAR(255) | 所属社区ID |
| `avatar` | VARCHAR(255) | 头像URL |
| `has_camera` | TINYINT(1) | 是否有摄像头 |
| `camera_auth_until` | BIGINT | 监控授权到期时间戳 |
| `camera_pending` | TINYINT(1) | 是否有待审批监控申请 |
| `last_online` | DATETIME | 最后在线时间 |
| `created_at` / `update_time` | DATETIME | 创建/更新时间 |

### 1.2 family_user — 家属用户表

家属APP端用户账号信息。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `family_id` | VARCHAR(255) UNIQUE | 家属业务ID |
| `name` | VARCHAR(255) | 姓名 |
| `phone` | VARCHAR(255) UNIQUE | 手机号 |
| `password` | VARCHAR(255) | 密码 |
| `elder_id` | VARCHAR(255) | 关联老人ID |
| `relation` | VARCHAR(255) | 与老人关系 |
| `avatar` | VARCHAR(255) | 头像URL |
| `created_at` / `update_time` | DATETIME | 创建/更新时间 |

### 1.3 staff_user — 工作人员表

社区管理人员（Web端）账号信息。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `staff_id` | VARCHAR(255) UNIQUE | 员工业务ID |
| `username` | VARCHAR(255) UNIQUE | 用户名 |
| `name` | VARCHAR(255) | 姓名 |
| `phone` | VARCHAR(255) UNIQUE | 手机号 |
| `password` | VARCHAR(255) | 密码 |
| `role` | VARCHAR(255) | 角色 (admin/supervisor/staff) |
| `community_id` | VARCHAR(255) | 所属社区ID |
| `avatar` | VARCHAR(255) | 头像URL |
| `created_at` / `update_time` | DATETIME | 创建/更新时间 |

### 1.4 emergency_contact — 紧急联系人表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `contact_id` | VARCHAR(255) UNIQUE | 联系人业务ID |
| `elder_id` | VARCHAR(255) | 关联老人ID |
| `name` | VARCHAR(255) | 姓名 |
| `phone` | VARCHAR(255) | 手机号 |
| `relation` | VARCHAR(255) | 关系 |
| `is_primary` | TINYINT(1) | 是否主要联系人 |
| `sort_order` | INT | 排序 |
| `created_at` / `update_time` | DATETIME | 创建/更新时间 |

### 1.5 elderly — 老人信息表（旧版/备用）

> ⚠️ 当前为空，新版使用 `elder_user` 表。结构与 `elder_user` 类似，字段名略有不同。

### 1.6 staff — 员工表（旧版/备用）

> ⚠️ 当前为空，新版使用 `staff_user` 表。结构与 `staff_user` 类似。

---

## 二、设备与传感器模块 (2 张表)

### 2.1 device — 设备表

存储老人家中/社区的智能硬件设备信息。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `device_id` | VARCHAR(255) UNIQUE | 设备业务ID |
| `elder_id` | VARCHAR(255) | 所属老人ID |
| `device_type` | VARCHAR(255) | 设备类型 (手环/摄像头/烟感/门锁等) |
| `device_name` | VARCHAR(255) | 设备名称 |
| `location` | VARCHAR(255) | 安装位置 |
| `building` | VARCHAR(255) | 所在楼栋 |
| `room` | VARCHAR(255) | 房间号 |
| `status` | VARCHAR(255) | 状态 (online/offline) |
| `battery_level` | INT | 电量 (有线设备填0) |
| `last_online_time` | DATETIME | 最后在线时间 |
| `created_at` / `update_time` | DATETIME | 创建/更新时间 |

### 2.2 sensor_data — 传感器数据表

存储各类 IoT 传感器上报的原始数据。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `elder_id` | VARCHAR(255) | 老人ID |
| `device_id` | VARCHAR(255) | 设备ID |
| `sensor_type` | VARCHAR(255) | 传感器类型 |
| `value` | DOUBLE | 数值 |
| `unit` | VARCHAR(255) | 单位 |
| `is_abnormal` | TINYINT(1) | 是否异常 |
| `timestamp` | DATETIME | 数据时间戳 |
| `created_at` | DATETIME | 入库时间 |

---

## 三、健康数据模块 (4 张活跃表 + 3 张已废弃表)

### 3.1 blood_pressure — 血压记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `bp_id` | VARCHAR(255) UNIQUE | 血压记录业务ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `systolic` | INT | 收缩压 |
| `diastolic` | INT | 舒张压 |
| `timestamp` | DATETIME | 测量时间 |
| `created_at` | DATETIME | 入库时间 |

### 3.2 heart_rate — 心率记录表 ⚠️ 已废弃

> ⚠️ **已废弃**：数据已合并到 `sensor_data` 表，使用 `sensor_type` 字段区分数据类型。生产环境（2026-07-04）中不再使用此独立表。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `hr_id` | VARCHAR(50) UNIQUE | 心率记录业务ID |
| `elder_id` | VARCHAR(50) | 老人ID |
| `value` | INT | 心率值 |
| `unit` | VARCHAR(20) | 单位 (次/分) |
| `timestamp` | DATETIME | 测量时间 |
| `created_at` | DATETIME | 入库时间 |

> 索引: `idx_hr_elder`(elder_id), `idx_hr_timestamp`(timestamp)

### 3.3 blood_oxygen — 血氧记录表 ⚠️ 已废弃

> ⚠️ **已废弃**：数据已合并到 `sensor_data` 表，使用 `sensor_type` 字段区分数据类型。生产环境（2026-07-04）中不再使用此独立表。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `bo_id` | VARCHAR(50) UNIQUE | 血氧记录业务ID |
| `elder_id` | VARCHAR(50) | 老人ID |
| `value` | DECIMAL(5,1) | 血氧值 |
| `unit` | VARCHAR(20) | 单位 (%) |
| `timestamp` | DATETIME | 测量时间 |
| `created_at` | DATETIME | 入库时间 |

> 索引: `idx_bo_elder`(elder_id), `idx_bo_timestamp`(timestamp)

### 3.4 body_temperature — 体温记录表 ⚠️ 已废弃

> ⚠️ **已废弃**：数据已合并到 `sensor_data` 表，使用 `sensor_type` 字段区分数据类型。生产环境（2026-07-04）中不再使用此独立表。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `bt_id` | VARCHAR(50) UNIQUE | 体温记录业务ID |
| `elder_id` | VARCHAR(50) | 老人ID |
| `value` | DECIMAL(4,1) | 体温值 |
| `unit` | VARCHAR(20) | 单位 (℃) |
| `timestamp` | DATETIME | 测量时间 |
| `created_at` | DATETIME | 入库时间 |

> 索引: `idx_bt_elder`(elder_id), `idx_bt_timestamp`(timestamp)

### 3.5 health_record — 健康记录表

存储老人健康档案和病史记录。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `record_id` | VARCHAR(255) UNIQUE | 记录业务ID |
| `elder_id` | VARCHAR(255) UNIQUE | 老人ID |
| `hospitalization_info` | VARCHAR(255) | 住院信息 |
| `medical_history` | VARCHAR(255) | 既往病史 |
| `allergy_history` | VARCHAR(255) | 过敏史 |
| `common_medications` | VARCHAR(255) | 常用药物 |
| `blood_type` | VARCHAR(255) | 血型 |
| `remarks` | VARCHAR(255) | 备注 |
| `created_at` / `update_time` | DATETIME | 创建/更新时间 |

### 3.6 health_vital_record — 健康体征记录表

存储老人日常体征综合数据。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `elder_id` | VARCHAR(64) | 老人ID |
| `heart_rate` | INT | 心率 |
| `systolic` / `diastolic` | INT | 收缩压/舒张压 |
| `spo2` | INT | 血氧饱和度 |
| `temperature` | DECIMAL(3,1) | 体温 |
| `source` | VARCHAR(50) | 数据来源 |
| `measured_at` | DATETIME(6) | 测量时间 |
| `created_at` / `updated_at` | DATETIME(6) | 创建/更新时间 |

### 3.7 sleep_record — 睡眠数据表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `elder_id` | VARCHAR(64) | 老人ID |
| `in_bed` | BIT(1) | 是否在床 |
| `bed_time` | VARCHAR(10) | 上床时间 |
| `deep_sleep_percent` | INT | 深睡百分比 |
| `quality_score` | INT | 睡眠质量评分 |
| `wake_count` | INT | 醒来次数 |
| `recorded_at` | DATETIME(6) | 记录时间 |
| `created_at` | DATETIME(6) | 创建时间 |

---

## 四、AI 智能体模块 (4 张表)

### 4.1 local_agent — 本地 Agent 配置表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `agent_id` | VARCHAR(255) UNIQUE | 智能体业务ID |
| `agent_type` | VARCHAR(255) | 类型 (local_gateway) |
| `status` | VARCHAR(255) | 状态 (online/offline) |
| `last_heartbeat` | DATETIME | 最后心跳时间 |
| `ip` | VARCHAR(255) | IP地址 |
| `device_count` | INT | 设备总数 |
| `connected_devices` | INT | 已连接设备数 |
| `created_at` / `update_time` | DATETIME | 创建/更新时间 |

### 4.2 cloud_agent — 云端 Agent 配置表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `agent_id` | VARCHAR(255) UNIQUE | 智能体业务ID |
| `agent_type` | VARCHAR(255) | 类型 (cloud_agent) |
| `status` | VARCHAR(255) | 状态 (online/offline) |
| `last_heartbeat` | DATETIME | 最后心跳时间 |
| `ip` | VARCHAR(255) | IP地址 |
| `device_count` | INT | 设备总数 |
| `connected_devices` | INT | 已连接设备数 |
| `created_at` / `update_time` | DATETIME | 创建/更新时间 |

### 4.3 agent_conversation — Agent 会话记录表

存储用户与 AI Agent 的对话历史。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `conversation_id` | VARCHAR(255) UNIQUE | 会话业务ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `agent_type` | VARCHAR(255) | 智能体类型 (local_agent/cloud_agent) |
| `user_text` | VARCHAR(255) | 用户输入 |
| `intent` | VARCHAR(255) | 识别意图 |
| `agent_reply` | VARCHAR(255) | 智能体回复 |
| `risk_level` | VARCHAR(255) | 风险等级 |
| `created_at` | DATETIME | 创建时间 |

### 4.4 agent_intent_log — Agent 意图日志表

记录 AI Agent 每次意图识别的详细日志。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `intent_id` | VARCHAR(255) UNIQUE | 意图记录业务ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `source` | VARCHAR(255) | 输入来源 (voice/app) |
| `user_text` | VARCHAR(255) | 用户输入 |
| `intent` | VARCHAR(255) | 识别意图 |
| `confidence` | DOUBLE | 置信度 |
| `handled_by` | VARCHAR(255) | 处理智能体 |
| `created_at` | DATETIME | 创建时间 |

---

## 五、告警与安全模块 (4 张表)

### 5.1 alarm_event — 告警事件表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `alarm_id` | VARCHAR(255) UNIQUE | 报警业务ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `device_id` | VARCHAR(255) | 关联设备ID |
| `alarm_type` | VARCHAR(255) | 报警类型 (fall/health_abnormal/smoke/sos等) |
| `alarm_level` | VARCHAR(255) | 报警等级 (critical/high/medium/low) |
| `alarm_status` | VARCHAR(255) | 状态 (pending/handled) |
| `description` | VARCHAR(255) | 描述 |
| `building` / `room_number` / `unit` | VARCHAR(255) | 位置信息 |
| `snapshot_url` | VARCHAR(255) | 抓拍快照URL |
| `handler` / `handler_name` | VARCHAR(255) | 处理人信息 |
| `handle_remark` | VARCHAR(255) | 处理备注 |
| `is_read` | TINYINT(1) | 是否已读 |
| `created_at` | DATETIME | 发生时间 |
| `resolved_at` | DATETIME | 解决时间 |
| `location` | VARCHAR(255) | 位置描述 |

### 5.2 alert — 告警表

AlarmEvent 实体对应的 JPA 表，与 `alarm_event` 互补。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `alarm_id` | VARCHAR(64) UNIQUE | 告警业务ID |
| `type` | VARCHAR(255) | 告警类型 |
| `risk_level` | VARCHAR(255) | 风险等级 |
| `status` | VARCHAR(255) | 状态 |
| `elder_id` / `device_id` | VARCHAR(255) | 关联老人/设备 |
| `description` | VARCHAR(255) | 描述 |
| `building` / `room_number` / `location` | VARCHAR(255) | 位置信息 |
| `occur_time` | DATETIME(6) | 发生时间 |
| `handle_note` | TEXT | 处理备注 |
| `handler_id` / `handler_name` | VARCHAR(255) | 处理人信息 |
| `handle_time` | DATETIME(6) | 处理时间 |
| `snapshot_url` | VARCHAR(255) | 抓拍URL |
| `is_read` | BIT(1) | 是否已读 |

### 5.3 alarm_process — 告警处理记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `process_id` | VARCHAR(255) UNIQUE | 处理记录业务ID |
| `alarm_id` | VARCHAR(255) | 告警ID |
| `handler_id` | VARCHAR(255) | 处理人ID |
| `handler_type` | VARCHAR(255) | 处理人类型 |
| `action` | VARCHAR(255) | 处理动作 |
| `result` | VARCHAR(255) | 处理结果 |
| `remark` | VARCHAR(255) | 备注 |
| `process_time` | DATETIME | 处理时间 |

### 5.4 sos_record — SOS 呼救记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `sos_id` | VARCHAR(255) UNIQUE | SOS业务ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `trigger_time` | DATETIME | 触发时间 |
| `status` | VARCHAR(255) | 状态 |
| `location` | VARCHAR(255) | 位置 |
| `handler_id` | VARCHAR(255) | 处理人ID |
| `handled_time` | DATETIME | 处理时间 |
| `created_at` | DATETIME | 创建时间 |

---

## 六、工单与服务模块 (5 张表)

### 6.1 work_order — 工单表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `order_id` | VARCHAR(255) UNIQUE | 工单业务ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `order_type` | VARCHAR(255) | 工单类型 (紧急巡检/健康关注/设备维修等) |
| `description` | VARCHAR(255) | 描述 |
| `status` | VARCHAR(255) | 状态 |
| `creator_id` | VARCHAR(255) | 创建人ID |
| `handler_id` / `handler_name` / `handler_phone` | VARCHAR(255) | 处理人信息 |
| `complete_time` | DATETIME | 完成时间 |
| `service_request_id` | VARCHAR(255) | 关联服务请求ID |
| `alarm_id` | VARCHAR(255) | 关联告警ID |
| `family_request_id` | VARCHAR(255) | 关联家属请求ID |
| `from_family` | BIT(1) | 是否来自家属 |
| `created_at` / `update_time` | DATETIME | 创建/更新时间 |

### 6.2 service_request — 服务请求表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `request_id` | VARCHAR(255) UNIQUE | 请求业务ID |
| `family_id` | VARCHAR(255) | 家属ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `request_type` | VARCHAR(255) | 请求类型 |
| `content` | VARCHAR(255) | 请求内容 |
| `status` | VARCHAR(255) | 状态 |
| `related_order_id` | VARCHAR(255) | 关联工单ID |
| `reject_reason` | VARCHAR(255) | 拒绝原因 |
| `created_at` / `update_time` | DATETIME | 创建/更新时间 |

### 6.3 family_request — 家属服务申请表

ServiceRequest 实体对应的 JPA 表。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `request_id` | VARCHAR(255) UNIQUE | 请求业务ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `family_id` / `family_name` / `family_phone` | VARCHAR(255) | 家属信息 |
| `type` | VARCHAR(255) | 请求类型 |
| `content` | VARCHAR(255) | 请求内容 |
| `status` | VARCHAR(255) | 状态 |
| `reject_reason` | VARCHAR(255) | 拒绝原因 |
| `converted_work_order_id` | VARCHAR(255) | 转换后的工单ID |
| `request_time` | DATETIME(6) | 请求时间 |

### 6.4 monitor_request — 监控请求记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `request_id` | VARCHAR(255) UNIQUE | 申请业务ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `staff_id` / `staff_name` / `staff_phone` | VARCHAR(255) | 申请人信息 |
| `reason` | VARCHAR(255) | 申请原因 |
| `status` | VARCHAR(255) | 状态 |
| `approved_at` | BIGINT | 审批时间戳 |
| `expired_at` | DATETIME | 过期时间 |
| `created_at` / `update_time` | DATETIME | 创建/更新时间 |

### 6.5 camera_request — 摄像头请求表

MonitorRequest 实体对应的 JPA 表。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `request_id` | VARCHAR(255) UNIQUE | 请求业务ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `staff_id` / `staff_name` / `staff_phone` | VARCHAR(255) | 申请人信息 |
| `camera_type` | VARCHAR(255) | 摄像头类型 |
| `reason` | VARCHAR(255) | 申请原因 |
| `status` | VARCHAR(255) | 状态 |
| `reject_reason` | VARCHAR(255) | 拒绝原因 |
| `approved_at` | BIGINT | 审批时间戳 |
| `expired_at` | DATETIME(6) | 过期时间 |
| `request_time` | DATETIME(6) | 请求时间 |

---

## 七、AI 分析与建议模块 (2 张表)

### 7.1 ai_advice — AI 建议表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `advice_id` | VARCHAR(255) UNIQUE | 建议业务ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `advice_type` | VARCHAR(255) | 建议类型 |
| `input_summary` | VARCHAR(255) | 输入摘要 |
| `advice_content` | VARCHAR(255) | 建议内容 |
| `created_at` | DATETIME | 创建时间 |

### 7.2 ai_analysis_record — AI 分析记录表

保存每次 AI 健康分析的完整结果，供家属APP和社区大屏展示。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `record_id` | VARCHAR(64) UNIQUE | 记录业务ID |
| `elder_id` | VARCHAR(64) | 老人ID |
| `risk_level` | VARCHAR(32) | 风险等级 |
| `risk_reason` | VARCHAR(500) | 风险原因 |
| `need_alarm` | BIT(1) | 是否需要告警 |
| `need_work_order` | BIT(1) | 是否需要工单 |
| `work_order_type` | VARCHAR(64) | 工单类型 |
| `generated_alarm_id` | VARCHAR(64) | 生成的告警ID |
| `source` | VARCHAR(64) | 数据来源 |
| `model` | VARCHAR(64) | AI模型 |
| `elder_reply` | VARCHAR(500) | 给老人的回复 |
| `family_notice` | VARCHAR(1000) | 家属通知内容 |
| `community_suggestion` | VARCHAR(1000) | 社区建议 |
| `suggestion` | VARCHAR(1000) | AI建议 |
| `scope` | VARCHAR(20) | 范围 |
| `analyzed_at` | DATETIME(6) | 分析时间 |
| `created_at` | DATETIME(6) | 创建时间 |

---

## 八、智能家居模块 (3 张表)

### 8.1 home_control_log — 家居控制日志表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `control_id` | VARCHAR(255) UNIQUE | 控制记录业务ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `device_id` | VARCHAR(255) | 设备ID |
| `command` | VARCHAR(255) | 控制指令 |
| `source_agent` | VARCHAR(255) | 来源智能体 |
| `result` | VARCHAR(255) | 执行结果 |
| `created_at` | DATETIME | 执行时间 |

### 8.2 music_intervention — 音乐干预表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `intervention_id` | VARCHAR(255) UNIQUE | 干预业务ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `trigger_reason` | VARCHAR(255) | 触发原因 |
| `music_type` | VARCHAR(255) | 音乐类型 |
| `start_time` | DATETIME | 开始时间 |
| `duration_minutes` | INT | 持续时长(分钟) |
| `before_state` | VARCHAR(255) | 干预前状态 |
| `after_state` | VARCHAR(255) | 干预后状态 |
| `result` | VARCHAR(255) | 执行结果 (completed/pending) |
| `created_at` / `update_time` | DATETIME | 创建/更新时间 |

### 8.3 voice_prompt — 语音/音乐疗法提醒表

MusicIntervention 实体对应的 JPA 表。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `intervention_id` | VARCHAR(255) UNIQUE | 干预业务ID |
| `elder_id` | VARCHAR(255) | 老人ID |
| `reason` | VARCHAR(255) | 触发原因 |
| `music_type` | VARCHAR(255) | 音乐类型 |
| `music` | VARCHAR(255) | 音乐内容 |
| `duration` | INT | 持续时长 |
| `before_state` / `after_state` | VARCHAR(255) | 前后状态 |
| `result` | VARCHAR(255) | 执行结果 |
| `closed` | BIT(1) | 是否已关闭 |
| `prompt_time` | DATETIME(6) | 提示时间 |

---

## 九、交互与陪伴模块 (2 张表)

### 9.1 companion_record — 陪伴交互记录表

存储 AI 陪伴机器人与老人的对话记录。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `elder_id` | VARCHAR(64) | 老人ID |
| `interaction_time` | DATETIME(6) | 交互时间 |
| `emotion` | VARCHAR(20) | 情绪状态 |
| `emotion_color` | VARCHAR(10) | 情绪颜色标识 |
| `summary` | TEXT | 对话摘要 |
| `created_at` | DATETIME(6) | 创建时间 |

### 9.2 vlm_record — VLM 找物品记录表

存储视觉大模型找物品的交互记录。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `elder_id` | VARCHAR(64) | 老人ID |
| `question` | TEXT | 用户问题 |
| `answer` | TEXT | 模型回答 |
| `item` | VARCHAR(100) | 寻找的物品 |
| `location` | VARCHAR(200) | 找到的位置 |
| `result` | VARCHAR(20) | 查找结果 |
| `query_time` | DATETIME(6) | 查询时间 |
| `created_at` | DATETIME(6) | 创建时间 |

---

## 十、通知模块 (2 张表)

### 10.1 notification — 通知记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `notification_id` | VARCHAR(255) UNIQUE | 通知业务ID |
| `user_id` | VARCHAR(255) | 接收用户ID |
| `user_type` | VARCHAR(255) | 用户类型 (staff/family/elder) |
| `notification_type` | VARCHAR(255) | 通知类型 |
| `title` | VARCHAR(255) | 标题 |
| `content` | VARCHAR(255) | 内容 |
| `is_read` | TINYINT(1) | 是否已读 |
| `building` / `room` | VARCHAR(255) | 位置信息 |
| `order_id` / `request_id` | VARCHAR(255) | 关联工单/请求ID |
| `elder_id` / `related_id` | VARCHAR(255) | 关联老人/关联ID |
| `created_at` | DATETIME | 创建时间 |

### 10.2 app_notification — APP 通知表

Notification 实体对应的 JPA 表。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `notification_id` | VARCHAR(255) UNIQUE | 通知业务ID |
| `user_id` | VARCHAR(255) | 接收用户ID |
| `user_type` | VARCHAR(255) | 用户类型 |
| `type` | VARCHAR(255) | 通知类型 |
| `title` | VARCHAR(255) | 标题 |
| `content` | VARCHAR(255) | 内容 |
| `is_read` | BIT(1) | 是否已读 |
| `elder_id` | VARCHAR(255) | 关联老人ID |
| `building` / `room` | VARCHAR(255) | 位置信息 |
| `work_order_id` / `camera_request_id` | VARCHAR(255) | 关联工单/摄像头请求 |
| `notify_time` | DATETIME(6) | 通知时间 |
| `created_at` | DATETIME(6) | 创建时间 |

---

## 十一、辅助模块 (1 张表)

### 11.1 camera_view_record — 监控查看记录表

记录每次实际查看监控的审计日志。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键，自增 |
| `camera_request_id` | VARCHAR(255) | 关联摄像头请求ID |
| `camera_type` | VARCHAR(20) | 摄像头类型 |
| `staff_id` | VARCHAR(255) | 查看人员ID |
| `view_time` | DATETIME(6) | 查看时间 |
| `duration` | INT | 查看持续时长(秒) |
| `created_at` | DATETIME(6) | 创建时间 |

---

## 📋 表名速查索引

| 序号 | 表名 | 中文名称 | 业务模块 | 对应实体类 |
|------|------|----------|----------|-----------|
| 1 | `elder_user` | 老人档案表 | 用户权限 | ElderUser |
| 2 | `family_user` | 家属用户表 | 用户权限 | FamilyUser |
| 3 | `staff_user` | 工作人员表 | 用户权限 | StaffUser |
| 4 | `emergency_contact` | 紧急联系人表 | 用户权限 | EmergencyContact |
| 5 | `elderly` | 老人信息表(旧) | 用户权限 | — |
| 6 | `staff` | 员工表(旧) | 用户权限 | — |
| 7 | `device` | 设备表 | 设备传感器 | Device |
| 8 | `sensor_data` | 传感器数据表 | 设备传感器 | SensorData |
| 9 | `blood_pressure` | 血压记录表 | 健康数据 | BloodPressure |
| 10 | `heart_rate` | 心率记录表 ⚠️已废弃 | 健康数据 | — |
| 11 | `blood_oxygen` | 血氧记录表 ⚠️已废弃 | 健康数据 | — |
| 12 | `body_temperature` | 体温记录表 ⚠️已废弃 | 健康数据 | — |
| 13 | `health_record` | 健康记录表 | 健康数据 | HealthRecord |
| 14 | `health_vital_record` | 健康体征记录表 | 健康数据 | HealthVitalRecord |
| 15 | `sleep_record` | 睡眠数据表 | 健康数据 | SleepRecord |
| 16 | `local_agent` | 本地Agent配置表 | AI智能体 | LocalAgent |
| 17 | `cloud_agent` | 云端Agent配置表 | AI智能体 | CloudAgent |
| 18 | `agent_conversation` | Agent会话记录表 | AI智能体 | AgentConversation |
| 19 | `agent_intent_log` | Agent意图日志表 | AI智能体 | AgentIntentLog |
| 20 | `alarm_event` | 告警事件表 | 告警安全 | AlarmEvent |
| 21 | `alert` | 告警表(JPA) | 告警安全 | AlarmEvent |
| 22 | `alarm_process` | 告警处理记录表 | 告警安全 | AlarmProcess |
| 23 | `sos_record` | SOS呼救记录表 | 告警安全 | SosRecord |
| 24 | `work_order` | 工单表 | 工单服务 | WorkOrder |
| 25 | `service_request` | 服务请求表 | 工单服务 | ServiceRequest |
| 26 | `family_request` | 家属服务申请表 | 工单服务 | ServiceRequest |
| 27 | `monitor_request` | 监控请求记录表 | 工单服务 | MonitorRequest |
| 28 | `camera_request` | 摄像头请求表 | 工单服务 | MonitorRequest |
| 29 | `camera_view_record` | 监控查看记录表 | 辅助 | CameraViewRecord |
| 30 | `ai_advice` | AI建议表 | AI分析 | AiAdvice |
| 31 | `ai_analysis_record` | AI分析记录表 | AI分析 | AiAnalysisRecord |
| 32 | `home_control_log` | 家居控制日志表 | 智能家居 | HomeControlLog |
| 33 | `music_intervention` | 音乐干预表 | 智能家居 | MusicIntervention |
| 34 | `voice_prompt` | 语音音乐提醒表 | 智能家居 | MusicIntervention |
| 35 | `companion_record` | 陪伴交互记录表 | 交互陪伴 | CompanionRecord |
| 36 | `vlm_record` | VLM找物品记录表 | 交互陪伴 | — |
| 37 | `notification` | 通知记录表 | 通知 | Notification |
| 38 | `app_notification` | APP通知表 | 通知 | Notification |

---

## 🔗 核心业务关系图

```
elder_user (老人) ──1:N──> device (设备)
       │
       ├──1:N──> sensor_data (传感器数据)
       ├──1:N──> blood_pressure (血压) + sensor_data (心率/血氧/体温统一存储)
       ├──1:1──> health_record (健康档案)
       ├──1:N──> health_vital_record (体征记录)
       ├──1:N──> sleep_record (睡眠数据)
       ├──1:N──> emergency_contact (紧急联系人)
       ├──1:N──> alarm_event / alert (告警)
       ├──1:N──> sos_record (SOS呼救)
       ├──1:N──> work_order (工单)
       ├──1:N──> service_request (服务请求)
       ├──1:N──> agent_conversation (AI对话)
       ├──1:N──> ai_advice (AI建议)
       ├──1:N──> ai_analysis_record (AI分析)
       ├──1:N──> companion_record (陪伴记录)
       ├──1:N──> music_intervention (音乐干预)
       └──1:N──> notification (通知)

family_user (家属) ──1:N──> service_request / family_request
staff_user (员工)  ──1:N──> work_order / alarm_process / monitor_request
local_agent / cloud_agent ──1:N──> home_control_log / agent_conversation
alarm_event ──1:N──> alarm_process (告警处理)
work_order ──关联──> alarm_event / service_request
```

---

> 📅 文档更新时间：2026-07-17 | 基于生产数据库 `anxinban`（35张表，mysqldump 2026-07-04）
