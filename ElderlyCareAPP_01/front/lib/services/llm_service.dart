import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/ai_analysis.dart';
import '../models/vitals_history.dart';
import '../models/latest_vitals.dart';
import '../models/alert_item.dart';
import '../models/profile.dart';
import 'analysis_engine.dart';

/// DeepSeek LLM 健康分析服务。
///
/// 使用 Anthropic 兼容接口调用 DeepSeek 大模型，
/// 将结构化健康数据构造成 prompt，生成专业的自然语言分析文案。
class LlmService {
  // ──── 配置 ────

  /// DeepSeek Anthropic 兼容接口地址
  static const String baseUrl = 'https://api.deepseek.com/anthropic/v1/messages';

  /// 默认模型
  static const String model = 'deepseek-v4-flash';

  /// Anthropic API 版本
  static const String apiVersion = '2023-06-01';

  /// DeepSeek API Key（硬编码，不对外暴露）
  static const String _apiKey = 'sk-562b3c54cdc94f28802447dde98e16fa';

  // ──── 顶层：混合分析 ────

  /// 混合分析：本地引擎计算统计数据 + 大模型生成自然语言文案。
  ///
  /// 如果 API Key 未配置或网络异常，自动降级为纯本地引擎分析。
  Future<({
    bool llmSuccess,
    AiAnalysis analysis,
  })> hybridAnalyze({
    required VitalsHistory history,
    required LatestVitals latest,
    required List<AlertItem> alerts,
    required String period,
    Map<String, dynamic>? healthRecord,
    Profile? profile,
    List<Map<String, dynamic>>? sosHistory,
  }) async {
    // 1. 本地引擎计算统计数据
    final engine = AnalysisEngine();
    final localResult = engine.analyze(
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

    // 2. 尝试调用 LLM 生成自然语言文案
    try {
      final prompt = _buildHealthPrompt(
        history: history,
        latest: latest,
        alerts: alerts,
        period: period,
        healthRecord: healthRecord,
        profile: profile,
        sosHistory: sosHistory,
        localResult: localResult,
      );

      final llmResponse = await _callLlm(prompt);

      if (llmResponse != null) {
        // 3. 合并：本地统计数据 + LLM 文案
        final merged = _mergeResults(localResult, llmResponse, period);
        return (llmSuccess: true, analysis: merged);
      }
    } catch (_) {
      // LLM 调用失败，降级为纯本地引擎
    }

    return (llmSuccess: false, analysis: localResult);
  }

  // ──── Prompt 构造 ────

  /// 将健康数据构造成大模型 prompt
  String _buildHealthPrompt({
    required VitalsHistory history,
    required LatestVitals latest,
    required List<AlertItem> alerts,
    required String period,
    Map<String, dynamic>? healthRecord,
    Profile? profile,
    List<Map<String, dynamic>>? sosHistory,
    required AiAnalysis localResult,
  }) {
    final periodLabel = period == 'day' ? '今日' : (period == 'month' ? '本月' : '本周');
    final buffer = StringBuffer();

    buffer.writeln('你是一位专业的老年健康管理AI助手，拥有丰富的老年医学和健康管理经验。');
    buffer.writeln('请根据以下老人的健康数据，生成一份专业、详尽、有人情味的健康分析报告。');
    buffer.writeln();
    buffer.writeln('## 老人基本信息');
    buffer.writeln('- 姓名：${profile?.name ?? '未知'}');
    buffer.writeln('- 年龄：${profile?.age ?? '未知'}岁');
    buffer.writeln('- 性别：${profile?.gender ?? '未知'}');
    buffer.writeln('- 病史：${healthRecord?['medicalHistory']?.toString().isNotEmpty == true ? healthRecord!['medicalHistory'] : '无记录'}');
    buffer.writeln('- 过敏史：${healthRecord?['allergies']?.toString().isNotEmpty == true ? healthRecord!['allergies'] : '无记录'}');
    buffer.writeln('- 常用药物：${healthRecord?['medications']?.toString().isNotEmpty == true ? healthRecord!['medications'] : '无记录'}');
    buffer.writeln('- 血型：${healthRecord?['bloodType']?.toString().isNotEmpty == true ? healthRecord!['bloodType'] : '未知'}');
    buffer.writeln();

    buffer.writeln('## 分析周期：$periodLabel');
    buffer.writeln();

    buffer.writeln('## 体征数据统计');
    for (final m in localResult.metrics) {
      if (!m.hasData) {
        buffer.writeln('### ${m.label}：暂无数据');
        continue;
      }
      buffer.writeln('### ${m.label}（${m.unit}）');
      buffer.writeln('- 状态：${_statusLabel(m.status)}');
      buffer.writeln('- 平均值：${_fmt(m.average, m.type)} ${m.unit}');
      buffer.writeln('- 最低值：${_fmt(m.min, m.type)} ${m.unit}');
      buffer.writeln('- 最高值：${_fmt(m.max, m.type)} ${m.unit}');
      buffer.writeln('- 最新值：${_fmt(m.latest, m.type)} ${m.unit}');
      buffer.writeln('- 趋势：${m.trendDescription ?? '数据不足'}');
      buffer.writeln();
    }

    buffer.writeln('## 健康综合评分');
    buffer.writeln('- 综合评分：${localResult.overall.score}/100 分');
    buffer.writeln('- 健康等级：${localResult.healthLevelLabel}');
    buffer.writeln();

    buffer.writeln('## 风险提示');
    if (localResult.riskFactors.isNotEmpty) {
      for (final r in localResult.riskFactors) {
        buffer.writeln('- [${_riskLabel(r.level)}] ${r.title}：${r.description}');
      }
    } else {
      buffer.writeln('- 暂未发现明显风险');
    }
    buffer.writeln();

    if (alerts.isNotEmpty) {
      buffer.writeln('## 近期告警记录（共${alerts.length}条）');
      for (final a in alerts.take(10)) {
        buffer.writeln('- ${a.type.name}：${a.detail}（${_timeStr(a.occurredAt)}）');
      }
      buffer.writeln();
    }

    if (sosHistory != null && sosHistory.isNotEmpty) {
      buffer.writeln('## SOS紧急求助记录（共${sosHistory.length}次）');
      buffer.writeln('老人近期有过紧急求助记录，请关注其居家安全。');
      buffer.writeln();
    }

    // 实时数据
    buffer.writeln('## 当前最新体征');
    buffer.writeln('- 体温：${latest.temperature > 0 ? '${latest.temperature.toStringAsFixed(1)}°C' : '无数据'}');
    buffer.writeln('- 心率：${latest.heartRate > 0 ? '${latest.heartRate}bpm' : '无数据'}');
    buffer.writeln('- 血压：${latest.systolic > 0 && latest.diastolic > 0 ? '${latest.systolic}/${latest.diastolic} mmHg' : '无数据'}');
    buffer.writeln('- 血氧：${latest.bloodOxygen != null && latest.bloodOxygen! > 0 ? '${latest.bloodOxygen}%' : '无数据'}');
    buffer.writeln();

    buffer.writeln('---');
    buffer.writeln();
    buffer.writeln('请基于以上数据，以专业老年健康管理师的口吻，生成分析报告。');
    buffer.writeln('注意：如果某项数据缺失，请不要编造，如实说明数据不足。');
    buffer.writeln('分析应该温暖、专业、实用，站在家属角度提供可操作的建议。');
    buffer.writeln();
    buffer.writeln('请严格按照以下JSON格式回复（不要包含其他内容，只输出JSON）：');
    buffer.writeln('{');
    buffer.writeln('  "summary": "300字以内的${periodLabel}健康情况综合概述，包含关键数据，语言温暖专业",');
    buffer.writeln('  "suggestions": [');
    buffer.writeln('    {"category": "diet|exercise|medication|checkup|lifestyle|emergency", "content": "具体可操作的建议内容", "priority": 1-5}');
    buffer.writeln('  ],');
    buffer.writeln('  "trendDescription": "整体趋势一句话描述，15字以内",');
    buffer.writeln('  "riskOverview": "风险评估概述，如果有风险要明确指出，无风险则说明整体平稳"');
    buffer.writeln('}');

    return buffer.toString();
  }

  // ──── API 调用 ────

  /// 调用 DeepSeek Anthropic 兼容 API
  ///
  /// 返回解析后的 JSON Map，如果失败返回 null。
  Future<Map<String, dynamic>?> _callLlm(String prompt) async {
    final uri = Uri.parse(baseUrl);

    final body = jsonEncode({
      'model': model,
      'max_tokens': 2048,
      'temperature': 0.7,
      'messages': [
        {
          'role': 'user',
          'content': prompt,
        }
      ],
    });

    final response = await http
        .post(
          uri,
          headers: {
            'Content-Type': 'application/json',
            'x-api-key': _apiKey,
            'anthropic-version': apiVersion,
          },
          body: body,
        )
        .timeout(const Duration(seconds: 30));

    if (response.statusCode != 200) {
      return null;
    }

    final data = jsonDecode(response.body) as Map<String, dynamic>;

    // Anthropic 格式响应: { content: [{ type: "text", text: "..." }] }
    final contentList = data['content'] as List<dynamic>?;
    if (contentList == null || contentList.isEmpty) return null;

    final textContent = contentList
        .whereType<Map<String, dynamic>>()
        .firstWhere(
          (c) => c['type'] == 'text',
          orElse: () => <String, dynamic>{},
        );

    final text = textContent['text'] as String?;
    if (text == null || text.isEmpty) return null;

    return _parseLlmJson(text);
  }

  /// 从 LLM 返回的文本中提取 JSON
  Map<String, dynamic>? _parseLlmJson(String text) {
    // 尝试直接解析整个文本
    try {
      return jsonDecode(text) as Map<String, dynamic>;
    } catch (_) {}

    // 尝试提取 ```json ... ``` 代码块
    final codeBlock = RegExp(r'```(?:json)?\s*([\s\S]*?)```');
    final match = codeBlock.firstMatch(text);
    if (match != null) {
      try {
        return jsonDecode(match.group(1)!.trim()) as Map<String, dynamic>;
      } catch (_) {}
    }

    // 尝试找到第一个 { 到最后一个 }
    final start = text.indexOf('{');
    final end = text.lastIndexOf('}');
    if (start >= 0 && end > start) {
      try {
        return jsonDecode(text.substring(start, end + 1)) as Map<String, dynamic>;
      } catch (_) {}
    }

    return null;
  }

  // ──── 结果合并 ────

  /// 将本地引擎的统计数据与 LLM 的自然语言文案合并
  AiAnalysis _mergeResults(
    AiAnalysis local,
    Map<String, dynamic> llmResponse,
    String period,
  ) {
    // 解析 LLM 返回的建议列表
    final llmSuggestions = <Suggestion>[];
    final rawSuggestions = llmResponse['suggestions'] as List<dynamic>?;
    if (rawSuggestions != null) {
      for (final s in rawSuggestions) {
        if (s is Map<String, dynamic>) {
          llmSuggestions.add(Suggestion(
            category: _parseCategory(s['category'] as String? ?? 'lifestyle'),
            content: s['content'] as String? ?? '',
            priority: (s['priority'] as num?)?.toInt() ?? 1,
          ));
        }
      }
    }

    // 解析 LLM trend
    final llmTrendDesc = llmResponse['trendDescription'] as String?;

    return AiAnalysis(
      meta: AiAnalysisMeta(
        analyzedAt: DateTime.now(),
        period: period,
        elderName: local.meta.elderName,
        elderAge: local.meta.elderAge,
        dataCompleteness: local.meta.dataCompleteness,
      ),
      overall: OverallStatus(
        level: local.overall.level,
        score: local.overall.score,
        summary: llmResponse['summary'] as String? ?? local.overall.summary,
      ),
      metrics: local.metrics, // 统计数据始终用本地引擎
      riskFactors: local.riskFactors, // 风险评估始终用本地引擎
      suggestions: llmSuggestions.isNotEmpty ? llmSuggestions : local.suggestions,
      trendSummary: TrendSummary(
        direction: local.trendSummary.direction,
        description: llmTrendDesc ?? local.trendSummary.description,
      ),
      fromLlm: true, // 标记为大模型参与分析
    );
  }

  // ──── 辅助方法 ────

  String _statusLabel(MetricStatus s) {
    return switch (s) {
      MetricStatus.normal => '正常',
      MetricStatus.slightlyAbnormal => '略偏离正常',
      MetricStatus.abnormal => '异常',
    };
  }

  String _riskLabel(RiskLevel l) {
    return switch (l) {
      RiskLevel.low => '低风险',
      RiskLevel.medium => '中风险',
      RiskLevel.high => '高风险',
    };
  }

  String _fmt(double? v, VitalSignType type) {
    if (v == null) return '--';
    if (type == VitalSignType.temperature) return v.toStringAsFixed(1);
    return v.round().toString();
  }

  String _timeStr(DateTime dt) {
    return '${dt.month}/${dt.day} ${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
  }

  SuggestionCategory _parseCategory(String cat) {
    return switch (cat) {
      'diet' => SuggestionCategory.diet,
      'exercise' => SuggestionCategory.exercise,
      'medication' => SuggestionCategory.medication,
      'checkup' => SuggestionCategory.checkup,
      'lifestyle' => SuggestionCategory.lifestyle,
      'emergency' => SuggestionCategory.emergency,
      _ => SuggestionCategory.lifestyle,
    };
  }
}

