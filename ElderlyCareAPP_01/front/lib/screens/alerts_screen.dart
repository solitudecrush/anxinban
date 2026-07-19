import 'dart:async';

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../models/notification_item.dart';
import '../services/api_service.dart';
import '../state/nav_controller.dart';
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
  bool _isAggregated = false;

  /// 本地已读记录（ID 集合），防止后端写入未完成时重载覆盖已读状态。
  final Set<String> _localReadIds = {};

  @override
  void initState() {
    super.initState();
    _load();
    final api = context.read<ApiService>();
    _sub = api.syncStream.listen((_) => _load());
    _timer = Timer.periodic(const Duration(seconds: 10), (_) => _load());
    // 监听 NavController，当首页"查看更多"点击时消费初始筛选参数
    context.read<NavController>().addListener(_onNavChanged);
  }

  void _onNavChanged() {
    final filter = context.read<NavController>().consumeMessageFilter();
    if (filter != null && mounted) {
      setState(() => _filter = filter);
    }
  }

  @override
  void dispose() {
    context.read<NavController>().removeListener(_onNavChanged);
    _sub?.cancel();
    _timer?.cancel();
    super.dispose();
  }

  Future<void> _load() async {
    // 消费 NavController 传入的初始筛选参数（由首页"查看更多"传入）
    final nav = context.read<NavController>();
    final initialFilter = nav.consumeMessageFilter();
    if (initialFilter != null && mounted) {
      setState(() => _filter = initialFilter);
    }

    try {
      final api = context.read<ApiService>();
      // Ensure userId is available; fall back to SharedPreferences
      final prefs = await SharedPreferences.getInstance();
      if (api.userId == null || api.userId!.isEmpty) {
        final uid = prefs.getString('app_user_id');
        if (uid != null && uid.isNotEmpty) {
          api.setUserId(uid);
        }
      }
      // Always fetch both notifications and aggregated alerts for a complete view.
      // Previously notifications from app_notification table and alerts from alert table
      // were queried separately, causing alerts to be missing in the message center
      // when app_notification had any records (even non-alert ones).
      final notifList = await api.fetchNotifications();
      final aggregated = await _aggregateMessages(api, prefs);
      if (!mounted) return;

      // Merge notifications and aggregated alerts, deduplicating by ID
      final seen = <String>{};
      final merged = <NotificationItem>[];
      for (final item in [...notifList, ...aggregated]) {
        if (seen.add(item.id)) {
          // 本地已读 → 强制标记为已读（防止后端尚未完成写入时被重载覆盖）
          if (_localReadIds.contains(item.id)) {
            item.read = true;
          }
          merged.add(item);
        }
      }
      merged.sort((a, b) => b.time.compareTo(a.time));

      // 清理本地已读记录中后端已确认的 ID（节省内存）
      for (final item in merged) {
        if (item.read && _localReadIds.contains(item.id)) {
          // 不删除——保留到明确不再需要为止。Set 很小，不影响性能。
        }
      }

      setState(() {
        _items = merged;
        _isAggregated = true;
      });
    } catch (_) {
      // On error, try to aggregate from individual sources
      try {
        final api = context.read<ApiService>();
        final prefs = await SharedPreferences.getInstance();
        final aggregated = await _aggregateMessages(api, prefs);
        if (mounted) setState(() => _items = aggregated);
      } catch (_) {
        if (mounted) setState(() => _items = []);
      }
    }
  }

  /// Aggregate messages from alarms, service requests, and camera requests
  /// into NotificationItems when the notification API returns empty.
  Future<List<NotificationItem>> _aggregateMessages(ApiService api, SharedPreferences prefs) async {
    final items = <NotificationItem>[];
    try {
      // Fetch alarms
      final alarms = await api.fetchAlerts();
      for (final alarm in alarms) {
        items.add(NotificationItem(
          id: 'alarm-${alarm.id}',
          type: NotificationType.alert,
          title: '告警通知',
          content: alarm.detail,
          time: alarm.occurredAt,
          read: alarm.isRead,
        ));
      }
    } catch (_) {}
    try {
      // Fetch camera requests
      final cameraReqs = await api.fetchCameraRequests();
      for (final req in cameraReqs) {
        final statusText = req.status == 'approved' ? '已批准' : (req.status == 'rejected' ? '已拒绝' : '待处理');
        items.add(NotificationItem(
          id: 'camera-${req.id}',
          type: NotificationType.camera,
          title: '监控申请',
          content: '${req.staffName} 申请查看${req.elderName}的监控 - $statusText',
          time: req.requestTime,
          read: req.status != 'pending',
        ));
      }
    } catch (_) {}
    try {
      // Fetch service requests (mapped to ORDER type for "工单" filter)
      final srvReqs = await api.fetchServiceRequests();
      for (final req in srvReqs) {
        final statusText = req.status == 'converted' ? '已转为工单' : (req.status == 'rejected' ? '已拒绝' : '处理中');
        items.add(NotificationItem(
          id: 'service-${req.id}',
          type: NotificationType.order,
          title: '服务申请 - ${req.type}',
          content: '${req.content}\n状态: $statusText',
          time: req.requestTime,
          read: req.status != 'pending',
        ));
      }
    } catch (_) {}
    items.sort((a, b) => b.time.compareTo(a.time));
    return items;
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
    setState(() {
      n.read = true;
      _localReadIds.add(n.id);
    });
    // 根据消息来源调用正确的后端 API 持久化已读状态
    final api = context.read<ApiService>();
    try {
      if (n.id.startsWith('alarm-')) {
        // 从 alarm_event 表聚合的消息 → 调用告警已读接口
        final realAlarmId = n.id.substring(6); // remove "alarm-" prefix
        api.markAlarmRead(realAlarmId);
      } else if (n.id.startsWith('camera-') || n.id.startsWith('service-')) {
        // camera_request / service_request 没有已读接口，仅本地记录
      } else {
        // 来自 notification 表的消息 → 调用通知已读接口
        api.markNotificationRead(n.id);
      }
    } catch (_) {
      // Best-effort read sync
    }
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

  Future<void> _markAllRead() async {
    if (_items == null || _items!.isEmpty) return;
    final unreadCount = _items!.where((n) => !n.read).length;
    if (unreadCount == 0) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('没有未读消息')),
      );
      return;
    }
    final ok = await showDialog<bool>(
      context: context,
      builder: (c) => AlertDialog(
        title: const Text('一键已读'),
        content: Text('确定要将全部 $unreadCount 条未读消息标记为已读吗？'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(c, false), child: const Text('取消')),
          FilledButton(onPressed: () => Navigator.pop(c, true), child: const Text('确定')),
        ],
      ),
    );
    if (ok != true || !mounted) return;
    setState(() {
      for (final n in _items!) {
        n.read = true;
        _localReadIds.add(n.id);
      }
    });
    try {
      final api = context.read<ApiService>();
      // 分别标记各类型消息为已读
      for (final n in _items!) {
        try {
          if (n.id.startsWith('alarm-')) {
            final realAlarmId = n.id.substring(6);
            api.markAlarmRead(realAlarmId);
          } else if (!n.id.startsWith('camera-') && !n.id.startsWith('service-')) {
            api.markNotificationRead(n.id);
          }
        } catch (_) {}
      }
      // 同时调用通知表的一键已读
      await api.markAllNotificationsRead();
    } catch (_) {
      // Best-effort sync
    }
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('已全部标记为已读')),
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
            padding: const EdgeInsets.only(right: 4),
            child: Center(
              child: IconButton(
                tooltip: '全部标记为已读',
                onPressed: _markAllRead,
                icon: const Icon(Icons.done_all),
              ),
            ),
          ),
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
