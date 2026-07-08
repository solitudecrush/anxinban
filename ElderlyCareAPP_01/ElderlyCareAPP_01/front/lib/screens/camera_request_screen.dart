import 'dart:async';

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import '../models/camera_request.dart';
import '../services/api_service.dart';

class CameraRequestScreen extends StatefulWidget {
  const CameraRequestScreen({super.key});

  @override
  State<CameraRequestScreen> createState() => _CameraRequestScreenState();
}

class _CameraRequestScreenState extends State<CameraRequestScreen> {
  List<CameraRequest>? _items;
  StreamSubscription<String>? _sub;
  Timer? _timer;
  bool _loading = false;

  @override
  void initState() {
    super.initState();
    _load();
    final api = context.read<ApiService>();
    _sub = api.syncStream.listen((_) => _load());
    _timer = Timer.periodic(const Duration(seconds: 3), (_) => _load());
  }

  @override
  void dispose() {
    _sub?.cancel();
    _timer?.cancel();
    super.dispose();
  }

  Future<void> _load() async {
    final api = context.read<ApiService>();
    final list = await api.fetchCameraRequests();
    if (!mounted) return;
    setState(() => _items = list.toList()..sort((a, b) => b.requestTime.compareTo(a.requestTime)));
  }

  String _formatTime(DateTime t) {
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final yesterday = today.subtract(const Duration(days: 1));
    final target = DateTime(t.year, t.month, t.day);
    if (target == today) return '今天 ${DateFormat('HH:mm').format(t)}';
    if (target == yesterday) return '昨天 ${DateFormat('HH:mm').format(t)}';
    return DateFormat('MM-dd HH:mm').format(t);
  }

  String _remainingTime(DateTime? expiresAt) {
    if (expiresAt == null) return '';
    final diff = expiresAt.difference(DateTime.now());
    if (diff.isNegative) return '已过期';
    final hours = diff.inHours;
    final minutes = diff.inMinutes % 60;
    if (hours > 0) return '剩余 ${hours}小时${minutes}分';
    return '剩余 ${minutes}分钟';
  }

  Future<void> _approve(CameraRequest req) async {
    final api = context.read<ApiService>();
    await api.approveCameraRequest(req.id.toString());
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('已同意社区人员查看监控（有效期24小时）')),
    );
    _load();
  }

  Future<void> _reject(CameraRequest req) async {
    final api = context.read<ApiService>();
    await api.rejectCameraRequest(req.id.toString());
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('已拒绝监控申请')),
    );
    _load();
  }

  Future<void> _revoke() async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (c) => AlertDialog(
        title: const Text('撤销监控权限'),
        content: const Text('确定要撤销社区人员查看监控的权限吗？撤销后对方将立即失去监控画面。'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(c, false), child: const Text('取消')),
          FilledButton(onPressed: () => Navigator.pop(c, true), child: const Text('确定撤销')),
        ],
      ),
    );
    if (ok != true || !mounted) return;
    final api = context.read<ApiService>();
    setState(() => _loading = true);
    try {
      final list = await api.fetchCameraRequests();
      final approved = list.where((r) => r.status == 'approved').toList();
      if (approved.isNotEmpty) {
        await api.revokeCameraAuth(approved.first.id.toString());
      }
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('已撤销监控权限')),
        );
      }
      await _load();
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.toString())));
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          '监控申请',
          style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Colors.white),
        ),
        flexibleSpace: _appBarGradient(),
        foregroundColor: Colors.white,
      ),
      body: RefreshIndicator(
        onRefresh: _load,
        child: (_items == null)
            ? const Center(child: CircularProgressIndicator())
            : (_items!.isEmpty)
                ? ListView(
                    physics: const AlwaysScrollableScrollPhysics(),
                    children: [
                      SizedBox(height: MediaQuery.sizeOf(context).height * 0.2),
                      const Center(child: Text('🛡️', style: TextStyle(fontSize: 48))),
                      const SizedBox(height: 12),
                      const Center(child: Text('暂无监控申请', style: TextStyle(fontSize: 16))),
                    ],
                  )
                : ListView.separated(
                    physics: const AlwaysScrollableScrollPhysics(),
                    padding: const EdgeInsets.fromLTRB(16, 12, 16, 24),
                    itemCount: _items!.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 10),
                    itemBuilder: (context, i) {
                      final r = _items![i];
                      final isPending = r.status == 'pending';
                      final isApproved = r.status == 'approved';
                      return Container(
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(12),
                          boxShadow: [
                            BoxShadow(
                              color: Colors.grey.shade200.withValues(alpha: 0.8),
                              blurRadius: 4,
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
                                  Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                    decoration: BoxDecoration(
                                      color: isPending
                                          ? Colors.orange.shade50
                                          : isApproved
                                              ? Colors.green.shade50
                                              : Colors.grey.shade100,
                                      borderRadius: BorderRadius.circular(6),
                                    ),
                                    child: Text(
                                      isPending ? '待处理' : isApproved ? '已同意' : '已拒绝',
                                      style: TextStyle(
                                        fontSize: 12,
                                        fontWeight: FontWeight.w600,
                                        color: isPending
                                            ? Colors.orange
                                            : isApproved
                                                ? Colors.green
                                                : Colors.grey,
                                      ),
                                    ),
                                  ),
                                  const Spacer(),
                                  Text(
                                    _formatTime(r.requestTime),
                                    style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 12),
                              Text(
                                '申请人员：${r.staffName}（${r.staffPhone}）',
                                style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w500),
                              ),
                              const SizedBox(height: 6),
                              Text(
                                '查看原因：${r.reason}',
                                style: TextStyle(fontSize: 13, color: Colors.grey.shade700),
                              ),
                              if (isApproved && r.expiresAt != null) ...[
                                const SizedBox(height: 8),
                                Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                                  decoration: BoxDecoration(
                                    color: Colors.blue.shade50,
                                    borderRadius: BorderRadius.circular(6),
                                  ),
                                  child: Text(
                                    '剩余 ${_remainingTime(r.expiresAt)}',
                                    style: TextStyle(fontSize: 12, color: Colors.blue.shade700, fontWeight: FontWeight.w500),
                                  ),
                                ),
                                const SizedBox(height: 12),
                                SizedBox(
                                  width: double.infinity,
                                  child: FilledButton.icon(
                                    onPressed: () => _revoke(),
                                    icon: const Icon(Icons.stop_circle_outlined, size: 18),
                                    label: const Text('停止查看 / 撤销权限'),
                                    style: FilledButton.styleFrom(backgroundColor: Colors.red),
                                  ),
                                ),
                              ],
                              if (isPending) ...[
                                const SizedBox(height: 12),
                                Row(
                                  children: [
                                    Expanded(
                                      child: OutlinedButton(
                                        onPressed: () => _reject(r),
                                        style: OutlinedButton.styleFrom(
                                          foregroundColor: Colors.grey.shade700,
                                          side: BorderSide(color: Colors.grey.shade300),
                                        ),
                                        child: const Text('拒绝'),
                                      ),
                                    ),
                                    const SizedBox(width: 12),
                                    Expanded(
                                      child: FilledButton(
                                        onPressed: () => _approve(r),
                                        style: FilledButton.styleFrom(backgroundColor: const Color(0xFF2C7DA0)),
                                        child: const Text('同意（24小时）'),
                                      ),
                                    ),
                                  ],
                                ),
                              ],
                            ],
                          ),
                        ),
                      );
                    },
                  ),
      ),
    );
  }
}

Widget _appBarGradient() {
  return Container(
    decoration: const BoxDecoration(
      gradient: LinearGradient(
        colors: [Color(0xFF2C7DA0), Color(0xFF4A90E2)],
        begin: Alignment.topLeft,
        end: Alignment.bottomRight,
      ),
    ),
  );
}
