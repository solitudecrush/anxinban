#!/usr/bin/env python3
"""
Generate physiologically realistic health data for anxious elderly people.
Updates body_temperature, heart_rate, blood_pressure, blood_oxygen tables
with values that follow circadian rhythms and anxiety-state characteristics.

Key features:
- Circadian rhythm (sine wave) for each metric
- Per-elder baseline variations
- Day-over-day smooth drift (random walk with bounds)
- Anxiety characteristics: higher HR, higher BP, slightly higher temp, slightly lower SpO2
- Post-generation smoothing to enforce adjacency constraints
"""

import pymysql
import math
import random
from datetime import datetime, timedelta, date
from collections import defaultdict

# ─── Database config ───
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '1234',
    'database': 'anxinban',
    'charset': 'utf8mb4',
}

# ─── Elders ───
ELDERS = ['elder_001', 'elder_002', 'elder_003', 'elder_004', 'elder_005']

# Per-elder baseline offsets (small variations to make each elder unique)
# These are deviations from the population mean for each metric
ELDER_OFFSETS = {
    'elder_001': {'temp': +0.10, 'hr': +3, 'sbp': +4, 'dbp': +2, 'spo2': +0.3},
    'elder_002': {'temp': -0.05, 'hr': -2, 'sbp': -3, 'dbp': -1, 'spo2': -0.2},
    'elder_003': {'temp': +0.05, 'hr': +1, 'sbp': +2, 'dbp': +1, 'spo2': +0.1},
    'elder_004': {'temp': -0.10, 'hr': -4, 'sbp': -5, 'dbp': -2, 'spo2': -0.4},
    'elder_005': {'temp': +0.00, 'hr': +2, 'sbp': +1, 'dbp':  0, 'spo2': +0.2},
}

# ─── Circadian curve generators ───

def circadian_temp(hour: int) -> float:
    """
    Anxious elder body temperature (amplified波动).
    Baseline: 36.8°C, min ~36.3 at ~4am, max ~37.3 at ~16pm.
    Amplitude 0.50 keeps peak at 37.3 (before offset), minimizing ceiling plateau.
    Elder offsets + day drift spread the range to 35.8-37.3 across the population.
    """
    phase = 2 * math.pi * (hour - 9.5) / 24  # peak at 15:30, trough at 03:30
    amplitude = 0.42  # peak 37.17; elder_001(+0.1)+drift≤0.12=37.39→天花板仅1h
    return 36.75 + amplitude * math.sin(phase)


def circadian_hr(hour: int) -> float:
    """
    Anxious elder heart rate (amplified波动).
    Baseline: 82 bpm, min ~62 at ~3am, max ~100 at ~17pm.
    """
    phase = 2 * math.pi * (hour - 9) / 24  # peak at 15:00, trough at 03:00
    amplitude = 12.0  # peak 94; elder_001(+3)+drift≤4=101, 触105仅偶尔
    return 82.0 + amplitude * math.sin(phase)


def circadian_sbp(hour: int) -> float:
    """
    Anxious elder SBP (amplified波动).
    Baseline: 132 mmHg, min ~114 at 3am, max ~150 at 17pm.
    """
    phase = 2 * math.pi * (hour - 8.5) / 24  # peak at 14:30, trough at 02:30
    amplitude = 10.0  # peak 140; elder_001(+4)+drift≤5=149, 触150仅偶尔
    return 130.0 + amplitude * math.sin(phase)


def circadian_dbp(hour: int) -> float:
    """
    Anxious elder DBP (amplified波动).
    Baseline: 82 mmHg, min ~72 at 3am, max ~92 at 17pm.
    """
    phase = 2 * math.pi * (hour - 8.5) / 24  # peak at 14:30, trough at 02:30 (same as SBP)
    amplitude = 7.0  # peak 87; elder_001(+2)=89, 避免卡92天花板
    return 80.0 + amplitude * math.sin(phase)


def circadian_spo2(hour: int) -> float:
    """
    Anxious elder SpO2 (amplified波动).
    Baseline: 94.0%, min ~91.0 at ~3am, max ~97.0 at ~15pm.
    """
    phase = 2 * math.pi * (hour - 9) / 24
    amplitude = 2.80  # ±2.8%, gives range ~91.2-96.8
    return 94.00 + amplitude * math.sin(phase)


# ─── Day-level drift (random walk) ───

def generate_day_drifts(dates_list, max_daily_change, max_total_drift, seed_offset=0):
    """
    Generate a smooth random walk of daily drifts.
    Returns: dict[date] -> float
    """
    if not dates_list:
        return {}

    sorted_dates = sorted(set(dates_list))
    rng = random.Random(42 + seed_offset)

    drifts = {}
    prev = 0.0
    for d in sorted_dates:
        step = rng.uniform(-max_daily_change, max_daily_change)
        new_val = prev + step
        new_val = max(-max_total_drift, min(max_total_drift, new_val))
        drifts[d] = new_val
        prev = new_val

    return drifts


# ─── Post-generation smoothing ───

def smooth_sequence(values, timestamps, max_adj_change, lo, hi):
    """
    Forward-backward smoothing pass to enforce adjacency constraints.
    Modifies values in-place minimally to ensure no two adjacent values
    (within 2 hours) differ by more than max_adj_change.

    Uses two-pass approach:
    1. Forward pass: clamp each value relative to previous
    2. Backward pass: clamp each value relative to next
    This ensures symmetry and minimal distortion.
    """
    n = len(values)
    if n < 2:
        return values

    # Forward pass
    for i in range(1, n):
        time_diff = abs((timestamps[i] - timestamps[i-1]).total_seconds()) / 3600.0
        if time_diff <= 2.0:
            diff = values[i] - values[i-1]
            if abs(diff) > max_adj_change:
                values[i] = values[i-1] + max_adj_change if diff > 0 else values[i-1] - max_adj_change

    # Backward pass
    for i in range(n - 2, -1, -1):
        time_diff = abs((timestamps[i+1] - timestamps[i]).total_seconds()) / 3600.0
        if time_diff <= 2.0:
            diff = values[i] - values[i+1]
            if abs(diff) > max_adj_change:
                values[i] = values[i+1] + max_adj_change if diff > 0 else values[i+1] - max_adj_change

    # Final range clamp
    for i in range(n):
        values[i] = max(lo, min(hi, values[i]))

    return values


def ensure_min_diff(values, min_diff, lo, hi):
    """
    确保相邻值至少相差 min_diff，消除连续相同值。
    两趟扫描：当前值相对前一值差距不足时，沿趋势方向推开。
    """
    n = len(values)
    if n < 2 or min_diff <= 0:
        return values
    for i in range(1, n):
        diff = values[i] - values[i-1]
        if abs(diff) < min_diff:
            values[i] = values[i-1] + (min_diff if diff >= 0 else -min_diff)
    for i in range(n - 2, -1, -1):
        diff = values[i] - values[i+1]
        if abs(diff) < min_diff:
            values[i] = values[i+1] + (min_diff if diff >= 0 else -min_diff)
    for i in range(n):
        values[i] = max(lo, min(hi, values[i]))
    return values


# ─── Main generation function ───

def generate_updates(table_name, value_columns, circadian_funcs, constraints,
                     day_drift_amplitude, max_total_drift, noise_std,
                     elder_offsets_key, max_adj_changes):
    """
    Generate UPDATE data for a table. Returns list of (id, value1, [value2, ...]) tuples.
    """
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    cols = ['id', 'elder_id', 'timestamp']
    cursor.execute(f"SELECT {', '.join(cols)} FROM {table_name} ORDER BY elder_id, timestamp")
    rows = cursor.fetchall()

    if not rows:
        conn.close()
        return []

    # Collect records per elder
    elder_records = defaultdict(list)
    elder_dates = defaultdict(set)
    for row in rows:
        id_val, elder_id, ts = row
        d = ts.date() if isinstance(ts, datetime) else ts.date()
        elder_dates[elder_id].add(d)
        elder_records[elder_id].append((id_val, ts, d))

    # Generate day drifts per elder
    all_drifts = {}
    for elder_id in ELDERS:
        dates = sorted(elder_dates.get(elder_id, set()))
        if dates:
            seed = ELDERS.index(elder_id) * 13 + hash(table_name) % 100
            all_drifts[elder_id] = generate_day_drifts(
                dates, day_drift_amplitude, max_total_drift, seed
            )
        else:
            all_drifts[elder_id] = {}

    n_cols = len(value_columns)

    # ── Phase 1: Generate raw values per elder ──
    elder_raw_values = {}  # elder_id -> list of (id, [val1, val2, ...])

    for elder_id in ELDERS:
        recs = elder_records.get(elder_id, [])
        if not recs:
            continue

        offset = ELDER_OFFSETS.get(elder_id, {})
        vals = []
        prev_raw = [None] * n_cols
        prev_ts = None

        for id_val, ts, d in recs:
            hour = ts.hour
            day_drift = all_drifts.get(elder_id, {}).get(d, 0.0)

            row_vals = []
            for i in range(n_cols):
                # Circadian base + elder offset + day drift + noise
                base = circadian_funcs[i](hour)
                elder_off_key = elder_offsets_key[i] if isinstance(elder_offsets_key, list) else elder_offsets_key
                elder_off = offset.get(elder_off_key, 0)
                raw = base + elder_off + day_drift
                raw += random.gauss(0, noise_std)

                # Soft adjacency constraint (initial)
                if prev_raw[i] is not None and prev_ts is not None:
                    time_diff = abs((ts - prev_ts).total_seconds()) / 3600.0
                    if time_diff <= 2.0:
                        diff = raw - prev_raw[i]
                        if abs(diff) > max_adj_changes[i] * 1.2:  # Slightly looser, will be fixed in smoothing
                            raw = prev_raw[i] + max_adj_changes[i] * 1.2 if diff > 0 else prev_raw[i] - max_adj_changes[i] * 1.2

                # Range clamp
                lo, hi = constraints[i]
                raw = max(lo, min(hi, raw))
                row_vals.append(raw)

            vals.append((id_val, ts, row_vals))
            prev_raw = row_vals
            prev_ts = ts

        elder_raw_values[elder_id] = vals

    # ── Phase 2: Post-processing smoothing per elder per column ──
    updates = []
    for elder_id in ELDERS:
        vals = elder_raw_values.get(elder_id, [])
        if not vals:
            continue

        ids = [v[0] for v in vals]
        timestamps = [v[1] for v in vals]

        # Extract per-column values
        col_data = []
        for i in range(n_cols):
            col_vals = [v[2][i] for v in vals]
            lo, hi = constraints[i]
            smoothed = smooth_sequence(col_vals, timestamps, max_adj_changes[i], lo, hi)
            col_data.append(smoothed)

        # Recombine
        for j, id_val in enumerate(ids):
            row_vals = tuple(col_data[i][j] for i in range(n_cols))
            updates.append((id_val,) + row_vals)

    conn.close()
    return updates


# ─── Post-rounding adjacency fix ───

def fix_adjacency_after_rounding(updates, table_name, value_columns, round_funcs, max_adj_changes,
                                 constraints, use_tenths=False):
    """
    After rounding, re-check adjacency constraints and fix violations.
    When use_tenths=True, values are multiplied by 10 and treated as integers
    to eliminate floating-point rounding artifacts (for body_temp, blood_oxygen).
    """
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    n_cols = len(value_columns)

    # Build a dict: id -> {elder_id, timestamp}
    id_map = {}
    ids = [u[0] for u in updates]

    batch_size = 500
    for i in range(0, len(ids), batch_size):
        batch_ids = ids[i:i+batch_size]
        placeholders = ','.join(['%s'] * len(batch_ids))
        cursor.execute(
            f"SELECT id, elder_id, timestamp FROM {table_name} WHERE id IN ({placeholders})",
            batch_ids
        )
        for row in cursor.fetchall():
            id_map[row[0]] = (row[1], row[2])

    # Group by elder, sort by timestamp
    elder_data = defaultdict(list)
    for upd in updates:
        id_val = upd[0]
        if id_val in id_map:
            elder_id, ts = id_map[id_val]
            if use_tenths:
                # Convert to integer tenths for precise control
                rounded_vals = [
                    int(round(round_funcs[i](upd[1+i]) * 10)) for i in range(n_cols)
                ]
            else:
                rounded_vals = [
                    round_funcs[i](upd[1+i]) for i in range(n_cols)
                ]
            elder_data[elder_id].append((id_val, ts, rounded_vals))

    # For each elder, smooth each column
    fixed_updates = []
    for elder_id in ELDERS:
        recs = elder_data.get(elder_id, [])
        if not recs:
            continue
        recs.sort(key=lambda x: x[1])  # sort by timestamp

        ids_list = [r[0] for r in recs]
        ts_list = [r[1] for r in recs]

        col_values = []
        for i in range(n_cols):
            vals = [r[2][i] for r in recs]
            if use_tenths:
                # Constraints in tenths
                lo = int(constraints[i][0] * 10)
                hi = int(constraints[i][1] * 10)
                adj_change = int(max_adj_changes[i] * 10)  # e.g., 0.15 → 1 tenth
                # Ensure at least 1 to avoid zero-change
                if adj_change < 1:
                    adj_change = 1
            else:
                lo, hi = constraints[i]
                adj_change = max_adj_changes[i]

            smoothed = smooth_sequence(list(vals), ts_list, adj_change, lo, hi)
            # 消除连续相同值：相邻值至少相差 1 个单位
            smoothed = ensure_min_diff(smoothed, 1, lo, hi)
            col_values.append(smoothed)

        for j, id_val in enumerate(ids_list):
            if use_tenths:
                row_vals = tuple(col_values[i][j] / 10.0 for i in range(n_cols))
            else:
                row_vals = tuple(col_values[i][j] for i in range(n_cols))
            fixed_updates.append((id_val,) + row_vals)

    conn.close()
    return fixed_updates


# ─── Rounding helpers ───

def round_temp(v):
    return round(v, 1)

def round_hr(v):
    return int(round(v))

def round_bp(v):
    return int(round(v))

def round_spo2(v):
    return round(v, 1)


# ═══════════════════════════════════════════════════════════
# MAIN EXECUTION
# ═══════════════════════════════════════════════════════════

def main():
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    random.seed(12345)

    # ── 1. body_temperature ──
    print("=" * 60)
    print("Generating body_temperature data...")
    print("=" * 60)

    bt_updates = generate_updates(
        table_name='body_temperature',
        value_columns=['value'],
        circadian_funcs=[circadian_temp],
        constraints=[(35.8, 37.3)],
        day_drift_amplitude=0.12,     # 日间波动（相邻天漂移上限）
        max_total_drift=0.30,          # 累计漂移上限
        noise_std=0.05,                # 小时内随机噪声
        elder_offsets_key='temp',
        max_adj_changes=[0.25],
    )

    # Post-rounding adjacency fix
    bt_updates = fix_adjacency_after_rounding(
        bt_updates, 'body_temperature', ['value'],
        [round_temp], [0.25], [(35.8, 37.3)],
        use_tenths=True
    )

    batch_size = 100
    sql = "UPDATE body_temperature SET value = %s WHERE id = %s"
    for i in range(0, len(bt_updates), batch_size):
        batch = bt_updates[i:i+batch_size]
        params = [(round_temp(v), id_val) for id_val, v in batch]
        cursor.executemany(sql, params)
        conn.commit()
    print(f"Updated {len(bt_updates)} records in body_temperature")

    # ── 2. heart_rate ──
    print("=" * 60)
    print("Generating heart_rate data...")
    print("=" * 60)

    hr_updates = generate_updates(
        table_name='heart_rate',
        value_columns=['value'],
        circadian_funcs=[circadian_hr],
        constraints=[(60, 105)],
        day_drift_amplitude=3.5,      # 日间波动
        max_total_drift=7.0,           # 累计漂移上限
        noise_std=1.5,                 # 小时内噪声
        elder_offsets_key='hr',
        max_adj_changes=[10],
    )

    sql = "UPDATE heart_rate SET value = %s WHERE id = %s"
    for i in range(0, len(hr_updates), batch_size):
        batch = hr_updates[i:i+batch_size]
        params = [(round_hr(v), id_val) for id_val, v in batch]
        cursor.executemany(sql, params)
        conn.commit()
    print(f"Updated {len(hr_updates)} records in heart_rate")

    # ── 3. blood_pressure ──
    print("=" * 60)
    print("Generating blood_pressure data...")
    print("=" * 60)

    bp_updates = generate_updates(
        table_name='blood_pressure',
        value_columns=['systolic', 'diastolic'],
        circadian_funcs=[circadian_sbp, circadian_dbp],
        constraints=[(110, 150), (65, 92)],
        day_drift_amplitude=4.5,      # 日间波动
        max_total_drift=9.0,           # 累计漂移上限
        noise_std=2.0,                 # 小时内噪声
        elder_offsets_key=['sbp', 'dbp'],
        max_adj_changes=[12, 8],
    )

    sql = "UPDATE blood_pressure SET systolic = %s, diastolic = %s WHERE id = %s"
    for i in range(0, len(bp_updates), batch_size):
        batch = bp_updates[i:i+batch_size]
        params = [(round_bp(sbp), round_bp(dbp), id_val) for id_val, sbp, dbp in batch]
        cursor.executemany(sql, params)
        conn.commit()
    print(f"Updated {len(bp_updates)} records in blood_pressure")

    # ── 4. blood_oxygen ──
    print("=" * 60)
    print("Generating blood_oxygen data...")
    print("=" * 60)

    bo_updates = generate_updates(
        table_name='blood_oxygen',
        value_columns=['value'],
        circadian_funcs=[circadian_spo2],
        constraints=[(90.0, 98.0)],
        day_drift_amplitude=1.0,      # 日间波动
        max_total_drift=2.2,           # 累计漂移上限
        noise_std=0.45,                # 小时内噪声
        elder_offsets_key='spo2',
        max_adj_changes=[2.5],
    )

    # Post-rounding adjacency fix for SpO2
    bo_updates = fix_adjacency_after_rounding(
        bo_updates, 'blood_oxygen', ['value'],
        [round_spo2], [2.5], [(90.0, 98.0)],
        use_tenths=True
    )

    sql = "UPDATE blood_oxygen SET value = %s WHERE id = %s"
    for i in range(0, len(bo_updates), batch_size):
        batch = bo_updates[i:i+batch_size]
        params = [(round_spo2(v), id_val) for id_val, v in batch]
        cursor.executemany(sql, params)
        conn.commit()
    print(f"Updated {len(bo_updates)} records in blood_oxygen")

    conn.close()

    print("\n" + "=" * 60)
    print("ALL UPDATES COMPLETE")
    print("=" * 60)

    return {
        'body_temperature': len(bt_updates),
        'heart_rate': len(hr_updates),
        'blood_pressure': len(bp_updates),
        'blood_oxygen': len(bo_updates),
    }


if __name__ == '__main__':
    counts = main()
    print("\nSummary:")
    for table, count in counts.items():
        print(f"  {table}: {count} records")
