import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_image_compress/flutter_image_compress.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';

import '../services/api_service.dart';

/// 头像选择器 - 底部弹出菜单，支持拍照和相册选择。
///
/// 使用示例：
/// ```dart
/// final url = await showAvatarPicker(context, userId: currentUserId);
/// if (url != null && mounted) setState(() => _avatarUrl = url);
/// ```
///
/// 返回上传后的头像相对路径，用户取消时返回 null。
Future<String?> showAvatarPicker(
  BuildContext context, {
  required String userId,
  String role = 'family',
}) async {
  return showModalBottomSheet<String>(
    context: context,
    shape: const RoundedRectangleBorder(
      borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
    ),
    builder: (sheetContext) => _AvatarPickerSheet(
      userId: userId,
      role: role,
    ),
  );
}

class _AvatarPickerSheet extends StatefulWidget {
  const _AvatarPickerSheet({required this.userId, required this.role});

  final String userId;
  final String role;

  @override
  State<_AvatarPickerSheet> createState() => _AvatarPickerSheetState();
}

class _AvatarPickerSheetState extends State<_AvatarPickerSheet> {
  final _picker = ImagePicker();
  bool _uploading = false;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.fromLTRB(0, 12, 0, 16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // 拖拽指示条
            Container(
              width: 36,
              height: 4,
              decoration: BoxDecoration(
                color: const Color(0xFFCBD5E0),
                borderRadius: BorderRadius.circular(2),
              ),
            ),
            const SizedBox(height: 20),
            const Text(
              '更换头像',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
            ),
            const SizedBox(height: 20),
            if (_uploading)
              const Padding(
                padding: EdgeInsets.all(32),
                child: Column(
                  children: [
                    CircularProgressIndicator(),
                    SizedBox(height: 12),
                    Text('正在上传...', style: TextStyle(color: Color(0xFF718096))),
                  ],
                ),
              )
            else
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  _buildOption(
                    icon: Icons.camera_alt,
                    label: '拍照',
                    color: const Color(0xFF3182CE),
                    onTap: () => _pickAndUpload(ImageSource.camera),
                  ),
                  _buildOption(
                    icon: Icons.photo_library,
                    label: '相册',
                    color: const Color(0xFF38A169),
                    onTap: () => _pickAndUpload(ImageSource.gallery),
                  ),
                  _buildOption(
                    icon: Icons.delete_outline,
                    label: '移除',
                    color: const Color(0xFFE53E3E),
                    onTap: _removeAvatar,
                  ),
                ],
              ),
            const SizedBox(height: 12),
          ],
        ),
      ),
    );
  }

  Widget _buildOption({
    required IconData icon,
    required String label,
    required Color color,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: _uploading ? null : onTap,
      child: Column(
        children: [
          Container(
            width: 56,
            height: 56,
            decoration: BoxDecoration(
              color: color.withValues(alpha: 0.1),
              shape: BoxShape.circle,
            ),
            child: Icon(icon, color: color, size: 26),
          ),
          const SizedBox(height: 8),
          Text(label, style: const TextStyle(fontSize: 13, color: Color(0xFF4A5568))),
        ],
      ),
    );
  }

  Future<void> _pickAndUpload(ImageSource source) async {
    try {
      final picked = await _picker.pickImage(
        source: source,
        maxWidth: 800,
        maxHeight: 800,
        imageQuality: 85,
      );
      if (picked == null) return;

      setState(() => _uploading = true);

      // 压缩图片
      File compressed = await _compressImage(File(picked.path));

      // 上传
      final api = context.read<ApiService>();
      final url = await api.uploadAvatar(
        compressed.path,
        widget.userId,
        role: widget.role,
      );

      if (mounted) {
        Navigator.of(context).pop(url);
      }
    } catch (e) {
      if (mounted) {
        setState(() => _uploading = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('上传失败: $e')),
        );
      }
    }
  }

  Future<void> _removeAvatar() async {
    setState(() => _uploading = true);
    try {
      final api = context.read<ApiService>();
      final url = await api.deleteAvatar(widget.userId, role: widget.role);
      if (mounted) {
        Navigator.of(context).pop(url);
      }
    } catch (e) {
      if (mounted) {
        setState(() => _uploading = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('操作失败: $e')),
        );
      }
    }
  }

  /// 压缩图片到 400x400 以内，质量 80%
  Future<File> _compressImage(File file) async {
    try {
      final result = await FlutterImageCompress.compressAndGetFile(
        file.absolute.path,
        '${file.absolute.path}_compressed.jpg',
        minWidth: 400,
        minHeight: 400,
        quality: 80,
        format: CompressFormat.jpeg,
      );
      if (result != null) {
        return File(result.path);
      }
    } catch (_) {
      // 压缩失败则使用原图
    }
    return file;
  }
}
