enum NotificationType {
  alert('ALERT', '告警消息'),
  order('ORDER', '工单消息'),
  camera('CAMERA', '申请消息'),
  service('SERVICE', '服务消息');

  const NotificationType(this.apiValue, this.labelZh);

  final String apiValue;
  final String labelZh;

  static NotificationType fromApi(String raw) {
    return NotificationType.values.firstWhere(
      (e) => e.apiValue == raw,
      orElse: () => NotificationType.alert,
    );
  }
}

class NotificationItem {
  NotificationItem({
    required this.id,
    required this.type,
    required this.title,
    required this.content,
    required this.time,
    this.read = false,
    this.extra,
  });

  final String id;
  final NotificationType type;
  final String title;
  final String content;
  final DateTime time;
  bool read;
  final Map<String, dynamic>? extra;

  factory NotificationItem.fromJson(Map<String, dynamic> json) {
    return NotificationItem(
      id: json['id'] as String,
      type: NotificationType.fromApi(json['type'] as String),
      title: json['title'] as String,
      content: json['content'] as String,
      time: DateTime.parse(json['time'] as String).toLocal(),
      read: json['read'] as bool? ?? false,
      extra: json['extra'] as Map<String, dynamic>?,
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'type': type.apiValue,
    'title': title,
    'content': content,
    'time': time.toIso8601String(),
    'read': read,
    'extra': extra,
  };
}
