import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
//    kotlin("native.cocoapods")
}

android {
    compileSdkVersion(Config.Android.targetSdk)
    buildToolsVersion(Config.Android.buildTools)
    defaultConfig {
        minSdkVersion(Config.Android.minSdk)
        targetSdkVersion(Config.Android.minSdk)
        versionCode = Config.Version.code
        versionName = Config.Version.name
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            useIR = true
        }
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

kotlin {
    android()
    ios {
        val postfix = name.substring(3).toLowerCase()
        binaries {
            framework {
                baseName = "core_$postfix"
                transitiveExport = true
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":data"))
                api(Deps.Kotlin.Coroutines.core)
                api(Deps.Kodein.DI.core)
                implementation(Deps.Ktor.Client.core)
                implementation(Deps.Ktor.auth)
                implementation(Deps.Ktor.json)
                implementation(Deps.Ktor.serial)
                implementation(Deps.Ktor.log)
                implementation(Deps.multiplatformSettings)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Deps.Ktor.Client.jvm)
                api(Deps.Kodein.DI.android)
                api(Deps.Android.Ktx.core)
                implementation(Deps.okhttpinterceptor)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(Deps.Ktor.Client.ios)
            }
        }
    }
}

val packForXcode by tasks.creating(Sync::class) {
    group = "build"
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    kotlin.targets
        .filterIsInstance<KotlinNativeTarget>()
        .map { it.binaries.getFramework(mode) }
        .forEach { framework ->
            println("Building framework ${framework.baseName}")
            inputs.property("mode", mode)
            dependsOn(framework.linkTask)
            val targetDir = File(buildDir, "xcode-frameworks")
            from({ framework.outputDirectory })
            into(targetDir)
        }
}
tasks.getByName("build").dependsOn(packForXcode)