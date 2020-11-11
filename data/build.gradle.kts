plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    ios()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Deps.Kotlin.Kserialize.core)
                api(Deps.Kotlin.Kserialize.json)
                api(Deps.Kotlin.time)
            }
        }
    }
}

//val packForXcode by tasks.creating(Sync::class) {
//    group = "build"
//    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
//    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
//    val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
//    val framework = kotlin.targets.getByName<KotlinNativeTarget>(targetName).binaries.getFramework(mode)
//    inputs.property("mode", mode)
//    dependsOn(framework.linkTask)
//    val targetDir = File(buildDir, "xcode-frameworks")
//    from({ framework.outputDirectory })
//    into(targetDir)
//}
//tasks.getByName("build").dependsOn(packForXcode)