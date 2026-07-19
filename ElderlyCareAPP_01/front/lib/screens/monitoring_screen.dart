import 'dart:async';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/camera_request.dart';
import '../services/api_service.dart';
import '../services/device_battery_simulator.dart';
import 'live_location_screen.dart';

class MonitoringScreen extends StatefulWidget {
  const MonitoringScreen({super.key});

  @override
  State<MonitoringScreen> createState() => _MonitoringScreenState();
}

class _MonitoringScreenState extends State<MonitoringScreen> {
  List<CameraRequest>? _requests;
  List<DeviceBatteryState>? _deviceStates;
  StreamSubscription<String>? _sub;
  Timer? _batteryTimer;
  final _simulator = DeviceBatterySimulator();

  @override
  void initState() {
    super.initState();
    _load();
    final api = context.read<ApiService>();
    _sub = api.syncStream.listen((_) => _load());
    // 每 60 秒自动刷新电量，无需手动刷新
    _batteryTimer = Timer.periodic(const Duration(seconds: 60), (_) => _loadDevices());
  }

  @override
  void dispose() {
    _sub?.cancel();
    _batteryTimer?.cancel();
    super.dispose();
  }

  Future<void> _load() async {
    await Future.wait([_loadCameraRequests(), _loadDevices()]);
  }

  Future<void> _loadCameraRequests() async {
    try {
      final api = context.read<ApiService>();
      final list = await api.fetchCameraRequests();
      if (!mounted) return;
      setState(() => _requests = list.toList()..sort((a, b) => b.requestTime.compareTo(a.requestTime)));
    } catch (_) {
      if (mounted) setState(() => _requests = []);
    }
  }

  Future<void> _loadDevices() async {
    try {
      final api = context.read<ApiService>();
      final raw = await api.fetchDevices();
      if (!mounted) return;
      // 过滤出需要显示电量的设备类型
      final target = raw.where((d) {
        final type = d['deviceType'] as String? ?? '';
        return type == '手环' || type == '摄像头' || type == '门锁';
      }).toList();
      if (target.isEmpty) return;
      final states = await _simulator.computeBatteryLevels(target);
      if (!mounted) return;
      setState(() => _deviceStates = states);
    } catch (_) {
      // 设备列表加载失败不影响其他模块
    }
  }

  void _openLiveLocation() {
    Navigator.of(context).push(
      MaterialPageRoute<void>(builder: (_) => const LiveLocationScreen()),
    );
  }

  String _remainingTime(DateTime expiresAt) {
    final diff = expiresAt.difference(DateTime.now());
    if (diff.isNegative) return '已过期';
    final hours = diff.inHours;
    final minutes = diff.inMinutes % 60;
    if (hours > 0) return '${hours}小时${minutes}分';
    return '${minutes}分钟';
  }

  Future<void> _revoke() async {
    // Use the active viewer request ID directly
    final active = _activeViewer;
    if (active == null) return;
    final ok = await showDialog<bool>(
      context: context,
      builder: (c) => AlertDialog(
        title: const Text('停止监控查看'),
        content: const Text('确定要撤销社区人员查看监控的权限吗？撤销后对方将立即失去监控画面。'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(c, false), child: const Text('取消')),
          FilledButton(onPressed: () => Navigator.pop(c, true), child: const Text('确定撤销')),
        ],
      ),
    );
    if (ok != true || !mounted) return;
    final api = context.read<ApiService>();
    try {
      await api.revokeCameraAuth(active.id.toString());
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

  CameraRequest? get _activeViewer {
    if (_requests == null) return null;
    try {
      return _requests!.firstWhere(
        (r) => r.status == 'approved' && (r.expiresAt?.isAfter(DateTime.now()) ?? false),
      );
    } catch (_) {
      return null;
    }
  }

  @override
  Widget build(BuildContext context) {
    final active = _activeViewer;
    final hasActiveViewer = active != null;

    return Scaffold(
      appBar: AppBar(
        title: const Text(
          '实时监控',
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
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // Camera viewing status card
          if (hasActiveViewer)
            Container(
              margin: const EdgeInsets.only(bottom: 16),
              decoration: BoxDecoration(
                color: Colors.red.shade50,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.red.shade200),
                boxShadow: [
                  BoxShadow(
                    color: Colors.red.shade100.withValues(alpha: 0.4),
                    blurRadius: 6,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Icon(Icons.videocam, color: Colors.red.shade600, size: 20),
                        const SizedBox(width: 8),
                        Text(
                          '社区正在查看监控',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            color: Colors.red.shade700,
                          ),
                        ),
                        const Spacer(),
                        Container(
                          width: 8,
                          height: 8,
                          decoration: BoxDecoration(
                            color: Colors.red,
                            shape: BoxShape.circle,
                            boxShadow: [
                              BoxShadow(color: Colors.red.withValues(alpha: 0.4), blurRadius: 6, spreadRadius: 2),
                            ],
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 10),
                    Text(
                      '查看人员：${active.staffName}',
                      style: TextStyle(fontSize: 14, color: Colors.grey.shade800),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '查看原因：${active.reason}',
                      style: TextStyle(fontSize: 13, color: Colors.grey.shade700),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '剩余权限时间：${_remainingTime(active.expiresAt!)}',
                      style: TextStyle(
                        fontSize: 13,
                        color: Colors.red.shade700,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 12),
                    SizedBox(
                      width: double.infinity,
                      child: FilledButton.icon(
                        onPressed: () => _revoke(),
                        icon: const Icon(Icons.stop_circle_outlined),
                        label: const Text('停止查看 / 撤销权限'),
                        style: FilledButton.styleFrom(backgroundColor: Colors.red),
                      ),
                    ),
                  ],
                ),
              ),
            )
          else
            Container(
              margin: const EdgeInsets.only(bottom: 16),
              decoration: BoxDecoration(
                color: Colors.green.shade50,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.green.shade200),
              ),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Row(
                  children: [
                    Icon(Icons.check_circle, color: Colors.green.shade600, size: 20),
                    const SizedBox(width: 8),
                    Text(
                      '当前无社区人员查看监控',
                      style: TextStyle(
                        fontSize: 15,
                        fontWeight: FontWeight.w500,
                        color: Colors.green.shade700,
                      ),
                    ),
                  ],
                ),
              ),
            ),

          // Real-time location card
          Container(
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(12),
              boxShadow: [
                BoxShadow(
                  color: Colors.blue.shade100.withValues(alpha: 0.6),
                  blurRadius: 6,
                  offset: const Offset(0, 2),
                ),
              ],
            ),
            clipBehavior: Clip.antiAlias,
            child: Material(
              color: Colors.transparent,
              child: InkWell(
                onTap: _openLiveLocation,
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Row(
                    children: [
                      Container(
                        width: 48,
                        height: 48,
                        decoration: BoxDecoration(
                          gradient: const LinearGradient(
                            colors: [Color(0xFF2C7DA0), Color(0xFF4A90E2)],
                            begin: Alignment.topLeft,
                            end: Alignment.bottomRight,
                          ),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: const Icon(Icons.location_on, color: Colors.white),
                      ),
                      const SizedBox(width: 16),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text(
                              '实时位置',
                              style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              '查看老人当前位置与地图',
                              style: TextStyle(color: Colors.grey.shade600, fontSize: 13),
                            ),
                          ],
                        ),
                      ),
                      Icon(Icons.arrow_forward_ios, size: 16, color: Colors.grey.shade400),
                    ],
                  ),
                ),
              ),
            ),
          ),
          // Device status overview
          if (_deviceStates != null && _deviceStates!.isNotEmpty) ...[
            const SizedBox(height: 20),
            _buildDeviceCard(),
          ],

          const SizedBox(height: 20),
          AspectRatio(
            aspectRatio: 16 / 10,
            child: CustomPaint(
              painter: _DashedBorderPainter(),
              child: Container(
                decoration: BoxDecoration(
                  color: const Color(0xFFE8F4FD).withValues(alpha: 0.5),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.videocam, size: 56, color: Colors.grey.shade500),
                    const SizedBox(height: 10),
                    Text(
                      hasActiveViewer ? '社区正在查看实时画面' : '实时画面准备就绪',
                      style: TextStyle(color: Colors.grey.shade700, fontSize: 16),
                    ),
                    if (hasActiveViewer) ...[
                      const SizedBox(height: 6),
                      Text(
                        '查看人员：${active!.staffName}',
                        style: TextStyle(color: Colors.grey.shade600, fontSize: 13),
                      ),
                    ],
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  // ---------- 设备状态卡片 ----------

  Widget _buildDeviceCard() {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        boxShadow: [
          BoxShadow(
            color: Colors.grey.shade200.withValues(alpha: 0.8),
            blurRadius: 6,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Padding(
        padding: const EdgeInsets.all(14),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Container(
                  width: 36,
                  height: 36,
                  decoration: BoxDecoration(
                    gradient: const LinearGradient(
                      colors: [Color(0xFF2C7DA0), Color(0xFF4A90E2)],
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                    ),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: const Icon(Icons.devices, color: Colors.white, size: 20),
                ),
                const SizedBox(width: 10),
                const Text(
                  '设备状态',
                  style: TextStyle(fontSize: 15, fontWeight: FontWeight.w600),
                ),
                const Spacer(),
                Text(
                  '${_deviceStates!.where((d) => d.online).length}/${_deviceStates!.length} 在线',
                  style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
                ),
              ],
            ),
            const SizedBox(height: 12),
            ..._deviceStates!.map((d) => _buildDeviceRow(d)),
          ],
        ),
      ),
    );
  }

  Widget _buildDeviceRow(DeviceBatteryState device) {
    final batteryColor = _batteryColor(device.battery);
    final iconData = _deviceIcon(device.deviceType, device.location);

    return Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: Row(
        children: [
          Container(
            width: 38,
            height: 38,
            decoration: BoxDecoration(
              color: device.online
                  ? const Color(0xFFF0F7FF)
                  : Colors.grey.shade100,
              borderRadius: BorderRadius.circular(10),
            ),
            child: Icon(
              iconData,
              size: 20,
              color: device.online
                  ? const Color(0xFF2C7DA0)
                  : Colors.grey.shade400,
            ),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  device.deviceName,
                  style: TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w500,
                    color: device.online
                        ? Colors.grey.shade900
                        : Colors.grey.shade400,
                  ),
                ),
                Text(
                  device.location,
                  style: TextStyle(fontSize: 11, color: Colors.grey.shade500),
                ),
              ],
            ),
          ),
          SizedBox(
            width: 72,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text(
                  '${device.battery}%',
                  style: TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                    color: batteryColor,
                  ),
                ),
                const SizedBox(height: 4),
                ClipRRect(
                  borderRadius: BorderRadius.circular(4),
                  child: LinearProgressIndicator(
                    value: device.battery / 100,
                    minHeight: 5,
                    backgroundColor: Colors.grey.shade200,
                    valueColor: AlwaysStoppedAnimation<Color>(batteryColor),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(width: 8),
          Container(
            width: 8,
            height: 8,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: device.online ? const Color(0xFF22C55E) : Colors.grey.shade400,
              boxShadow: device.online
                  ? [
                      BoxShadow(
                        color: const Color(0xFF22C55E).withValues(alpha: 0.4),
                        blurRadius: 4,
                        spreadRadius: 1,
                      ),
                    ]
                  : null,
            ),
          ),
        ],
      ),
    );
  }

  Color _batteryColor(int level) {
    if (level > 50) return const Color(0xFF22C55E);
    if (level > 30) return const Color(0xFFF59E0B);
    return const Color(0xFFEF4444);
  }

  IconData _deviceIcon(String type, String location) {
    switch (type) {
      case '手环':
        return Icons.watch;
      case '摄像头':
        if (location.contains('门口')) return Icons.meeting_room;
        if (location.contains('卧室')) return Icons.bed;
        return Icons.videocam;
      case '门锁':
        return Icons.lock;
      default:
        return Icons.devices_other;
    }
  }
}

class _DashedBorderPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = Colors.grey.shade400
      ..strokeWidth = 1.5
      ..style = PaintingStyle.stroke;

    final rrect = RRect.fromRectAndRadius(
      Rect.fromLTWH(0, 0, size.width, size.height),
      const Radius.circular(12),
    );

    final path = Path()..addRRect(rrect);
    final metrics = path.computeMetrics();
    final dashedPath = Path();

    for (final metric in metrics) {
      var distance = 0.0;
      const dash = 6.0;
      const gap = 4.0;
      while (distance < metric.length) {
        dashedPath.addPath(metric.extractPath(distance, distance + dash), Offset.zero);
        distance += dash + gap;
      }
    }

    canvas.drawPath(dashedPath, paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
