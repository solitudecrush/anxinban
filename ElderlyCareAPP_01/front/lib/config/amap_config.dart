import 'package:flutter/services.dart';

/// 在高德开放平台申请 Key：https://lbs.amap.com/dev/
///
/// 构建：`flutter run --dart-define=AMAP_ANDROID_KEY=xxx`
/// 或直接改默认值（勿将真实 Key 提交到公开仓库）。
class AmapConfig {
  AmapConfig._();

  static const String _defaultAndroidKey = String.fromEnvironment(
    'AMAP_ANDROID_KEY',
    defaultValue: 'YOUR_AMAP_ANDROID_KEY',
  );

  static String _androidKey = _defaultAndroidKey;

  static String get androidKey => _androidKey;

  static bool get keysLookUnset => _androidKey.contains('YOUR_');

  static const MethodChannel _channel = MethodChannel(
    'com.elderlycare.elderly_care_app/amap',
  );

  /// 从 Android Manifest 的 meta-data 中读取高德 Key。
  /// 在运行高德地图相关功能前调用一次即可。
  static Future<void> init() async {
    try {
      final key = await _channel.invokeMethod<String>('getApiKey');
      if (key != null && key.isNotEmpty && !key.contains('YOUR_')) {
        _androidKey = key;
      }
    } catch (_) {
      // 保持默认值或 dart-define 传入的值
    }
  }
}
