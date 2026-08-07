#!/usr/bin/env python3
"""
══════════════════════════════════════════════════════════════════
李顺 (elder_002) — 生命体征模拟数据生成
══════════════════════════════════════════════════════════════════

病史档案:
  年龄 82, 女性 | 健康状态: danger/高危
  诊断: 心脏病(3年) + 骨质疏松 + 失眠:轻度
  用药: 硝酸甘油, 钙片
  过敏: 无
  备注: 需注意跌倒风险

生理特征 (心脏病+骨质疏松+轻度失眠+严重焦虑):
  【心率】基线 72-85 bpm, 偶发心律失常(心率突升15-25 bpm后缓慢回落)
         心脏病+焦虑 → 心悸发作模式: 突然心动过速 → 缓慢恢复
         变异性大(心律失常倾向), 对儿茶酚胺敏感
  【血压】SBP 120-145 / DBP 72-90, 波动较大
         偶发高血压危象(SBP 155+时触发硝酸甘油使用), 情绪相关波动
         焦虑→心悸→恐惧→血压进一步升高(恶性循环)
  【体温】基线 36.5-36.8℃, 相对稳定, 心脏病药物可能轻微影响
  【血氧】基线 93-96%, 夜间疑似睡眠呼吸暂停(SpO2降至90-92%)
         日间焦虑过度换气→短暂升高
         心功能不全→轻度肺淤血→偶尔略低
  【焦虑模式】心悸发作→恐惧→交感激活→更严重心悸(正反馈);
             对异常体征的过度警觉; 骨质疏松致活动受限→无助感→焦虑
══════════════════════════════════════════════════════════════════
"""

import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from elder_profiles import *
import random
import pymysql

ELDER_ID = 'elder_002'
ELDER_NAME = ELDER_NAMES[ELDER_ID]

# ─── 心率配置 ───
# 心脏病核心特征: 偶发心律失常 + 心悸发作后缓慢恢复
def e002_hr_events():
    """心律失常状态机: 是否处于发作中"""
    return {'in_episode': False, 'episode_hours_left': 0, 'peak_hr': 0}

e002_hr_state = e002_hr_events()

def e002_hr_modifier(h, raw, d, rng):
    global e002_hr_state
    # 心律失常发作触发: 日间压力/焦虑时段概率较高
    trigger_prob = 0.04  # 基础4%/小时
    if 8 <= h <= 20:
        trigger_prob = 0.06  # 日间更高
    if d.weekday() in (5, 6):
        trigger_prob *= 1.5  # 周末焦虑更高

    if not e002_hr_state['in_episode']:
        if rng.random() < trigger_prob:
            e002_hr_state['in_episode'] = True
            e002_hr_state['episode_hours_left'] = rng.randint(1, 3)
            e002_hr_state['peak_hr'] = raw + rng.uniform(15, 28)
            return e002_hr_state['peak_hr']

    if e002_hr_state['in_episode']:
        e002_hr_state['episode_hours_left'] -= 1
        # 缓慢回落: 每小时恢复30-50%
        recovery = (e002_hr_state['peak_hr'] - raw) * rng.uniform(0.3, 0.5)
        if e002_hr_state['episode_hours_left'] <= 0:
            e002_hr_state = e002_hr_events()
        return raw + recovery

    # 轻度失眠: 夜间偶有觉醒
    if 0 <= h <= 5 and rng.random() < 0.12:
        raw += rng.uniform(5, 10)
    return raw

HR_CONFIG = make_sensor_config(
    'heart_rate', 'bpm', 55, 125,
    circadian_hr_target, baseline_shift=-2.0,  # 心脏病: β阻滞剂效应, 基线略低
    noise_std=4.0, alpha=0.35, max_step=10, min_diff=1,  # 变异性大
    weekly_boost=3, round_int=True,
    monthly_drift_config=(0.4, 5.0),
    hourly_modifier=e002_hr_modifier,
    event_triggers=[],  # 事件在modifier中处理
    abnormal_fn=abnormal_hr,
)

# ─── 血氧配置 ───
# 睡眠呼吸暂停 + 心功能影响
def e002_spo2_modifier(h, raw, d, rng):
    # 夜间睡眠呼吸暂停: SpO2间歇性下降
    if 0 <= h <= 5:
        if rng.random() < 0.18:  # 18%概率每小时
            raw -= rng.uniform(1.5, 3.5)  # 显著去饱和
        elif rng.random() < 0.3:
            raw -= rng.uniform(0.3, 1.0)  # 轻度下降
    # 恢复期: 呼吸暂停后反弹
    if 6 <= h <= 8 and rng.random() < 0.2:
        raw += rng.uniform(0.5, 1.5)
    return raw

SPO2_CONFIG = make_sensor_config(
    'spo2', '%', 88, 100,
    circadian_spo2_target, baseline_shift=-0.3,  # 心功能不全: 略低
    noise_std=0.7, alpha=0.40, max_step=2.5, min_diff=0.1,
    weekly_boost=0, round_int=False,
    monthly_drift_config=(0.06, 1.2),
    hourly_modifier=e002_spo2_modifier,
    abnormal_fn=abnormal_spo2,
)

# ─── 体温配置 ───
# 相对稳定, 老年女性基线略低
TEMP_CONFIG = make_sensor_config(
    'temperature', '℃', 36.0, 37.5,
    circadian_temp_target, baseline_shift=-0.08,
    noise_std=0.06, alpha=0.55, max_step=0.15, min_diff=0.1,
    weekly_boost=0.02, round_int=False,
    monthly_drift_config=(0.01, 0.12),
    hourly_modifier=None,
    abnormal_fn=abnormal_temp,
)

# ─── 收缩压配置 ───
# 心脏病+焦虑: 血压波动大, 偶发危象
def e002_sbp_modifier(h, raw, d, rng):
    # 情绪性血压波动 (焦虑→心悸→血压升高)
    if 8 <= h <= 20 and rng.random() < 0.08:
        raw += rng.uniform(8, 18)  # 情绪触发高血压反应
    # 夜间: 正常勺型(心脏病患者部分保留)
    if 0 <= h <= 5:
        raw -= rng.uniform(3, 8)
    # 清晨轻度激增
    if 6 <= h <= 8:
        raw += rng.uniform(2, 6)
    return raw

SBP_CONFIG = make_sensor_config(
    'blood_pressure_sys', 'mmHg', 110, 165,
    circadian_sbp_target, baseline_shift=-2.0,
    noise_std=4.0, alpha=0.35, max_step=10, min_diff=1,  # 波动大
    weekly_boost=3, round_int=True,
    monthly_drift_config=(0.5, 6.0),
    hourly_modifier=e002_sbp_modifier,
    abnormal_fn=abnormal_sbp,
)

# ─── 舒张压配置 ───
def e002_dbp_modifier(h, raw, d, rng):
    if 8 <= h <= 20 and rng.random() < 0.07:
        raw += rng.uniform(5, 12)
    if 0 <= h <= 5:
        raw -= rng.uniform(2, 5)
    if 6 <= h <= 8:
        raw += rng.uniform(1, 4)
    return raw

DBP_CONFIG = make_sensor_config(
    'blood_pressure_dia', 'mmHg', 65, 105,
    circadian_dbp_target, baseline_shift=-1.0,
    noise_std=2.5, alpha=0.35, max_step=6, min_diff=1,
    weekly_boost=2, round_int=True,
    monthly_drift_config=(0.4, 4.0),
    hourly_modifier=e002_dbp_modifier,
    abnormal_fn=abnormal_dbp,
)

SENSOR_CONFIGS = [HR_CONFIG, SPO2_CONFIG, TEMP_CONFIG, SBP_CONFIG, DBP_CONFIG]

def generate():
    """仅生成数据，不写入数据库"""
    random.seed(23456)
    start_date, end_date = date(2026, 7, 7), date(2026, 8, 7)
    dates, d = [], start_date
    while d <= end_date:
        dates.append(d); d += timedelta(days=1)
    hours = list(range(24))
    return build_records(ELDER_ID, SENSOR_CONFIGS, dates, hours)


def main():
    records = generate()
    
    print(f"{'='*60}")
    print(f"生成 {ELDER_NAME}({ELDER_ID}) 模拟数据")
    print(f"病史: 心脏病+骨质疏松+轻度失眠 | 严重焦虑")
    print(f"日期: 2026-07-07 ~ 2026-08-07 (32天)")
    print(f"{'='*60}")
    print(f"\n生成 {len(records)} 条记录")
    print_statistics(records)

    print(f"\n写入数据库...")
    apply_records_to_db(records)
    print(f"✓ {ELDER_NAME} 数据写入完成\n")
    return records

if __name__ == '__main__':
    main()
