class CameraRequest {
  CameraRequest({
    required this.id,
    required this.elderName,
    required this.staffName,
    required this.staffPhone,
    required this.reason,
    required this.requestTime,
    this.status = 'pending',
    this.expiresAt,
    this.approvedAt,
  });

  final String id;
  final String elderName;
  final String staffName;
  final String staffPhone;
  final String reason;
  final DateTime requestTime;
  String status; // pending, approved, rejected
  final DateTime? expiresAt;
  final DateTime? approvedAt;

  factory CameraRequest.fromJson(Map<String, dynamic> json) {
    return CameraRequest(
      id: json['id'] as String,
      elderName: json['elderName'] as String,
      staffName: json['staffName'] as String,
      staffPhone: json['staffPhone'] as String,
      reason: json['reason'] as String,
      requestTime: DateTime.parse(json['requestTime'] as String).toLocal(),
      status: json['status'] as String? ?? 'pending',
      expiresAt: json['expiresAt'] != null
          ? DateTime.parse(json['expiresAt'] as String).toLocal()
          : null,
      approvedAt: json['approvedAt'] != null
          ? DateTime.parse(json['approvedAt'] as String).toLocal()
          : null,
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'elderName': elderName,
    'staffName': staffName,
    'staffPhone': staffPhone,
    'reason': reason,
    'requestTime': requestTime.toIso8601String(),
    'status': status,
    'expiresAt': expiresAt?.toIso8601String(),
    'approvedAt': approvedAt?.toIso8601String(),
  };
}
