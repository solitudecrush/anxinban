import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/snapshot_record.dart';

/// 门外陌生人抓拍记录的本地持久化存储
/// 使用 SharedPreferences + JSON 编码，与项目现有模式保持一致
class SnapshotStore {
  SnapshotStore._();

  static const _kKey = 'snapshot_records_json';

  /// 加载所有抓拍记录（按时间倒序）
  static Future<List<SnapshotRecord>> loadAll() async {
    final p = await SharedPreferences.getInstance();
    final raw = p.getString(_kKey);
    if (raw == null || raw.isEmpty) return [];
    try {
      final list = jsonDecode(raw) as List<dynamic>;
      return list
          .map((e) => SnapshotRecord.fromJson(e as Map<String, dynamic>))
          .toList()
        ..sort((a, b) => b.capturedAt.compareTo(a.capturedAt));
    } catch (_) {
      return [];
    }
  }

  /// 保存所有抓拍记录
  static Future<void> saveAll(List<SnapshotRecord> records) async {
    final p = await SharedPreferences.getInstance();
    final list = records.map((r) => r.toJson()).toList();
    await p.setString(_kKey, jsonEncode(list));
  }

  /// 获取最新一条记录
  static Future<SnapshotRecord?> getLatest() async {
    final all = await loadAll();
    return all.isNotEmpty ? all.first : null;
  }

  /// 获取最近 N 条记录
  static Future<List<SnapshotRecord>> getRecent(int count) async {
    final all = await loadAll();
    return all.take(count).toList();
  }

  /// 初始化种子数据（仅在无数据时写入，用于首次启动演示）
  static Future<void> seedIfEmpty() async {
    final existing = await loadAll();
    if (existing.isNotEmpty) return;

    final now = DateTime.now();
    final seeds = [
      SnapshotRecord(
        id: 'snap_001',
        imageAssetPath: 'assets/snapshots/snapshot_1.png',
        capturedAt: now.subtract(const Duration(minutes: 15)),
      ),
      SnapshotRecord(
        id: 'snap_002',
        imageAssetPath: 'assets/snapshots/snapshot_2.png',
        capturedAt: now.subtract(const Duration(hours: 1, minutes: 30)),
      ),
      SnapshotRecord(
        id: 'snap_003',
        imageAssetPath: 'assets/snapshots/snapshot_3.png',
        capturedAt: now.subtract(const Duration(hours: 4)),
      ),
      SnapshotRecord(
        id: 'snap_004',
        imageAssetPath: 'assets/snapshots/snapshot_4.png',
        capturedAt: now.subtract(const Duration(hours: 7, minutes: 20)),
      ),
      SnapshotRecord(
        id: 'snap_005',
        imageAssetPath: 'assets/snapshots/snapshot_5.png',
        capturedAt: now.subtract(const Duration(hours: 12, minutes: 45)),
      ),
    ];
    await saveAll(seeds);
  }
}
