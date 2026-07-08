import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';

import '../services/api_service.dart';

/// 用户头像组件。
///
/// 使用 [CachedNetworkImage] 加载远程头像，自动缓存到本地，
/// 加载失败或未设置头像时显示默认占位图标。
///
/// 使用示例：
/// ```dart
/// AvatarWidget(
///   avatarUrl: '/uploads/avatars/avatar-abc123.jpg',
///   radius: 40,
///   onTap: () => _showAvatarPicker(),
/// )
/// ```
class AvatarWidget extends StatelessWidget {
  const AvatarWidget({
    super.key,
    this.avatarUrl,
    this.radius = 40,
    this.onTap,
    this.showBadge = false,
  });

  /// 头像相对路径（如 /uploads/avatars/avatar-xxx.jpg），为 null 时显示默认头像
  final String? avatarUrl;

  /// 头像半径
  final double radius;

  /// 点击回调
  final VoidCallback? onTap;

  /// 是否显示编辑角标（相机图标）
  final bool showBadge;

  @override
  Widget build(BuildContext context) {
    final fullUrl = ApiService.avatarFullUrl(avatarUrl);
    final diameter = radius * 2;

    Widget avatar = CircleAvatar(
      radius: radius,
      backgroundColor: const Color(0xFFE2E8F0),
      child: ClipOval(
        child: CachedNetworkImage(
          imageUrl: fullUrl,
          width: diameter,
          height: diameter,
          fit: BoxFit.cover,
          placeholder: (context, url) => _buildPlaceholder(radius),
          errorWidget: (context, url, error) => _buildPlaceholder(radius),
          memCacheWidth: (diameter * 1.5).round(),
          memCacheHeight: (diameter * 1.5).round(),
        ),
      ),
    );

    if (onTap != null) {
      avatar = GestureDetector(
        onTap: onTap,
        child: avatar,
      );
    }

    if (showBadge) {
      return Stack(
        clipBehavior: Clip.none,
        children: [
          avatar,
          Positioned(
            right: -2,
            bottom: -2,
            child: GestureDetector(
              onTap: onTap,
              child: Container(
                width: radius * 0.7,
                height: radius * 0.7,
                decoration: BoxDecoration(
                  color: const Color(0xFF3182CE),
                  shape: BoxShape.circle,
                  border: Border.all(color: Colors.white, width: 2),
                ),
                child: Icon(
                  Icons.camera_alt,
                  size: radius * 0.4,
                  color: Colors.white,
                ),
              ),
            ),
          ),
        ],
      );
    }

    return avatar;
  }

  Widget _buildPlaceholder(double radius) {
    return Container(
      color: const Color(0xFFE2E8F0),
      child: Icon(
        Icons.person,
        size: radius * 1.2,
        color: const Color(0xFFA0AEC0),
      ),
    );
  }
}
