class LatestVitals {
  LatestVitals({
    required this.temperature,
    required this.heartRate,
    required this.systolic,
    required this.diastolic,
    required this.measuredAt,
  });

  final double temperature;
  final int heartRate;
  final int systolic;
  final int diastolic;
  final DateTime measuredAt;

  factory LatestVitals.fromJson(Map<String, dynamic> json) {
    return LatestVitals(
      temperature: (json['temperature'] as num).toDouble(),
      heartRate: (json['heartRate'] as num).toInt(),
      systolic: (json['systolic'] as num).toInt(),
      diastolic: (json['diastolic'] as num).toInt(),
      measuredAt: DateTime.parse(json['measuredAt'] as String).toLocal(),
    );
  }
}
