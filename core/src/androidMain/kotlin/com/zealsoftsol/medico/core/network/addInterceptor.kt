package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.BuildConfig
import com.zealsoftsol.medico.core.extensions.logIt
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.okhttp.OkHttpConfig
import okhttp3.logging.HttpLoggingInterceptor

actual fun addInterceptor(config: HttpClientConfig<HttpClientEngineConfig>) {
    if (BuildConfig.DEBUG) {
        (config as HttpClientConfig<OkHttpConfig>).engine {
            addInterceptor(HttpLoggingInterceptor().also {
                it.level = HttpLoggingInterceptor.Level.BODY
            })
            "added network interceptor".logIt()
        }
    } else {
        "skip network interceptor".logIt()
    }
}