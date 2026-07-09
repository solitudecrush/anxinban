import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'app_shell.dart';
import 'config/api_config.dart';
import 'screens/auth_screen.dart';
import 'services/api_service.dart';
import 'state/nav_controller.dart';
import 'theme/app_theme.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const ElderlyCareApp());
}

class ElderlyCareApp extends StatelessWidget {
  const ElderlyCareApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        Provider(create: (_) => ApiService()),
        ChangeNotifierProvider(create: (_) => NavController()),
      ],
      child: MaterialApp(
        title: '健康助手',
        theme: AppTheme.light(),
        home: const _AuthGate(),
      ),
    );
  }
}

/// 登录状态闸门：已登录则进 AppShell，未登录则进 AuthScreen
class _AuthGate extends StatefulWidget {
  const _AuthGate();

  @override
  State<_AuthGate> createState() => _AuthGateState();
}

class _AuthGateState extends State<_AuthGate> {
  bool? _isLoggedIn;

  @override
  void initState() {
    super.initState();
    _initAndCheck();
  }

  Future<void> _initAndCheck() async {
    // 1. 加载已保存的 API 服务器地址
    await ApiConfig.init();

    // 2. 检查登录状态
    final prefs = await SharedPreferences.getInstance();
    bool loggedIn = prefs.getBool('app_is_logged_in') ?? false;

    // 3. 如果已登录，恢复 accessToken、userId 和 elderId 到 ApiService 中
    if (loggedIn && mounted) {
      final token = prefs.getString('app_access_token');
      final userId = prefs.getString('app_user_id');
      final elderId = prefs.getString('app_elder_id');
      final api = context.read<ApiService>();
      if (token != null && token.isNotEmpty) {
        api.setToken(token);
      }
      if (userId != null && userId.isNotEmpty) {
        api.setUserId(userId);
      }
      if (elderId != null && elderId.isNotEmpty) {
        api.setElderId(elderId);
      }

      // 4. 验证 token 是否仍然有效（尝试获取绑定老人信息）
      if (token != null && token.isNotEmpty && userId != null && userId.isNotEmpty) {
        try {
          final eid = await api.fetchAndSetElderId();
          if (eid != null && eid.isNotEmpty) {
            await prefs.setString('app_elder_id', eid);
          }
        } on ApiException catch (e) {
          // 业务错误（如"未绑定老人"）说明 token 有效，只是还没绑定老人
          // 不做处理，保持登录状态
        } catch (_) {
          // 网络错误（连接被拒绝、超时等）：服务器不可达，清除登录状态
          // 让用户重新登录（可能是服务器地址变了）
          await prefs.clear();
          loggedIn = false;
        }
      }
    }

    if (mounted) {
      setState(() => _isLoggedIn = loggedIn);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoggedIn == null) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }
    return _isLoggedIn! ? const AppShell() : const AuthScreen();
  }
}
