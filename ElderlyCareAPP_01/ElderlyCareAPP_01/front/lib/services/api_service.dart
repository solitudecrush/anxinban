import 'dart:async';
import 'dart:convert';
// import 'dart:io';

import 'package:http/http.dart' as http;

import '../config/api_config.dart';
import '../models/ai_analysis.dart';
import '../models/alert_item.dart';
import '../models/camera_request.dart';
import '../models/latest_vitals.dart';
import '../models/notification_item.dart';
import '../models/profile.dart';
import '../models/service_request.dart';
import '../models/vitals_history.dart';

class ApiException implements Exception {
  ApiException(this.message);

  final String message;

  @override
  String toString() => message;
}

/// 统一 API 响应封装
class ApiResponse<T> {
  final int code;
  final String message;
  final T? data;

  ApiResponse({required this.code, required this.message, this.data});

  factory ApiResponse.fromJson(Map<String, dynamic> json, T Function(dynamic)? parser) {
    return ApiResponse(
      code: json['code'] as int? ?? 500,
      message: json['message'] as String? ?? '未知错误',
      data: json.containsKey('data') && parser != null ? parser(json['data']) : null,
    );
  }

  bool get isSuccess => code == 200 || code == 201;
}

/// 后端真实 API 服务
class ApiService {
  ApiService() {
    _startPolling();
  }

  final _client = http.Client();
  String? _accessToken;

  // ---------- 基础请求封装 ----------

  Future<Map<String, dynamic>> _request(
    String method,
    String path, {
    Map<String, String>? queryParams,
    Object? body,
  }) async {
    final uri = Uri.parse(ApiConfig.baseUrl + path).replace(
      queryParameters: queryParams?.map((k, v) => MapEntry(k, v)),
    );

    final headers = <String, String>{
      'Content-Type': 'application/json; charset=UTF-8',
      if (_accessToken != null) 'Authorization': 'Bearer $_accessToken',
    };

    late final http.Response response;
    final encodedBody = body != null ? jsonEncode(body) : null;

    switch (method.toUpperCase()) {
      case 'GET':
        response = await _client.get(uri, headers: headers);
      case 'POST':
        response = await _client.post(uri, headers: headers, body: encodedBody);
      case 'PUT':
        response = await _client.put(uri, headers: headers, body: encodedBody);
      case 'DELETE':
        response = await _client.delete(uri, headers: headers, body: encodedBody);
      default:
        throw ApiException('不支持的请求方法: $method');
    }

    if (response.statusCode >= 500) {
      throw ApiException('服务器错误 (${response.statusCode})');
    }

    final jsonBody = jsonDecode(utf8.decode(response.bodyBytes)) as Map<String, dynamic>;
    final apiResp = ApiResponse<Map<String, dynamic>>.fromJson(jsonBody, (d) => d as Map<String, dynamic>);

    if (!apiResp.isSuccess) {
      throw ApiException(apiResp.message);
    }

    return jsonBody;
  }

  Future<Map<String, dynamic>> _get(String path, {Map<String, String>? queryParams}) async {
    final resp = await _request('GET', path, queryParams: queryParams);
    return resp;
  }

  Future<Map<String, dynamic>> _post(String path, {Object? body, Map<String, String>? queryParams}) async {
    final resp = await _request('POST', path, body: body, queryParams: queryParams);
    return resp;
  }

  Future<Map<String, dynamic>> _put(String path, {Object? body, Map<String, String>? queryParams}) async {
    final resp = await _request('PUT', path, body: body, queryParams: queryParams);
    return resp;
  }

  Future<Map<String, dynamic>> _delete(String path, {Map<String, String>? queryParams}) async {
    final resp = await _request('DELETE', path, queryParams: queryParams);
    return resp;
  }

  dynamic _extractData(Map<String, dynamic> resp) => resp['data'];

  // ---------- 认证模块 ----------

  Future<Map<String, dynamic>> login(String phone, String password, {String userType = 'family'}) async {
    final resp = await _post('/api/auth/login', body: {
      'phone': phone,
      'password': password,
      'userType': userType,
    });
    final data = _extractData(resp) as Map<String, dynamic>?;
    if (data != null && data['accessToken'] != null) {
      _accessToken = data['accessToken'] as String;
    }
    return data ?? {};
  }

  Future<Map<String, dynamic>> register(Map<String, dynamic> data) async {
    final resp = await _post('/api/auth/register', body: data);
    final result = _extractData(resp) as Map<String, dynamic>?;
    if (result != null && result['accessToken'] != null) {
      _accessToken = result['accessToken'] as String;
    }
    return result ?? {};
  }

  Future<void> resetPassword(String phone, String newPassword) async {
    await _post('/api/auth/reset-password', body: {
      'phone': phone,
      'newPassword': newPassword,
    });
  }

  Future<void> logout() async {
    try {
      await _post('/api/auth/logout');
    } catch (_) {}
    _accessToken = null;
  }

  Future<Map<String, dynamic>> fetchCurrentUser(String phone) async {
    final resp = await _get('/api/auth/me', queryParams: {'phone': phone});
    return (_extractData(resp) as Map<String, dynamic>?) ?? {};
  }

  void setToken(String token) {
    _accessToken = token;
  }

  // ---------- 老人信息 ----------

  Future<Map<String, dynamic>> fetchBoundElder(String familyId) async {
    final resp = await _get('/api/elder/bound', queryParams: {'familyId': familyId});
    return (_extractData(resp) as Map<String, dynamic>?) ?? {};
  }

  // ---------- Profile（兼容旧接口） ----------

  Future<Profile> fetchProfile() async {
    // App 端当前无独立 profile 接口，从绑定老人信息映射
    // 如需真实接口，可后续扩展 /api/auth/me
    return const Profile(
      name: '曾姐',
      age: 76,
      gender: '女',
      familyPhone: '13887654321',
      address: '6号楼 6-201',
    );
  }

  Future<Profile> patchProfileField(String field, String value) async {
    // 本地兼容，后续可对接真实接口
    return fetchProfile();
  }

  // ---------- 体征数据 ----------

  Future<LatestVitals> fetchLatestVitals({String elderId = '100'}) async {
    final resp = await _get('/api/health/latest/$elderId');
    final data = _extractData(resp) as Map<String, dynamic>?;
    if (data == null) {
      return LatestVitals(
        temperature: 36.5,
        heartRate: 72,
        systolic: 125,
        diastolic: 82,
        measuredAt: DateTime.now().subtract(const Duration(minutes: 5)),
      );
    }
    return LatestVitals(
      temperature: (data['temperature'] as num?)?.toDouble() ?? 36.5,
      heartRate: (data['heartRate'] as num?)?.toInt() ?? 72,
      systolic: (data['systolic'] as num?)?.toInt() ?? 125,
      diastolic: (data['diastolic'] as num?)?.toInt() ?? 82,
      measuredAt: _parseDateTime(data['updateTime']),
    );
  }

  Future<VitalsHistory> fetchHistory(String period, {String elderId = '100'}) async {
    final resp = await _get('/api/elder/$elderId/health/history', queryParams: {
      'type': 'heart_rate',
      'range': period,
    });
    final data = _extractData(resp) as Map<String, dynamic>?;
    final points = <VitalsHistoryPoint>[];
    if (data != null && data['points'] is List) {
      for (final p in data['points'] as List) {
        final m = p as Map<String, dynamic>;
        points.add(VitalsHistoryPoint(
          label: m['label'] as String? ?? '',
          temperature: (m['temperature'] as num?)?.toDouble() ?? 0,
          heartRate: (m['heartRate'] as num?)?.toInt() ?? 0,
          systolic: (m['systolic'] as num?)?.toInt() ?? 0,
          diastolic: (m['diastolic'] as num?)?.toInt() ?? 0,
        ));
      }
    }
    if (points.isEmpty) {
      return _mockHistory[period] ?? _mockHistory['week']!;
    }
    return VitalsHistory(period: period, points: points);
  }

  Future<VitalsHistory> fetchTrend({String elderId = '100', String? type, String period = 'week'}) async {
    final resp = await _get('/api/health/trend/$elderId', queryParams: {
      if (type != null) 'type': type,
      'period': period,
    });
    final data = _extractData(resp) as Map<String, dynamic>?;
    final points = <VitalsHistoryPoint>[];
    if (data != null && data['points'] is List) {
      for (final p in data['points'] as List) {
        final m = p as Map<String, dynamic>;
        points.add(VitalsHistoryPoint(
          label: m['label'] as String? ?? '',
          temperature: (m['temperature'] as num?)?.toDouble() ?? 0,
          heartRate: (m['heartRate'] as num?)?.toInt() ?? 0,
          systolic: (m['systolic'] as num?)?.toInt() ?? 0,
          diastolic: (m['diastolic'] as num?)?.toInt() ?? 0,
        ));
      }
    }
    if (points.isEmpty) {
      return _mockHistory[period] ?? _mockHistory['week']!;
    }
    return VitalsHistory(period: period, points: points);
  }

  Future<AiAnalysis> analyzeAi({String elderId = '100', String period = 'week'}) async {
    final resp = await _get('/api/health/analysis/$elderId', queryParams: {'period': period});
    final data = _extractData(resp) as Map<String, dynamic>?;
    if (data == null) {
      return _mockAi[period] ?? _mockAi['week']!;
    }
    return AiAnalysis(
      summary: data['summary'] as String? ?? '',
      suggestion: data['suggestion'] as String? ?? '',
      fromLlm: false,
    );
  }

  // ---------- 告警 ----------

  Future<List<AlertItem>> fetchAlerts({String elderId = '100'}) async {
    final resp = await _get('/api/alarm/list', queryParams: {
      if (elderId != null) 'elderId': elderId,
      'page': '1',
      'pageSize': '50',
    });
    final data = _extractData(resp);
    final list = (data is Map && data['list'] is List)
        ? data['list'] as List
        : (data is List ? data : <dynamic>[]);
    return list.map((a) => _convertAlarm(a as Map<String, dynamic>)).toList();
  }

  Future<AlertItem?> fetchLatestAlert({String elderId = '100'}) async {
    final list = await fetchAlerts(elderId: elderId);
    return list.isNotEmpty ? list.first : null;
  }

  Future<void> markAlarmRead(String alarmId) async {
    await _put('/api/alarm/$alarmId/read');
  }

  Future<int> fetchAlarmUnreadCount(String elderId) async {
    final resp = await _get('/api/alarm/unread-count', queryParams: {'elderId': elderId});
    final data = _extractData(resp) as Map<String, dynamic>?;
    return (data?['count'] as num?)?.toInt() ?? 0;
  }

  AlertItem _convertAlarm(Map<String, dynamic> m) {
    String typeRaw = (m['alarmType'] as String? ?? '').toUpperCase();
    AlertTypeCode type;
    if (typeRaw.contains('心率')) {
      type = AlertTypeCode.heartRateHigh;
    } else if (typeRaw.contains('血压')) {
      type = AlertTypeCode.pressureHigh;
    } else if (typeRaw.contains('体温') || typeRaw.contains('温度')) {
      type = AlertTypeCode.temperatureHigh;
    } else {
      type = AlertTypeCode.heartRateHigh;
    }
    return AlertItem(
      id: int.tryParse(m['alarmId']?.toString() ?? '') ?? 0,
      type: type,
      detail: '${m['alarmType'] ?? ''} · ${m['description'] ?? ''} · ${_formatTime(m['occurTime'])}',
      occurredAt: _parseDateTime(m['occurTime']),
    );
  }

  // ---------- 通知 ----------

  Future<List<NotificationItem>> fetchNotifications({
    String userId = '100',
    String userType = 'family',
    int page = 1,
    int pageSize = 20,
  }) async {
    final resp = await _get('/api/notification/list', queryParams: {
      'userId': userId,
      'userType': userType,
      'page': page.toString(),
      'pageSize': pageSize.toString(),
    });
    final data = _extractData(resp);
    final list = (data is Map && data['list'] is List)
        ? data['list'] as List
        : (data is List ? data : <dynamic>[]);
    return list.map((n) => _convertNotification(n as Map<String, dynamic>)).toList();
  }

  Future<int> fetchUnreadNotificationCount({String userId = '100', String userType = 'family'}) async {
    final resp = await _get('/api/notification/unread-count', queryParams: {
      'userId': userId,
      'userType': userType,
    });
    final data = _extractData(resp) as Map<String, dynamic>?;
    return (data?['count'] as num?)?.toInt() ?? 0;
  }

  Future<void> markNotificationRead(String notificationId) async {
    await _post('/api/notification/$notificationId/read');
  }

  NotificationItem _convertNotification(Map<String, dynamic> m) {
    return NotificationItem(
      id: int.tryParse(m['notificationId']?.toString() ?? '') ?? 0,
      type: _parseNotificationType(m['notificationType'] as String? ?? 'ALERT'),
      title: m['title'] as String? ?? '',
      content: m['content'] as String? ?? '',
      time: _parseDateTime(m['createTime']),
      read: m['isRead'] as bool? ?? false,
      extra: <String, dynamic>{
        if (m['elderId'] != null) 'elderId': m['elderId'],
        if (m['orderId'] != null) 'orderId': m['orderId'],
        if (m['requestId'] != null) 'requestId': m['requestId'],
      },
    );
  }

  NotificationType _parseNotificationType(String raw) {
    switch (raw.toUpperCase()) {
      case 'ORDER':
        return NotificationType.order;
      case 'CAMERA':
        return NotificationType.camera;
      case 'SERVICE':
        return NotificationType.service;
      default:
        return NotificationType.alert;
    }
  }

  // ---------- 监控申请 ----------

  Future<List<CameraRequest>> fetchCameraRequests({String familyId = '100'}) async {
    final resp = await _get('/api/monitor-request/list/family', queryParams: {'familyId': familyId});
    final data = _extractData(resp);
    final list = data is List ? data : <dynamic>[];
    return list.map((r) => CameraRequest.fromJson(r as Map<String, dynamic>)).toList();
  }

  Future<void> approveCameraRequest(String requestId) async {
    await _post('/api/monitor-request/$requestId/approve');
  }

  Future<void> rejectCameraRequest(String requestId) async {
    await _post('/api/monitor-request/$requestId/reject');
  }

  Future<void> revokeCameraAuth(String requestId) async {
    await _post('/api/monitor-request/$requestId/revoke');
  }

  // ---------- 服务申请 ----------

  Future<List<ServiceRequest>> fetchServiceRequests({String familyId = '100'}) async {
    final resp = await _get('/api/service-request/my-list', queryParams: {'familyId': familyId});
    final data = _extractData(resp);
    final list = data is List ? data : <dynamic>[];
    return list.map((r) => _convertServiceRequest(r as Map<String, dynamic>)).toList();
  }

  Future<void> submitServiceRequest(ServiceRequest request) async {
    await _post('/api/service-request', body: {
      'requestType': request.type,
      'content': request.content,
    });
  }

  ServiceRequest _convertServiceRequest(Map<String, dynamic> m) {
    return ServiceRequest(
      id: int.tryParse(m['requestId']?.toString() ?? '') ?? 0,
      type: m['requestType'] as String? ?? '上门看护',
      elderName: m['elderName'] as String? ?? '曾姐',
      content: m['content'] as String? ?? '',
      requestTime: _parseDateTime(m['createTime']),
      status: m['status'] as String? ?? 'pending',
      convertedTo: m['relatedOrderId'] as String?,
    );
  }

  // ---------- SOS ----------

  Future<Map<String, dynamic>> triggerSos(String elderId) async {
    final resp = await _post('/api/sos', body: {
      'sosId': 'SOS-${DateTime.now().millisecondsSinceEpoch}',
      'elderId': elderId,
    });
    return (_extractData(resp) as Map<String, dynamic>?) ?? {};
  }

  Future<List<Map<String, dynamic>>> fetchSosHistory(String elderId) async {
    final resp = await _get('/api/sos/list', queryParams: {'elderId': elderId});
    final data = _extractData(resp);
    final list = data is List ? data : <dynamic>[];
    return list.cast<Map<String, dynamic>>();
  }

  // ---------- 紧急联系人 ----------

  Future<List<Map<String, dynamic>>> fetchEmergencyContacts(String elderId) async {
    final resp = await _get('/api/emergency-contact/list', queryParams: {'elderId': elderId});
    final data = _extractData(resp);
    final list = data is List ? data : <dynamic>[];
    return list.cast<Map<String, dynamic>>();
  }

  Future<Map<String, dynamic>> createEmergencyContact(Map<String, dynamic> data) async {
    final resp = await _post('/api/emergency-contact', body: data);
    return (_extractData(resp) as Map<String, dynamic>?) ?? {};
  }

  Future<Map<String, dynamic>> updateEmergencyContact(String contactId, Map<String, dynamic> data) async {
    final resp = await _put('/api/emergency-contact/$contactId', body: data);
    return (_extractData(resp) as Map<String, dynamic>?) ?? {};
  }

  Future<void> deleteEmergencyContact(String contactId) async {
    await _delete('/api/emergency-contact/$contactId');
  }

  // ---------- 轮询与通知 ----------

  Timer? _pollTimer;
  final _controllers = <StreamController<String>>[];

  void _startPolling() {
    _pollTimer?.cancel();
    _pollTimer = Timer.periodic(const Duration(seconds: 5), (_) {
      for (final c in _controllers) {
        if (!c.isClosed) c.add('poll');
      }
    });
  }

  Stream<String> get syncStream {
    final controller = StreamController<String>.broadcast();
    _controllers.add(controller);
    controller.onCancel = () => _controllers.remove(controller);
    return controller.stream;
  }

  void dispose() {
    _pollTimer?.cancel();
    _client.close();
    for (final c in _controllers) {
      if (!c.isClosed) c.close();
    }
    _controllers.clear();
  }

  // ---------- 工具方法 ----------

  DateTime _parseDateTime(dynamic value) {
    if (value == null) return DateTime.now();
    if (value is DateTime) return value;
    if (value is String) {
      try {
        return DateTime.parse(value.replaceFirst(' ', 'T'));
      } catch (_) {
        return DateTime.now();
      }
    }
    return DateTime.now();
  }

  String _formatTime(dynamic value) {
    final dt = _parseDateTime(value);
    return '${dt.month.toString().padLeft(2, '0')}-${dt.day.toString().padLeft(2, '0')} ${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
  }

  // ---------- 兼容旧页面的 Mock 回退数据 ----------

  static final Map<String, VitalsHistory> _mockHistory = {
    'day': VitalsHistory(period: 'day', points: [
      VitalsHistoryPoint(label: '08:00', temperature: 36.4, heartRate: 72, systolic: 125, diastolic: 82),
      VitalsHistoryPoint(label: '10:00', temperature: 36.5, heartRate: 75, systolic: 128, diastolic: 83),
      VitalsHistoryPoint(label: '12:00', temperature: 36.6, heartRate: 78, systolic: 130, diastolic: 85),
      VitalsHistoryPoint(label: '14:00', temperature: 36.5, heartRate: 76, systolic: 129, diastolic: 84),
      VitalsHistoryPoint(label: '16:00', temperature: 36.5, heartRate: 78, systolic: 128, diastolic: 85),
      VitalsHistoryPoint(label: '18:00', temperature: 36.4, heartRate: 74, systolic: 126, diastolic: 83),
      VitalsHistoryPoint(label: '20:00', temperature: 36.4, heartRate: 73, systolic: 125, diastolic: 82),
    ]),
    'week': VitalsHistory(period: 'week', points: [
      VitalsHistoryPoint(label: '周一', temperature: 36.4, heartRate: 74, systolic: 126, diastolic: 82),
      VitalsHistoryPoint(label: '周二', temperature: 36.5, heartRate: 76, systolic: 128, diastolic: 84),
      VitalsHistoryPoint(label: '周三', temperature: 36.6, heartRate: 78, systolic: 130, diastolic: 85),
      VitalsHistoryPoint(label: '周四', temperature: 36.5, heartRate: 77, systolic: 129, diastolic: 84),
      VitalsHistoryPoint(label: '周五', temperature: 36.5, heartRate: 78, systolic: 128, diastolic: 85),
      VitalsHistoryPoint(label: '周六', temperature: 36.4, heartRate: 75, systolic: 127, diastolic: 83),
      VitalsHistoryPoint(label: '周日', temperature: 36.4, heartRate: 73, systolic: 125, diastolic: 82),
    ]),
    'month': VitalsHistory(period: 'month', points: [
      VitalsHistoryPoint(label: '第1周', temperature: 36.4, heartRate: 75, systolic: 127, diastolic: 83),
      VitalsHistoryPoint(label: '第2周', temperature: 36.5, heartRate: 76, systolic: 128, diastolic: 84),
      VitalsHistoryPoint(label: '第3周', temperature: 36.6, heartRate: 78, systolic: 130, diastolic: 85),
      VitalsHistoryPoint(label: '第4周', temperature: 36.5, heartRate: 77, systolic: 129, diastolic: 84),
    ]),
  };

  static final Map<String, AiAnalysis> _mockAi = {
    'day': AiAnalysis(
      summary: '今日老人各项体征指标总体平稳，心率、血压、体温均在正常范围内波动。',
      suggestion: '建议保持当前作息，适当增加午休时间。',
      fromLlm: false,
    ),
    'week': AiAnalysis(
      summary: '本周老人健康状况良好，平均心率 75bpm，平均血压 127/83 mmHg。',
      suggestion: '建议继续监测，保持适度活动。',
      fromLlm: false,
    ),
    'month': AiAnalysis(
      summary: '本月老人健康趋势整体平稳，第三周血压略有升高，随后恢复。',
      suggestion: '建议每月进行一次全面体检，关注血压变化趋势。',
      fromLlm: false,
    ),
  };
}
