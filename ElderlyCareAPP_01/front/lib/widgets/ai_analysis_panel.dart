import 'package:flutter/material.dart';
import '../models/ai_analysis.dart';
import 'health_score_indicator.dart';

/// The main AI-powered health analysis panel.
///
/// Displays health score, per-metric analysis cards, risk factors, and
/// categorized suggestions in a professional medical-style layout.
class AiAnalysisPanel extends StatelessWidget {
  const AiAnalysisPanel({
    super.key,
    required this.analysis,
    this.onRefresh,
    this.loading = false,
  });

  /// The analysis data to display. Null triggers empty/error state.
  final AiAnalysis? analysis;

  /// Called when user taps the refresh button.
  final VoidCallback? onRefresh;

  /// Whether data is currently loading.
  final bool loading;

  /// Factory for loading shimmer placeholder.
  const AiAnalysisPanel.loading({
    super.key,
    this.onRefresh,
  })  : analysis = null,
        loading = true;

  /// Factory for empty/no-data state.
  const AiAnalysisPanel.empty({
    super.key,
    this.onRefresh,
  })  : analysis = null,
        loading = false;

  // ──── Color palette ────

  static const Color _tempColor = Color(0xFFFF9500);
  static const Color _hrColor = Color(0xFFFF3B30);
  static const Color _bpColor = Color(0xFF5856D6);
  static const Color _spo2Color = Color(0xFF34C759);
  static const Color _healthyColor = Color(0xFF34C759);
  static const Color _attentionColor = Color(0xFFFF9500);
  static const Color _warningColor = Color(0xFFFF3B30);

  Color _metricColor(VitalSignType type) {
    return switch (type) {
      VitalSignType.temperature => _tempColor,
      VitalSignType.heartRate => _hrColor,
      VitalSignType.bloodPressure => _bpColor,
      VitalSignType.bloodOxygen => _spo2Color,
    };
  }

  IconData _metricIcon(VitalSignType type) {
    return switch (type) {
      VitalSignType.temperature => Icons.thermostat_outlined,
      VitalSignType.heartRate => Icons.favorite_border,
      VitalSignType.bloodPressure => Icons.monitor_heart_outlined,
      VitalSignType.bloodOxygen => Icons.air_outlined,
    };
  }

  // ──── Build ────

  @override
  Widget build(BuildContext context) {
    if (loading) return _LoadingPlaceholder();
    if (analysis == null) return _EmptyPlaceholder(onRefresh: onRefresh);

    final a = analysis!;
    final metricsWithData = a.metricsWithData;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // ── Section header ──
        _SectionHeader(fromLlm: a.fromLlm),
        const SizedBox(height: 12),

        // ── Overall status card ──
        _OverallStatusCard(
          score: a.overall.score,
          level: a.overall.level,
          levelLabel: a.healthLevelLabel,
          summary: a.overall.summary,
          period: a.periodLabel,
          trendDescription: a.trendSummary.description,
        ),
        const SizedBox(height: 12),

        // ── Data deficiency warning ──
        if (a.meta.dataCompleteness == DataCompleteness.minimal)
          _DataWarningBanner(completeness: a.meta.dataCompleteness),

        // ── Metric analysis cards ──
        ...metricsWithData.map(
          (m) => Padding(
            padding: const EdgeInsets.only(bottom: 10),
            child: _MetricCard(
              metric: m,
              color: _metricColor(m.type),
              icon: _metricIcon(m.type),
            ),
          ),
        ),

        // Show "no data" for metrics without data
        if (a.metrics.any((m) => !m.hasData)) ...[
          const SizedBox(height: 4),
          _NoDataHint(
            missingMetrics: a.metrics.where((m) => !m.hasData).map((m) => m.label).toList(),
          ),
        ],

        // ── Risk factors section ──
        if (a.riskFactors.isNotEmpty) ...[
          const SizedBox(height: 16),
          _RiskSection(risks: a.riskFactors),
        ],

        // ── Suggestions section ──
        if (a.suggestions.isNotEmpty) ...[
          const SizedBox(height: 16),
          _SuggestionsSection(suggestions: a.suggestions),
        ],

        // ── Footer ──
        const SizedBox(height: 12),
        _DataFooter(
          fromLlm: a.fromLlm,
          metricsCount: a.metricsWithDataCount,
          totalMetrics: a.metrics.length,
          riskCount: a.riskFactors.length,
        ),

        // ── Refresh button ──
        if (onRefresh != null) ...[
          const SizedBox(height: 8),
          Align(
            alignment: Alignment.centerRight,
            child: FilledButton.tonalIcon(
              onPressed: onRefresh,
              icon: const Icon(Icons.refresh, size: 18),
              label: const Text('刷新分析'),
            ),
          ),
        ],
      ],
    );
  }
}

// ──── Sub-widgets ────

class _SectionHeader extends StatelessWidget {
  const _SectionHeader({required this.fromLlm});
  final bool fromLlm;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Container(
          padding: const EdgeInsets.all(6),
          decoration: BoxDecoration(
            color: fromLlm
                ? const Color(0xFF4A90E2).withValues(alpha: 0.12)
                : const Color(0xFF2C7DA0).withValues(alpha: 0.12),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Icon(
            fromLlm ? Icons.psychology_outlined : Icons.insights_outlined,
            color: fromLlm ? const Color(0xFF4A90E2) : const Color(0xFF2C7DA0),
            size: 20,
          ),
        ),
        const SizedBox(width: 10),
        const Expanded(
          child: Text(
            'AI 智能分析',
            style: TextStyle(
              fontWeight: FontWeight.w600,
              fontSize: 18,
              color: Color(0xFF1D1D1F),
            ),
          ),
        ),
        if (fromLlm)
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFF4A90E2), Color(0xFF2C7DA0)],
              ),
              borderRadius: BorderRadius.circular(10),
            ),
            child: const Text(
              '大模型',
              style: TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.w600),
            ),
          ),
      ],
    );
  }
}

class _OverallStatusCard extends StatelessWidget {
  const _OverallStatusCard({
    required this.score,
    required this.level,
    required this.levelLabel,
    required this.summary,
    required this.period,
    required this.trendDescription,
  });

  final int score;
  final HealthLevel level;
  final String levelLabel;
  final String summary;
  final String period;
  final String trendDescription;

  Color get _levelColor {
    return switch (level) {
      HealthLevel.healthy => AiAnalysisPanel._healthyColor,
      HealthLevel.attention => AiAnalysisPanel._attentionColor,
      HealthLevel.warning => AiAnalysisPanel._warningColor,
      HealthLevel.critical => AiAnalysisPanel._warningColor,
    };
  }

  IconData get _levelIcon {
    return switch (level) {
      HealthLevel.healthy => Icons.check_circle_outline,
      HealthLevel.attention => Icons.info_outline,
      HealthLevel.warning => Icons.warning_amber_rounded,
      HealthLevel.critical => Icons.dangerous_outlined,
    };
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: BorderSide(color: _levelColor.withValues(alpha: 0.3), width: 1),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            // Score gauge
            HealthScoreIndicator(score: score, level: level, size: 100),
            const SizedBox(width: 20),
            // Info
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                        decoration: BoxDecoration(
                          color: _levelColor.withValues(alpha: 0.12),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Icon(_levelIcon, size: 16, color: _levelColor),
                            const SizedBox(width: 4),
                            Text(
                              levelLabel,
                              style: TextStyle(
                                color: _levelColor,
                                fontWeight: FontWeight.w700,
                                fontSize: 14,
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(width: 8),
                      Text(
                        period,
                        style: TextStyle(
                          color: Colors.grey.shade500,
                          fontSize: 12,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 10),
                  Text(
                    summary,
                    style: const TextStyle(
                      fontSize: 13,
                      height: 1.4,
                      color: Color(0xFF3A3A3C),
                    ),
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      Icon(Icons.trending_up, size: 14, color: Colors.grey.shade500),
                      const SizedBox(width: 4),
                      Expanded(
                        child: Text(
                          trendDescription,
                          style: TextStyle(
                            fontSize: 12,
                            color: Colors.grey.shade600,
                          ),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _MetricCard extends StatelessWidget {
  const _MetricCard({
    required this.metric,
    required this.color,
    required this.icon,
  });

  final MetricAnalysis metric;
  final Color color;
  final IconData icon;

  Color get _statusColor {
    return switch (metric.status) {
      MetricStatus.normal => AiAnalysisPanel._healthyColor,
      MetricStatus.slightlyAbnormal => AiAnalysisPanel._attentionColor,
      MetricStatus.abnormal => AiAnalysisPanel._warningColor,
    };
  }

  String get _statusLabel {
    return switch (metric.status) {
      MetricStatus.normal => '正常',
      MetricStatus.slightlyAbnormal => '略异常',
      MetricStatus.abnormal => '异常',
    };
  }

  IconData get _trendIcon {
    return switch (metric.trend) {
      TrendDirection.rising => Icons.trending_up,
      TrendDirection.falling => Icons.trending_down,
      TrendDirection.stable => Icons.trending_flat,
      TrendDirection.fluctuating => Icons.swap_vert,
      null => Icons.remove,
    };
  }

  Color get _trendColor {
    return switch (metric.trend) {
      TrendDirection.rising => AiAnalysisPanel._warningColor,
      TrendDirection.falling => const Color(0xFF4A90E2),
      TrendDirection.stable => AiAnalysisPanel._healthyColor,
      TrendDirection.fluctuating => AiAnalysisPanel._attentionColor,
      null => Colors.grey,
    };
  }

  String _fmt(double? v) {
    if (v == null) return '--';
    if (metric.type == VitalSignType.temperature) return v.toStringAsFixed(1);
    return v.round().toString();
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(14),
        side: BorderSide(color: color.withValues(alpha: 0.25), width: 1),
      ),
      child: Padding(
        padding: const EdgeInsets.all(14),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header
            Row(
              children: [
                Container(
                  width: 32,
                  height: 32,
                  decoration: BoxDecoration(
                    color: color.withValues(alpha: 0.12),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Icon(icon, size: 18, color: color),
                ),
                const SizedBox(width: 10),
                Text(
                  metric.label,
                  style: const TextStyle(
                    fontWeight: FontWeight.w600,
                    fontSize: 15,
                    color: Color(0xFF1D1D1F),
                  ),
                ),
                const Spacer(),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                  decoration: BoxDecoration(
                    color: _statusColor.withValues(alpha: 0.12),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Text(
                    _statusLabel,
                    style: TextStyle(
                      color: _statusColor,
                      fontSize: 11,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),

            // Stats row
            Row(
              children: [
                _StatChip(label: '平均值', value: _fmt(metric.average), unit: metric.unit),
                _StatDivider(),
                _StatChip(label: '最低', value: _fmt(metric.min), unit: metric.unit),
                _StatDivider(),
                _StatChip(label: '最高', value: _fmt(metric.max), unit: metric.unit),
                _StatDivider(),
                _StatChip(label: '最新', value: _fmt(metric.latest), unit: metric.unit),
              ],
            ),
            const SizedBox(height: 10),

            // Trend row
            if (metric.trend != null)
              Padding(
                padding: const EdgeInsets.only(bottom: 6),
                child: Row(
                  children: [
                    Icon(_trendIcon, size: 15, color: _trendColor),
                    const SizedBox(width: 4),
                    Text(
                      metric.trendDescription ?? '',
                      style: TextStyle(
                        fontSize: 12,
                        color: _trendColor,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ],
                ),
              ),

            // Assessment
            Text(
              metric.assessment,
              style: const TextStyle(
                fontSize: 12.5,
                height: 1.4,
                color: Color(0xFF5A5A5C),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _StatChip extends StatelessWidget {
  const _StatChip({required this.label, required this.value, required this.unit});
  final String label;
  final String value;
  final String unit;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Column(
        children: [
          Text(
            label,
            style: TextStyle(fontSize: 10, color: Colors.grey.shade500),
          ),
          const SizedBox(height: 2),
          Text(
            value,
            style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w700, color: Color(0xFF1D1D1F)),
          ),
          Text(
            unit,
            style: TextStyle(fontSize: 9, color: Colors.grey.shade500),
          ),
        ],
      ),
    );
  }
}

class _StatDivider extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      width: 1,
      height: 34,
      color: Colors.grey.shade200,
    );
  }
}

class _RiskSection extends StatelessWidget {
  const _RiskSection({required this.risks});
  final List<RiskFactor> risks;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            const Icon(Icons.warning_amber_rounded, size: 18, color: Color(0xFFFF9500)),
            const SizedBox(width: 6),
            Text(
              '风险提示',
              style: TextStyle(
                fontWeight: FontWeight.w600,
                fontSize: 15,
                color: Colors.grey.shade800,
              ),
            ),
            const SizedBox(width: 8),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
              decoration: BoxDecoration(
                color: const Color(0xFFFF9500).withValues(alpha: 0.12),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(
                '${risks.length}',
                style: const TextStyle(
                  fontSize: 11,
                  fontWeight: FontWeight.w700,
                  color: Color(0xFFFF9500),
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 8),
        ...risks.map((risk) => _RiskTile(risk: risk)),
      ],
    );
  }
}

class _RiskTile extends StatelessWidget {
  const _RiskTile({required this.risk});
  final RiskFactor risk;

  Color get _levelColor {
    return switch (risk.level) {
      RiskLevel.low => const Color(0xFFFF9500),
      RiskLevel.medium => const Color(0xFFFF9500),
      RiskLevel.high => const Color(0xFFFF3B30),
    };
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 0,
      margin: const EdgeInsets.only(bottom: 6),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10),
        side: BorderSide(color: _levelColor.withValues(alpha: 0.2)),
      ),
      child: Padding(
        padding: const EdgeInsets.all(10),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              width: 8,
              height: 8,
              margin: const EdgeInsets.only(top: 5),
              decoration: BoxDecoration(color: _levelColor, shape: BoxShape.circle),
            ),
            const SizedBox(width: 10),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    risk.title,
                    style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 13, color: Color(0xFF1D1D1F)),
                  ),
                  const SizedBox(height: 2),
                  Text(
                    risk.description,
                    style: const TextStyle(fontSize: 12, height: 1.35, color: Color(0xFF5A5A5C)),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _SuggestionsSection extends StatelessWidget {
  const _SuggestionsSection({required this.suggestions});
  final List<Suggestion> suggestions;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            const Icon(Icons.lightbulb_outline, size: 18, color: Color(0xFF4A90E2)),
            const SizedBox(width: 6),
            Text(
              '健康建议',
              style: TextStyle(
                fontWeight: FontWeight.w600,
                fontSize: 15,
                color: Colors.grey.shade800,
              ),
            ),
          ],
        ),
        const SizedBox(height: 8),
        ...suggestions.map((s) => _SuggestionTile(suggestion: s)),
      ],
    );
  }
}

class _SuggestionTile extends StatelessWidget {
  const _SuggestionTile({required this.suggestion});
  final Suggestion suggestion;

  IconData get _categoryIcon {
    return switch (suggestion.category) {
      SuggestionCategory.diet => Icons.restaurant_outlined,
      SuggestionCategory.exercise => Icons.directions_walk,
      SuggestionCategory.medication => Icons.medication_outlined,
      SuggestionCategory.checkup => Icons.local_hospital_outlined,
      SuggestionCategory.lifestyle => Icons.self_improvement,
      SuggestionCategory.emergency => Icons.emergency_outlined,
    };
  }

  Color get _categoryColor {
    return switch (suggestion.category) {
      SuggestionCategory.diet => const Color(0xFF34C759),
      SuggestionCategory.exercise => const Color(0xFF4A90E2),
      SuggestionCategory.medication => const Color(0xFF5856D6),
      SuggestionCategory.checkup => const Color(0xFFFF9500),
      SuggestionCategory.lifestyle => const Color(0xFF34C759),
      SuggestionCategory.emergency => const Color(0xFFFF3B30),
    };
  }

  Color get _priorityColor {
    if (suggestion.priority >= 4) return const Color(0xFFFF3B30);
    if (suggestion.priority >= 3) return const Color(0xFFFF9500);
    return const Color(0xFF34C759);
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 0,
      margin: const EdgeInsets.only(bottom: 6),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10),
        side: BorderSide(color: _categoryColor.withValues(alpha: 0.15)),
      ),
      child: Padding(
        padding: const EdgeInsets.all(10),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              width: 28,
              height: 28,
              decoration: BoxDecoration(
                color: _categoryColor.withValues(alpha: 0.10),
                borderRadius: BorderRadius.circular(7),
              ),
              child: Icon(_categoryIcon, size: 16, color: _categoryColor),
            ),
            const SizedBox(width: 10),
            Expanded(
              child: Text(
                suggestion.content,
                style: const TextStyle(fontSize: 12.5, height: 1.35, color: Color(0xFF3A3A3C)),
              ),
            ),
            const SizedBox(width: 8),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
              decoration: BoxDecoration(
                color: _priorityColor.withValues(alpha: 0.10),
                borderRadius: BorderRadius.circular(6),
              ),
              child: Text(
                'P${suggestion.priority}',
                style: TextStyle(
                  fontSize: 10,
                  fontWeight: FontWeight.w700,
                  color: _priorityColor,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _DataFooter extends StatelessWidget {
  const _DataFooter({
    required this.fromLlm,
    required this.metricsCount,
    required this.totalMetrics,
    required this.riskCount,
  });

  final bool fromLlm;
  final int metricsCount;
  final int totalMetrics;
  final int riskCount;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Icon(Icons.analytics_outlined, size: 14, color: Colors.grey.shade400),
        const SizedBox(width: 4),
        Text(
          '基于$metricsCount/$totalMetrics项体征数据分析',
          style: TextStyle(fontSize: 11, color: Colors.grey.shade500),
        ),
        if (riskCount > 0) ...[
          const SizedBox(width: 10),
          Container(width: 3, height: 3, decoration: BoxDecoration(color: Colors.grey.shade400, shape: BoxShape.circle)),
          const SizedBox(width: 10),
          Text(
            '$riskCount项风险提示',
            style: const TextStyle(fontSize: 11, color: Color(0xFFFF9500), fontWeight: FontWeight.w500),
          ),
        ],
        const Spacer(),
        Text(
          fromLlm ? '大模型分析' : '本地分析',
          style: TextStyle(fontSize: 11, color: Colors.grey.shade400),
        ),
      ],
    );
  }
}

class _DataWarningBanner extends StatelessWidget {
  const _DataWarningBanner({required this.completeness});
  final DataCompleteness completeness;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          color: const Color(0xFFFF9500).withValues(alpha: 0.08),
          borderRadius: BorderRadius.circular(10),
          border: Border.all(color: const Color(0xFFFF9500).withValues(alpha: 0.2)),
        ),
        child: Row(
          children: [
            const Icon(Icons.info_outline, size: 16, color: Color(0xFFFF9500)),
            const SizedBox(width: 8),
            Expanded(
              child: Text(
                completeness == DataCompleteness.minimal
                    ? '体征数据严重不足，分析结果仅供参考。请确保设备正常运行并上传数据。'
                    : '部分体征数据缺失，分析结果可能不完整。',
                style: const TextStyle(fontSize: 12, color: Color(0xFFFF9500)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _NoDataHint extends StatelessWidget {
  const _NoDataHint({required this.missingMetrics});
  final List<String> missingMetrics;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
      decoration: BoxDecoration(
        color: Colors.grey.shade100,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          Icon(Icons.info_outline, size: 15, color: Colors.grey.shade400),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              '缺少${missingMetrics.join("、")}数据',
              style: TextStyle(fontSize: 12, color: Colors.grey.shade500),
            ),
          ),
        ],
      ),
    );
  }
}

// ──── Loading / Empty states ────

class _LoadingPlaceholder extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const _SectionHeader(fromLlm: false),
        const SizedBox(height: 12),
        _shimmerCard(120),
        const SizedBox(height: 10),
        _shimmerCard(140),
        const SizedBox(height: 10),
        _shimmerCard(140),
        const SizedBox(height: 10),
        _shimmerCard(140),
      ],
    );
  }

  Widget _shimmerCard(double height) {
    return Container(
      height: height,
      decoration: BoxDecoration(
        color: Colors.grey.shade100,
        borderRadius: BorderRadius.circular(14),
      ),
    );
  }
}

class _EmptyPlaceholder extends StatelessWidget {
  const _EmptyPlaceholder({this.onRefresh});
  final VoidCallback? onRefresh;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        color: const Color(0xFFE8F4FD),
        borderRadius: BorderRadius.circular(16),
        border: const Border(
          left: BorderSide(color: Color(0xFF4A90E2), width: 4),
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              const Icon(Icons.psychology_outlined, color: Color(0xFF2C7DA0)),
              const SizedBox(width: 8),
              const Text(
                'AI 智能分析',
                style: TextStyle(fontWeight: FontWeight.w600, fontSize: 18),
              ),
            ],
          ),
          const SizedBox(height: 12),
          const Text(
            '暂无分析数据，请检查网络连接后刷新。',
            style: TextStyle(fontSize: 14, height: 1.35),
          ),
          if (onRefresh != null) ...[
            const SizedBox(height: 12),
            Align(
              alignment: Alignment.centerRight,
              child: FilledButton.tonal(
                onPressed: onRefresh,
                child: const Text('重试'),
              ),
            ),
          ],
        ],
      ),
    );
  }
}
