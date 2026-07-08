class ServiceRequest {
  ServiceRequest({
    required this.id,
    required this.type,
    required this.elderName,
    required this.content,
    required this.requestTime,
    this.status = 'pending',
    this.convertedTo,
  });

  final int id;
  final String type; // 上门看护、设备维修、健康咨询、紧急求助、生活物资代购
  final String elderName;
  final String content;
  final DateTime requestTime;
  String status; // pending, converted, completed, ignored
  final String? convertedTo;

  factory ServiceRequest.fromJson(Map<String, dynamic> json) {
    return ServiceRequest(
      id: (json['id'] as num).toInt(),
      type: json['type'] as String,
      elderName: json['elderName'] as String,
      content: json['content'] as String,
      requestTime: DateTime.parse(json['requestTime'] as String).toLocal(),
      status: json['status'] as String? ?? 'pending',
      convertedTo: json['convertedTo'] as String?,
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'type': type,
    'elderName': elderName,
    'content': content,
    'requestTime': requestTime.toIso8601String(),
    'status': status,
    'convertedTo': convertedTo,
  };
}
