#!/usr/bin/env python3
"""
共享库：老人医学档案配置 & 生理学精准的生命体征生成引擎

核心算法：分层 AR(1) 随机游走 + 均值回归
  value[t] = value[t-1] + mean_reversion + noise + event_effects
  mean_reversion = (circadian_target[h] - value[t-1]) * alpha

波动层级：日(昼夜节律) > 周(周末焦虑) > 月(缓慢漂移)
约束：无连续相同值 | 相邻变化平滑 | 生理学合理范围
"""

import math
import random
from datetime import datetime, timedelta, date
from collections import defaultdict

# ═══════════════════════════════════════════════════════════════
# 昼夜节律基础函数（人群均值）
# ═══════════════════════════════════════════════════════════════

def circadian_hr_target(hour: float) -> float:
    """
    心率昼夜节律（焦虑老人基线偏高）
    谷值 ~03:00 (62 bpm), 峰值 ~15:00 (96 bpm)
    """
    phase = 2 * math.pi * (hour - 9.0) / 24
    return 79.0 + 13.0 * math.sin(phase)

def circadian_sbp_target(hour: float) -> float:
    """
    收缩压昼夜节律
    谷值 ~02:30 (118 mmHg), 峰值 ~14:30 (148 mmHg)
    """
    phase = 2 * math.pi * (hour - 8.5) / 24
    return 133.0 + 12.0 * math.sin(phase)

def circadian_dbp_target(hour: float) -> float:
    """
    舒张压昼夜节律（同 SBP 相位）
    谷值 ~02:30 (74 mmHg), 峰值 ~14:30 (90 mmHg)
    """
    phase = 2 * math.pi * (hour - 8.5) / 24
    return 82.0 + 7.0 * math.sin(phase)

def circadian_temp_target(hour: float) -> float:
    """
    体温昼夜节律（焦虑老人基线略高）
    谷值 ~04:00 (36.3℃), 峰值 ~16:00 (37.2℃)
    """
    phase = 2 * math.pi * (hour - 9.5) / 24
    return 36.70 + 0.42 * math.sin(phase)

def circadian_spo2_target(hour: float) -> float:
    """
    血氧昼夜节律
    谷值 ~03:00 (92%), 峰值 ~15:00 (97%)
    """
    phase = 2 * math.pi * (hour - 9.0) / 24
    return 94.5 + 2.5 * math.sin(phase)


# ═══════════════════════════════════════════════════════════════
# 通用工具函数
# ═══════════════════════════════════════════════════════════════

def clamp(v, lo, hi):
    return max(lo, min(hi, v))

def ensure_diff(prev_val, new_val, min_diff, lo, hi, round_int=True):
    """确保相邻值至少相差 min_diff，杜绝连续相同值"""
    if prev_val is None:
        return clamp(new_val, lo, hi)
    if abs(new_val - prev_val) < min_diff:
        # 尝试按趋势方向推开
        direction = 1 if new_val >= prev_val else -1
        for attempt in range(5):  # 最多尝试5次
            step = min_diff + (attempt * min_diff * 0.5)
            candidate = prev_val + direction * step
            candidate = clamp(candidate, lo, hi)
            if abs(candidate - prev_val) >= min_diff:
                return candidate
            direction *= -1  # 反转方向重试
        # 最后手段：直接加减
        if round_int:
            if prev_val + 1 <= hi:
                return prev_val + 1
            elif prev_val - 1 >= lo:
                return prev_val - 1
        else:
            if prev_val + min_diff <= hi:
                return prev_val + min_diff
            elif prev_val - min_diff >= lo:
                return prev_val - min_diff
    return clamp(new_val, lo, hi)

def safe_round(value, is_int=True):
    """安全取整/舍入"""
    if is_int:
        return int(round(value))
    return round(value, 1)

def get_daily_tag(d, weekday_anxiety=True):
    """日标签：周末 anxiety_peak，其他 normal"""
    if weekday_anxiety and d.weekday() in (5, 6):
        return 'anxiety_peak'
    return 'normal'


# ═══════════════════════════════════════════════════════════════
# 核心生成引擎：AR(1) 随机游走 + 均值回归
# ═══════════════════════════════════════════════════════════════

def generate_vital_series(config, dates, hours, seed=42):
    """
    对单个传感器类型生成完整时序数据。

    参数 config:
      - name: 传感器名称
      - unit: 单位
      - lo, hi: 生理范围
      - round_int: True=int, False=float(1小数)
      - circadian_fn: 昼夜节律函数(hour) -> target
      - baseline_shift: 该老人基线偏移
      - noise_std: 逐时噪声标准差
      - mean_reversion_alpha: 均值回归强度 (0~1)
      - max_step: 相邻值最大变化幅度
      - min_diff: 相邻值最小差异
      - weekly_boost: 周末焦虑提升量
      - monthly_drift_config: (max_daily_change, max_total) 月漂移参数
      - event_triggers: [(condition_fn, effect_fn), ...] 特殊事件
      - abnormal_fn: 异常判断函数(value) -> bool
      - hourly_modifier: 特定时段修正 fn(hour, value, elder_id) -> value

    返回: [(datetime, value, is_abnormal), ...]
    """
    rng = random.Random(seed)

    # 月漂移：对每天生成一个缓慢变化的偏移
    sorted_dates = sorted(set(dates))
    md_max_daily, md_max_total = config.get('monthly_drift_config', (0.02, 0.5))
    daily_drifts = {}
    prev_drift = 0.0
    for d in sorted_dates:
        step = rng.uniform(-md_max_daily, md_max_daily)
        new_drift = clamp(prev_drift + step, -md_max_total, md_max_total)
        daily_drifts[d] = new_drift
        prev_drift = new_drift

    # 周调整：工作日 vs 周末
    weekly_boost = config.get('weekly_boost', 0)

    results = []
    prev_val = None

    for d in sorted_dates:
        # 计算当天的基线目标（月漂移 + 周调整）
        monthly_drift = daily_drifts.get(d, 0.0)
        is_weekend = d.weekday() in (5, 6)
        weekend_effect = weekly_boost if is_weekend else 0.0

        for h in hours:
            # 1. 昼夜节律目标值
            circadian_base = config['circadian_fn'](h)

            # 2. 当日综合目标 = 节律 + 老人基线偏移 + 月漂移 + 周效应
            daily_target = circadian_base + config['baseline_shift'] + monthly_drift + weekend_effect

            # 3. AR(1) 随机游走
            if prev_val is None:
                # 初始值：目标值 + 小噪声
                raw = daily_target + rng.gauss(0, config['noise_std'] * 0.5)
            else:
                # 均值回归
                mean_reversion = (daily_target - prev_val) * config['mean_reversion_alpha']
                # 随机噪声（逐时波动）
                noise = rng.gauss(0, config['noise_std'])
                raw = prev_val + mean_reversion + noise

                # 限制相邻步长（防止急上急下）
                diff = raw - prev_val
                max_step = config['max_step']
                if abs(diff) > max_step:
                    raw = prev_val + max_step if diff > 0 else prev_val - max_step

            # 4. 特定时段修正（如夜间效应、清晨激增等）
            if config.get('hourly_modifier') is not None:
                raw = config['hourly_modifier'](h, raw, d, rng)

            # 5. 特殊事件（如心律失常、呼吸暂停、疼痛发作等）
            if config.get('event_triggers'):
                for condition_fn, effect_fn in config['event_triggers']:
                    if condition_fn(h, d, prev_val, rng):
                        raw = effect_fn(raw, h, rng)

            # 6. 范围钳制
            raw = clamp(raw, config['lo'], config['hi'])

            # 7. 取整并确保与前值不同
            value = safe_round(raw, config.get('round_int', True))
            value = ensure_diff(prev_val, value, config['min_diff'], config['lo'], config['hi'], config.get('round_int', True))

            # 8. 异常判断
            is_abnormal = 1 if (config.get('abnormal_fn') and config['abnormal_fn'](value)) else 0

            results.append({
                'value': value,
                'is_abnormal': is_abnormal,
            })

            prev_val = value

    return results


# ═══════════════════════════════════════════════════════════════
# 传感器通用配置模板
# ═══════════════════════════════════════════════════════════════

def make_sensor_config(name, unit, lo, hi, circadian_fn, baseline_shift,
                       noise_std, alpha, max_step, min_diff,
                       weekly_boost=0, round_int=True,
                       monthly_drift_config=None,
                       hourly_modifier=None,
                       event_triggers=None,
                       abnormal_fn=None):
    return {
        'name': name,
        'unit': unit,
        'lo': lo,
        'hi': hi,
        'circadian_fn': circadian_fn,
        'baseline_shift': baseline_shift,
        'noise_std': noise_std,
        'mean_reversion_alpha': alpha,
        'max_step': max_step,
        'min_diff': min_diff,
        'weekly_boost': weekly_boost,
        'round_int': round_int,
        'monthly_drift_config': monthly_drift_config or (0.02, 0.5),
        'hourly_modifier': hourly_modifier,
        'event_triggers': event_triggers or [],
        'abnormal_fn': abnormal_fn,
    }


# ═══════════════════════════════════════════════════════════════
# 通用异常判断函数
# ═══════════════════════════════════════════════════════════════

def abnormal_hr(v): return v > 105 or v < 55
def abnormal_spo2(v): return v < 91
def abnormal_temp(v): return v > 37.5 or v < 35.8
def abnormal_sbp(v): return v > 160
def abnormal_dbp(v): return v > 100


# ═══════════════════════════════════════════════════════════════
# 通用时段修正函数
# ═══════════════════════════════════════════════════════════════

def nighttime_dip(h, raw, d, rng, dip_strength=1.0):
    """夜间(0-6时)心率/血压下降"""
    if 0 <= h <= 3:
        return raw - dip_strength * rng.uniform(2, 8)
    elif 4 <= h <= 6:
        return raw - dip_strength * rng.uniform(1, 4)
    return raw

def morning_surge(h, raw, d, rng, surge_strength=1.0):
    """清晨(5-8时)血压/心率激增"""
    if 5 <= h <= 8:
        return raw + surge_strength * (h - 4) * rng.uniform(1.5, 3.5)
    return raw

def anxiety_night_hr(h, raw, d, rng, prob=0.2, spike=12):
    """焦虑性夜间觉醒：心率突升"""
    if 0 <= h <= 5 and rng.random() < prob:
        return raw + rng.uniform(spike * 0.6, spike)
    return raw

def apnea_dip(h, raw, d, rng, prob=0.12, dip=2.0):
    """睡眠呼吸暂停：血氧骤降"""
    if 0 <= h <= 5 and rng.random() < prob:
        return raw - rng.uniform(dip * 0.5, dip)
    return raw


# ═══════════════════════════════════════════════════════════════
# 老人设备映射
# ═══════════════════════════════════════════════════════════════

ELDER_DEVICES = {
    'elder_001': 'dev_001',
    'elder_002': 'dev_004',
    'elder_003': 'dev_007',
    'elder_004': 'dev_010',
    'elder_005': 'dev_012',
}

ELDER_NAMES = {
    'elder_001': '张国强',
    'elder_002': '李顺',
    'elder_003': '王兴国',
    'elder_004': '赵泽莲',
    'elder_005': '孙锦年',
}

# ═══════════════════════════════════════════════════════════════
# 数据库操作
# ═══════════════════════════════════════════════════════════════

DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '1234',
    'database': 'anxinban',
    'charset': 'utf8mb4',
}

def build_records(elder_id, sensor_configs, dates, hours):
    """为一位老人构建所有传感器的完整记录"""
    device_id = ELDER_DEVICES[elder_id]
    records = []

    for cfg in sensor_configs:
        seed = (list(ELDER_DEVICES.keys()).index(elder_id) * 100 +
                sensor_configs.index(cfg) * 13 + 42)
        series = generate_vital_series(cfg, dates, hours, seed)

        for i, d in enumerate(sorted(set(dates))):
            for j, h in enumerate(hours):
                idx = i * len(hours) + j
                if idx >= len(series):
                    break
                s = series[idx]
                minute = random.randint(0, 29) if idx == 0 else (idx * 7 + 13) % 60
                second = (idx * 17 + 31) % 60
                ts = datetime(d.year, d.month, d.day, h, int(minute), int(second))
                daily_tag = get_daily_tag(d)

                records.append({
                    'elder_id': elder_id,
                    'device_id': device_id,
                    'sensor_type': cfg['name'],
                    'value': s['value'],
                    'unit': cfg['unit'],
                    'is_abnormal': s['is_abnormal'],
                    'timestamp': ts,
                    'created_at': ts,
                    'daily_tag': daily_tag,
                    'is_peak': 0,
                    'measurement_source': 'watch',
                })

    return records


def apply_records_to_db(records, clear_before='2026-07-07', clear_range_start='2026-07-07', clear_range_end='2026-08-07'):
    """将记录写入数据库"""
    import pymysql
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    try:
        # 清空旧数据
        cursor.execute(f"DELETE FROM sensor_data WHERE timestamp < '{clear_before} 00:00:00'")
        deleted_before = cursor.rowcount
        cursor.execute(f"DELETE FROM sensor_data WHERE timestamp >= '{clear_range_start} 00:00:00' AND timestamp <= '{clear_range_end} 23:59:59'")
        deleted_range = cursor.rowcount
        conn.commit()
        print(f"  已删除: {clear_before} 前 {deleted_before} 条, 范围内 {deleted_range} 条")

        # 批量插入
        sql = """INSERT INTO sensor_data
            (elder_id, device_id, sensor_type, value, unit, is_abnormal, timestamp, created_at, daily_tag, is_peak, measurement_source)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"""

        batch_size = 500
        inserted = 0
        for i in range(0, len(records), batch_size):
            batch = records[i:i+batch_size]
            params = [(r['elder_id'], r['device_id'], r['sensor_type'], r['value'], r['unit'],
                       r['is_abnormal'], r['timestamp'], r['created_at'], r['daily_tag'],
                       r['is_peak'], r['measurement_source']) for r in batch]
            cursor.executemany(sql, params)
            conn.commit()
            inserted += len(batch)
        print(f"  已插入: {inserted} 条")
        return inserted

    except Exception as e:
        conn.rollback()
        raise
    finally:
        conn.close()


def print_statistics(records):
    """打印数据统计"""
    from collections import Counter
    types = Counter(r['sensor_type'] for r in records)
    abnormals = Counter(r['sensor_type'] for r in records if r['is_abnormal'])
    days = len(set(r['timestamp'].date() for r in records))

    print(f"  总记录: {len(records)}, 覆盖 {days} 天")
    for st in sorted(types):
        ab = abnormals.get(st, 0)
        vals = [r['value'] for r in records if r['sensor_type'] == st]
        print(f"  {st:22s}: {types[st]:5d} 条 | 异常 {ab:4d} ({100*ab/types[st]:.1f}%) | "
              f"范围 {min(vals):.1f}-{max(vals):.1f} | 均值 {sum(vals)/len(vals):.2f}")

    # 验证无连续相同值
    for st in sorted(types):
        vals = [r['value'] for r in records if r['sensor_type'] == st]
        consecutive_same = sum(1 for i in range(1, len(vals)) if vals[i] == vals[i-1])
        status = "✓" if consecutive_same == 0 else f"✗ ({consecutive_same} 对)"
        print(f"  连续相同检查 [{st}]: {status}")
