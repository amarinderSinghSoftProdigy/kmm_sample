import groovy.json.JsonSlurper
import java.io.File

object Versions {
    const val kotlin = "1.4.10"
    const val ktor = "1.4.1"
    const val coroutines = "1.3.9-native-mt-2"
    const val kserialize = "1.0.0"
    const val work = "2.0.1"
    const val kodeinDi = "7.1.0"
    const val kodeinDb = "0.4.0-beta"
    const val compose = "1.0.0-alpha07"
}

object Deps {
    object Kotlin {
//        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:1.4.10"
        const val time = "org.jetbrains.kotlinx:kotlinx-datetime:0.1.0"
        object Kserialize {
            const val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kserialize}"
            const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kserialize}"
        }
        object Coroutines {
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
            const val play = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.coroutines}"
        }
    }

    object Android {
        const val appCompat = "androidx.appcompat:appcompat:1.2.0"
        const val material = "com.google.android.material:material:1.2.0"
        const val constraint = "androidx.constraintlayout:constraintlayout:1.1.3"
        const val dynamic = "androidx.dynamicanimation:dynamicanimation:1.0.0"
        const val exif = "androidx.exifinterface:exifinterface:1.0.0"
        const val lifecycle = "androidx.lifecycle:lifecycle-process:2.2.0"
        const val customTabs = "androidx.browser:browser:1.2.0"
        const val workManager = "androidx.work:work-runtime-ktx:2.2.0"
        const val emoji = "androidx.emoji:emoji-bundled:1.0.0"
        const val swipe = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
        const val playcore = "com.google.android.play:core:1.7.3"
        const val playcorektx = "com.google.android.play:core-ktx:1.7.0"

        object Ktx {
            const val core = "androidx.core:core-ktx:1.3.2"
        }

        object Compose {
            const val ui = "androidx.compose.ui:ui:${Versions.compose}"
            // Tooling support (Previews, etc.)
            const val tooling = "androidx.ui:ui-tooling:${Versions.compose}"
            // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
            const val foundation = "androidx.compose.foundation:foundation:${Versions.compose}"
            // Material Design
            const val material = "androidx.compose.material:material:${Versions.compose}"
            // Material design icons
            const val iconsCore = "androidx.compose.material:material-icons-core:${Versions.compose}"
            const val iconsExtended = "androidx.compose.material:material-icons-extended:${Versions.compose}"
            // Integration with observables
//            const val livedata = "androidx.compose.runtime:runtime-livedata:${Versions.compose}"
//            const val rx = "androidx.compose.runtime:runtime-rxjava2:${Versions.compose}"
            const val accompanist = "dev.chrisbanes.accompanist:accompanist-coil:0.2.1"

            val all = listOf(ui, tooling, foundation, material, iconsCore, iconsExtended, accompanist)
        }
    }

    object Kodein {
        object DI {
            const val core = "org.kodein.di:kodein-di:${Versions.kodeinDi}"
            const val android = "org.kodein.di:kodein-di-framework-android-x:${Versions.kodeinDi}"
        }
        object DB {
            const val core = "org.kodein.db:kodein-db:${Versions.kodeinDb}"
            const val serializer = "org.kodein.db:kodein-db-serializer-kotlinx:${Versions.kodeinDb}"
        }
    }

    object Firebase {
        const val BOM = "com.google.firebase:firebase-bom:26.0.0"
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
        const val messaging = "com.google.firebase:firebase-messaging:20.2.0"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
        const val perfomance = "com.google.firebase:firebase-perf:17.0.0"
        const val remoteConfig = "com.google.firebase:firebase-config:17.0.0"
        const val firestore = "com.google.firebase:firebase-firestore:20.1.0"
        const val storage = "com.google.firebase:firebase-storage:18.1.1"
        const val auth = "com.google.firebase:firebase-auth:19.3.1"
        const val links = "com.google.firebase:firebase-dynamic-links-ktx:19.1.0"
    }

    object Ktor {
        object Client {
            const val core = "io.ktor:ktor-client-core:${Versions.ktor}"
            const val jvm = "io.ktor:ktor-client-okhttp:${Versions.ktor}"
            const val ios = "io.ktor:ktor-client-ios:${Versions.ktor}"
        }
        const val auth = "io.ktor:ktor-client-auth:${Versions.ktor}"
        const val json = "io.ktor:ktor-client-json:${Versions.ktor}"
        const val serial = "io.ktor:ktor-client-serialization:${Versions.ktor}"
        const val log = "io.ktor:ktor-client-logging:${Versions.ktor}"
    }

    const val multiplatformSettings = "com.russhwolf:multiplatform-settings:0.6.3"
    const val libphonenumber = "com.googlecode.libphonenumber:libphonenumber:8.12.7"
}


object Config {

    object Version {
        var code: Int = 1
        var name: String = "1"

        init {
            try {
                val map = JsonSlurper().parse(File("config.json")) as Map<String, Map<String, Any>>
                val version = map["version"]!!
                val major = version["major"].toString().toInt()
                val minor = version["minor"].toString().toInt()
                val patch = version["patch"].toString().toInt()
                val isBuild = version["build"].toString().toBoolean()
                code = major * 10000 + minor * 1000 + patch * 10 + (if (isBuild) 1 else 0)
                name = "${major}.${minor}.${patch}${if (isBuild) "[dev]" else ""}"
            } catch (e: Exception) {
                println("COULD NOT PARSE config.json")
            }
        }
    }

    object Android {
        const val minSdk = 21
        const val targetSdk = 30
        const val buildTools = "30.0.2"
    }
}