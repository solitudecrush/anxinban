import 'package:flutter/services.dart';

/// 原生 MethodChannel 封装：后台直接发送短信，支持双 SIM 卡。
/// Android 侧实现位于 MainActivity.kt。
class SmsSenderService {
  SmsSenderService._();

  static const MethodChannel _channel = MethodChannel(
    'com.elderlycare.elderly_care_app/amap',
  );

  /// 直接发送短信（不跳转系统短信界面）。
  ///
  /// [phoneNumber] 接收方手机号
  /// [message] 短信内容
  /// [simSlot] SIM 卡槽索引（0 = 卡槽1，1 = 卡槽2）
  static Future<bool> sendSms({
    required String phoneNumber,
    required String message,
    int simSlot = 0,
  }) async {
    try {
      final result = await _channel.invokeMethod<bool>('sendSms', {
        'phoneNumber': phoneNumber,
        'message': message,
        'simSlot': simSlot,
      });
      return result ?? false;
    } on PlatformException {
      return false;
    }
  }
}
