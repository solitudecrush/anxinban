#!/usr/bin/env python3
"""
Update elder_001 health data to increase volatility and reflect elderly characteristics.

Elderly health characteristics:
- Body temperature: 36.0-37.8°C, more variable, occasional low-grade fever, circadian rhythm
- Heart rate: 68-108 bpm, higher baseline, more variability, occasional tachycardia
- Blood pressure: systolic 128-162, diastolic 82-98 (hypertension common in elderly)
- Blood oxygen: 92-98%, more dips below 95% (common in elderly with respiratory issues)

Key principle: no consecutive identical values, increased day-to-day and within-day volatility.
"""

import random
import mysql.connector
from datetime import datetime

random.seed(20260719)

conn = mysql.connector.connect(
    host='localhost',
    user='root',
    password='1234',
    database='anxinban'
)
cursor = conn.cursor()

# ==================== Ensure 24-hour data coverage ====================
# For each table, check if the target date has all required hourly data.
# Insert any missing hours with appropriate values before applying volatility.

def get_next_id(table, id_column, prefix):
    """Get the next available ID number for a given table and prefix."""
    cursor.execute(f"""
        SELECT MAX(CAST(SUBSTRING({id_column}, {len(prefix) + 1}) AS UNSIGNED))
        FROM {table} WHERE {id_column} LIKE %s
    """, (f'{prefix}%',))
    result = cursor.fetchone()
    return (result[0] or 0) + 1

def ensure_table_coverage(table, id_column, id_prefix, target_date, elder_id, value_fn, hourly=True):
    """
    Ensure data coverage for a table on a target date.
    - hourly=True: all 24 hours (0-23)
    - hourly=False: every 3 hours (0, 3, 6, 9, 12, 15, 18, 21) — for blood pressure
    """
    cursor.execute(f"""
        SELECT HOUR(timestamp) FROM {table}
        WHERE elder_id = %s AND DATE(timestamp) = %s
    """, (elder_id, target_date))
    existing_hours = set(row[0] for row in cursor.fetchall())

    required_hours = range(24) if hourly else range(0, 24, 3)
    missing = [h for h in required_hours if h not in existing_hours]

    if not missing:
        return 0

    next_id = get_next_id(table, id_column, id_prefix)

    for hour in missing:
        val = value_fn(hour)
        ts = f'{target_date} {hour:02d}:00:00'
        created_ts = f'{target_date} {hour:02d}:01:00'
        rid = f'{id_prefix}{next_id:03d}'
        next_id += 1

        if id_column == 'bt_id':
            cursor.execute(f"""
                INSERT INTO {table} ({id_column}, elder_id, value, unit, timestamp, created_at)
                VALUES (%s, %s, %s, NULL, %s, %s)
            """, (rid, elder_id, val, ts, created_ts))
        elif id_column == 'hr_id':
            cursor.execute(f"""
                INSERT INTO {table} ({id_column}, elder_id, value, unit, timestamp, created_at)
                VALUES (%s, %s, %s, NULL, %s, %s)
            """, (rid, elder_id, int(val), ts, created_ts))
        elif id_column == 'bo_id':
            cursor.execute(f"""
                INSERT INTO {table} ({id_column}, elder_id, value, unit, timestamp, created_at)
                VALUES (%s, %s, %s, NULL, %s, %s)
            """, (rid, elder_id, val, ts, created_ts))
        elif id_column == 'bp_id':
            sys_val, dia_val = val
            cursor.execute(f"""
                INSERT INTO {table} ({id_column}, elder_id, systolic, diastolic, timestamp, created_at)
                VALUES (%s, %s, %s, %s, %s, %s)
            """, (rid, elder_id, sys_val, dia_val, ts, created_ts))

    conn.commit()
    return len(missing)


# Value generators for missing hours (realistic elderly circadian patterns)
def temp_value(hour):
    if 0 <= hour < 5:    return round(36.0 + random.uniform(0, 0.3), 1)
    elif 5 <= hour < 7:  return round(36.1 + random.uniform(0, 0.3), 1)
    elif 7 <= hour < 9:  return round(36.3 + random.uniform(0, 0.4), 1)
    elif 9 <= hour < 11: return round(36.5 + random.uniform(0, 0.4), 1)
    elif 11 <= hour < 14: return round(36.6 + random.uniform(0, 0.5), 1)
    elif 14 <= hour < 17: return round(36.5 + random.uniform(0, 0.4), 1)
    elif 17 <= hour < 20: return round(36.3 + random.uniform(0, 0.3), 1)
    else:                 return round(36.1 + random.uniform(0, 0.3), 1)

def hr_value(hour):
    if 0 <= hour < 5:    return random.randint(60, 66)
    elif 5 <= hour < 7:  return random.randint(63, 70)
    elif 7 <= hour < 9:  return random.randint(72, 82)
    elif 9 <= hour < 11: return random.randint(80, 90)
    elif 11 <= hour < 14: return random.randint(84, 95)
    elif 14 <= hour < 17: return random.randint(78, 88)
    elif 17 <= hour < 20: return random.randint(72, 80)
    else:                 return random.randint(64, 72)

def bo_value(hour):
    if 0 <= hour < 5:    return round(94.0 + random.uniform(0, 2.5), 1)
    elif 5 <= hour < 8:  return round(95.0 + random.uniform(0, 2.5), 1)
    elif 8 <= hour < 12: return round(96.5 + random.uniform(0, 2.0), 1)
    elif 12 <= hour < 17: return round(96.5 + random.uniform(0, 2.0), 1)
    elif 17 <= hour < 21: return round(95.5 + random.uniform(0, 2.0), 1)
    else:                 return round(95.0 + random.uniform(0, 2.0), 1)

def bp_value(hour):
    if 0 <= hour < 5:    return (random.randint(114, 120), random.randint(74, 80))
    elif 5 <= hour < 9:  return (random.randint(120, 140), random.randint(78, 88))
    elif 9 <= hour < 12: return (random.randint(130, 145), random.randint(82, 92))
    elif 12 <= hour < 17: return (random.randint(128, 142), random.randint(80, 90))
    elif 17 <= hour < 21: return (random.randint(126, 138), random.randint(80, 88))
    else:                 return (random.randint(120, 130), random.randint(76, 84))

# Today's date (default) — change TARGET_DATE for a specific date
TARGET_DATE = datetime.now().strftime('%Y-%m-%d')
ELDER_ID = 'elder_001'

print(f"Ensuring data coverage for {TARGET_DATE}...")
n_bt = ensure_table_coverage('body_temperature', 'bt_id', 'bt_v2_', TARGET_DATE, ELDER_ID, temp_value, hourly=True)
n_hr = ensure_table_coverage('heart_rate', 'hr_id', 'hr_v2_', TARGET_DATE, ELDER_ID, hr_value, hourly=True)
n_bo = ensure_table_coverage('blood_oxygen', 'bo_id', 'bo_v2_', TARGET_DATE, ELDER_ID, bo_value, hourly=True)
n_bp = ensure_table_coverage('blood_pressure', 'bp_id', 'bp_v2_', TARGET_DATE, ELDER_ID, bp_value, hourly=False)

if any([n_bt, n_hr, n_bo, n_bp]):
    print(f"  Inserted: body_temp={n_bt}, heart_rate={n_hr}, blood_oxygen={n_bo}, blood_pressure={n_bp}")
else:
    print("  All tables already have full coverage.")
print()

# ==================== Body Temperature ====================
# Elderly: slightly lower baseline but more volatile, occasional elevated readings
# Normal elderly range: 36.0-37.5, with more fluctuations
print("Updating body_temperature for elder_001...")
cursor.execute("SELECT id, bt_id, timestamp FROM body_temperature WHERE elder_id = 'elder_001' ORDER BY timestamp")
rows = cursor.fetchall()

prev_temp = None
for row_id, bt_id, ts in rows:
    # MySQL connector returns datetime objects directly
    if isinstance(ts, datetime):
        hour = ts.hour
    else:
        hour = int(str(ts).split()[1].split(':')[0]) if ' ' in str(ts) else 8

    # Circadian rhythm for elderly (slightly lower overall, more variable)
    if 5 <= hour < 7:
        base = 36.0
        spread = 0.5   # 36.0-36.5 (early morning low)
    elif 7 <= hour < 9:
        base = 36.2
        spread = 0.6   # 36.2-36.8 (morning rise)
    elif 9 <= hour < 11:
        base = 36.4
        spread = 0.7   # 36.4-37.1 (late morning)
    elif 11 <= hour < 14:
        base = 36.5
        spread = 0.9   # 36.5-37.4 (midday peak)
    elif 14 <= hour < 17:
        base = 36.4
        spread = 0.8   # 36.4-37.2 (afternoon)
    elif 17 <= hour < 20:
        base = 36.3
        spread = 0.6   # 36.3-36.9 (evening decline)
    else:
        base = 36.1
        spread = 0.4   # 36.1-36.5 (night low)

    val = round(base + random.uniform(0, spread), 1)

    # Occasional low-grade fever spike (10% chance): +0.3-0.6
    if random.random() < 0.10:
        val = round(val + random.uniform(0.3, 0.6), 1)
        val = min(val, 37.9)

    # Occasional sub-normal dip (8% chance): -0.2-0.4
    if random.random() < 0.08:
        val = round(val - random.uniform(0.2, 0.4), 1)
        val = max(val, 35.8)

    # Ensure no consecutive identical values (minimum 0.1 difference)
    attempts = 0
    while prev_temp is not None and abs(val - prev_temp) < 0.1:
        val = round(val + random.choice([-0.3, -0.2, -0.1, 0.1, 0.2, 0.3, 0.4]), 1)
        val = max(35.8, min(37.9, val))
        attempts += 1
        if attempts > 20:
            break

    prev_temp = val
    cursor.execute("UPDATE body_temperature SET value = %s WHERE id = %s", (val, row_id))

conn.commit()
print(f"  Updated {len(rows)} body_temperature records")

# ==================== Heart Rate ====================
# Elderly: higher baseline (68-108), more variability, occasional tachycardia
print("Updating heart_rate for elder_001...")
cursor.execute("SELECT id, hr_id, timestamp FROM heart_rate WHERE elder_id = 'elder_001' ORDER BY timestamp")
rows = cursor.fetchall()

prev_hr = None
for row_id, hr_id, ts in rows:
    if isinstance(ts, datetime):
        hour = ts.hour
    else:
        hour = int(str(ts).split()[1].split(':')[0]) if ' ' in str(ts) else 8

    # Elderly heart rate: higher baseline, wider swings
    if 5 <= hour < 7:
        base = 68
        spread = 14   # 68-82 (morning, lower after sleep but still higher than young)
    elif 7 <= hour < 9:
        base = 78
        spread = 18   # 78-96 (morning activity surge)
    elif 9 <= hour < 11:
        base = 82
        spread = 22   # 82-104 (late morning peak)
    elif 11 <= hour < 14:
        base = 84
        spread = 24   # 84-108 (midday highest)
    elif 14 <= hour < 17:
        base = 80
        spread = 20   # 80-100 (afternoon)
    elif 17 <= hour < 20:
        base = 76
        spread = 16   # 76-92 (evening wind-down)
    else:
        base = 70
        spread = 12   # 70-82 (night)

    val = base + random.randint(0, spread)

    # Occasional tachycardia spike (12% chance): +8-22 bpm
    if random.random() < 0.12:
        val += random.randint(8, 22)
        val = min(val, 115)

    # Occasional bradycardia dip (5% chance): -6-14 bpm
    if random.random() < 0.05:
        val -= random.randint(6, 14)
        val = max(val, 56)

    # Ensure no consecutive identical values
    attempts = 0
    while prev_hr is not None and val == prev_hr:
        val += random.choice([-4, -3, -2, -1, 1, 2, 3, 4, 5])
        val = max(56, min(115, val))
        attempts += 1
        if attempts > 20:
            break

    prev_hr = val
    cursor.execute("UPDATE heart_rate SET value = %s WHERE id = %s", (val, row_id))

conn.commit()
print(f"  Updated {len(rows)} heart_rate records")

# ==================== Blood Pressure ====================
# Elderly: higher values (hypertension common), more volatile, wider pulse pressure
print("Updating blood_pressure for elder_001...")
cursor.execute("SELECT id, bp_id, timestamp FROM blood_pressure WHERE elder_id = 'elder_001' ORDER BY timestamp")
rows = cursor.fetchall()

prev_sys = None
prev_dia = None
for row_id, bp_id, ts in rows:
    if isinstance(ts, datetime):
        hour = ts.hour
    else:
        hour = int(str(ts).split()[1].split(':')[0]) if ' ' in str(ts) else 8

    # Elderly BP: hypertension pattern, morning surge
    if 5 <= hour < 9:
        base_sys = 138
        spread_sys = 24   # 138-162 (morning surge - highest, common in elderly)
        base_dia = 86
        spread_dia = 12   # 86-98
    elif 9 <= hour < 12:
        base_sys = 134
        spread_sys = 20   # 134-154
        base_dia = 85
        spread_dia = 10   # 85-95
    elif 12 <= hour < 17:
        base_sys = 132
        spread_sys = 20   # 132-152 (afternoon)
        base_dia = 84
        spread_dia = 10   # 84-94
    else:
        base_sys = 130
        spread_sys = 18   # 130-148 (evening)
        base_dia = 83
        spread_dia = 8    # 83-91

    sys = base_sys + random.randint(0, spread_sys)
    dia = base_dia + random.randint(0, spread_dia)

    # Elderly often have wider pulse pressure (sys - dia): 35-68 mmHg
    pulse_pressure = sys - dia
    if pulse_pressure < 38:
        sys += random.randint(8, 15)
    elif pulse_pressure > 68:
        dia += random.randint(3, 8)

    # Occasional hypertension spike (15% chance)
    if random.random() < 0.15:
        sys += random.randint(5, 14)
        dia += random.randint(2, 6)
        sys = min(sys, 168)
        dia = min(dia, 102)

    # Ensure no consecutive identical systolic
    attempts = 0
    while prev_sys is not None and sys == prev_sys:
        sys += random.choice([-4, -3, -2, -1, 1, 2, 3, 4, 5, 6])
        sys = max(126, min(168, sys))
        attempts += 1
        if attempts > 20:
            break

    prev_sys = sys
    prev_dia = dia
    cursor.execute("UPDATE blood_pressure SET systolic = %s, diastolic = %s WHERE id = %s", (sys, dia, row_id))

conn.commit()
print(f"  Updated {len(rows)} blood_pressure records")

# ==================== Blood Oxygen ====================
# Elderly: slightly lower baseline, more variability, occasional desaturation
print("Updating blood_oxygen for elder_001...")
cursor.execute("SELECT id, bo_id, timestamp FROM blood_oxygen WHERE elder_id = 'elder_001' ORDER BY timestamp")
rows = cursor.fetchall()

prev_bo = None
for row_id, bo_id, ts in rows:
    if isinstance(ts, datetime):
        hour = ts.hour
    else:
        hour = int(str(ts).split()[1].split(':')[0]) if ' ' in str(ts) else 8

    # Elderly SpO2: lower baseline, more dips
    if 5 <= hour < 8:
        base = 93.5
        spread = 4.0   # 93.5-97.5 (morning, often lower)
    elif 8 <= hour < 12:
        base = 94.5
        spread = 4.0   # 94.5-98.5 (after activity, better oxygenation)
    elif 12 <= hour < 17:
        base = 94.0
        spread = 4.5   # 94.0-98.5 (afternoon)
    elif 17 <= hour < 21:
        base = 93.5
        spread = 3.5   # 93.5-97.0 (evening decline)
    else:
        base = 93.0
        spread = 3.0   # 93.0-96.0 (night lowest)

    val = round(base + random.uniform(0, spread), 1)

    # Occasional desaturation (10% chance): elderly may have brief dips
    if random.random() < 0.10:
        val = round(val - random.uniform(1.0, 3.5), 1)
        val = max(val, 90.0)

    # Occasional good reading (5% chance)
    if random.random() < 0.05:
        val = round(val + random.uniform(0.5, 1.5), 1)
        val = min(val, 99.5)

    # Ensure no consecutive identical values (minimum 0.3 difference)
    attempts = 0
    while prev_bo is not None and abs(val - prev_bo) < 0.3:
        val = round(val + random.choice([-0.8, -0.6, -0.4, 0.4, 0.6, 0.8, 1.0]), 1)
        val = max(90.0, min(99.5, val))
        attempts += 1
        if attempts > 20:
            break

    prev_bo = val
    cursor.execute("UPDATE blood_oxygen SET value = %s WHERE id = %s", (val, row_id))

conn.commit()
print(f"  Updated {len(rows)} blood_oxygen records")

cursor.close()
conn.close()
print("\nAll data updated successfully!")
print("Summary of changes:")
print("  - Body temperature: increased volatility, elderly circadian pattern, occasional fevers")
print("  - Heart rate: higher baseline (68-108), more variability, tachycardia spikes")
print("  - Blood pressure: elevated to elderly hypertension range (126-168/80-102)")
print("  - Blood oxygen: lower baseline (90-98.5), more desaturation dips")
print("  - All metrics: NO consecutive identical values")
