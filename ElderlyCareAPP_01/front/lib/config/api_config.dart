import 'package:shared_preferences/shared_preferences.dart';

/// API 服务器地址配置。
///
/// 优先级（从高到低）：
/// 1. `--dart-define=API_BASE=http://你的云服务器IP:8080`（编译时注入）
/// 2. 运行时通过 [setBaseUrl] 设置（可在 App 内修改服务器地址）
/// 3. SharedPreferences 中保存的地址（通过 [init] 加载）
/// 4. 代码中硬编码的 [_productionUrl]（APK 默认直连云服务器）
///
/// 构建 Release APK 时也可通过 --dart-define 覆盖：
/// ```bash
/// flutter build apk --release --dart-define=API_BASE=http://你的云服务器公网IP:8080
/// ```
class ApiConfig {
  ApiConfig._();

  static const String _prefsKey = 'api_server_base_url';

  /// ★ 生产环境云服务器默认地址（APK 直连）。
  ///
  /// 打包前修改为你的云服务器实际地址（IP 或域名），例如：
  /// ```dart
  /// static const _productionUrl = 'http://120.27.129.78:8080';
  /// ```
  /// 如果服务器配置了 HTTPS，改为 `https://your-domain.com`。
  ///
  /// 优先级最低，可被 SharedPreferences / 运行时设置 / --dart-define 覆盖。
  static const _productionUrl = 'http://120.27.129.78:8080';

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
    // 4. 生产环境默认地址（APK 直连云服务器）
    // 真机运行时默认连接 _productionUrl，无需额外配置
    return _stripTrailingSlash(_productionUrl);
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
