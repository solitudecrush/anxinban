import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

// ========== Release 签名配置 ==========
// 从 key.properties 读取签名信息（不提交 Git）
val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("app/key.properties")
if (keystorePropertiesFile.exists()) {
    keystorePropertiesFile.inputStream().use { keystoreProperties.load(it) }
}

android {
    namespace = "com.elderlycare.elderly_care_app"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    defaultConfig {
        // TODO: Specify your own unique Application ID (https://developer.android.com/studio/build/application-id.html).
        applicationId = "com.elderlycare.elderly_care_app"
        // You can update the following values to match your application needs.
        // For more information, see: https://flutter.dev/to/review-gradle-config.
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName

        // 高德地图 SDK 仅提供 arm 架构 so 库，过滤掉 x86 可避免模拟器 UnsatisfiedLinkError
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }

        // 高德地图 Android Key：从 local.properties 读取，避免硬编码到 Git 仓库
        val localProps = Properties()
        val localPropsFile = rootProject.file("local.properties")
        if (localPropsFile.exists()) {
            localPropsFile.inputStream().use { localProps.load(it) }
        }
        val amapAndroidKey = localProps.getProperty("amap.android.key", "YOUR_AMAP_ANDROID_KEY")
        manifestPlaceholders["AMAP_ANDROID_KEY"] = amapAndroidKey
    }

    buildTypes {
        release {
            // Release 签名：优先使用 key.properties 中的签名，不存在则回退到 debug 签名
            signingConfig = if (keystorePropertiesFile.exists()) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
            // R8 混淆规则（忽略可选 API 缺失类，如 androidx.window 折叠屏 API）
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    // 高德官方插件使用 compileOnly 依赖 SDK，需要宿主 App 显式引入 implementation
    // 否则运行时会出现 ClassNotFoundException
    implementation("com.amap.api:3dmap:8.1.0")
    implementation("com.amap.api:location:5.6.0")
}

flutter {
    source = "../.."
}

// 修复旧版高德 Flutter 插件在 AGP 8.x 下的 AndroidManifest.xml package 属性问题
tasks.register("fixAmapManifests") {
    doLast {
        val pubCache = File(System.getProperty("user.home"), "AppData/Local/Pub/Cache/hosted/pub.flutter-io.cn")
        arrayOf(
            "amap_flutter_location-3.0.0/android/src/main/AndroidManifest.xml",
            "amap_flutter_map-3.0.0/android/src/main/AndroidManifest.xml"
        ).forEach { path ->
            val manifest = File(pubCache, path)
            if (manifest.exists()) {
                var text = manifest.readText()
                text = text.replace(Regex("""\s+package="[^"]*""""), "")
                manifest.writeText(text)
                println("Fixed manifest: ${manifest.absolutePath}")
            }
        }
    }
}

tasks.whenTaskAdded {
    if (name.contains("processDebugManifest") || name.contains("processReleaseManifest")) {
        dependsOn("fixAmapManifests")
    }
}
