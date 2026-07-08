import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart';

import '../models/health_record.dart';

class HealthRecordStore {
  HealthRecordStore._();

  static const _kKey = 'health_record_json';

  static Future<HealthRecord> load() async {
    final p = await SharedPreferences.getInstance();
    final raw = p.getString(_kKey);
    if (raw != null && raw.isNotEmpty) {
      try {
        return HealthRecord.fromJson(
          Map<String, dynamic>.from(jsonDecode(raw) as Map),
        );
      } catch (_) {}
    }
    return HealthRecord();
  }

  static Future<void> save(HealthRecord record) async {
    final p = await SharedPreferences.getInstance();
    await p.setString(_kKey, jsonEncode(record.toJson()));
  }
}
