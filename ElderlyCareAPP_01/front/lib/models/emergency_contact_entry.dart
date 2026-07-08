class EmergencyContactEntry {
  EmergencyContactEntry({
    required this.id,
    required this.name,
    required this.phone,
  });

  final String id;
  final String name;
  final String phone;

  Map<String, dynamic> toJson() => {
        'id': id,
        'name': name,
        'phone': phone,
      };

  factory EmergencyContactEntry.fromJson(Map<String, dynamic> json) {
    return EmergencyContactEntry(
      id: json['id'] as String,
      name: json['name'] as String? ?? '',
      phone: json['phone'] as String? ?? '',
    );
  }

  EmergencyContactEntry copyWith({String? name, String? phone}) {
    return EmergencyContactEntry(
      id: id,
      name: name ?? this.name,
      phone: phone ?? this.phone,
    );
  }
}
