import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// API 服务器地址配置。
///
/// 优先级（从高到低）：
/// 1. `--dart-define=API_BASE=http://你的云服务器IP:8080`（编译时注入，生产环境推荐）
/// 2. 运行时通过 [setBaseUrl] 设置（可在 App 内修改服务器地址）
/// 3. SharedPreferences 中保存的地址（通过 [init] 加载）
/// 4. 平台默认值（Android 模拟器用 10.0.2.2，iOS 模拟器用 127.0.0.1）
///
/// 真机通过 USB 连接 VSCode 调试时，使用方式：
/// ```bash
/// flutter run --dart-define=API_BASE=http://你的云服务器公网IP:8080
/// ```
///
/// 如果云服务器使用域名和 HTTPS：
/// ```bash
/// flutter run --dart-define=API_BASE=https://your-domain.com
/// ```
class ApiConfig {
  ApiConfig._();

  static const String _prefsKey = 'api_server_base_url';

  /// 编译时注入的地址（优先级最高）
  static String get _fromEnv {
    const env = String.fromEnvironment('API_BASE');
    return env;
  }

  /// 运行时自定义地址
  static String? _customBaseUrl;

  /// 从 SharedPreferences 加载的地址
  static String? _savedBaseUrl;

  /// 获取当前 API 服务器 base URL（不含尾部斜杠）。
  static String get baseUrl {
    // 1. 编译时注入（--dart-define=API_BASE=...）
    if (_fromEnv.isNotEmpty) {
      return _stripTrailingSlash(_fromEnv);
    }
    // 2. 运行时设置
    if (_customBaseUrl != null && _customBaseUrl!.isNotEmpty) {
      return _stripTrailingSlash(_customBaseUrl!);
    }
    // 3. SharedPreferences 中保存的地址
    if (_savedBaseUrl != null && _savedBaseUrl!.isNotEmpty) {
      return _stripTrailingSlash(_savedBaseUrl!);
    }
    // 4. 平台默认（仅开发环境使用）
    if (kIsWeb) {
      return 'http://localhost:8080';
    }
    if (Platform.isAndroid) {
      // Android 模拟器通过 10.0.2.2 访问宿主机
      // 真机调试请使用 --dart-define 指定云服务器地址
      return 'http://10.0.2.2:8080';
    }
    return 'http://127.0.0.1:8080';
  }

  /// 运行时动态设置服务器地址（优先级高于 SharedPreferences，低于 --dart-define）。
  static void setBaseUrl(String url) {
    _customBaseUrl = url;
  }

  /// 从 SharedPreferences 加载已保存的服务器地址。
  ///
  /// 应在 App 启动时调用（main.dart 中）。
  static Future<void> init() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      _savedBaseUrl = prefs.getString(_prefsKey);
    } catch (_) {
      // SharedPreferences 不可用时忽略
    }
  }

  /// 将服务器地址持久化保存到 SharedPreferences。
  ///
  /// 保存后下次启动时自动生效。
  static Future<void> saveBaseUrl(String url) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      if (url.isEmpty) {
        await prefs.remove(_prefsKey);
        _savedBaseUrl = null;
      } else {
        await prefs.setString(_prefsKey, url);
        _savedBaseUrl = url;
      }
    } catch (_) {
      // SharedPreferences 不可用时忽略
    }
  }

  /// 返回当前生效的服务器地址来源（用于调试显示）。
  static String get source {
    if (_fromEnv.isNotEmpty) return '编译参数 (--dart-define)';
    if (_customBaseUrl != null && _customBaseUrl!.isNotEmpty) return '运行时设置';
    if (_savedBaseUrl != null && _savedBaseUrl!.isNotEmpty) return '已保存配置';
    return '平台默认';
  }

  static String _stripTrailingSlash(String url) {
    return url.endsWith('/') ? url.substring(0, url.length - 1) : url;
  }
}
