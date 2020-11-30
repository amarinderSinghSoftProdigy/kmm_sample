import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("android")
}

android {
    compileSdkVersion(Config.Android.targetSdk)
    buildToolsVersion(Config.Android.buildTools)
    defaultConfig {
        applicationId = "com.zealsoftsol.medico"
        minSdkVersion(Config.Android.minSdk)
        targetSdkVersion(Config.Android.minSdk)
        versionCode = Config.Version.code
        versionName = Config.Version.name
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
            properties.load(project.rootProject.file("local.properties").inputStream())
            storeFile = File(properties.getProperty("store.file"))
            keyAlias = properties.getProperty("key.alias")
            keyPassword = properties.getProperty("key.password")
            storePassword = properties.getProperty("store.password")
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), File("proguard-rules.pro"))
            signingConfig = signingConfigs.getByName("release")
        }
    }
    flavorDimensions("default")
    productFlavors {
        create("dev") {
            dimension = "default"
            applicationIdSuffix = ".dev"
            buildConfigField("String", "SERVER_URL", "\"url\"")
        }
        create("prod") {
            dimension = "default"
            buildConfigField("String", "SERVER_URL", "\"url\"")
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
    Deps.Android.Compose.all.forEach {
        implementation(it)
    }
    implementation(platform(Deps.Firebase.BOM))
    implementation(Deps.Firebase.analytics)
    implementation(Deps.Firebase.crashlytics)
    implementation(Deps.libphonenumber)
}