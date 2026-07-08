import 'dart:async';

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import '../models/service_request.dart';
import '../services/api_service.dart';

class ServiceRequestScreen extends StatefulWidget {
  const ServiceRequestScreen({super.key});

  @override
  State<ServiceRequestScreen> createState() => _ServiceRequestScreenState();
}

class _ServiceRequestScreenState extends State<ServiceRequestScreen> {
  List<ServiceRequest>? _items;
  final _formKey = GlobalKey<FormState>();
  String _selectedType = '上门看护';
  final String _elderName = '曾姐'; // 固定为绑定的老人
  final _contentController = TextEditingController();
  StreamSubscription<String>? _sub;
  Timer? _timer;

  final List<String> _types = ['上门看护', '设备维修', '健康咨询', '紧急求助', '生活物资代购'];

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
    _contentController.dispose();
    super.dispose();
  }

  Future<void> _load() async {
    final api = context.read<ApiService>();
    final list = await api.fetchServiceRequests();
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

  Future<void> _submit() async {
    if (!(_formKey.currentState?.validate() ?? false)) return;
    final api = context.read<ApiService>();
    final req = ServiceRequest(
      id: DateTime.now().millisecondsSinceEpoch,
      type: _selectedType,
      elderName: _elderName,
      content: _contentController.text.trim(),
      requestTime: DateTime.now(),
    );
    await api.submitServiceRequest(req);
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('服务申请已提交')),
    );
    _contentController.clear();
    _load();
    Navigator.pop(context);
  }

  void _openCreateSheet() {
    showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) {
        return Padding(
          padding: EdgeInsets.only(
            bottom: MediaQuery.of(context).viewInsets.bottom,
            left: 20,
            right: 20,
            top: 20,
          ),
          child: Form(
            key: _formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      '发起服务申请',
                      style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                    ),
                    IconButton(
                      onPressed: () => Navigator.pop(context),
                      icon: const Icon(Icons.close),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                DropdownButtonFormField<String>(
                  value: _selectedType,
                  decoration: InputDecoration(
                    labelText: '申请类型',
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                  ),
                  items: _types.map((t) => DropdownMenuItem(value: t, child: Text(t))).toList(),
                  onChanged: (v) => setState(() => _selectedType = v!),
                ),
                const SizedBox(height: 12),
                TextFormField(
                  controller: _contentController,
                  decoration: InputDecoration(
                    labelText: '申请内容描述',
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                    alignLabelWithHint: true,
                  ),
                  maxLines: 4,
                  validator: (v) => (v == null || v.trim().isEmpty) ? '请输入申请内容' : null,
                ),
                const SizedBox(height: 20),
                SizedBox(
                  width: double.infinity,
                  child: FilledButton(
                    onPressed: _submit,
                    style: FilledButton.styleFrom(
                      backgroundColor: const Color(0xFF2C7DA0),
                      padding: const EdgeInsets.symmetric(vertical: 14),
                    ),
                    child: const Text('提交申请', style: TextStyle(fontSize: 16)),
                  ),
                ),
                const SizedBox(height: 20),
              ],
            ),
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          '服务申请',
          style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Colors.white),
        ),
        flexibleSpace: _appBarGradient(),
        foregroundColor: Colors.white,
      ),
      body: RefreshIndicator(
        onRefresh: _load,
        child: (_items == null)
            ? const Center(child: CircularProgressIndicator())
            : ListView(
                physics: const AlwaysScrollableScrollPhysics(),
                padding: const EdgeInsets.fromLTRB(16, 12, 16, 24),
                children: [
                  // Create button
                  SizedBox(
                    width: double.infinity,
                    child: FilledButton.icon(
                      onPressed: _openCreateSheet,
                      style: FilledButton.styleFrom(
                        backgroundColor: const Color(0xFF2C7DA0),
                        padding: const EdgeInsets.symmetric(vertical: 14),
                      ),
                      icon: const Icon(Icons.add),
                      label: const Text('发起新申请', style: TextStyle(fontSize: 16)),
                    ),
                  ),
                  const SizedBox(height: 16),
                  if (_items!.isEmpty) ...[
                    SizedBox(height: MediaQuery.sizeOf(context).height * 0.15),
                    const Center(child: Text('📋', style: TextStyle(fontSize: 48))),
                    const SizedBox(height: 12),
                    const Center(child: Text('暂无服务申请', style: TextStyle(fontSize: 16))),
                  ] else
                    ..._items!.map((r) {
                      final statusMap = {
                        'pending': ('待处理', Colors.orange),
                        'converted': ('已受理', Colors.blue),
                        'completed': ('已完成', Colors.green),
                        'ignored': ('已拒绝', Colors.grey),
                      };
                      final s = statusMap[r.status] ?? (r.status == 'completed' ? ('已完成', Colors.green) : ('未知', Colors.grey));
                      return Container(
                        margin: const EdgeInsets.only(bottom: 10),
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
                                      color: s.$2.withValues(alpha: 0.1),
                                      borderRadius: BorderRadius.circular(6),
                                    ),
                                    child: Text(
                                      s.$1,
                                      style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: s.$2),
                                    ),
                                  ),
                                  const Spacer(),
                                  Text(
                                    _formatTime(r.requestTime),
                                    style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 10),
                              Text(
                                '${r.type} · ${r.elderName}',
                                style: const TextStyle(fontSize: 15, fontWeight: FontWeight.w600),
                              ),
                              const SizedBox(height: 6),
                              Text(
                                r.content,
                                style: TextStyle(fontSize: 13, color: Colors.grey.shade700),
                              ),
                              if (r.convertedTo != null) ...[
                                const SizedBox(height: 6),
                                Text(
                                  '关联工单：${r.convertedTo}',
                                  style: TextStyle(fontSize: 12, color: Colors.blue.shade700),
                                ),
                              ],
                            ],
                          ),
                        ),
                      );
                    }),
                ],
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
