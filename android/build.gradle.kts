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
    compileSdk = Config.Android.targetSdk
    buildToolsVersion = Config.Android.buildTools
    defaultConfig {
        applicationId = "com.zealsoftsol.medico"
        minSdk = Config.Android.minSdk
        targetSdk = Config.Android.minSdk
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
            buildConfigField("boolean", "CI_BUILD", "$isCiBuild")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                File("proguard-rules.pro")
            )
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("boolean", "ANDROID_DEV", "false")
            buildConfigField("boolean", "CI_BUILD", "$isCiBuild")
        }
    }
    flavorDimensions += "default"
    productFlavors {
        create("dev") {
            dimension = "default"
            applicationIdSuffix = ".dev"
        }
        create("stag") {
            dimension = "default"
            applicationIdSuffix = ".stag"
        }
        create("prod") {
            dimension = "default"
        }
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(Deps.Android.customTabs)
    Deps.Android.Compose.all.forEach {
        implementation(it)
    }
    implementation(Deps.Android.coil)
    implementation(platform(Deps.Firebase.BOM))
    implementation(Deps.Firebase.analytics)
    implementation(Deps.Firebase.crashlytics)
    implementation(Deps.Firebase.messaging)
    implementation(Deps.libphonenumber)
    implementation("io.karn:notify:1.3.0")
    implementation("joda-time:joda-time:2.10.5")

    // for lint
    implementation("androidx.fragment:fragment-ktx:1.3.4")
}