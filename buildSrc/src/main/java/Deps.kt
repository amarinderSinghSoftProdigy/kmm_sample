import groovy.json.JsonSlurper
import java.io.File

object Versions {
    const val agp = "7.0.2"
    const val kotlin = "1.5.21"
    const val ktor = "1.6.0"
    const val coroutines = "1.5.2-native-mt"
    const val kserialize = "1.2.1"
    const val kodeinDi = "7.5.0"
    const val compose = "1.0.2"
}

object Deps {
    object Kotlin {
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
        const val dynamic = "androidx.dynamicanimation:dynamicanimation:1.0.0"
        const val exif = "androidx.exifinterface:exifinterface:1.0.0"
        const val customTabs = "androidx.browser:browser:1.2.0"
        const val workManager = "androidx.work:work-runtime-ktx:2.2.0"
        const val emoji = "androidx.emoji:emoji-bundled:1.0.0"
        const val playcore = "com.google.android.play:core:1.7.3"
        const val playcorektx = "com.google.android.play:core-ktx:1.7.0"

        object Ktx {
            const val core = "androidx.core:core-ktx:1.5.0"
        }

        object Compose {
            const val ui = "androidx.compose.ui:ui:${Versions.compose}"

            // Tooling support (Previews, etc.)
            const val tooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"

            // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
            const val foundation = "androidx.compose.foundation:foundation:${Versions.compose}"

            // Material Design
            const val material = "androidx.compose.material:material:${Versions.compose}"

            // Material design icons
            const val iconsCore =
                "androidx.compose.material:material-icons-core:${Versions.compose}"
            const val iconsExtended =
                "androidx.compose.material:material-icons-extended:${Versions.compose}"
            const val activity = "androidx.activity:activity-compose:1.3.1"
            const val constraint =
                "androidx.constraintlayout:constraintlayout-compose:1.0.0-beta02"

            val all = listOf(
                ui,
                tooling,
                foundation,
                material,
                iconsCore,
                iconsExtended,
                activity,
                constraint
            )
        }

        const val coil = "io.coil-kt:coil-compose:1.3.0"
    }

    object Kodein {
        object DI {
            const val core = "org.kodein.di:kodein-di:${Versions.kodeinDi}"
            const val android = "org.kodein.di:kodein-di-framework-android-x:${Versions.kodeinDi}"
        }
    }

    object Firebase {
        const val BOM = "com.google.firebase:firebase-bom:26.0.0"
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
        const val messaging = "com.google.firebase:firebase-messaging-ktx"
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

    const val multiplatformSettings = "com.russhwolf:multiplatform-settings:0.7.7"
    const val okhttpinterceptor = "com.squareup.okhttp3:logging-interceptor:4.9.0"
}


object Config {
    var isAndroidDev = false

    init {
        isAndroidDev = File("android_dev").exists()
        if (isAndroidDev) println("ANDROID DEV")
    }

    object Version {
        var code: Int = 1
        var name: String = "na"

        fun parseConfig() {
            val file = File("config.json")
            if (file.exists()) {
                val map = JsonSlurper().parse(file) as Map<String, Map<String, Any>>
                val version = map["version"]!!
                val major = version["major"].toString().toInt()
                val minor = version["minor"].toString().toInt()
                val patch = version["patch"].toString().toInt()
                code = major * 10000 + minor * 1000 + patch * 10 + 0
                name = "${major}.${minor}.${patch}"
                println("APP VERSION $name")
            }
        }
    }

    object Android {
        const val minSdk = 21
        const val targetSdk = 31
        const val buildTools = "30.0.2"
    }

    object Ios {
        val isForSimulator: Boolean
            get() = isAndroidDev || System.getenv("FOR_SIMULATOR") == "true" 
    }
}