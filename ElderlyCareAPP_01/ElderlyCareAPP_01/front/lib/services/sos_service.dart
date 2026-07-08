import 'package:direct_caller_sim_choice/direct_caller_sim_choice.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:permission_handler/permission_handler.dart';
import 'sms_sender_service.dart';
import 'package:flutter/services.dart';

import '../screens/emergency_contact_settings_screen.dart';
import 'emergency_contact_store.dart';
import 'sos_sim_store.dart';

/// 首页 SOS：获取位置 → 后台发送短信 → 直接拨打电话。
/// 所有操作一键完成，不跳转系统界面，不弹二次确认。
/// 使用紧急联系人列表中的第一位。
class SosService {
  SosService._();

  static String _digitsPhone(String raw) {
    return raw.replaceAll(RegExp(r'\D'), '');
  }

  static String _buildSmsBody({
    required double latitude,
    required double longitude,
  }) {
    final mapLink = 'https://uri.amap.com/marker?position=$longitude,$latitude';
    return '【健康助手 SOS】我的当前位置：纬度 $latitude，经度 $longitude\n'
        '高德地图打开链接：$mapLink';
  }

  static Future<bool> _requestCallPermission(BuildContext context) async {
    final status = await Permission.phone.status;
    if (status.isGranted) {
      return true;
    }

    if (status.isPermanentlyDenied) {
      if (!context.mounted) return false;
      await showDialog<void>(
        context: context,
        builder: (c) => AlertDialog(
          title: const Row(
            children: [
              Icon(Icons.phone_disabled, color: Colors.red),
              SizedBox(width: 8),
              Text('需要电话权限'),
            ],
          ),
          content: const Text(
            'SOS 紧急呼叫需要电话权限。\n\n'
            '请在系统设置中为「健康助手」开启电话权限后再试。',
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(c),
              child: const Text('取消'),
            ),
            FilledButton(
              onPressed: () async {
                Navigator.pop(c);
                await openAppSettings();
              },
              child: const Text('去设置'),
            ),
          ],
        ),
      );
      return false;
    }

    // 首次申请或临时拒绝
    final result = await Permission.phone.request();
    if (result.isGranted) {
      return true;
    }

    // 申请后仍被拒绝
    if (!context.mounted) return false;
    await showDialog<void>(
      context: context,
      builder: (c) => AlertDialog(
        title: const Row(
          children: [
            Icon(Icons.phone_disabled, color: Colors.red),
            SizedBox(width: 8),
            Text('需要电话权限'),
          ],
        ),
        content: const Text(
          'SOS 紧急呼叫需要电话权限。\n\n'
          '若已勾选「不再询问」，请前往系统设置手动开启。',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(c),
            child: const Text('取消'),
          ),
          FilledButton(
            onPressed: () async {
              Navigator.pop(c);
              await openAppSettings();
            },
            child: const Text('去设置'),
          ),
        ],
      ),
    );
    return false;
  }

  static Future<bool> _requestSmsPermission() async {
    final status = await Permission.sms.status;
    if (status.isGranted) {
      return true;
    }
    final result = await Permission.sms.request();
    return result.isGranted;
  }

  static Future<void> triggerSos(BuildContext context) async {
    // 震动反馈
    await HapticFeedback.heavyImpact();

    if (!context.mounted) return;

    final first = await EmergencyContactStore.firstForSos();
    final rawPhone = first?.phone;
    final phone = rawPhone != null ? _digitsPhone(rawPhone) : '';

    if (phone.isEmpty) {
      if (!context.mounted) return;
      await showDialog<void>(
        context: context,
        builder: (c) => AlertDialog(
          title: const Text('尚未添加紧急联系人'),
          content: const Text(
            '请先在「紧急联系人」中至少添加一位联系人并保存手机号。',
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(c),
              child: const Text('取消'),
            ),
            FilledButton(
              onPressed: () {
                Navigator.pop(c);
                Navigator.of(context).push(
                  MaterialPageRoute<void>(
                    builder: (_) => const EmergencyContactSettingsScreen(),
                  ),
                );
              },
              child: const Text('去添加'),
            ),
          ],
        ),
      );
      return;
    }

    // 申请电话权限
    final hasCallPermission = await _requestCallPermission(context);
    if (!hasCallPermission || !context.mounted) return;

    // 申请短信权限（不阻塞，失败后续会提示）
    final hasSmsPermission = await _requestSmsPermission();

    // 获取用户选择的 SIM 卡槽（1-based）
    final simSlot = await SosSimStore.getDefaultSimSlot();

    // 开始获取位置（后台执行）
    Position? position;
    String? locError;

    final serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      locError = '定位服务未开启';
    } else {
      var locPermission = await Geolocator.checkPermission();
      if (locPermission == LocationPermission.denied) {
        locPermission = await Geolocator.requestPermission();
      }
      if (locPermission == LocationPermission.denied ||
          locPermission == LocationPermission.deniedForever) {
        locError = '未获得定位权限';
      } else {
        try {
          position = await Geolocator.getCurrentPosition(
            locationSettings: const LocationSettings(
              accuracy: LocationAccuracy.high,
            ),
          ).timeout(const Duration(seconds: 20));
        } catch (_) {
          locError = '获取位置超时或失败';
        }
      }
    }

    // 发送短信（后台直接发送，不跳转界面）
    bool smsSent = false;
    if (hasSmsPermission && position != null) {
      try {
        final body = _buildSmsBody(
          latitude: position.latitude,
          longitude: position.longitude,
        );
        smsSent = await SmsSenderService.sendSms(
          phoneNumber: phone,
          message: body,
          simSlot: simSlot - 1, // 转换为 0-based
        );
      } catch (_) {
        smsSent = false;
      }
    }

    // 直接拨打电话（不跳转拨号盘）
    bool callSuccess = false;
    try {
      callSuccess = DirectCaller().makePhoneCall(phone, simSlot: simSlot);
    } catch (_) {
      callSuccess = false;
    }

    if (!context.mounted) return;

    // 根据结果给用户反馈
    final List<String> messages = [];
    if (callSuccess) {
      messages.add('✓ 已拨打电话');
    } else {
      messages.add('✗ 电话拨打失败');
    }

    if (hasSmsPermission) {
      if (position != null) {
        if (smsSent) {
          messages.add('✓ 位置短信已发送');
        } else {
          messages.add('✗ 位置短信发送失败');
        }
      } else {
        messages.add('⚠ ${locError ?? '无法获取位置'}，短信未发送');
      }
    } else {
      messages.add('⚠ 未获得短信权限，位置短信未发送');
    }

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(messages.join('\n')),
        duration: const Duration(seconds: 4),
        backgroundColor: callSuccess ? Colors.green.shade700 : Colors.red.shade700,
      ),
    );

    // 短信发送失败时弹窗提示
    if (hasSmsPermission && position != null && !smsSent) {
      if (!context.mounted) return;
      await showDialog<void>(
        context: context,
        builder: (c) => AlertDialog(
          title: const Row(
            children: [
              Icon(Icons.sms_failed, color: Colors.orange),
              SizedBox(width: 8),
              Text('短信发送失败'),
            ],
          ),
          content: const Text(
            '位置短信未能自动发送，请检查短信权限或 SIM 卡状态。\n\n'
            '电话已尝试拨打，请尽快与紧急联系人通话确认安全。',
          ),
          actions: [
            FilledButton(
              onPressed: () => Navigator.pop(c),
              child: const Text('我知道了'),
            ),
          ],
        ),
      );
    }
  }
}
