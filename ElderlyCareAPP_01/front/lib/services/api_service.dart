import 'dart:async';
import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';
import 'package:mime/mime.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../config/api_config.dart';
import '../models/ai_analysis.dart';
import '../models/alert_item.dart';
import '../models/camera_request.dart';
import '../models/latest_vitals.dart';
import '../models/notification_item.dart';
import '../models/profile.dart';
import '../models/service_request.dart';
import '../models/vitals_history.dart';
import 'analysis_engine.dart';
import 'llm_service.dart';

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
  String? _elderId; // 当前绑定老人 ID（由 fetchAndSetElderId 自动获取）

  /// 当前登录令牌（供外部读取，如 main.dart 恢复登录状态）
  String? get accessToken => _accessToken;

  /// 当前登录用户 ID
  String? get userId => _userId;

  /// 当前绑定老人 ID（从 /api/elder/bound 获取）
  String? get elderId => _elderId;

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

    final decoded = jsonDecode(utf8.decode(response.bodyBytes));
    final jsonBody = decoded is Map<String, dynamic> ? decoded : <String, dynamic>{};

    final code = jsonBody['code'] as int? ?? 500;
    if (code != 200 && code != 201) {
      final message = jsonBody['message'] as String? ?? '未知错误';
      throw ApiException(message);
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

  /// 从 SharedPreferences 恢复老人 ID（App 启动时调用）
  void setElderId(String id) {
    _elderId = id;
  }

  /// 根据当前登录的 familyId 查询绑定的老人 ID，并存储到 _elderId。
  ///
  /// 返回获取到的 elderId，如果未绑定则返回 null。
  /// 调用时机：登录成功后、App 启动恢复登录状态后。
  Future<String?> fetchAndSetElderId() async {
    if (_userId == null || _userId!.isEmpty) return null;
    try {
      final elder = await fetchBoundElder(_userId!);
      final eid = elder['elderId'] as String?;
      if (eid != null && eid.isNotEmpty) {
        _elderId = eid;
        return eid;
      }
    } catch (_) {
      // 用户可能还未绑定老人
    }
    return null;
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

  /// 获取当前绑定老人的 Profile 信息。
  ///
  /// 通过 /api/elder/bound 获取老人真实信息，映射为 Profile 模型。
  /// 若未绑定老人或请求失败，返回默认占位信息。
  Future<Profile> fetchProfile() async {
    try {
      final elder = await fetchBoundElder(_userId ?? '');
      if (elder.isNotEmpty) {
        final building = elder['building'] as String? ?? '';
        final room = elder['room'] as String? ?? '';
        final address = elder['address'] as String? ?? '';
        final fullAddress = address.isNotEmpty
            ? address
            : (building.isNotEmpty && room.isNotEmpty
                ? '$building $room'
                : (building.isNotEmpty ? building : (room.isNotEmpty ? room : '')));
        return Profile(
          name: elder['name'] as String? ?? '未知',
          age: (elder['age'] as num?)?.toInt() ?? 0,
          gender: elder['gender'] as String? ?? '未知',
          familyPhone: elder['familyPhone'] as String? ?? elder['guardianPhone'] as String? ?? '',
          address: fullAddress.isNotEmpty ? fullAddress : '暂无地址',
          avatar: elder['avatar'] as String?,
        );
      }
    } catch (_) {
      // fall through to default
    }
    return const Profile(
      name: '未绑定老人',
      age: 0,
      gender: '未知',
      familyPhone: '',
      address: '',
    );
  }

  Future<Profile> patchProfileField(String field, String value) async {
    // 调用后端 PUT /api/elder/{elderId} 更新老人信息
    final eid = _elderId;
    if (eid == null || eid.isEmpty) {
      throw ApiException('未绑定老人信息，请重新登录后再试');
    }
    final body = <String, dynamic>{field: value};
    // 特殊处理 age 字段（需要 int 类型）
    if (field == 'age') {
      body['age'] = int.tryParse(value) ?? 0;
    }
    await _put('/api/elder/$eid', body: body);
    return fetchProfile();
  }

  /// 批量更新老人信息字段
  Future<void> patchProfileFields(Map<String, dynamic> fields) async {
    final eid = _elderId;
    if (eid == null || eid.isEmpty) {
      throw ApiException('未绑定老人信息，无法保存');
    }
    await _put('/api/elder/$eid', body: fields);
  }

  // ---------- 体征数据 ----------

  /// 获取老人最新体征数据。
  /// 使用 /api/elder/{elderId}/health/realtime，返回 HealthLatestDto (camelCase)。
  Future<LatestVitals> fetchLatestVitals({String? elderId}) async {
    final eid = elderId ?? _elderId;
    if (eid == null || eid.isEmpty) {
      return LatestVitals(
        temperature: 0,
        heartRate: 0,
        systolic: 0,
        diastolic: 0,
        bloodOxygen: null,
        measuredAt: DateTime.now(),
      );
    }
    final resp = await _get('/api/elder/$eid/health/realtime');
    final data = _extractData(resp) as Map<String, dynamic>?;
    if (data == null) {
      return LatestVitals(
        temperature: 0,
        heartRate: 0,
        systolic: 0,
        diastolic: 0,
        bloodOxygen: null,
        measuredAt: DateTime.now(),
      );
    }
    return LatestVitals(
      temperature: (data['temperature'] as num?)?.toDouble() ?? 0,
      heartRate: (data['heartRate'] as num?)?.toInt() ?? 0,
      systolic: (data['systolic'] as num?)?.toInt() ?? 0,
      diastolic: (data['diastolic'] as num?)?.toInt() ?? 0,
      bloodOxygen: (data['bloodOxygen'] as num?)?.toInt(),
      measuredAt: _parseDateTime(data['updateTime']),
    );
  }

  /// 获取体征历史数据（综合）。
  ///
  /// 使用 /api/elder/{elderId}/health/history 分别获取三种体征趋势后合并，
  /// 确保与后端独立体征表（body_temperature, heart_rate, blood_pressure）对齐。
  Future<VitalsHistory> fetchHistory(String period, {String? elderId}) async {
    final eid = elderId ?? _elderId;
    if (eid == null || eid.isEmpty) {
      return _generateMockHistory(period);
    }
    try {
      // 并行获取四种体征趋势
      final results = await Future.wait([
        _get('/api/elder/$eid/health/history', queryParams: {'type': 'temperature', 'range': period}),
        _get('/api/elder/$eid/health/history', queryParams: {'type': 'heart_rate', 'range': period}),
        _get('/api/elder/$eid/health/history', queryParams: {'type': 'blood_pressure', 'range': period}),
        _get('/api/elder/$eid/health/history', queryParams: {'type': 'blood_oxygen', 'range': period}),
      ]);

      final tempData = _extractTrendItems(results[0]);
      final hrData = _extractTrendItems(results[1]);
      final bpData = _extractTrendItems(results[2]);
      final boData = _extractTrendItems(results[3]);

      // 合并：按时间对齐，使用 period 感知的标签
      // 对于 week/month，使用日期级标签自动聚合同一天的多条数据
      // 使用累加器结构支持同标签数据的平均值计算
      final merged = <String, _VitalsAccumulator>{};

      void _addTemp(String label, num? value, String rawTime) {
        final acc = merged.putIfAbsent(label, () => _VitalsAccumulator(label: label, rawTime: rawTime));
        if (value != null) { acc.tempSum += value.toDouble(); acc.tempCount++; }
      }
      void _addHr(String label, num? value, String rawTime) {
        final acc = merged.putIfAbsent(label, () => _VitalsAccumulator(label: label, rawTime: rawTime));
        if (value != null) { acc.hrSum += value.toDouble(); acc.hrCount++; }
      }
      void _addBp(String label, num? systolic, num? diastolic, String rawTime) {
        final acc = merged.putIfAbsent(label, () => _VitalsAccumulator(label: label, rawTime: rawTime));
        if (systolic != null) { acc.sysSum += systolic.toDouble(); acc.sysCount++; }
        if (diastolic != null) { acc.diaSum += diastolic.toDouble(); acc.diaCount++; }
      }
      void _addBo(String label, num? value, String rawTime) {
        final acc = merged.putIfAbsent(label, () => _VitalsAccumulator(label: label, rawTime: rawTime));
        if (value != null) { acc.boSum += value.toDouble(); acc.boCount++; }
      }

      for (var i = 0; i < tempData.length; i++) {
        final t = tempData[i];
        final rawTime = t['time'] as String? ?? '';
        final label = _formatLabel(rawTime, period, fallbackIndex: i);
        _addTemp(label, t['value'] as num?, rawTime);
      }
      for (var i = 0; i < hrData.length; i++) {
        final h = hrData[i];
        final rawTime = h['time'] as String? ?? '';
        final label = _formatLabel(rawTime, period, fallbackIndex: i);
        _addHr(label, h['value'] as num?, rawTime);
      }
      for (var i = 0; i < bpData.length; i++) {
        final b = bpData[i];
        final rawTime = b['time'] as String? ?? '';
        final label = _formatLabel(rawTime, period, fallbackIndex: i);
        _addBp(label, b['systolic'] as num?, b['diastolic'] as num?, rawTime);
      }
      for (var i = 0; i < boData.length; i++) {
        final o = boData[i];
        final rawTime = o['time'] as String? ?? '';
        final label = _formatLabel(rawTime, period, fallbackIndex: i);
        _addBo(label, o['value'] as num?, rawTime);
      }

      // 将累加器转换为最终数据点（求平均值）
      final points = merged.values.map((acc) {
        return VitalsHistoryPoint(
          label: acc.label,
          temperature: acc.tempCount > 0 ? acc.tempSum / acc.tempCount : null,
          heartRate: acc.hrCount > 0 ? (acc.hrSum / acc.hrCount).round() : null,
          systolic: acc.sysCount > 0 ? (acc.sysSum / acc.sysCount).round() : null,
          diastolic: acc.diaCount > 0 ? (acc.diaSum / acc.diaCount).round() : null,
          bloodOxygen: acc.boCount > 0 ? (acc.boSum / acc.boCount).round() : null,
        );
      }).toList()
        ..sort((a, b) => _labelSortValue(a.label, period).compareTo(_labelSortValue(b.label, period)));

      if (points.isNotEmpty) {
        return VitalsHistory(period: period, points: points);
      }
    } catch (_) {
      // 后端无数据或接口不可用时回退 mock
    }
    return _generateMockHistory(period);
  }

  /// 从 health/history 响应中提取 data 列表
  List<Map<String, dynamic>> _extractTrendItems(Map<String, dynamic> resp) {
    final data = _extractData(resp);
    if (data is Map && data['data'] is List) {
      return (data['data'] as List).map((e) => e as Map<String, dynamic>).toList();
    }
    return [];
  }

  /// 获取单项体征趋势。
  ///
  /// 使用 /api/elder/{elderId}/health/history，返回 HealthTrendDto。
  /// HealthTrendDto.data 是 List<HealthTrendItemDto>，每项含 time/value/systolic/diastolic。
  Future<VitalsHistory> fetchTrend({String? elderId, String? type, String period = 'week'}) async {
    final eid = elderId ?? _elderId;
    if (eid == null || eid.isEmpty) {
      return _generateMockHistory(period);
    }
    final vitalType = type ?? 'heart_rate';
    final resp = await _get('/api/elder/$eid/health/history', queryParams: {
      'type': vitalType,
      'range': period,
    });
    final data = _extractData(resp) as Map<String, dynamic>?;
    final points = <VitalsHistoryPoint>[];
    if (data != null && data['data'] is List) {
      final items = data['data'] as List;
      for (var i = 0; i < items.length; i++) {
        final m = items[i] as Map<String, dynamic>;
        final isBP = vitalType == 'blood_pressure';
        final isBO = vitalType == 'blood_oxygen';
        points.add(VitalsHistoryPoint(
          label: _formatLabel(m['time'], period, fallbackIndex: i),
          temperature: (isBP || isBO) ? null : (vitalType == 'temperature' ? (m['value'] as num?)?.toDouble() : null),
          heartRate: (isBP || isBO) ? null : (vitalType == 'heart_rate' ? (m['value'] as num?)?.toInt() : null),
          systolic: (m['systolic'] as num?)?.toInt(),
          diastolic: (m['diastolic'] as num?)?.toInt(),
          bloodOxygen: isBO ? (m['value'] as num?)?.toInt() : null,
        ));
      }
    }
    if (points.isEmpty) {
      return _generateMockHistory(period);
    }
    return VitalsHistory(period: period, points: points);
  }

  /// AI 健康分析。
  ///
  /// [useLlm] 为 true 时调用 DeepSeek 大模型生成专业分析文案（较慢，消耗 API 额度）；
  /// 为 false 时仅使用本地引擎（快速，不消耗 API）。
  /// 统计数据始终由本地引擎保证准确性。
  Future<AiAnalysis> analyzeAi({
    String? elderId,
    String period = 'week',
    bool useLlm = false,
  }) async {
    try {
      final eid = elderId ?? _elderId;
      if (eid == null || eid.isEmpty) {
        return _fallbackAnalysis(period);
      }

      // Fetch all data sources in parallel
      final results = await Future.wait([
        fetchHistory(period, elderId: eid),
        fetchLatestVitals(elderId: eid),
        fetchAlerts(elderId: eid),
        fetchHealthRecord(elderId: eid),
        fetchProfile(),
        fetchSosHistory(eid),
      ]);

      final history = results[0] as VitalsHistory;
      final latest = results[1] as LatestVitals;
      final alerts = results[2] as List<AlertItem>;
      final healthRecord = results[3] as Map<String, dynamic>?;
      final profile = results[4] as Profile?;
      final sosHistory = results[5] as List<Map<String, dynamic>>;

      if (useLlm) {
        // 混合模式：本地引擎 + DeepSeek 大模型
        final llm = LlmService();
        final result = await llm.hybridAnalyze(
          history: history,
          latest: latest,
          alerts: alerts,
          period: period,
          healthRecord: healthRecord,
          profile: profile,
          sosHistory: sosHistory,
        );
        return result.analysis;
      } else {
        // 纯本地引擎（快速，不消耗 API）
        return AnalysisEngine().analyze(
          history: history,
          temperature: latest.temperature,
          heartRate: latest.heartRate,
          systolic: latest.systolic,
          diastolic: latest.diastolic,
          bloodOxygen: latest.bloodOxygen,
          alerts: alerts,
          healthRecord: healthRecord,
          profile: profile != null
              ? {'name': profile.name, 'age': profile.age}
              : null,
          sosHistory: sosHistory,
          period: period,
        );
      }
    } catch (_) {
      return _fallbackAnalysis(period);
    }
  }

  /// AI 情绪状态分析。
  ///
  /// 调用后端 /api/ai/emotion-analysis 接口，
  /// 综合健康数据、睡眠监测、告警记录等多维度信息分析老人情绪/心理状态。
  ///
  /// 返回 [EmotionAnalysis] 包含焦虑评分、情绪等级、依据和建议。
  Future<EmotionAnalysis?> fetchEmotionAnalysis({
    String? elderId,
    String period = 'week',
  }) async {
    try {
      final eid = elderId ?? _elderId;
      if (eid == null || eid.isEmpty) return null;

      // Gather all required data for emotion analysis
      final results = await Future.wait([
        fetchLatestVitals(elderId: eid),
        fetchAlerts(elderId: eid),
        fetchLatestSleep(elderId: eid),
      ]);

      final latest = results[0] as LatestVitals;
      final alerts = results[1] as List<AlertItem>;
      final sleepData = results[2] as Map<String, dynamic>?;

      // Count recent alarms by type
      int sosCount = 0;
      int fallCount = 0;
      int healthCount = 0;
      final now = DateTime.now();
      final periodStart = period == 'day'
          ? now.subtract(const Duration(days: 1))
          : period == 'month'
              ? now.subtract(const Duration(days: 30))
              : now.subtract(const Duration(days: 7));
      for (final a in alerts) {
        if (a.occurredAt.isBefore(periodStart)) continue;
        healthCount++;
        final typeLower = a.type.labelZh;
        if (typeLower.contains('SOS') || typeLower.contains('呼救')) sosCount++;
        if (typeLower.contains('跌倒') || typeLower.contains('FALL')) fallCount++;
      }

      // Use real sleep data if available, otherwise default to normal
      final insomniaLevel = sleepData?['insomnia_level'] as String? ?? '正常';
      final qualityScore = (sleepData?['quality_score'] as num?)?.toInt() ?? 85;
      final wakeCount = (sleepData?['wake_count'] as num?)?.toInt() ?? 0;

      final body = <String, dynamic>{
        'elder_id': eid,
        'recent_health': {
          'heart_rate': latest.heartRate,
          'spo2': latest.bloodOxygen,
          'temperature': latest.temperature,
          'systolic': latest.systolic,
          'diastolic': latest.diastolic,
        },
        'sleep_data': {
          'insomnia_level': insomniaLevel,
          'quality_score': qualityScore,
          'wake_count': wakeCount,
        },
        'recent_alarms': {
          'total_count': healthCount,
          'sos_count': sosCount,
          'fall_count': fallCount,
          'health_abnormal_count': healthCount - sosCount - fallCount,
        },
      };

      final resp = await _post('/api/ai/emotion-analysis', body: body);
      final data = _extractData(resp) as Map<String, dynamic>?;
      if (data == null) return null;

      return EmotionAnalysis.fromJson(data);
    } catch (e) {
      // Return null on failure — UI shows a fallback message
      return null;
    }
  }

  /// 获取老人最新睡眠记录。
  ///
  /// 调用 GET /api/sleep-record/latest，返回包含失眠等级、质量评分、
  /// 醒来次数等字段的 Map。无记录时返回 null。
  Future<Map<String, dynamic>?> fetchLatestSleep({String? elderId}) async {
    try {
      final eid = elderId ?? _elderId;
      if (eid == null || eid.isEmpty) return null;
      final resp = await _get('/api/sleep-record/latest', queryParams: {'elderId': eid});
      final data = _extractData(resp) as Map<String, dynamic>?;
      return data;
    } catch (_) {
      return null; // 无睡眠记录是正常情况，不抛异常
    }
  }

  /// 数据不足或网络异常时的降级分析结果。
  AiAnalysis _fallbackAnalysis(String period) {
    final periodLabel = period == 'day' ? '今日' : (period == 'month' ? '本月' : '本周');
    return AiAnalysis(
      meta: AiAnalysisMeta(
        analyzedAt: DateTime.now(),
        period: period,
        dataCompleteness: DataCompleteness.minimal,
      ),
      overall: OverallStatus(
        level: HealthLevel.attention,
        score: 50,
        summary: '$periodLabel暂无足够体征数据，无法进行完整健康分析。',
      ),
      metrics: [
        MetricAnalysis(
          type: VitalSignType.temperature,
          label: '体温',
          unit: '°C',
          status: MetricStatus.normal,
          assessment: '暂无数据',
          hasData: false,
        ),
        MetricAnalysis(
          type: VitalSignType.heartRate,
          label: '心率',
          unit: 'bpm',
          status: MetricStatus.normal,
          assessment: '暂无数据',
          hasData: false,
        ),
        MetricAnalysis(
          type: VitalSignType.bloodPressure,
          label: '血压',
          unit: 'mmHg',
          status: MetricStatus.normal,
          assessment: '暂无数据',
          hasData: false,
        ),
        MetricAnalysis(
          type: VitalSignType.bloodOxygen,
          label: '血氧',
          unit: '%',
          status: MetricStatus.normal,
          assessment: '暂无数据',
          hasData: false,
        ),
      ],
      riskFactors: [],
      suggestions: [
        Suggestion(
          category: SuggestionCategory.checkup,
          content: '暂无足够数据进行分析，请确保设备正常运行并上传体征数据。',
          priority: 1,
        ),
      ],
      trendSummary: TrendSummary(
        direction: TrendDirection.stable,
        description: '暂无足够数据进行趋势分析',
      ),
      fromLlm: false,
    );
  }

  // ---------- 告警 ----------

  /// 获取告警列表。
  /// 后端返回 PageResult<AlarmDto>（data.list）。
  Future<List<AlertItem>> fetchAlerts({String? elderId}) async {
    final eid = elderId ?? _elderId;
    if (eid == null || eid.isEmpty) return [];
    final resp = await _get('/api/alarm/list', queryParams: {
      'elderId': eid,
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

  Future<AlertItem?> fetchLatestAlert({String? elderId}) async {
    final eid = elderId ?? _elderId;
    if (eid == null || eid.isEmpty) return null;
    // 请求较大的 pageSize 并多页查询，确保获取到最新告警
    final allAlerts = <AlertItem>[];
    for (int page = 1; page <= 3; page++) {
      final resp = await _get('/api/alarm/list', queryParams: {
        'elderId': eid,
        'page': page.toString(),
        'pageSize': '100',
        'sort': 'occurTime',
        'order': 'desc',
      });
      final data = _extractData(resp);
      final list = (data is Map && data['list'] is List)
          ? data['list'] as List
          : (data is List ? data : <dynamic>[]);
      if (list.isEmpty) break;
      allAlerts.addAll(list.map((a) => _convertAlarm(a as Map<String, dynamic>)));
      // 如果返回数量少于 pageSize，说明已经是最后一页
      final total = (data is Map ? data['total'] : null) as int?;
      if (total != null && allAlerts.length >= total) break;
      if (list.length < 100) break;
    }
    if (allAlerts.isEmpty) return null;
    // Sort by occurredAt descending to get the latest alert
    allAlerts.sort((a, b) => b.occurredAt.compareTo(a.occurredAt));
    return allAlerts.first;
  }

  Future<void> markAlarmRead(String alarmId) async {
    await _put('/api/alarm/$alarmId/read');
  }

  Future<int> fetchAlarmUnreadCount(String elderId) async {
    final eid = elderId.isNotEmpty ? elderId : _elderId;
    if (eid == null || eid.isEmpty) return 0;
    final resp = await _get('/api/alarm/unread-count', queryParams: {'elderId': eid});
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
      id: m['alarmId']?.toString() ?? '',
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
    String? uid = userId ?? _userId;
    // Fallback: try SharedPreferences if _userId not yet set
    if (uid == null || uid.isEmpty) {
      try {
        final prefs = await _getPrefs();
        uid = prefs.getString('app_user_id');
      } catch (_) {}
    }
    if (uid == null || uid.isEmpty) return [];
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
    final uid = userId ?? _userId;
    if (uid == null || uid.isEmpty) return 0;
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

  /// 一键已读所有通知
  Future<void> markAllNotificationsRead({String? userId, String userType = 'family'}) async {
    final uid = userId ?? _userId;
    if (uid == null || uid.isEmpty) return;
    await _post('/api/notification/read-all', queryParams: {
      'userId': uid,
      'userType': userType,
    });
  }

  /// 将 NotificationDto (camelCase) 转为 NotificationItem。
  /// 后端 NotificationDto.type 对应通知类型。
  NotificationItem _convertNotification(Map<String, dynamic> m) {
    return NotificationItem(
      id: m['notificationId']?.toString() ?? '',
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
    final fid = familyId ?? _userId;
    if (fid == null || fid.isEmpty) return [];
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
      id: m['requestId']?.toString() ?? '',
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
    final fid = familyId ?? _userId;
    if (fid == null || fid.isEmpty) return [];
    final resp = await _get('/api/service-request/my-list', queryParams: {'familyId': fid});
    final data = _extractData(resp);
    final list = data is List ? data : <dynamic>[];
    return list.map((r) => _convertServiceRequest(r as Map<String, dynamic>)).toList();
  }

  /// 提交服务申请。
  /// 后端 ServiceRequestDto 字段：type, content, familyId, elderId, elderName 等。
  Future<void> submitServiceRequest(ServiceRequest request) async {
    await _post('/api/service-request', body: {
      'type': request.type,
      'content': request.content,
      if (_userId != null) 'familyId': _userId,
      if (_elderId != null) 'elderId': _elderId,
      if (request.elderName.isNotEmpty) 'elderName': request.elderName,
    });
  }

  /// 将 ServiceRequestDto 转为 ServiceRequest。
  /// 后端 DTO 字段：requestId, type, content, status, createTime, convertedWorkOrderId, elderName 等。
  ServiceRequest _convertServiceRequest(Map<String, dynamic> m) {
    return ServiceRequest(
      id: m['requestId']?.toString() ?? '',
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

  // ---------- 健康档案 ----------

  /// 获取老人健康档案。
  /// 后端 GET /api/health-record/by-elder/{elderId}
  Future<Map<String, dynamic>?> fetchHealthRecord({String? elderId}) async {
    final eid = elderId ?? _elderId;
    if (eid == null || eid.isEmpty) return null;
    try {
      final resp = await _get('/api/health-record/by-elder/$eid');
      final data = _extractData(resp) as Map<String, dynamic>?;
      return data;
    } catch (_) {
      return null;
    }
  }

  /// 保存老人健康档案。
  /// 后端 POST /api/health-record
  Future<void> saveHealthRecord(Map<String, dynamic> record, {String? elderId}) async {
    final eid = elderId ?? _elderId;
    if (eid == null || eid.isEmpty) return;
    try {
      await _post('/api/health-record', body: {
        'elderId': eid,
        'hospitalizationInfo': record['hospitalizations'] ?? '',
        'medicalHistory': record['medicalHistory'] ?? '',
        'allergyHistory': record['allergies'] ?? '',
        'commonMedications': record['medications'] ?? '',
        'bloodType': record['bloodType'] ?? '',
        'remarks': record['remarks'] ?? '',
      });
    } catch (_) {
      // 静默处理
    }
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

  /// 懒加载 SharedPreferences 实例（避免在构造函数中初始化）
  SharedPreferences? _prefsCache;
  Future<SharedPreferences> _getPrefs() async {
    _prefsCache ??= await SharedPreferences.getInstance();
    return _prefsCache!;
  }

  DateTime _parseDateTime(dynamic value) {
    if (value == null) return DateTime.now();
    if (value is DateTime) return value;
    if (value is String) {
      // Try ISO 8601 / "yyyy-MM-dd HH:mm:ss" / "yyyy-MM-ddTHH:mm:ss"
      try {
        return DateTime.parse(value.replaceFirst(' ', 'T'));
      } catch (_) {}
      // Try "yyyy-MM-dd" only
      try {
        return DateTime.parse(value.trim());
      } catch (_) {}
      // Try "HH:mm:ss" or "HH:mm" — use today's date
      try {
        final parts = value.trim().split(':');
        if (parts.length >= 2) {
          final now = DateTime.now();
          final h = int.parse(parts[0]);
          final m = int.parse(parts[1]);
          final s = parts.length >= 3 ? int.parse(parts[2]) : 0;
          return DateTime(now.year, now.month, now.day, h, m, s);
        }
      } catch (_) {}
      // Try epoch milliseconds
      final ms = int.tryParse(value.trim());
      if (ms != null && ms > 1000000000000) {
        return DateTime.fromMillisecondsSinceEpoch(ms);
      }
      return DateTime.now();
    }
    if (value is num) {
      final ms = value.toInt();
      if (ms > 1000000000000) {
        return DateTime.fromMillisecondsSinceEpoch(ms);
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

  /// 根据 period 生成合适的标签：
  /// - day: HH:mm（如 "08:00"）
  /// - week: yyyy-MM-dd（如 "2026-07-10"），用于按天聚合和排序
  /// - month: d日（如 "15日"）
  ///
  /// [fallbackIndex] 用于当时间解析失败时生成基于索引的标签（0-based）。
  String _formatLabel(dynamic value, String period, {int fallbackIndex = 0}) {
    // If the value already looks like a valid label for this period, use it directly
    if (value is String) {
      switch (period) {
        case 'day':
          // For day view, always parse to extract just HH:mm
          try {
            final dt = DateTime.parse(value.replaceFirst(' ', 'T'));
            return '${_pad(dt.hour)}:${_pad(dt.minute)}';
          } catch (_) {}
          final timeMatch = RegExp(r'(\d{1,2}):(\d{2})').firstMatch(value);
          if (timeMatch != null) {
            return '${_pad(int.parse(timeMatch.group(1)!))}:${timeMatch.group(2)}';
          }
          break;
        case 'week':
          // Week view: return yyyy-MM-dd for day-level aggregation
          try {
            final dt = DateTime.parse(value.replaceFirst(' ', 'T'));
            return '${dt.year}-${_pad(dt.month)}-${_pad(dt.day)}';
          } catch (_) {}
          break;
        case 'month':
          if (value.contains('日')) return value;
          break;
      }
    }

    // Try to parse as datetime
    final dt = _parseDateTime(value);
    final now = DateTime.now();

    // Detect parse failure: _parseDateTime returns DateTime.now() on failure.
    // A value that looks like a time string (contains ':') could legitimately
    // be from today — only treat as failure if it doesn't look time-like.
    final looksLikeNow = dt.year == now.year && dt.month == now.month && dt.day == now.day;
    final isTimeStr = value is String && value.contains(':');
    final parseFailed = looksLikeNow && !isTimeStr;

    if (parseFailed) {
      // Use index-based fallback labels
      switch (period) {
        case 'day':
          final hour = (8 + fallbackIndex * 2) % 24;
          return '${_pad(hour)}:00';
        case 'month':
          return '${fallbackIndex + 1}日';
        case 'week':
        default:
          // fallback: use dates counting back from today
          final d = DateTime.now().subtract(Duration(days: 6 - fallbackIndex.clamp(0, 6)));
          return '${d.year}-${_pad(d.month)}-${_pad(d.day)}';
      }
    }
    switch (period) {
      case 'day':
        return '${_pad(dt.hour)}:${_pad(dt.minute)}';
      case 'month':
        return '${dt.day}日';
      case 'week':
      default:
        return '${dt.year}-${_pad(dt.month)}-${_pad(dt.day)}';
    }
  }

  String _weekdayName(int weekday) {
    return switch (weekday) {
      1 => '周一',
      2 => '周二',
      3 => '周三',
      4 => '周四',
      5 => '周五',
      6 => '周六',
      7 => '周日',
      _ => '?',
    };
  }

  /// 将 period 感知的标签转换为可排序的数值。
  /// - week: "周一" → 1, "周日" → 7
  /// - month: "15日" → 15
  /// - day: "08:30" → 8.5
  double _labelSortValue(String label, String period) {
    switch (period) {
      case 'week':
        // Label format: "yyyy-MM-dd", compute days from week start (7 days ago)
        try {
          final dt = DateTime.parse(label); // yyyy-MM-dd is valid ISO format
          final weekStart = DateTime.now().subtract(const Duration(days: 6));
          // Normalize to start of day for comparison
          final weekStartDay = DateTime(weekStart.year, weekStart.month, weekStart.day);
          final labelDay = DateTime(dt.year, dt.month, dt.day);
          return labelDay.difference(weekStartDay).inDays.toDouble().clamp(0.0, 6.0);
        } catch (_) {
          return 0.0;
        }
      case 'month':
        final m = RegExp(r'(\d+)').firstMatch(label);
        if (m != null) {
          final d = int.tryParse(m.group(1)!) ?? 1;
          if (d >= 1 && d <= 31) return d.toDouble();
        }
        return 1.0;
      case 'day':
        final t = RegExp(r'(\d{1,2}):(\d{2})').firstMatch(label);
        if (t != null) {
          final h = int.tryParse(t.group(1)!) ?? 0;
          final m = int.tryParse(t.group(2)!) ?? 0;
          return h + m / 60.0;
        }
        return 0.0;
      default:
        return 0.0;
    }
  }

  String _pad(int n) => n.toString().padLeft(2, '0');

  // ---------- 兼容旧页面的 Mock 回退数据 ----------

  static VitalsHistory _generateMockHistory(String period) {
    final now = DateTime.now();
    final rng = _mockRandom(now.millisecond);
    switch (period) {
      case 'day':
        final points = <VitalsHistoryPoint>[];
        // Generate data for all 24 hours with realistic circadian rhythm
        for (int h = 0; h <= 23; h++) {
          // Base temperature follows circadian rhythm (lowest at 3-5am, peak at 2-4pm)
          double baseTemp;
          int baseHr;
          if (h < 6) {
            baseTemp = 36.2; baseHr = 62; // midnight to dawn: lowest (sleeping)
          } else if (h < 8) {
            baseTemp = 36.3; baseHr = 66; // early morning: waking up
          } else if (h < 11) {
            baseTemp = 36.5; baseHr = 78; // morning: rising
          } else if (h < 14) {
            baseTemp = 36.8; baseHr = 85; // midday: peak
          } else if (h < 17) {
            baseTemp = 36.7; baseHr = 82; // afternoon: slightly lower
          } else if (h < 21) {
            baseTemp = 36.5; baseHr = 73; // evening: falling
          } else {
            baseTemp = 36.4; baseHr = 68; // late night: resting
          }
          points.add(VitalsHistoryPoint(
            label: '${h.toString().padLeft(2, '0')}:${(rng * 7 + h * 13) % 60 < 30 ? '00' : '30'}',
            temperature: baseTemp + (h + rng) % 5 / 10.0 - 0.1,
            heartRate: baseHr + (rng + h) % 10,
            systolic: 120 + (rng + h * 2) % 15,
            diastolic: 78 + (rng + h) % 10,
            bloodOxygen: 95 + (rng + h) % 4,
          ));
        }
        return VitalsHistory(period: 'day', points: points);

      case 'week':
        final points = <VitalsHistoryPoint>[];
        // Only show up to current weekday (e.g., if today is Friday, show Mon-Fri)
        final todayWeekday = now.weekday; // 1=Mon, 7=Sun
        for (int d = 1; d <= todayWeekday; d++) {
          // Calculate actual date for this weekday
          final daysBack = todayWeekday - d;
          final date = now.subtract(Duration(days: daysBack));
          final label = '${date.year}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')}';
          points.add(VitalsHistoryPoint(
            label: label,
            temperature: 36.3 + (rng + d) % 5 / 10.0,
            heartRate: 72 + (rng + d) % 10,
            systolic: 124 + (rng + d) % 8,
            diastolic: 81 + (rng + d) % 6,
            bloodOxygen: 96 + (rng + d) % 3,
          ));
        }
        return VitalsHistory(period: 'week', points: points);

      case 'month':
        final points = <VitalsHistoryPoint>[];
        final maxDay = now.day; // only show up to today
        // 确保从1号开始，每隔1-2天生成一个数据点，保证数据连续
        for (int d = 1; d <= maxDay; d++) {
          // 1号、最大值、以及每2天生成一个数据点（更密集）
          if (d == 1 || d % 2 == 0 || d == maxDay) {
            points.add(VitalsHistoryPoint(
              label: '${d}日',
              temperature: 36.3 + (rng + d) % 5 / 10.0,
              heartRate: 72 + (rng + d) % 10,
              systolic: 124 + (rng + d) % 10,
              diastolic: 81 + (rng + d) % 6,
              bloodOxygen: 96 + (rng + d) % 3,
            ));
          }
        }
        return VitalsHistory(period: 'month', points: points);

      default:
        return _generateMockHistory('week');
    }
  }

  static int _mockRandom(int seed) => (seed * 1103515245 + 12345) & 0x7fffffff;

  static String _staticWeekdayName(int weekday) {
    return switch (weekday) {
      1 => '周一',
      2 => '周二',
      3 => '周三',
      4 => '周四',
      5 => '周五',
      6 => '周六',
      7 => '周日',
      _ => '?',
    };
  }

  static final Map<String, VitalsHistory> _mockHistory = {
    'day': _generateMockHistory('day'),
    'week': _generateMockHistory('week'),
    'month': _generateMockHistory('month'),
  };

}

/// 体征数据累加器，用于同标签多条数据的平均值计算。
class _VitalsAccumulator {
  _VitalsAccumulator({required this.label, required this.rawTime});

  final String label;
  final String rawTime;
  double tempSum = 0;
  int tempCount = 0;
  double hrSum = 0;
  int hrCount = 0;
  double sysSum = 0;
  int sysCount = 0;
  double diaSum = 0;
  int diaCount = 0;
  double boSum = 0;
  int boCount = 0;
}
