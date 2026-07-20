import 'dart:async';
import 'dart:io' show Platform;

import 'package:amap_flutter_base/amap_flutter_base.dart';
import 'package:amap_flutter_location/amap_flutter_location.dart';
import 'package:amap_flutter_location/amap_location_option.dart';
import 'package:amap_flutter_map/amap_flutter_map.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart' as geo;
import 'package:permission_handler/permission_handler.dart';

import '../config/amap_config.dart';

/// 实时位置页面（由监控页面点击进入，不作为底部导航 Tab）
/// 使用高德官方 Flutter 插件：amap_flutter_map + amap_flutter_location
class LiveLocationScreen extends StatefulWidget {
  const LiveLocationScreen({super.key});

  @override
  State<LiveLocationScreen> createState() => _LiveLocationScreenState();
}

class _LiveLocationScreenState extends State<LiveLocationScreen> {
  static final LatLng _fallback = LatLng(39.9042, 116.4074);

  AMapController? _mapController;
  LatLng _center = _fallback;
  Set<Marker> _markers = {}; // 缓存 markers，避免每次 build 重建
  Map<String, Object>? _lastLocation;
  String? _error;
  bool _mapReady = false;
  bool _locationStarted = false;
  bool _initialized = false;
  bool _mapAvailable = false; // Key 有效后即为 true，不依赖定位权限
  bool _shouldStartLocation = false; // 地图准备好后需要启动定位
  bool _disposed = false; // 防止 dispose 后异步回调访问已销毁的插件

  AMapFlutterLocation? _locationPlugin;
  StreamSubscription<Map<String, Object>>? _locationSub;

  @override
  void initState() {
    super.initState();
    _initialize();
  }

  Future<void> _initialize() async {
    // ⚠️ 隐私合规必须第一时间设置，不能有任何 await 在前
    // 否则在异步等待期间 SDK 可能因未设置隐私合规而 native crash
    // ⚠️ 地图 SDK 和定位 SDK 的隐私合规必须**同时**初始化，缺一不可。
    AMapFlutterLocation.updatePrivacyAgree(true);
    AMapFlutterLocation.updatePrivacyShow(true, true);
    // 注意：amap_flutter_map 3.0.0 已移除 AMapFlutterMap 静态方法，
    // 地图隐私合规通过 AMapWidget 的 privacyStatement 参数配置（见 build 方法）
    // 先尝试从 AndroidManifest 获取真实 Key
    try {
      await AmapConfig.init();
    } catch (_) {
      // 高德 Key 初始化失败，继续使用 dart-define 的值
    }
    if (!mounted) return;
    if (AmapConfig.keysLookUnset) {
      setState(() {
        _error = '请配置高德 Key';
        _initialized = true;
      });
      return;
    }
    // Key 有效后才设置（避免用无效 Key 污染 SDK 全局状态导致后续闪退）
    AMapFlutterLocation.setApiKey(AmapConfig.androidKey, '');

    // ── Key 有效 → 先显示地图（让用户看到地图瓦片加载） ──
    setState(() {
      _mapAvailable = true; // 地图始终可见，不依赖定位权限
      _initialized = true;
    });

    // ── 第二步：检查定位权限，独立于地图显示 ──
    final ok = await _checkSystemLocation();
    if (!mounted) return;

    // ── 第三步：Android 13+ 需要通知权限（高德持续定位依赖前台服务通知）──
    if (ok) {
      await _ensureNotificationPermission();
    }
    if (!mounted) return;

    if (ok) {
      setState(() {
        _shouldStartLocation = true;
      });
      // 如果 AMapWidget 已经创建完成（onMapCreated 已回调），
      // 立即启动定位；否则等 onMapCreated 中触发
      if (_mapReady) {
        _startLocationSafe();
      }
    }
    // 权限未通过时 _error 已在 _checkSystemLocation 中设置，
    // 但地图仍然可见，让用户可以看到底图。
  }

  void _initAmapLocation() {
    if (_locationStarted || _disposed) return;
    try {
      _locationPlugin = AMapFlutterLocation();
      _locationPlugin!.setLocationOption(AMapLocationOption(
        onceLocation: false,
        needAddress: true,
        locationMode: AMapLocationMode.Hight_Accuracy,
        locationInterval: 5000,
      ));

      _locationSub = _locationPlugin!.onLocationChanged().listen((result) {
        if (!mounted || _disposed) return;
        final errorCode = result['errorCode'] as int?;
        if (errorCode != null && errorCode != 0) {
          setState(() {
            _error = result['errorInfo'] as String? ?? '定位失败 (code: $errorCode)';
          });
          return;
        }
        final lat = result['latitude'] as double?;
        final lng = result['longitude'] as double?;
        if (lat == null || lng == null) return;

        final ll = LatLng(lat, lng);
        setState(() {
          _lastLocation = result;
          _center = ll;
          _error = null;
          _mapAvailable = true; // 定位数据到达，地图可用
          _markers = <Marker>{
            Marker(
              position: ll,
              infoWindowEnable: true,
              infoWindow: const InfoWindow(title: '老人当前位置'),
            ),
          };
        });

        if (_mapReady && _mapController != null) {
          _mapController!.moveCamera(
            CameraUpdate.newLatLngZoom(ll, 16),
            animated: true,
          );
        }
      });

      _locationPlugin!.startLocation();
      _locationStarted = true;
      // 不在此处设置 _mapAvailable = true，等待 onLocationChanged 收到第一条
      // 有效定位数据后再显示地图（此时原生 SDK 一定已完成初始化）
    } catch (e) {
      setState(() {
        _error = '定位初始化失败: $e';
        _mapAvailable = false;
      });
    }
  }

  void _startLocationSafe() {
    if (_locationStarted || _disposed) return;
    try {
      _initAmapLocation();
    } catch (e) {
      if (mounted) {
        setState(() => _error = '定位启动失败: $e');
      }
    }
  }

  Future<void> _retry() async {
    setState(() {
      _error = null;
      _shouldStartLocation = false;
    });

    final ok = await _checkSystemLocation();
    if (!mounted) return;

    if (ok) {
      setState(() {
        _shouldStartLocation = true;
      });
      // 如果 AMapWidget 已经创建完成，立即启动定位
      if (_mapReady) {
        _startLocationSafe();
      }
    }
  }

  Future<bool> _checkSystemLocation() async {
    try {
      final serviceEnabled = await geo.Geolocator.isLocationServiceEnabled();
      if (!serviceEnabled) {
        if (mounted) setState(() => _error = '请打开系统定位服务');
        return false;
      }
      var permission = await geo.Geolocator.checkPermission();
      if (permission == geo.LocationPermission.denied) {
        permission = await geo.Geolocator.requestPermission();
      }
      if (permission == geo.LocationPermission.denied ||
          permission == geo.LocationPermission.deniedForever) {
        if (mounted) setState(() => _error = '需要定位权限以显示实时位置');
        return false;
      }
      return true;
    } catch (e) {
      if (mounted) setState(() => _error = '定位服务异常: $e');
      return false;
    }
  }

  /// Android 13+：请求通知权限。
  /// 高德持续定位依赖前台服务，Android 13 起必须先获得通知授权才能拉起前台服务。
  Future<void> _ensureNotificationPermission() async {
    if (!Platform.isAndroid) return;
    try {
      var status = await Permission.notification.status;
      if (status.isDenied || status.isPermanentlyDenied) {
        status = await Permission.notification.request();
      }
    } catch (_) {
      // notification 权限不影响定位核心功能，静默处理
    }
  }

  @override
  void dispose() {
    _disposed = true;
    if (_locationStarted && _locationPlugin != null) {
      try { _locationPlugin!.stopLocation(); } catch (_) {}
      try { _locationPlugin!.destroy(); } catch (_) {}
    }
    _locationSub?.cancel();
    // amap_flutter_map 3.0.0: AMapController 不再有 dispose 方法
    _mapController = null;
    super.dispose();
  }

  // markers 已缓存为 _markers 成员变量，在 setState 时同步更新

  String? get _coordText {
    final loc = _lastLocation;
    if (loc == null) return null;
    final lat = loc['latitude'] as double?;
    final lng = loc['longitude'] as double?;
    if (lat == null || lng == null) return null;
    return '${lat.toStringAsFixed(6)}, ${lng.toStringAsFixed(6)}';
  }

  String? get _addressText {
    final loc = _lastLocation;
    if (loc == null) return null;
    return loc['address'] as String?;
  }

  String? get _accuracyText {
    final loc = _lastLocation;
    if (loc == null) return null;
    final accuracy = loc['accuracy'] as double?;
    if (accuracy == null) return null;
    return '精度约 ${accuracy.toStringAsFixed(0)} m';
  }

  Widget _buildMapFallback() {
    return Container(
      color: const Color(0xFFF0F7FF),
      child: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.map_outlined, size: 64, color: Colors.grey.shade400),
            const SizedBox(height: 16),
            Text(
              _error ?? '地图暂不可用',
              style: TextStyle(fontSize: 15, color: Colors.grey.shade600),
            ),
            const SizedBox(height: 8),
            Text(
              '请确保已配置高德地图 Key 并授予定位权限',
              style: TextStyle(fontSize: 12, color: Colors.grey.shade400),
            ),
            const SizedBox(height: 20),
            if (_error != null)
              FilledButton.tonal(
                onPressed: () async {
                  setState(() { _error = null; _shouldStartLocation = false; _initialized = false; });
                  await _initialize();
                },
                child: const Text('重试'),
              ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('实时位置'),
        backgroundColor: Colors.white.withValues(alpha: 0.92),
      ),
      body: Stack(
        children: [
          if (!_initialized)
            const Center(child: CircularProgressIndicator())
          else if (AmapConfig.keysLookUnset)
            Positioned.fill(
              child: _buildMapFallback(),
            )
          else
            Positioned.fill(
              child: AMapWidget(
                key: const ValueKey('elderly_live_amap'),
                apiKey: AMapApiKey(
                  androidKey: AmapConfig.androidKey,
                  iosKey: '',
                ),
                privacyStatement: const AMapPrivacyStatement(
                  hasContains: true,
                  hasShow: true,
                  hasAgree: true,
                ),
                initialCameraPosition: CameraPosition(
                  target: _fallback,
                  zoom: 11,
                ),
                markers: _markers,
                compassEnabled: true,
                scaleEnabled: true,
                onMapCreated: (c) {
                  _mapController = c;
                  _mapReady = true;
                  // 如果初始化流程已通过权限检查，但地图刚创建完成，
                  // 此时才启动定位（避免在 AMapWidget 未就绪时启动）
                  if (_shouldStartLocation && !_locationStarted) {
                    _startLocationSafe();
                  }
                  if (_lastLocation != null) {
                    final lat = _lastLocation!['latitude'] as double?;
                    final lng = _lastLocation!['longitude'] as double?;
                    if (lat != null && lng != null) {
                      c.moveCamera(
                        CameraUpdate.newLatLngZoom(LatLng(lat, lng), 16),
                        animated: false,
                      );
                    }
                  }
                },
              ),
            ),
          if (AmapConfig.keysLookUnset)
            Positioned(
              top: 8,
              left: 12,
              right: 12,
              child: Material(
                color: Colors.amber.shade50,
                borderRadius: BorderRadius.circular(12),
                child: Padding(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
                  child: Text(
                    '请配置高德 Key：使用 flutter run '
                    '--dart-define=AMAP_ANDROID_KEY=… '
                    '或修改 lib/config/amap_config.dart。',
                    style: TextStyle(
                      color: Colors.amber.shade900,
                      fontSize: 12,
                    ),
                  ),
                ),
              ),
            ),
          if (_error != null && _mapAvailable)
            Positioned(
              top: 8,
              left: 12,
              right: 12,
              child: Material(
                color: Colors.orange.shade50,
                borderRadius: BorderRadius.circular(12),
                elevation: 3,
                child: Padding(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
                  child: Row(
                    children: [
                      Icon(Icons.warning_amber_rounded,
                          size: 18, color: Colors.orange.shade700),
                      const SizedBox(width: 8),
                      Expanded(
                        child: Text(
                          _error!,
                          style: TextStyle(
                            color: Colors.orange.shade900,
                            fontSize: 13,
                          ),
                        ),
                      ),
                      TextButton(
                        onPressed: _retry,
                        child: const Text('重试', style: TextStyle(fontSize: 13)),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          if (_mapAvailable)
            Positioned(
              left: 16,
              right: 16,
              bottom: 24,
              child: ClipRRect(
                borderRadius: BorderRadius.circular(18),
                child: Material(
                  color: Colors.white.withValues(alpha: 0.92),
                  elevation: 8,
                  shadowColor: Colors.black26,
                  child: Padding(
                    padding: const EdgeInsets.fromLTRB(18, 14, 18, 16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Text(
                          '坐标（设备 GPS）',
                          style: Theme.of(context).textTheme.labelMedium?.copyWith(
                                color: Colors.black54,
                                letterSpacing: 0.3,
                              ),
                        ),
                        const SizedBox(height: 6),
                        Text(
                          _coordText ?? '定位中…',
                          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                                fontWeight: FontWeight.w600,
                              ),
                        ),
                        if (_addressText != null) ...[
                          const SizedBox(height: 4),
                          Text(
                            _addressText!,
                            style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                  color: Colors.black45,
                                ),
                          ),
                        ],
                        if (_accuracyText != null) ...[
                          const SizedBox(height: 4),
                          Text(
                            '$_accuracyText · 每 5 秒自动更新 · 底图：高德',
                            style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                  color: Colors.black45,
                                ),
                          ),
                        ],
                      ],
                    ),
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }
}
