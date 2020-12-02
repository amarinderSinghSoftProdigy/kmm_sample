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
//        classpath("org.jetbrains.kotlin:kotlin-android-extensions:${Versions.kotlin}")
        classpath("com.android.tools.build:gradle:4.2.0-beta01")
        classpath("com.google.gms:google-services:4.3.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.4.1")
    }
}

allprojects {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx/")
//        maven {
//            url = uri("https://zealsoftsol.jfrog.io/artifactory/libs-release")
//            credentials {
//                username = "${project.property("artifactory_user")}"
//                password = "${project.property("artifactory_password")}"
//            }
//        }
    }
}