package com.zealsoftsol.medico.core

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

actual fun DI.MainBuilder.platformDependencies(context: Any) {
    context as Context
    bind<HttpClientEngineFactory<*>>() with singleton { OkHttp }
    bind<Settings>() with singleton { AndroidSettings(context.getSharedPreferences("prefs", Context.MODE_PRIVATE)) }
}