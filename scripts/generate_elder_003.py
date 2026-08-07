#!/usr/bin/env python3
"""
══════════════════════════════════════════════════════════════════
王兴国 (elder_003) — 生命体征模拟数据生成
══════════════════════════════════════════════════════════════════

病史档案:
  年龄 75, 男性 | 健康状态: normal/正常
  诊断: 轻度认知障碍(MCI) + 失眠:轻度
  用药: 银杏叶片
  过敏: 花粉
  备注: 记忆力减退需关注

生理特征 (轻度认知障碍+轻度失眠+严重焦虑):
  【心率】基线 70-82 bpm, 五人中最稳定的心血管状态
         傍晚"日落综合征"(sundowning): 16-19点定向力下降→焦虑→心率上升
         轻度失眠: 入睡困难但睡眠连续性尚可, 夜间偶有觉醒
  【血压】SBP 122-140 / DBP 75-86, 相对平稳
         认知混乱致血压偶发波动(迷路/遗忘→恐慌→血压升高)
  【体温】基线 36.4-36.8℃, 正常昼夜节律, 认知障碍不影响体温调节
  【血氧】基线 95-98%, 五人中最佳, 无心肺基础病
  【焦虑模式】午后/傍晚定向力下降→"我在哪里?"→焦虑加重
             对陌生环境/变化的生理应激反应
             MCI+焦虑→对自主体征的过度关注→心身放大效应
══════════════════════════════════════════════════════════════════
"""

import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from elder_profiles import *
import random
import pymysql

ELDER_ID = 'elder_003'
ELDER_NAME = ELDER_NAMES[ELDER_ID]

# ─── 心率配置 ───
# 最稳定心血管 + 日落综合征
def e003_hr_modifier(h, raw, d, rng):
    # 日落综合征: 16-19点定向力下降→焦虑→心率上升
    if 16 <= h <= 19:
        sundown_intensity = (h - 15) * 0.5 if h <= 18 else (19 - h) * 0.5 + 1
        raw += sundown_intensity * rng.uniform(2, 6)
    # 轻度失眠: 入睡困难(22-24点略高), 夜间偶醒
    if 22 <= h <= 23:
        raw += rng.uniform(2, 5)  # 入睡困难→焦虑
    if 1 <= h <= 3 and rng.random() < 0.08:
        raw += rng.uniform(5, 10)  # 偶发夜间觉醒
    return raw

HR_CONFIG = make_sensor_config(
    'heart_rate', 'bpm', 60, 105,
    circadian_hr_target, baseline_shift=+1.0,  # 最接近人群均值
    noise_std=2.0, alpha=0.50, max_step=5, min_diff=1,  # 变化平缓
    weekly_boost=2, round_int=True,
    monthly_drift_config=(0.3, 3.0),
    hourly_modifier=e003_hr_modifier,
    abnormal_fn=abnormal_hr,
)

# ─── 血氧配置 ───
# 最佳: 无心肺疾病
SPO2_CONFIG = make_sensor_config(
    'spo2', '%', 93, 100,
    circadian_spo2_target, baseline_shift=+1.0,  # 最佳基线
    noise_std=0.35, alpha=0.60, max_step=0.8, min_diff=0.1,
    weekly_boost=0, round_int=False,
    monthly_drift_config=(0.03, 0.5),
    hourly_modifier=None,
    abnormal_fn=abnormal_spo2,
)

# ─── 体温配置 ───
TEMP_CONFIG = make_sensor_config(
    'temperature', '℃', 36.1, 37.3,
    circadian_temp_target, baseline_shift=+0.0,
    noise_std=0.05, alpha=0.55, max_step=0.12, min_diff=0.1,
    weekly_boost=0.03, round_int=False,
    monthly_drift_config=(0.01, 0.10),
    hourly_modifier=None,
    abnormal_fn=abnormal_temp,
)

# ─── 收缩压配置 ───
# 认知混乱→血压偶发波动
def e003_sbp_modifier(h, raw, d, rng):
    # 日落时段: 焦虑→血压升高
    if 16 <= h <= 19:
        raw += rng.uniform(2, 6)
    # 认知混乱触发: 随机轻度升高
    if 9 <= h <= 17 and rng.random() < 0.05:
        raw += rng.uniform(5, 12)  # 迷路/遗忘→恐慌
    # 正常夜间勺型 (保留较好)
    if 0 <= h <= 5:
        raw -= rng.uniform(5, 10)
    return raw

SBP_CONFIG = make_sensor_config(
    'blood_pressure_sys', 'mmHg', 115, 155,
    circadian_sbp_target, baseline_shift=+1.0,
    noise_std=2.5, alpha=0.45, max_step=6, min_diff=1,
    weekly_boost=2, round_int=True,
    monthly_drift_config=(0.4, 4.0),
    hourly_modifier=e003_sbp_modifier,
    abnormal_fn=abnormal_sbp,
)

# ─── 舒张压配置 ───
def e003_dbp_modifier(h, raw, d, rng):
    if 16 <= h <= 19:
        raw += rng.uniform(1, 4)
    if 9 <= h <= 17 and rng.random() < 0.04:
        raw += rng.uniform(3, 8)
    if 0 <= h <= 5:
        raw -= rng.uniform(3, 6)
    return raw

DBP_CONFIG = make_sensor_config(
    'blood_pressure_dia', 'mmHg', 68, 95,
    circadian_dbp_target, baseline_shift=+1.0,
    noise_std=1.8, alpha=0.45, max_step=4, min_diff=1,
    weekly_boost=1, round_int=True,
    monthly_drift_config=(0.3, 3.0),
    hourly_modifier=e003_dbp_modifier,
    abnormal_fn=abnormal_dbp,
)

SENSOR_CONFIGS = [HR_CONFIG, SPO2_CONFIG, TEMP_CONFIG, SBP_CONFIG, DBP_CONFIG]

def generate():
    """仅生成数据，不写入数据库"""
    random.seed(34567)
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
    print(f"病史: 轻度认知障碍+轻度失眠 | 严重焦虑")
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
