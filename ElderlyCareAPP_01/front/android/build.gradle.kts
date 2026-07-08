allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
    }
}

val newBuildDir: Directory =
    rootProject.layout.buildDirectory
        .dir("../../build")
        .get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
    project.evaluationDependsOn(":app")

    // 修复旧版高德 Flutter 插件（amap_flutter_map / amap_flutter_location）
    // 在 AGP 8.x 下缺少 namespace 与 compileSdk 的问题
    plugins.withId("com.android.library") {
        val androidExt = extensions.findByName("android")
        if (androidExt != null) {
            try {
                val clazz = androidExt::class.java
                val getNamespace = clazz.getMethod("getNamespace")
                val ns = getNamespace.invoke(androidExt) as? String
                if (ns.isNullOrEmpty()) {
                    val fallbackNs = project.group.toString().ifEmpty {
                        "com.amap.flutter.${project.name}"
                    }
                    clazz.getMethod("setNamespace", String::class.java)
                        .invoke(androidExt, fallbackNs)
                }
                clazz.getMethod("setCompileSdkVersion", Int::class.java)
                    .invoke(androidExt, 34)
            } catch (_: Exception) {
                // 忽略
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
