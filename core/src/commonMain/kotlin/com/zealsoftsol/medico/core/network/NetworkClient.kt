package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.core.extensions.Interval
import com.zealsoftsol.medico.core.extensions.retry
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.ktorDispatcher
import com.zealsoftsol.medico.data.JustResponseBody
import com.zealsoftsol.medico.data.OtpRequest
import com.zealsoftsol.medico.data.PasswordResetRequest
import com.zealsoftsol.medico.data.ResponseBody
import com.zealsoftsol.medico.data.TokenInfo
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.VerifyOtpRequest
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
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.invoke
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

class NetworkClient(engine: HttpClientEngineFactory<*>) : NetworkScope.Auth {

    private val client = HttpClient(engine) {
        addInterceptor(this)
        expectSuccess = false
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
    private var noAuthToken: Deferred<TokenInfo?> = fetchNoAuthToken()

    override suspend fun login(request: UserRequest): TokenInfo? = ktorDispatcher {
        client.post<ResponseBody<TokenInfo>>("$AUTH_URL/medico/login") {
            contentType(ContentType.parse("application/json"))
            body = request
        }.getBodyOrNull()
    }

    override suspend fun logout(): Boolean = ktorDispatcher {
        client.post<JustResponseBody>("$AUTH_URL/medico/logout") {
            withToken()
        }.isSuccess
    }

    override suspend fun sendOtp(phoneNumber: String): Boolean = ktorDispatcher {
        client.post<JustResponseBody>("$AUTH_URL/api/v1/medico/forgetpwd") {
            withNoAuthToken()
            contentType(ContentType.parse("application/json"))
            body = OtpRequest(phoneNumber)
        }.isSuccess
    }

    override suspend fun retryOtp(phoneNumber: String): Boolean = ktorDispatcher {
        client.post<JustResponseBody>("$NOTIFICATIONS_URL/api/v1/notifications/retryOTP") {
            withNoAuthToken()
            contentType(ContentType.parse("application/json"))
            body = OtpRequest(phoneNumber)
        }.isSuccess
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Boolean = ktorDispatcher {
        client.post<JustResponseBody>("$NOTIFICATIONS_URL/api/v1/notifications/verifyOTP") {
            withNoAuthToken()
            contentType(ContentType.parse("application/json"))
            body = VerifyOtpRequest(phoneNumber, otp)
        }.isSuccess
    }

    override suspend fun changePassword(phoneNumber: String, password: String): Boolean = ktorDispatcher {
        client.post<JustResponseBody>("$AUTH_URL/api/v1/medico/forgetpwd/update") {
            withNoAuthToken()
            contentType(ContentType.parse("application/json"))
            body = PasswordResetRequest(phoneNumber, password, password)
        }.isSuccess
    }

    private inline fun HttpRequestBuilder.withToken() {
        token?.let { header("Authorization", "Bearer $it") } ?: "no token for request".warnIt()
    }

    private suspend inline fun HttpRequestBuilder.withNoAuthToken() {
        retry(Interval.Linear(100, 5)) {
            val tokenInfo = noAuthToken.await()
                ?.takeIf { Clock.System.now().toEpochMilliseconds() < it.expiresAt() }
            if (tokenInfo == null)
                noAuthToken = fetchNoAuthToken()
            tokenInfo
        }?.let { header("Authorization", "Bearer ${it.token}") }
            ?: "no noAuth token for request".warnIt()
    }

    private inline fun fetchNoAuthToken(): Deferred<TokenInfo?> = GlobalScope.async(ktorDispatcher, start = CoroutineStart.LAZY) {
        client.get<ResponseBody<TokenInfo>>("$AUTH_URL/api/v1/public/medico/forgetpwd").getBodyOrNull()
    }

    companion object {
        private const val AUTH_URL = "https://develop-api-auth0.medicostores.com"
        private const val NOTIFICATIONS_URL = "https://develop-api-notifications.medicostores.com"
    }
}

expect fun addInterceptor(config: HttpClientConfig<HttpClientEngineConfig>)

