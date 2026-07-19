import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/ai_analysis.dart';
import '../models/vitals_history.dart';
import '../services/api_service.dart';
import '../widgets/ai_analysis_panel.dart';
import '../widgets/emotion_banner.dart';
import '../widgets/music_recommendation_card.dart';

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
  EmotionAnalysis? _emotion;
  bool _aiLoading = false;
  bool _chartLoading = true;
  bool _musicLoading = false;
  final ScrollController _listScrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    _loadChartData();
    // 自动加载情绪分析（使用 Java 规则引擎，无需消耗 API 额度）
    _autoLoadEmotionAnalysis();
  }

  /// 页面加载时自动触发情绪分析（静默执行，不显示 loading）。
  /// 仅调用 Java 规则引擎，不消耗大模型 API 额度。
  Future<void> _autoLoadEmotionAnalysis() async {
    try {
      final api = context.read<ApiService>();
      final emotion = await api.fetchEmotionAnalysis(period: _period);
      if (!mounted) return;
      if (emotion != null) {
        setState(() => _emotion = emotion);
      }
    } catch (_) {
      // 静默失败，用户可手动触发完整分析
    }
  }

  @override
  void dispose() {
    _listScrollController.dispose();
    super.dispose();
  }

  /// 加载图表数据（不触发 AI 分析，快速无 API 消耗）
  Future<void> _loadChartData() async {
    final api = context.read<ApiService>();
    setState(() => _chartLoading = true);
    final h = await api.fetchHistory(_period);
    if (!mounted) return;
    setState(() {
      _history = h;
      _chartLoading = false;
    });
  }

  /// 触发 AI 智能分析（健康分析 + 情绪分析并行执行）
  Future<void> _runAiAnalysis() async {
    final api = context.read<ApiService>();
    setState(() => _aiLoading = true);
    // 并行请求健康分析和情绪分析
    final results = await Future.wait([
      api.analyzeAi(period: _period, useLlm: true),
      api.fetchEmotionAnalysis(period: _period),
    ]);
    if (!mounted) return;
    setState(() {
      _ai = results[0] as AiAnalysis?;
      _emotion = results[1] as EmotionAnalysis?;
      _aiLoading = false;
    });
  }

  /// 处理"播放舒缓音乐"按钮点击。
  /// 创建音乐干预记录并通过快速对话接口发送音乐控制指令。
  Future<void> _handlePlayMusic() async {
    final api = context.read<ApiService>();
    final eid = api.elderId;
    if (eid == null || eid.isEmpty) return;

    setState(() => _musicLoading = true);

    try {
      // 1. 创建音乐干预记录
      await api.createIntervention(
        elderId: eid,
        type: 'music',
        reason: '情绪分析触发：${_emotion?.emotionState ?? '未知'}',
        musicType: '舒缓音乐',
      );

      // 2. 发送音乐控制指令
      await api.sendChatQuick(
        elderId: eid,
        message: '播放舒缓音乐',
      );

      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('音乐干预请求已发送，将通过智能音箱为老人播放舒缓音乐'),
          backgroundColor: const Color(0xFF5856D6),
          behavior: SnackBarBehavior.floating,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          duration: const Duration(seconds: 3),
        ),
      );
    } catch (_) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('音乐干预请求发送失败，请重试'),
          behavior: SnackBarBehavior.floating,
        ),
      );
    } finally {
      if (mounted) setState(() => _musicLoading = false);
    }
  }

  /// 下拉刷新：只刷新图表数据
  Future<void> _onRefresh() async {
    await _loadChartData();
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
      body: Scrollbar(
        controller: _listScrollController,
        thumbVisibility: true,
        child: RefreshIndicator(
          onRefresh: _onRefresh,
          child: ListView(
            controller: _listScrollController,
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
                _loadChartData();
                _autoLoadEmotionAnalysis(); // 切换周期时同步刷新情绪数据
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

            // ── 🎭 情绪状态横幅（页面最醒目元素，始终显示）──
            if (_emotion != null) ...[
              EmotionBanner(emotion: _emotion!),
              const SizedBox(height: 16),
            ],

            // ── 🎵 音乐舒缓推荐（仅焦虑时显示）──
            if (_emotion != null &&
                _emotion!.emotionLevel.index >= EmotionLevel.medium.index) ...[
              MusicRecommendationCard(
                emotion: _emotion!,
                onPlayMusic: _musicLoading ? null : _handlePlayMusic,
                loading: _musicLoading,
              ),
              const SizedBox(height: 16),
            ],

            // ── 血压图例 ──
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

            // ── 折线图 ──
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

            // ── AI 健康分析（情绪卡片已移除，独立展示在上面）──
            if (_aiLoading)
              const AiAnalysisPanel.loading()
            else if (_ai != null)
              AiAnalysisPanel(
                analysis: _ai,
                onRefresh: _runAiAnalysis,
              )
            else
              _AiPlaceholder(onTap: _runAiAnalysis),
          ],
        ),
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

  /// Custom tooltip state
  int? _selectedPointIndex;
  String _tooltipText = '';

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
    // First, try to parse as datetime (handles "2026-07-10 08:00", "2026-07-10T08:00", "2026-07-10", etc.)
    try {
      final dt = DateTime.parse(raw.replaceFirst(' ', 'T'));
      switch (period) {
        case 'day':
          return '${_pad(dt.hour)}:${_pad(dt.minute)}';
        case 'month':
          return '${dt.day}日';
        case 'week':
        default:
          // Show as MM/dd for display
          return '${_pad(dt.month)}/${_pad(dt.day)}';
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

    // For week view: try to parse as date string
    if (period == 'week') {
      // Try MM/dd format (already display label, return as-is)
      if (RegExp(r'^\d{1,2}/\d{1,2}$').hasMatch(raw)) return raw;
      // Try yyyy-MM-dd format
      final dateMatch = RegExp(r'(\d{4})-(\d{1,2})-(\d{1,2})').firstMatch(raw);
      if (dateMatch != null) {
        return '${_pad(int.parse(dateMatch.group(2)!))}/${_pad(int.parse(dateMatch.group(3)!))}';
      }
      // Try weekday names (old format compatibility)
      if (raw.contains('周')) return raw;
    }

    // Ultimate fallback: generate based on index
    if (period == 'week') {
      // Generate dates counting back from today
      final d = DateTime.now().subtract(Duration(days: 6 - (index % 7)));
      return '${_pad(d.month)}/${_pad(d.day)}';
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

  /// 去重 FlSpot 列表：同一 x 坐标只保留一个点，多个点时取 y 平均值。
  List<FlSpot> _deduplicateSpots(List<FlSpot> spots) {
    if (spots.length <= 1) return spots;
    final map = <double, List<double>>{};
    for (final s in spots) {
      map.putIfAbsent(s.x, () => []).add(s.y);
    }
    return map.entries
        .map((e) => FlSpot(e.key, e.value.reduce((a, b) => a + b) / e.value.length))
        .toList()
      ..sort((a, b) => a.x.compareTo(b.x));
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
        maxX = 6.0; // Fixed 7-day range: 周一=0, 周日=6
        break;
    }

    switch (metric) {
      case _Metric.temperature:
        minY = 35.0;
        maxY = 38.5;
        final rawSpots = <FlSpot>[];
        for (var i = 0; i < n; i++) {
          final t = pts[i].temperature;
          if (t != null) {
            rawSpots.add(FlSpot(positions[i], t));
          }
        }
        final spots = _deduplicateSpots(rawSpots);
        // Anchor curve at left boundary to prevent backward overshoot artifact
        if (spots.isNotEmpty && spots.first.x > minX + 0.01) {
          spots.insert(0, FlSpot(minX, spots.first.y));
        }
        bars = [
          LineChartBarData(
            isCurved: true,
            preventCurveOverShooting: true,
            color: Colors.orange,
            barWidth: 3,
            dotData: FlDotData(
              show: true,
              getDotPainter: (spot, percent, barData, index) {
                if (_selectedPointIndex != null &&
                    _selectedPointIndex! < positions.length &&
                    (spot.x - positions[_selectedPointIndex!]).abs() < 0.001) {
                  return FlDotCirclePainter(
                    radius: 7,
                    color: Colors.white,
                    strokeWidth: 3,
                    strokeColor: Colors.blue,
                  );
                }
                return FlDotCirclePainter(
                  radius: 3,
                  color: barData.color ?? Colors.grey,
                  strokeWidth: 0,
                );
              },
            ),
            spots: spots,
          ),
        ];
        break;
      case _Metric.heartRate:
        minY = 40;
        maxY = 120;
        final rawSpots = <FlSpot>[];
        for (var i = 0; i < n; i++) {
          final hr = pts[i].heartRate;
          if (hr != null) {
            rawSpots.add(FlSpot(positions[i], hr.toDouble()));
          }
        }
        final spots = _deduplicateSpots(rawSpots);
        // Anchor curve at left boundary to prevent backward overshoot artifact
        if (spots.isNotEmpty && spots.first.x > minX + 0.01) {
          spots.insert(0, FlSpot(minX, spots.first.y));
        }
        bars = [
          LineChartBarData(
            isCurved: true,
            preventCurveOverShooting: true,
            color: Colors.redAccent,
            barWidth: 3,
            dotData: FlDotData(
              show: true,
              getDotPainter: (spot, percent, barData, index) {
                if (_selectedPointIndex != null &&
                    _selectedPointIndex! < positions.length &&
                    (spot.x - positions[_selectedPointIndex!]).abs() < 0.001) {
                  return FlDotCirclePainter(
                    radius: 7,
                    color: Colors.white,
                    strokeWidth: 3,
                    strokeColor: Colors.blue,
                  );
                }
                return FlDotCirclePainter(
                  radius: 3,
                  color: barData.color ?? Colors.grey,
                  strokeWidth: 0,
                );
              },
            ),
            spots: spots,
          ),
        ];
        break;
      case _Metric.bloodPressure:
        minY = 40;
        maxY = 180;
        final rawSys = <FlSpot>[];
        final rawDia = <FlSpot>[];
        for (var i = 0; i < n; i++) {
          final s = pts[i].systolic;
          final d = pts[i].diastolic;
          if (s != null) {
            rawSys.add(FlSpot(positions[i], s.toDouble()));
          }
          if (d != null) {
            rawDia.add(FlSpot(positions[i], d.toDouble()));
          }
        }
        final sys = _deduplicateSpots(rawSys);
        final dia = _deduplicateSpots(rawDia);
        // Anchor curves at left boundary to prevent backward overshoot artifact
        if (sys.isNotEmpty && sys.first.x > minX + 0.01) {
          sys.insert(0, FlSpot(minX, sys.first.y));
        }
        if (dia.isNotEmpty && dia.first.x > minX + 0.01) {
          dia.insert(0, FlSpot(minX, dia.first.y));
        }
        bars = [
          LineChartBarData(
            isCurved: true,
            preventCurveOverShooting: true,
            color: Colors.deepPurple,
            barWidth: 3,
            dotData: FlDotData(
              show: true,
              getDotPainter: (spot, percent, barData, index) {
                if (_selectedPointIndex != null &&
                    _selectedPointIndex! < positions.length &&
                    (spot.x - positions[_selectedPointIndex!]).abs() < 0.001) {
                  return FlDotCirclePainter(
                    radius: 7,
                    color: Colors.white,
                    strokeWidth: 3,
                    strokeColor: Colors.blue,
                  );
                }
                return FlDotCirclePainter(
                  radius: 3,
                  color: barData.color ?? Colors.grey,
                  strokeWidth: 0,
                );
              },
            ),
            spots: sys,
          ),
          LineChartBarData(
            isCurved: true,
            preventCurveOverShooting: true,
            color: Colors.lightBlue,
            barWidth: 3,
            dotData: FlDotData(
              show: true,
              getDotPainter: (spot, percent, barData, index) {
                if (_selectedPointIndex != null &&
                    _selectedPointIndex! < positions.length &&
                    (spot.x - positions[_selectedPointIndex!]).abs() < 0.001) {
                  return FlDotCirclePainter(
                    radius: 7,
                    color: Colors.white,
                    strokeWidth: 3,
                    strokeColor: Colors.blue,
                  );
                }
                return FlDotCirclePainter(
                  radius: 3,
                  color: barData.color ?? Colors.grey,
                  strokeWidth: 0,
                );
              },
            ),
            spots: dia,
          ),
        ];
        break;
      case _Metric.bloodOxygen:
        minY = 85;
        maxY = 105;
        final rawSpots = <FlSpot>[];
        for (var i = 0; i < n; i++) {
          final bo = pts[i].bloodOxygen;
          if (bo != null) {
            rawSpots.add(FlSpot(positions[i], bo.toDouble()));
          }
        }
        final spots = _deduplicateSpots(rawSpots);
        // Anchor curve at left boundary to prevent backward overshoot artifact
        if (spots.isNotEmpty && spots.first.x > minX + 0.01) {
          spots.insert(0, FlSpot(minX, spots.first.y));
        }
        bars = [
          LineChartBarData(
            isCurved: true,
            preventCurveOverShooting: true,
            color: Colors.teal,
            barWidth: 3,
            dotData: FlDotData(
              show: true,
              getDotPainter: (spot, percent, barData, index) {
                if (_selectedPointIndex != null &&
                    _selectedPointIndex! < positions.length &&
                    (spot.x - positions[_selectedPointIndex!]).abs() < 0.001) {
                  return FlDotCirclePainter(
                    radius: 7,
                    color: Colors.white,
                    strokeWidth: 3,
                    strokeColor: Colors.blue,
                  );
                }
                return FlDotCirclePainter(
                  radius: 3,
                  color: barData.color ?? Colors.grey,
                  strokeWidth: 0,
                );
              },
            ),
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
              lineTouchData: const LineTouchData(
                enabled: false,
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
            // ── Chart: scrolls horizontally with custom tooltip overlay ──
            Expanded(
              child: Scrollbar(
                controller: _hController,
                thumbVisibility: true,
                child: SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  controller: _hController,
                  physics: const BouncingScrollPhysics(),
                  child: GestureDetector(
                  onTapUp: (details) {
                    _handleChartTap(
                      details,
                      useWidth: useWidth,
                      maxX: maxX,
                      minX: minX,
                      positions: positions,
                      labels: labels,
                      pts: pts,
                    );
                  },
                  child: SizedBox(
                    width: useWidth,
                    height: chartHeight + 32,
                    child: Stack(
                      children: [
                        SizedBox(
                          width: useWidth,
                          height: chartHeight + 32,
                          child: buildChart(),
                        ),
                        // Tooltip bar overlay at bottom of chart
                        if (_selectedPointIndex != null &&
                            _selectedPointIndex! < pts.length)
                          Positioned(
                            bottom: 0,
                            left: 0,
                            right: 0,
                            child: Container(
                              height: 32,
                              color: Colors.black87,
                              alignment: Alignment.center,
                              child: Text(
                                _tooltipText,
                                style: const TextStyle(
                                  color: Colors.white,
                                  fontSize: 12,
                                  fontWeight: FontWeight.w500,
                                ),
                              ),
                            ),
                          ),
                      ],
                    ),
                  ),
                ),
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
        // Parse MM/dd display label to position 0-6 (days from week start)
        final weekMatch = RegExp(r'(\d{1,2})/(\d{1,2})').firstMatch(label);
        if (weekMatch != null) {
          final month = int.parse(weekMatch.group(1)!);
          final day = int.parse(weekMatch.group(2)!);
          final now = DateTime.now();
          var date = DateTime(now.year, month, day);
          // Handle year boundary
          if (date.isAfter(now)) {
            date = DateTime(now.year - 1, month, day);
          }
          final weekStart = DateTime(now.year, now.month, now.day).subtract(const Duration(days: 6));
          final diff = date.difference(weekStart).inDays;
          return diff.toDouble().clamp(0.0, 6.0);
        }
        return index.toDouble();
      default:
        return index.toDouble();
    }
  }

  /// Generate all x-axis labels for the full time range (not just data points).
  /// 每日: 0点到23点，每隔2小时显示标签
  /// 每周: 周一到周日固定
  /// 每月: 1日到31日固定
  List<String> _generateFullLabels() {
    switch (period) {
      case 'month':
        // 固定1日到31日
        return List.generate(31, (i) => '${i + 1}日');
      case 'day':
        // 0点到23点，共24个小时点
        return List.generate(24, (i) => '${_pad(i)}:00');
      case 'week':
      default:
        // 最近7天的实际日期
        final now = DateTime.now();
        final start = DateTime(now.year, now.month, now.day).subtract(const Duration(days: 6));
        return List.generate(7, (i) {
          final d = start.add(Duration(days: i));
          return '${_pad(d.month)}/${_pad(d.day)}';
        });
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

  String _formatTapValue(VitalsHistoryPoint p, _Metric m) {
    return switch (m) {
      _Metric.temperature =>
        p.temperature == null ? '-' : '${p.temperature!.toStringAsFixed(1)} ℃',
      _Metric.heartRate =>
        p.heartRate == null ? '-' : '${p.heartRate} bpm',
      _Metric.bloodPressure =>
        (p.systolic == null || p.diastolic == null)
            ? '-'
            : '${p.systolic}/${p.diastolic} mmHg',
      _Metric.bloodOxygen =>
        p.bloodOxygen == null ? '-' : '${p.bloodOxygen}%',
    };
  }

  void _handleChartTap(
    TapUpDetails details, {
    required double useWidth,
    required double maxX,
    required double minX,
    required List<double> positions,
    required List<String> labels,
    required List<VitalsHistoryPoint> pts,
  }) {
    final dataRange = maxX - minX;
    if (dataRange <= 0 || positions.isEmpty) return;

    final tapX = details.localPosition.dx;
    final dataX = tapX / useWidth * dataRange + minX;

    int? bestIdx;
    double bestDist = double.infinity;
    for (int i = 0; i < positions.length; i++) {
      final dist = (positions[i] - dataX).abs();
      if (dist < bestDist) {
        bestDist = dist;
        bestIdx = i;
      }
    }

    if (bestIdx != null && bestIdx < pts.length) {
      // Accept tap if within 10% of x-range from nearest data point
      final threshold = dataRange * 0.10;
      if (bestDist <= threshold) {
        final p = pts[bestIdx];
        final lbl = bestIdx < labels.length ? labels[bestIdx] : p.label;
        final value = _formatTapValue(p, metric);
        setState(() {
          _selectedPointIndex = bestIdx;
          _tooltipText = '$lbl: $value';
        });
        return;
      }
    }

    // Tap on empty area clears the selection
    setState(() {
      _selectedPointIndex = null;
      _tooltipText = '';
    });
  }
}

/// AI 分析未触发时的占位卡片，提示用户点击按钮获取大模型分析。
class _AiPlaceholder extends StatelessWidget {
  const _AiPlaceholder({required this.onTap});
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFFF0F7FF), Color(0xFFE8F0FE)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFF4A90E2).withValues(alpha: 0.2)),
      ),
      child: Column(
        children: [
          Container(
            width: 64,
            height: 64,
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFF4A90E2), Color(0xFF2C7DA0)],
              ),
              borderRadius: BorderRadius.circular(20),
            ),
            child: const Icon(Icons.psychology_outlined, color: Colors.white, size: 34),
          ),
          const SizedBox(height: 16),
          const Text(
            'AI 智能分析',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: Color(0xFF1D1D1F),
            ),
          ),
          const SizedBox(height: 8),
          const Text(
            '基于 AI 智能体，综合分析体征趋势、告警记录、\n睡眠数据等多维度信息，评估老人情绪状态与健康风险',
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 13,
              color: Color(0xFF6B7280),
              height: 1.5,
            ),
          ),
          const SizedBox(height: 20),
          FilledButton.icon(
            onPressed: onTap,
            icon: const Icon(Icons.auto_awesome, size: 18),
            label: const Text('获取 AI 智能分析'),
            style: FilledButton.styleFrom(
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
            ),
          ),
          const SizedBox(height: 8),
          Text(
            '每次分析消耗 API 额度，请按需使用',
            style: TextStyle(fontSize: 11, color: Colors.grey.shade400),
          ),
        ],
      ),
    );
  }
}
