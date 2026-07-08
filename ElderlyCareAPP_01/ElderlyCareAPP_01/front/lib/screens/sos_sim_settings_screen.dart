import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:sim_reader/sim_reader.dart';

import '../services/sos_sim_store.dart';

class SosSimSettingsScreen extends StatefulWidget {
  const SosSimSettingsScreen({super.key});

  @override
  State<SosSimSettingsScreen> createState() => _SosSimSettingsScreenState();
}

class _SosSimSettingsScreenState extends State<SosSimSettingsScreen> {
  bool _loading = true;
  String? _error;
  List<SimInfo> _simCards = [];
  int _selectedSlot = 1;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() {
      _loading = true;
      _error = null;
    });

    try {
      final status = await Permission.phone.status;
      if (!status.isGranted) {
        final req = await Permission.phone.request();
        if (!req.isGranted) {
          setState(() {
            _error = '需要电话权限才能读取 SIM 卡信息';
            _loading = false;
          });
          return;
        }
      }

      final hasSim = await SimReader.hasSimCard();
      if (!hasSim) {
        setState(() {
          _error = '未检测到 SIM 卡';
          _loading = false;
        });
        return;
      }

      final sims = await SimReader.getAllSimInfo();
      final slot = await SosSimStore.getDefaultSimSlot();

      if (!mounted) return;
      setState(() {
        _simCards = sims;
        _selectedSlot = slot;
        _loading = false;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _error = '读取 SIM 卡信息失败：$e';
        _loading = false;
      });
    }
  }

  Future<void> _selectSlot(int slot) async {
    await SosSimStore.setDefaultSimSlot(slot);
    if (!mounted) return;
    setState(() => _selectedSlot = slot);
  }

  String _simLabel(SimInfo sim) {
    final slot = sim.simSlotIndex ?? 0;
    final carrier = sim.carrierName ?? '未知运营商';
    return '卡槽 ${slot + 1} — $carrier';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'SOS 默认拨号卡',
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
        onRefresh: _load,
        child: _buildBody(),
      ),
    );
  }

  Widget _buildBody() {
    if (_loading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_error != null) {
      return ListView(
        physics: const AlwaysScrollableScrollPhysics(),
        padding: const EdgeInsets.all(24),
        children: [
          const SizedBox(height: 40),
          Icon(Icons.error_outline, size: 56, color: Colors.grey.shade400),
          const SizedBox(height: 16),
          Text(
            _error!,
            textAlign: TextAlign.center,
            style: TextStyle(fontSize: 16, color: Colors.grey.shade600),
          ),
          const SizedBox(height: 24),
          Center(
            child: FilledButton(
              onPressed: _load,
              child: const Text('重试'),
            ),
          ),
          if (_error!.contains('权限'))
            Padding(
              padding: const EdgeInsets.only(top: 12),
              child: Center(
                child: TextButton(
                  onPressed: openAppSettings,
                  child: const Text('去系统设置开启权限'),
                ),
              ),
            ),
        ],
      );
    }

    if (_simCards.isEmpty) {
      return ListView(
        physics: const AlwaysScrollableScrollPhysics(),
        padding: const EdgeInsets.all(24),
        children: [
          const SizedBox(height: 40),
          Icon(Icons.sim_card_alert, size: 56, color: Colors.grey.shade400),
          const SizedBox(height: 16),
          Text(
            '未检测到可用的 SIM 卡',
            textAlign: TextAlign.center,
            style: TextStyle(fontSize: 16, color: Colors.grey.shade600),
          ),
        ],
      );
    }

    return ListView(
      physics: const AlwaysScrollableScrollPhysics(),
      padding: const EdgeInsets.all(16),
      children: [
        const Text(
          '选择 SOS 紧急呼叫时使用的默认 SIM 卡（同时用于电话和短信）：',
          style: TextStyle(fontSize: 15, color: Colors.black54),
        ),
        const SizedBox(height: 12),
        Card(
          clipBehavior: Clip.antiAlias,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          child: Column(
            children: [
              for (var i = 0; i < _simCards.length; i++) ...[
                if (i > 0) const Divider(height: 1),
                ListTile(
                  leading: Icon(
                    _selectedSlot == (_simCards[i].simSlotIndex ?? 0) + 1
                        ? Icons.check_circle
                        : Icons.radio_button_unchecked,
                    color: _selectedSlot == (_simCards[i].simSlotIndex ?? 0) + 1
                        ? const Color(0xFF4A90E2)
                        : Colors.grey.shade400,
                  ),
                  title: Text(_simLabel(_simCards[i])),
                  trailing: const Icon(Icons.sim_card_outlined),
                  onTap: () => _selectSlot((_simCards[i].simSlotIndex ?? 0) + 1),
                ),
              ],
            ],
          ),
        ),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(
            color: Colors.orange.shade50,
            borderRadius: BorderRadius.circular(8),
          ),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Icon(Icons.info_outline, size: 18, color: Colors.orange.shade700),
              const SizedBox(width: 8),
              Expanded(
                child: Text(
                  '若设备只有单卡，选择卡槽 1 即可。双卡设备可根据运营商灵活选择，SOS 拨打电话和发送位置短信将同时使用该卡。',
                  style: TextStyle(
                    fontSize: 13,
                    color: Colors.orange.shade800,
                  ),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}
