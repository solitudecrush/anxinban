enum AlertTypeCode {
  heartRateHigh('HEART_RATE_HIGH', '心率过高'),
  heartRateLow('HEART_RATE_LOW', '心率过低'),
  temperatureHigh('TEMPERATURE_HIGH', '体温过高'),
  temperatureLow('TEMPERATURE_LOW', '体温过低'),
  pressureHigh('PRESSURE_HIGH', '血压过高'),
  pressureLow('PRESSURE_LOW', '血压过低');

  const AlertTypeCode(this.apiValue, this.labelZh);

  final String apiValue;
  final String labelZh;

  static AlertTypeCode fromApi(String raw) {
    return AlertTypeCode.values.firstWhere(
      (e) => e.apiValue == raw,
      orElse: () => AlertTypeCode.heartRateHigh,
    );
  }
}

class AlertItem {
  AlertItem({
    required this.id,
    required this.type,
    required this.detail,
    required this.occurredAt,
  });

  final String id;
  final AlertTypeCode type;
  final String detail;
  final DateTime occurredAt;

  factory AlertItem.fromJson(Map<String, dynamic> json) {
    return AlertItem(
      id: json['id'] as String,
      type: AlertTypeCode.fromApi(json['type'] as String),
      detail: json['detail'] as String,
      occurredAt: DateTime.parse(json['occurredAt'] as String).toLocal(),
    );
  }
}
