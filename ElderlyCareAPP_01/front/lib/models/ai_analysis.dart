class AiAnalysis {
  AiAnalysis({
    required this.summary,
    required this.suggestion,
    required this.fromLlm,
  });

  final String summary;
  final String suggestion;
  final bool fromLlm;

  factory AiAnalysis.fromJson(Map<String, dynamic> json) {
    return AiAnalysis(
      summary: json['summary'] as String,
      suggestion: json['suggestion'] as String,
      fromLlm: json['fromLlm'] as bool? ?? false,
    );
  }
}
