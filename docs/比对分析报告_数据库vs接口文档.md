# 数据库脚本与 API 接口测试文档 全面比对分析报告

> 生成时间：2026-06-14  
> 比对范围：db_schema.sql（22张表）↔ API_TESTING_DOC.md（原7个接口）↔ 后端Java代码 ↔ 前端Flutter/Web代码

---

## 一、总体差距总结

| 维度 | 数据库脚本 db_schema.sql | API 测试文档 API_TESTING_DOC.md | 后端 Java 代码 |
|------|--------------------------|-------------------------------|---------------|
| **表/接口数量** | **22 张表** | **7 个接口**（过少） | **4 个 Controller**（过少） |
| **覆盖率** | 100%（基准） | **31.8%**（仅覆盖4张表） | **18.2%**（仅4个模块） |
| **数据完整性** | ✅ 每条 ≥3条，无空白 | ❌ 缺少15个业务模块接口 | ❌ 后端缺少18个模块 |

---

## 二、数据库各表数据完整性检查 ✅

| # | 表名 | 数据行数 | 是否≥3 | 是否有空值 | 状态 |
|---|------|---------|--------|-----------|------|
| 1 | elder_user | 5 条 | ✅ | 无空白 | ✅ 完美 |
| 2 | device | 17 条 | ✅ | 无空白 | ✅ 完美 |
| 3 | sensor_data | 31 条 | ✅ | 无空白 | ✅ 完美 |
| 4 | blood_pressure | 9 条 | ✅ | 无空白 | ✅ 完美 |
| 5 | alarm_event | 8 条 | ✅ | 无空白 | ✅ 完美 |
| 6 | local_agent | 5 条 | ✅ | 无空白 | ✅ 完美 |
| 7 | cloud_agent | 3 条 | ✅ | 无空白 | ✅ 完美 |
| 8 | music_intervention | 5 条 | ✅ | 无空白 | ✅ 完美 |
| 9 | home_control_log | 6 条 | ✅ | 无空白 | ✅ 完美 |
| 10 | agent_conversation | 6 条 | ✅ | 无空白 | ✅ 完美 |
| 11 | agent_intent_log | 8 条 | ✅ | 无空白 | ✅ 完美 |
| 12 | staff_user | 4 条 | ✅ | 无空白 | ✅ 完美 |
| 13 | family_user | 5 条 | ✅ | 无空白 | ✅ 完美 |
| 14 | emergency_contact | 7 条 | ✅ | 无空白 | ✅ 完美 |
| 15 | health_record | 5 条 | ✅ | 无空白 | ✅ 完美 |
| 16 | sos_record | 5 条 | ✅ | 无空白 | ✅ 完美 |
| 17 | work_order | 5 条 | ✅ | 无空白 | ✅ 完美 |
| 18 | service_request | 5 条 | ✅ | 无空白 | ✅ 完美 |
| 19 | monitor_request | 5 条 | ✅ | 无空白 | ✅ 完美 |
| 20 | notification | 8 条 | ✅ | 无空白 | ✅ 完美 |
| 21 | ai_advice | 5 条 | ✅ | 无空白 | ✅ 完美 |
| 22 | alarm_process | 5 条 | ✅ | 无空白 | ✅ 完美 |

### ✅ 数据库结论

**每条数据表都有≥3条模拟数据，且所有字段均已填充，无空白/空值。数据库脚本完整无误。**

---

## 三、缺失的 API 接口清单

原 API 文档只有 **7 个接口**，仅覆盖了 elder_user、sensor_data、blood_pressure、alarm_event 共4张表。需要新增以下接口：

### P0 级 - 必须新增

| # | API 路径 | 方法 | 对应数据库表 | 对应的前端页面 |
|---|---------|------|------------|--------------|
| 8 | /api/elder/list | GET | elder_user | WebAdmin 老人管理 |
| 9 | /api/elder/{id} | GET | elder_user | Flutter 个人信息 |
| 10 | /api/elder/{id} | PUT | elder_user | WebAdmin 编辑老人 |
| 11 | /api/device/list | GET | device | WebAdmin 设备管理 |
| 12 | /api/device/{id} | GET | device | 设备详情 |
| 13 | /api/sensor-data/list | GET | sensor_data | 传感器数据查询 |
| 14 | /api/blood-pressure/list | GET | blood_pressure | 血压记录查询 |
| 15 | /api/alarm/list | GET | alarm_event | 告警列表（分页） |
| 16 | /api/alarm/{id}/handle | POST | alarm_event | 处理告警 |
| 17 | /api/auth/login | POST | staff_user | WebAdmin & Flutter 登录 |
| 18 | /api/staff/list | GET | staff_user | 员工管理 |
| 19 | /api/family/list | GET | family_user | 家属管理 |
| 20 | /api/emergency-contact/list | GET | emergency_contact | Flutter 紧急联系人 |
| 21 | /api/health-record/{elderId} | GET | health_record | Flutter 健康档案 |
| 22 | /api/health-record/{elderId} | PUT | health_record | Flutter 保存健康档案 |
| 23 | /api/sos/list | GET | sos_record | SOS记录查询 |
| 24 | /api/work-order/list | GET | work_order | WebAdmin 工单管理 |
| 25 | /api/work-order/create | POST | work_order | 创建工单 |
| 26 | /api/service-request/list | GET | service_request | Flutter 服务申请 |
| 27 | /api/service-request/create | POST | service_request | Flutter 提交申请 |
| 28 | /api/monitor-request/list | GET | monitor_request | 监控申请列表 |
| 29 | /api/monitor-request/approve | POST | monitor_request | 同意监控申请 |
| 30 | /api/notification/list | GET | notification | Flutter 通知列表 |
| 31 | /api/ai-advice/list | GET | ai_advice | AI建议历史 |
| 32 | /api/local-agent/list | GET | local_agent | 本地智能体 |
| 33 | /api/cloud-agent/list | GET | cloud_agent | 云端智能体 |
| 34 | /api/music-intervention/list | GET | music_intervention | 音乐干预记录 |
| 35 | /api/home-control-log/list | GET | home_control_log | 家居控制日志 |
| 36 | /api/conversation/list | GET | agent_conversation | 对话记录 |
| 37 | /api/intent-log/list | GET | agent_intent_log | 意图日志 |
| 38 | /api/alarm-process/list | GET | alarm_process | 告警处理记录 |

### 已存在的接口（7个）
| # | 路径 | 方法 | 状态 |
|---|------|------|------|
| 1-7 | /api/profile, /api/vitals/latest, /api/vitals/history, /api/alerts, /api/alerts/latest, /api/ai/analyze | GET/PATCH/POST | ✅ 已有 |

---

## 四、数据库脚本发现问题

### 1. 中文注释乱码
- db_schema.sql 中所有 COMMENT 内容为乱码，例如：
  - COMMENT '鑰佷汉鐢ㄦ埛琛?' 应为 '老人用户表'
- 原因：文件保存时编码为 GBK，但 MySQL 预期 utf8mb4
- **修复建议**：用记事本另存为 UTF-8 编码

### 2. 字段约束建议
| 表 | 字段 | 建议 |
|----|------|------|
| sos_record | handled_time | 增加 NOT NULL DEFAULT '1970-01-01 00:00:00' |
| work_order | complete_time | 同上 |
| alarm_process | 缺少 created_at | 建议增加该字段 |

---

## 五、前端已调用但后端缺失的接口

Flutter 的 remote_api_service.dart 已定义但后端 Java 未实现：

| 调用路径 | 用途 |
|---------|------|
| /api/elder/{elderId} | GET/PUT 老人信息 |
| /api/health/latest/{elderId} | GET 最新体征 |
| /api/health/trend/{elderId} | GET 体征趋势 |
| /api/health/analysis/{elderId} | GET AI分析 |
| /api/alarm/list | GET 告警列表 |
| /api/notification/list | GET 通知列表 |
| /api/monitor-request/{id}/approve | POST 同意 |
| /api/monitor-request/{id}/reject | POST 拒绝 |
| /api/monitor-request/{id}/revoke | POST 撤销 |
| /api/service-request/my-list | GET 服务申请列表 |
| /api/service-request | POST 提交申请 |

---

## 六、最终结论

| 检查项 | 结果 |
|--------|------|
| 数据库每表≥3条数据 | ✅ 全部通过（22/22 表均满足） |
| 每条数据无空白字段 | ✅ 全部通过（所有INSERT数据完整） |
| API文档覆盖所有数据库表 | ❌ 仅覆盖 18.2%（4/22 表） |
| 数据库设计完整性 | ✅ 优秀（22张表覆盖完整业务） |
| 后端代码覆盖率 | ❌ 严重不足（仅有4个Controller） |
