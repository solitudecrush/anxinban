import 'package:flutter/material.dart';
import '../models/ai_analysis.dart';

/// 音乐舒缓推荐卡片。
///
/// 当检测到老人焦虑（中度或重度）时，在情绪横幅下方展示。
/// 提供一键播放舒缓音乐的按钮，通过后端 API 触发音乐干预。
class MusicRecommendationCard extends StatelessWidget {
  const MusicRecommendationCard({
    super.key,
    required this.emotion,
    this.onPlayMusic,
    this.loading = false,
  });

  final EmotionAnalysis emotion;
  final VoidCallback? onPlayMusic;
  final bool loading;

  /// 从情绪分析的建议中提取音乐相关建议
  String get _musicDescription {
    // 优先使用后端返回的音乐相关建议
    for (final s in emotion.suggestions) {
      if (emotion.hasMusicSuggestion &&
          (s.contains('音乐') || s.contains('戏曲') || s.contains('舒缓'))) {
        return s;
      }
    }
    // Fallback 默认文案
    return '检测到${emotion.emotionState}情绪，音乐疗法可有效舒缓身心。'
        '建议播放舒缓音乐或老人喜爱的戏曲，帮助放松情绪。';
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFF5856D6), Color(0xFF7B68EE)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: const Color(0xFF5856D6).withValues(alpha: 0.3),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // ── 标题行 ──
          Row(
            children: [
              Container(
                width: 36,
                height: 36,
                decoration: BoxDecoration(
                  color: Colors.white.withValues(alpha: 0.2),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: const Icon(
                  Icons.music_note,
                  size: 22,
                  color: Colors.white,
                ),
              ),
              const SizedBox(width: 12),
              const Expanded(
                child: Text(
                  '音乐舒缓推荐',
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
              ),
              // 情绪触发标签
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                decoration: BoxDecoration(
                  color: Colors.white.withValues(alpha: 0.2),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  '${emotion.emotionState}触发',
                  style: const TextStyle(
                    fontSize: 12,
                    color: Colors.white,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
            ],
          ),

          const SizedBox(height: 12),

          // ── 描述文字 ──
          Text(
            _musicDescription,
            style: TextStyle(
              fontSize: 14,
              height: 1.5,
              color: Colors.white.withValues(alpha: 0.9),
            ),
          ),

          const SizedBox(height: 16),

          // ── 播放按钮 ──
          SizedBox(
            width: double.infinity,
            height: 48,
            child: FilledButton.icon(
              onPressed: loading ? null : onPlayMusic,
              icon: loading
                  ? const SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(
                        strokeWidth: 2,
                        color: Color(0xFF5856D6),
                      ),
                    )
                  : const Icon(Icons.play_circle_filled, size: 24),
              label: Text(
                loading ? '正在发送请求…' : '播放舒缓音乐',
                style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
              ),
              style: FilledButton.styleFrom(
                backgroundColor: Colors.white,
                foregroundColor: const Color(0xFF5856D6),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(14),
                ),
                elevation: 0,
              ),
            ),
          ),

          // ── 底部提示 ──
          const SizedBox(height: 8),
          Center(
            child: Text(
              '将通过智能音箱为老人播放音乐',
              style: TextStyle(
                fontSize: 11,
                color: Colors.white.withValues(alpha: 0.6),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
