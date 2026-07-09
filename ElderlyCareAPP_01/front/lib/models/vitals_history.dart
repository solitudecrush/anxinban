class VitalsHistoryPoint {
  VitalsHistoryPoint({
    required this.label,
    this.temperature,
    this.heartRate,
    this.systolic,
    this.diastolic,
    this.bloodOxygen,
  });

  final String label;
  final double? temperature;
  final int? heartRate;
  final int? systolic;
  final int? diastolic;
  final int? bloodOxygen;

  factory VitalsHistoryPoint.fromJson(Map<String, dynamic> json) {
    return VitalsHistoryPoint(
      label: json['label'] as String,
      temperature: (json['temperature'] as num?)?.toDouble(),
      heartRate: (json['heartRate'] as num?)?.toInt(),
      systolic: (json['systolic'] as num?)?.toInt(),
      diastolic: (json['diastolic'] as num?)?.toInt(),
      bloodOxygen: (json['bloodOxygen'] as num?)?.toInt(),
    );
  }
}

class VitalsHistory {
  VitalsHistory({required this.period, required this.points});

  final String period;
  final List<VitalsHistoryPoint> points;

  factory VitalsHistory.fromJson(Map<String, dynamic> json) {
    final list = (json['points'] as List<dynamic>)
        .map((e) => VitalsHistoryPoint.fromJson(e as Map<String, dynamic>))
        .toList();
    return VitalsHistory(period: json['period'] as String, points: list);
  }
}
