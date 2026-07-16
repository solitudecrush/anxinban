#!/usr/bin/env python3
"""
Generate sql_table.md with ALL data from anxinban MySQL database.
Exports every row from every table (no sampling).
"""

import os
import pymysql
import datetime

DB_CONFIG = {
    'host': os.environ.get('DB_HOST', 'localhost'),
    'user': os.environ.get('DB_USERNAME', 'root'),
    'password': os.environ.get('DB_PASSWORD', ''),
    'database': os.environ.get('DB_NAME', 'anxinban'),
    'charset': 'utf8mb4',
}

OUTPUT_FILE = '/var/www/anxinban-backend/sql_table.md'

# Table descriptions (order as in current sql_table.md)
TABLE_ORDER = [
    'agent_conversation', 'agent_intent_log', 'ai_advice', 'ai_analysis_record',
    'alarm_event', 'alarm_process', 'alert', 'app_notification',
    'blood_oxygen', 'blood_pressure', 'body_temperature',
    'camera_request', 'camera_view_record', 'cloud_agent', 'companion_record',
    'device', 'elder_user', 'elderly', 'emergency_contact',
    'family_request', 'family_user',
    'health_record', 'health_vital_record', 'heart_rate',
    'home_control_log', 'local_agent',
    'monitor_request', 'music_intervention', 'notification',
    'sensor_data', 'service_request', 'sleep_record', 'sos_record',
    'staff', 'staff_user', 'vlm_record', 'voice_prompt', 'work_order',
]

TABLE_INFO = {
    'agent_conversation': {
        'cn': 'Agent 会话记录表 — 存储用户与 AI Agent 的对话历史',
        'comment': '智能体对话表',
    },
    'agent_intent_log': {
        'cn': 'Agent 意图日志表 — 记录 AI Agent 每次意图识别的详细日志',
        'comment': '意图识别日志表',
    },
    'ai_advice': {
        'cn': 'AI 建议表 — 存储 AI 生成的健康建议内容',
        'comment': 'AI建议表',
    },
    'ai_analysis_record': {
        'cn': 'AI 分析记录表 — 保存每次 AI 健康分析的完整结果',
        'comment': 'AI分析记录表',
    },
    'alarm_event': {
        'cn': '告警事件表 — 存储系统产生的告警事件',
        'comment': '告警事件表',
    },
    'alarm_process': {
        'cn': '告警处理记录表 — 记录每次告警的处理过程和结果',
        'comment': '告警处理表',
    },
    'alert': {
        'cn': '告警表（Alert 实体）— 存储 AI/传感器产生的各类告警记录',
        'comment': '告警表',
    },
    'app_notification': {
        'cn': 'APP 通知表 — 存储推送给家属 APP 的消息通知',
        'comment': 'APP通知表',
    },
    'blood_oxygen': {
        'cn': '血氧记录表 — 存储老人血氧饱和度测量数据',
        'comment': '血氧记录表',
    },
    'blood_pressure': {
        'cn': '血压记录表 — 存储老人血压测量数据',
        'comment': '血压记录表',
    },
    'body_temperature': {
        'cn': '体温记录表 — 存储老人体温测量数据',
        'comment': '体温记录表',
    },
    'camera_request': {
        'cn': '摄像头请求表 — 家属/工作人员申请查看老人摄像头的授权请求',
        'comment': '摄像头查看申请表',
    },
    'camera_view_record': {
        'cn': '监控查看记录表 — 记录每次实际查看监控的审计日志',
        'comment': '监控查看记录表',
    },
    'cloud_agent': {
        'cn': '云端 Agent 配置表 — 存储云端 AI Agent 的配置信息',
        'comment': '云端智能体表',
    },
    'companion_record': {
        'cn': '陪伴交互记录表 — 存储 AI 陪伴机器人与老人的对话记录',
        'comment': '陪伴记录表',
    },
    'device': {
        'cn': '设备表 — 存储智能硬件设备信息',
        'comment': '设备表',
    },
    'elder_user': {
        'cn': '老人档案表 — 核心实体，存储老人基本信息、健康状态',
        'comment': '老人档案表',
    },
    'elderly': {
        'cn': '老人信息表（旧版/备用）',
        'comment': '',
    },
    'emergency_contact': {
        'cn': '紧急联系人表 — 存储老人的紧急联系人信息',
        'comment': '紧急联系人表',
    },
    'family_request': {
        'cn': '家属服务申请表 — 存储家属通过 APP 提交的服务申请',
        'comment': '',
    },
    'family_user': {
        'cn': '家属用户表 — 存储家属 APP 端用户账号信息',
        'comment': '家属用户表',
    },
    'health_record': {
        'cn': '健康记录表 — 存储老人健康档案和病史记录',
        'comment': '',
    },
    'health_vital_record': {
        'cn': '健康体征记录表 — 存储老人日常体征综合数据',
        'comment': '',
    },
    'heart_rate': {
        'cn': '心率记录表 — 存储老人心率测量数据',
        'comment': '心率记录表',
    },
    'home_control_log': {
        'cn': '家居控制日志表 — 记录智能家居设备的控制操作日志',
        'comment': '家居控制日志表',
    },
    'local_agent': {
        'cn': '本地 Agent 配置表 — 存储本地端 AI Agent 的配置信息',
        'comment': '本地智能体表',
    },
    'monitor_request': {
        'cn': '监控请求记录表 — 记录工作人员发起的监控查看请求',
        'comment': '监控查看申请表',
    },
    'music_intervention': {
        'cn': '音乐干预表 — 存储音乐疗法的干预方案和记录',
        'comment': '音乐干预表',
    },
    'notification': {
        'cn': '通知记录表 — 存储系统通知的通用记录',
        'comment': '通知表',
    },
    'sensor_data': {
        'cn': '传感器数据表 — 存储各类 IoT 传感器上报的原始数据',
        'comment': '传感器数据表',
    },
    'service_request': {
        'cn': '服务请求表 — 存储用户提交的各类服务请求记录',
        'comment': '服务请求表',
    },
    'sleep_record': {
        'cn': '睡眠数据表 — 存储老人睡眠监测数据',
        'comment': '',
    },
    'sos_record': {
        'cn': 'SOS 呼救记录表 — 存储老人 SOS 紧急呼救的触发和处理记录',
        'comment': 'SOS呼救记录表',
    },
    'staff': {
        'cn': '员工表（旧版/备用）',
        'comment': '',
    },
    'staff_user': {
        'cn': '工作人员表 — 存储社区管理人员（Web 端）账号信息',
        'comment': '工作人员表',
    },
    'vlm_record': {
        'cn': 'VLM 找物品记录表 — 存储视觉大模型（VLM）找物品的交互记录',
        'comment': '',
    },
    'voice_prompt': {
        'cn': '语音/音乐疗法提醒表 — 存储语音提示和音乐疗法的定时提醒',
        'comment': '',
    },
    'work_order': {
        'cn': '工单表 — 存储社区工作人员处理的服务工单',
        'comment': '工单表',
    },
}

# Exclude internal/hidden columns that shouldn't be printed
SKIP_COLUMNS = set()


def connect():
    return pymysql.connect(**DB_CONFIG)


def get_create_table(cursor, table_name):
    """Get CREATE TABLE statement"""
    cursor.execute(f"SHOW CREATE TABLE `{table_name}`")
    row = cursor.fetchone()
    if row:
        return row[1]
    return None


def get_columns(cursor, table_name):
    """Get column names in order"""
    cursor.execute(f"SHOW COLUMNS FROM `{table_name}`")
    return [row[0] for row in cursor.fetchall()]


def get_all_data(cursor, table_name, columns):
    """Get all rows from table"""
    col_str = ', '.join(f'`{c}`' for c in columns)
    cursor.execute(f"SELECT {col_str} FROM `{table_name}` ORDER BY `id`")
    return cursor.fetchall()


def format_value(val):
    """Format a value for markdown table"""
    if val is None:
        return '—'
    if isinstance(val, bytes):
        try:
            val = val.decode('utf-8')
        except:
            val = val.hex()
    if isinstance(val, bool):
        return '✓' if val else '✗'
    if isinstance(val, (int, float)):
        if isinstance(val, float) and val == int(val):
            val = int(val)
        return str(val)
    if isinstance(val, datetime.datetime):
        return val.strftime('%Y-%m-%d %H:%M:%S')
    s = str(val).replace('|', '\\|').replace('\n', ' ').replace('\r', '')
    return s if s else '—'


def build_markdown_table(columns, data):
    """Build a markdown table from columns and data"""
    lines = []
    # Header
    lines.append('| ' + ' | '.join(columns) + ' |')
    # Separator
    lines.append('|' + '|'.join('----' for _ in columns) + '|')
    # Data rows
    for row in data:
        formatted = [format_value(v) for v in row]
        lines.append('| ' + ' | '.join(formatted) + ' |')
    return '\n'.join(lines)


def generate():
    conn = connect()
    cursor = conn.cursor()

    lines = []
    export_time = datetime.datetime.now().strftime('%Y-%m-%d')

    # Title
    lines.append('# 安心伴（anxinban）智慧家居系统 - 数据库全量数据')
    lines.append('')
    lines.append(f'> 数据库：`anxinban` | 引擎：MySQL 8.0 | 字符集：utf8mb4')
    lines.append(f'> 共 38 张表，导出时间：{export_time}，包含所有记录（非样本数据）')
    lines.append('')
    lines.append('---')
    lines.append('')

    # Table of contents
    lines.append('## 目录')
    lines.append('')
    for i, t in enumerate(TABLE_ORDER, 1):
        cursor.execute(f"SELECT COUNT(*) FROM `{t}`")
        count = cursor.fetchone()[0]
        info = TABLE_INFO.get(t, {})
        cn = info.get('cn', t)
        # Extract first sentence for TOC
        cn_short = cn.split('—')[0].strip().rstrip('表')
        empty_mark = '（空）' if count == 0 else ''
        lines.append(f'{i}. [{t}](#{i}-{t.lower().replace("_", "-")}) — {cn_short} {empty_mark}')
    lines.append('')
    lines.append('---')
    lines.append('')

    total_rows = 0
    non_empty_count = 0

    for idx, table_name in enumerate(TABLE_ORDER, 1):
        info = TABLE_INFO.get(table_name, {})
        cn_desc = info.get('cn', table_name)
        comment = info.get('comment', '')

        # Get schema info
        cursor.execute(f"SELECT COUNT(*) FROM `{table_name}`")
        row_count = cursor.fetchone()[0]

        columns = get_columns(cursor, table_name)
        create_sql = get_create_table(cursor, table_name)

        lines.append(f'## {idx}. {table_name}')
        lines.append('')

        if row_count == 0:
            lines.append(f'> {cn_desc}')
            lines.append('')
            if create_sql:
                lines.append('```sql')
                lines.append(create_sql + ';')
                lines.append('```')
            lines.append('')
            lines.append(f'*（当前无数据，共 0 条记录）*')
            lines.append('')
            continue

        non_empty_count += 1
        total_rows += row_count

        lines.append(f'> {cn_desc}（{row_count} 条记录）')
        lines.append('')

        if create_sql:
            lines.append('```sql')
            lines.append(create_sql + ';')
            lines.append('```')
            lines.append('')

        # Get all data
        data = get_all_data(cursor, table_name, columns)

        if row_count <= 50:
            # Small table: show all data inline
            lines.append(build_markdown_table(columns, data))
            lines.append('')
        else:
            # Large table: show summary + all data
            lines.append(f'**全部 {row_count} 条记录：**')
            lines.append('')
            lines.append(build_markdown_table(columns, data))
            lines.append('')

    # Statistics
    lines.append('---')
    lines.append('')
    lines.append('## 数据统计')
    lines.append('')
    lines.append('| 指标 | 数值 |')
    lines.append('|------|------|')
    lines.append(f'| 数据库表总数 | **38** |')
    lines.append(f'| 有数据的表 | {non_empty_count} |')
    lines.append(f'| 空表 | {38 - non_empty_count} |')
    lines.append(f'| 总记录数 | **{total_rows}** |')

    for t in TABLE_ORDER:
        cursor.execute(f"SELECT COUNT(*) FROM `{t}`")
        count = cursor.fetchone()[0]
        cn = TABLE_INFO.get(t, {}).get('cn', t).split('—')[0].strip()
        lines.append(f'| {t} | {count} |')

    lines.append('')

    cursor.close()
    conn.close()

    # Write file
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))

    print(f'Written {OUTPUT_FILE}')
    print(f'Tables: {non_empty_count} non-empty, {38 - non_empty_count} empty')
    print(f'Total rows: {total_rows}')


if __name__ == '__main__':
    generate()
