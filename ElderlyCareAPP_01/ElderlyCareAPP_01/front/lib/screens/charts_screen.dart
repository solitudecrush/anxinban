import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/ai_analysis.dart';
import '../models/vitals_history.dart';
import '../services/api_service.dart';

enum _Metric { temperature, heartRate, bloodPressure }

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
              ],
            ),
            const SizedBox(height: 16),
            Card(
              elevation: 2,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
              child: Padding(
                padding: const EdgeInsets.all(12),
                child: SizedBox(
                  height: 236,
                  child: _history == null || _chartEmpty()
                      ? const Center(
                          child: Text(
                            '暂无数据',
                            style: TextStyle(fontSize: 14),
                          ),
                        )
                      : _HealthLineChart(
                          history: _history!, metric: _metric),
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
        _Metric.bloodPressure => p.systolic != null && p.diastolic != null,
      };
    }
    return !any;
  }
}

class _HealthLineChart extends StatelessWidget {
  const _HealthLineChart({required this.history, required this.metric});

  final VitalsHistory history;
  final _Metric metric;

  @override
  Widget build(BuildContext context) {
    final pts = history.points;
    final n = pts.length;
    if (n == 0) {
      return const SizedBox.shrink();
    }

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
        minY = 60;
        maxY = 160;
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
    }

    String yLabel(double v) {
      if (metric == _Metric.temperature) {
        return v.toStringAsFixed(1);
      }
      return v.round().toString();
    }

    return InteractiveViewer(
      minScale: 1,
      maxScale: 3.2,
      boundaryMargin: const EdgeInsets.symmetric(horizontal: 24),
      child: LineChart(
        LineChartData(
          minX: 0,
          maxX: (n - 1).toDouble(),
          minY: minY,
          maxY: maxY,
          clipData: const FlClipData.all(),
          gridData: FlGridData(show: true, drawVerticalLine: false),
          borderData: FlBorderData(show: true),
          titlesData: FlTitlesData(
            topTitles: const AxisTitles(
              sideTitles: SideTitles(showTitles: false),
            ),
            rightTitles: const AxisTitles(
              sideTitles: SideTitles(showTitles: false),
            ),
            leftTitles: AxisTitles(
              sideTitles: SideTitles(
                showTitles: true,
                reservedSize: 36,
                getTitlesWidget: (v, m) =>
                    Text(yLabel(v), style: const TextStyle(fontSize: 10)),
              ),
            ),
            bottomTitles: AxisTitles(
              sideTitles: SideTitles(
                showTitles: true,
                interval: _bottomInterval(n),
                getTitlesWidget: (v, m) {
                  final i = v.round().clamp(0, n - 1);
                  final label = pts[i].label;
                  return Padding(
                    padding: const EdgeInsets.only(top: 6),
                    child: Text(label, style: const TextStyle(fontSize: 10)),
                  );
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
                  final i = t.x.round().clamp(0, n - 1);
                  final p = pts[i];
                  final text = switch (metric) {
                    _Metric.temperature =>
                      p.temperature == null
                          ? '-'
                          : '${p.temperature!.toStringAsFixed(1)} ℃',
                    _Metric.heartRate =>
                      p.heartRate == null ? '-' : '${p.heartRate} bpm',
                    _Metric.bloodPressure =>
                      (p.systolic == null || p.diastolic == null)
                          ? '-'
                          : '${p.systolic}/${p.diastolic} mmHg',
                  };
                  return LineTooltipItem(
                    '${p.label}\n$text',
                    const TextStyle(color: Colors.white, fontSize: 12),
                  );
                }).toList();
              },
            ),
          ),
          lineBarsData: bars,
        ),
        duration: Duration.zero,
      ),
    );
  }

  double _bottomInterval(int n) {
    if (n <= 8) {
      return 1;
    }
    if (n <= 16) {
      return 2;
    }
    return 4;
  }
}
