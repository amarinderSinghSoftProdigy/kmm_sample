package com.zealsoftsol.medico.core

import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.Settings
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.ios.Ios
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import platform.Foundation.NSUserDefaults

actual fun DI.MainBuilder.platformDependencies(context: Any, isDebugBuild: Boolean) {
    bind<HttpClientEngineFactory<*>>() with singleton { Ios }
    bind<Settings>() with singleton { AppleSettings(NSUserDefaults()) }
}