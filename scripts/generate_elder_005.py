#!/usr/bin/env python3
"""
══════════════════════════════════════════════════════════════════
孙锦年 (elder_005) — 生命体征模拟数据生成
══════════════════════════════════════════════════════════════════

病史档案:
  年龄 76, 男性 | 健康状态: danger/高危
  诊断: 脑梗后遗症 + 高血压 + 失眠:中度
  用药: 降压药, 抗凝药(华法林等)
  过敏: 磺胺类药物
  备注: 需定期康复训练, 2024年12月脑梗住院30天

生理特征 (脑梗后遗症+高血压+中度失眠+严重焦虑):
  【心率】基线 82-95 bpm, 压力反射(baroreflex)受损致血压心率联动异常
         对刺激反应过度(exaggerated response): 微小情绪波动→心率剧烈波动
         脑梗后自主神经功能紊乱: 交感神经过度活跃+副交感抑制不足
         基线为五人中最高(脑梗后交感激活+抗凝药不影响心率)
  【血压】SBP 130-160 / DBP 78-98, 五人中变异性最大(labile hypertension)
         压力反射受损 → BP对外界刺激极度敏感 → 剧烈起伏
         情绪波动(脑梗后情绪失禁) → BP同步剧烈波动
         高血压+焦虑+脑梗恐惧 → 持续高血压负荷
         偶发低血压(自主神经不稳定)
  【体温】基线 36.3-36.7℃, 脑梗后体温调节中枢轻度受损
         基线偏低(下丘脑体温调节受影响), 但对环境温度敏感
  【血氧】基线 93-97%, 无原发性肺病
         脑梗后偶有吞咽困难→轻度误吸→短暂低氧(罕见)
         焦虑过度换气→轻度升高
  【焦虑模式】脑梗后情绪失禁(emotional lability):
         微小刺激→不成比例的生理反应(HR/BP剧烈波动)
         对"再次脑梗"的持续恐惧→交感神经慢性激活
         康复训练挫败感→情绪低落→焦虑→BP波动
         抗凝药出血恐惧→对身体感受过度警觉
══════════════════════════════════════════════════════════════════
"""

import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from elder_profiles import *
import random
import pymysql

ELDER_ID = 'elder_005'
ELDER_NAME = ELDER_NAMES[ELDER_ID]

# ─── 心率配置 ───
# 脑梗后: 基线最高, 对刺激反应过度, 血压心率联动
def e005_hr_modifier(h, raw, d, rng):
    # 脑梗后交感亢进: 持续偏高
    raw += rng.uniform(1, 3)

    # 中度失眠: 夜间维持较高水平
    if 0 <= h <= 5:
        raw += rng.uniform(2, 8)  # 睡眠碎片化→心率不降
        # 失眠觉醒
        if rng.random() < 0.20:
            raw += rng.uniform(6, 14)

    # 情绪失禁: 随机且不成比例的心率反应
    # 脑梗后对外界刺激极度敏感
    if rng.random() < 0.04:  # 4%/小时: 情绪触发
        trigger_type = rng.random()
        if trigger_type < 0.5:
            # 轻度刺激: 想起病情→焦虑
            raw += rng.uniform(3, 8)
        elif trigger_type < 0.8:
            # 中度刺激: 康复挫败→沮丧+愤怒
            raw += rng.uniform(6, 14)
        else:
            # 重度刺激: 突发的惊恐
            raw += rng.uniform(12, 22)
            # 惊恐后缓慢恢复(通过AR(1)自然回落)

    # 清晨血压心率联动(baroreflex受损)
    if 6 <= h <= 8:
        raw += rng.uniform(3, 9)

    return raw

HR_CONFIG = make_sensor_config(
    'heart_rate', 'bpm', 58, 115,
    circadian_hr_target, baseline_shift=+4.0,  # 脑梗后: 基线偏高但可控
    noise_std=3.5, alpha=0.35, max_step=8, min_diff=1,  # 变异性大但回归更快
    weekly_boost=2, round_int=True,
    monthly_drift_config=(0.5, 5.0),
    hourly_modifier=e005_hr_modifier,
    abnormal_fn=abnormal_hr,
)

# ─── 血氧配置 ───
# 偶有吞咽困难致轻度低氧
def e005_spo2_modifier(h, raw, d, rng):
    # 焦虑过度换气: 日间偶致升高
    if 8 <= h <= 18 and rng.random() < 0.10:
        raw += rng.uniform(0.3, 1.0)
    # 吞咽困难: 进食时段(7-8, 12-13, 18-19)偶发轻微低氧
    meal_times = [7, 8, 12, 13, 18, 19]
    if h in meal_times and rng.random() < 0.04:
        raw -= rng.uniform(0.5, 1.5)
    return raw

SPO2_CONFIG = make_sensor_config(
    'spo2', '%', 90, 100,
    circadian_spo2_target, baseline_shift=+0.1,
    noise_std=0.5, alpha=0.42, max_step=1.5, min_diff=0.1,
    weekly_boost=0, round_int=False,
    monthly_drift_config=(0.05, 1.0),
    hourly_modifier=e005_spo2_modifier,
    abnormal_fn=abnormal_spo2,
)

# ─── 体温配置 ───
# 脑梗后体温调节轻度受损: 基线偏低
def e005_temp_modifier(h, raw, d, rng):
    # 下丘脑体温调节轻度受损→对昼夜节律的跟随减弱
    # 通过降低回归强度(alpha)实现
    # 环境敏感: 随机轻度波动
    if rng.random() < 0.05:
        raw += rng.uniform(-0.2, 0.2)
    return raw

TEMP_CONFIG = make_sensor_config(
    'temperature', '℃', 35.9, 37.4,
    circadian_temp_target, baseline_shift=-0.15,  # 体温调节受损: 基线略低
    noise_std=0.08, alpha=0.35, max_step=0.20, min_diff=0.1,  # alpha低→调节减弱
    weekly_boost=0.02, round_int=False,
    monthly_drift_config=(0.01, 0.20),
    hourly_modifier=e005_temp_modifier,
    abnormal_fn=abnormal_temp,
)

# ─── 收缩压配置 ───
# Labile hypertension: 最大变异性, baroreflex受损
def e005_sbp_modifier(h, raw, d, rng):
    # 高血压: 持续偏高
    raw += rng.uniform(2, 5)

    # 情绪失禁→血压剧烈波动 (baroreflex不能有效缓冲)
    if rng.random() < 0.05:
        trigger = rng.random()
        if trigger < 0.5:
            raw += rng.uniform(5, 12)  # 轻度情绪
        elif trigger < 0.8:
            raw += rng.uniform(8, 18)  # 中度情绪
        else:
            raw += rng.uniform(15, 25)  # 惊恐→BP飙升

    # 脑梗后自主神经不稳定: 偶发低血压
    if rng.random() < 0.02:
        raw -= rng.uniform(8, 15)  # 体位性低血压倾向

    # 中度失眠: 夜间非勺型
    if 0 <= h <= 5:
        raw += rng.uniform(2, 8)

    # 清晨激增(baroreflex受损→过度反应)
    if 5 <= h <= 8:
        raw += (h - 3.5) * rng.uniform(3, 6)

    return raw

SBP_CONFIG = make_sensor_config(
    'blood_pressure_sys', 'mmHg', 105, 165,
    circadian_sbp_target, baseline_shift=+3.0,
    noise_std=4.0, alpha=0.35, max_step=10, min_diff=1,
    weekly_boost=3, round_int=True,
    monthly_drift_config=(0.5, 5.0),
    hourly_modifier=e005_sbp_modifier,
    abnormal_fn=abnormal_sbp,
)

# ─── 舒张压配置 ───
def e005_dbp_modifier(h, raw, d, rng):
    raw += rng.uniform(1, 3)
    if rng.random() < 0.05:
        trigger = rng.random()
        if trigger < 0.5:
            raw += rng.uniform(3, 8)
        elif trigger < 0.8:
            raw += rng.uniform(6, 12)
        else:
            raw += rng.uniform(10, 18)
    if rng.random() < 0.02:
        raw -= rng.uniform(4, 8)
    if 0 <= h <= 5:
        raw += rng.uniform(1, 5)
    if 5 <= h <= 8:
        raw += (h - 3.5) * rng.uniform(1.5, 3)
    return raw

DBP_CONFIG = make_sensor_config(
    'blood_pressure_dia', 'mmHg', 62, 105,
    circadian_dbp_target, baseline_shift=+1.0,
    noise_std=2.5, alpha=0.35, max_step=7, min_diff=1,
    weekly_boost=2, round_int=True,
    monthly_drift_config=(0.4, 4.0),
    hourly_modifier=e005_dbp_modifier,
    abnormal_fn=abnormal_dbp,
)

SENSOR_CONFIGS = [HR_CONFIG, SPO2_CONFIG, TEMP_CONFIG, SBP_CONFIG, DBP_CONFIG]

def generate():
    """仅生成数据，不写入数据库"""
    random.seed(56789)
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
    print(f"病史: 脑梗后遗症+高血压+中度失眠 | 严重焦虑")
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
