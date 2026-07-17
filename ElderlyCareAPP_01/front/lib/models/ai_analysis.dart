/// Enums for health analysis

enum HealthLevel { healthy, attention, warning, critical }

enum VitalSignType { temperature, heartRate, bloodPressure, bloodOxygen }

enum MetricStatus { normal, slightlyAbnormal, abnormal }

enum SuggestionCategory {
  diet,
  exercise,
  medication,
  checkup,
  lifestyle,
  emergency,
}

enum TrendDirection { rising, falling, stable, fluctuating }

enum RiskLevel { low, medium, high }

enum DataCompleteness { full, partial, minimal }

// ──── Sub-models ────

class AiAnalysisMeta {
  AiAnalysisMeta({
    required this.analyzedAt,
    required this.period,
    this.elderName,
    this.elderAge,
    required this.dataCompleteness,
  });

  final DateTime analyzedAt;
  final String period;
  final String? elderName;
  final int? elderAge;
  final DataCompleteness dataCompleteness;

  factory AiAnalysisMeta.fromJson(Map<String, dynamic> json) {
    return AiAnalysisMeta(
      analyzedAt: DateTime.parse(json['analyzedAt'] as String),
      period: json['period'] as String,
      elderName: json['elderName'] as String?,
      elderAge: json['elderAge'] as int?,
      dataCompleteness: DataCompleteness.values.firstWhere(
        (e) => e.name == json['dataCompleteness'],
        orElse: () => DataCompleteness.minimal,
      ),
    );
  }
}

class OverallStatus {
  OverallStatus({
    required this.level,
    required this.score,
    required this.summary,
  });

  final HealthLevel level;
  final int score; // 0–100
  final String summary;

  factory OverallStatus.fromJson(Map<String, dynamic> json) {
    return OverallStatus(
      level: HealthLevel.values.firstWhere(
        (e) => e.name == json['level'],
        orElse: () => HealthLevel.attention,
      ),
      score: json['score'] as int,
      summary: json['summary'] as String,
    );
  }
}

class MetricAnalysis {
  MetricAnalysis({
    required this.type,
    required this.label,
    required this.unit,
    this.average,
    this.min,
    this.max,
    this.latest,
    required this.status,
    required this.assessment,
    this.trend,
    this.trendDescription,
    required this.hasData,
  });

  final VitalSignType type;
  final String label;
  final String unit;
  final double? average;
  final double? min;
  final double? max;
  final double? latest;
  final MetricStatus status;
  final String assessment;
  final TrendDirection? trend;
  final String? trendDescription;
  final bool hasData;

  factory MetricAnalysis.fromJson(Map<String, dynamic> json) {
    return MetricAnalysis(
      type: VitalSignType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => VitalSignType.heartRate,
      ),
      label: json['label'] as String,
      unit: json['unit'] as String,
      average: (json['average'] as num?)?.toDouble(),
      min: (json['min'] as num?)?.toDouble(),
      max: (json['max'] as num?)?.toDouble(),
      latest: (json['latest'] as num?)?.toDouble(),
      status: MetricStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => MetricStatus.normal,
      ),
      assessment: json['assessment'] as String,
      trend: json['trend'] != null
          ? TrendDirection.values.firstWhere(
              (e) => e.name == json['trend'],
              orElse: () => TrendDirection.stable,
            )
          : null,
      trendDescription: json['trendDescription'] as String?,
      hasData: json['hasData'] as bool? ?? false,
    );
  }
}

class RiskFactor {
  RiskFactor({
    required this.title,
    required this.description,
    required this.level,
    this.relatedMetric,
  });

  final String title;
  final String description;
  final RiskLevel level;
  final VitalSignType? relatedMetric;

  factory RiskFactor.fromJson(Map<String, dynamic> json) {
    return RiskFactor(
      title: json['title'] as String,
      description: json['description'] as String,
      level: RiskLevel.values.firstWhere(
        (e) => e.name == json['level'],
        orElse: () => RiskLevel.low,
      ),
      relatedMetric: json['relatedMetric'] != null
          ? VitalSignType.values.firstWhere(
              (e) => e.name == json['relatedMetric'],
              orElse: () => VitalSignType.heartRate,
            )
          : null,
    );
  }
}

class Suggestion {
  Suggestion({
    required this.category,
    required this.content,
    required this.priority,
  });

  final SuggestionCategory category;
  final String content;
  final int priority; // 1–5, higher = more urgent

  factory Suggestion.fromJson(Map<String, dynamic> json) {
    return Suggestion(
      category: SuggestionCategory.values.firstWhere(
        (e) => e.name == json['category'],
        orElse: () => SuggestionCategory.lifestyle,
      ),
      content: json['content'] as String,
      priority: json['priority'] as int,
    );
  }
}

class TrendSummary {
  TrendSummary({
    required this.direction,
    required this.description,
  });

  final TrendDirection direction;
  final String description;

  factory TrendSummary.fromJson(Map<String, dynamic> json) {
    return TrendSummary(
      direction: TrendDirection.values.firstWhere(
        (e) => e.name == json['direction'],
        orElse: () => TrendDirection.stable,
      ),
      description: json['description'] as String,
    );
  }
}

// ──── Main model ────

class AiAnalysis {
  AiAnalysis({
    required this.meta,
    required this.overall,
    required this.metrics,
    required this.riskFactors,
    required this.suggestions,
    required this.trendSummary,
    required this.fromLlm,
  });

  final AiAnalysisMeta meta;
  final OverallStatus overall;
  final List<MetricAnalysis> metrics;
  final List<RiskFactor> riskFactors;
  final List<Suggestion> suggestions;
  final TrendSummary trendSummary;
  final bool fromLlm;

  factory AiAnalysis.fromJson(Map<String, dynamic> json) {
    return AiAnalysis(
      meta: AiAnalysisMeta.fromJson(json['meta'] as Map<String, dynamic>),
      overall: OverallStatus.fromJson(json['overall'] as Map<String, dynamic>),
      metrics: (json['metrics'] as List<dynamic>)
          .map((e) => MetricAnalysis.fromJson(e as Map<String, dynamic>))
          .toList(),
      riskFactors: (json['riskFactors'] as List<dynamic>)
          .map((e) => RiskFactor.fromJson(e as Map<String, dynamic>))
          .toList(),
      suggestions: (json['suggestions'] as List<dynamic>)
          .map((e) => Suggestion.fromJson(e as Map<String, dynamic>))
          .toList(),
      trendSummary:
          TrendSummary.fromJson(json['trendSummary'] as Map<String, dynamic>),
      fromLlm: json['fromLlm'] as bool? ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'meta': {
        'analyzedAt': meta.analyzedAt.toIso8601String(),
        'period': meta.period,
        'elderName': meta.elderName,
        'elderAge': meta.elderAge,
        'dataCompleteness': meta.dataCompleteness.name,
      },
      'overall': {
        'level': overall.level.name,
        'score': overall.score,
        'summary': overall.summary,
      },
      'metrics': metrics
          .map((m) => {
                'type': m.type.name,
                'label': m.label,
                'unit': m.unit,
                'average': m.average,
                'min': m.min,
                'max': m.max,
                'latest': m.latest,
                'status': m.status.name,
                'assessment': m.assessment,
                'trend': m.trend?.name,
                'trendDescription': m.trendDescription,
                'hasData': m.hasData,
              })
          .toList(),
      'riskFactors': riskFactors
          .map((r) => {
                'title': r.title,
                'description': r.description,
                'level': r.level.name,
                'relatedMetric': r.relatedMetric?.name,
              })
          .toList(),
      'suggestions': suggestions
          .map((s) => {
                'category': s.category.name,
                'content': s.content,
                'priority': s.priority,
              })
          .toList(),
      'trendSummary': {
        'direction': trendSummary.direction.name,
        'description': trendSummary.description,
      },
      'fromLlm': fromLlm,
    };
  }

  // ──── Helpers for UI display ────

  String get periodLabel {
    return switch (meta.period) {
      'day' => '今日',
      'month' => '本月',
      _ => '本周',
    };
  }

  String get healthLevelLabel {
    return switch (overall.level) {
      HealthLevel.healthy => '健康',
      HealthLevel.attention => '需关注',
      HealthLevel.warning => '警告',
      HealthLevel.critical => '危险',
    };
  }

  List<MetricAnalysis> get metricsWithData =>
      metrics.where((m) => m.hasData).toList();

  int get metricsWithDataCount => metrics.where((m) => m.hasData).length;
}

// ──── Emotion Analysis Model ────

enum EmotionLevel { normal, low, medium, high }

class EmotionEvidence {
  EmotionEvidence({
    required this.dimension,
    required this.finding,
    required this.impact,
  });

  final String dimension;
  final String finding;
  final String impact;

  factory EmotionEvidence.fromJson(Map<String, dynamic> json) {
    return EmotionEvidence(
      dimension: json['dimension'] as String? ?? '',
      finding: json['finding'] as String? ?? '',
      impact: json['impact'] as String? ?? '',
    );
  }
}

class EmotionAnalysis {
  EmotionAnalysis({
    required this.emotionState,
    required this.emotionLevel,
    required this.anxietyScore,
    required this.colorClass,
    required this.conclusion,
    required this.evidences,
    required this.suggestions,
    this.elderName,
    this.analyzedAt,
  });

  final String emotionState; // "重度焦虑", "中度焦虑", "轻度焦虑", "情绪平稳"
  final EmotionLevel emotionLevel;
  final int anxietyScore; // 0-100
  final String colorClass; // "danger", "warning", "info", "success"
  final String conclusion;
  final String? elderName;
  final DateTime? analyzedAt;
  final List<EmotionEvidence> evidences;
  final List<String> suggestions;

  factory EmotionAnalysis.fromJson(Map<String, dynamic> json) {
    return EmotionAnalysis(
      emotionState: json['emotion_state'] as String? ?? '情绪平稳',
      emotionLevel: _parseEmotionLevel(json['emotion_level'] as String?),
      anxietyScore: (json['anxiety_score'] as num?)?.toInt() ?? 0,
      colorClass: json['color_class'] as String? ?? 'success',
      conclusion: json['conclusion'] as String? ?? '',
      elderName: json['elder_name'] as String?,
      analyzedAt: json['analyzed_at'] != null
          ? DateTime.tryParse(json['analyzed_at'] as String)
          : null,
      evidences: (json['evidences'] as List<dynamic>?)
              ?.map((e) => EmotionEvidence.fromJson(e as Map<String, dynamic>))
              .toList() ??
          [],
      suggestions: (json['suggestions'] as List<dynamic>?)
              ?.map((e) => e.toString())
              .toList() ??
          [],
    );
  }

  static EmotionLevel _parseEmotionLevel(String? level) {
    switch (level) {
      case 'normal':
        return EmotionLevel.normal;
      case 'low':
        return EmotionLevel.low;
      case 'medium':
        return EmotionLevel.medium;
      case 'high':
        return EmotionLevel.high;
      default:
        return EmotionLevel.normal;
    }
  }

  String get emotionLevelLabel {
    return switch (emotionLevel) {
      EmotionLevel.normal => '平稳',
      EmotionLevel.low => '轻度焦虑',
      EmotionLevel.medium => '中度焦虑',
      EmotionLevel.high => '重度焦虑',
    };
  }
}
