package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.core.ktorDispatcher
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

class NetworkClient(engine: HttpClientEngineFactory<*>) : CoroutineScope, NetworkScope.Auth {

    override val coroutineContext: CoroutineContext = ktorDispatcher + SupervisorJob()

    private val client = HttpClient(engine) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    allowStructuredMapKeys = true
                }
            )
        }
//        install(HttpTimeout) {
//            socketTimeoutMillis = 15_000
//        }
//        install(Auth) {
//
//        }
    }
}

