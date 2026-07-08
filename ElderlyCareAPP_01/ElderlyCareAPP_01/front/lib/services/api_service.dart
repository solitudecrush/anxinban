import 'dart:async';
import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';
import 'package:mime/mime.dart';

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
  String? _userId; // 当前登录用户 ID（family 用户即为 familyId）

  /// 当前登录令牌（供外部读取，如 main.dart 恢复登录状态）
  String? get accessToken => _accessToken;

  /// 当前登录用户 ID
  String? get userId => _userId;

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

  /// App 端家属登录（使用新端点 /api/auth/login/app）
  Future<Map<String, dynamic>> login(String phone, String password, {String userType = 'family'}) async {
    final resp = await _post('/api/auth/login/app', body: {
      'phone': phone,
      'password': password,
    });
    final data = _extractData(resp) as Map<String, dynamic>?;
    if (data != null && data['accessToken'] != null) {
      _accessToken = data['accessToken'] as String;
      _userId = data['userId'] as String?;
    }
    return data ?? {};
  }

  Future<Map<String, dynamic>> register(Map<String, dynamic> data) async {
    final resp = await _post('/api/auth/register', body: {
      'phone': data['phone'],
      'password': data['password'],
      'name': data['name'] ?? data['phone'],
      'verifyCode': data['verifyCode'],
      'userType': data['userType'] ?? 'family',
      if (data['elderId'] != null) 'elderId': data['elderId'],
      if (data['relation'] != null) 'relation': data['relation'],
    });
    final result = _extractData(resp) as Map<String, dynamic>?;
    if (result != null && result['accessToken'] != null) {
      _accessToken = result['accessToken'] as String;
      _userId = result['userId'] as String?;
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
    _userId = null;
  }

  /// 获取当前用户信息。
  ///
  /// 注意：后端 /api/auth/me 仅查询 staff 表，family 用户会返回 404。
  /// family 用户应使用登录时返回的 LoginResponse 中的信息。
  Future<Map<String, dynamic>> fetchCurrentUser(String phone) async {
    try {
      final resp = await _get('/api/auth/me', queryParams: {'phone': phone});
      return (_extractData(resp) as Map<String, dynamic>?) ?? {};
    } on ApiException {
      // family 用户查不到 staff 表，返回空（使用方应从登录缓存中获取）
      return {};
    }
  }

  /// 从 SharedPreferences 恢复登录令牌（App 启动时调用）
  void setToken(String token) {
    _accessToken = token;
  }

  /// 从 SharedPreferences 恢复用户 ID（App 启动时调用）
  void setUserId(String id) {
    _userId = id;
  }

  // ---------- 头像上传 ----------

  /// 上传用户头像。
  ///
  /// [filePath] 本地图片文件路径（来自 image_picker）。
  /// [userId] 当前登录用户的 ID。
  /// [role] 用户角色，默认 "family"。
  ///
  /// 返回后端存储的相对路径（如 /uploads/avatars/avatar-xxx.jpg），
  /// 可通过 [avatarFullUrl] 获取完整访问地址。
  Future<String> uploadAvatar(String filePath, String userId, {String role = 'family'}) async {
    final uri = Uri.parse(ApiConfig.baseUrl + '/api/upload/avatar');

    final mimeType = lookupMimeType(filePath) ?? 'image/jpeg';
    final mediaType = MediaType.parse(mimeType);

    final request = http.MultipartRequest('POST', uri)
      ..headers.addAll({
        if (_accessToken != null) 'Authorization': 'Bearer $_accessToken',
      })
      ..fields['userId'] = userId
      ..fields['role'] = role
      ..files.add(await http.MultipartFile.fromPath('file', filePath, contentType: mediaType));

    final streamedResponse = await request.send();
    final response = await http.Response.fromStream(streamedResponse);

    if (response.statusCode >= 500) {
      throw ApiException('服务器错误 (${response.statusCode})');
    }

    final jsonBody = jsonDecode(utf8.decode(response.bodyBytes)) as Map<String, dynamic>;
    final apiResp = ApiResponse<Map<String, dynamic>>.fromJson(jsonBody, (d) => d as Map<String, dynamic>);

    if (!apiResp.isSuccess) {
      throw ApiException(apiResp.message);
    }

    final data = apiResp.data;
    return data?['url'] as String? ?? '';
  }

  /// 删除用户头像（恢复默认头像）。
  Future<String> deleteAvatar(String userId, {String role = 'family'}) async {
    final resp = await _delete('/api/upload/avatar', queryParams: {
      'userId': userId,
      'role': role,
    });
    final data = _extractData(resp) as Map<String, dynamic>?;
    return data?['url'] as String? ?? '/uploads/avatars/default.png';
  }

  /// 根据相对路径构建完整的头像访问 URL。
  ///
  /// 如果 [relativePath] 为空或 null，返回默认头像 URL。
  static String avatarFullUrl(String? relativePath) {
    if (relativePath == null || relativePath.isEmpty) {
      return '${ApiConfig.baseUrl}/uploads/avatars/default.png';
    }
    if (relativePath.startsWith('http')) {
      return relativePath;
    }
    return '${ApiConfig.baseUrl}$relativePath';
  }

  // ---------- 老人信息 ----------

  Future<Map<String, dynamic>> fetchBoundElder(String familyId) async {
    final resp = await _get('/api/elder/bound', queryParams: {'familyId': familyId});
    return (_extractData(resp) as Map<String, dynamic>?) ?? {};
  }

  // ---------- Profile（兼容旧接口） ----------

  Future<Profile> fetchProfile() async {
    // App 端当前无独立 profile 接口，从绑定老人信息映射
    return const Profile(
      name: '曾姐',
      age: 76,
      gender: '女',
      familyPhone: '13887654321',
      address: '6号楼 6-201',
    );
  }

  Future<Profile> patchProfileField(String field, String value) async {
    return fetchProfile();
  }

  // ---------- 体征数据 ----------

  /// 获取老人最新体征数据。
  /// 使用 /api/elder/{elderId}/health/realtime，返回 HealthLatestDto (camelCase)。
  Future<LatestVitals> fetchLatestVitals({String elderId = '100'}) async {
    final resp = await _get('/api/elder/$elderId/health/realtime');
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

  /// 获取体征历史数据。
  ///
  /// 使用 /api/health-vital/list 获取综合体征记录（每条记录包含全部体征），
  /// 然后按 period 计算日期范围过滤，按 measuredAt 分组。
  Future<VitalsHistory> fetchHistory(String period, {String elderId = '100'}) async {
    try {
      final now = DateTime.now();
      String start;
      switch (period) {
        case 'day':
          start = '${now.year}-${_pad(now.month)}-${_pad(now.day)} 00:00:00';
          break;
        case 'month':
          final monthAgo = now.subtract(const Duration(days: 30));
          start = '${monthAgo.year}-${_pad(monthAgo.month)}-${_pad(monthAgo.day)} 00:00:00';
          break;
        default: // week
          final weekAgo = now.subtract(const Duration(days: 7));
          start = '${weekAgo.year}-${_pad(weekAgo.month)}-${_pad(weekAgo.day)} 00:00:00';
      }
      final end = '${now.year}-${_pad(now.month)}-${_pad(now.day)} 23:59:59';

      final resp = await _get('/api/health-vital/list', queryParams: {
        'elderId': elderId,
        'start': start,
        'end': end,
      });
      final data = _extractData(resp);
      if (data is List && data.isNotEmpty) {
        final points = <VitalsHistoryPoint>[];
        for (final r in data) {
          final m = r as Map<String, dynamic>;
          points.add(VitalsHistoryPoint(
            label: _formatTimeShort(m['measuredAt'] ?? m['createdAt']),
            temperature: (m['temperature'] as num?)?.toDouble(),
            heartRate: (m['heartRate'] as num?)?.toInt(),
            systolic: (m['systolic'] as num?)?.toInt(),
            diastolic: (m['diastolic'] as num?)?.toInt(),
          ));
        }
        if (points.isNotEmpty) {
          return VitalsHistory(period: period, points: points);
        }
      }
    } catch (_) {
      // 后端无数据或接口不可用时回退 mock
    }
    return _mockHistory[period] ?? _mockHistory['week']!;
  }

  /// 获取单项体征趋势。
  ///
  /// 使用 /api/elder/{elderId}/health/history，返回 HealthTrendDto。
  /// HealthTrendDto.data 是 List<HealthTrendItemDto>，每项含 time/value/systolic/diastolic。
  Future<VitalsHistory> fetchTrend({String elderId = '100', String? type, String period = 'week'}) async {
    final vitalType = type ?? 'heart_rate';
    final resp = await _get('/api/elder/$elderId/health/history', queryParams: {
      'type': vitalType,
      'range': period,
    });
    final data = _extractData(resp) as Map<String, dynamic>?;
    final points = <VitalsHistoryPoint>[];
    if (data != null && data['data'] is List) {
      for (final p in data['data'] as List) {
        final m = p as Map<String, dynamic>;
        final isBP = vitalType == 'blood_pressure';
        points.add(VitalsHistoryPoint(
          label: m['time'] as String? ?? '',
          temperature: isBP ? null : (vitalType == 'temperature' ? (m['value'] as num?)?.toDouble() : null),
          heartRate: isBP ? null : (vitalType == 'heart_rate' ? (m['value'] as num?)?.toInt() : null),
          systolic: (m['systolic'] as num?)?.toInt(),
          diastolic: (m['diastolic'] as num?)?.toInt(),
        ));
      }
    }
    if (points.isEmpty) {
      return _mockHistory[period] ?? _mockHistory['week']!;
    }
    return VitalsHistory(period: period, points: points);
  }

  /// AI 健康分析。
  ///
  /// 注意：后端 HealthAnalysisDto 已定义但未通过 REST 端点暴露。
  /// 当前始终返回 mock 分析数据。
  Future<AiAnalysis> analyzeAi({String elderId = '100', String period = 'week'}) async {
    // 后端 /api/health/analysis 端点不存在，使用本地 mock
    return _mockAi[period] ?? _mockAi['week']!;
  }

  // ---------- 告警 ----------

  /// 获取告警列表。
  /// 后端返回 PageResult<AlarmDto>（data.list）。
  Future<List<AlertItem>> fetchAlerts({String elderId = '100'}) async {
    final resp = await _get('/api/alarm/list', queryParams: {
      'elderId': elderId,
      'page': '1',
      'pageSize': '50',
    });
    final data = _extractData(resp);
    // data 可能是 PageResult（含 list 字段）或直接是 List
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

  /// 将 AlarmDto (camelCase) 转为 AlertItem
  AlertItem _convertAlarm(Map<String, dynamic> m) {
    final typeRaw = (m['alarmType'] as String? ?? '').toUpperCase();
    AlertTypeCode type;
    if (typeRaw.contains('心率') || typeRaw.contains('HEART')) {
      type = AlertTypeCode.heartRateHigh;
    } else if (typeRaw.contains('血压') || typeRaw.contains('BLOOD_PRESSURE') || typeRaw.contains('PRESSURE')) {
      type = AlertTypeCode.pressureHigh;
    } else if (typeRaw.contains('体温') || typeRaw.contains('温度') || typeRaw.contains('TEMPERATURE')) {
      type = AlertTypeCode.temperatureHigh;
    } else if (typeRaw.contains('FALL') || typeRaw.contains('跌倒')) {
      type = AlertTypeCode.heartRateHigh; // 暂无跌倒枚举，归入心率告警
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

  /// 获取通知列表。
  /// 后端返回 PageResult<NotificationDto>（data.list）。
  Future<List<NotificationItem>> fetchNotifications({
    String? userId,
    String userType = 'family',
    int page = 1,
    int pageSize = 20,
  }) async {
    final uid = userId ?? _userId ?? '100';
    final resp = await _get('/api/notification/list', queryParams: {
      'userId': uid,
      'userType': userType,
      'page': page.toString(),
      'pageSize': pageSize.toString(),
    });
    final data = _extractData(resp);
    // data 可能是 PageResult（含 list 字段）或直接是 List
    final list = (data is Map && data['list'] is List)
        ? data['list'] as List
        : (data is List ? data : <dynamic>[]);
    return list.map((n) => _convertNotification(n as Map<String, dynamic>)).toList();
  }

  Future<int> fetchUnreadNotificationCount({String? userId, String userType = 'family'}) async {
    final uid = userId ?? _userId ?? '100';
    final resp = await _get('/api/notification/unread-count', queryParams: {
      'userId': uid,
      'userType': userType,
    });
    final data = _extractData(resp) as Map<String, dynamic>?;
    return (data?['count'] as num?)?.toInt() ?? 0;
  }

  Future<void> markNotificationRead(String notificationId) async {
    await _post('/api/notification/$notificationId/read');
  }

  /// 将 NotificationDto (camelCase) 转为 NotificationItem。
  /// 后端 NotificationDto.type 对应通知类型。
  NotificationItem _convertNotification(Map<String, dynamic> m) {
    return NotificationItem(
      id: int.tryParse(m['notificationId']?.toString() ?? '') ?? 0,
      type: _parseNotificationType(m['type'] as String? ?? 'ALERT'),
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

  /// 获取家属的监控申请列表。
  /// 后端返回 List<MonitorRequestDto>。
  Future<List<CameraRequest>> fetchCameraRequests({String? familyId}) async {
    final fid = familyId ?? _userId ?? '100';
    final resp = await _get('/api/monitor-request/list/family', queryParams: {'familyId': fid});
    final data = _extractData(resp);
    final list = data is List ? data : <dynamic>[];
    return list.map((r) => _convertCameraRequest(r as Map<String, dynamic>)).toList();
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

  /// 将 MonitorRequestDto 转为 CameraRequest
  CameraRequest _convertCameraRequest(Map<String, dynamic> m) {
    return CameraRequest(
      id: int.tryParse(m['requestId']?.toString() ?? '') ?? 0,
      elderName: m['elderName'] as String? ?? '',
      staffName: m['staffName'] as String? ?? '',
      staffPhone: m['staffPhone'] as String? ?? '',
      reason: m['reason'] as String? ?? '',
      requestTime: _parseDateTime(m['createTime']),
      status: m['status'] as String? ?? 'pending',
      expiresAt: _parseDateTimeNullable(m['expiredAt']),
      approvedAt: _parseDateTimeNullable(m['approvedAt']),
    );
  }

  // ---------- 服务申请 ----------

  /// 获取家属的服务申请列表。
  /// 后端返回 List<ServiceRequestDto>。
  Future<List<ServiceRequest>> fetchServiceRequests({String? familyId}) async {
    final fid = familyId ?? _userId ?? '100';
    final resp = await _get('/api/service-request/my-list', queryParams: {'familyId': fid});
    final data = _extractData(resp);
    final list = data is List ? data : <dynamic>[];
    return list.map((r) => _convertServiceRequest(r as Map<String, dynamic>)).toList();
  }

  /// 提交服务申请。
  /// 后端 ServiceRequestDto 字段名为 type（非 requestType）。
  Future<void> submitServiceRequest(ServiceRequest request) async {
    await _post('/api/service-request', body: {
      'type': request.type,
      'content': request.content,
      if (_userId != null) 'familyId': _userId,
      if (request.elderName.isNotEmpty) 'elderName': request.elderName,
    });
  }

  /// 将 ServiceRequestDto 转为 ServiceRequest。
  /// 后端 DTO 字段：requestId, type, content, status, createTime, convertedWorkOrderId, elderName 等。
  ServiceRequest _convertServiceRequest(Map<String, dynamic> m) {
    return ServiceRequest(
      id: int.tryParse(m['requestId']?.toString() ?? '') ?? 0,
      type: m['type'] as String? ?? '上门看护',
      elderName: m['elderName'] as String? ?? '曾姐',
      content: m['content'] as String? ?? '',
      requestTime: _parseDateTime(m['createTime']),
      status: m['status'] as String? ?? 'pending',
      convertedTo: m['convertedWorkOrderId'] as String?,
    );
  }

  // ---------- SOS ----------

  /// 触发 SOS 求救。
  /// 后端 POST /api/sos，body 为 SosDto。
  Future<Map<String, dynamic>> triggerSos(String elderId) async {
    final resp = await _post('/api/sos', body: {
      'sosId': 'SOS-${DateTime.now().millisecondsSinceEpoch}',
      'elderId': elderId,
    });
    return (_extractData(resp) as Map<String, dynamic>?) ?? {};
  }

  /// 获取 SOS 历史记录。
  /// 后端返回 List<SosDto>（无分页）。
  Future<List<Map<String, dynamic>>> fetchSosHistory(String elderId) async {
    final resp = await _get('/api/sos/list', queryParams: {'elderId': elderId});
    final data = _extractData(resp);
    final list = data is List ? data : <dynamic>[];
    return list.cast<Map<String, dynamic>>();
  }

  // ---------- 紧急联系人 ----------

  /// 获取紧急联系人列表。
  /// 后端返回 List<EmergencyContactDto>（无分页）。
  Future<List<Map<String, dynamic>>> fetchEmergencyContacts(String elderId) async {
    final resp = await _get('/api/emergency-contact/list', queryParams: {'elderId': elderId});
    final data = _extractData(resp);
    final list = data is List ? data : <dynamic>[];
    return list.cast<Map<String, dynamic>>();
  }

  /// 创建紧急联系人。
  /// 后端 POST /api/emergency-contact，body 为 EmergencyContactDto。
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

  DateTime? _parseDateTimeNullable(dynamic value) {
    if (value == null) return null;
    if (value is DateTime) return value;
    if (value is String && value.isNotEmpty) {
      try {
        return DateTime.parse(value.replaceFirst(' ', 'T'));
      } catch (_) {
        return null;
      }
    }
    return null;
  }

  String _formatTime(dynamic value) {
    final dt = _parseDateTime(value);
    return '${dt.month.toString().padLeft(2, '0')}-${dt.day.toString().padLeft(2, '0')} ${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
  }

  String _formatTimeShort(dynamic value) {
    final dt = _parseDateTime(value);
    return '${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
  }

  String _pad(int n) => n.toString().padLeft(2, '0');

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
