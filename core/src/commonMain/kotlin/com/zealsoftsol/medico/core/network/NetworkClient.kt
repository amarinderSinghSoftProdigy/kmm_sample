package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.core.extensions.Interval
import com.zealsoftsol.medico.core.extensions.retry
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.ktorDispatcher
import com.zealsoftsol.medico.data.JustResponseBody
import com.zealsoftsol.medico.data.Location
import com.zealsoftsol.medico.data.MapBody
import com.zealsoftsol.medico.data.OtpRequest
import com.zealsoftsol.medico.data.PasswordResetRequest
import com.zealsoftsol.medico.data.PasswordValidation
import com.zealsoftsol.medico.data.ResponseBody
import com.zealsoftsol.medico.data.TempOtpRequest
import com.zealsoftsol.medico.data.TokenInfo
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3
import com.zealsoftsol.medico.data.ValidatedResponseBody
import com.zealsoftsol.medico.data.ValidationData
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
import kotlinx.coroutines.CompletableDeferred
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
    private var tempToken: Deferred<TokenInfo?> = CompletableDeferred(value = null)

    override suspend fun login(request: UserRequest): TokenInfo? = ktorDispatcher {
        client.post<ResponseBody<TokenInfo>>("$AUTH_URL/medico/login") {
            contentType(ContentType.parse("application/json"))
            body = request
        }.getBodyOrNull()
    }

    override suspend fun logout(): Boolean = ktorDispatcher {
        client.post<JustResponseBody>("$AUTH_URL/medico/logout") {
            withMainToken()
        }.isSuccess
    }

    override suspend fun sendOtp(phoneNumber: String): Boolean = ktorDispatcher {
        client.post<JustResponseBody>("$AUTH_URL/api/v1/medico/forgetpwd") {
            withTempToken(TempToken.FORGET_PASSWORD)
            contentType(ContentType.parse("application/json"))
            body = TempOtpRequest(phoneNumber)
        }.isSuccess
    }

    override suspend fun retryOtp(phoneNumber: String): Boolean = ktorDispatcher {
        client.post<JustResponseBody>("$NOTIFICATIONS_URL/api/v1/notifications/retryOTP") {
            withTempToken(TempToken.FORGET_PASSWORD)
            contentType(ContentType.parse("application/json"))
            body = OtpRequest(phoneNumber)
        }.isSuccess
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Boolean = ktorDispatcher {
        val body =
            client.post<ResponseBody<TokenInfo>>("$NOTIFICATIONS_URL/api/v1/notifications/verifyOTP") {
                withTempToken(TempToken.FORGET_PASSWORD)
                contentType(ContentType.parse("application/json"))
                body = VerifyOtpRequest(phoneNumber, otp)
            }
        if (body.isSuccess) {
            tempToken = CompletableDeferred(body.getBodyOrNull())
        }
        body.isSuccess
    }

    override suspend fun changePassword(
        phoneNumber: String,
        password: String
    ): ValidationData<PasswordValidation> =
        ktorDispatcher {
            client.post<ValidatedResponseBody<MapBody, PasswordValidation>>("$AUTH_URL/api/v1/medico/forgetpwd/update") {
                withTempToken(TempToken.FORGET_PASSWORD)
                contentType(ContentType.parse("application/json"))
                body = PasswordResetRequest(phoneNumber, password, password)
            }.getValidationData()
        }

    override suspend fun signUpPart1(userRegistration1: UserRegistration1): ValidationData<UserValidation1> =
        ktorDispatcher {
            client.post<ValidatedResponseBody<MapBody, UserValidation1>>("$REGISTRATION_URL/api/v1/registration/step1") {
                withTempToken(TempToken.REGISTRATION)
                contentType(ContentType.parse("application/json"))
                body = userRegistration1
            }.getValidationData()
        }

    override suspend fun signUpPart2(userRegistration2: UserRegistration2): ValidationData<UserValidation2> =
        ktorDispatcher {
            client.post<ValidatedResponseBody<MapBody, UserValidation2>>("$REGISTRATION_URL/api/v1/registration/step2") {
                withTempToken(TempToken.REGISTRATION)
                contentType(ContentType.parse("application/json"))
                body = userRegistration2
            }.getValidationData()
        }

    override suspend fun signUpPart3(userRegistration3: UserRegistration3): ValidationData<UserValidation3> =
        ktorDispatcher {
            client.post<ValidatedResponseBody<MapBody, UserValidation3>>("$REGISTRATION_URL/api/v1/registration/step3") {
                withTempToken(TempToken.REGISTRATION)
                contentType(ContentType.parse("application/json"))
                body = userRegistration3
            }.getValidationData()
        }

    override suspend fun getLocationData(pincode: String): Location.Data? = ktorDispatcher {
        client.get<ResponseBody<Location.Data>>("$MASTER_URL/api/v1/masterdata/pincode/$pincode") {
            withTempToken(TempToken.REGISTRATION)
        }.getBodyOrNull()
    }

    private inline fun HttpRequestBuilder.withMainToken() {
        token?.let { header("Authorization", "Bearer $it") } ?: "no token for request".warnIt()
    }

    private suspend inline fun HttpRequestBuilder.withTempToken(tokenType: TempToken) {
        retry(Interval.Linear(100, 5)) {
            val tokenInfo = tempToken.await()
                ?.takeIf { Clock.System.now().toEpochMilliseconds() < it.expiresAt() }
            if (tokenInfo == null || tokenInfo.id != tokenType.serverValue) {
                tempToken = when (tokenType) {
                    TempToken.FORGET_PASSWORD -> fetchNoAuthToken()
                    TempToken.REGISTRATION -> fetchRegistrationToken()
                }
            }
            tokenInfo
        }?.let { header("Authorization", "Bearer ${it.token}") }
            ?: "no temp token (${tokenType.serverValue}) for request".warnIt()
    }

    private inline fun fetchNoAuthToken(): Deferred<TokenInfo?> =
        GlobalScope.async(ktorDispatcher, start = CoroutineStart.LAZY) {
            client.get<ResponseBody<TokenInfo>>("$AUTH_URL/api/v1/public/medico/forgetpwd")
                .getBodyOrNull()
        }

    private inline fun fetchRegistrationToken(): Deferred<TokenInfo?> =
        GlobalScope.async(ktorDispatcher, start = CoroutineStart.LAZY) {
            client.get<ResponseBody<TokenInfo>>("$REGISTRATION_URL/api/v1/registration")
                .getBodyOrNull()
        }

    private enum class TempToken(val serverValue: String) {
        //        MAIN("login"),
        FORGET_PASSWORD("forgetpwd"),

        //        UPDATE_PASSWORD("updatepwd"),
        REGISTRATION("registration");
    }

    companion object {
        private const val AUTH_URL = "https://develop-api-auth0.medicostores.com"
        private const val REGISTRATION_URL = "https://develop-api-registration.medicostores.com"
        private const val NOTIFICATIONS_URL = "https://develop-api-notifications.medicostores.com"
        private const val MASTER_URL = "https://develop-api-masterdata.medicostores.com"
    }
}

expect fun addInterceptor(config: HttpClientConfig<HttpClientEngineConfig>)

