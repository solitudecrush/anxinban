import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/ai_analysis.dart';
import '../models/vitals_history.dart';
import '../services/api_service.dart';

enum _Metric { temperature, heartRate, bloodPressure, bloodOxygen }

class ChartsScreen extends StatefulWidget {
  const ChartsScreen({super.key});

  @override
  State<ChartsScreen> createState() => _ChartsScreenState();
}

class _ChartsScreenState extends State<ChartsScreen> {
  String _period = 'week';
  _Metric _metric = _Metric.temperature;
  VitalsHistory? _history;
  AiAnalysis? _ai;

  @override
  void initState() {
    super.initState();
    _reloadAll();
  }

  Future<void> _reloadAll() async {
    final api = context.read<ApiService>();
    final h = await api.fetchHistory(_period);
    final a = await api.analyzeAi(period: _period);
    if (!mounted) return;
    setState(() {
      _history = h;
      _ai = a;
    });
  }

  String _metricLabel(_Metric m) {
    return switch (m) {
      _Metric.temperature => '体温',
      _Metric.heartRate => '心率',
      _Metric.bloodPressure => '血压',
      _Metric.bloodOxygen => '血氧',
    };
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          '健康趋势',
          style: TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        ),
        foregroundColor: Colors.white,
        flexibleSpace: Container(
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              colors: [Color(0xFF2C7DA0), Color(0xFF4A90E2)],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
          ),
        ),
      ),
      body: RefreshIndicator(
        onRefresh: _reloadAll,
        child: ListView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
          children: [
            SegmentedButton<String>(
              segments: const [
                ButtonSegment(value: 'day', label: Text('日')),
                ButtonSegment(value: 'week', label: Text('周')),
                ButtonSegment(value: 'month', label: Text('月')),
              ],
              selected: {_period},
              onSelectionChanged: (s) {
                setState(() => _period = s.first);
                _reloadAll();
              },
            ),
            const SizedBox(height: 12),
            Wrap(
              spacing: 8,
              runSpacing: 4,
              children: [
                ChoiceChip(
                  label: const Text('体温'),
                  selected: _metric == _Metric.temperature,
                  onSelected: (_) =>
                      setState(() => _metric = _Metric.temperature),
                ),
                ChoiceChip(
                  label: const Text('心率'),
                  selected: _metric == _Metric.heartRate,
                  onSelected: (_) =>
                      setState(() => _metric = _Metric.heartRate),
                ),
                ChoiceChip(
                  label: const Text('血压'),
                  selected: _metric == _Metric.bloodPressure,
                  onSelected: (_) =>
                      setState(() => _metric = _Metric.bloodPressure),
                ),
                ChoiceChip(
                  label: const Text('血氧'),
                  selected: _metric == _Metric.bloodOxygen,
                  onSelected: (_) =>
                      setState(() => _metric = _Metric.bloodOxygen),
                ),
              ],
            ),
            const SizedBox(height: 16),
            if (_metric == _Metric.bloodPressure)
              Padding(
                padding: const EdgeInsets.only(bottom: 4),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    _LegendDot(color: Colors.deepPurple, label: '收缩压'),
                    const SizedBox(width: 16),
                    _LegendDot(color: Colors.lightBlue, label: '舒张压'),
                  ],
                ),
              ),
            Card(
              elevation: 2,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
              child: Padding(
                padding: const EdgeInsets.fromLTRB(8, 12, 8, 8),
                child: SizedBox(
                  height: 360,
                  child: _history == null || _chartEmpty()
                      ? const Center(
                          child: Text(
                            '暂无数据',
                            style: TextStyle(fontSize: 14),
                          ),
                        )
                      : _HealthLineChart(
                          history: _history!,
                          metric: _metric,
                          period: _period,
                        ),
                ),
              ),
            ),
            const SizedBox(height: 16),
            Container(
              decoration: BoxDecoration(
                color: const Color(0xFFE8F4FD),
                borderRadius: BorderRadius.circular(12),
                border: const Border(
                  left: BorderSide(
                    color: Color(0xFF4A90E2),
                    width: 4,
                  ),
                ),
              ),
              child: Padding(
                padding: const EdgeInsets.all(14),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        const Icon(
                          Icons.smart_toy_outlined,
                          color: Color(0xFF2C7DA0),
                        ),
                        const SizedBox(width: 8),
                        const Text(
                          'AI 智能分析',
                          style: TextStyle(
                            fontWeight: FontWeight.w600,
                            fontSize: 18,
                          ),
                        ),
                        const Spacer(),
                        if (_ai?.fromLlm == true)
                          Text(
                            '大模型',
                            style: TextStyle(
                              color: Colors.grey.shade600,
                              fontSize: 12,
                            ),
                          ),
                      ],
                    ),
                    const SizedBox(height: 12),
                    if (_ai != null) ...[
                      Text(
                        '总结：${_ai!.summary}',
                        style: const TextStyle(
                          height: 1.35,
                          fontSize: 14,
                        ),
                      ),
                      const SizedBox(height: 10),
                      Text(
                        '建议：${_ai!.suggestion}',
                        style: const TextStyle(
                          height: 1.35,
                          fontSize: 14,
                        ),
                      ),
                    ],
                    const SizedBox(height: 12),
                    Align(
                      alignment: Alignment.centerRight,
                      child: FilledButton.tonal(
                        onPressed: _reloadAll,
                        child: const Text('刷新分析'),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  bool _chartEmpty() {
    final pts = _history!.points;
    if (pts.isEmpty) {
      return true;
    }
    bool any = false;
    for (final p in pts) {
      any |= switch (_metric) {
        _Metric.temperature => p.temperature != null,
        _Metric.heartRate => p.heartRate != null,
        _Metric.bloodPressure => p.systolic != null || p.diastolic != null,
        _Metric.bloodOxygen => p.bloodOxygen != null,
      };
    }
    return !any;
  }
}

class _LegendDot extends StatelessWidget {
  const _LegendDot({required this.color, required this.label});
  final Color color;
  final String label;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          width: 10,
          height: 10,
          decoration: BoxDecoration(color: color, shape: BoxShape.circle),
        ),
        const SizedBox(width: 4),
        Text(label, style: const TextStyle(fontSize: 11, color: Colors.black54)),
      ],
    );
  }
}

class _HealthLineChart extends StatefulWidget {
  const _HealthLineChart({
    required this.history,
    required this.metric,
    required this.period,
  });

  final VitalsHistory history;
  final _Metric metric;
  final String period;

  @override
  State<_HealthLineChart> createState() => _HealthLineChartState();
}

class _HealthLineChartState extends State<_HealthLineChart> {
  /// Shared horizontal scroll controller so the chart and bottom axis stay synced.
  final ScrollController _hController = ScrollController();

  @override
  void dispose() {
    _hController.dispose();
    super.dispose();
  }

  // ──── helpers (delegated from widget) ────

  VitalsHistory get history => widget.history;
  _Metric get metric => widget.metric;
  String get period => widget.period;

  List<String> _buildLabels(List<VitalsHistoryPoint> pts, String period) {
    final labels = <String>[];
    for (var i = 0; i < pts.length; i++) {
      final raw = pts[i].label;
      final parsed = _tryParseLabel(raw, period, i, pts.length);
      labels.add(parsed);
    }
    return labels;
  }

  String _tryParseLabel(String raw, String period, int index, int total) {
    // First, try to parse as datetime (handles "2026-07-10 08:00", "2026-07-10T08:00", etc.)
    try {
      final dt = DateTime.parse(raw.replaceFirst(' ', 'T'));
      switch (period) {
        case 'day':
          return '${_pad(dt.hour)}:${_pad(dt.minute)}';
        case 'month':
          return '${dt.day}日';
        case 'week':
        default:
          return _weekdayName(dt.weekday);
      }
    } catch (_) {
      // Datetime parse failed, try pattern matching below
    }

    // For day view: extract HH:mm from any format (e.g. "08:00", "2026-07-10 08:00", "08:00:00")
    if (period == 'day') {
      final timeMatch = RegExp(r'(\d{1,2}):(\d{2})').firstMatch(raw);
      if (timeMatch != null) {
        final hour = int.tryParse(timeMatch.group(1)!) ?? 0;
        final minute = timeMatch.group(2) ?? '00';
        return '${_pad(hour)}:$minute';
      }
    }

    // For month view: extract day number from formats like "10日", "7月10日", "2026-07-10"
    if (period == 'month') {
      // Try "X日" pattern first
      final dayMatch = RegExp(r'(\d{1,2})\s*日').firstMatch(raw);
      if (dayMatch != null) {
        final num = int.tryParse(dayMatch.group(1) ?? '');
        if (num != null && num >= 1 && num <= 31) {
          return '${num}日';
        }
      }
      // Try extracting from date string like "2026-07-10" or "07-10"
      final dateMatch = RegExp(r'(\d{4}-)?(\d{1,2})-(\d{1,2})').firstMatch(raw);
      if (dateMatch != null) {
        final num = int.tryParse(dateMatch.group(3) ?? '');
        if (num != null && num >= 1 && num <= 31) {
          return '${num}日';
        }
      }
      // Generic digit extraction (must be reasonable day number)
      final digits = RegExp(r'(\d+)').firstMatch(raw);
      if (digits != null) {
        final num = int.tryParse(digits.group(1) ?? '');
        if (num != null && num >= 1 && num <= 31) {
          return '${num}日';
        }
      }
    }

    // For week view: check for weekday names
    if (period == 'week') {
      if (raw.contains('周')) return raw;
      final digits = RegExp(r'(\d+)').firstMatch(raw);
      if (digits != null) {
        final num = int.tryParse(digits.group(1) ?? '');
        if (num != null && num >= 1 && num <= 7) {
          return _weekdayName(num);
        }
      }
    }

    // Ultimate fallback: generate based on index
    if (period == 'week') {
      return _weekdayName((index % 7) + 1);
    }
    if (period == 'month') {
      // Generate sequential day numbers starting from 1
      return '${index + 1}日';
    }
    final hour = (8 + index * 2) % 24;
    return '${_pad(hour)}:00';
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

  String _pad(int n) => n.toString().padLeft(2, '0');

  // ──── build ────

  @override
  Widget build(BuildContext context) {
    final pts = history.points;
    final n = pts.length;
    if (n == 0) {
      return const SizedBox.shrink();
    }

    final labels = _buildLabels(pts, period);
    final positions = List.generate(n, (i) => _extractPosition(labels[i], i));
    final fullLabels = _generateFullLabels();
    final fullN = fullLabels.length;

    double minY;
    double maxY;
    List<LineChartBarData> bars;
    double minX;
    double maxX;

    switch (period) {
      case 'month':
        minX = 0;
        maxX = (fullN - 1).toDouble();
        break;
      case 'day':
        minX = 0;
        maxX = (fullN - 1).toDouble();
        break;
      case 'week':
      default:
        minX = 0;
        maxX = (n - 1).toDouble();
        break;
    }

    switch (metric) {
      case _Metric.temperature:
        minY = 35.0;
        maxY = 38.5;
        final spots = <FlSpot>[];
        for (var i = 0; i < n; i++) {
          final t = pts[i].temperature;
          if (t != null) {
            spots.add(FlSpot(positions[i], t));
          }
        }
        bars = [
          LineChartBarData(
            isCurved: true,
            color: Colors.orange,
            barWidth: 3,
            dotData: const FlDotData(show: true),
            spots: spots,
          ),
        ];
        break;
      case _Metric.heartRate:
        minY = 40;
        maxY = 120;
        final spots = <FlSpot>[];
        for (var i = 0; i < n; i++) {
          final hr = pts[i].heartRate;
          if (hr != null) {
            spots.add(FlSpot(positions[i], hr.toDouble()));
          }
        }
        bars = [
          LineChartBarData(
            isCurved: true,
            color: Colors.redAccent,
            barWidth: 3,
            dotData: const FlDotData(show: true),
            spots: spots,
          ),
        ];
        break;
      case _Metric.bloodPressure:
        minY = 40;
        maxY = 180;
        final sys = <FlSpot>[];
        final dia = <FlSpot>[];
        for (var i = 0; i < n; i++) {
          final s = pts[i].systolic;
          final d = pts[i].diastolic;
          if (s != null) {
            sys.add(FlSpot(positions[i], s.toDouble()));
          }
          if (d != null) {
            dia.add(FlSpot(positions[i], d.toDouble()));
          }
        }
        bars = [
          LineChartBarData(
            isCurved: true,
            color: Colors.deepPurple,
            barWidth: 3,
            dotData: const FlDotData(show: true),
            spots: sys,
          ),
          LineChartBarData(
            isCurved: true,
            color: Colors.lightBlue,
            barWidth: 3,
            dotData: const FlDotData(show: true),
            spots: dia,
          ),
        ];
        break;
      case _Metric.bloodOxygen:
        minY = 85;
        maxY = 105;
        final spots = <FlSpot>[];
        for (var i = 0; i < n; i++) {
          final bo = pts[i].bloodOxygen;
          if (bo != null) {
            spots.add(FlSpot(positions[i], bo.toDouble()));
          }
        }
        bars = [
          LineChartBarData(
            isCurved: true,
            color: Colors.teal,
            barWidth: 3,
            dotData: const FlDotData(show: true),
            spots: spots,
          ),
        ];
        break;
    }

    String yLabel(double v) {
      if (metric == _Metric.temperature) {
        return v.toStringAsFixed(1);
      }
      return v.round().toString();
    }

    // Build Y-axis label list (top = maxY → bottom = minY)
    final yInterval = _leftInterval(minY, maxY);
    final yTicks = <double>[];
    for (double v = maxY; v >= minY - 0.001; v -= yInterval) {
      yTicks.add(v);
    }

    // Use appropriate bar width based on period
    final minBarWidth = period == 'day' ? 36.0 : (period == 'month' ? 24.0 : 52.0);
    final chartWidth = fullN * minBarWidth;
    const yAxisWidth = 44.0;
    // Match fl_chart internal border padding so grid lines align
    const chartVPadding = 5.0;

    final chartHeight = 340.0;

    return LayoutBuilder(
      builder: (context, constraints) {
        final useWidth =
            chartWidth > constraints.maxWidth ? chartWidth : constraints.maxWidth;

        // Build shared chart data widget to avoid duplication
        // build bottom titles interval
        final bottomInterval = _bottomInterval(fullN).round();

        Widget buildChart() {
          return LineChart(
            LineChartData(
              minX: minX,
              maxX: maxX,
              minY: minY,
              maxY: maxY,
              clipData: const FlClipData.none(),
              gridData: FlGridData(
                show: true,
                drawVerticalLine: false,
              ),
              borderData: FlBorderData(show: true),
              titlesData: FlTitlesData(
                topTitles: const AxisTitles(
                  sideTitles: SideTitles(showTitles: false),
                ),
                rightTitles: const AxisTitles(
                  sideTitles: SideTitles(showTitles: false),
                ),
                // Hide built-in left titles — we render our own fixed Y-axis
                leftTitles: const AxisTitles(
                  sideTitles: SideTitles(showTitles: false),
                ),
                // Built-in bottom titles with full range labels
                bottomTitles: AxisTitles(
                  sideTitles: SideTitles(
                    showTitles: true,
                    reservedSize: 32,
                    interval: bottomInterval <= 0 ? 1 : bottomInterval.toDouble(),
                    getTitlesWidget: (value, meta) {
                      final idx = value.round();
                      if (idx >= 0 && idx < fullLabels.length) {
                        final show = period == 'week'
                            ? _shouldShowXLabel(idx, fullN)
                            : (bottomInterval <= 1 || idx % bottomInterval == 0);
                        if (!show) return const SizedBox.shrink();
                        return Padding(
                          padding: const EdgeInsets.only(top: 8),
                          child: Text(
                            fullLabels[idx],
                            style: const TextStyle(
                              fontSize: 10,
                              color: Colors.black54,
                            ),
                          ),
                        );
                      }
                      return const SizedBox.shrink();
                    },
                  ),
                ),
              ),
              lineTouchData: LineTouchData(
                enabled: true,
                handleBuiltInTouches: true,
                touchTooltipData: LineTouchTooltipData(
                  getTooltipItems: (touched) {
                    return touched.map((t) {
                      // Find nearest data point by x position
                      var bestIdx = 0;
                      var bestDist = double.infinity;
                      for (var j = 0; j < n; j++) {
                        final dist = (positions[j] - t.x).abs();
                        if (dist < bestDist) {
                          bestDist = dist;
                          bestIdx = j;
                        }
                      }
                      final p = pts[bestIdx];
                      final lbl =
                          bestIdx < labels.length ? labels[bestIdx] : p.label;
                      final text = switch (metric) {
                        _Metric.temperature =>
                          p.temperature == null
                              ? '-'
                              : '${p.temperature!.toStringAsFixed(1)} ℃',
                        _Metric.heartRate =>
                          p.heartRate == null
                              ? '-'
                              : '${p.heartRate} bpm',
                        _Metric.bloodPressure =>
                          (p.systolic == null || p.diastolic == null)
                              ? '-'
                              : '${p.systolic}/${p.diastolic} mmHg',
                        _Metric.bloodOxygen =>
                          p.bloodOxygen == null
                              ? '-'
                              : '${p.bloodOxygen}%',
                      };
                      return LineTooltipItem(
                        '$lbl\n$text',
                        const TextStyle(
                          color: Colors.white,
                          fontSize: 12,
                        ),
                      );
                    }).toList();
                  },
                ),
              ),
              lineBarsData: bars,
            ),
            duration: Duration.zero,
          );
        }

        return Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // ── Y-axis: fixed horizontally ──
            SizedBox(
              width: yAxisWidth,
              height: chartHeight + 32, // +32 for bottom titles reserved space
              child: Padding(
                padding: const EdgeInsets.only(
                  top: chartVPadding,
                  bottom: chartVPadding + 32,
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: yTicks
                      .map((v) => Padding(
                            padding: const EdgeInsets.only(right: 4),
                            child: Text(
                              yLabel(v),
                              style: const TextStyle(
                                fontSize: 10,
                                color: Colors.black54,
                              ),
                            ),
                          ))
                      .toList(),
                ),
              ),
            ),
            // Vertical divider
            Container(
              width: 1,
              height: chartHeight + 32,
              color: Colors.grey.shade300,
            ),
            // ── Chart: scrolls horizontally (x-axis labels inside chart via bottomTitles) ──
            Expanded(
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                controller: _hController,
                physics: const BouncingScrollPhysics(),
                child: SizedBox(
                  width: useWidth,
                  height: chartHeight + 32,
                  child: buildChart(),
                ),
              ),
            ),
          ],
        );
      },
    );
  }

  /// Whether to show the X-axis label at index [i] out of [total].
  bool _shouldShowXLabel(int i, int total) {
    final interval = _bottomInterval(total).round();
    if (interval <= 1) return true;
    return i % interval == 0;
  }

  /// Extract the numeric x-position from a label based on period.
  /// Month: "15日" → 14.0 (0-indexed). Day: "08:30" → 8.5.
  double _extractPosition(String label, int index) {
    switch (period) {
      case 'month':
        final match = RegExp(r'(\d+)').firstMatch(label);
        if (match != null) {
          final day = int.tryParse(match.group(1)!) ?? (index + 1);
          // Ensure day is in valid range (1-31), not a year like 2026
          if (day >= 1 && day <= 31) {
            return (day - 1).toDouble();
          }
        }
        return index.toDouble();
      case 'day':
        final match = RegExp(r'(\d{1,2}):(\d{2})').firstMatch(label);
        if (match != null) {
          final hour = int.tryParse(match.group(1)!) ?? 0;
          final minute = int.tryParse(match.group(2)!) ?? 0;
          return hour + minute / 60.0;
        }
        return index.toDouble();
      case 'week':
      default:
        return index.toDouble();
    }
  }

  /// Generate all x-axis labels for the full time range (not just data points).
  List<String> _generateFullLabels() {
    switch (period) {
      case 'month':
        final now = DateTime.now();
        // 从1号到今天的日期，确保横坐标从1号开始显示
        final maxDay = now.day;
        return List.generate(maxDay, (i) => '${i + 1}日');
      case 'day':
        // 只显示小时点数，不写年月日
        return List.generate(24, (i) => '${_pad(i)}:00');
      case 'week':
      default:
        return _buildLabels(history.points, period);
    }
  }

  double _leftInterval(double minY, double maxY) {
    final range = maxY - minY;
    if (range <= 4) return 0.5;
    if (range <= 20) return 5;
    if (range <= 50) return 10;
    return 20;
  }

  double _bottomInterval(int n) {
    if (n <= 8) return 1;
    if (n <= 16) return 2;
    return 4;
  }
}
