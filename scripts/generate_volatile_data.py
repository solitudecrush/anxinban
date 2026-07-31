#!/usr/bin/env python3
"""
Generate volatile health data for 2026-07-21 for 5 elders.
Key features:
- 0-8 AM nighttime: significant volatility (sleep stages, apnea events, bathroom visits)
- Realistic medical patterns for elderly with different health profiles
- Hourly data for all 4 tables (blood_pressure also hourly, not every 3h)
"""

import random
import math

random.seed(42)  # reproducible

DATE = "2026-07-21"
ELDERS = {
    "elder_001": {
        "profile": "Mild hypertension, occasional nighttime arrhythmia",
        "hr_base": 75, "hr_volatility": "high",
        "bp_sys_base": 135, "bp_dia_base": 85,
        "spo2_base": 97.0, "spo2_volatility": "moderate",
        "temp_base": 36.5, "temp_volatility": "moderate",
    },
    "elder_002": {
        "profile": "Moderate-severe hypertension, suspected sleep apnea",
        "hr_base": 72, "hr_volatility": "very_high",
        "bp_sys_base": 148, "bp_dia_base": 93,
        "spo2_base": 96.0, "spo2_volatility": "very_high",  # apnea events
        "temp_base": 36.6, "temp_volatility": "moderate",
    },
    "elder_003": {
        "profile": "Generally healthy, mild fluctuations",
        "hr_base": 72, "hr_volatility": "low",
        "bp_sys_base": 128, "bp_dia_base": 82,
        "spo2_base": 98.2, "spo2_volatility": "low",
        "temp_base": 36.3, "temp_volatility": "low",
    },
    "elder_004": {
        "profile": "Moderate condition, nocturnal BP non-dipper",
        "hr_base": 70, "hr_volatility": "moderate",
        "bp_sys_base": 132, "bp_dia_base": 84,
        "spo2_base": 97.0, "spo2_volatility": "moderate",
        "temp_base": 36.4, "temp_volatility": "moderate",
    },
    "elder_005": {
        "profile": "Poorly controlled hypertension, highest volatility, severe sleep disturbances",
        "hr_base": 88, "hr_volatility": "extreme",
        "bp_sys_base": 150, "bp_dia_base": 95,
        "spo2_base": 95.5, "spo2_volatility": "extreme",  # severe apnea
        "temp_base": 36.6, "temp_volatility": "high",
    },
}


def nighttime_volatility(hour, intensity):
    """
    Generate volatility multiplier based on time of day.
    0-8 AM nighttime: higher volatility (sleep stages, apnea, bathroom visits)
    Returns a random offset factor.
    """
    if 0 <= hour <= 3:
        # Deep sleep / REM cycles — irregular, unpredictable fluctuations
        base_vol = random.uniform(-intensity, intensity)
        # Occasional spike (REM sleep, brief awakening)
        if random.random() < 0.25:
            base_vol += random.uniform(-intensity * 0.8, intensity * 1.5)
    elif 4 <= hour <= 6:
        # Early morning surge — BP/HR rising but erratically
        base_vol = random.uniform(-intensity * 0.6, intensity * 1.2)
        if random.random() < 0.2:
            base_vol += random.uniform(0, intensity * 0.8)  # morning surge spike
    elif 7 <= hour <= 8:
        # Waking period — volatile transition
        base_vol = random.uniform(-intensity * 0.7, intensity * 0.9)
        if random.random() < 0.15:
            base_vol += random.uniform(-intensity * 0.5, intensity)
    else:
        # Daytime — moderate normal variation
        base_vol = random.uniform(-intensity * 0.5, intensity * 0.5)
    return base_vol


def apnea_event(hour, severity):
    """
    Simulate sleep apnea oxygen desaturation events.
    Most common during REM sleep (2-5 AM) and deep sleep (0-2 AM).
    """
    if 0 <= hour <= 5:
        # Nighttime — highest apnea risk
        if severity >= 0.7 and random.random() < 0.4:
            return random.uniform(-4.0, -1.5)  # significant desaturation
        elif severity >= 0.4 and random.random() < 0.3:
            return random.uniform(-2.5, -0.8)  # moderate desaturation
        elif random.random() < 0.15:
            return random.uniform(-1.5, -0.3)  # mild dip
    elif 6 <= hour <= 8:
        # Early morning — still some risk
        if severity >= 0.6 and random.random() < 0.2:
            return random.uniform(-2.0, -0.5)
    return 0


# ==================== Heart Rate Generator ====================
def ensure_diff(prev_val, new_val, min_diff=2):
    """Ensure new_val differs from prev_val by at least min_diff.
    If too close, bump away by a random amount in a random direction."""
    if prev_val is None or abs(new_val - prev_val) >= min_diff:
        return new_val
    direction = random.choice([-1, 1])
    return prev_val + direction * random.randint(min_diff, max(min_diff + 2, 5))


def generate_heart_rate():
    """Generate hourly heart rate data with STRICT no-consecutive-same rule."""
    volatility_map = {"low": 5, "moderate": 8, "high": 12, "very_high": 15, "extreme": 18}

    rows = []
    for elder_id, cfg in ELDERS.items():
        base = cfg["hr_base"]
        vol = volatility_map[cfg["hr_volatility"]]
        prev_val = None

        for hour in range(24):
            # Strong nighttime volatility (0-8 AM) — much more dramatic
            if 0 <= hour <= 3:
                # Deep sleep / REM — erratic fluctuations
                nv = random.uniform(-vol * 1.2, vol * 1.2)
                if random.random() < 0.35:
                    nv += random.choice([-1, 1]) * random.uniform(vol * 0.5, vol * 1.5)
            elif 4 <= hour <= 6:
                # Early morning surge — rapid spikes
                nv = random.uniform(-vol * 0.5, vol * 1.5)
                nv += random.uniform(2, 6)  # sympathetic surge
                if random.random() < 0.3:
                    nv += random.uniform(4, 10)  # extra spike
            elif 7 <= hour <= 8:
                # Waking — volatile transition
                nv = random.uniform(-vol * 0.8, vol * 1.0)
                if random.random() < 0.2:
                    nv += random.choice([-1, 1]) * random.uniform(3, 8)
            else:
                # Daytime
                nv = random.uniform(-vol * 0.6, vol * 0.6)

            # Mean reversion
            reversion = (base - (prev_val if prev_val else base)) * random.uniform(0.1, 0.4)

            # Apnea arousal tachycardia
            if elder_id in ("elder_002", "elder_005") and 0 <= hour <= 5:
                if random.random() < 0.35:
                    nv += random.uniform(6, 15)

            # Random jitter to prevent flat spots
            jitter = random.choice([-1, 1]) * random.randint(1, 3)

            raw = round(base + nv + reversion + jitter)

            # Physiological bounds
            upper = 145 if cfg["hr_volatility"] in ("extreme", "very_high") else 135
            raw = max(42, min(upper, raw))

            # STRICT: no consecutive same values (min diff = 2)
            value = ensure_diff(prev_val, raw, min_diff=random.randint(2, 5))
            # Re-clamp after adjustment
            value = max(42, min(upper, value))

            prev_val = value

            hr_id = f"hr_v3_{elder_id[-3:]}_{hour:02d}"
            rows.append((hr_id, elder_id, value, "次/分", f"{DATE} {hour:02d}:00:00", f"{DATE} {hour:02d}:01:00"))

    return rows


# ==================== Blood Pressure Generator ====================
def generate_blood_pressure():
    """Generate hourly blood pressure with no consecutive same values."""
    rows = []
    for elder_id, cfg in ELDERS.items():
        sys_base = cfg["bp_sys_base"]
        dia_base = cfg["bp_dia_base"]
        vol_map = {"low": 7, "moderate": 10, "high": 14, "very_high": 17, "extreme": 20}
        vol = vol_map[cfg["hr_volatility"]]

        prev_sys, prev_dia = None, None

        for hour in range(24):
            if elder_id != "elder_004":
                if 0 <= hour <= 3:
                    dip = random.uniform(10, 22)
                    sys_offset = -dip + random.uniform(-vol * 0.7, vol * 0.7)
                    dia_offset = -dip * 0.6 + random.uniform(-vol * 0.5, vol * 0.5)
                elif 4 <= hour <= 7:
                    surge = random.uniform(5, 16)
                    sys_offset = surge + random.uniform(-vol * 0.9, vol * 0.9)
                    dia_offset = surge * 0.5 + random.uniform(-vol * 0.5, vol * 0.5)
                else:
                    sys_offset = random.uniform(-vol * 0.6, vol * 0.6)
                    dia_offset = random.uniform(-vol * 0.4, vol * 0.4)
            else:
                sys_offset = random.uniform(-vol * 0.5, vol * 0.5)
                dia_offset = random.uniform(-vol * 0.3, vol * 0.3)

            sys_raw = round(sys_base + sys_offset + random.choice([-1, 1]) * random.randint(1, 4))
            dia_raw = round(dia_base + dia_offset + random.choice([-1, 1]) * random.randint(1, 3))

            systolic = ensure_diff(prev_sys, sys_raw, min_diff=random.randint(2, 6))
            diastolic = ensure_diff(prev_dia, dia_raw, min_diff=random.randint(1, 4))

            systolic = max(95, min(190, systolic))
            diastolic = max(55, min(115, diastolic))

            prev_sys, prev_dia = systolic, diastolic

            bp_id = f"bp_v3_{elder_id[-3:]}_{hour:02d}"
            rows.append((bp_id, elder_id, systolic, diastolic, f"{DATE} {hour:02d}:00:00", f"{DATE} {hour:02d}:01:00"))

    return rows


# ==================== Blood Oxygen Generator ====================
def generate_blood_oxygen():
    """Generate hourly SpO2 data with apnea events and no consecutive same values."""
    volatility_map = {"low": 0.6, "moderate": 1.2, "very_high": 2.0, "extreme": 3.0}
    severity_map = {"low": 0.2, "moderate": 0.5, "very_high": 0.75, "extreme": 0.9}

    rows = []
    for elder_id, cfg in ELDERS.items():
        base = cfg["spo2_base"]
        vol = volatility_map.get(cfg["spo2_volatility"], 0.8)
        sev = severity_map.get(cfg["spo2_volatility"], 0.3)
        prev_val = None

        for hour in range(24):
            offset = random.uniform(-vol * 0.4, vol * 0.4)
            offset += apnea_event(hour, sev)

            # Extra nighttime jitter (0-8 AM)
            if 0 <= hour <= 8:
                offset += random.uniform(-vol * 0.2, vol * 0.2)

            if 6 <= hour <= 8 and random.random() < 0.3:
                offset += random.uniform(0.3, 1.0)

            raw = round(base + offset, 1)
            raw = max(85.0, min(100.0, raw))

            # Ensure diff from previous
            if prev_val is not None and abs(raw - prev_val) < 0.2:
                raw = prev_val + random.choice([-1, 1]) * random.uniform(0.3, 1.0)
                raw = max(85.0, min(100.0, round(raw, 1)))

            prev_val = raw

            bo_id = f"bo_v3_{elder_id[-3:]}_{hour:02d}"
            rows.append((bo_id, elder_id, raw, "%", f"{DATE} {hour:02d}:00:00", f"{DATE} {hour:02d}:01:00"))

    return rows


# ==================== Body Temperature Generator ====================
def generate_body_temperature():
    """Generate hourly temperature with no consecutive same values."""
    volatility_map = {"low": 0.2, "moderate": 0.3, "high": 0.45}

    rows = []
    for elder_id, cfg in ELDERS.items():
        base = cfg["temp_base"]
        vol = volatility_map.get(cfg["temp_volatility"], 0.25)
        prev_val = None

        for hour in range(24):
            circadian = -0.4 * math.cos(2 * math.pi * (hour - 4) / 24)

            # More micro-fluctuations, especially at night
            if 0 <= hour <= 5:
                micro = random.uniform(-0.25, 0.25)
                if random.random() < 0.25:
                    micro += random.choice([-1, 1]) * random.uniform(0.1, 0.2)
            elif 6 <= hour <= 8:
                micro = random.uniform(-0.2, 0.3)
            else:
                micro = random.uniform(-0.15, 0.15)

            raw = round(base + circadian + micro, 1)
            raw = max(35.5, min(37.8, raw))

            # No consecutive same
            if prev_val is not None and abs(raw - prev_val) < 0.05:
                raw = prev_val + random.choice([-1, 1]) * random.uniform(0.1, 0.3)
                raw = max(35.5, min(37.8, round(raw, 1)))

            prev_val = raw

            bt_id = f"bt_v3_{elder_id[-3:]}_{hour:02d}"
            rows.append((bt_id, elder_id, raw, "℃", f"{DATE} {hour:02d}:00:00", f"{DATE} {hour:02d}:01:00"))

    return rows


# ==================== SQL Generation ====================
def generate_sql():
    hr_data = generate_heart_rate()
    bp_data = generate_blood_pressure()
    bo_data = generate_blood_oxygen()
    bt_data = generate_body_temperature()

    sql = []
    sql.append("-- ===================================================")
    sql.append("-- Volatile health data for 2026-07-21 (yesterday)")
    sql.append(f"-- Generated: {DATE}")
    sql.append("-- Features: enhanced nighttime (0-8 AM) volatility,")
    sql.append("--   sleep apnea events, early morning BP surge,")
    sql.append("--   HR arrhythmia spikes, circadian temp variation")
    sql.append("-- ===================================================")
    sql.append("")

    # Delete old 2026-07-21 data
    sql.append("-- Clear existing 2026-07-21 data")
    sql.append(f"DELETE FROM body_temperature WHERE timestamp >= '{DATE} 00:00:00' AND timestamp <= '{DATE} 23:59:59';")
    sql.append(f"DELETE FROM blood_oxygen WHERE timestamp >= '{DATE} 00:00:00' AND timestamp <= '{DATE} 23:59:59';")
    sql.append(f"DELETE FROM blood_pressure WHERE timestamp >= '{DATE} 00:00:00' AND timestamp <= '{DATE} 23:59:59';")
    sql.append(f"DELETE FROM heart_rate WHERE timestamp >= '{DATE} 00:00:00' AND timestamp <= '{DATE} 23:59:59';")
    sql.append("")

    # Heart Rate INSERT
    sql.append("-- ==================== Heart Rate (心率) — 每小时一条 ====================")
    sql.append("INSERT INTO heart_rate (hr_id, elder_id, value, unit, timestamp, created_at) VALUES")
    hr_lines = [f"('{r[0]}', '{r[1]}', {r[2]}, '{r[3]}', '{r[4]}', '{r[5]}')" for r in hr_data]
    sql.append(",\n".join(hr_lines) + ";")
    sql.append("")

    # Blood Pressure INSERT
    sql.append("-- ==================== Blood Pressure (血压) — 每小时一条 ====================")
    sql.append("INSERT INTO blood_pressure (bp_id, elder_id, systolic, diastolic, timestamp, created_at) VALUES")
    bp_lines = [f"('{r[0]}', '{r[1]}', {r[2]}, {r[3]}, '{r[4]}', '{r[5]}')" for r in bp_data]
    sql.append(",\n".join(bp_lines) + ";")
    sql.append("")

    # Blood Oxygen INSERT
    sql.append("-- ==================== Blood Oxygen (血氧) — 每小时一条 ====================")
    sql.append("INSERT INTO blood_oxygen (bo_id, elder_id, value, unit, timestamp, created_at) VALUES")
    bo_lines = [f"('{r[0]}', '{r[1]}', {r[2]}, '{r[3]}', '{r[4]}', '{r[5]}')" for r in bo_data]
    sql.append(",\n".join(bo_lines) + ";")
    sql.append("")

    # Body Temperature INSERT
    sql.append("-- ==================== Body Temperature (体温) — 每小时一条 ====================")
    sql.append("INSERT INTO body_temperature (bt_id, elder_id, value, unit, timestamp, created_at) VALUES")
    bt_lines = [f"('{r[0]}', '{r[1]}', {r[2]}, '{r[3]}', '{r[4]}', '{r[5]}')" for r in bt_data]
    sql.append(",\n".join(bt_lines) + ";")
    sql.append("")

    return "\n".join(sql), hr_data, bp_data, bo_data, bt_data


if __name__ == "__main__":
    sql, hr, bp, bo, bt = generate_sql()

    # Write SQL file
    output_path = "/var/www/anxinban-backend/scripts/insert_volatile_health_data.sql"
    with open(output_path, "w", encoding="utf-8") as f:
        f.write(sql)

    # Print summary
    print(f"✅ SQL file written: {output_path}")
    print(f"\n📊 Data Summary:")
    print(f"   heart_rate:   {len(hr)} rows ({len(hr)//5} per elder)")
    print(f"   blood_pressure: {len(bp)} rows ({len(bp)//5} per elder) — NOW HOURLY")
    print(f"   blood_oxygen: {len(bo)} rows ({len(bo)//5} per elder)")
    print(f"   body_temperature: {len(bt)} rows ({len(bt)//5} per elder)")

    # Print volatility demo for elder_001 heart_rate
    print(f"\n🔍 Heart Rate preview (elder_001):")
    for r in hr[:24]:
        print(f"   {r[4][-8:]} → {r[2]:4d} bpm")

    print(f"\n🔍 Heart Rate preview (elder_005 - extreme volatility):")
    for r in hr[96:120]:
        print(f"   {r[4][-8:]} → {r[2]:4d} bpm")

    print(f"\n🫁 Blood Oxygen preview (elder_002 - suspected apnea):")
    for r in bo[24:48]:
        print(f"   {r[4][-8:]} → {r[2]:5.1f}%")

    print(f"\n💓 Blood Pressure preview (elder_005 - poorly controlled):")
    for r in bp[96:120]:
        print(f"   {r[4][-8:]} → {r[2]:3d}/{r[3]:3d}")
