import 'package:flutter/material.dart';
import '../models/ai_analysis.dart';

/// 情绪状态横幅 — 页面最醒目的元素。
///
/// 根据情绪等级展示不同渐变背景和图标，让用户一眼看到老人的情绪状态。
class EmotionBanner extends StatelessWidget {
  const EmotionBanner({super.key, required this.emotion});

  final EmotionAnalysis emotion;

  // ──── 按情绪等级的颜色配置 ────

  Color get _levelColor {
    return switch (emotion.emotionLevel) {
      EmotionLevel.normal => const Color(0xFF34C759),
      EmotionLevel.low => const Color(0xFF4A90E2),
      EmotionLevel.medium => const Color(0xFFFF9500),
      EmotionLevel.high => const Color(0xFFFF3B30),
    };
  }

  LinearGradient get _gradient {
    return switch (emotion.emotionLevel) {
      EmotionLevel.normal => const LinearGradient(
          colors: [Color(0xFF34C759), Color(0xFF2EA84C)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
      EmotionLevel.low => const LinearGradient(
          colors: [Color(0xFF4A90E2), Color(0xFF3A7BC8)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
      EmotionLevel.medium => const LinearGradient(
          colors: [Color(0xFFFF9500), Color(0xFFE68A00)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
      EmotionLevel.high => const LinearGradient(
          colors: [Color(0xFFFF3B30), Color(0xFFD63031)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
    };
  }

  IconData get _levelIcon {
    return switch (emotion.emotionLevel) {
      EmotionLevel.normal => Icons.sentiment_satisfied_alt,
      EmotionLevel.low => Icons.sentiment_neutral,
      EmotionLevel.medium => Icons.sentiment_dissatisfied,
      EmotionLevel.high => Icons.sentiment_very_dissatisfied,
    };
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: _gradient,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: _levelColor.withValues(alpha: 0.35),
            blurRadius: 12,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // ── 顶部：大图标 + 情绪标签 ──
          Row(
            children: [
              // 大图标
              Container(
                width: 72,
                height: 72,
                decoration: BoxDecoration(
                  color: Colors.white.withValues(alpha: 0.2),
                  shape: BoxShape.circle,
                ),
                child: Icon(_levelIcon, size: 44, color: Colors.white),
              ),
              const SizedBox(width: 16),
              // 情绪文字
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      emotion.emotionState,
                      style: const TextStyle(
                        fontSize: 28,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '焦虑评分：${emotion.anxietyScore}',
                      style: TextStyle(
                        fontSize: 16,
                        color: Colors.white.withValues(alpha: 0.85),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),

          const SizedBox(height: 16),

          // ── 焦虑评分进度条 ──
          Row(
            children: [
              const Text(
                '焦虑评分',
                style: TextStyle(
                  fontSize: 12,
                  color: Colors.white70,
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(4),
                  child: LinearProgressIndicator(
                    value: emotion.anxietyScore / 100.0,
                    minHeight: 8,
                    backgroundColor: Colors.white.withValues(alpha: 0.25),
                    valueColor: const AlwaysStoppedAnimation<Color>(Colors.white),
                  ),
                ),
              ),
              const SizedBox(width: 8),
              Text(
                '${emotion.anxietyScore}',
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w700,
                  color: Colors.white,
                ),
              ),
            ],
          ),

          // ── 结论文字 ──
          if (emotion.conclusion.isNotEmpty) ...[
            const SizedBox(height: 12),
            Text(
              emotion.conclusion,
              style: TextStyle(
                fontSize: 14,
                height: 1.4,
                color: Colors.white.withValues(alpha: 0.9),
              ),
              maxLines: 3,
              overflow: TextOverflow.ellipsis,
            ),
          ],
        ],
      ),
    );
  }
}
