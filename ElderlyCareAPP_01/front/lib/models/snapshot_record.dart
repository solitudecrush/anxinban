/// 门外陌生人抓拍记录模型
class SnapshotRecord {
  SnapshotRecord({
    required this.id,
    required this.imageAssetPath,
    required this.capturedAt,
  });

  final String id; // 唯一标识，如 'snap_001'
  final String imageAssetPath; // 资源路径，如 'assets/snapshots/snapshot_1.png'
  final DateTime capturedAt; // 抓拍时间

  /// 格式化的抓拍时间（用于显示）
  String get formattedTime {
    final now = DateTime.now();
    final diff = now.difference(capturedAt);
    if (diff.inMinutes < 1) return '刚刚';
    if (diff.inMinutes < 60) return '${diff.inMinutes}分钟前';
    if (diff.inHours < 24) return '${diff.inHours}小时前';
    if (diff.inDays < 7) return '${diff.inDays}天前';
    return '${capturedAt.month}/${capturedAt.day} ${capturedAt.hour}:${capturedAt.minute.toString().padLeft(2, '0')}';
  }

  factory SnapshotRecord.fromJson(Map<String, dynamic> json) {
    return SnapshotRecord(
      id: json['id'] as String,
      imageAssetPath: json['imageAssetPath'] as String,
      capturedAt: DateTime.parse(json['capturedAt'] as String),
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'imageAssetPath': imageAssetPath,
        'capturedAt': capturedAt.toIso8601String(),
      };
}
