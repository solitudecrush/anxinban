import 'dart:io';

import 'package:flutter/foundation.dart';

/// 默认：Android 模拟器访问本机用 10.0.2.2；iOS 模拟器/桌面用 127.0.0.1。
/// 真机调试请使用 `--dart-define=API_BASE=http://你的电脑IP:8080`。
class ApiConfig {
  ApiConfig._();

  static String get baseUrl {
    const fromEnv = String.fromEnvironment('API_BASE');
    if (fromEnv.isNotEmpty) {
      return fromEnv;
    }
    if (kIsWeb) {
      return 'http://localhost:8080';
    }
    if (Platform.isAndroid) {
      return 'http://10.0.2.2:8080';
    }
    return 'http://127.0.0.1:8080';
  }
}
