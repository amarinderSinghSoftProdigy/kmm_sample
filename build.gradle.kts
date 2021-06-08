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
        classpath("com.android.tools.build:gradle:7.0.0-beta03")
        classpath("com.google.gms:google-services:4.3.8")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.0")
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
//        maven {
//            url = uri("https://zealsoftsol.jfrog.io/artifactory/libs-release")
//            credentials {
//                username = "${project.property("artifactory_user")}"
//                password = "${project.property("artifactory_password")}"
//            }
//        }
    }
}

Config.Version.parseConfig()