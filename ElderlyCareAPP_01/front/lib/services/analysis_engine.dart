import '../models/ai_analysis.dart';
import '../models/vitals_history.dart';

/// Client-side health analysis engine.
///
/// Takes raw health data and produces a rich [AiAnalysis] with per-metric
/// statistics, trend detection, risk assessment, and categorized suggestions.
///
/// All thresholds use geriatric-adjusted reference ranges.
class AnalysisEngine {
  // ──── Geriatric health thresholds ────

  // Temperature (°C)
  static const double _tempOptimal = 36.5;
  static const double _tempNormalLow = 36.0;
  static const double _tempNormalHigh = 37.2;
  static const double _tempAttentionLow = 35.5;
  static const double _tempAttentionHigh = 38.0;

  // Heart rate (bpm)
  static const int _hrOptimal = 75;
  static const int _hrNormalLow = 60;
  static const int _hrNormalHigh = 100;
  static const int _hrAttentionLow = 50;
  static const int _hrAttentionHigh = 110;

  // Systolic BP (mmHg)
  static const int _sysOptimal = 115;
  static const int _sysNormalLow = 90;
  static const int _sysNormalHigh = 130;
  static const int _sysAttentionHigh = 140;
  static const int _sysAttentionLow = 80;

  // Diastolic BP (mmHg)
  static const int _diaOptimal = 75;
  static const int _diaNormalLow = 60;
  static const int _diaNormalHigh = 85;
  static const int _diaAttentionHigh = 90;
  static const int _diaAttentionLow = 55;

  // Blood oxygen (%)
  static const int _spo2Optimal = 98;
  static const int _spo2NormalLow = 95;
  static const int _spo2AttentionLow = 90;

  // ──── Public API ────

  AiAnalysis analyze({
    required VitalsHistory history,
    double? temperature,
    int? heartRate,
    int? systolic,
    int? diastolic,
    int? bloodOxygen,
    List<dynamic>? alerts,
    Map<String, dynamic>? healthRecord,
    Map<String, dynamic>? profile,
    List<dynamic>? sosHistory,
    required String period,
  }) {
    final pts = history.points;

    // ── Extract data points ──
    final tempValues = <double>[];
    final hrValues = <int>[];
    final sysValues = <int>[];
    final diaValues = <int>[];
    final spo2Values = <int>[];

    for (final p in pts) {
      if (p.temperature != null) tempValues.add(p.temperature!);
      if (p.heartRate != null) hrValues.add(p.heartRate!);
      if (p.systolic != null) sysValues.add(p.systolic!);
      if (p.diastolic != null) diaValues.add(p.diastolic!);
      if (p.bloodOxygen != null) spo2Values.add(p.bloodOxygen!);
    }

    // ── Analyze each metric ──
    final tempAnalysis = _analyzeTemperature(tempValues, temperature);
    final hrAnalysis = _analyzeHeartRate(hrValues, heartRate);
    final bpAnalysis = _analyzeBloodPressure(sysValues, diaValues, systolic, diastolic);
    final spo2Analysis = _analyzeBloodOxygen(spo2Values, bloodOxygen);

    final metrics = [tempAnalysis, hrAnalysis, bpAnalysis, spo2Analysis];
    final metricsWithData = metrics.where((m) => m.hasData).toList();

    // ── Data completeness ──
    final completeness = _assessCompleteness(metrics);

    // ── Health score ──
    final score = _computeHealthScore(metricsWithData);
    final level = _scoreToLevel(score);

    // ── Trend summary ──
    final trendSummary = _computeTrendSummary(metricsWithData);

    // ── Risk factors ──
    final risks = _generateRisks(metrics, alerts, healthRecord);

    // ── Suggestions ──
    final suggestions = _generateSuggestions(metrics, risks, healthRecord, period,
        alertCount: alerts?.length ?? 0, sosCount: sosHistory?.length ?? 0);

    // ── Overall summary ──
    final summary = _overallSummary(level, metricsWithData, period);

    // ── Build result ──
    return AiAnalysis(
      meta: AiAnalysisMeta(
        analyzedAt: DateTime.now(),
        period: period,
        elderName: profile?['name'] as String?,
        elderAge: profile?['age'] as int?,
        dataCompleteness: completeness,
      ),
      overall: OverallStatus(level: level, score: score, summary: summary),
      metrics: metrics,
      riskFactors: risks,
      suggestions: suggestions,
      trendSummary: trendSummary,
      fromLlm: false,
    );
  }

  // ──── Per-metric analysis ────

  MetricAnalysis _analyzeTemperature(List<double> values, double? latest) {
    if (values.isEmpty) {
      return MetricAnalysis(
        type: VitalSignType.temperature,
        label: '体温',
        unit: '°C',
        status: MetricStatus.normal,
        assessment: '暂无体温数据',
        hasData: false,
      );
    }

    final avg = _avg(values);
    final min = _minD(values);
    final max = _maxD(values);
    final status = _classifyTemp(avg);
    final trend = _detectTrendD(values);

    String assessment;
    if (status == MetricStatus.normal) {
      assessment =
          '体温平均${avg.toStringAsFixed(1)}°C，在正常范围内（${_tempNormalLow.toStringAsFixed(1)}-${_tempNormalHigh.toStringAsFixed(1)}°C）。';
    } else if (status == MetricStatus.slightlyAbnormal) {
      assessment = avg > _tempNormalHigh
          ? '体温平均${avg.toStringAsFixed(1)}°C，略高于正常范围，建议持续关注。'
          : '体温平均${avg.toStringAsFixed(1)}°C，略低于正常范围，注意保暖。';
    } else {
      assessment = avg > _tempAttentionHigh
          ? '体温平均${avg.toStringAsFixed(1)}°C，明显偏高，可能提示感染或炎症，建议就医检查。'
          : '体温平均${avg.toStringAsFixed(1)}°C，明显偏低，需警惕低体温风险。';
    }

    return MetricAnalysis(
      type: VitalSignType.temperature,
      label: '体温',
      unit: '°C',
      average: avg,
      min: min,
      max: max,
      latest: latest,
      status: status,
      assessment: assessment,
      trend: trend,
      trendDescription: _trendDescription(trend, '体温'),
      hasData: true,
    );
  }

  MetricAnalysis _analyzeHeartRate(List<int> values, int? latest) {
    if (values.isEmpty) {
      return MetricAnalysis(
        type: VitalSignType.heartRate,
        label: '心率',
        unit: 'bpm',
        status: MetricStatus.normal,
        assessment: '暂无心率数据',
        hasData: false,
      );
    }

    final doubles = values.map((v) => v.toDouble()).toList();
    final avg = _avg(doubles);
    final min = values.reduce((a, b) => a < b ? a : b);
    final max = values.reduce((a, b) => a > b ? a : b);
    final status = _classifyHR(avg.round());
    final trend = _detectTrendD(doubles);

    String assessment;
    if (status == MetricStatus.normal) {
      assessment =
          '心率平均${avg.round()}bpm，在正常范围内（$_hrNormalLow-$_hrNormalHigh bpm）。';
    } else if (status == MetricStatus.slightlyAbnormal) {
      assessment = avg > _hrNormalHigh
          ? '心率平均${avg.round()}bpm，略高于正常范围，可能因活动或情绪引起。'
          : '心率平均${avg.round()}bpm，略低于正常范围。';
    } else {
      assessment = avg > _hrAttentionHigh
          ? '心率平均${avg.round()}bpm，心动过速，建议做心电图检查。'
          : '心率平均${avg.round()}bpm，心动过缓，建议就医评估。';
    }

    return MetricAnalysis(
      type: VitalSignType.heartRate,
      label: '心率',
      unit: 'bpm',
      average: avg,
      min: min.toDouble(),
      max: max.toDouble(),
      latest: latest?.toDouble(),
      status: status,
      assessment: assessment,
      trend: trend,
      trendDescription: _trendDescription(trend, '心率'),
      hasData: true,
    );
  }

  MetricAnalysis _analyzeBloodPressure(
    List<int> sysValues,
    List<int> diaValues,
    int? latestSys,
    int? latestDia,
  ) {
    if (sysValues.isEmpty && diaValues.isEmpty) {
      return MetricAnalysis(
        type: VitalSignType.bloodPressure,
        label: '血压',
        unit: 'mmHg',
        status: MetricStatus.normal,
        assessment: '暂无血压数据',
        hasData: false,
      );
    }

    final sysAvg = sysValues.isEmpty ? 0.0 : _avg(sysValues.map((v) => v.toDouble()).toList());
    final diaAvg = diaValues.isEmpty ? 0.0 : _avg(diaValues.map((v) => v.toDouble()).toList());
    final sysMin = sysValues.isEmpty ? null : sysValues.reduce((a, b) => a < b ? a : b);
    final sysMax = sysValues.isEmpty ? null : sysValues.reduce((a, b) => a > b ? a : b);
    final diaMin = diaValues.isEmpty ? null : diaValues.reduce((a, b) => a < b ? a : b);
    final diaMax = diaValues.isEmpty ? null : diaValues.reduce((a, b) => a > b ? a : b);

    final sysStatus = _classifySys(sysAvg.round());
    final diaStatus = _classifyDia(diaAvg.round());
    final status = sysStatus.index > diaStatus.index ? sysStatus : diaStatus;
    final trend = _detectTrendD(sysValues.map((v) => v.toDouble()).toList());

    String assessment;
    if (status == MetricStatus.normal) {
      assessment =
          '血压平均${sysAvg.round()}/${diaAvg.round()} mmHg，在正常范围内。';
    } else if (sysAvg.round() > _sysAttentionHigh || diaAvg.round() > _diaAttentionHigh) {
      assessment =
          '血压平均${sysAvg.round()}/${diaAvg.round()} mmHg，明显偏高，建议咨询医生调整用药方案。';
    } else if (sysAvg.round() < _sysAttentionLow || diaAvg.round() < _diaAttentionLow) {
      assessment =
          '血压平均${sysAvg.round()}/${diaAvg.round()} mmHg，偏低，需注意头晕跌倒风险。';
    } else {
      assessment =
          '血压平均${sysAvg.round()}/${diaAvg.round()} mmHg，略偏离正常范围，建议持续监测。';
    }

    return MetricAnalysis(
      type: VitalSignType.bloodPressure,
      label: '血压',
      unit: 'mmHg',
      average: sysAvg, // for display, BP uses the systolic average as primary
      min: sysMin?.toDouble(),
      max: sysMax?.toDouble(),
      latest: latestSys?.toDouble(),
      status: status,
      assessment: assessment,
      trend: trend,
      trendDescription: _trendDescription(trend, '血压'),
      hasData: true,
    );
  }

  MetricAnalysis _analyzeBloodOxygen(List<int> values, int? latest) {
    if (values.isEmpty) {
      return MetricAnalysis(
        type: VitalSignType.bloodOxygen,
        label: '血氧',
        unit: '%',
        status: MetricStatus.normal,
        assessment: '暂无血氧数据',
        hasData: false,
      );
    }

    final doubles = values.map((v) => v.toDouble()).toList();
    final avg = _avg(doubles);
    final min = values.reduce((a, b) => a < b ? a : b);
    final max = values.reduce((a, b) => a > b ? a : b);
    final status = _classifySpO2(avg.round());
    final trend = _detectTrendD(doubles);

    String assessment;
    if (status == MetricStatus.normal) {
      assessment = '血氧平均${avg.round()}%，在正常范围内（≥$_spo2NormalLow%）。';
    } else if (status == MetricStatus.slightlyAbnormal) {
      assessment =
          '血氧平均${avg.round()}%，略低于正常值，建议关注呼吸状况，适当进行深呼吸练习。';
    } else {
      assessment =
          '血氧平均${avg.round()}%，明显偏低（<$_spo2AttentionLow%），可能出现缺氧，建议立即吸氧并就医！';
    }

    return MetricAnalysis(
      type: VitalSignType.bloodOxygen,
      label: '血氧',
      unit: '%',
      average: avg,
      min: min.toDouble(),
      max: max.toDouble(),
      latest: latest?.toDouble(),
      status: status,
      assessment: assessment,
      trend: trend,
      trendDescription: _trendDescription(trend, '血氧'),
      hasData: true,
    );
  }

  // ──── Classifiers ────

  MetricStatus _classifyTemp(double value) {
    if (value >= _tempNormalLow && value <= _tempNormalHigh) return MetricStatus.normal;
    if ((value >= _tempAttentionLow && value < _tempNormalLow) ||
        (value > _tempNormalHigh && value <= _tempAttentionHigh)) {
      return MetricStatus.slightlyAbnormal;
    }
    return MetricStatus.abnormal;
  }

  MetricStatus _classifyHR(int value) {
    if (value >= _hrNormalLow && value <= _hrNormalHigh) return MetricStatus.normal;
    if ((value >= _hrAttentionLow && value < _hrNormalLow) ||
        (value > _hrNormalHigh && value <= _hrAttentionHigh)) {
      return MetricStatus.slightlyAbnormal;
    }
    return MetricStatus.abnormal;
  }

  MetricStatus _classifySys(int value) {
    if (value >= _sysNormalLow && value <= _sysNormalHigh) return MetricStatus.normal;
    if ((value >= _sysAttentionLow && value < _sysNormalLow) ||
        (value > _sysNormalHigh && value <= _sysAttentionHigh)) {
      return MetricStatus.slightlyAbnormal;
    }
    return MetricStatus.abnormal;
  }

  MetricStatus _classifyDia(int value) {
    if (value >= _diaNormalLow && value <= _diaNormalHigh) return MetricStatus.normal;
    if ((value >= _diaAttentionLow && value < _diaNormalLow) ||
        (value > _diaNormalHigh && value <= _diaAttentionHigh)) {
      return MetricStatus.slightlyAbnormal;
    }
    return MetricStatus.abnormal;
  }

  MetricStatus _classifySpO2(int value) {
    if (value >= _spo2NormalLow) return MetricStatus.normal;
    if (value >= _spo2AttentionLow) return MetricStatus.slightlyAbnormal;
    return MetricStatus.abnormal;
  }

  // ──── Trend detection ────

  TrendDirection _detectTrendD(List<double> values) {
    if (values.length < 3) return TrendDirection.stable;
    final n = values.length;
    final earlyCount = (n * 0.3).ceil().clamp(1, n);
    final lateCount = (n * 0.3).ceil().clamp(1, n);

    final earlyAvg = _avg(values.sublist(0, earlyCount));
    final lateAvg = _avg(values.sublist(n - lateCount));
    final overallAvg = _avg(values);

    final change = lateAvg - earlyAvg;
    final pct = overallAvg > 0 ? (change / overallAvg).abs() : 0;

    // Check variance
    double variance = 0;
    for (final v in values) {
      variance += (v - overallAvg) * (v - overallAvg);
    }
    variance /= values.length;
    final cv = overallAvg > 0 ? (variance > 0 ? _sqrt(variance) / overallAvg : 0) : 0;

    if (pct < 0.03 && cv < 0.05) return TrendDirection.stable;
    if (cv > 0.10) return TrendDirection.fluctuating;
    if (change > 0 && pct > 0.03) return TrendDirection.rising;
    if (change < 0 && pct > 0.03) return TrendDirection.falling;
    return TrendDirection.stable;
  }

  String? _trendDescription(TrendDirection? trend, String metricName) {
    if (trend == null) return null;
    return switch (trend) {
      TrendDirection.rising => '$metricName呈上升趋势',
      TrendDirection.falling => '$metricName呈下降趋势',
      TrendDirection.stable => '$metricName保持稳定',
      TrendDirection.fluctuating => '$metricName波动较大',
    };
  }

  // ──── Health score (0–100) ────

  int _computeHealthScore(List<MetricAnalysis> metrics) {
    if (metrics.isEmpty) return 0;
    final weights = {
      VitalSignType.bloodPressure: 0.30,
      VitalSignType.heartRate: 0.25,
      VitalSignType.temperature: 0.20,
      VitalSignType.bloodOxygen: 0.25,
    };

    double totalScore = 0;
    double totalWeight = 0;

    for (final m in metrics) {
      final w = weights[m.type] ?? 0.25;
      totalWeight += w;
      totalScore += _metricScore(m) * w;
    }

    return totalWeight > 0 ? totalScore.round().clamp(0, 100) : 50;
  }

  double _metricScore(MetricAnalysis m) {
    if (!m.hasData || m.average == null) return 50; // neutral

    switch (m.type) {
      case VitalSignType.temperature:
        return _valueScore(m.average!, _tempOptimal, _tempNormalLow, _tempNormalHigh,
            _tempAttentionLow, _tempAttentionHigh);
      case VitalSignType.heartRate:
        return _valueScore(m.average!, _hrOptimal.toDouble(), _hrNormalLow.toDouble(),
            _hrNormalHigh.toDouble(), _hrAttentionLow.toDouble(), _hrAttentionHigh.toDouble());
      case VitalSignType.bloodPressure:
        // Average of systolic and diastolic scores
        if (m.average != null) {
          final sysScore = _valueScore(m.average!, _sysOptimal.toDouble(),
              _sysNormalLow.toDouble(), _sysNormalHigh.toDouble(),
              _sysAttentionLow.toDouble(), _sysAttentionHigh.toDouble());
          return sysScore;
        }
        return 50;
      case VitalSignType.bloodOxygen:
        // SpO2: higher is better up to 100
        if (m.average! >= _spo2Optimal) return 100;
        if (m.average! >= _spo2NormalLow) return 85;
        if (m.average! >= _spo2AttentionLow) return 60;
        return 30;
    }
  }

  double _valueScore(double value, double optimal, double normalLow, double normalHigh,
      double attentionLow, double attentionHigh) {
    if (value >= normalLow && value <= normalHigh) {
      // Within normal range: distance from optimal determines score (80-100)
      final dist = (value - optimal).abs();
      final maxDist = (normalHigh - normalLow) / 2;
      return maxDist > 0 ? 100 - (dist / maxDist * 20) : 90;
    } else if (value >= attentionLow && value <= attentionHigh) {
      // Attention range: 50-80
      final distFromNormal = value > normalHigh ? value - normalHigh : normalLow - value;
      final attnRange = value > normalHigh ? attentionHigh - normalHigh : normalLow - attentionLow;
      return attnRange > 0 ? 80 - (distFromNormal / attnRange * 30) : 65;
    } else {
      // Abnormal range: 0-50
      final distFromAttn = value > attentionHigh ? value - attentionHigh : attentionLow - value;
      final penalty = (distFromAttn / (attentionHigh - attentionLow).abs() * 50).clamp(0, 50);
      return (50 - penalty).clamp(10, 50).toDouble();
    }
  }

  HealthLevel _scoreToLevel(int score) {
    if (score >= 80) return HealthLevel.healthy;
    if (score >= 60) return HealthLevel.attention;
    if (score >= 40) return HealthLevel.warning;
    return HealthLevel.critical;
  }

  // ──── Risk factors ────

  List<RiskFactor> _generateRisks(
    List<MetricAnalysis> metrics,
    List<dynamic>? alerts,
    Map<String, dynamic>? healthRecord,
  ) {
    final risks = <RiskFactor>[];

    for (final m in metrics) {
      if (!m.hasData) continue;

      if (m.status == MetricStatus.abnormal) {
        final riskLabel = switch (m.type) {
          VitalSignType.temperature => '体温异常',
          VitalSignType.heartRate => '心率异常',
          VitalSignType.bloodPressure => '血压异常',
          VitalSignType.bloodOxygen => '血氧异常',
        };
        risks.add(RiskFactor(
          title: riskLabel,
          description: m.assessment,
          level: m.type == VitalSignType.bloodOxygen ? RiskLevel.high : RiskLevel.medium,
          relatedMetric: m.type,
        ));
      } else if (m.status == MetricStatus.slightlyAbnormal) {
        final riskLabel = switch (m.type) {
          VitalSignType.temperature => '体温略偏离正常',
          VitalSignType.heartRate => '心率需关注',
          VitalSignType.bloodPressure => '血压需关注',
          VitalSignType.bloodOxygen => '血氧略低于正常',
        };
        risks.add(RiskFactor(
          title: riskLabel,
          description: m.assessment,
          level: RiskLevel.low,
          relatedMetric: m.type,
        ));
      }

      // Check rising trends for blood pressure and heart rate
      if (m.trend == TrendDirection.rising &&
          (m.type == VitalSignType.bloodPressure || m.type == VitalSignType.heartRate)) {
        final exists = risks.any((r) => r.relatedMetric == m.type && r.level == RiskLevel.medium);
        if (!exists) {
          risks.add(RiskFactor(
            title: '${m.label}持续上升',
            description: '${m.trendDescription}，建议增加监测频率。',
            level: RiskLevel.low,
            relatedMetric: m.type,
          ));
        }
      }
    }

    // Medical history risks
    if (healthRecord != null) {
      final history = (healthRecord['medicalHistory'] as String? ?? '').toLowerCase();
      if (history.contains('高血压') || history.contains('血压')) {
        if (!risks.any((r) => r.title.contains('血压'))) {
          risks.add(RiskFactor(
            title: '高血压病史',
            description: '老人有高血压病史，需持续关注血压变化，规律服药。',
            level: RiskLevel.medium,
            relatedMetric: VitalSignType.bloodPressure,
          ));
        }
      }
      if (history.contains('糖尿病') || history.contains('血糖')) {
        risks.add(RiskFactor(
          title: '糖尿病史',
          description: '老人有糖尿病史，建议注意饮食控制和血糖监测。',
          level: RiskLevel.low,
        ));
      }
      if (history.contains('心脏病') || history.contains('冠心病')) {
        risks.add(RiskFactor(
          title: '心脏病史',
          description: '老人有心脏病史，需密切关注心率和血压变化。',
          level: RiskLevel.medium,
          relatedMetric: VitalSignType.heartRate,
        ));
      }
    }

    // Alert-based risks
    if (alerts != null && alerts.isNotEmpty) {
      final alertCount = alerts.length;
      if (alertCount >= 5) {
        risks.add(RiskFactor(
          title: '告警频繁',
          description: '近期共有$alertCount条健康告警记录，建议进行全面体检。',
          level: RiskLevel.medium,
        ));
      } else if (alertCount >= 2) {
        risks.add(RiskFactor(
          title: '存在告警记录',
          description: '近期有$alertCount条健康告警，需关注老人健康状况。',
          level: RiskLevel.low,
        ));
      }
    }

    // If no abnormal metrics found
    if (risks.isEmpty) {
      // All clear, no risks
    }

    return risks;
  }

  // ──── Suggestions ────

  List<Suggestion> _generateSuggestions(
    List<MetricAnalysis> metrics,
    List<RiskFactor> risks,
    Map<String, dynamic>? healthRecord,
    String period, {
    int alertCount = 0,
    int sosCount = 0,
  }) {
    final suggestions = <Suggestion>[];

    // Check for critical issues first
    for (final m in metrics) {
      if (!m.hasData) continue;
      if (m.status == MetricStatus.abnormal) {
        switch (m.type) {
          case VitalSignType.bloodOxygen:
            suggestions.add(Suggestion(
              category: SuggestionCategory.emergency,
              content: '血氧饱和度偏低，建议立即吸氧并就医检查呼吸功能。',
              priority: 5,
            ));
            break;
          case VitalSignType.bloodPressure:
            if ((m.average ?? 0) > _sysAttentionHigh) {
              suggestions.add(Suggestion(
                category: SuggestionCategory.medication,
                content: '血压持续偏高，请按时服用降压药，切勿自行停药，建议就医调整用药方案。',
                priority: 5,
              ));
            }
            break;
          case VitalSignType.heartRate:
            suggestions.add(Suggestion(
              category: SuggestionCategory.checkup,
              content: '心率异常，建议做心电图检查，排除心律不齐等问题。',
              priority: 4,
            ));
            break;
          case VitalSignType.temperature:
            suggestions.add(Suggestion(
              category: SuggestionCategory.checkup,
              content: '体温异常，建议尽快就医检查，排查感染或其他病因。',
              priority: 4,
            ));
            break;
        }
      }
    }

    // General suggestions based on metrics
    for (final m in metrics) {
      if (!m.hasData) continue;
      if (m.trend == TrendDirection.rising) {
        suggestions.add(Suggestion(
          category: SuggestionCategory.checkup,
          content: '${m.label}呈上升趋势，建议增加${_periodUnit(period)}监测频率。',
          priority: 3,
        ));
      } else if (m.trend == TrendDirection.fluctuating) {
        suggestions.add(Suggestion(
          category: SuggestionCategory.checkup,
          content: '${m.label}波动较大，建议记录${_periodUnit(period)}测量数据以观察规律。',
          priority: 2,
        ));
      } else if (m.status == MetricStatus.slightlyAbnormal) {
        suggestions.add(Suggestion(
          category: SuggestionCategory.lifestyle,
          content: '${m.label}略偏离正常范围，建议保持规律作息，${_periodUnit(period)}复查。',
          priority: 2,
        ));
      }
    }

    // Medical history suggestions
    if (healthRecord != null) {
      final history = (healthRecord['medicalHistory'] as String? ?? '');
      if (history.contains('高血压')) {
        suggestions.add(Suggestion(
          category: SuggestionCategory.medication,
          content: '有高血压病史，请按时服用降压药物，每日定时测量血压。',
          priority: 3,
        ));
      }
      if (history.contains('糖尿病')) {
        suggestions.add(Suggestion(
          category: SuggestionCategory.diet,
          content: '有糖尿病史，建议控制碳水化合物摄入，定期监测血糖。',
          priority: 3,
        ));
      }
      if (history.contains('心脏')) {
        suggestions.add(Suggestion(
          category: SuggestionCategory.exercise,
          content: '有心脏病史，建议进行适度有氧运动（如散步），避免剧烈运动。',
          priority: 2,
        ));
      }
    }

    // Alert/SOS related suggestions
    if (alertCount >= 3) {
      suggestions.add(Suggestion(
        category: SuggestionCategory.checkup,
        content: '近期健康告警较多（$alertCount次），建议安排一次全面体检。',
        priority: 3,
      ));
    }
    if (sosCount > 0) {
      suggestions.add(Suggestion(
        category: SuggestionCategory.checkup,
        content: '近期有SOS紧急求助记录，建议评估老人当前身体状况和居家安全。',
        priority: 4,
      ));
    }

    // Lifestyle suggestions (always add if not many high-priority ones)
    final highPriorityCount = suggestions.where((s) => s.priority >= 4).length;
    if (highPriorityCount == 0) {
      suggestions.add(Suggestion(
        category: SuggestionCategory.lifestyle,
        content: '各项指标整体平稳，建议保持规律作息，每日适度活动30分钟。',
        priority: 1,
      ));
      suggestions.add(Suggestion(
        category: SuggestionCategory.diet,
        content: '建议饮食清淡，多摄入蔬菜水果，控制盐分和油脂摄入。',
        priority: 1,
      ));
    }

    // If period is month, add a regular checkup suggestion
    if (period == 'month') {
      suggestions.add(Suggestion(
        category: SuggestionCategory.checkup,
        content: '建议每季度安排一次常规体检，重点关注血压、心率和血氧指标。',
        priority: 2,
      ));
    }

    // Deduplicate
    final seen = <String>{};
    final unique = <Suggestion>[];
    for (final s in suggestions) {
      if (seen.add(s.content)) unique.add(s);
    }

    // Sort by priority descending
    unique.sort((a, b) => b.priority.compareTo(a.priority));

    // Limit to 8 suggestions
    return unique.take(8).toList();
  }

  // ──── Overall summary ────

  String _overallSummary(
      HealthLevel level, List<MetricAnalysis> metricsWithData, String period) {
    final periodName = switch (period) {
      'day' => '今日',
      'month' => '本月',
      _ => '本周',
    };

    if (metricsWithData.isEmpty) {
      return '$periodName暂无体征数据，无法进行健康分析。请确保设备正常运行。';
    }

    final normalCount = metricsWithData.where((m) => m.status == MetricStatus.normal).length;
    final total = metricsWithData.length;

    // Build a data-rich summary
    final parts = <String>[];
    for (final m in metricsWithData) {
      if (m.type == VitalSignType.bloodPressure) {
        if (m.average != null && m.average! > 0) {
          parts.add('血压均值${m.average!.round()} mmHg');
        }
      } else {
        if (m.average != null && m.average! > 0) {
          final valStr = m.type == VitalSignType.temperature
              ? m.average!.toStringAsFixed(1)
              : m.average!.round().toString();
          parts.add('${m.label}均值$valStr${m.unit}');
        }
      }
    }

    final dataSummary = parts.isNotEmpty ? '（${parts.join('，')}）' : '';

    return switch (level) {
      HealthLevel.healthy =>
        '$periodName老人健康状况良好，$normalCount/$total项指标正常$dataSummary。',
      HealthLevel.attention =>
        '$periodName老人部分健康指标需关注，$normalCount/$total项指标正常$dataSummary。',
      HealthLevel.warning =>
        '$periodName老人健康指标出现异常，请尽快关注，$normalCount/$total项指标正常$dataSummary。',
      HealthLevel.critical =>
        '$periodName老人健康出现危险信号！仅$normalCount/$total项指标正常$dataSummary，请立即采取措施！',
    };
  }

  // ──── Trend summary ────

  TrendSummary _computeTrendSummary(List<MetricAnalysis> metricsWithData) {
    if (metricsWithData.isEmpty) {
      return TrendSummary(
        direction: TrendDirection.stable,
        description: '暂无足够数据进行趋势分析',
      );
    }

    final trends = metricsWithData
        .where((m) => m.trend != null)
        .map((m) => m.trend!)
        .toList();

    if (trends.isEmpty) {
      return TrendSummary(direction: TrendDirection.stable, description: '各项指标数据不足，无法判断趋势');
    }

    final stableCount = trends.where((t) => t == TrendDirection.stable).length;
    final risingCount = trends.where((t) => t == TrendDirection.rising).length;
    final fallingCount = trends.where((t) => t == TrendDirection.falling).length;
    final fluctuatingCount = trends.where((t) => t == TrendDirection.fluctuating).length;

    if (stableCount >= metricsWithData.length / 2 && risingCount == 0 && fallingCount == 0) {
      return TrendSummary(direction: TrendDirection.stable, description: '各项指标整体平稳，未发现明显异常趋势');
    } else if (risingCount > fallingCount && risingCount > 0) {
      return TrendSummary(
          direction: TrendDirection.rising,
          description: '部分指标呈上升趋势（$risingCount/${trends.length}项），建议关注变化');
    } else if (fallingCount > risingCount && fallingCount > 0) {
      return TrendSummary(
          direction: TrendDirection.falling,
          description: '部分指标呈下降趋势（$fallingCount/${trends.length}项），建议关注变化');
    } else if (fluctuatingCount > 0) {
      return TrendSummary(
          direction: TrendDirection.fluctuating,
          description: '${fluctuatingCount}项指标存在波动，建议增加监测频率');
    }

    return TrendSummary(direction: TrendDirection.stable, description: '整体趋势平稳');
  }

  // ──── Helpers ────

  DataCompleteness _assessCompleteness(List<MetricAnalysis> metrics) {
    final count = metrics.where((m) => m.hasData).length;
    if (count >= 4) return DataCompleteness.full;
    if (count >= 2) return DataCompleteness.partial;
    return DataCompleteness.minimal;
  }

  String _periodUnit(String period) {
    return switch (period) {
      'day' => '每日',
      'month' => '每月',
      _ => '每周',
    };
  }

  double _avg(List<double> values) {
    if (values.isEmpty) return 0;
    return values.reduce((a, b) => a + b) / values.length;
  }

  double _minD(List<double> values) {
    if (values.isEmpty) return 0;
    return values.reduce((a, b) => a < b ? a : b);
  }

  double _maxD(List<double> values) {
    if (values.isEmpty) return 0;
    return values.reduce((a, b) => a > b ? a : b);
  }

  double _sqrt(double x) {
    if (x <= 0) return 0;
    var guess = x / 2;
    for (var i = 0; i < 10; i++) {
      guess = (guess + x / guess) / 2;
    }
    return guess;
  }
}
