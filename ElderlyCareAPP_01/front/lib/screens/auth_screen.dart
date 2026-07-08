import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../app_shell.dart';
import '../services/api_service.dart';

enum AuthPage { login, register, forgot }

class AuthScreen extends StatefulWidget {
  const AuthScreen({super.key});

  @override
  State<AuthScreen> createState() => _AuthScreenState();
}

class _AuthScreenState extends State<AuthScreen>
    with SingleTickerProviderStateMixin {
  AuthPage _page = AuthPage.login;

  // Login
  final _loginPhoneCtrl = TextEditingController();
  final _loginPwdCtrl = TextEditingController();
  bool _loginRemember = false;

  // Register
  final _regPhoneCtrl = TextEditingController();
  final _regCodeCtrl = TextEditingController();
  final _regPwdCtrl = TextEditingController();
  final _regPwd2Ctrl = TextEditingController();

  // Forgot
  final _forgotPhoneCtrl = TextEditingController();
  final _forgotCodeCtrl = TextEditingController();
  final _forgotPwdCtrl = TextEditingController();
  final _forgotPwd2Ctrl = TextEditingController();

  // Countdown
  int _regCountdown = 0;
  int _forgotCountdown = 0;
  Timer? _regTimer;
  Timer? _forgotTimer;

  bool _obscureLogin = true;
  bool _obscureReg = true;
  bool _obscureReg2 = true;
  bool _obscureForgot = true;
  bool _obscureForgot2 = true;

  late AnimationController _animCtrl;
  late Animation<double> _fadeAnim;

  @override
  void initState() {
    super.initState();
    _animCtrl = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 350),
    );
    _fadeAnim = CurvedAnimation(parent: _animCtrl, curve: Curves.easeOut);
    _animCtrl.forward();
    _loadSavedLogin();
  }

  @override
  void dispose() {
    _animCtrl.dispose();
    _loginPhoneCtrl.dispose();
    _loginPwdCtrl.dispose();
    _regPhoneCtrl.dispose();
    _regCodeCtrl.dispose();
    _regPwdCtrl.dispose();
    _regPwd2Ctrl.dispose();
    _forgotPhoneCtrl.dispose();
    _forgotCodeCtrl.dispose();
    _forgotPwdCtrl.dispose();
    _forgotPwd2Ctrl.dispose();
    _regTimer?.cancel();
    _forgotTimer?.cancel();
    super.dispose();
  }

  Future<void> _loadSavedLogin() async {
    final prefs = await SharedPreferences.getInstance();
    final saved = prefs.getString('app_login_remember');
    if (saved != null) {
      final parts = saved.split('|');
      if (parts.length == 2) {
        _loginPhoneCtrl.text = parts[0];
        _loginPwdCtrl.text = parts[1];
        _loginRemember = true;
        if (mounted) setState(() {});
      }
    }
  }

  void _switchPage(AuthPage page) {
    _animCtrl.reverse().then((_) {
      setState(() => _page = page);
      _animCtrl.forward();
    });
  }

  void _startCountdown(bool isReg) {
    final timer = isReg ? _regTimer : _forgotTimer;
    timer?.cancel();

    if (isReg) {
      _regCountdown = 60;
      _regTimer = Timer.periodic(const Duration(seconds: 1), (_) {
        if (!mounted) return;
        setState(() {
          _regCountdown--;
          if (_regCountdown <= 0) _regTimer?.cancel();
        });
      });
    } else {
      _forgotCountdown = 60;
      _forgotTimer = Timer.periodic(const Duration(seconds: 1), (_) {
        if (!mounted) return;
        setState(() {
          _forgotCountdown--;
          if (_forgotCountdown <= 0) _forgotTimer?.cancel();
        });
      });
    }
  }

  Future<void> _sendCode(bool isReg) async {
    final phone = isReg ? _regPhoneCtrl.text.trim() : _forgotPhoneCtrl.text.trim();
    if (phone.length != 11) {
      _showSnack('请输入正确的11位手机号');
      return;
    }
    _showSnack('验证码：123456');
    _startCountdown(isReg);
  }

  Future<void> _doLogin() async {
    final phone = _loginPhoneCtrl.text.trim();
    final pwd = _loginPwdCtrl.text;

    if (phone.isEmpty || pwd.isEmpty) {
      _showSnack('请输入手机号和密码');
      return;
    }
    if (pwd.length < 6) {
      _showSnack('密码长度不能少于6位');
      return;
    }

    try {
      final api = context.read<ApiService>();
      final result = await api.login(phone, pwd, userType: 'family');
      final prefs = await SharedPreferences.getInstance();
      await prefs.setBool('app_is_logged_in', true);
      await prefs.setString('app_login_phone', phone);
      await prefs.setString('app_access_token', result['accessToken'] ?? '');
      await prefs.setString('app_user_id', result['userId'] ?? '');
      await prefs.setString('app_user_name', result['name'] ?? '');
      await prefs.setString('app_user_role', result['role'] ?? '');
      final avatar = result['avatar'];
      if (avatar != null && avatar is String && avatar.isNotEmpty) {
        await prefs.setString('profile_avatar', avatar);
      }
      if (_loginRemember) {
        await prefs.setString('app_login_remember', '$phone|$pwd');
      } else {
        await prefs.remove('app_login_remember');
      }
      if (!mounted) return;
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (_) => const AppShell()),
      );
    } catch (e) {
      _showSnack(e.toString());
    }
  }

  Future<void> _doRegister() async {
    final phone = _regPhoneCtrl.text.trim();
    final code = _regCodeCtrl.text.trim();
    final pwd = _regPwdCtrl.text;
    final pwd2 = _regPwd2Ctrl.text;

    if (phone.length != 11) { _showSnack('请输入正确的11位手机号'); return; }
    if (code != '123456') { _showSnack('验证码错误，演示验证码：123456'); return; }
    if (pwd.length < 6) { _showSnack('密码长度不能少于6位'); return; }
    if (pwd != pwd2) { _showSnack('两次输入的密码不一致'); return; }

    try {
      final api = context.read<ApiService>();
      await api.register({
        'name': phone,
        'phone': phone,
        'password': pwd,
        'verifyCode': code,
        'userType': 'family',
      });
      _showSnack('注册成功！请登录');
      _loginPhoneCtrl.text = phone;
      _loginPwdCtrl.text = pwd;
      _switchPage(AuthPage.login);
    } catch (e) {
      _showSnack(e.toString());
    }
  }

  Future<void> _doForgot() async {
    final phone = _forgotPhoneCtrl.text.trim();
    final code = _forgotCodeCtrl.text.trim();
    final pwd = _forgotPwdCtrl.text;
    final pwd2 = _forgotPwd2Ctrl.text;

    if (phone.length != 11) { _showSnack('请输入正确的11位手机号'); return; }
    if (code != '123456') { _showSnack('验证码错误，演示验证码：123456'); return; }
    if (pwd.length < 6) { _showSnack('密码长度不能少于6位'); return; }
    if (pwd != pwd2) { _showSnack('两次输入的密码不一致'); return; }

    try {
      final api = context.read<ApiService>();
      await api.resetPassword(phone, pwd);
      _showSnack('密码重置成功！请登录');
      _loginPhoneCtrl.text = phone;
      _loginPwdCtrl.text = pwd;
      _switchPage(AuthPage.login);
    } catch (e) {
      _showSnack(e.toString());
    }
  }

  void _showSnack(String msg) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(msg), duration: const Duration(seconds: 2)),
    );
  }

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.sizeOf(context);
    final isWide = size.width > 800;

    return Scaffold(
      body: isWide ? _buildWideLayout() : _buildNarrowLayout(),
    );
  }

  // ========== 宽屏布局（左右分栏） ==========
  Widget _buildWideLayout() {
    return Row(
      children: [
        // 左侧插图
        Expanded(
          flex: 5,
          child: Container(
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                colors: [Color(0xFF1a365d), Color(0xFF2c5282), Color(0xFF3182ce)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
            ),
            child: Center(
              child: FadeTransition(
                opacity: _fadeAnim,
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Text('👴🏠❤️', style: TextStyle(fontSize: 100)),
                    const SizedBox(height: 32),
                    const Text(
                      '智慧养老服务平台',
                      style: TextStyle(
                        fontSize: 32,
                        fontWeight: FontWeight.w600,
                        color: Colors.white,
                      ),
                    ),
                    const SizedBox(height: 16),
                    const Text(
                      '科技赋能养老，数据守护健康\n让每一位老人都能享受有尊严、有品质的晚年生活',
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        fontSize: 15,
                        color: Colors.white70,
                        height: 1.8,
                      ),
                    ),
                    const SizedBox(height: 48),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        _featureIcon(Icons.monitor_heart_outlined, '健康监测'),
                        const SizedBox(width: 40),
                        _featureIcon(Icons.videocam_outlined, '实时监控'),
                        const SizedBox(width: 40),
                        _featureIcon(Icons.assignment_turned_in_outlined, '服务申请'),
                        const SizedBox(width: 40),
                        _featureIcon(Icons.notifications_active_outlined, '智能告警'),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
        // 右侧表单
        Container(
          width: 480,
          color: Colors.white,
          padding: const EdgeInsets.symmetric(horizontal: 64, vertical: 48),
          child: Center(child: _buildFormPanel()),
        ),
      ],
    );
  }

  // ========== 窄屏布局（上下分栏） ==========
  Widget _buildNarrowLayout() {
    return SingleChildScrollView(
      child: Column(
        children: [
          // 顶部插图
          Container(
            width: double.infinity,
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                colors: [Color(0xFF1a365d), Color(0xFF2c5282), Color(0xFF3182ce)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
            ),
            padding: const EdgeInsets.symmetric(vertical: 48, horizontal: 32),
            child: Column(
              children: [
                const Text('👴🏠❤️', style: TextStyle(fontSize: 72)),
                const SizedBox(height: 20),
                const Text(
                  '智慧养老服务平台',
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.w600,
                    color: Colors.white,
                  ),
                ),
                const SizedBox(height: 8),
                const Text(
                  '科技赋能养老，数据守护健康',
                  style: TextStyle(fontSize: 13, color: Colors.white70),
                ),
              ],
            ),
          ),
          // 表单
          Container(
            color: Colors.white,
            padding: const EdgeInsets.all(32),
            child: _buildFormPanel(),
          ),
        ],
      ),
    );
  }

  Widget _featureIcon(IconData icon, String label) {
    return Column(
      children: [
        Icon(icon, color: Colors.white70, size: 28),
        const SizedBox(height: 8),
        Text(label, style: const TextStyle(color: Colors.white60, fontSize: 12)),
      ],
    );
  }

  // ========== 表单面板 ==========
  Widget _buildFormPanel() {
    switch (_page) {
      case AuthPage.login:
        return _buildLoginPanel();
      case AuthPage.register:
        return _buildRegisterPanel();
      case AuthPage.forgot:
        return _buildForgotPanel();
    }
  }

  Widget _buildLoginPanel() {
    return FadeTransition(
      opacity: _fadeAnim,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text('欢迎登录', style: TextStyle(fontSize: 28, fontWeight: FontWeight.w600, color: Color(0xFF1a202c))),
          const SizedBox(height: 8),
          const Text('请使用您的家属账号登录系统', style: TextStyle(fontSize: 14, color: Color(0xFF718096))),
          const SizedBox(height: 36),
          _buildTextField(
            label: '手机号',
            controller: _loginPhoneCtrl,
            hint: '请输入手机号',
            keyboard: TextInputType.phone,
            prefix: Icons.phone_android_outlined,
            inputFormatters: [FilteringTextInputFormatter.digitsOnly],
            maxLength: 11,
          ),
          const SizedBox(height: 18),
          _buildTextField(
            label: '密码',
            controller: _loginPwdCtrl,
            hint: '请输入密码',
            obscure: _obscureLogin,
            prefix: Icons.lock_outline,
            suffix: IconButton(
              icon: Icon(_obscureLogin ? Icons.visibility_off_outlined : Icons.visibility_outlined, size: 20, color: Colors.grey),
              onPressed: () => setState(() => _obscureLogin = !_obscureLogin),
            ),
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              Checkbox(
                value: _loginRemember,
                onChanged: (v) => setState(() => _loginRemember = v ?? false),
                activeColor: const Color(0xFF3182ce),
                materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
              ),
              const Text('记住密码', style: TextStyle(fontSize: 13, color: Color(0xFF4a5568))),
              const Spacer(),
              TextButton(
                onPressed: () => _switchPage(AuthPage.forgot),
                style: TextButton.styleFrom(foregroundColor: const Color(0xFF3182ce), padding: EdgeInsets.zero, minimumSize: Size.zero),
                child: const Text('忘记密码？', style: TextStyle(fontSize: 13)),
              ),
            ],
          ),
          const SizedBox(height: 8),
          SizedBox(
            width: double.infinity,
            height: 48,
            child: ElevatedButton(
              onPressed: _doLogin,
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF3182ce),
                foregroundColor: Colors.white,
                elevation: 0,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                textStyle: const TextStyle(fontSize: 15, fontWeight: FontWeight.w600),
              ),
              child: const Text('登 录'),
            ),
          ),
          const SizedBox(height: 24),
          Center(
            child: GestureDetector(
              onTap: () => _switchPage(AuthPage.register),
              child: RichText(
                text: const TextSpan(
                  text: '还没有账号？',
                  style: TextStyle(fontSize: 13, color: Color(0xFF718096)),
                  children: [
                    TextSpan(
                      text: '注册账号',
                      style: TextStyle(fontSize: 13, color: Color(0xFF3182ce), fontWeight: FontWeight.w600),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRegisterPanel() {
    return FadeTransition(
      opacity: _fadeAnim,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text('注册账号', style: TextStyle(fontSize: 28, fontWeight: FontWeight.w600, color: Color(0xFF1a202c))),
          const SizedBox(height: 8),
          const Text('创建家属账号，关爱老人健康', style: TextStyle(fontSize: 14, color: Color(0xFF718096))),
          const SizedBox(height: 36),
          _buildTextField(
            label: '手机号',
            controller: _regPhoneCtrl,
            hint: '请输入手机号',
            keyboard: TextInputType.phone,
            prefix: Icons.phone_android_outlined,
            inputFormatters: [FilteringTextInputFormatter.digitsOnly],
            maxLength: 11,
          ),
          const SizedBox(height: 18),
          _buildCodeRow(
            controller: _regCodeCtrl,
            hint: '请输入验证码',
            countdown: _regCountdown,
            onSend: () => _sendCode(true),
          ),
          const SizedBox(height: 18),
          _buildTextField(
            label: '密码',
            controller: _regPwdCtrl,
            hint: '请设置密码（至少6位）',
            obscure: _obscureReg,
            prefix: Icons.lock_outline,
            suffix: IconButton(
              icon: Icon(_obscureReg ? Icons.visibility_off_outlined : Icons.visibility_outlined, size: 20, color: Colors.grey),
              onPressed: () => setState(() => _obscureReg = !_obscureReg),
            ),
          ),
          const SizedBox(height: 18),
          _buildTextField(
            label: '确认密码',
            controller: _regPwd2Ctrl,
            hint: '请再次输入密码',
            obscure: _obscureReg2,
            prefix: Icons.lock_outline,
            suffix: IconButton(
              icon: Icon(_obscureReg2 ? Icons.visibility_off_outlined : Icons.visibility_outlined, size: 20, color: Colors.grey),
              onPressed: () => setState(() => _obscureReg2 = !_obscureReg2),
            ),
          ),
          const SizedBox(height: 28),
          SizedBox(
            width: double.infinity,
            height: 48,
            child: ElevatedButton(
              onPressed: _doRegister,
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF3182ce),
                foregroundColor: Colors.white,
                elevation: 0,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                textStyle: const TextStyle(fontSize: 15, fontWeight: FontWeight.w600),
              ),
              child: const Text('注 册'),
            ),
          ),
          const SizedBox(height: 24),
          Center(
            child: GestureDetector(
              onTap: () => _switchPage(AuthPage.login),
              child: RichText(
                text: const TextSpan(
                  text: '已有账号？',
                  style: TextStyle(fontSize: 13, color: Color(0xFF718096)),
                  children: [
                    TextSpan(
                      text: '去登录',
                      style: TextStyle(fontSize: 13, color: Color(0xFF3182ce), fontWeight: FontWeight.w600),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildForgotPanel() {
    return FadeTransition(
      opacity: _fadeAnim,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text('重置密码', style: TextStyle(fontSize: 28, fontWeight: FontWeight.w600, color: Color(0xFF1a202c))),
          const SizedBox(height: 8),
          const Text('通过手机号验证重置您的密码', style: TextStyle(fontSize: 14, color: Color(0xFF718096))),
          const SizedBox(height: 36),
          _buildTextField(
            label: '手机号',
            controller: _forgotPhoneCtrl,
            hint: '请输入手机号',
            keyboard: TextInputType.phone,
            prefix: Icons.phone_android_outlined,
            inputFormatters: [FilteringTextInputFormatter.digitsOnly],
            maxLength: 11,
          ),
          const SizedBox(height: 18),
          _buildCodeRow(
            controller: _forgotCodeCtrl,
            hint: '请输入验证码',
            countdown: _forgotCountdown,
            onSend: () => _sendCode(false),
          ),
          const SizedBox(height: 18),
          _buildTextField(
            label: '新密码',
            controller: _forgotPwdCtrl,
            hint: '请设置新密码（至少6位）',
            obscure: _obscureForgot,
            prefix: Icons.lock_outline,
            suffix: IconButton(
              icon: Icon(_obscureForgot ? Icons.visibility_off_outlined : Icons.visibility_outlined, size: 20, color: Colors.grey),
              onPressed: () => setState(() => _obscureForgot = !_obscureForgot),
            ),
          ),
          const SizedBox(height: 18),
          _buildTextField(
            label: '确认新密码',
            controller: _forgotPwd2Ctrl,
            hint: '请再次输入新密码',
            obscure: _obscureForgot2,
            prefix: Icons.lock_outline,
            suffix: IconButton(
              icon: Icon(_obscureForgot2 ? Icons.visibility_off_outlined : Icons.visibility_outlined, size: 20, color: Colors.grey),
              onPressed: () => setState(() => _obscureForgot2 = !_obscureForgot2),
            ),
          ),
          const SizedBox(height: 28),
          SizedBox(
            width: double.infinity,
            height: 48,
            child: ElevatedButton(
              onPressed: _doForgot,
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF3182ce),
                foregroundColor: Colors.white,
                elevation: 0,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                textStyle: const TextStyle(fontSize: 15, fontWeight: FontWeight.w600),
              ),
              child: const Text('重 置 密 码'),
            ),
          ),
          const SizedBox(height: 24),
          Center(
            child: TextButton(
              onPressed: () => _switchPage(AuthPage.login),
              style: TextButton.styleFrom(foregroundColor: const Color(0xFF3182ce)),
              child: const Text('返回登录', style: TextStyle(fontSize: 13)),
            ),
          ),
        ],
      ),
    );
  }

  // ========== 通用组件 ==========

  Widget _buildTextField({
    required String label,
    required TextEditingController controller,
    required String hint,
    required IconData prefix,
    TextInputType keyboard = TextInputType.text,
    bool obscure = false,
    Widget? suffix,
    List<TextInputFormatter>? inputFormatters,
    int? maxLength,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w500, color: Color(0xFF4a5568))),
        const SizedBox(height: 6),
        TextField(
          controller: controller,
          keyboardType: keyboard,
          obscureText: obscure,
          inputFormatters: inputFormatters,
          maxLength: maxLength,
          decoration: InputDecoration(
            hintText: hint,
            hintStyle: const TextStyle(fontSize: 14, color: Color(0xFFa0aec0)),
            prefixIcon: Icon(prefix, size: 20, color: const Color(0xFFa0aec0)),
            suffixIcon: suffix,
            filled: true,
            fillColor: const Color(0xFFf7fafc),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Color(0xFFe2e8f0)),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Color(0xFFe2e8f0)),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(color: Color(0xFF3182ce), width: 1.5),
            ),
            contentPadding: const EdgeInsets.symmetric(vertical: 14),
            counterText: '',
          ),
        ),
      ],
    );
  }

  Widget _buildCodeRow({
    required TextEditingController controller,
    required String hint,
    required int countdown,
    required VoidCallback onSend,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('验证码', style: TextStyle(fontSize: 13, fontWeight: FontWeight.w500, color: Color(0xFF4a5568))),
        const SizedBox(height: 6),
        Row(
          children: [
            Expanded(
              child: TextField(
                controller: controller,
                keyboardType: TextInputType.number,
                maxLength: 6,
                inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                decoration: InputDecoration(
                  hintText: hint,
                  hintStyle: const TextStyle(fontSize: 14, color: Color(0xFFa0aec0)),
                  prefixIcon: const Icon(Icons.message_outlined, size: 20, color: Color(0xFFa0aec0)),
                  filled: true,
                  fillColor: const Color(0xFFf7fafc),
                  border: OutlineInputBorder(borderRadius: BorderRadius.circular(8), borderSide: const BorderSide(color: Color(0xFFe2e8f0))),
                  enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(8), borderSide: const BorderSide(color: Color(0xFFe2e8f0))),
                  focusedBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(8), borderSide: const BorderSide(color: Color(0xFF3182ce), width: 1.5)),
                  contentPadding: const EdgeInsets.symmetric(vertical: 14),
                  counterText: '',
                ),
              ),
            ),
            const SizedBox(width: 12),
            SizedBox(
              height: 48,
              child: ElevatedButton(
                onPressed: countdown > 0 ? null : onSend,
                style: ElevatedButton.styleFrom(
                  backgroundColor: countdown > 0 ? const Color(0xFFe2e8f0) : Colors.white,
                  foregroundColor: countdown > 0 ? const Color(0xFFa0aec0) : const Color(0xFF3182ce),
                  elevation: 0,
                  side: BorderSide(color: countdown > 0 ? const Color(0xFFe2e8f0) : const Color(0xFF3182ce)),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  textStyle: const TextStyle(fontSize: 13),
                ),
                child: Text(countdown > 0 ? '${countdown}s后重试' : '获取验证码'),
              ),
            ),
          ],
        ),
      ],
    );
  }
}
