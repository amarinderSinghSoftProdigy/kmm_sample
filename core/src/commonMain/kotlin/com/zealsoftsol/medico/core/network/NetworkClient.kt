package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.ktorDispatcher
import com.zealsoftsol.medico.data.ResponseBody
import com.zealsoftsol.medico.data.UserInfo
import com.zealsoftsol.medico.data.UserRequest
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.invoke
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

class NetworkClient(engine: HttpClientEngineFactory<*>) : CoroutineScope, NetworkScope.Auth {

    override val coroutineContext: CoroutineContext = ktorDispatcher + SupervisorJob()

    private val client = HttpClient(engine) {
        addInterceptor(this)
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    allowStructuredMapKeys = true
                }
            )
        }
        install(HttpTimeout) {
            socketTimeoutMillis = 20_000
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }
    override var token: String? = null

    override suspend fun login(request: UserRequest): UserInfo? = ktorDispatcher {
        client.post<ResponseBody<UserInfo>>("$BASE_URL/medico/login") {
            contentType(ContentType.parse("application/json"))
            body = request
        }.getBodyOrNull()
    }

    override suspend fun logout(): Boolean = ktorDispatcher {
        client.post<ResponseBody<String>>("$BASE_URL/medico/logout") {
            withToken()
        }.isSuccess
    }

    private inline fun HttpRequestBuilder.withToken() {
        token?.let { header("Authorization", "Bearer $it") } ?: "no token for request".warnIt()
    }

    companion object {
        private const val BASE_URL = "https://develop-api-auth0.medicostores.com"
    }
}

expect fun addInterceptor(config: HttpClientConfig<HttpClientEngineConfig>)

