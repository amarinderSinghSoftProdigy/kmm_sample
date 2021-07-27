buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
        maven(url = "https://dl.bintray.com/kodein-framework/Kodein-DB")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}")
        classpath("com.android.tools.build:gradle:${Versions.agp}")
        classpath("com.google.gms:google-services:4.3.8")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
    }
}

allprojects {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx/")
        maven(url = "https://jitpack.io")
    }
}

Config.Version.parseConfig()