import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:url_launcher/url_launcher.dart';

import '../models/camera_request.dart';
import '../models/emergency_contact_entry.dart';
import '../models/profile.dart';
import '../screens/camera_request_screen.dart';
import '../screens/emergency_contact_settings_screen.dart';
import '../screens/health_record_screen.dart';
import '../screens/sos_sim_settings_screen.dart';
import '../services/api_service.dart';
import '../services/emergency_contact_store.dart';
import '../services/sos_sim_store.dart';
import '../widgets/avatar_picker.dart';
import '../widgets/avatar_widget.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  late Profile _p;
  String? _avatarPath;
  List<EmergencyContactEntry> _contacts = [];
  String _version = '';
  bool _notifyEnabled = true;
  int _defaultSimSlot = 1;

  @override
  void initState() {
    super.initState();
    _loadProfile();
    _loadContacts();
    _loadVersion();
    _loadSettings();
    _loadSimSlot();
    _startPolling();
  }

  StreamSubscription<String>? _syncSub;

  void _startPolling() {
    final api = context.read<ApiService>();
    _syncSub = api.syncStream.listen((_) {
      _loadProfile();
    });
  }

  @override
  void dispose() {
    _syncSub?.cancel();
    super.dispose();
  }

  Future<void> _loadProfile() async {
    final api = context.read<ApiService>();
    final profile = await api.fetchProfile();
    final prefs = await SharedPreferences.getInstance();
    final avatarPath = prefs.getString('profile_avatar');
    if (mounted) {
      setState(() {
        _p = profile;
        if (avatarPath != null) _avatarPath = avatarPath;
      });
    }
  }

  Future<void> _saveProfile() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('profile_name', _p.name);
    await prefs.setInt('profile_age', _p.age);
    await prefs.setString('profile_gender', _p.gender);
    await prefs.setString('profile_familyPhone', _p.familyPhone);
    await prefs.setString('profile_address', _p.address);
    if (_avatarPath != null) {
      await prefs.setString('profile_avatar', _avatarPath!);
    }
    // 同步到 Web 端（localStorage）
    final api = context.read<ApiService>();
    await api.patchProfileField('name', _p.name);
    await api.patchProfileField('familyPhone', _p.familyPhone);
    await api.patchProfileField('address', _p.address);
    // age 和 gender 也通过 localStorage 同步
    final elder = <String, dynamic>{};
    elder['age'] = _p.age;
    elder['gender'] = _p.gender;
    await api.patchProfileField('_raw_age', '${_p.age}');
    await api.patchProfileField('_raw_gender', _p.gender);
  }

  Future<void> _pickAvatar() async {
    final prefs = await SharedPreferences.getInstance();
    final userId = prefs.getString('app_user_id') ?? '100';
    final role = prefs.getString('app_user_role') ?? 'family';

    final url = await showAvatarPicker(
      context,
      userId: userId,
      role: role,
    );
    if (url != null && mounted) {
      setState(() => _avatarPath = url);
      await prefs.setString('profile_avatar', url);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('头像已更新')),
        );
      }
    }
  }

  Future<void> _loadContacts() async {
    final contacts = await EmergencyContactStore.loadAll();
    if (!mounted) return;
    setState(() => _contacts = contacts);
  }

  Future<void> _loadVersion() async {
    try {
      final info = await PackageInfo.fromPlatform();
      if (!mounted) return;
      setState(() => _version = '${info.version}+${info.buildNumber}');
    } catch (_) {
      setState(() => _version = '1.0.0+1');
    }
  }

  Future<void> _loadSettings() async {
    final prefs = await SharedPreferences.getInstance();
    if (!mounted) return;
    setState(() => _notifyEnabled = prefs.getBool('notify_enabled') ?? true);
  }

  Future<void> _loadSimSlot() async {
    final slot = await SosSimStore.getDefaultSimSlot();
    if (!mounted) return;
    setState(() => _defaultSimSlot = slot);
  }

  Future<void> _setNotify(bool value) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('notify_enabled', value);
    if (!mounted) return;
    setState(() => _notifyEnabled = value);
  }

  String _maskPhone(String p) {
    final digits = p.replaceAll(RegExp(r'\D'), '');
    if (digits.length != 11) return p;
    return '${digits.substring(0, 3)}****${digits.substring(7)}';
  }

  Future<void> _editField({
    required String title,
    required String field,
    required String initial,
    TextInputType keyboard = TextInputType.text,
    List<TextInputFormatter>? formatters,
    int maxLines = 1,
  }) async {
    final controller = TextEditingController(text: initial);
    final ok = await showDialog<bool>(
      context: context,
      builder: (c) => AlertDialog(
        title: Text('编辑$title'),
        content: TextField(
          controller: controller,
          keyboardType: keyboard,
          inputFormatters: formatters,
          autofocus: true,
          maxLines: maxLines,
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(c, false),
            child: const Text('取消'),
          ),
          FilledButton(
            onPressed: () => Navigator.pop(c, true),
            child: const Text('确定'),
          ),
        ],
      ),
    );
    if (ok != true || !mounted) return;
    final value = controller.text.trim();
    final api = context.read<ApiService>();
    await api.patchProfileField(field, value);
    setState(() {
      _p = Profile(
        name: field == 'name' ? value : _p.name,
        age: _p.age,
        gender: _p.gender,
        familyPhone: field == 'familyPhone' ? value : _p.familyPhone,
        address: field == 'address' ? value : _p.address,
      );
    });
    await _saveProfile();
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('更新成功并已同步到社区端')),
      );
    }
  }

  Future<void> _editAge() async {
    final controller = TextEditingController(text: '${_p.age}');
    final ok = await showDialog<bool>(
      context: context,
      builder: (c) => AlertDialog(
        title: const Text('编辑年龄'),
        content: TextField(
          controller: controller,
          keyboardType: TextInputType.number,
          inputFormatters: [FilteringTextInputFormatter.digitsOnly],
          autofocus: true,
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(c, false),
            child: const Text('取消'),
          ),
          FilledButton(
            onPressed: () => Navigator.pop(c, true),
            child: const Text('确定'),
          ),
        ],
      ),
    );
    if (ok != true || !mounted) return;
    final value = int.tryParse(controller.text.trim()) ?? _p.age;
    setState(() {
      _p = Profile(
        name: _p.name,
        age: value,
        gender: _p.gender,
        familyPhone: _p.familyPhone,
        address: _p.address,
      );
    });
    await _saveProfile();
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('更新成功')),
      );
    }
  }

  Future<void> _editGender() async {
    final selected = await showDialog<String>(
      context: context,
      builder: (c) => AlertDialog(
        title: const Text('选择性别'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: ['男', '女'].map((g) {
            return ListTile(
              title: Text(g),
              trailing: _p.gender == g
                  ? const Icon(Icons.check, color: Color(0xFF4A90E2))
                  : null,
              onTap: () => Navigator.pop(c, g),
            );
          }).toList(),
        ),
      ),
    );
    if (selected == null || !mounted) return;
    setState(() {
      _p = Profile(
        name: _p.name,
        age: _p.age,
        gender: selected,
        familyPhone: _p.familyPhone,
        address: _p.address,
      );
    });
    await _saveProfile();
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('更新成功')),
      );
    }
  }

  Widget _sectionHeader(String title) {
    return Padding(
      padding: const EdgeInsets.only(left: 6, bottom: 8, top: 4),
      child: Text(
        title.toUpperCase(),
        style: const TextStyle(
          fontSize: 12,
          color: Colors.black45,
          letterSpacing: 0.8,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }

  Widget _groupedCard(List<Widget> children) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: Colors.grey.shade200.withValues(alpha: 0.8),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      clipBehavior: Clip.antiAlias,
      child: Column(
        children: [
          for (var i = 0; i < children.length; i++) ...[
            if (i > 0) const Divider(height: 1),
            children[i],
          ],
        ],
      ),
    );
  }

  Widget _row({
    required String label,
    required String value,
    required VoidCallback onTap,
    bool editable = false,
  }) {
    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
          child: Row(
            children: [
              Expanded(
                flex: 2,
                child: Text(
                  label,
                  style: const TextStyle(
                    fontSize: 14,
                    color: Colors.black54,
                  ),
                ),
              ),
              Expanded(
                flex: 3,
                child: Align(
                  alignment: Alignment.centerRight,
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      Flexible(
                        child: Text(
                          value,
                          textAlign: TextAlign.end,
                          style: const TextStyle(
                            fontSize: 15,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                      ),
                      const SizedBox(width: 4),
                      if (editable)
                        Icon(
                          Icons.edit,
                          size: 16,
                          color: Colors.grey.shade400,
                        )
                      else
                        Icon(
                          Icons.chevron_right,
                          color: Colors.grey.shade400,
                        ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _actionRow({
    required IconData icon,
    required Color iconColor,
    required String title,
    String? subtitle,
    required VoidCallback onTap,
  }) {
    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
          child: Row(
            children: [
              Icon(icon, color: iconColor, size: 22),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: const TextStyle(
                        fontSize: 15,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                    if (subtitle != null)
                      Text(
                        subtitle,
                        style: const TextStyle(
                          fontSize: 13,
                          color: Colors.black45,
                        ),
                      ),
                  ],
                ),
              ),
              Icon(Icons.chevron_right, color: Colors.grey.shade400, size: 20),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _openEmergencyContacts() async {
    await Navigator.of(context).push<void>(
      MaterialPageRoute<void>(
        builder: (_) => const EmergencyContactSettingsScreen(),
      ),
    );
    final list = await EmergencyContactStore.loadAll();
    if (mounted) setState(() => _contacts = list);
  }

  Future<void> _openHealthRecord() async {
    await Navigator.of(context).push<void>(
      MaterialPageRoute<void>(
        builder: (_) => const HealthRecordScreen(),
      ),
    );
  }

  Future<void> _openSosSimSettings() async {
    await Navigator.of(context).push<void>(
      MaterialPageRoute<void>(
        builder: (_) => const SosSimSettingsScreen(),
      ),
    );
    await _loadSimSlot();
  }

  Future<void> _openPrivacyPolicy() async {
    const url = 'https://lbs.amap.com/home/privacy/';
    final uri = Uri.parse(url);
    if (await canLaunchUrl(uri)) {
      await launchUrl(uri, mode: LaunchMode.externalApplication);
    }
  }

  void _logout() {
    showDialog<void>(
      context: context,
      builder: (c) => AlertDialog(
        title: const Text('退出登录'),
        content: const Text('确定要退出登录吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(c),
            child: const Text('取消'),
          ),
          FilledButton(
            onPressed: () {
              Navigator.pop(c);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('已退出登录')),
              );
            },
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          '个人中心',
          style: TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        ),
        foregroundColor: Colors.white,
        flexibleSpace: Container(
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              colors: [Color(0xFF2C7DA0), Color(0xFF4A90E2)],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
          ),
        ),
      ),
      body: RefreshIndicator(
        onRefresh: _loadContacts,
        child: ListView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.fromLTRB(16, 0, 16, 40),
          children: [
            const SizedBox(height: 16),
            Center(
              child: GestureDetector(
                onTap: _pickAvatar,
                child: Stack(
                  alignment: Alignment.bottomRight,
                  children: [
                    AvatarWidget(
                      avatarUrl: _avatarPath,
                      radius: 40,
                    ),
                    Container(
                      width: 26,
                      height: 26,
                      decoration: BoxDecoration(
                        color: Colors.white,
                        shape: BoxShape.circle,
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black.withValues(alpha: 0.1),
                            blurRadius: 4,
                          ),
                        ],
                      ),
                      child: const Icon(
                        Icons.camera_alt,
                        size: 14,
                        color: Color(0xFF2C7DA0),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 12),
            Center(
              child: Text(
                _p.name,
                style: const TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
            const SizedBox(height: 24),
            _sectionHeader('老人资料'),
            _groupedCard([
              _row(
                label: '老人姓名',
                value: _p.name,
                editable: true,
                onTap: () => _editField(
                  title: '老人姓名',
                  field: 'name',
                  initial: _p.name,
                ),
              ),
              _row(
                label: '年龄',
                value: '${_p.age} 岁',
                editable: true,
                onTap: _editAge,
              ),
              _row(
                label: '性别',
                value: _p.gender,
                editable: true,
                onTap: _editGender,
              ),
              _row(
                label: '家属联系电话',
                value: _maskPhone(_p.familyPhone),
                editable: true,
                onTap: () => _editField(
                  title: '家属联系电话',
                  field: 'familyPhone',
                  initial: _p.familyPhone,
                  keyboard: TextInputType.phone,
                  formatters: [FilteringTextInputFormatter.digitsOnly],
                ),
              ),
              _row(
                label: '家庭住址',
                value: _p.address.length > 18
                    ? '${_p.address.substring(0, 18)}…'
                    : _p.address,
                editable: true,
                onTap: () => _editField(
                  title: '家庭住址（或养老院地址）',
                  field: 'address',
                  initial: _p.address,
                  maxLines: 4,
                ),
              ),
            ]),
            const SizedBox(height: 24),
            _sectionHeader('监控状态'),
            _groupedCard([
              _CameraStatusLive(api: context.read<ApiService>()),
            ]),
            const SizedBox(height: 24),
            _sectionHeader('紧急联系人（本机）'),
            Card(
              clipBehavior: Clip.antiAlias,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  if (_contacts.isEmpty)
                    const Padding(
                      padding: EdgeInsets.all(20),
                      child: Text(
                        '示例联系人：李叔叔 13800138000\n请添加真实联系人用于 SOS 一键呼叫。',
                        style: TextStyle(
                          fontSize: 14,
                          color: Colors.black45,
                        ),
                      ),
                    )
                  else
                    ...List.generate(_contacts.length, (i) {
                      final e = _contacts[i];
                      return Column(
                        key: ValueKey(e.id),
                        children: [
                          if (i > 0) const Divider(height: 1),
                          ListTile(
                            title: Text(
                              e.name.isEmpty ? '未命名' : e.name,
                              style: const TextStyle(
                                fontSize: 15,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                            subtitle: Text(
                              _maskPhone(e.phone),
                              style: const TextStyle(fontSize: 13),
                            ),
                          ),
                        ],
                      );
                    }),
                  const Divider(height: 1),
                  ListTile(
                    leading: Icon(
                      Icons.edit_outlined,
                      color: Theme.of(context).colorScheme.primary,
                    ),
                    title: const Text(
                      '管理紧急联系人',
                      style: TextStyle(fontSize: 15),
                    ),
                    trailing: const Icon(Icons.chevron_right),
                    onTap: _openEmergencyContacts,
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            _sectionHeader('健康档案'),
            Card(
              clipBehavior: Clip.antiAlias,
              child: _actionRow(
                icon: Icons.folder_open_outlined,
                iconColor: Colors.teal,
                title: '健康档案',
                subtitle: '住院信息、既往病史、过敏史等',
                onTap: _openHealthRecord,
              ),
            ),
            const SizedBox(height: 24),
            _sectionHeader('设置'),
            Card(
              clipBehavior: Clip.antiAlias,
              child: Column(
                children: [
                  Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 16, vertical: 6),
                    child: Row(
                      children: [
                        Icon(
                          Icons.notifications_outlined,
                          color: Theme.of(context).colorScheme.primary,
                          size: 22,
                        ),
                        const SizedBox(width: 12),
                        const Expanded(
                          child: Text(
                            '消息通知',
                            style: TextStyle(
                              fontSize: 15,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                        Switch(
                          value: _notifyEnabled,
                          onChanged: _setNotify,
                        ),
                      ],
                    ),
                  ),
                  const Divider(height: 1),
                  _actionRow(
                    icon: Icons.sim_card_outlined,
                    iconColor: const Color(0xFF4A90E2),
                    title: 'SOS 默认拨号卡',
                    subtitle: '卡槽 $_defaultSimSlot',
                    onTap: _openSosSimSettings,
                  ),
                  const Divider(height: 1),
                  _actionRow(
                    icon: Icons.logout,
                    iconColor: Colors.redAccent,
                    title: '退出登录',
                    onTap: _logout,
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            _sectionHeader('关于'),
            Card(
              clipBehavior: Clip.antiAlias,
              child: Column(
                children: [
                  _actionRow(
                    icon: Icons.info_outline,
                    iconColor: Colors.grey.shade600,
                    title: '版本号',
                    subtitle: _version,
                    onTap: () {},
                  ),
                  const Divider(height: 1),
                  _actionRow(
                    icon: Icons.privacy_tip_outlined,
                    iconColor: Colors.grey.shade600,
                    title: '隐私政策',
                    onTap: _openPrivacyPolicy,
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _CameraStatusLive extends StatefulWidget {
  const _CameraStatusLive({required this.api});
  final ApiService api;

  @override
  State<_CameraStatusLive> createState() => _CameraStatusLiveState();
}

class _CameraStatusLiveState extends State<_CameraStatusLive> {
  List<CameraRequest>? _requests;
  StreamSubscription<String>? _sub;

  @override
  void initState() {
    super.initState();
    _load();
    _sub = widget.api.syncStream.listen((_) => _load());
  }

  @override
  void dispose() {
    _sub?.cancel();
    super.dispose();
  }

  Future<void> _load() async {
    final list = await widget.api.fetchCameraRequests();
    if (!mounted) return;
    setState(() => _requests = list);
  }

  String _remainingTime(DateTime? expiresAt) {
    if (expiresAt == null) return '';
    final diff = expiresAt.difference(DateTime.now());
    if (diff.isNegative) return '已过期';
    final hours = diff.inHours;
    final minutes = diff.inMinutes % 60;
    if (hours > 0) return '${hours}小时${minutes}分';
    return '${minutes}分钟';
  }

  Future<void> _revoke() async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (c) => AlertDialog(
        title: const Text('撤销监控权限'),
        content: const Text('确定要撤销社区人员查看监控的权限吗？'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(c, false), child: const Text('取消')),
          FilledButton(onPressed: () => Navigator.pop(c, true), child: const Text('确定撤销')),
        ],
      ),
    );
    if (ok != true || !mounted) return;
    try {
      final list = await widget.api.fetchCameraRequests();
      final approved = list.where((r) => r.status == 'approved').toList();
      if (approved.isNotEmpty) {
        await widget.api.revokeCameraAuth(approved.first.id.toString());
      }
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('已撤销监控权限')),
        );
      }
      await _load();
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.toString())));
    }
  }

  @override
  Widget build(BuildContext context) {
    final activeReq = _requests?.firstWhere(
      (r) => r.status == 'approved' && (r.expiresAt?.isAfter(DateTime.now()) ?? false),
      orElse: () => CameraRequest(
        id: 0,
        elderName: '曾姐',
        staffName: '-',
        staffPhone: '-',
        reason: '-',
        requestTime: DateTime.now(),
        status: 'none',
      ),
    );
    final isActive = activeReq != null && activeReq.id != 0 && activeReq.status == 'approved';
    final pendingReq = _requests?.firstWhere(
      (r) => r.status == 'pending',
      orElse: () => CameraRequest(id: 0, elderName: '曾姐', staffName: '-', staffPhone: '-', reason: '-', requestTime: DateTime.now(), status: 'none'),
    );
    final hasPending = pendingReq != null && pendingReq.id != 0 && pendingReq.status == 'pending';

    return Material(
      color: Colors.transparent,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  Icons.videocam,
                  color: isActive ? Colors.red.shade400 : (hasPending ? Colors.orange : Colors.green),
                  size: 20,
                ),
                const SizedBox(width: 10),
                Expanded(
                  child: Text(
                    isActive
                        ? '社区正在查看监控'
                        : hasPending
                            ? '有待处理的监控申请'
                            : '当前无社区人员查看监控',
                    style: TextStyle(
                      fontSize: 15,
                      fontWeight: FontWeight.w600,
                      color: isActive
                          ? Colors.red.shade600
                          : hasPending
                              ? Colors.orange.shade700
                              : Colors.green.shade700,
                    ),
                  ),
                ),
                if (isActive)
                  Container(
                    width: 8,
                    height: 8,
                    decoration: BoxDecoration(
                      color: Colors.red,
                      shape: BoxShape.circle,
                      boxShadow: [BoxShadow(color: Colors.red.withValues(alpha: 0.4), blurRadius: 4, spreadRadius: 1)],
                    ),
                  ),
              ],
            ),
            if (isActive && activeReq != null) ...[
              const SizedBox(height: 8),
              Text('查看人员：${activeReq.staffName}', style: TextStyle(fontSize: 13, color: Colors.grey.shade700)),
              const SizedBox(height: 4),
              Text('查看原因：${activeReq.reason}', style: TextStyle(fontSize: 13, color: Colors.grey.shade700)),
              const SizedBox(height: 4),
              Text('剩余权限时间：${_remainingTime(activeReq.expiresAt)}', style: TextStyle(fontSize: 13, color: Colors.red.shade600, fontWeight: FontWeight.w600)),
              const SizedBox(height: 10),
              SizedBox(
                width: double.infinity,
                child: FilledButton.icon(
                  onPressed: _revoke,
                  icon: const Icon(Icons.stop_circle),
                  label: const Text('停止监控 / 撤销权限'),
                  style: FilledButton.styleFrom(backgroundColor: Colors.red),
                ),
              ),
            ] else if (hasPending && pendingReq != null) ...[
              const SizedBox(height: 8),
              Text('申请人：${pendingReq.staffName}', style: TextStyle(fontSize: 13, color: Colors.grey.shade700)),
              const SizedBox(height: 4),
              Text('申请原因：${pendingReq.reason}', style: TextStyle(fontSize: 13, color: Colors.grey.shade700)),
              const SizedBox(height: 10),
              SizedBox(
                width: double.infinity,
                child: FilledButton(
                  onPressed: () => Navigator.of(context).push(MaterialPageRoute(builder: (_) => const CameraRequestScreen())),
                  child: const Text('去处理申请'),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}
