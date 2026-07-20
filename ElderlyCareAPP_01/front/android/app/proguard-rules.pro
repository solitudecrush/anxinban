# androidx.window — 折叠屏/大屏设备专用 API，普通手机不存在，忽略缺失警告
-dontwarn androidx.window.**
-keep class androidx.window.** { *; }

# ========== 高德地图 SDK（amap_flutter_map + amap_flutter_location）==========
# 保留高德 3D 地图 SDK
-keep class com.amap.api.** { *; }
-keep class com.autonavi.** { *; }
-keep class com.loc.** { *; }
-dontwarn com.amap.api.**
-dontwarn com.autonavi.**
-dontwarn com.loc.**
-dontwarn com.google.android.gms.**

# 保留高德 Flutter 插件（MethodChannel 回调需要的类）
-keep class com.amap.flutter.** { *; }
-dontwarn com.amap.flutter.**

# 保留 JNI 相关（高德 SDK 内部 Native 调用）
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留 Flutter MethodChannel 回调（定位数据回调）
-keep class io.flutter.plugin.common.MethodChannel* { *; }
-keep class io.flutter.plugin.common.EventChannel* { *; }
