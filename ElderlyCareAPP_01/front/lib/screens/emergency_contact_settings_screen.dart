import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../models/emergency_contact_entry.dart';
import '../services/api_service.dart';
import '../services/emergency_contact_store.dart';

class EmergencyContactSettingsScreen extends StatefulWidget {
  const EmergencyContactSettingsScreen({super.key});

  @override
  State<EmergencyContactSettingsScreen> createState() =>
      _EmergencyContactSettingsScreenState();
}

class _EmergencyContactSettingsScreenState
    extends State<EmergencyContactSettingsScreen> {
  List<EmergencyContactEntry> _items = [];
  bool _loading = true;
  bool _syncing = false;

  @override
  void initState() {
    super.initState();
    _reload();
  }

  Future<void> _reload() async {
    // Load local contacts first for instant display
    final localList = await EmergencyContactStore.loadAll();

    // Try to fetch from backend and merge
    try {
      final api = context.read<ApiService>();
      final eid = api.elderId;
      if (eid != null && eid.isNotEmpty) {
        final remoteList = await api.fetchEmergencyContacts(eid);
        final merged = _mergeContacts(localList, remoteList);
        if (!mounted) return;
        setState(() {
          _items = merged;
          _loading = false;
        });
        // Save merged list back to local store
        await EmergencyContactStore.saveAll(merged);
        return;
      }
    } catch (_) {
      // Backend unavailable, use local data only
    }

    if (!mounted) return;
    setState(() {
      _items = localList;
      _loading = false;
    });
  }

  /// Merge local and remote contacts. Remote wins for same phone number.
  List<EmergencyContactEntry> _mergeContacts(
    List<EmergencyContactEntry> local,
    List<Map<String, dynamic>> remote,
  ) {
    final merged = <String, EmergencyContactEntry>{};
    // Add local entries first, keyed by normalized phone
    for (final e in local) {
      final key = e.phone.replaceAll(RegExp(r'\D'), '');
      merged[key] = e;
    }
    // Remote entries override local by phone
    for (final r in remote) {
      final phone = (r['phone'] as String? ?? '').replaceAll(RegExp(r'\D'), '');
      if (phone.isNotEmpty) {
        merged[phone] = EmergencyContactEntry(
          id: r['contactId']?.toString() ?? EmergencyContactStore.createDraft(name: r['name'] as String? ?? '', phone: r['phone'] as String? ?? '').id,
          name: r['name'] as String? ?? '',
          phone: r['phone'] as String? ?? '',
        );
      }
    }
    return merged.values.toList();
  }

  Future<void> _syncToBackend(EmergencyContactEntry entry, {bool isNew = true}) async {
    try {
      final api = context.read<ApiService>();
      final eid = api.elderId;
      if (eid == null || eid.isEmpty) return;

      final data = <String, dynamic>{
        'elderId': eid,
        'name': entry.name,
        'phone': entry.phone,
      };

      if (isNew) {
        final result = await api.createEmergencyContact(data);
        // Update local entry with backend ID
        final remoteId = result['contactId']?.toString();
        if (remoteId != null && remoteId.isNotEmpty) {
          final updated = EmergencyContactEntry(id: remoteId, name: entry.name, phone: entry.phone);
          await EmergencyContactStore.update(updated);
        }
      } else {
        await api.updateEmergencyContact(entry.id, data);
      }
    } catch (_) {
      // Backend sync is best-effort; local storage is primary
    }
  }

  Future<void> _deleteFromBackend(String contactId) async {
    try {
      final api = context.read<ApiService>();
      await api.deleteEmergencyContact(contactId);
    } catch (_) {
      // Best-effort delete
    }
  }

  Future<void> _confirmDelete(EmergencyContactEntry e) async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (c) => AlertDialog(
        title: const Text('删除联系人'),
        content: Text('确定删除「${e.name.isEmpty ? e.phone : e.name}」吗？'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(c, false), child: const Text('取消')),
          FilledButton(
            onPressed: () => Navigator.pop(c, true),
            child: const Text('删除'),
          ),
        ],
      ),
    );
    if (ok != true) {
      return;
    }
    await EmergencyContactStore.removeById(e.id);
    await _deleteFromBackend(e.id);
    await _reload();
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('已删除并同步到云端')),
      );
    }
  }

  Future<void> _openEditor({EmergencyContactEntry? existing}) async {
    final nameCtrl = TextEditingController(text: existing?.name ?? '');
    final phoneCtrl = TextEditingController(text: existing?.phone ?? '');
    final saved = await showModalBottomSheet<bool>(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (ctx) {
        final bottom = MediaQuery.of(ctx).viewInsets.bottom;
        return Padding(
          padding: EdgeInsets.only(bottom: bottom),
          child: Container(
            margin: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Theme.of(ctx).colorScheme.surface,
              borderRadius: BorderRadius.circular(20),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withValues(alpha: 0.12),
                  blurRadius: 24,
                  offset: const Offset(0, 8),
                ),
              ],
            ),
            child: Padding(
              padding: const EdgeInsets.fromLTRB(20, 16, 20, 20),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Center(
                    child: Container(
                      width: 36,
                      height: 4,
                      decoration: BoxDecoration(
                        color: Colors.black26,
                        borderRadius: BorderRadius.circular(2),
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    existing == null ? '添加紧急联系人' : '编辑联系人',
                    style: Theme.of(ctx).textTheme.titleLarge?.copyWith(
                          fontWeight: FontWeight.w600,
                        ),
                  ),
                  const SizedBox(height: 16),
                  TextField(
                    controller: nameCtrl,
                    decoration: const InputDecoration(labelText: '姓名'),
                    textInputAction: TextInputAction.next,
                  ),
                  const SizedBox(height: 12),
                  TextField(
                    controller: phoneCtrl,
                    decoration: const InputDecoration(labelText: '手机号码'),
                    keyboardType: TextInputType.phone,
                    inputFormatters: [
                      FilteringTextInputFormatter.allow(RegExp(r'[\d+\s-]')),
                    ],
                  ),
                  const SizedBox(height: 20),
                  FilledButton(
                    onPressed: () {
                      final phone = phoneCtrl.text.trim();
                      if (phone.isEmpty) {
                        ScaffoldMessenger.of(ctx).showSnackBar(
                          const SnackBar(content: Text('请填写手机号码')),
                        );
                        return;
                      }
                      Navigator.pop(ctx, true);
                    },
                    child: const Text('保存'),
                  ),
                  const SizedBox(height: 8),
                ],
              ),
            ),
          ),
        );
      },
    );
    if (saved != true) {
      return;
    }
    final name = nameCtrl.text.trim();
    final phone = phoneCtrl.text.trim();
    if (existing == null) {
      final entry = EmergencyContactStore.createDraft(name: name, phone: phone);
      await EmergencyContactStore.add(entry);
      await _syncToBackend(entry, isNew: true);
    } else {
      final updated = existing.copyWith(name: name, phone: phone);
      await EmergencyContactStore.update(updated);
      await _syncToBackend(updated, isNew: false);
    }
    await _reload();
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('已保存并同步到云端')),
      );
    }
  }

  Future<void> _onReorder(int oldIndex, int newIndex) async {
    setState(() {
      if (newIndex > oldIndex) {
        newIndex -= 1;
      }
      final x = _items.removeAt(oldIndex);
      _items.insert(newIndex, x);
    });
    await EmergencyContactStore.saveAll(_items);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('紧急联系人'),
        actions: [
          IconButton(
            tooltip: '刷新',
            onPressed: _reload,
            icon: const Icon(Icons.refresh_rounded),
          ),
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Padding(
                  padding: const EdgeInsets.fromLTRB(16, 12, 16, 8),
                  child: Text(
                    '保存在本机，用于 SOS。拖动 ≡ 可调整顺序，首位将用于一键呼叫与短信。',
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: Colors.black45,
                        ),
                  ),
                ),
                Expanded(
                  child: _items.isEmpty
                      ? Center(
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(Icons.people_outline,
                                  size: 56, color: Colors.grey.shade400),
                              const SizedBox(height: 12),
                              Text(
                                '暂无联系人',
                                style: Theme.of(context)
                                    .textTheme
                                    .titleMedium
                                    ?.copyWith(color: Colors.black45),
                              ),
                              const SizedBox(height: 8),
                              TextButton(
                                onPressed: () => _openEditor(),
                                child: const Text('添加第一个联系人'),
                              ),
                            ],
                          ),
                        )
                      : ReorderableListView.builder(
                          padding: const EdgeInsets.fromLTRB(16, 0, 16, 100),
                          buildDefaultDragHandles: false,
                          itemCount: _items.length,
                          onReorder: _onReorder,
                          itemBuilder: (context, i) {
                            final e = _items[i];
                            return Padding(
                              key: ValueKey(e.id),
                              padding: const EdgeInsets.only(bottom: 10),
                              child: Card(
                                child: Padding(
                                  padding:
                                      const EdgeInsets.fromLTRB(4, 8, 8, 8),
                                  child: Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.center,
                                    children: [
                                      ReorderableDragStartListener(
                                        index: i,
                                        child: Padding(
                                          padding: const EdgeInsets.symmetric(
                                              horizontal: 8),
                                          child: Icon(
                                            Icons.drag_handle_rounded,
                                            color: Colors.grey.shade500,
                                          ),
                                        ),
                                      ),
                                      CircleAvatar(
                                        backgroundColor: Theme.of(context)
                                            .colorScheme
                                            .primaryContainer,
                                        foregroundColor: Theme.of(context)
                                            .colorScheme
                                            .onPrimaryContainer,
                                        child: Text(
                                          e.name.isNotEmpty ? e.name[0] : '#',
                                          style: const TextStyle(
                                            fontWeight: FontWeight.w600,
                                          ),
                                        ),
                                      ),
                                      const SizedBox(width: 12),
                                      Expanded(
                                        child: Column(
                                          crossAxisAlignment:
                                              CrossAxisAlignment.start,
                                          children: [
                                            Text(
                                              e.name.isEmpty
                                                  ? '未命名'
                                                  : e.name,
                                              style: const TextStyle(
                                                fontWeight: FontWeight.w600,
                                                fontSize: 16,
                                              ),
                                            ),
                                            const SizedBox(height: 4),
                                            Text(
                                              e.phone,
                                              style: TextStyle(
                                                color: Colors.grey.shade700,
                                                fontSize: 14,
                                              ),
                                            ),
                                          ],
                                        ),
                                      ),
                                      TextButton(
                                        onPressed: () =>
                                            _openEditor(existing: e),
                                        child: const Text('编辑'),
                                      ),
                                      TextButton(
                                        onPressed: () => _confirmDelete(e),
                                        style: TextButton.styleFrom(
                                          foregroundColor: Colors.red,
                                        ),
                                        child: const Text('删除'),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            );
                          },
                        ),
                ),
              ],
            ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => _openEditor(),
        icon: const Icon(Icons.person_add_alt_1_outlined),
        label: const Text('添加新联系人'),
      ),
    );
  }
}
