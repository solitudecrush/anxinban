import 'package:flutter/foundation.dart';

class NavController extends ChangeNotifier {
  int _index = 0;

  /// 消息中心初始筛选参数，由 HomeScreen "查看更多" 传入。
  /// 可选值：'all'、'ALERT'、'ORDER'、'CAMERA'。
  /// 为 null 时 AlertsScreen 保持当前筛选状态。
  String? _messageFilter;

  int get index => _index;

  /// 消息中心初始筛选值（一次性消费后自动清零）。
  String? get messageFilter => _messageFilter;

  /// 消费消息中心筛选值（读取后清除）。
  String? consumeMessageFilter() {
    final f = _messageFilter;
    _messageFilter = null;
    return f;
  }

  void setTab(int i, {String? messageFilter}) {
    if (i < 0 || i > 4) {
      return;
    }
    _messageFilter = messageFilter;
    if (i == _index) {
      // 同一个 tab，通知一下让 AlertsScreen 读取 filter
      notifyListeners();
      return;
    }
    _index = i;
    notifyListeners();
  }
}
