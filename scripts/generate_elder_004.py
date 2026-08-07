#!/usr/bin/env python3
"""
══════════════════════════════════════════════════════════════════
赵泽莲 (elder_004) — 生命体征模拟数据生成
══════════════════════════════════════════════════════════════════

病史档案:
  年龄 80, 女性 | 健康状态: warning/关注
  诊断: 糖尿病(8年) + 关节炎 + 失眠:重度
  用药: 胰岛素, 止痛药
  过敏: 无
  备注: 行动不便需辅助, 糖尿病足史

生理特征 (糖尿病+关节炎+重度失眠+严重焦虑):
  【心率】基线 80-92 bpm, 五人中持续交感张力最高
         慢性疼痛(关节炎) → 持续高交感张力 → 心率持续偏高
         重度失眠 → 夜间几乎不降(<5% dip): 清醒+疼痛+焦虑=心率平台
         疼痛发作 → 心率骤升(模拟关节疼痛阵发性加剧)
  【血压】SBP 118-140 / DBP 72-88
         关节疼痛发作 → 血压骤升(急性疼痛→交感反射)
         重度失眠 → 非勺型血压模式(夜间不降)
         止痛药效果波动 → 血压跟随疼痛水平波动
  【体温】基线 36.6-37.1℃, 慢性炎症→持续低热倾向
         关节炎活动期 → 轻度体温升高
         糖尿病→感染风险→偶发低热
  【血氧】基线 93-96%, 重度失眠致夜间氧合波动
         疼痛致呼吸浅快 → 轻度通气不足 → SpO2略降
  【焦虑模式】五人中最严重的焦虑-失眠恶性循环:
         疼痛 → 失眠 → 焦虑 → 交感激活 → 更痛 → 更睡不着
         深夜清醒+疼痛+焦虑 → 心率血压持续高位(应触发系统告警)
         对胰岛素注射的焦虑(针头恐惧→心率升高)
══════════════════════════════════════════════════════════════════
"""

import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from elder_profiles import *
import random
import pymysql

ELDER_ID = 'elder_004'
ELDER_NAME = ELDER_NAMES[ELDER_ID]

# ─── 心率配置 ───
# 重度失眠+慢性疼痛: 最高基线, 夜间几乎不降
def e004_hr_modifier(h, raw, d, rng):
    # 重度失眠: 夜间几乎不降(抵消昼夜节律的夜间下降)
    if 0 <= h <= 6:
        raw += rng.uniform(5, 10)  # 抵消夜间降幅→心率平台
        # 深夜清醒: 22-3点疼痛+焦虑高峰
        if 22 <= h or h <= 3:
            raw += rng.uniform(2, 5)
            # 失眠的绝望感→焦虑爆发→心率飙升
            if rng.random() < 0.18:
                raw += rng.uniform(8, 18)

    # 关节炎疼痛发作: 随机疼痛加剧
    if rng.random() < 0.08:  # 8%/小时: 慢性疼痛呈阵发性加重
        pain_severity = rng.uniform(0.5, 1.0)
        # 晨僵 (morning stiffness): 6-9点疼痛加重
        if 6 <= h <= 9:
            pain_severity *= 1.5
        # 夜间疼痛: 静卧不动→关节僵硬→疼痛
        if 22 <= h or h <= 4:
            pain_severity *= 1.3
        raw += pain_severity * rng.uniform(5, 15)

    return raw

HR_CONFIG = make_sensor_config(
    'heart_rate', 'bpm', 62, 115,
    circadian_hr_target, baseline_shift=+4.0,  # 重度失眠+疼痛: 基线高
    noise_std=3.0, alpha=0.35, max_step=8, min_diff=1,
    weekly_boost=2, round_int=True,
    monthly_drift_config=(0.5, 5.0),
    hourly_modifier=e004_hr_modifier,
    abnormal_fn=abnormal_hr,
)

# ─── 血氧配置 ───
# 疼痛致呼吸浅快+重度失眠
def e004_spo2_modifier(h, raw, d, rng):
    # 疼痛时呼吸浅快→轻度通气不足
    if rng.random() < 0.06:
        raw -= rng.uniform(0.3, 1.0)
    # 深夜失眠: 呼吸不规则
    if (22 <= h or h <= 3) and rng.random() < 0.12:
        raw -= rng.uniform(0.5, 1.5)
    return raw

SPO2_CONFIG = make_sensor_config(
    'spo2', '%', 90, 100,
    circadian_spo2_target, baseline_shift=-0.2,
    noise_std=0.5, alpha=0.45, max_step=1.2, min_diff=0.1,
    weekly_boost=0, round_int=False,
    monthly_drift_config=(0.05, 1.0),
    hourly_modifier=e004_spo2_modifier,
    abnormal_fn=abnormal_spo2,
)

# ─── 体温配置 ───
# 慢性炎症→持续低热, 关节炎活动期
def e004_temp_modifier(h, raw, d, rng):
    # 慢性低度炎症: 基线偏高
    if rng.random() < 0.07:
        raw += rng.uniform(0.1, 0.3)  # 炎症活动
    # 晨僵伴随轻度体温升高
    if 6 <= h <= 9 and rng.random() < 0.15:
        raw += rng.uniform(0.1, 0.25)
    return raw

TEMP_CONFIG = make_sensor_config(
    'temperature', '℃', 36.1, 37.8,
    circadian_temp_target, baseline_shift=+0.12,
    noise_std=0.07, alpha=0.45, max_step=0.18, min_diff=0.1,
    weekly_boost=0.03, round_int=False,
    monthly_drift_config=(0.01, 0.18),
    hourly_modifier=e004_temp_modifier,
    abnormal_fn=abnormal_temp,
)

# ─── 收缩压配置 ───
# 疼痛驱动的血压波动 + 重度失眠非勺型
def e004_sbp_modifier(h, raw, d, rng):
    # 重度失眠: 夜间非勺型
    if 0 <= h <= 5:
        raw += rng.uniform(3, 8)  # 抵消夜间降幅
    # 疼痛发作→血压升高
    if rng.random() < 0.07:
        pain_bp_boost = rng.uniform(5, 15)
        if 6 <= h <= 9:  # 晨僵
            pain_bp_boost *= 1.3
        raw += pain_bp_boost
    return raw

SBP_CONFIG = make_sensor_config(
    'blood_pressure_sys', 'mmHg', 110, 160,
    circadian_sbp_target, baseline_shift=-3.0,  # 老年女性基线略低
    noise_std=3.0, alpha=0.35, max_step=8, min_diff=1,
    weekly_boost=2, round_int=True,
    monthly_drift_config=(0.5, 5.0),
    hourly_modifier=e004_sbp_modifier,
    abnormal_fn=abnormal_sbp,
)

# ─── 舒张压配置 ───
def e004_dbp_modifier(h, raw, d, rng):
    if 0 <= h <= 5:
        raw += rng.uniform(2, 5)
    if rng.random() < 0.06:
        raw += rng.uniform(3, 10)
    return raw

DBP_CONFIG = make_sensor_config(
    'blood_pressure_dia', 'mmHg', 65, 100,
    circadian_dbp_target, baseline_shift=-2.0,
    noise_std=2.0, alpha=0.35, max_step=5, min_diff=1,
    weekly_boost=1, round_int=True,
    monthly_drift_config=(0.4, 4.0),
    hourly_modifier=e004_dbp_modifier,
    abnormal_fn=abnormal_dbp,
)

SENSOR_CONFIGS = [HR_CONFIG, SPO2_CONFIG, TEMP_CONFIG, SBP_CONFIG, DBP_CONFIG]

def generate():
    """仅生成数据，不写入数据库"""
    random.seed(45678)
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
    print(f"病史: 糖尿病+关节炎+重度失眠 | 严重焦虑")
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
