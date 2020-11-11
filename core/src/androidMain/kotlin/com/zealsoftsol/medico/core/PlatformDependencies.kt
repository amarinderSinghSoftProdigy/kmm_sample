package com.zealsoftsol.medico.core

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

actual fun DI.MainBuilder.platformDependencies(context: Any) {
    bind<HttpClientEngineFactory<*>>() with singleton { OkHttp }
}