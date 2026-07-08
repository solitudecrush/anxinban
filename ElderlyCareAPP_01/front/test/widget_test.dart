import 'package:flutter_test/flutter_test.dart';

import 'package:elderly_care_app/main.dart';

void main() {
  testWidgets('App builds', (WidgetTester tester) async {
    await tester.pumpWidget(const ElderlyCareApp());
    expect(find.text('健康助手'), findsOneWidget);
  });
}
