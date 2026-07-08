import 'dart:async';

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import '../models/alert_item.dart';
import '../models/latest_vitals.dart';
import '../services/api_service.dart';
import '../services/sos_service.dart';
import '../state/nav_controller.dart';
import 'camera_request_screen.dart';
import 'service_request_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  LatestVitals? _vitals;
  AlertItem? _latestAlert;
  StreamSubscription<String>? _sub;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _load();
    final api = context.read<ApiService>();
    _sub = api.syncStream.listen((_) => _load());
    _timer = Timer.periodic(const Duration(seconds: 5), (_) => _load());
  }

  @override
  void dispose() {
    _sub?.cancel();
    _timer?.cancel();
    super.dispose();
  }

  Future<void> _load() async {
    final api = context.read<ApiService>();
    final v = await api.fetchLatestVitals();
    AlertItem? a;
    try {
      a = await api.fetchLatestAlert();
    } catch (_) {
      a = null;
    }
    if (!mounted) return;
    setState(() {
      _vitals = v;
      _latestAlert = a;
    });
  }

  String _alertPreviewTime(DateTime t) {
    final now = DateTime.now();
    final sameDay =
        now.year == t.year && now.month == t.month && now.day == t.day;
    if (sameDay) {
      return '今天 ${DateFormat('HH:mm').format(t)}';
    }
    return DateFormat('MM-dd HH:mm').format(t);
  }

  String _relativeRefresh(DateTime t) {
    final d = DateTime.now().difference(t);
    if (d.inSeconds < 45) {
      return '刚刚';
    }
    if (d.inMinutes < 60) {
      return '${d.inMinutes}分钟前';
    }
    if (d.inHours < 24) {
      return '${d.inHours}小时前';
    }
    return '${d.inDays}天前';
  }

  @override
  Widget build(BuildContext context) {
    final timeFmt = DateFormat('HH:mm');
    final vitals = _vitals;
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          '健康助手',
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
        actions: [
          IconButton(
            tooltip: '个人中心',
            onPressed: () => context.read<NavController>().setTab(4),
            icon: const CircleAvatar(
              radius: 18,
              backgroundColor: Colors.white24,
              child: Icon(Icons.person, size: 20, color: Colors.white),
            ),
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [Color(0xFFF0F7FF), Colors.white],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
          ),
        ),
        child: RefreshIndicator(
          onRefresh: _load,
          child: ListView(
            physics: const AlwaysScrollableScrollPhysics(),
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
            children: [
              SizedBox(
                width: double.infinity,
                height: 56,
                child: FilledButton.icon(
                  style: FilledButton.styleFrom(
                    backgroundColor: Colors.red,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(vertical: 16),
                  ),
                  icon: const Icon(Icons.emergency, size: 24),
                  onPressed: () => SosService.triggerSos(context),
                  label: const Text(
                    'SOS 紧急呼叫',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ),
              const SizedBox(height: 12),
              if (vitals != null) ...[
                GridView.count(
                  crossAxisCount: 2,
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  mainAxisSpacing: 10,
                  crossAxisSpacing: 10,
                  childAspectRatio: 1.25,
                  children: [
                    _VitalCard(
                      title: '体温',
                      value: vitals.temperature.toStringAsFixed(1),
                      unit: '℃',
                      subtitle: timeFmt.format(vitals.measuredAt),
                    ),
                    _VitalCard(
                      title: '心率',
                      value: '${vitals.heartRate}',
                      unit: 'bpm',
                      subtitle: timeFmt.format(vitals.measuredAt),
                    ),
                    _VitalCard(
                      title: '收缩压/舒张压',
                      value: '${vitals.systolic}/${vitals.diastolic}',
                      unit: 'mmHg',
                      subtitle: '',
                    ),
                    _VitalCard(
                      title: '更新时间',
                      value: _relativeRefresh(vitals.measuredAt),
                      unit: '',
                      subtitle: '',
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                Row(
                  children: [
                    Expanded(
                      child: _ScaleButton(
                        onTap: () => context.read<NavController>().setTab(1),
                        child: FilledButton.tonal(
                          onPressed: () =>
                              context.read<NavController>().setTab(1),
                          child: const Text(
                            '查看详细图表',
                            style: TextStyle(fontSize: 14),
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: _ScaleButton(
                        onTap: () => context.read<NavController>().setTab(2),
                        child: FilledButton(
                          onPressed: () =>
                              context.read<NavController>().setTab(2),
                          child: const Text(
                            '实时监控',
                            style: TextStyle(fontSize: 14),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                // Quick action buttons
                Row(
                  children: [
                    Expanded(
                      child: _ScaleButton(
                        onTap: () => Navigator.of(context).push(
                          MaterialPageRoute<void>(builder: (_) => const CameraRequestScreen()),
                        ),
                        child: Container(
                          padding: const EdgeInsets.symmetric(vertical: 14),
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
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(Icons.videocam, color: Colors.blue.shade700, size: 20),
                              const SizedBox(width: 6),
                              Text(
                                '监控申请',
                                style: TextStyle(
                                  fontSize: 14,
                                  fontWeight: FontWeight.w600,
                                  color: Colors.blue.shade700,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: _ScaleButton(
                        onTap: () => Navigator.of(context).push(
                          MaterialPageRoute<void>(builder: (_) => const ServiceRequestScreen()),
                        ),
                        child: Container(
                          padding: const EdgeInsets.symmetric(vertical: 14),
                          decoration: BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(12),
                            boxShadow: [
                              BoxShadow(
                                color: Colors.orange.shade100.withValues(alpha: 0.6),
                                blurRadius: 6,
                                offset: const Offset(0, 2),
                              ),
                            ],
                          ),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(Icons.build, color: Colors.orange.shade700, size: 20),
                              const SizedBox(width: 6),
                              Text(
                                '服务申请',
                                style: TextStyle(
                                  fontSize: 14,
                                  fontWeight: FontWeight.w600,
                                  color: Colors.orange.shade700,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 20),
                const Text(
                  '最新告警',
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 8),
                _ScaleButton(
                  onTap: () => context.read<NavController>().setTab(3),
                  child: Material(
                    color: Theme.of(context).colorScheme.surfaceContainerHighest
                        .withValues(alpha: 0.35),
                    borderRadius: BorderRadius.circular(12),
                    child: InkWell(
                      borderRadius: BorderRadius.circular(12),
                      onTap: () => context.read<NavController>().setTab(3),
                      child: Padding(
                        padding: const EdgeInsets.all(14),
                        child: Row(
                          children: [
                            const Icon(
                              Icons.warning_amber_rounded,
                              color: Colors.orange,
                            ),
                            const SizedBox(width: 10),
                            Expanded(
                              child: _latestAlert == null
                                  ? const Text(
                                      '暂无告警',
                                      style: TextStyle(fontSize: 15),
                                    )
                                  : Column(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      children: [
                                        Text(
                                          '${_alertPreviewTime(_latestAlert!.occurredAt)}  ${_latestAlert!.type.labelZh}',
                                          style: const TextStyle(
                                            fontWeight: FontWeight.w500,
                                            fontSize: 15,
                                          ),
                                        ),
                                        const SizedBox(height: 4),
                                        Text(
                                          _latestAlert!.detail,
                                          style: TextStyle(
                                            color: Colors.grey.shade700,
                                            fontSize: 13,
                                          ),
                                        ),
                                      ],
                                    ),
                            ),
                            TextButton(
                              onPressed: () =>
                                  context.read<NavController>().setTab(3),
                              child: const Text('查看更多 >'),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}

class _VitalCard extends StatelessWidget {
  const _VitalCard({
    required this.title,
    required this.value,
    required this.unit,
    required this.subtitle,
  });

  final String title;
  final String value;
  final String unit;
  final String subtitle;

  @override
  Widget build(BuildContext context) {
    return Container(
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
      padding: const EdgeInsets.all(10),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: TextStyle(
              color: Colors.grey.shade700,
              fontSize: 12,
            ),
          ),
          const Spacer(),
          Row(
            crossAxisAlignment: CrossAxisAlignment.baseline,
            textBaseline: TextBaseline.alphabetic,
            children: [
              Flexible(
                child: Text(
                  value,
                  style: const TextStyle(
                    fontSize: 22,
                    fontWeight: FontWeight.bold,
                  ),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
              if (unit.isNotEmpty) ...[
                const SizedBox(width: 3),
                Text(
                  unit,
                  style: TextStyle(
                    color: Colors.grey.shade600,
                    fontSize: 12,
                  ),
                ),
              ],
            ],
          ),
          if (subtitle.isNotEmpty)
            Text(
              subtitle,
              style: TextStyle(
                color: Colors.grey.shade600,
                fontSize: 11,
              ),
            ),
        ],
      ),
    );
  }
}

class _ScaleButton extends StatefulWidget {
  const _ScaleButton({required this.child, required this.onTap});
  final Widget child;
  final VoidCallback onTap;

  @override
  State<_ScaleButton> createState() => _ScaleButtonState();
}

class _ScaleButtonState extends State<_ScaleButton> {
  bool _pressed = false;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTapDown: (_) => setState(() => _pressed = true),
      onTapUp: (_) {
        setState(() => _pressed = false);
        widget.onTap();
      },
      onTapCancel: () => setState(() => _pressed = false),
      child: AnimatedScale(
        scale: _pressed ? 0.95 : 1.0,
        duration: const Duration(milliseconds: 150),
        curve: Curves.easeInOut,
        child: widget.child,
      ),
    );
  }
}
