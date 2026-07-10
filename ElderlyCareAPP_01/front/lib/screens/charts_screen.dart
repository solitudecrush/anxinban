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
      bool matchesExpected;
      switch (period) {
        case 'day':
          matchesExpected = raw.contains(':');
          break;
        case 'week':
          matchesExpected = raw.contains('周');
          break;
        case 'month':
          matchesExpected = raw.contains('日');
          break;
        default:
          matchesExpected = false;
      }
      if (matchesExpected) {
        labels.add(raw);
      } else {
        final parsed = _tryParseLabel(raw, period, i, pts.length);
        labels.add(parsed);
      }
    }
    return labels;
  }

  String _tryParseLabel(String raw, String period, int index, int total) {
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
      if (period == 'day' && raw.contains(':')) {
        return raw;
      }
      final digits = RegExp(r'(\d+)').firstMatch(raw);
      if (digits != null) {
        final num = int.tryParse(digits.group(1) ?? '');
        if (num != null) {
          if (period == 'month') {
            return '${num}日';
          }
          if (period == 'week' && num >= 1 && num <= 7) {
            return _weekdayName(num);
          }
        }
      }
      if (period == 'week') {
        return _weekdayName((index % 7) + 1);
      }
      if (period == 'month') {
        final dayNum = total > 1 ? 1 + (index * (28 ~/ total)).clamp(0, 30) : 1;
        return '${dayNum}日';
      }
      final hour = (8 + index * 2) % 24;
      return '${_pad(hour)}:00';
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

    double minY;
    double maxY;
    List<LineChartBarData> bars;

    switch (metric) {
      case _Metric.temperature:
        minY = 35.0;
        maxY = 38.5;
        final spots = <FlSpot>[];
        for (var i = 0; i < n; i++) {
          final t = pts[i].temperature;
          if (t != null) {
            spots.add(FlSpot(i.toDouble(), t));
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
            spots.add(FlSpot(i.toDouble(), hr.toDouble()));
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
            sys.add(FlSpot(i.toDouble(), s.toDouble()));
          }
          if (d != null) {
            dia.add(FlSpot(i.toDouble(), d.toDouble()));
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
            spots.add(FlSpot(i.toDouble(), bo.toDouble()));
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

    final minBarWidth = period == 'day' ? 56.0 : 52.0;
    final chartWidth = n * minBarWidth;
    const yAxisWidth = 44.0;
    const xAxisHeight = 30.0;
    // Match fl_chart internal border padding so grid lines align
    const chartVPadding = 5.0;

    // Taller chart for day view (many time points) to make vertical scrolling useful
    final chartHeight = period == 'day' ? 500.0 : 340.0;

    return LayoutBuilder(
      builder: (context, constraints) {
        final useWidth =
            chartWidth > constraints.maxWidth ? chartWidth : constraints.maxWidth;

        // Build shared chart data widget to avoid duplication
        Widget buildChart() {
          return LineChart(
            LineChartData(
              minX: 0,
              maxX: (n - 1).toDouble(),
              minY: minY,
              maxY: maxY,
              clipData: const FlClipData.all(),
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
                // Hide built-in bottom titles — we render our own fixed X-axis
                bottomTitles: const AxisTitles(
                  sideTitles: SideTitles(showTitles: false),
                ),
              ),
              lineTouchData: LineTouchData(
                enabled: true,
                handleBuiltInTouches: true,
                touchTooltipData: LineTouchTooltipData(
                  getTooltipItems: (touched) {
                    return touched.map((t) {
                      final i = t.x.round().clamp(0, n - 1);
                      final p = pts[i];
                      final lbl =
                          i < labels.length ? labels[i] : p.label;
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

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // ── Main content: vertically scrollable area ──
            // Y-axis labels scroll vertically with the chart (so they stay aligned
            // with grid lines), but are fixed horizontally.
            Expanded(
              child: SingleChildScrollView(
                scrollDirection: Axis.vertical,
                physics: const BouncingScrollPhysics(),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // ── Y-axis: scrolls vertically, FIXED horizontally ──
                    SizedBox(
                      width: yAxisWidth,
                      height: chartHeight,
                      child: Padding(
                        padding: const EdgeInsets.only(
                          top: chartVPadding,
                          bottom: chartVPadding,
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
                    // Vertical divider (scrolls vertically with content)
                    Container(
                      width: 1,
                      height: chartHeight,
                      color: Colors.grey.shade300,
                    ),
                    // ── Chart: scrolls BOTH horizontally and vertically ──
                    Expanded(
                      child: SingleChildScrollView(
                        scrollDirection: Axis.horizontal,
                        controller: _hController,
                        physics: const BouncingScrollPhysics(),
                        child: SizedBox(
                          width: useWidth,
                          height: chartHeight,
                          child: buildChart(),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            // ── X-axis row: FIXED vertically, scrolls horizontally (synced) ──
            SizedBox(
              height: xAxisHeight,
              child: Row(
                children: [
                  // Spacer matching Y-axis width + divider
                  const SizedBox(width: yAxisWidth + 1),
                  // Horizontally scrollable X-axis (passively follows chart)
                  Expanded(
                    child: SingleChildScrollView(
                      scrollDirection: Axis.horizontal,
                      controller: _hController,
                      physics: const NeverScrollableScrollPhysics(),
                      child: SizedBox(
                        width: useWidth,
                        height: xAxisHeight,
                        child: Row(
                          children: List.generate(n, (i) {
                            final label =
                                i < labels.length ? labels[i] : pts[i].label;
                            final showLabel = _shouldShowXLabel(i, n);
                            return SizedBox(
                              width: minBarWidth,
                              child: showLabel
                                  ? Center(
                                      child: Text(
                                        label,
                                        style: const TextStyle(
                                          fontSize: 10,
                                          color: Colors.black54,
                                        ),
                                        overflow: TextOverflow.ellipsis,
                                      ),
                                    )
                                  : const SizedBox.shrink(),
                            );
                          }),
                        ),
                      ),
                    ),
                  ),
                ],
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
