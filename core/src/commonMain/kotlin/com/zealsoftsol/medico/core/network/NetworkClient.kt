package com.zealsoftsol.medico.core.network

import com.zealsoftsol.medico.core.extensions.Interval
import com.zealsoftsol.medico.core.extensions.retry
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.ktorDispatcher
import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.DrugLicenseUpload
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.Location
import com.zealsoftsol.medico.data.MapBody
import com.zealsoftsol.medico.data.OtpRequest
import com.zealsoftsol.medico.data.PasswordResetRequest
import com.zealsoftsol.medico.data.PasswordValidation
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.SimpleBody
import com.zealsoftsol.medico.data.StorageKeyResponse
import com.zealsoftsol.medico.data.SubmitRegistration
import com.zealsoftsol.medico.data.TokenInfo
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3
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
    private val tempTokenMap = hashMapOf<TempToken, TokenInfo>()

    override fun clearToken() {
        token = null
        tempTokenMap.clear()
    }

    override suspend fun login(request: UserRequest): Response.Wrapped<TokenInfo> = ktorDispatcher {
        client.post<SimpleBody<TokenInfo>>("$AUTH_URL/medico/login") {
            jsonBody(request)
        }.getWrappedBody()
    }

    override suspend fun logout(): Boolean = ktorDispatcher {
        client.post<Response.Status>("$AUTH_URL/medico/logout") {
            withMainToken()
        }.isSuccess
    }

    override suspend fun sendOtp(phoneNumber: String): Response.Wrapped<ErrorCode> =
        ktorDispatcher {
            client.post<SimpleBody<MapBody>>("$NOTIFICATIONS_URL/api/v1/notifications/sendOTP") {
                withTempToken(TempToken.OTP)
                jsonBody(OtpRequest(phoneNumber))
            }.getWrappedError()
        }

    override suspend fun retryOtp(phoneNumber: String): Response.Wrapped<ErrorCode> =
        ktorDispatcher {
            client.post<SimpleBody<MapBody>>("$NOTIFICATIONS_URL/api/v1/notifications/retryOTP") {
                withTempToken(TempToken.OTP)
                jsonBody(OtpRequest(phoneNumber))
            }.getWrappedError()
        }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Response.Wrapped<ErrorCode> =
        ktorDispatcher {
            val body =
                client.post<SimpleBody<TokenInfo>>("$NOTIFICATIONS_URL/api/v1/notifications/verifyOTP") {
                    withTempToken(TempToken.OTP)
                    jsonBody(VerifyOtpRequest(phoneNumber, otp))
                }
            if (body.isSuccess) {
                body.getBodyOrNull()?.let { tempTokenMap[TempToken.UPDATE_PASSWORD] = it }
            }
            body.getWrappedError()
        }

    override suspend fun changePassword(
        phoneNumber: String,
        password: String
    ): Response.Wrapped<PasswordValidation> =
        ktorDispatcher {
            client.post<Response.Body<MapBody, PasswordValidation>>("$AUTH_URL/api/v1/medico/forgetpwd/update") {
                withTempToken(TempToken.UPDATE_PASSWORD)
                jsonBody(PasswordResetRequest(phoneNumber, password, password))
            }.getWrappedValidation()
        }

    override suspend fun signUpValidation1(userRegistration1: UserRegistration1): Response.Wrapped<UserValidation1> =
        ktorDispatcher {
            client.post<Response.Body<MapBody, UserValidation1>>("$REGISTRATION_URL/api/v1/registration/step1") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(userRegistration1)
            }.getWrappedValidation()
        }

    override suspend fun signUpValidation2(userRegistration2: UserRegistration2): Response.Wrapped<UserValidation2> =
        ktorDispatcher {
            client.post<Response.Body<MapBody, UserValidation2>>("$REGISTRATION_URL/api/v1/registration/step2") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(userRegistration2)
            }.getWrappedValidation()
        }

    override suspend fun signUpValidation3(userRegistration3: UserRegistration3): Response.Wrapped<UserValidation3> =
        ktorDispatcher {
            client.post<Response.Body<MapBody, UserValidation3>>("$REGISTRATION_URL/api/v1/registration/step3") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(userRegistration3)
            }.getWrappedValidation()
        }

    override suspend fun getLocationData(pincode: String): Response.Wrapped<Location.Data> =
        ktorDispatcher {
            client.get<SimpleBody<Location.Data>>("$MASTER_URL/api/v1/masterdata/pincode/$pincode") {
                withTempToken(TempToken.REGISTRATION)
            }.getWrappedBody()
        }

    override suspend fun uploadAadhaar(aadhaarData: AadhaarUpload): Boolean = ktorDispatcher {
        client.post<Response.Status>("$REGISTRATION_URL/api/v1/upload/aadhaar") {
            withTempToken(TempToken.REGISTRATION)
            jsonBody(aadhaarData)
        }.isSuccess
    }

    override suspend fun uploadDrugLicense(licenseData: DrugLicenseUpload): Response.Wrapped<StorageKeyResponse> =
        ktorDispatcher {
            client.post<SimpleBody<StorageKeyResponse>>("$REGISTRATION_URL/api/v1/upload/druglicense") {
                withTempToken(TempToken.REGISTRATION)
                jsonBody(licenseData)
            }.getWrappedBody()
        }

    override suspend fun signUp(submitRegistration: SubmitRegistration): Boolean = ktorDispatcher {
        client.post<Response.Status>("$REGISTRATION_URL/api/v1/registration/submit") {
            withTempToken(TempToken.REGISTRATION)
            jsonBody(submitRegistration)
        }.isSuccess
    }

    private inline fun HttpRequestBuilder.withMainToken() {
        token?.let { header("Authorization", "Bearer $it") } ?: "no token for request".warnIt()
    }

    private suspend inline fun HttpRequestBuilder.withTempToken(tokenType: TempToken) {
        retry(Interval.Linear(100, 5)) {
            val tokenInfo = tempTokenMap.remove(tokenType)
                ?.takeIf { Clock.System.now().toEpochMilliseconds() < it.expiresAt() }
            if (tokenInfo == null || tokenInfo.id != tokenType.serverValue) {
                when (tokenType) {
                    TempToken.OTP -> fetchOtpToken()
                    TempToken.REGISTRATION -> fetchRegistrationToken()
                    TempToken.UPDATE_PASSWORD -> tokenInfo
                }?.let {
                    tempTokenMap[tokenType] = it
                }
            }
            tempTokenMap[tokenType]
        }?.let { header("Authorization", "Bearer ${it.token}") }
            ?: "no temp token (${tokenType.serverValue}) for request".warnIt()
    }

    private suspend inline fun fetchOtpToken(): TokenInfo? {
        // todo new otp endpoint
        return client.get<SimpleBody<TokenInfo>>("new url")
            .getBodyOrNull()
    }

    private suspend inline fun fetchRegistrationToken(): TokenInfo? {
        return client.get<SimpleBody<TokenInfo>>("$REGISTRATION_URL/api/v1/registration")
            .getBodyOrNull()
    }

    private inline fun HttpRequestBuilder.jsonBody(body: Any) {
        contentType(ContentType.parse("application/json"))
        this.body = body
    }

    private enum class TempToken(val serverValue: String) {
        //        MAIN("login"),
        OTP("otp"),
        UPDATE_PASSWORD("updatepwd"),
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

