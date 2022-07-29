package com.zealsoftsol.medico.core.interop

enum class Platform {
    iOS, Android;
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect val platform: Platform