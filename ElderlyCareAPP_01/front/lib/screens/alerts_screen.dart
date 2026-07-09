import 'dart:async';

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import '../models/notification_item.dart';
import '../services/api_service.dart';
import 'camera_request_screen.dart';
import 'service_request_screen.dart';

class AlertsScreen extends StatefulWidget {
  const AlertsScreen({super.key});

  @override
  State<AlertsScreen> createState() => _AlertsScreenState();
}

class _AlertsScreenState extends State<AlertsScreen> {
  List<NotificationItem>? _items;
  String _filter = 'all';
  StreamSubscription<String>? _sub;
  Timer? _timer;

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
    try {
      final api = context.read<ApiService>();
      final list = await api.fetchNotifications();
      if (!mounted) return;
      setState(() => _items = list.toList()..sort((a, b) => b.time.compareTo(a.time)));
    } catch (_) {
      if (mounted) setState(() => _items = []);
    }
  }

  (IconData icon, Color color) _style(NotificationType t) {
    return switch (t) {
      NotificationType.alert => (Icons.warning_amber_rounded, Colors.red),
      NotificationType.order => (Icons.assignment_turned_in, Colors.green),
      NotificationType.camera => (Icons.videocam, Colors.blue),
      NotificationType.service => (Icons.build, Colors.orange),
    };
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

  List<NotificationItem> get _filtered {
    if (_items == null) return [];
    if (_filter == 'all') return _items!;
    return _items!.where((n) => n.type.apiValue.toLowerCase() == _filter.toLowerCase()).toList();
  }

  void _onTapItem(NotificationItem n) {
    setState(() => n.read = true);
    if (n.type == NotificationType.camera) {
      Navigator.of(context).push(
        MaterialPageRoute<void>(builder: (_) => const CameraRequestScreen()),
      );
    } else if (n.type == NotificationType.order) {
      Navigator.of(context).push(
        MaterialPageRoute<void>(builder: (_) => const ServiceRequestScreen()),
      );
    } else {
      final snapshot = n.extra?['snapshot'] as String?;
      showDialog<void>(
        context: context,
        builder: (c) => AlertDialog(
          title: Text(n.title),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('${n.content}\n\n时间：${_formatTime(n.time)}'),
              if (snapshot != null && snapshot.isNotEmpty) ...[
                const SizedBox(height: 12),
                ClipRRect(
                  borderRadius: BorderRadius.circular(8),
                  child: Image.network(
                    snapshot,
                    height: 160,
                    width: double.infinity,
                    fit: BoxFit.cover,
                    errorBuilder: (_, __, ___) => Container(
                      height: 100,
                      color: Colors.grey.shade200,
                      child: const Center(child: Text('快照加载失败')),
                    ),
                  ),
                ),
              ],
            ],
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(c), child: const Text('关闭')),
          ],
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final count = _items?.where((n) => !n.read).length ?? 0;
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          '消息中心',
          style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Colors.white),
        ),
        flexibleSpace: _appBarGradient(),
        foregroundColor: Colors.white,
        actions: [
          Padding(
            padding: const EdgeInsets.only(right: 12),
            child: Center(
              child: Badge(
                isLabelVisible: count > 0,
                label: Text('$count'),
                child: const Icon(Icons.notifications_none),
              ),
            ),
          ),
        ],
      ),
      body: Column(
        children: [
          // Filter chips
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
            child: SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Row(
                children: [
                  _FilterChip(label: '全部', value: 'all', selected: _filter, onSelected: (v) => setState(() => _filter = v)),
                  const SizedBox(width: 8),
                  _FilterChip(label: '告警', value: 'ALERT', selected: _filter, onSelected: (v) => setState(() => _filter = v)),
                  const SizedBox(width: 8),
                  _FilterChip(label: '工单', value: 'ORDER', selected: _filter, onSelected: (v) => setState(() => _filter = v)),
                  const SizedBox(width: 8),
                  _FilterChip(label: '申请', value: 'CAMERA', selected: _filter, onSelected: (v) => setState(() => _filter = v)),
                ],
              ),
            ),
          ),
          // Quick actions
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Row(
              children: [
                Expanded(
                  child: _ActionCard(
                    icon: Icons.videocam,
                    label: '监控申请',
                    color: Colors.blue,
                    onTap: () => Navigator.of(context).push(
                      MaterialPageRoute<void>(builder: (_) => const CameraRequestScreen()),
                    ),
                  ),
                ),
                const SizedBox(width: 10),
                Expanded(
                  child: _ActionCard(
                    icon: Icons.build,
                    label: '服务申请',
                    color: Colors.orange,
                    onTap: () => Navigator.of(context).push(
                      MaterialPageRoute<void>(builder: (_) => const ServiceRequestScreen()),
                    ),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 8),
          // List
          Expanded(
            child: RefreshIndicator(
              onRefresh: _load,
              child: (_items == null)
                  ? const Center(child: CircularProgressIndicator())
                  : _filtered.isEmpty
                      ? ListView(
                          physics: const AlwaysScrollableScrollPhysics(),
                          children: [
                            SizedBox(height: MediaQuery.sizeOf(context).height * 0.15),
                            const Center(child: Text('📭', style: TextStyle(fontSize: 48))),
                            const SizedBox(height: 12),
                            const Center(child: Text('暂无消息', style: TextStyle(fontSize: 16))),
                          ],
                        )
                      : ListView.separated(
                          physics: const AlwaysScrollableScrollPhysics(),
                          padding: const EdgeInsets.fromLTRB(16, 4, 16, 24),
                          itemCount: _filtered.length,
                          separatorBuilder: (_, __) => const SizedBox(height: 10),
                          itemBuilder: (context, i) {
                            final n = _filtered[i];
                            final st = _style(n.type);
                            return _NotificationCard(
                              item: n,
                              icon: st.$1,
                              iconColor: st.$2,
                              time: _formatTime(n.time),
                              onTap: () => _onTapItem(n),
                            );
                          },
                        ),
            ),
          ),
        ],
      ),
    );
  }
}

class _FilterChip extends StatelessWidget {
  const _FilterChip({required this.label, required this.value, required this.selected, required this.onSelected});
  final String label;
  final String value;
  final String selected;
  final ValueChanged<String> onSelected;

  @override
  Widget build(BuildContext context) {
    final isSelected = selected == value;
    return ChoiceChip(
      label: Text(label),
      selected: isSelected,
      onSelected: (_) => onSelected(value),
      selectedColor: const Color(0xFF2C7DA0),
      labelStyle: TextStyle(color: isSelected ? Colors.white : Colors.grey.shade700, fontSize: 13),
    );
  }
}

class _ActionCard extends StatelessWidget {
  const _ActionCard({required this.icon, required this.label, required this.color, required this.onTap});
  final IconData icon;
  final String label;
  final Color color;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.white,
      borderRadius: BorderRadius.circular(12),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 14),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(12),
            boxShadow: [
              BoxShadow(
                color: Colors.grey.shade200.withValues(alpha: 0.8),
                blurRadius: 4,
                offset: const Offset(0, 2),
              ),
            ],
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(icon, color: color, size: 20),
              const SizedBox(width: 6),
              Text(label, style: TextStyle(fontSize: 14, fontWeight: FontWeight.w600, color: color)),
            ],
          ),
        ),
      ),
    );
  }
}

class _NotificationCard extends StatefulWidget {
  const _NotificationCard({required this.item, required this.icon, required this.iconColor, required this.time, required this.onTap});
  final NotificationItem item;
  final IconData icon;
  final Color iconColor;
  final String time;
  final VoidCallback onTap;

  @override
  State<_NotificationCard> createState() => _NotificationCardState();
}

class _NotificationCardState extends State<_NotificationCard> {
  bool _pressed = false;

  @override
  Widget build(BuildContext context) {
    final n = widget.item;
    return AnimatedScale(
      scale: _pressed ? 0.98 : 1.0,
      duration: const Duration(milliseconds: 150),
      child: Container(
        decoration: BoxDecoration(
          color: n.read ? Colors.white : Colors.white,
          borderRadius: BorderRadius.circular(12),
          boxShadow: [
            BoxShadow(
              color: Colors.grey.shade200.withValues(alpha: 0.8),
              blurRadius: 4,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: ClipRRect(
          borderRadius: BorderRadius.circular(12),
          child: Material(
            color: n.read ? Colors.white : const Color(0xFFF0F7FF),
            child: InkWell(
              onTapDown: (_) => setState(() => _pressed = true),
              onTapUp: (_) {
                setState(() => _pressed = false);
                widget.onTap();
              },
              onTapCancel: () => setState(() => _pressed = false),
              child: Row(
                children: [
                  Container(
                    width: 4,
                    height: 76,
                    color: widget.iconColor,
                  ),
                  Expanded(
                    child: ListTile(
                      contentPadding: const EdgeInsets.symmetric(horizontal: 12),
                      leading: Stack(
                        children: [
                          Icon(widget.icon, color: widget.iconColor),
                          if (!n.read)
                            Positioned(
                              top: 0,
                              right: 0,
                              child: Container(
                                width: 8,
                                height: 8,
                                decoration: BoxDecoration(color: Colors.red, shape: BoxShape.circle, border: Border.all(color: Colors.white, width: 1)),
                              ),
                            ),
                        ],
                      ),
                      title: Text(
                        n.title,
                        style: TextStyle(
                          fontWeight: n.read ? FontWeight.normal : FontWeight.w600,
                          fontSize: 15,
                        ),
                      ),
                      subtitle: Text(
                        n.content,
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        style: TextStyle(fontSize: 13, color: Colors.grey.shade700),
                      ),
                      trailing: Text(
                        widget.time,
                        style: TextStyle(color: Colors.grey.shade600, fontSize: 12),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
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
