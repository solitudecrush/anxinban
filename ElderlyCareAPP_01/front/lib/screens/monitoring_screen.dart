import 'dart:async';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/camera_request.dart';
import '../services/api_service.dart';
import 'live_location_screen.dart';

class MonitoringScreen extends StatefulWidget {
  const MonitoringScreen({super.key});

  @override
  State<MonitoringScreen> createState() => _MonitoringScreenState();
}

class _MonitoringScreenState extends State<MonitoringScreen> {
  final _rng = Random();
  Timer? _timer;
  int _demoHr = 72;
  List<CameraRequest>? _requests;
  StreamSubscription<String>? _sub;

  @override
  void initState() {
    super.initState();
    _timer = Timer.periodic(const Duration(seconds: 1), (_) {
      setState(() => _demoHr = 60 + _rng.nextInt(41));
    });
    _load();
    final api = context.read<ApiService>();
    _sub = api.syncStream.listen((_) => _load());
  }

  @override
  void dispose() {
    _timer?.cancel();
    _sub?.cancel();
    super.dispose();
  }

  Future<void> _load() async {
    try {
      final api = context.read<ApiService>();
      final list = await api.fetchCameraRequests();
      if (!mounted) return;
      setState(() => _requests = list.toList()..sort((a, b) => b.requestTime.compareTo(a.requestTime)));
    } catch (_) {
      if (mounted) setState(() => _requests = []);
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
          const SizedBox(height: 20),
          Text(
            '该功能开发中，即将支持：',
            style: TextStyle(
              color: Colors.grey.shade800,
              fontWeight: FontWeight.w600,
              fontSize: 15,
            ),
          ),
          const SizedBox(height: 8),
          const Text('• 摄像头心率测量', style: TextStyle(fontSize: 14)),
          const Text('• 红外体温监测', style: TextStyle(fontSize: 14)),
          const SizedBox(height: 20),
          Center(
            child: Text(
              '模拟心率：$_demoHr bpm',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.w600),
            ),
          ),
        ],
      ),
    );
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
