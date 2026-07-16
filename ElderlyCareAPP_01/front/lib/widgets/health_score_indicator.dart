import 'dart:math' as math;
import 'package:flutter/material.dart';
import '../models/ai_analysis.dart';

/// A circular health score gauge that displays 0–100 score with color-coded arc.
class HealthScoreIndicator extends StatelessWidget {
  const HealthScoreIndicator({
    super.key,
    required this.score,
    required this.level,
    this.size = 120,
  });

  final int score;
  final HealthLevel level;
  final double size;

  Color get _arcColor {
    return switch (level) {
      HealthLevel.healthy => const Color(0xFF34C759),
      HealthLevel.attention => const Color(0xFFFF9500),
      HealthLevel.warning => const Color(0xFFFF9500),
      HealthLevel.critical => const Color(0xFFFF3B30),
    };
  }

  Color get _trackColor => Colors.grey.shade200;

  @override
  Widget build(BuildContext context) {
    final strokeWidth = size * 0.10;

    return SizedBox(
      width: size,
      height: size,
      child: Stack(
        alignment: Alignment.center,
        children: [
          // Background track
          CustomPaint(
            size: Size(size, size),
            painter: _ArcPainter(
              color: _trackColor,
              strokeWidth: strokeWidth,
              sweepAngle: 2 * math.pi,
            ),
          ),
          // Score arc
          CustomPaint(
            size: Size(size, size),
            painter: _ArcPainter(
              color: _arcColor,
              strokeWidth: strokeWidth,
              sweepAngle: 2 * math.pi * (score / 100.0),
            ),
          ),
          // Score text
          Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                '$score',
                style: TextStyle(
                  fontSize: size * 0.26,
                  fontWeight: FontWeight.bold,
                  color: _arcColor,
                  height: 1,
                ),
              ),
              Text(
                '分',
                style: TextStyle(
                  fontSize: size * 0.12,
                  color: Colors.grey.shade600,
                  height: 1,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _ArcPainter extends CustomPainter {
  _ArcPainter({
    required this.color,
    required this.strokeWidth,
    required this.sweepAngle,
  });

  final Color color;
  final double strokeWidth;
  final double sweepAngle;

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = color
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round
      ..style = PaintingStyle.stroke;

    final rect = Rect.fromLTWH(
      strokeWidth / 2,
      strokeWidth / 2,
      size.width - strokeWidth,
      size.height - strokeWidth,
    );

    const startAngle = -math.pi / 2; // Start from top
    canvas.drawArc(rect, startAngle, sweepAngle, false, paint);
  }

  @override
  bool shouldRepaint(covariant _ArcPainter oldDelegate) {
    return color != oldDelegate.color ||
        strokeWidth != oldDelegate.strokeWidth ||
        sweepAngle != oldDelegate.sweepAngle;
  }
}
