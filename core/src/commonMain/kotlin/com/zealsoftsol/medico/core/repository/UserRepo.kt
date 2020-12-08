package com.zealsoftsol.medico.core.repository

import com.russhwolf.settings.Settings
import com.zealsoftsol.medico.core.extensions.errorIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.utils.PhoneEmailVerifier
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState
import com.zealsoftsol.medico.data.DrugLicenseUpload
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.Location
import com.zealsoftsol.medico.data.PasswordValidation
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.StorageKeyResponse
import com.zealsoftsol.medico.data.SubmitRegistration
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3
import kotlinx.serialization.json.Json

class UserRepo(
    private val networkAuthScope: NetworkScope.Auth,
    private val settings: Settings,
    private val phoneEmailVerifier: PhoneEmailVerifier,
) {
    val authState: AuthState
        get() = AuthState.fromKey(settings.getString(AUTH_STATE, AuthState.NOT_AUTHORIZED.key))

    init {
        if (authState == AuthState.AUTHORIZED) {
            fetchUser()?.let {
                networkAuthScope.token = it.token
            }
        }
    }

    suspend fun login(authCredentials: AuthCredentials): Boolean {
        settings.putString(AUTH_ID_KEY, authCredentials.phoneNumberOrEmail)
        settings.putString(AUTH_PASS_KEY, authCredentials.password)
        val (tokenInfo, isSuccess) = networkAuthScope.login(
            UserRequest(authCredentials.phoneNumberOrEmail, authCredentials.password)
        )
        tokenInfo?.let {
            networkAuthScope.token = it.token
            val json = Json.encodeToString(
                User.serializer(),
                User(
                    authCredentials.getEmail(),
                    authCredentials.getPhoneNumber().orEmpty(),
                    it.token,
                    "",
                    authCredentials.password
                )
            )
            settings.putString(AUTH_USER_KEY, json)
            settings.putString(AUTH_STATE, AuthState.AUTHORIZED.key)
            true
        }
        return isSuccess
    }

    suspend fun logout(): Boolean {
        return networkAuthScope.logout().also { isSuccess ->
            if (isSuccess) {
                settings.remove(AUTH_USER_KEY)
                settings.remove(AUTH_STATE)
                networkAuthScope.clearToken()
            }
        }
    }

    fun getAuthCredentials(): AuthCredentials {
        val id = settings.getString(AUTH_ID_KEY, "")
        return AuthCredentials(
            id,
            phoneEmailVerifier.verify(id),
            settings.getString(AUTH_PASS_KEY, ""),
        )
    }

    fun updateAuthCredentials(current: AuthCredentials, emailOrPhone: String, password: String): AuthCredentials {
        return current.copy(
            emailOrPhone,
            phoneEmailVerifier.verify(emailOrPhone),
            password,
        )
    }

    suspend fun sendOtp(phoneNumber: String): Response.Wrapped<ErrorCode> {
        return networkAuthScope.sendOtp(phoneNumber)
    }

    suspend fun submitOtp(phoneNumber: String, otp: String): Response.Wrapped<ErrorCode> {
        return networkAuthScope.verifyOtp(phoneNumber, otp)
    }

    suspend fun changePassword(
        phoneNumber: String,
        newPassword: String
    ): Response.Wrapped<PasswordValidation> {
        return networkAuthScope.changePassword(phoneNumber, newPassword)
    }

    suspend fun resendOtp(phoneNumber: String): Response.Wrapped<ErrorCode> {
        return networkAuthScope.retryOtp(phoneNumber)
    }

    suspend fun signUpValidation1(userRegistration1: UserRegistration1): Response.Wrapped<UserValidation1> {
        return networkAuthScope.signUpValidation1(userRegistration1)
    }

    suspend fun signUpValidation2(userRegistration2: UserRegistration2): Response.Wrapped<UserValidation2> {
        return networkAuthScope.signUpValidation2(userRegistration2)
    }

    suspend fun signUpValidation3(userRegistration3: UserRegistration3): Response.Wrapped<UserValidation3> {
        return networkAuthScope.signUpValidation3(userRegistration3)
    }

    suspend fun getLocationData(pincode: String): Location {
        return networkAuthScope.getLocationData(pincode).entity ?: Location.Unknown
    }

    suspend fun signUp(
        userRegistration1: UserRegistration1,
        userRegistration2: UserRegistration2,
        userRegistration3: UserRegistration3,
        storageKey: String?,
    ): Boolean {
        val isSuccess = networkAuthScope.signUp(
            SubmitRegistration.from(
                userRegistration1,
                userRegistration2,
                userRegistration3,
                storageKey
            )
        )
        if (isSuccess) {
            settings.putString(AUTH_STATE, AuthState.PENDING_VERIFICATION.key)
        }
        return isSuccess
    }

    suspend fun uploadAadhaar(
        aadhaar: AadhaarData,
        fileString: String,
        email: String,
        phoneNumber: String
    ): Boolean {
        return networkAuthScope.uploadAadhaar(
            AadhaarUpload(
                cardNumber = aadhaar.cardNumber,
                shareCode = aadhaar.shareCode,
                email = email,
                phoneNumber = phoneNumber,
                fileString = fileString,
            )
        )
    }

    suspend fun uploadDrugLicense(
        fileString: String,
        mimeType: String,
        phoneNumber: String
    ): Response.Wrapped<StorageKeyResponse> {
        return networkAuthScope.uploadDrugLicense(
            DrugLicenseUpload(
                phoneNumber = phoneNumber,
                fileString = fileString,
                mimeType = mimeType,
            )
        )
    }

    private fun fetchUser(): User? {
        val user = runCatching {
            Json.decodeFromString(User.serializer(), settings.getString(AUTH_USER_KEY))
        }.getOrNull()
        if (user == null) "error fetching user".errorIt()
        return user
    }

    companion object {
        // TODO make secure
        private const val AUTH_ID_KEY = "auid"
        private const val AUTH_PASS_KEY = "apass"
        private const val AUTH_USER_KEY = "ukey"
        private const val AUTH_STATE = "ast"
    }
}