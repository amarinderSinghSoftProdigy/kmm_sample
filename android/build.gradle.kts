import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("android")
}

val isCiBuild = !System.getenv("CI_BUILD").isNullOrEmpty()
println("CI Build: ${if (isCiBuild) "YES" else "NO"}")

android {
    compileSdkVersion(Config.Android.targetSdk)
    buildToolsVersion(Config.Android.buildTools)
    defaultConfig {
        applicationId = "com.zealsoftsol.medico"
        minSdkVersion(Config.Android.minSdk)
        targetSdkVersion(Config.Android.minSdk)
        versionCode = Config.Version.code
        versionName = Config.Version.name
        vectorDrawables.useSupportLibrary = true
    }
    signingConfigs {
        named("debug").configure {
            storeFile = file("debug.keystore")
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            storePassword = "android"
        }
        create("release") {
            val properties = Properties()
            if (isCiBuild) {
                properties["key.alias"] = System.getenv("ANDROID_KEY_ALIAS")
                properties["key.password"] = System.getenv("ANDROID_KEY_PASSWORD")
                properties["store.password"] = System.getenv("ANDROID_STORE_PASSWORD")
            } else {
                properties.load(project.rootProject.file("local.properties").inputStream())
            }
            storeFile = File("${rootDir.absolutePath}/${project.name}/release.key")
            keyAlias = properties.getProperty("key.alias")
            keyPassword = properties.getProperty("key.password")
            storePassword = properties.getProperty("store.password")
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("boolean", "ANDROID_DEV", "${Config.isAndroidDev}")
        }
        getByName("release") {
            // TODO enable proguard
            isMinifyEnabled = false
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), File("proguard-rules.pro"))
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("boolean", "ANDROID_DEV", "false")
        }
    }
    flavorDimensions("default")
    productFlavors {
        create("dev") {
            dimension = "default"
            applicationIdSuffix = ".dev"
        }
        create("prod") {
            dimension = "default"
        }
    }
    buildFeatures {
        // Enables Jetpack Compose for this module
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerVersion = Versions.kotlin
        kotlinCompilerExtensionVersion = Versions.compose
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            useIR = true
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(Deps.Android.appCompat)
    implementation(Deps.Android.customTabs)
    implementation(Deps.Android.activity)
    Deps.Android.Compose.all.forEach {
        implementation(it)
    }
    implementation(Deps.Android.accompanist)
    implementation(platform(Deps.Firebase.BOM))
    implementation(Deps.Firebase.analytics)
    implementation(Deps.Firebase.crashlytics)
    implementation(Deps.Firebase.messaging)
    implementation(Deps.libphonenumber)
    implementation("io.karn:notify:1.3.0")
}