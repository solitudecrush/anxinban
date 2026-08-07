#!/usr/bin/env python3
"""
══════════════════════════════════════════════════════════════════
张国强 (elder_001) — 生命体征模拟数据生成
══════════════════════════════════════════════════════════════════

病史档案:
  年龄 78, 男性 | 健康状态: warning/关注
  诊断: 高血压(10年) + 糖尿病(5年) + 失眠:中度
  用药: 降压药, 降糖药, 阿司匹林
  过敏: 青霉素
  备注: 需定期监测血糖血压

生理特征 (高血压+糖尿病+中度失眠+严重焦虑):
  【心率】基线 78-88 bpm, 非勺型(夜间仅降5-8%), 凌晨2-4点失眠觉醒致心率突升
         糖尿病自主神经病变 → 心率变异性降低 → 变化相对平缓但基线偏高
  【血压】SBP 135-155 / DBP 82-95, 非勺型, 清晨血压激增显著(+15-20 mmHg)
         高血压+焦虑 → 持续压力负荷, 周末独处焦虑加重
  【体温】基线 36.7-37.0℃, 轻度心因性发热(焦虑时+0.2-0.4℃)
  【血氧】基线 93-97%, 糖尿病自主神经病变致变异性降低
         焦虑过度换气偶致短暂升高(+1-2%)
  【焦虑模式】晨起交感神经激活致血压心率双升; 周末独处焦虑加重;
             糖尿病低血糖恐惧 → 随机轻度心率增快+出汗(无法模拟出汗)
══════════════════════════════════════════════════════════════════
"""

import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from elder_profiles import *
import random
import pymysql

ELDER_ID = 'elder_001'
ELDER_NAME = ELDER_NAMES[ELDER_ID]

# ─── 心率配置 ───
# 高血压+中度失眠: 非勺型, 凌晨觉醒, 变异性降低
def e001_hr_modifier(h, raw, d, rng):
    # 凌晨失眠觉醒: 2-4点心率突升
    if 1 <= h <= 4 and rng.random() < 0.25:
        raw += rng.uniform(8, 15)
    # 非勺型: 夜间降幅减小
    if 0 <= h <= 5:
        raw += rng.uniform(2, 5)  # 抵消部分夜间降幅
    # 清晨觉醒反应增强
    if 6 <= h <= 8:
        raw += rng.uniform(3, 8)
    return raw

HR_CONFIG = make_sensor_config(
    'heart_rate', 'bpm', 58, 115,
    circadian_hr_target, baseline_shift=+4.0,  # 高血压+焦虑: 基线偏高
    noise_std=2.5, alpha=0.45, max_step=6, min_diff=1,
    weekly_boost=2, round_int=True,
    monthly_drift_config=(0.3, 4.0),
    hourly_modifier=e001_hr_modifier,
    abnormal_fn=abnormal_hr,
)

# ─── 血氧配置 ───
# 糖尿病自主神经病变: 变异性降低, 但焦虑过度换气偶致升高
def e001_spo2_modifier(h, raw, d, rng):
    # 糖尿病: 相对稳定 (减少随机噪声在generate中已控制)
    if 8 <= h <= 18 and rng.random() < 0.12:
        raw += rng.uniform(0.5, 1.5)  # 日间焦虑过度换气→短暂升高
    return raw

SPO2_CONFIG = make_sensor_config(
    'spo2', '%', 91, 100,
    circadian_spo2_target, baseline_shift=+0.3,
    noise_std=0.4, alpha=0.55, max_step=1.0, min_diff=0.1,
    weekly_boost=0, round_int=False,
    monthly_drift_config=(0.05, 0.8),
    hourly_modifier=e001_spo2_modifier,
    abnormal_fn=abnormal_spo2,
)

# ─── 体温配置 ───
# 轻度心因性发热 + 糖尿病代谢影响
def e001_temp_modifier(h, raw, d, rng):
    if 10 <= h <= 18 and rng.random() < 0.15:
        raw += rng.uniform(0.1, 0.3)  # 日间焦虑→心因性发热
    return raw

TEMP_CONFIG = make_sensor_config(
    'temperature', '℃', 36.0, 37.7,
    circadian_temp_target, baseline_shift=+0.15,
    noise_std=0.06, alpha=0.50, max_step=0.15, min_diff=0.1,
    weekly_boost=0.05, round_int=False,
    monthly_drift_config=(0.01, 0.15),
    hourly_modifier=e001_temp_modifier,
    abnormal_fn=abnormal_temp,
)

# ─── 收缩压配置 ───
# 高血压核心特征: 持续偏高, 非勺型, 清晨激增强
def e001_sbp_modifier(h, raw, d, rng):
    # 非勺型夜间: 降幅不足
    if 0 <= h <= 5:
        raw += rng.uniform(4, 10)
    # 清晨血压激增 (morning surge): 5-8点
    if 5 <= h <= 8:
        raw += (h - 3) * rng.uniform(2.5, 4.5)
    return raw

SBP_CONFIG = make_sensor_config(
    'blood_pressure_sys', 'mmHg', 115, 165,
    circadian_sbp_target, baseline_shift=+4.0,  # 高血压基线(适度)
    noise_std=3.0, alpha=0.40, max_step=8, min_diff=1,
    weekly_boost=2, round_int=True,
    monthly_drift_config=(0.4, 4.0),
    hourly_modifier=e001_sbp_modifier,
    abnormal_fn=abnormal_sbp,
)

# ─── 舒张压配置 ───
def e001_dbp_modifier(h, raw, d, rng):
    if 0 <= h <= 5:
        raw += rng.uniform(2, 6)
    if 5 <= h <= 8:
        raw += (h - 3) * rng.uniform(1.0, 2.5)
    return raw

DBP_CONFIG = make_sensor_config(
    'blood_pressure_dia', 'mmHg', 68, 100,
    circadian_dbp_target, baseline_shift=+1.5,
    noise_std=2.0, alpha=0.40, max_step=5, min_diff=1,
    weekly_boost=1, round_int=True,
    monthly_drift_config=(0.3, 3.0),
    hourly_modifier=e001_dbp_modifier,
    abnormal_fn=abnormal_dbp,
)

# ─── 组装传感器列表 ───
SENSOR_CONFIGS = [HR_CONFIG, SPO2_CONFIG, TEMP_CONFIG, SBP_CONFIG, DBP_CONFIG]

# ═══════════════════════════════════════════════════════════════
# 主程序
# ═══════════════════════════════════════════════════════════════

def generate():
    """仅生成数据，不写入数据库"""
    random.seed(12345)
    start_date = date(2026, 7, 7)
    end_date = date(2026, 8, 7)
    dates, d = [], start_date
    while d <= end_date:
        dates.append(d); d += timedelta(days=1)
    hours = list(range(24))
    return build_records(ELDER_ID, SENSOR_CONFIGS, dates, hours)


def main():
    records = generate()
    
    print(f"{'='*60}")
    print(f"生成 {ELDER_NAME}({ELDER_ID}) 模拟数据")
    print(f"病史: 高血压+糖尿病+中度失眠 | 严重焦虑")
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
