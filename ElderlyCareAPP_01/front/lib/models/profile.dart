class Profile {
  const Profile({
    required this.name,
    required this.age,
    required this.gender,
    required this.familyPhone,
    required this.address,
  });

  final String name;
  final int age;
  final String gender;
  final String familyPhone;
  final String address;

  factory Profile.fromJson(Map<String, dynamic> json) {
    return Profile(
      name: json['name'] as String,
      age: (json['age'] as num).toInt(),
      gender: json['gender'] as String,
      familyPhone: json['familyPhone'] as String,
      address: json['address'] as String,
    );
  }
}
