class HealthRecord {
  HealthRecord({
    this.hospitalizations = '',
    this.medicalHistory = '',
    this.allergies = '',
    this.medications = '',
    this.bloodType = '',
    this.remarks = '',
  });

  final String hospitalizations;
  final String medicalHistory;
  final String allergies;
  final String medications;
  final String bloodType;
  final String remarks;

  Map<String, dynamic> toJson() => {
        'hospitalizations': hospitalizations,
        'medicalHistory': medicalHistory,
        'allergies': allergies,
        'medications': medications,
        'bloodType': bloodType,
        'remarks': remarks,
      };

  factory HealthRecord.fromJson(Map<String, dynamic> json) {
    return HealthRecord(
      hospitalizations: json['hospitalizations'] as String? ?? '',
      medicalHistory: json['medicalHistory'] as String? ?? '',
      allergies: json['allergies'] as String? ?? '',
      medications: json['medications'] as String? ?? '',
      bloodType: json['bloodType'] as String? ?? '',
      remarks: json['remarks'] as String? ?? '',
    );
  }

  HealthRecord copyWith({
    String? hospitalizations,
    String? medicalHistory,
    String? allergies,
    String? medications,
    String? bloodType,
    String? remarks,
  }) {
    return HealthRecord(
      hospitalizations: hospitalizations ?? this.hospitalizations,
      medicalHistory: medicalHistory ?? this.medicalHistory,
      allergies: allergies ?? this.allergies,
      medications: medications ?? this.medications,
      bloodType: bloodType ?? this.bloodType,
      remarks: remarks ?? this.remarks,
    );
  }
}
