import 'package:flutter/material.dart';
import '../models/snapshot_record.dart';
import '../services/snapshot_store.dart';

/// 近期抓拍记录页面 —— 显示最近 5 次门外陌生人抓拍
class SnapshotHistoryScreen extends StatefulWidget {
  const SnapshotHistoryScreen({super.key});

  @override
  State<SnapshotHistoryScreen> createState() => _SnapshotHistoryScreenState();
}

class _SnapshotHistoryScreenState extends State<SnapshotHistoryScreen> {
  List<SnapshotRecord>? _records;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final records = await SnapshotStore.getRecent(5);
    if (mounted) setState(() => _records = records);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          '近期抓拍记录',
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
      body: _records == null
          ? const Center(child: CircularProgressIndicator())
          : _records!.isEmpty
              ? Center(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(Icons.photo_camera_front_outlined,
                          size: 64, color: Colors.grey.shade400),
                      const SizedBox(height: 16),
                      Text('暂无抓拍记录',
                          style: TextStyle(
                              fontSize: 15, color: Colors.grey.shade600)),
                    ],
                  ),
                )
              : ListView.builder(
                  padding: const EdgeInsets.all(16),
                  itemCount: _records!.length,
                  itemBuilder: (context, index) {
                    final record = _records![index];
                    return _buildSnapshotCard(record, index == 0);
                  },
                ),
    );
  }

  Widget _buildSnapshotCard(SnapshotRecord record, bool isLatest) {
    return Container(
      margin: const EdgeInsets.only(bottom: 14),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: isLatest
            ? Border.all(
                color: const Color(0xFF4A90E2).withValues(alpha: 0.5))
            : null,
        boxShadow: [
          BoxShadow(
            color: Colors.grey.shade200.withValues(alpha: 0.8),
            blurRadius: 6,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      clipBehavior: Clip.antiAlias,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 抓拍图片
          ClipRRect(
            borderRadius:
                const BorderRadius.vertical(top: Radius.circular(12)),
            child: Image.asset(
              record.imageAssetPath,
              width: double.infinity,
              height: 200,
              fit: BoxFit.cover,
              errorBuilder: (_, __, ___) => Container(
                height: 200,
                color: Colors.grey.shade100,
                child: Center(
                  child: Icon(Icons.broken_image_outlined,
                      size: 48, color: Colors.grey.shade400),
                ),
              ),
            ),
          ),
          // 信息行
          Padding(
            padding: const EdgeInsets.fromLTRB(14, 10, 14, 12),
            child: Row(
              children: [
                Container(
                  width: 36,
                  height: 36,
                  decoration: BoxDecoration(
                    color: const Color(0xFFF0F7FF),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: const Icon(Icons.camera_outdoor,
                      size: 20, color: Color(0xFF2C7DA0)),
                ),
                const SizedBox(width: 10),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        '门外陌生人抓拍',
                        style: TextStyle(
                          fontSize: 14,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                      Text(
                        record.formattedTime,
                        style: TextStyle(
                          fontSize: 12,
                          color: Colors.grey.shade500,
                        ),
                      ),
                    ],
                  ),
                ),
                if (isLatest)
                  Container(
                    padding:
                        const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                    decoration: BoxDecoration(
                      color: const Color(0xFF4A90E2).withValues(alpha: 0.1),
                      borderRadius: BorderRadius.circular(6),
                    ),
                    child: const Text(
                      '最新',
                      style: TextStyle(
                        fontSize: 11,
                        color: Color(0xFF4A90E2),
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
