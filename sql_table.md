# 安心伴（anxinban）智慧家居系统 - 数据库全量数据

> 数据库：`anxinban` | 引擎：MySQL 8.0 | 字符集：utf8mb4  
> 共 35 张表，导出时间：2026-06-20

---

## 目录

1. [agent_conversation](#1-agent_conversation) — Agent 会话记录
2. [agent_intent_log](#2-agent_intent_log) — Agent 意图日志
3. [ai_advice](#3-ai_advice) — AI 建议
4. [ai_analysis_record](#4-ai_analysis_record) — AI 分析记录
5. [alarm_event](#5-alarm_event) — 告警事件
6. [alarm_process](#6-alarm_process) — 告警处理记录
7. [alert](#7-alert) — 告警表
8. [app_notification](#8-app_notification) — APP 通知（空）
9. [blood_pressure](#9-blood_pressure) — 血压记录
10. [camera_request](#10-camera_request) — 摄像头请求（空）
11. [camera_view_record](#11-camera_view_record) — 监控查看记录（空）
12. [cloud_agent](#12-cloud_agent) — 云端 Agent
13. [companion_record](#13-companion_record) — 陪伴记录（空）
14. [device](#14-device) — 设备表
15. [elder_user](#15-elder_user) — 老人档案
16. [elderly](#16-elderly) — 老人信息（旧版，空）
17. [emergency_contact](#17-emergency_contact) — 紧急联系人
18. [family_request](#18-family_request) — 家属服务申请（空）
19. [family_user](#19-family_user) — 家属用户
20. [health_record](#20-health_record) — 健康记录
21. [health_vital_record](#21-health_vital_record) — 健康体征记录（空）
22. [home_control_log](#22-home_control_log) — 家居控制日志
23. [local_agent](#23-local_agent) — 本地 Agent
24. [monitor_request](#24-monitor_request) — 监控请求
25. [music_intervention](#25-music_intervention) — 音乐干预
26. [notification](#26-notification) — 通知记录
27. [sensor_data](#27-sensor_data) — 传感器数据
28. [service_request](#28-service_request) — 服务请求
29. [sleep_record](#29-sleep_record) — 睡眠记录（空）
30. [sos_record](#30-sos_record) — SOS 呼救记录
31. [staff](#31-staff) — 员工表（旧版，空）
32. [staff_user](#32-staff_user) — 工作人员
33. [vlm_record](#33-vlm_record) — VLM 找物品记录（空）
34. [voice_prompt](#34-voice_prompt) — 语音/音乐提醒（空）
35. [work_order](#35-work_order) — 工单表

---

## 1. agent_conversation

> Agent 会话记录表 — 存储用户与 AI Agent 的对话历史

| id | conversation_id | elder_id | agent_type | user_text | intent | agent_reply | risk_level | created_at |
|----|-----------------|----------|------------|-----------|--------|-------------|------------|------------|
| 1 | conv_001 | elder_001 | local_agent | 今天血压怎么样？ | query_health | 您今天血压为135/85，处于正常范围。记得按时服药哦。 | low | 2025-05-17 08:30:00 |
| 2 | conv_002 | elder_002 | local_agent | 我有点不舒服，胸口闷。 | report_discomfort | 已为您联系医护人员，请保持冷静，深呼吸。 | high | 2025-05-17 09:10:00 |
| 3 | conv_003 | elder_003 | cloud_agent | 给我播放点轻音乐。 | control_music | 正在为您播放轻音乐，音量已调至适中。 | low | 2025-05-17 10:00:00 |
| 4 | conv_004 | elder_001 | cloud_agent | 帮我打开客厅的灯。 | light-control | 好的，已为您打开客厅灯光。 | low | 2025-05-17 09:30:00 |
| 5 | conv_005 | elder_004 | local_agent | 我的眼镜找不到了。 | find-item | 已为您启动摄像头查找，请在摄像头可视范围内。 | low | 2025-05-17 08:15:00 |
| 6 | conv_006 | elder_005 | cloud_agent | 救命！ | emergency | 已收到紧急呼叫，正在通知工作人员和家属。 | critical | 2025-05-16 23:00:00 |
| 7 | 6e78caf4-8900-4989-84dd-05333a09224c | elder_001 | user | hello | — | — | — | 2026-06-18 12:59:41 |

## 2. agent_intent_log

> Agent 意图日志表 — 记录 AI Agent 每次意图识别的详细日志

| id | intent_id | elder_id | source | user_text | intent | confidence | handled_by | created_at |
|----|-----------|----------|--------|-----------|--------|------------|------------|------------|
| 1 | intent_001 | elder_001 | voice | 今天血压怎么样？ | query_health | 0.95 | local_agent | 2025-05-17 08:30:00 |
| 2 | intent_002 | elder_002 | voice | 我有点不舒服，胸口闷。 | report_discomfort | 0.88 | local_agent | 2025-05-17 09:10:00 |
| 3 | intent_003 | elder_003 | app | 给我播放点轻音乐。 | control_music | 0.92 | cloud_agent | 2025-05-17 10:00:00 |
| 4 | intent_004 | elder_001 | voice | 帮我打开客厅的灯。 | light-control | 0.96 | cloud_agent | 2025-05-17 09:30:00 |
| 5 | intent_005 | elder_004 | voice | 我的眼镜找不到了。 | find-item | 0.85 | local_agent | 2025-05-17 08:15:00 |
| 6 | intent_006 | elder_005 | voice | 救命！ | emergency | 0.98 | cloud_agent | 2025-05-16 23:00:00 |
| 7 | intent_007 | elder_001 | app | 今天天气怎么样？ | chat | 0.78 | cloud_agent | 2025-05-17 07:00:00 |
| 8 | intent_008 | elder_002 | voice | 把窗帘打开。 | curtain-control | 0.91 | local_agent | 2025-05-17 08:00:00 |

## 3. ai_advice

> AI 建议表 — 存储 AI 生成的健康建议内容

| id | advice_id | elder_id | advice_type | input_summary | advice_content | created_at |
|----|-----------|----------|-------------|---------------|----------------|------------|
| 1 | adv_001 | elder_001 | health | 近期血压偏高，血糖波动较大 | 建议增加血压监测频率，每日早晚各一次；调整饮食结构，减少盐分摄入；适当增加户外散步时间。 | 2025-05-17 08:30:00 |
| 2 | adv_002 | elder_002 | fall_prevention | 近期有跌倒记录，骨质疏松 | 建议在卫生间和卧室安装防滑垫；夜间保持走廊照明；穿戴防滑鞋；定期进行骨密度检查。 | 2025-05-17 09:30:00 |
| 3 | adv_003 | elder_003 | cognitive | 记忆力减退，轻度认知障碍 | 建议进行认知训练，如记忆卡片游戏；保持社交活动；规律作息；定期复查认知功能。 | 2025-05-17 10:00:00 |
| 4 | adv_004 | elder_004 | diabetes | 糖尿病足恢复期，血糖控制 | 建议每日检查足部皮肤状况；保持足部清洁干燥；穿宽松舒适的鞋子；定期测量血糖。 | 2025-05-17 08:00:00 |
| 5 | adv_005 | elder_005 | rehabilitation | 脑梗后遗症，行动不便 | 建议坚持每日康复训练；家属协助进行肢体活动；定期复查脑部影像；注意血压控制。 | 2025-05-17 09:00:00 |

## 4. ai_analysis_record

> AI 分析记录表 — 保存每次 AI 健康分析的完整结果（33 条记录）

| id | record_id | elder_id | risk_level | need_alarm | need_work_order | scope | source | model | analyzed_at | created_at |
|----|-----------|----------|------------|------------|-----------------|-------|--------|-------|-------------|------------|
| 1 | ar_3231a91b | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 09:36:54 |
| 2 | ar_3ed45798 | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 09:58:38 |
| 3 | ar_c53a5200 | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 10:37:29 |
| 4 | ar_402b4d4a | elder_001 | 中风险 | ✗ | ✓ | — | python_ai_service | — | — | 2026-06-19 12:22:29 |
| 5 | ar_b487583e | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 12:27:31 |
| 6 | ar_467face5 | elder_001 | 中风险 | ✗ | ✓ | — | python_ai_service | — | — | 2026-06-19 12:51:41 |
| 7 | ar_4a3c1aab | elder_001 | 中风险 | ✗ | ✓ | — | python_ai_service | — | — | 2026-06-19 12:52:19 |
| 8 | ar_f6c885fe | elder_001 | 中风险 | ✗ | ✓ | — | python_ai_service | — | — | 2026-06-19 12:52:49 |
| 9 | ar_a1014a6b | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 12:53:03 |
| 10 | ar_3d82ee56 | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 12:54:40 |
| 11 | ar_0b0616e4 | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 13:00:52 |
| 12 | ar_3d1d6dde | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 13:01:33 |
| 13 | ar_f711bb52 | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 13:01:41 |
| 14 | ar_b97122ab | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 13:01:52 |
| 15 | ar_15a83e6a | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 13:02:02 |
| 16 | ar_ccc2bfad | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 13:11:12 |
| 17 | ar_7e10c0cf | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 13:13:47 |
| 18 | ar_8dff23e2 | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 13:15:21 |
| 19 | ar_d3bf3657 | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-19 13:16:50 |
| 20 | ar_9696cc61 | elder_001 | 低风险 | ✗ | ✓ | — | python_ai_service | — | — | 2026-06-20 01:14:15 |
| 21 | ar_712e31b2 | elder_001 | 正常 | ✗ | ✗ | — | python_ai_service | — | — | 2026-06-20 01:14:18 |
| 22 | ar_5ee2161a | elder_001 | 中风险 | ✗ | ✓ | — | python_ai_service | — | — | 2026-06-20 01:14:45 |
| 23 | ar_8317272c | elder_001 | 低风险 | ✗ | ✓ | — | python_ai_service | — | — | 2026-06-20 01:14:53 |
| 24 | ar_e833f6da | elder_001 | 中风险 | ✗ | ✓ | — | python_ai_service | — | — | 2026-06-20 01:15:07 |
| 25 | ar_1fcc722c | elder_001 | 中风险 | ✗ | ✓ | — | python_ai_service | — | — | 2026-06-20 01:15:13 |
| 26 | ar_6040bcca | elder_001 | 中风险 | ✗ | ✓ | — | python_ai_service | — | — | 2026-06-20 01:15:20 |
| 27 | ar_2da7da20 | elder_001 | 中风险 | ✗ | ✓ | — | python_ai_service | — | — | 2026-06-20 01:15:25 |
| 28 | ar_971e6764 | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-20 01:15:46 |
| 29 | ar_7750a8f8 | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-20 01:15:58 |
| 30 | ar_87e9b2b4 | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-20 01:21:40 |
| 31 | ar_7a1aa39b | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-20 01:23:15 |
| 32 | ar_c87adb6e | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-20 01:24:37 |
| 33 | ar_b22186ad | elder_001 | 高风险 | ✓ | ✓ | — | python_ai_service | — | — | 2026-06-20 01:25:02 |

> 注：完整字段（elder_reply、family_notice、community_suggestion、suggestion、risk_reason、work_order_type、generated_alarm_id）请参见 `anxinban_database_full_script.txt`。

## 5. alarm_event

> 告警事件表 — 存储系统产生的告警事件（40 条记录）

| id | alarm_id | elder_id | device_id | alarm_type | alarm_level | alarm_status | description | building | room_number | handler | handler_name | is_read | created_at | resolved_at |
|----|----------|----------|-----------|------------|-------------|--------------|-------------|----------|-------------|---------|--------------|--------|------------|-------------|
| 1 | alarm_001 | elder_001 | dev_001 | heart_rate | high | handled | 心率持续偏高，超过阈值120bpm | 1号楼 | 1-201 | staff_001 | 张建国 | ✗ | 2025-05-17 09:20:00 | 2025-05-17 02:00:00 |
| 2 | alarm_002 | elder_002 | dev_004 | fall | critical | pending | 检测到跌倒行为，请立即确认 | 2号楼 | 2-301 | — | — | ✗ | 2025-05-17 09:15:00 | — |
| 3 | alarm_003 | elder_001 | dev_002 | intrusion | high | handled | 客厅检测到陌生人闯入 | 1号楼 | 1-201 | staff_001 | 张建国 | ✓ | 2025-05-16 20:00:00 | 2025-05-17 08:30:00 |
| 4 | alarm_004 | elder_003 | dev_009 | inactive | low | handled | 床垫传感器离线超过12小时 | 3号楼 | 3-401 | staff_002 | 李秀英 | ✓ | 2025-05-16 10:00:00 | 2025-05-17 08:00:00 |
| 5 | alarm_005 | elder_002 | dev_006 | smoke | critical | pending | 厨房烟感检测到烟雾浓度超标 | 2号楼 | 2-301 | — | — | ✗ | 2025-05-17 08:00:00 | — |
| 6 | alarm_006 | elder_005 | dev_012 | emergency-call | critical | pending | 手表紧急呼叫触发，心率异常110bpm | 5号楼 | 5-502 | — | — | ✗ | 2025-05-16 23:00:00 | — |
| 7 | alarm_007 | elder_004 | dev_011 | fall | high | pending | 卧室摄像头检测到疑似摔倒行为 | 4号楼 | 4-102 | — | — | ✗ | 2025-05-17 07:00:00 | — |
| 8 | alarm_008 | elder_001 | dev_003 | fingerprint-fail | medium | pending | 指纹连续识别失败3次，已抓拍陌生人 | 1号楼 | 1-201 | — | — | ✗ | 2025-05-17 06:30:00 | — |
| 9 | alarm_e4ee3618 | elder_001 | dev_001 | fall | critical | pending | 血氧偏低(91%)、心率偏高(112bpm)、疑似跌倒 | 1号楼 | 1-201 | — | — | ✗ | 2026-06-18 13:16:25 | — |
| 10 | alarm_49ea4bef | elder_001 | dev_001 | fall | critical | handled | 血氧偏低(91%)、心率偏高(112bpm)、疑似跌倒 | 1号楼 | 1-201 | — | — | ✗ | 2026-06-18 13:18:30 | — |
| 11-19 | *(alarm_4276038d ~ alarm_2fcab5b6)* | elder_001 | dev_001 | fall/health_abnormal | critical | handled/pending | 血氧/心率异常、疑似跌倒 | 1号楼 | 1-201 | — | — | ✗ | 2026-06-18~19 | — |
| 20 | alarm_c1cebbe2 | elder_001 | dev_001 | health_abnormal | critical | handled | 血氧88%+心率125+体温38.5℃ | 1号楼 | 1-201 | — | — | ✗ | 2026-06-19 09:36:45 | — |
| 21 | alarm_b8bde81e | elder_001 | dev_001 | health_abnormal | critical | handled | 血氧88%+心率125+体温38.5℃ | 1号楼 | 1-201 | — | — | ✗ | 2026-06-19 09:58:28 | — |
| 22 | alarm_bd8b9563 | elder_001 | dev_001 | health_abnormal | critical | handled | 血氧88%+心率125+体温38.5℃ | 1号楼 | 1-201 | — | — | ✗ | 2026-06-19 10:37:16 | — |
| 23 | alarm_7d2e5a9e | elder_001 | dev_001 | fall | critical | handled | 疑似跌倒+血氧91%+心率112 | 1号楼 | 1-201 | — | — | ✗ | 2026-06-19 12:27:26 | — |
| 24 | alarm_e4295987 | elder_001 | dev_001 | health_abnormal | critical | handled | 血氧85%+心率140+体温38.5℃ | 1号楼 | 1-201 | — | — | ✗ | 2026-06-19 12:52:55 | — |
| 25 | alarm_9fa929df | elder_001 | dev_001 | health_abnormal | critical | pending | 血氧85%+心率140+体温38.5℃ | 1号楼 | 1-201 | — | — | ✗ | 2026-06-19 12:54:30 | — |
| 26-34 | *(alarm_2a9cdda4 ~ alarm_367f136d)* | elder_001 | dev_001 | fall | critical | handled/pending | 疑似跌倒+血氧心率异常 | 1号楼 | 1-201 | — | — | ✗ | 2026-06-19 | — |
| 35-39 | *(alarm_3f0dc081 ~ alarm_ae09954b)* | elder_001 | dev_001 | fall | critical | handled/pending | 疑似跌倒+血氧心率异常 | 1号楼 | 1-201 | — | — | ✗ | 2026-06-20 | — |
| 40 | alarm_6daec3a1 | elder_001 | dev_001 | fall | critical | handled | 传感器提示疑似跌倒... | 1号楼 | 1-201 | staff_001 | 社区工作人员 | ✓ | 2026-06-20 01:24:59 | 2026-06-20 01:25:11 |

> 注：第 40 条已处理，location=客厅，handle_remark="已上门确认，老人无碍"。完整列表见 `anxinban_database_full_script.txt`。

## 6. alarm_process

> 告警处理记录表 — 记录每次告警的处理过程和结果

| id | process_id | alarm_id | handler_id | handler_type | action | result | remark | process_time |
|----|------------|----------|------------|--------------|--------|--------|--------|--------------|
| 1 | proc_001 | alarm_003 | staff_001 | staff | 上门核实 | confirmed | 已确认是访客，非陌生人闯入 | 2025-05-17 08:30:00 |
| 2 | proc_002 | alarm_004 | staff_002 | staff | 上门检修 | resolved | 已更换床垫传感器电池，设备恢复在线 | 2025-05-17 08:00:00 |
| 3 | proc_003 | alarm_002 | staff_001 | staff | 紧急上门 | in_progress | 正在赶往现场，老人意识清醒 | 2025-05-17 09:20:00 |
| 4 | proc_004 | alarm_001 | staff_003 | staff | 电话确认 | pending | 已电话联系老人，老人表示无不适，继续观察 | 2025-05-17 09:25:00 |
| 5 | proc_005 | alarm_005 | staff_002 | staff | 紧急处理 | pending | 已赶往现场，烟感已停止报警，正在排查原因 | 2025-05-17 08:05:00 |

## 7. alert

> 告警表 — 存储 AI/传感器产生的各类告警（AlarmEvent 实体）

| id | alarm_id | elder_id | type | risk_level | description | device_id | building | room_number | location | status | is_read | occur_time |
|----|----------|----------|------|------------|-------------|-----------|----------|-------------|----------|--------|--------|------------|
| 1 | alarm_001 | elder_001 | fall | high | 检测到老人在卧室发生跌倒 | dev_001 | 1号楼 | 101 | 卧室 | pending | ✗ | 2026-06-20 17:45:37 |
| 2 | alarm_002 | elder_002 | heart_rate | high | 心率异常：130bpm，超出正常范围 | dev_002 | 2号楼 | 205 | 客厅 | pending | ✗ | 2026-06-20 17:45:37 |
| 3 | alarm_003 | elder_003 | blood_pressure | medium | 血压偏高：160/95mmHg | dev_003 | 3号楼 | 310 | 卧室 | pending | ✓ | 2026-06-20 17:45:37 |
| 4 | alarm_004 | elder_004 | emergency-call | high | 老人按下手表SOS紧急呼叫 | dev_004 | 4号楼 | 402 | 室外 | pending | ✗ | 2026-06-20 17:45:37 |
| 5 | alarm_005 | elder_005 | inactive | medium | 长时间无活动检测：超过6小时未检测到活动 | dev_005 | 5号楼 | 508 | 客厅 | pending | ✗ | 2026-06-20 17:45:37 |
| 6 | alarm_006 | elder_001 | smoke | high | 厨房烟雾浓度超标，疑似火灾 | dev_006 | 1号楼 | 101 | 厨房 | handled | ✓ | 2026-06-20 17:45:37 |
| 7 | alarm_007 | elder_002 | temperature | low | 体温偏高：38.2℃，可能发热 | dev_007 | 2号楼 | 205 | 卧室 | pending | ✗ | 2026-06-20 17:45:37 |
| 8 | alarm_008 | elder_003 | intrusion | high | 门锁异常：指纹验证失败超过5次 | dev_008 | 3号楼 | 310 | 门口 | pending | ✗ | 2026-06-20 17:45:37 |

## 8. app_notification

> APP 通知表 — 存储推送给家属 APP 的消息通知

*（当前无数据）*

## 9. blood_pressure

> 血压记录表 — 存储老人血压测量数据

| id | bp_id | elder_id | systolic | diastolic | timestamp | created_at |
|----|-------|----------|----------|-----------|-----------|------------|
| 1 | bp_001 | elder_001 | 135 | 85 | 2025-05-17 08:30:00 | 2025-05-17 08:31:00 |
| 2 | bp_002 | elder_001 | 132 | 84 | 2025-05-16 08:30:00 | 2025-05-16 08:31:00 |
| 3 | bp_003 | elder_002 | 145 | 90 | 2025-05-17 07:00:00 | 2025-05-17 07:01:00 |
| 4 | bp_004 | elder_002 | 148 | 92 | 2025-05-16 07:00:00 | 2025-05-16 07:01:00 |
| 5 | bp_005 | elder_003 | 128 | 82 | 2025-05-17 08:00:00 | 2025-05-17 08:01:00 |
| 6 | bp_006 | elder_003 | 125 | 80 | 2025-05-16 08:00:00 | 2025-05-16 08:01:00 |
| 7 | bp_007 | elder_004 | 138 | 88 | 2025-05-17 07:30:00 | 2025-05-17 07:31:00 |
| 8 | bp_008 | elder_005 | 150 | 95 | 2025-05-16 22:00:00 | 2025-05-16 22:01:00 |
| 9 | bp_009 | elder_004 | 140 | 86 | 2025-05-16 07:30:00 | 2025-05-16 07:31:00 |

## 10. camera_request

> 摄像头请求表 — 家属发起查看老人摄像头的授权请求

*（当前无数据）*

## 11. camera_view_record

> 监控查看记录表 — 记录家属每次实际查看监控的审计日志

*（当前无数据）*

## 12. cloud_agent

> 云端 Agent 配置表 — 存储云端 AI Agent 的配置信息

| id | agent_id | agent_type | status | last_heartbeat | ip | device_count | connected_devices | created_at |
|----|----------|------------|--------|----------------|------|-------------|-------------------|------------|
| 1 | CLOUD-01 | cloud_agent | online | 2025-05-17 09:55:00 | 10.0.0.1 | 17 | 14 | 2025-01-05 08:00:00 |
| 2 | CLOUD-02 | cloud_agent | online | 2025-05-17 09:50:00 | 10.0.0.2 | 0 | 0 | 2025-01-05 08:00:00 |
| 3 | CLOUD-03 | cloud_agent | offline | 2025-05-16 18:00:00 | 10.0.0.3 | 5 | 0 | 2025-02-01 08:00:00 |

## 13. companion_record

> 陪伴交互记录表 — 存储 AI 陪伴机器人与老人的对话记录

*（当前无数据）*

## 14. device

> 设备表 — 存储老人家中或社区的智能硬件设备信息（17 台设备）

| id | device_id | elder_id | device_type | device_name | location | building | room | status | battery_level | last_online_time |
|----|-----------|----------|-------------|-------------|----------|----------|------|--------|---------------|------------------|
| 1 | dev_001 | elder_001 | 手环 | 智能手环 | 随身佩戴 | 1号楼 | 1-201 | online | 78 | 2025-05-17 09:45:00 |
| 2 | dev_002 | elder_001 | 摄像头 | 客厅摄像头 | 客厅 | 1号楼 | 1-201 | online | 0 | 2025-05-17 09:00:00 |
| 3 | dev_003 | elder_001 | 摄像头 | 门口摄像头 | 门口 | 1号楼 | 1-201 | online | 0 | 2025-05-17 08:30:00 |
| 4 | dev_004 | elder_002 | 手环 | 智能手环 | 随身佩戴 | 2号楼 | 2-301 | online | 62 | 2025-05-17 09:50:00 |
| 5 | dev_005 | elder_002 | 摄像头 | 客厅摄像头 | 客厅 | 2号楼 | 2-301 | online | 0 | 2025-05-17 09:15:00 |
| 6 | dev_006 | elder_002 | 烟感 | 厨房烟感 | 厨房 | 2号楼 | 2-301 | online | 0 | 2025-05-17 09:00:00 |
| 7 | dev_007 | elder_003 | 手环 | 智能手环 | 随身佩戴 | 3号楼 | 3-401 | online | 45 | 2025-05-17 08:30:00 |
| 8 | dev_008 | elder_003 | 摄像头 | 客厅摄像头 | 客厅 | 3号楼 | 3-401 | online | 0 | 2025-05-17 09:15:00 |
| 9 | dev_009 | elder_003 | 床垫传感器 | 智能床垫 | 卧室 | 3号楼 | 3-401 | offline | 0 | 2025-05-15 22:00:00 |
| 10 | dev_010 | elder_004 | 手环 | 智能手环 | 随身佩戴 | 4号楼 | 4-102 | online | 55 | 2025-05-17 08:00:00 |
| 11 | dev_011 | elder_004 | 摄像头 | 卧室摄像头 | 卧室 | 4号楼 | 4-102 | online | 0 | 2025-05-17 07:30:00 |
| 12 | dev_012 | elder_005 | 手环 | 智能手环 | 随身佩戴 | 5号楼 | 5-502 | online | 30 | 2025-05-16 22:00:00 |
| 13 | dev_013 | elder_001 | 门锁 | 智能指纹门锁 | 大门 | 1号楼 | 1-201 | online | 85 | 2025-05-17 09:30:00 |
| 14 | dev_014 | elder_002 | 窗帘 | 智能窗帘电机 | 卧室 | 2号楼 | 2-301 | online | 0 | 2025-05-17 09:00:00 |
| 15 | dev_015 | elder_001 | 灯光 | 智能吸顶灯 | 客厅 | 1号楼 | 1-201 | online | 0 | 2025-05-17 09:00:00 |
| 16 | dev_016 | elder_004 | 蜂鸣器 | 紧急报警蜂鸣器 | 客厅 | 4号楼 | 4-102 | online | 0 | 2025-05-17 08:00:00 |
| 17 | dev_017 | elder_005 | 烟感 | 客厅烟感 | 客厅 | 5号楼 | 5-502 | online | 0 | 2025-05-16 22:00:00 |

## 15. elder_user

> 老人档案表 — 核心实体，存储老人基本信息、健康状态

| id | elder_id | name | age | gender | address | building | room_number | phone | password | health_status | health_status_text | health_note | family_phone | community_id | has_camera | camera_pending | last_online |
|----|----------|------|-----|--------|---------|----------|-------------|-------|----------|---------------|-------------------|-------------|--------------|--------------|------------|----------------|-------------|
| 1 | elder_001 | 张三 | 78 | 男 | 北京市朝阳区建国路88号 | 1号楼 | 1-201 | 13912345601 | 123456 | warning | 关注 | 高血压,糖尿病 | 13800138002 | community_001 | ✓ | ✗ | 2025-05-17 09:00:00 |
| 2 | elder_002 | 李四 | 82 | 女 | 北京市海淀区中关村大街1号 | 2号楼 | 2-301 | 13912345602 | 123456 | danger | 高危 | 心脏病,骨质疏松 | 13900139002 | community_001 | ✓ | ✓ | 2025-05-17 08:30:00 |
| 3 | elder_003 | 王五 | 75 | 男 | 北京市西城区金融街2号 | 3号楼 | 3-401 | 13912345603 | 123456 | normal | 正常 | 轻度认知障碍 | 13700137002 | community_001 | ✓ | ✗ | 2025-05-17 08:30:00 |
| 4 | elder_004 | 赵六 | 80 | 女 | 北京市东城区东直门外大街10号 | 4号楼 | 4-102 | 13912345604 | 123456 | warning | 关注 | 糖尿病,关节炎 | 13600136002 | community_001 | ✓ | ✗ | 2025-05-17 07:00:00 |
| 5 | elder_005 | 孙七 | 76 | 男 | 北京市丰台区南三环中路12号 | 5号楼 | 5-502 | 13912345605 | 123456 | danger | 高危 | 脑梗后遗症,高血压 | 13500135002 | community_001 | ✗ | ✓ | 2025-05-16 22:00:00 |
| 6 | 100 | 默认老人 | 80 | 男 | 北京市朝阳区默认地址 | 1号楼 | 1-001 | 13900000000 | 123456 | normal | 正常 | 无 | 13800000000 | community_001 | ✓ | ✗ | 2025-01-01 08:00:00 |

## 16. elderly

> 老人信息表（旧版/备用）

*（当前无数据，新版使用 elder_user 表）*

## 17. emergency_contact

> 紧急联系人表 — 存储老人的紧急联系人信息

| id | contact_id | elder_id | name | phone | relation | is_primary | sort_order | created_at |
|----|------------|----------|------|-------|----------|------------|------------|------------|
| 1 | contact_001 | elder_001 | 张小明 | 13800138011 | 儿子 | ✓ | 0 | 2025-01-10 09:00:00 |
| 2 | contact_002 | elder_001 | 张小红 | 13800138012 | 女儿 | ✗ | 1 | 2025-01-10 09:00:00 |
| 3 | contact_003 | elder_002 | 李小红 | 13900139012 | 女儿 | ✓ | 0 | 2025-01-15 10:30:00 |
| 4 | contact_004 | elder_002 | 李小刚 | 13900139013 | 儿子 | ✗ | 1 | 2025-01-15 10:30:00 |
| 5 | contact_005 | elder_003 | 王小刚 | 13700137013 | 侄子 | ✓ | 0 | 2025-02-01 14:00:00 |
| 6 | contact_006 | elder_004 | 赵小花 | 13600136014 | 女儿 | ✓ | 0 | 2025-03-01 09:00:00 |
| 7 | contact_007 | elder_005 | 孙小强 | 13500135015 | 孙子 | ✓ | 0 | 2025-03-15 10:00:00 |

## 18. family_request

> 家属服务申请表 — 存储家属通过 APP 提交的服务申请

*（当前无数据）*

## 19. family_user

> 家属用户表 — 存储家属 APP 端用户账号信息

| id | family_id | name | phone | password | elder_id | relation | avatar | created_at |
|----|-----------|------|-------|----------|----------|----------|--------|------------|
| 1 | family_001 | 张小明 | 13800138011 | 123456 | elder_001 | 儿子 | /uploads/avatar/family_001.jpg | 2025-01-10 09:00:00 |
| 2 | family_002 | 李小红 | 13900139012 | 123456 | elder_002 | 女儿 | /uploads/avatar/family_002.jpg | 2025-01-15 10:30:00 |
| 3 | family_003 | 王小刚 | 13700137013 | 123456 | elder_003 | 侄子 | /uploads/avatar/family_003.jpg | 2025-02-01 14:00:00 |
| 4 | family_004 | 赵小花 | 13600136014 | 123456 | elder_004 | 女儿 | /uploads/avatar/family_004.jpg | 2025-03-01 09:00:00 |
| 5 | family_005 | 孙小强 | 13500135015 | 123456 | elder_005 | 孙子 | /uploads/avatar/family_005.jpg | 2025-03-15 10:00:00 |

## 20. health_record

> 健康记录表 — 存储老人健康档案和病史记录

| id | record_id | elder_id | blood_type | medical_history | allergy_history | common_medications | hospitalization_info | remarks |
|----|-----------|----------|------------|-----------------|-----------------|--------------------|-----------------------|---------|
| 1 | hr_001 | elder_001 | A | 高血压10年,糖尿病5年 | 对青霉素过敏 | 降压药,降糖药,阿司匹林 | 2024年3月因高血压住院7天 | 需定期监测血糖血压 |
| 2 | hr_002 | elder_002 | B | 心脏病3年,骨质疏松 | 无 | 硝酸甘油,钙片 | 2024年8月因心脏病住院15天 | 需注意跌倒风险 |
| 3 | hr_003 | elder_003 | O | 轻度认知障碍 | 对花粉过敏 | 银杏叶片 | 无住院记录 | 记忆力减退需关注 |
| 4 | hr_004 | elder_004 | AB | 糖尿病8年,关节炎 | 无 | 胰岛素,止痛药 | 2025年1月因糖尿病足住院5天 | 行动不便需辅助 |
| 5 | hr_005 | elder_005 | A | 脑梗后遗症,高血压 | 对磺胺类药物过敏 | 降压药,抗凝药 | 2024年12月因脑梗住院30天 | 需定期康复训练 |

## 21. health_vital_record

> 健康体征记录表 — 存储老人日常体征数据

*（当前无数据）*

## 22. home_control_log

> 家居控制日志表 — 记录智能家居设备的控制操作日志

| id | control_id | elder_id | device_id | command | source_agent | result | created_at |
|----|------------|----------|-----------|---------|--------------|--------|------------|
| 1 | ctrl_001 | elder_001 | dev_002 | alarmMute | LOCAL-1001 | success | 2025-05-16 20:00:00 |
| 2 | ctrl_002 | elder_002 | dev_005 | startRecord | CLOUD-01 | success | 2025-05-15 09:00:00 |
| 3 | ctrl_003 | elder_003 | dev_008 | enableMotionDetect | LOCAL-1003 | success | 2025-05-17 08:30:00 |
| 4 | ctrl_004 | elder_001 | dev_015 | turnOn | CLOUD-01 | success | 2025-05-17 09:00:00 |
| 5 | ctrl_005 | elder_002 | dev_014 | close | LOCAL-1002 | success | 2025-05-17 08:00:00 |
| 6 | ctrl_006 | elder_004 | dev_016 | beep | CLOUD-01 | success | 2025-05-17 07:30:00 |

## 23. local_agent

> 本地 Agent 配置表 — 存储本地端 AI Agent 的配置信息

| id | agent_id | agent_type | status | last_heartbeat | ip | device_count | connected_devices | created_at |
|----|----------|------------|--------|----------------|------|-------------|-------------------|------------|
| 1 | LOCAL-1001 | local_gateway | online | 2025-05-17 09:50:00 | 192.168.1.101 | 3 | 3 | 2025-01-10 09:00:00 |
| 2 | LOCAL-1002 | local_gateway | online | 2025-05-17 09:48:00 | 192.168.1.102 | 3 | 2 | 2025-01-15 10:30:00 |
| 3 | LOCAL-1003 | local_gateway | offline | 2025-05-15 22:00:00 | 192.168.1.103 | 2 | 0 | 2025-02-01 14:00:00 |
| 4 | LOCAL-1004 | local_gateway | online | 2025-05-17 09:55:00 | 192.168.1.104 | 3 | 3 | 2025-03-01 09:00:00 |
| 5 | LOCAL-1005 | local_gateway | online | 2025-05-17 09:40:00 | 192.168.1.105 | 2 | 2 | 2025-03-15 10:00:00 |

## 24. monitor_request

> 监控请求记录表 — 记录家属发起的监控查看请求历史

| id | request_id | elder_id | staff_id | staff_name | staff_phone | reason | status | approved_at | expired_at | created_at |
|----|------------|----------|----------|------------|-------------|--------|--------|-------------|------------|------------|
| 1 | mr_001 | elder_001 | staff_001 | 张建国 | 13800138001 | 日常巡查需要查看老人状态 | approved | 1715904000000 | 2025-06-01 | 2025-05-10 09:00:00 |
| 2 | mr_002 | elder_002 | staff_002 | 李秀英 | 13800138002 | 老人摔倒后需持续观察 | approved | 1715904000000 | 2025-06-15 | 2025-05-15 10:00:00 |
| 3 | mr_003 | elder_003 | staff_003 | 王强 | 13800138003 | 设备维护期间需查看监控 | pending | 0 | — | 2025-05-17 08:00:00 |
| 4 | mr_004 | elder_004 | staff_001 | 张建国 | 13800138001 | 新入住老人需要了解情况 | approved | 1715904000000 | 2025-05-31 | 2025-05-01 09:00:00 |
| 5 | mr_005 | elder_005 | staff_002 | 李秀英 | 13800138002 | 紧急呼叫后需查看现场 | approved | 1715904000000 | 2025-05-18 | 2025-05-16 23:00:00 |

## 25. music_intervention

> 音乐干预表 — 存储音乐疗法的干预方案和记录

| id | intervention_id | elder_id | trigger_reason | music_type | start_time | duration_minutes | before_state | after_state | result |
|----|-----------------|----------|----------------|------------|------------|-----------------|-------------|-------------|--------|
| 1 | int_001 | elder_001 | 情绪识别-焦虑 | music | 2025-05-16 15:00:00 | 15 | 焦虑不安、心率加快 | 平静放松、心率恢复正常 | completed |
| 2 | int_002 | elder_002 | 夜间唤醒 | music | 2025-05-15 02:30:00 | 10 | 睡眠中断、辗转反侧 | 重新入睡、呼吸平稳 | completed |
| 3 | int_003 | elder_003 | 久坐提醒 | music | 2025-05-17 10:00:00 | 5 | 久坐不动超过2小时 | 起身活动、舒展身体 | completed |
| 4 | int_004 | elder_002 | 心率异常告警联动 | music | 2025-05-17 09:30:00 | 0 | 心率92bpm | — | pending |
| 5 | int_005 | elder_004 | 情绪安抚 | music | 2025-05-17 08:00:00 | 20 | 情绪低落、不愿交流 | 心情好转、愿意配合 | completed |

## 26. notification

> 通知记录表 — 存储系统通知的通用记录

| id | notification_id | user_id | user_type | notification_type | title | content | is_read | building | room | elder_id | related_id | created_at |
|----|-----------------|---------|-----------|-------------------|-------|---------|---------|----------|------|----------|------------|------------|
| 1 | notif_001 | staff_001 | staff | alarm | 新告警通知 | 1号楼1-201张三心率持续偏高 | ✗ | 1号楼 | 1-201 | elder_001 | alarm_001 | 2025-05-17 09:20:00 |
| 2 | notif_002 | staff_001 | staff | alarm | 紧急告警 | 2号楼2-301李四检测到跌倒 | ✗ | 2号楼 | 2-301 | elder_002 | alarm_002 | 2025-05-17 09:15:00 |
| 3 | notif_003 | family_001 | family | alarm | 告警通知 | 您的家属张三家中检测到陌生人闯入 | ✓ | 1号楼 | 1-201 | elder_001 | alarm_003 | 2025-05-16 20:00:00 |
| 4 | notif_004 | staff_002 | staff | device | 设备离线 | 3号楼3-401床垫传感器离线 | ✓ | 3号楼 | 3-401 | elder_003 | dev_009 | 2025-05-16 10:00:00 |
| 5 | notif_005 | family_005 | family | emergency | 紧急呼叫 | 您的家属孙七触发紧急呼叫 | ✗ | 5号楼 | 5-502 | elder_005 | sos_002 | 2025-05-16 23:00:00 |
| 6 | notif_006 | staff_003 | staff | alarm | 摔倒检测 | 4号楼4-102赵六卧室疑似摔倒 | ✗ | 4号楼 | 4-102 | elder_004 | alarm_007 | 2025-05-17 07:00:00 |
| 7 | notif_007 | family_002 | family | service | 服务请求处理 | 您的服务请求已处理完成 | ✓ | 2号楼 | 2-301 | elder_002 | — | 2025-05-17 09:30:00 |
| 8 | notif_008 | staff_001 | staff | monitor | 监控授权申请 | 新入住老人赵六监控查看申请 | ✓ | 4号楼 | 4-102 | elder_004 | mr_004 | 2025-05-01 09:00:00 |

## 27. sensor_data

> 传感器数据表 — 存储各类 IoT 传感器上报的原始数据（163 条记录）

### 初始种子数据（id 1-31，2025年5月）

| id | elder_id | device_id | sensor_type | value | unit | is_abnormal | timestamp |
|----|----------|-----------|-------------|-------|------|-------------|-----------|
| 1 | elder_001 | dev_001 | heart_rate | 72 | bpm | ✗ | 2025-05-17 08:00:00 |
| 2 | elder_001 | dev_001 | heart_rate | 85 | bpm | ✗ | 2025-05-17 09:00:00 |
| 3 | elder_001 | dev_001 | heart_rate | 95 | bpm | ✓ | 2025-05-17 09:30:00 |
| 4 | elder_001 | dev_001 | temperature | 36.5 | ℃ | ✗ | 2025-05-17 09:00:00 |
| 5 | elder_001 | dev_001 | blood_oxygen | 98 | % | ✗ | 2025-05-17 09:00:00 |
| 6 | elder_001 | dev_001 | insomnia | 0 | 等级 | ✗ | 2025-05-17 09:00:00 |
| 7 | elder_001 | dev_001 | sleep_time | 22.5 | 小时 | ✗ | 2025-05-17 09:00:00 |
| 8 | elder_002 | dev_004 | heart_rate | 68 | bpm | ✗ | 2025-05-17 07:30:00 |
| 9 | elder_002 | dev_004 | heart_rate | 92 | bpm | ✓ | 2025-05-17 09:20:00 |
| 10 | elder_002 | dev_004 | temperature | 36.8 | ℃ | ✗ | 2025-05-17 09:20:00 |
| 11 | elder_002 | dev_004 | blood_oxygen | 96 | % | ✗ | 2025-05-17 09:20:00 |
| 12 | elder_002 | dev_004 | insomnia | 1 | 等级 | ✗ | 2025-05-17 09:00:00 |
| 13 | elder_002 | dev_004 | sleep_time | 23.25 | 小时 | ✗ | 2025-05-17 09:00:00 |
| 14 | elder_003 | dev_007 | heart_rate | 70 | bpm | ✗ | 2025-05-17 08:00:00 |
| 15 | elder_003 | dev_007 | temperature | 36.4 | ℃ | ✗ | 2025-05-17 08:00:00 |
| 16 | elder_003 | dev_007 | blood_oxygen | 97 | % | ✗ | 2025-05-17 08:00:00 |
| 17 | elder_003 | dev_007 | insomnia | 2 | 等级 | ✗ | 2025-05-17 08:00:00 |
| 18 | elder_003 | dev_007 | sleep_time | 21.75 | 小时 | ✗ | 2025-05-17 08:00:00 |
| 19 | elder_001 | dev_001 | temperature | 24.5 | ℃ | ✗ | 2025-05-17 10:00:00 |
| 20 | elder_001 | dev_001 | humidity | 55 | % | ✗ | 2025-05-17 10:00:00 |
| 21 | elder_001 | dev_001 | body_temperature | 36.2 | ℃ | ✗ | 2025-05-17 10:00:00 |
| 22 | elder_002 | dev_004 | temperature | 23.8 | ℃ | ✗ | 2025-05-17 10:00:00 |
| 23 | elder_002 | dev_004 | humidity | 60 | % | ✗ | 2025-05-17 10:00:00 |
| 24 | elder_003 | dev_007 | temperature | 25 | ℃ | ✗ | 2025-05-17 10:00:00 |
| 25 | elder_003 | dev_007 | humidity | 52 | % | ✗ | 2025-05-17 10:00:00 |
| 26 | elder_004 | dev_010 | heart_rate | 65 | bpm | ✗ | 2025-05-17 08:00:00 |
| 27 | elder_004 | dev_010 | blood_oxygen | 99 | % | ✗ | 2025-05-17 08:00:00 |
| 28 | elder_004 | dev_010 | temperature | 23.5 | ℃ | ✗ | 2025-05-17 08:00:00 |
| 29 | elder_005 | dev_012 | heart_rate | 110 | bpm | ✓ | 2025-05-16 23:00:00 |
| 30 | elder_005 | dev_012 | blood_oxygen | 94 | % | ✓ | 2025-05-16 23:00:00 |
| 31 | elder_005 | dev_012 | temperature | 22 | ℃ | ✗ | 2025-05-16 23:00:00 |

### 模拟/测试数据摘要（id 32-163，2026年6月）

所有后续记录均来自 `elder_001/dev_001` 和少量 `elder_002/dev_004`，数据模式如下：

| 时间段 | 心率范围 | 血氧范围 | 体温范围 | 记录数 |
|--------|---------|---------|---------|--------|
| 2026-06-18 | 112 bpm | 91% | 37.4℃ | ~18 条 |
| 2026-06-19 凌晨 | 112 bpm | 91% | 37.4℃ | ~20 条 |
| 2026-06-19 上午(异常) | 125 bpm | 88% | 38.5℃ | ~9 条 |
| 2026-06-19 上午(恢复) | 112 bpm | 91% | 37.4℃ | ~6 条 |
| 2026-06-19 中午(危急) | 140 bpm | 85% | 38.5℃ | ~6 条 |
| 2026-06-19 下午 | 112 bpm | 91% | 37.4℃ | ~30 条 |
| 2026-06-20 | 112 bpm | 91% | 37.4℃ | ~35 条 |

其中 id=157,158,162,163 包含 `activity_status=3` 和 `fall_status=2`（触发跌倒告警的关键数据）。

> 完整 163 条数据见 `anxinban_database_full_script.txt`。

## 28. service_request

> 服务请求表 — 存储用户提交的各类服务请求记录

| id | request_id | family_id | elder_id | request_type | content | status | related_order_id | created_at |
|----|------------|-----------|----------|-------------|---------|--------|-----------------|------------|
| 1 | sr_001 | family_001 | elder_001 | 设备维修 | 请求安排人员上门检查客厅摄像头 | completed | wo_001 | 2025-05-16 09:00:00 |
| 2 | sr_002 | family_002 | elder_002 | 紧急协助 | 收到摔倒告警，请求工作人员上门确认 | completed | wo_002 | 2025-05-17 09:10:00 |
| 3 | sr_003 | family_003 | elder_003 | 设备维修 | 床垫传感器离线，请求恢复 | completed | wo_003 | 2025-05-16 10:00:00 |
| 4 | sr_004 | family_005 | elder_005 | 紧急协助 | 收到紧急呼叫，请求立即上门 | pending | wo_004 | 2025-05-16 23:00:00 |
| 5 | sr_005 | family_004 | elder_004 | 日常关怀 | 老人今天情绪低落，请求工作人员关怀 | in_progress | wo_005 | 2025-05-17 08:00:00 |

## 29. sleep_record

> 睡眠数据表 — 存储老人睡眠监测数据

*（当前无数据）*

## 30. sos_record

> SOS 呼救记录表 — 存储老人 SOS 紧急呼救的触发和处理记录

| id | sos_id | elder_id | trigger_time | status | location | handler_id | handled_time | created_at |
|----|--------|----------|-------------|--------|----------|------------|--------------|------------|
| 1 | sos_001 | elder_002 | 2025-05-17 09:15:00 | handled | 2号楼2-301客厅 | staff_001 | 2025-05-17 09:20:00 | 2025-05-17 09:15:00 |
| 2 | sos_002 | elder_005 | 2025-05-16 23:00:00 | pending | 5号楼5-502卧室 | — | — | 2025-05-16 23:00:00 |
| 3 | sos_003 | elder_001 | 2025-05-16 14:00:00 | handled | 1号楼1-201厨房 | staff_002 | 2025-05-16 14:10:00 | 2025-05-16 14:00:00 |
| 4 | sos_004 | elder_004 | 2025-05-17 06:00:00 | handled | 4号楼4-102卫生间 | staff_003 | 2025-05-17 06:15:00 | 2025-05-17 06:00:00 |
| 5 | sos_005 | elder_003 | 2025-05-15 20:00:00 | handled | 3号楼3-401客厅 | staff_002 | 2025-05-15 20:05:00 | 2025-05-15 20:00:00 |

## 31. staff

> 员工表（旧版/备用）

*（当前无数据，新版使用 staff_user 表）*

## 32. staff_user

> 工作人员表 — 存储社区管理人员（Web 端）账号信息

| id | staff_id | username | name | phone | password | role | community_id | avatar | created_at |
|----|----------|----------|------|-------|----------|------|--------------|--------|------------|
| 1 | staff_001 | zhangjianguo | 张建国 | 13800138001 | 123456 | supervisor | community_001 | /uploads/avatar/staff_001.jpg | 2025-01-05 08:00:00 |
| 2 | staff_002 | lixiuying | 李秀英 | 13800138002 | 123456 | staff | community_001 | /uploads/avatar/staff_002.jpg | 2025-01-08 09:00:00 |
| 3 | staff_003 | wangqiang | 王强 | 13800138003 | 123456 | staff | community_001 | /uploads/avatar/staff_003.jpg | 2025-01-10 10:00:00 |
| 4 | staff_004 | admin | 系统管理员 | 13800138000 | 123456 | admin | community_001 | /uploads/avatar/admin.jpg | 2025-01-01 08:00:00 |
| 5 | STF-f65acafd | 13900001111 | 李新成 | 13900001111 | 123456 | staff | — | — | 2026-06-20 07:55:53 |

## 33. vlm_record

> VLM 找物品记录表 — 存储视觉大模型（VLM）找物品的交互记录

*（当前无数据）*

## 34. voice_prompt

> 语音/音乐疗法提醒表 — 存储语音提示和音乐疗法的定时提醒

*（当前无数据）*

## 35. work_order

> 工单表 — 存储社区工作人员处理的服务工单（27 条记录）

### 初始工单（id 1-5，种子数据）

| id | order_id | elder_id | order_type | description | status | creator_id | handler_id | handler_name | complete_time | service_request_id |
|----|----------|----------|------------|-------------|--------|------------|------------|-------------|---------------|-------------------|
| 1 | wo_001 | elder_001 | 设备维修 | 客厅摄像头画面模糊，需要检修 | completed | staff_001 | staff_003 | 李护士 | 2025-05-16 16:00:00 | alarm_006 |
| 2 | wo_002 | elder_002 | 紧急处理 | 老人摔倒，需上门检查 | completed | staff_001 | staff_001 | 张建国 | 2025-05-17 09:30:00 | sr_002 |
| 3 | wo_003 | elder_003 | 设备巡检 | 床垫传感器离线，需上门恢复 | completed | staff_002 | staff_002 | 王师傅 | 2025-05-17 08:00:00 | sr_003 |
| 4 | wo_004 | elder_005 | 紧急处理 | 手表紧急呼叫未处理，需上门查看 | pending | staff_001 | — | — | — | sr_004 |
| 5 | wo_005 | elder_004 | 日常关怀 | 老人情绪低落，需心理疏导 | in_progress | staff_003 | staff_003 | 李护士 | — | sr_005 |

### 系统自动生成工单（id 6-27，2026年6月）

| id | order_id | elder_id | type | status | handler_id | handler_name | created_at | alarm_id |
|----|----------|----------|------|--------|------------|-------------|------------|----------|
| 6 | wo_6720ee14 | elder_001 | 紧急巡检 | 待处理 | — | — | 2026-06-18 13:19:10 | alarm_49ea4bef |
| 7 | wo_626abacd | elder_001 | 设备检查 | pending | — | — | 2026-06-18 13:20:35 | — |
| 8-12 | *(wo_3645eeb6 ~ wo_4a2d5964)* | elder_001 | 紧急巡检 | 待处理 | — | — | 2026-06-18~19 | 对应 alarm |
| 13-15 | *(wo_542bd8a9 ~ wo_3e5cd0df)* | elder_001 | 紧急巡检 | 待处理 | — | — | 2026-06-19 | 血氧88%+心率125+体温38.5 告警 |
| 16 | wo_366d573b | elder_001 | 紧急巡检 | 待处理 | — | — | 2026-06-19 11:39:56 | alarm_2fcab5b6 |
| 17 | wo_1da15558 | elder_001 | 紧急巡检 | 待处理 | — | — | 2026-06-19 12:28:30 | alarm_7d2e5a9e |
| 18 | wo_d888f1ce | elder_001 | 紧急巡检 | 待处理 | — | — | 2026-06-19 12:53:27 | alarm_e4295987 |
| 19 | wo_5965d5de | elder_001 | 紧急巡检 | 待处理 | — | — | 2026-06-19 12:54:20 | alarm_2ad038bb |
| 20-25 | *(wo_ab8aac4b ~ wo_25b4c14b)* | elder_001 | 紧急巡检 | 待处理 | — | — | 2026-06-19~20 | 对应 alarm |
| 26 | wo_4e040b27 | elder_001 | 紧急巡检 | **已完成** | staff_001 | 李工作人员 | 2026-06-20 01:25:13 | alarm_04629189 |
| 27 | wo_5ad39d29 | elder_001 | 健康关注 | **已完成** | — | test | 2026-06-20 01:38:40 | — |

---

## 数据统计

| 指标 | 数值 |
|------|------|
| 数据库表总数 | 35 |
| 有数据的表 | 21 |
| 空表 | 14 |
| 老人数量 | 6（elder_001 ~ elder_005 + 100） |
| 家属数量 | 5 |
| 工作人员数量 | 5 |
| 设备数量 | 17 |
| 传感器数据条数 | 163 |
| 告警事件数 | 40（alarm_event）+ 8（alert） |
| AI 分析记录数 | 33 |
| 工单数 | 27 |
| SOS 呼救数 | 5 |

> **完整原始数据请参阅：** `anxinban_database_full_script.txt`
