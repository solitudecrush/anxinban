import 'package:flutter/material.dart';

import '../models/health_record.dart';
import '../services/health_record_store.dart';

class HealthRecordScreen extends StatefulWidget {
  const HealthRecordScreen({super.key});

  @override
  State<HealthRecordScreen> createState() => _HealthRecordScreenState();
}

class _HealthRecordScreenState extends State<HealthRecordScreen> {
  bool _saving = false;
  bool _initialized = false;

  final _hospitalizationsCtrl = TextEditingController();
  final _medicalHistoryCtrl = TextEditingController();
  final _allergiesCtrl = TextEditingController();
  final _medicationsCtrl = TextEditingController();
  final _bloodTypeCtrl = TextEditingController();
  final _remarksCtrl = TextEditingController();

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    var record = await HealthRecordStore.load();
    // 如果全部为空，填充模拟数据
    if (record.hospitalizations.isEmpty &&
        record.medicalHistory.isEmpty &&
        record.allergies.isEmpty) {
      record = HealthRecord(
        hospitalizations: '2019年3月：因肺炎住院一周\n2021年8月：摔倒导致髋骨骨折，手术治疗',
        medicalHistory: '高血压（10年）\n2型糖尿病（5年）\n骨质疏松',
        allergies: '青霉素过敏\n花粉过敏（季节性）',
        medications: '阿司匹林肠溶片 100mg/日\n二甲双胍 500mg 每日两次\n钙片 600mg/日',
        bloodType: 'O型',
        remarks: '听力轻度下降，需大声交流。记忆力有所减退。',
      );
      await HealthRecordStore.save(record);
    }
    if (!mounted) return;
    setState(() => _initialized = true);
    _hospitalizationsCtrl.text = record.hospitalizations;
    _medicalHistoryCtrl.text = record.medicalHistory;
    _allergiesCtrl.text = record.allergies;
    _medicationsCtrl.text = record.medications;
    _bloodTypeCtrl.text = record.bloodType;
    _remarksCtrl.text = record.remarks;
  }

  Future<void> _save() async {
    setState(() => _saving = true);
    final record = HealthRecord(
      hospitalizations: _hospitalizationsCtrl.text.trim(),
      medicalHistory: _medicalHistoryCtrl.text.trim(),
      allergies: _allergiesCtrl.text.trim(),
      medications: _medicationsCtrl.text.trim(),
      bloodType: _bloodTypeCtrl.text.trim(),
      remarks: _remarksCtrl.text.trim(),
    );
    await HealthRecordStore.save(record);
    if (!mounted) return;
    setState(() => _saving = false);
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('健康档案已保存')),
    );
  }

  Widget _buildField(String label, TextEditingController controller,
      {int maxLines = 1}) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: TextField(
        controller: controller,
        maxLines: maxLines,
        decoration: InputDecoration(
          labelText: label,
          alignLabelWithHint: true,
          border: const OutlineInputBorder(),
        ),
      ),
    );
  }

  @override
  void dispose() {
    _hospitalizationsCtrl.dispose();
    _medicalHistoryCtrl.dispose();
    _allergiesCtrl.dispose();
    _medicationsCtrl.dispose();
    _bloodTypeCtrl.dispose();
    _remarksCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('健康档案'),
        actions: [
          if (_saving)
            const Padding(
              padding: EdgeInsets.only(right: 16),
              child: Center(
                child: SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(strokeWidth: 2),
                ),
              ),
            )
          else
            TextButton(
              onPressed: _save,
              child: const Text('保存'),
            ),
        ],
      ),
      body: !_initialized
          ? const Center(child: CircularProgressIndicator())
          : ListView(
              padding: const EdgeInsets.all(16),
              children: [
                _buildField('住院信息', _hospitalizationsCtrl, maxLines: 2),
                _buildField('既往病史', _medicalHistoryCtrl, maxLines: 3),
                _buildField('过敏史', _allergiesCtrl, maxLines: 2),
                _buildField('常用药物', _medicationsCtrl, maxLines: 2),
                _buildField('血型', _bloodTypeCtrl),
                _buildField('备注', _remarksCtrl, maxLines: 3),
              ],
            ),
    );
  }
}
