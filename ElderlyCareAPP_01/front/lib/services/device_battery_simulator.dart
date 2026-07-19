import 'dart:convert';
import 'dart:math';

import 'package:shared_preferences/shared_preferences.dart';

/// 单设备电池状态
class DeviceBatteryState {
  final String deviceId;
  final String deviceName;
  final String deviceType;
  final String location;
  final bool online;
  final int battery;
  final DateTime lastUpdate;

  const DeviceBatteryState({
    required this.deviceId,
    required this.deviceName,
    required this.deviceType,
    required this.location,
    required this.online,
    required this.battery,
    required this.lastUpdate,
  });

  Map<String, dynamic> toJson() => {
        'deviceId': deviceId,
        'deviceName': deviceName,
        'deviceType': deviceType,
        'location': location,
        'online': online,
        'battery': battery,
        'lastUpdate': lastUpdate.toIso8601String(),
      };

  factory DeviceBatteryState.fromJson(Map<String, dynamic> json) {
    return DeviceBatteryState(
      deviceId: json['deviceId'] as String,
      deviceName: json['deviceName'] as String,
      deviceType: json['deviceType'] as String,
      location: json['location'] as String? ?? '',
      online: json['online'] as bool? ?? true,
      battery: json['battery'] as int,
      lastUpdate: DateTime.parse(json['lastUpdate'] as String),
    );
  }
}

/// 设备电量模拟器
///
/// 模拟设备自然缓慢掉电以及用户不定时充电的逻辑：
/// - 每次打开页面时根据距上次查看的时间间隔计算掉电量
/// - 不同设备类型有不同的掉电速率
/// - 间隔超过 1 小时且电量偏低时，模拟充电行为（回到高位）
/// - 使用 SharedPreferences 持久化，关闭页面再打开也能看到变化
class DeviceBatterySimulator {
  static const _prefsKey = 'device_battery_states';
  static const minBattery = 30;
  static const maxBattery = 98;
  static const chargeThresholdHours = 1.0;
  static const chargeMinBattery = 70;

  final _rand = Random();

  /// 每种设备类型的每小时掉电百分比
  static double _drainRatePerHour(String deviceType) {
    switch (deviceType) {
      case '手环':
        return 4.0; // 小电池，传感器常开，掉电最快
      case '摄像头':
        return 2.5; // 偶尔推流
      case '门锁':
        return 1.5; // 极低功耗
      default:
        return 3.0;
    }
  }

  /// 从 SharedPreferences 加载上次保存的状态
  Future<Map<String, DeviceBatteryState>> _loadStates() async {
    final prefs = await SharedPreferences.getInstance();
    final raw = prefs.getString(_prefsKey);
    if (raw == null || raw.isEmpty) return {};
    try {
      final decoded = jsonDecode(raw) as Map<String, dynamic>;
      return decoded.map(
        (k, v) => MapEntry(k, DeviceBatteryState.fromJson(v as Map<String, dynamic>)),
      );
    } catch (_) {
      return {};
    }
  }

  /// 保存状态到 SharedPreferences
  Future<void> _saveStates(List<DeviceBatteryState> states) async {
    final prefs = await SharedPreferences.getInstance();
    final map = {for (final s in states) s.deviceId: s.toJson()};
    prefs.setString(_prefsKey, jsonEncode(map));
  }

  // ---------- public API ----------

  /// 根据从 API 获取的设备列表，计算并返回最新的电池状态。
  ///
  /// [apiDevices] 从后端 `/api/elder/{elderId}/devices` 获取，
  /// 包含 deviceId, deviceName, deviceType, location, status, batteryLevel。
  Future<List<DeviceBatteryState>> computeBatteryLevels(
    List<Map<String, dynamic>> apiDevices,
  ) async {
    final now = DateTime.now();
    final saved = await _loadStates();
    final results = <DeviceBatteryState>[];

    for (final d in apiDevices) {
      final id = d['deviceId'] as String? ?? '';
      final name = d['deviceName'] as String? ?? '';
      final type = d['deviceType'] as String? ?? '';
      final location = d['location'] as String? ?? '';
      final online = (d['status'] as String?) == 'online';
      final apiBattery = (d['batteryLevel'] as int?) ?? 80;

      final existing = saved[id];
      final int newBattery;

      if (existing == null) {
        // 首次打开：API 数据作为初始值
        newBattery = apiBattery.clamp(minBattery, maxBattery);
      } else {
        final elapsedHours =
            now.difference(existing.lastUpdate).inSeconds / 3600.0;

        if (elapsedHours >= chargeThresholdHours) {
          // 超过 1 小时：大概率模拟用户充过电
          if (_rand.nextDouble() < 0.7) {
            newBattery = chargeMinBattery +
                _rand.nextInt(maxBattery - chargeMinBattery + 1);
          } else {
            final drain = elapsedHours * _drainRatePerHour(type) * 0.5;
            newBattery =
                (existing.battery - drain).round().clamp(minBattery, maxBattery);
          }
        } else {
          // 正常掉电
          final drain = elapsedHours * _drainRatePerHour(type);
          final jitter = (_rand.nextDouble() - 0.5) * 2; // ±1%
          newBattery = (existing.battery - drain + jitter)
              .round()
              .clamp(minBattery, maxBattery);
        }
      }

      results.add(DeviceBatteryState(
        deviceId: id,
        deviceName: name,
        deviceType: type,
        location: location,
        online: online,
        battery: newBattery,
        lastUpdate: now,
      ));
    }

    await _saveStates(results);
    return results;
  }
}
