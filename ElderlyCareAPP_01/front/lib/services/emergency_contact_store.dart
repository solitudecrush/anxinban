import 'dart:convert';
import 'dart:math';

import 'package:shared_preferences/shared_preferences.dart';

import '../models/emergency_contact_entry.dart';

/// 本地持久化的多条 SOS 紧急联系人。
class EmergencyContactStore {
  EmergencyContactStore._();

  static const _kListJson = 'emergency_contacts_list_json';
  static const _kLegacyName = 'local_emergency_contact_name';
  static const _kLegacyPhone = 'local_emergency_contact_phone';
  static const _kInitialized = 'emergency_contacts_initialized';

  static String _newId() =>
      'ec_${DateTime.now().microsecondsSinceEpoch}_${Random().nextInt(1 << 30)}';

  static Future<List<EmergencyContactEntry>> loadAll() async {
    final p = await SharedPreferences.getInstance();

    // 首次使用：插入示例联系人
    final initialized = p.getBool(_kInitialized) ?? false;
    if (!initialized) {
      final demo = [
        EmergencyContactEntry(
          id: _newId(),
          name: '李叔叔',
          phone: '13800138000',
        ),
      ];
      await saveAll(demo);
      await p.setBool(_kInitialized, true);
      return demo;
    }

    final raw = p.getString(_kListJson);
    if (raw != null && raw.isNotEmpty) {
      try {
        final list = jsonDecode(raw) as List<dynamic>;
        return list
            .map((e) => EmergencyContactEntry.fromJson(
                  Map<String, dynamic>.from(e as Map),
                ))
            .toList();
      } catch (_) {}
    }
    final legacyName = p.getString(_kLegacyName) ?? '';
    final legacyPhone = p.getString(_kLegacyPhone) ?? '';
    if (legacyPhone.trim().isNotEmpty) {
      final migrated = [
        EmergencyContactEntry(
          id: _newId(),
          name: legacyName.trim(),
          phone: legacyPhone.trim(),
        ),
      ];
      await saveAll(migrated);
      await p.remove(_kLegacyName);
      await p.remove(_kLegacyPhone);
      return migrated;
    }
    return [];
  }

  static Future<void> saveAll(List<EmergencyContactEntry> items) async {
    final p = await SharedPreferences.getInstance();
    final raw = jsonEncode(items.map((e) => e.toJson()).toList());
    await p.setString(_kListJson, raw);
  }

  static Future<void> add(EmergencyContactEntry entry) async {
    final all = await loadAll();
    all.add(entry);
    await saveAll(all);
  }

  static Future<void> removeById(String id) async {
    final all = await loadAll();
    all.removeWhere((e) => e.id == id);
    await saveAll(all);
  }

  static Future<void> update(EmergencyContactEntry entry) async {
    final all = await loadAll();
    final i = all.indexWhere((e) => e.id == entry.id);
    if (i >= 0) {
      all[i] = entry;
      await saveAll(all);
    }
  }

  /// 用于 SOS：列表中的第一位（按当前保存顺序）。
  static Future<EmergencyContactEntry?> firstForSos() async {
    final all = await loadAll();
    if (all.isEmpty) {
      return null;
    }
    return all.first;
  }

  static EmergencyContactEntry createDraft({
    required String name,
    required String phone,
  }) {
    return EmergencyContactEntry(
      id: _newId(),
      name: name.trim(),
      phone: phone.trim(),
    );
  }
}
