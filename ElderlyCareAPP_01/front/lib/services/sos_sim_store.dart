import 'package:shared_preferences/shared_preferences.dart';

/// 存储 SOS 默认拨号卡槽设置（1 或 2，默认 1）。
class SosSimStore {
  SosSimStore._();

  static const _kSimSlot = 'sos_default_sim_slot';

  static Future<int> getDefaultSimSlot() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getInt(_kSimSlot) ?? 1;
  }

  static Future<void> setDefaultSimSlot(int slot) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setInt(_kSimSlot, slot);
  }
}
