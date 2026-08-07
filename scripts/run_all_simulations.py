#!/usr/bin/env python3
"""
一键运行全部5位老人的模拟数据生成, 写入数据库并更新SQL备份.

用法: python3 run_all_simulations.py
或单独运行: python3 generate_elder_001.py (仅该老人)
"""

import sys, os, importlib
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import pymysql
from elder_profiles import DB_CONFIG, print_statistics

MODULES = [
    ('generate_elder_001', '张国强', '高血压+糖尿病+中度失眠'),
    ('generate_elder_002', '李顺',   '心脏病+骨质疏松+轻度失眠'),
    ('generate_elder_003', '王兴国', '轻度认知障碍+轻度失眠'),
    ('generate_elder_004', '赵泽莲', '糖尿病+关节炎+重度失眠'),
    ('generate_elder_005', '孙锦年', '脑梗后遗症+高血压+中度失眠'),
]

def main():
    print("=" * 60)
    print("安心伴 — 全部老人传感器模拟数据生成")
    print("日期: 2026-07-07 ~ 2026-08-07 (32天 × 24h/天)")
    print("=" * 60)

    # ── 阶段0: 清空数据库 ──
    print("\n清空旧数据...")
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    cursor.execute("DELETE FROM sensor_data WHERE timestamp < '2026-07-07 00:00:00'")
    d1 = cursor.rowcount
    cursor.execute("DELETE FROM sensor_data WHERE timestamp >= '2026-07-07 00:00:00' AND timestamp <= '2026-08-07 23:59:59'")
    d2 = cursor.rowcount
    conn.commit()
    conn.close()
    print(f"  已清空: 7/7前 {d1} 条, 范围内 {d2} 条")

    # ── 阶段1: 逐位老人生成并写入 ──
    all_records = []
    for mod_name, name, history in MODULES:
        print(f"\n{'─'*60}")
        print(f"  {name} ({mod_name[-3:]}) — {history} | 严重焦虑")
        print(f"{'─'*60}")

        mod = importlib.import_module(mod_name)
        records = mod.generate()
        all_records.extend(records)
        print(f"  生成 {len(records)} 条记录")

        # 写入数据库（仅插入，不清除）
        conn = pymysql.connect(**DB_CONFIG)
        cursor = conn.cursor()
        sql = """INSERT INTO sensor_data
            (elder_id, device_id, sensor_type, value, unit, is_abnormal, timestamp, created_at, daily_tag, is_peak, measurement_source)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"""
        batch = [(r['elder_id'], r['device_id'], r['sensor_type'], r['value'], r['unit'],
                  r['is_abnormal'], r['timestamp'], r['created_at'], r['daily_tag'],
                  r['is_peak'], r['measurement_source']) for r in records]
        for i in range(0, len(batch), 500):
            cursor.executemany(sql, batch[i:i+500])
            conn.commit()
        conn.close()
        print(f"  ✓ 已写入数据库")

    # ── 阶段2: 验证 ──
    print(f"\n{'='*60}")
    print("数据质量验证")
    print(f"{'='*60}")

    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    total = len(all_records)
    expected = 5 * 5 * 32 * 24
    print(f"  {'✓' if total == expected else '✗'} 总记录: {total} (期望 {expected})")

    cursor.execute("SELECT MIN(timestamp), MAX(timestamp) FROM sensor_data")
    mn, mx = cursor.fetchone()
    print(f"  日期范围: {mn} ~ {mx}")

    cursor.execute("SELECT elder_id, COUNT(*) FROM sensor_data GROUP BY elder_id ORDER BY elder_id")
    for row in cursor.fetchall():
        ok = '✓' if row[1] == 5*32*24 else '✗'
        print(f"  {ok} {row[0]}: {row[1]} 条")

    print(f"\n  {'Sensor':22s} {'Count':>6s} {'Abn':>5s} {'Rate':>6s} {'Avg':>8s} {'Range'}")
    cursor.execute("SELECT sensor_type, COUNT(*), SUM(is_abnormal), ROUND(100*SUM(is_abnormal)/COUNT(*),1), ROUND(AVG(value),2), MIN(value), MAX(value) FROM sensor_data GROUP BY sensor_type ORDER BY sensor_type")
    for row in cursor.fetchall():
        abn_pct = str(row[3]) if row[3] is not None else '0'
        mn = float(row[5]) if row[5] is not None else 0
        mx = float(row[6]) if row[6] is not None else 0
        avg = float(row[4]) if row[4] is not None else 0
        print(f"  {row[0]:22s} {row[1]:6d} {row[2]:5d} {abn_pct:>5s}% {avg:8.2f} {mn:.1f}-{mx:.1f}")

    # 无连续相同值
    print(f"\n  连续相同值检查:")
    all_ok = True
    for st in ['heart_rate', 'spo2', 'temperature', 'blood_pressure_sys', 'blood_pressure_dia']:
        cursor.execute("""
            SELECT COUNT(*) FROM (
                SELECT value, LAG(value) OVER (PARTITION BY elder_id ORDER BY timestamp) as prev
                FROM sensor_data WHERE sensor_type = %s
            ) t WHERE value = prev
        """, (st,))
        n = cursor.fetchone()[0]
        s = '✓' if n == 0 else f'✗ ({n} pairs)'
        if n > 0: all_ok = False
        print(f"    {s} {st}")

    cursor.execute("SELECT COUNT(*) FROM elder_daily_stats")
    vr = cursor.fetchone()[0]
    print(f"\n  {'✓' if vr == 160 else '✗'} elder_daily_stats VIEW: {vr} 行 (期望 160)")

    conn.close()

    # ── 阶段3: 导出备份 ──
    print(f"\n{'='*60}")
    print("导出数据库备份...")
    import subprocess
    dump_path = "/var/www/anxinban-backend/docs/anxinban-db-full-20260704.sql"
    r = subprocess.run(['mysqldump', '-u', 'root', '-p1234', 'anxinban',
                        '--single-transaction', '--routines', '--triggers', '--events'],
                       capture_output=True, text=True)
    if r.returncode == 0:
        with open(dump_path, 'w') as f:
            f.write(r.stdout)
        print(f"  ✓ {dump_path} ({os.path.getsize(dump_path)/1024:.0f} KB)")
    else:
        print(f"  ✗ mysqldump 失败")

    print(f"\n{'='*60}")
    if all_ok and total == expected:
        print("✓ 全部完成! 数据质量验证通过.")
    else:
        print("⚠ 完成但有警告, 请检查输出.")
    print(f"{'='*60}")

if __name__ == '__main__':
    main()
