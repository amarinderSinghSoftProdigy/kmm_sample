package com.zealsoftsol.medico.core.network

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.okhttp.OkHttpConfig
import okhttp3.logging.HttpLoggingInterceptor

actual fun addInterceptor(config: HttpClientConfig<HttpClientEngineConfig>) {
    (config as HttpClientConfig<OkHttpConfig>).engine {
        addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        })
    }
}