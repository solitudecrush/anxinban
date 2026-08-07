#!/usr/bin/env python3
"""
传感器数据模拟 — 兼容入口 (调用新的分老人生成系统)

用法: python3 simulate_sensor_data.py
等价于: python3 run_all_simulations.py
"""

import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

if __name__ == '__main__':
    import run_all_simulations
    sys.exit(run_all_simulations.main())
