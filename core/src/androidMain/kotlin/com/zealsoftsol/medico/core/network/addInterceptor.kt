package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.core.extensions.logIt
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.okhttp.OkHttpConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

actual fun addInterceptor(config: HttpClientConfig<HttpClientEngineConfig>) {
    (config as HttpClientConfig<OkHttpConfig>).engine {
        addInterceptor(LoggingInterceptor())
        "added network interceptor".logIt()
    }
}

class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val t1 = System.nanoTime()
        "Sending request $request, headers - ${request.headers}, contentType - ${request.body?.contentType()}".logIt()

        val response = chain.proceed(request)

        val t2 = System.nanoTime()
        "Received response for ${response.request.url} in ${(t2 - t1) / 1e6}ms ${response.headers}".logIt()

        return response
    }
}