import 'dart:async';

import 'package:amap_flutter_base/amap_flutter_base.dart';
import 'package:amap_flutter_location/amap_flutter_location.dart';
import 'package:amap_flutter_location/amap_location_option.dart';
import 'package:amap_flutter_map/amap_flutter_map.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart' as geo;

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
  Map<String, Object>? _lastLocation;
  String? _error;
  bool _mapReady = false;
  bool _locationStarted = false;
  bool _initialized = false;

  late AMapFlutterLocation _locationPlugin;
  StreamSubscription<Map<String, Object>>? _locationSub;

  @override
  void initState() {
    super.initState();
    _initialize();
  }

  Future<void> _initialize() async {
    await AmapConfig.init();
    if (!mounted) return;
    if (AmapConfig.keysLookUnset) {
      setState(() {
        _error = '请配置高德 Key';
        _initialized = true;
      });
      return;
    }
    _initAmapLocation();
    _checkSystemLocation();
    if (mounted) {
      setState(() => _initialized = true);
    }
  }

  void _initAmapLocation() {
    // 高德隐私合规声明
    AMapFlutterLocation.updatePrivacyAgree(true);
    AMapFlutterLocation.updatePrivacyShow(true, true);
    // 设置 API Key
    AMapFlutterLocation.setApiKey(AmapConfig.androidKey, '');

    _locationPlugin = AMapFlutterLocation();
    _locationPlugin.setLocationOption(AMapLocationOption(
      onceLocation: false,
      needAddress: true,
      locationMode: AMapLocationMode.Hight_Accuracy,
      locationInterval: 5000,
    ));

    _locationSub = _locationPlugin.onLocationChanged().listen((result) {
      if (!mounted) return;
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
      });

      if (_mapReady && _mapController != null) {
        _mapController!.moveCamera(
          CameraUpdate.newLatLngZoom(ll, 16),
          animated: true,
        );
      }
    });

    _locationPlugin.startLocation();
    _locationStarted = true;
  }

  Future<void> _checkSystemLocation() async {
    final serviceEnabled = await geo.Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      setState(() => _error = '请打开系统定位服务');
      return;
    }
    var permission = await geo.Geolocator.checkPermission();
    if (permission == geo.LocationPermission.denied) {
      permission = await geo.Geolocator.requestPermission();
    }
    if (permission == geo.LocationPermission.denied ||
        permission == geo.LocationPermission.deniedForever) {
      setState(() => _error = '需要定位权限以显示实时位置');
      return;
    }
  }

  @override
  void dispose() {
    if (_locationStarted) {
      _locationPlugin.stopLocation();
      _locationPlugin.destroy();
    }
    _locationSub?.cancel();
    _mapController?.disponse();
    _mapController = null;
    super.dispose();
  }

  Set<Marker> get _markers => <Marker>{
        Marker(
          position: _center,
          infoWindowEnable: true,
          infoWindow: const InfoWindow(title: '老人当前位置'),
        ),
      };

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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('实时位置'),
        backgroundColor: Colors.white.withValues(alpha: 0.92),
      ),
      body: Stack(
        children: [
          if (!_initialized || AmapConfig.keysLookUnset)
            const SizedBox.shrink()
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
          if (_error != null && _lastLocation == null)
            Center(
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Card(
                  child: Padding(
                    padding: const EdgeInsets.all(20),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(
                          Icons.location_off_outlined,
                          size: 40,
                          color: Colors.grey.shade600,
                        ),
                        const SizedBox(height: 12),
                        Text(
                          _error!,
                          textAlign: TextAlign.center,
                          style: Theme.of(context).textTheme.bodyLarge,
                        ),
                        const SizedBox(height: 16),
                        FilledButton(
                          onPressed: _checkSystemLocation,
                          child: const Text('重试'),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
          if (!(_error != null && _lastLocation == null))
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
