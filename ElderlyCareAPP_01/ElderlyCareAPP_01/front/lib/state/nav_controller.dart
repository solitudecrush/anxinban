import 'package:flutter/foundation.dart';

class NavController extends ChangeNotifier {
  int _index = 0;

  int get index => _index;

  void setTab(int i) {
    if (i == _index || i < 0 || i > 4) {
      return;
    }
    _index = i;
    notifyListeners();
  }
}
