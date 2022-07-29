package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.core.extensions.Interval
import com.zealsoftsol.medico.core.extensions.isExpired
import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.extensions.retry
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.ktorDispatcher
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.storage.TokenStorage
import com.zealsoftsol.medico.core.data.AnyResponse
import com.zealsoftsol.medico.core.data.BodyResponse
import com.zealsoftsol.medico.core.data.ErrorCode
import com.zealsoftsol.medico.core.data.HeaderData
import com.zealsoftsol.medico.core.data.MapBody
import com.zealsoftsol.medico.core.data.OtpRequest
import com.zealsoftsol.medico.core.data.PasswordResetRequest
import com.zealsoftsol.medico.core.data.PasswordResetRequest2
import com.zealsoftsol.medico.core.data.PasswordValidation
import com.zealsoftsol.medico.core.data.RefreshTokenRequest
import com.zealsoftsol.medico.core.data.Response
import com.zealsoftsol.medico.core.data.TokenInfo
import com.zealsoftsol.medico.core.data.UserRequest
import com.zealsoftsol.medico.core.data.ValidationResponse
import com.zealsoftsol.medico.core.data.VerifyOtpRequest
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.invoke
import kotlinx.serialization.json.Json

class NetworkClient(
    engine: HttpClientEngineFactory<*>,
    private val tokenStorage: TokenStorage,
    useNetworkInterceptor: Boolean,
    private val crashOnServerError: Boolean,
    private val baseUrl: BaseUrl,
) : NetworkScope.Auth,
    NetworkScope.BottomSheetStore {

    init {
        "USING NetworkClient with $baseUrl".logIt()
    }

    private val client = HttpClient(engine) {
        expectSuccess = true
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                createJson()
            )
        }
        install(HttpTimeout) {
            socketTimeoutMillis = 20_000
        }
        if (useNetworkInterceptor) {
            addInterceptor(this)
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }

    override suspend fun login(request: UserRequest) = simpleRequest {
        client.post<BodyResponse<TokenInfo>>("${baseUrl.url}/medico/login") {
            jsonBody(request)
        }.also {
            it.getBodyOrNull()?.let(tokenStorage::saveMainToken)
        }
    }

    override suspend fun logout() = simpleRequest {
        client.post<AnyResponse>("${baseUrl.url}/medico/logout") {
            withMainToken()
        }
    }

    override suspend fun checkCanResetPassword(phoneNumber: String) = fullRequest {
        client.post<AnyResponse>("${baseUrl.url}/medico/forgetpwd") {
            withTempToken(TempToken.REGISTRATION)
            jsonBody(OtpRequest(phoneNumber))
        }
    }

    override suspend fun sendOtp(phoneNumber: String) =
        simpleRequest {
            client.post<AnyResponse>("${baseUrl.url}/notifications/sendOTP") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(OtpRequest(phoneNumber))
            }
        }

    override suspend fun retryOtp(phoneNumber: String) =
        simpleRequest {
            client.post<AnyResponse>("${baseUrl.url}/notifications/retryOTP") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(OtpRequest(phoneNumber))
            }
        }

    override suspend fun verifyOtp(phoneNumber: String, otp: String) =
        simpleRequest {
            val body =
                client.post<BodyResponse<TokenInfo>>("${baseUrl.url}/notifications/verifyOTP") {
                    withTempToken(TempToken.REGISTRATION)
                    jsonBody(VerifyOtpRequest(phoneNumber, otp))
                }
            if (body.isSuccess) {
                body.getBodyOrNull()
                    ?.let { tokenStorage.saveTempToken(TempToken.UPDATE_PASSWORD.serverValue, it) }
            }
            body
        }

    override suspend fun getDetails(
        unitCode: String,
    ) = simpleRequest {
        client.get<BodyResponse<HeaderData>>("${baseUrl.url}/b2bapp/detail/${unitCode}") {
            withMainToken()
        }
    }


    // Utils
    private inline fun HttpRequestBuilder.withB2bCodeToken(finalToken: String) {
        applyHeader(finalToken)
    }

    private suspend inline fun HttpRequestBuilder.withMainToken() {
        val finalToken = tokenStorage.getMainToken()?.let { _ ->
            retry(Interval.Linear(100, 5)) {
                val tokenInfo = requireNotNull(tokenStorage.getMainToken())
                if (tokenInfo.isExpired) {
                    fetchMainToken(tokenInfo)?.also(tokenStorage::saveMainToken)
                } else {
                    tokenInfo
                }
            }
        }

        if (finalToken != null) {
            applyHeader(finalToken)
        } else {
            "no main token for request".warnIt()
            EventCollector.sendEvent(Event.Action.Auth.LogOut(false))
        }
    }

    private suspend inline fun HttpRequestBuilder.withTempToken(tokenType: TempToken) {
        val finalToken = retry(Interval.Linear(100, 5)) {
            val tokenInfo =
                tokenStorage.getTempTokenOnce(tokenType.serverValue)?.takeIf { !it.isExpired }
            if (tokenInfo == null || tokenInfo.id != tokenType.serverValue) {
                when (tokenType) {
                    TempToken.REGISTRATION -> fetchRegistrationToken()
                    TempToken.UPDATE_PASSWORD -> tokenInfo
                }?.also {
                    tokenStorage.saveTempToken(tokenType.serverValue, it)
                }
            } else {
                tokenStorage.saveTempToken(tokenType.serverValue, tokenInfo)
                tokenInfo
            }
        }
        if (finalToken != null) {
            applyHeader(finalToken)
        } else {
            "no temp token (${tokenType.serverValue}) for request".warnIt()
        }
    }

    private suspend fun fetchMainToken(currentToken: TokenInfo): TokenInfo? {
        return client.post<BodyResponse<TokenInfo>>("${baseUrl.url}/medico/refresh-token") {
            applyHeader(currentToken)
            jsonBody(RefreshTokenRequest(currentToken.refreshToken))
        }.getBodyOrNull()
    }

    private suspend inline fun fetchRegistrationToken(): TokenInfo? {
        return client.get<BodyResponse<TokenInfo>>("${baseUrl.url}/registration")
            .getBodyOrNull()
    }

    private inline fun HttpRequestBuilder.applyHeader(tokenInfo: TokenInfo) {
        header("Authorization", "Bearer ${tokenInfo.token}")
    }

    private inline fun HttpRequestBuilder.applyHeader(tokenInfo: String) {
        header("X-TENANT-ID", tokenInfo)
    }

    private inline fun HttpRequestBuilder.jsonBody(body: Any) {
        contentType(ContentType.parse("application/json"))
        this.body = body
    }

    private suspend inline fun <T, V> fullRequest(
        dispatcher: CoroutineDispatcher = ktorDispatcher,
        crossinline call: suspend HttpClient.() -> Response<T, V>,
    ): Response<T, V> = dispatcher {
        val result = runCatching { client.call() }
        if (result.isSuccess || crashOnServerError) {
            result.getOrThrow()
        } else {
            Response(
                error = ErrorCode.somethingWentWrong,
                type = "error",
            )
        }
    }

    private suspend inline fun <T> simpleRequest(
        dispatcher: CoroutineDispatcher = ktorDispatcher,
        crossinline call: suspend HttpClient.() -> Response<T, MapBody>,
    ): Response<T, MapBody> = fullRequest<T, MapBody>(dispatcher, call)

    enum class TempToken(val serverValue: String) {
        UPDATE_PASSWORD("updatepwd"),
        REGISTRATION("registration");
    }

    enum class BaseUrl(val url: String) {
        DEV("https://dev-api.com"),
        STAG("https://staging-api.com"),
        PROD("https://prod-api.com");
    }


}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun addInterceptor(config: HttpClientConfig<HttpClientEngineConfig>)

internal fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    allowStructuredMapKeys = true
    // TODO remove after kserilize lib updated from 1.2.1
    useAlternativeNames = false
}
