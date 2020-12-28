package com.zealsoftsol.medico.core.repository

import com.russhwolf.settings.Settings
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.interop.IpAddressFetcher
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.storage.TokenStorage
import com.zealsoftsol.medico.core.utils.PhoneEmailVerifier
import com.zealsoftsol.medico.data.AadhaarData
import com.zealsoftsol.medico.data.AadhaarUpload
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.DrugLicenseUpload
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.PasswordValidation
import com.zealsoftsol.medico.data.PincodeValidation
import com.zealsoftsol.medico.data.Response
import com.zealsoftsol.medico.data.StorageKeyResponse
import com.zealsoftsol.medico.data.SubmitRegistration
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserRequest
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3
import kotlinx.serialization.json.Json

class UserRepo(
    private val networkAuthScope: NetworkScope.Auth,
    private val networkCustomerScope: NetworkScope.Customer,
    private val settings: Settings,
    private val tokenStorage: TokenStorage,
    private val phoneEmailVerifier: PhoneEmailVerifier,
    private val ipAddressFetcher: IpAddressFetcher,
) {
    val user: User?
        get() = fetchUser()

    fun getUserAccess(): UserAccess {
        return fetchUser()?.let {
            if (it.isVerified) UserAccess.FULL_ACCESS else UserAccess.LIMITED_ACCESS
        } ?: UserAccess.NO_ACCESS
    }

    suspend fun login(login: String, password: String): Response.Wrapped<ErrorCode> {
        settings.putString(AUTH_LOGIN_KEY, login)
        return networkAuthScope.login(UserRequest(login, password))
    }

    suspend fun loadUserFromServer(): User? {
        return networkCustomerScope.getCustomerData().entity?.let {
            val parsedType = UserType.parse(it.customerType) ?: run {
                "unknown user type".warnIt()
                return null
            }
            val user = User(
                it.firstName,
                it.lastName,
                it.email,
                it.phoneNumber,
                parsedType,
                it.customerMetaData.activated,
                it.isDocumentUploaded,
            )
            val json = Json.encodeToString(User.serializer(), user)
            settings.putString(AUTH_USER_KEY, json)
            user
        }
    }

    suspend fun logout(): Boolean {
        return networkAuthScope.logout().also { isSuccess ->
            if (isSuccess) {
                clearUserData()
            }
        }
    }

    fun getAuthCredentials(): AuthCredentials {
        val login = settings.getString(AUTH_LOGIN_KEY, "")
        return AuthCredentials(
            login,
            phoneEmailVerifier.verify(login),
            "",
        )
    }

    fun updateAuthCredentials(
        current: AuthCredentials,
        emailOrPhone: String,
        password: String
    ): AuthCredentials {
        return current.copy(
            emailOrPhone,
            phoneEmailVerifier.verify(emailOrPhone),
            password,
        )
    }

    suspend fun checkCanResetPassword(phoneNumber: String): Response.Wrapped<ErrorCode> {
        return networkAuthScope.checkCanResetPassword(phoneNumber)
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

    suspend fun getLocationData(pincode: String): Response.Body<LocationData, PincodeValidation> {
        return networkAuthScope.getLocationData(pincode)
    }

    suspend fun signUpNonSeasonBoy(
        userRegistration1: UserRegistration1,
        userRegistration2: UserRegistration2,
        userRegistration3: UserRegistration3,
        storageKey: String?,
    ): Response.Wrapped<ErrorCode> {
        return networkAuthScope.signUp(
            SubmitRegistration.nonSeasonBoy(
                userRegistration1,
                userRegistration2,
                userRegistration3,
                storageKey,
                ipAddressFetcher.getIpAddress().orEmpty(),
            )
        )
    }

    suspend fun signUpSeasonBoy(
        userRegistration1: UserRegistration1,
        userRegistration2: UserRegistration2,
        aadhaarData: AadhaarData,
        aadhaar: String,
    ): Response.Wrapped<ErrorCode> {
        return networkAuthScope.signUp(
            SubmitRegistration.seasonBoy(
                userRegistration1,
                userRegistration2,
                aadhaarData,
                aadhaar,
                ipAddressFetcher.getIpAddress().orEmpty(),
            )
        )
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
        phoneNumber: String,
        email: String,
    ): Response.Wrapped<StorageKeyResponse> {
        return networkAuthScope.uploadDrugLicense(
            DrugLicenseUpload(
                phoneNumber = phoneNumber,
                email = email,
                fileString = fileString,
                mimeType = mimeType,
            )
        )
    }

    private fun clearUserData() {
        settings.remove(AUTH_USER_KEY)
        tokenStorage.clear()
    }

    private fun fetchUser(): User? {
        val user = runCatching {
            Json.decodeFromString(User.serializer(), settings.getString(AUTH_USER_KEY))
        }.getOrNull()
        if (user == null) "no cached user".warnIt()
        return user
    }

    enum class UserAccess {
        FULL_ACCESS, LIMITED_ACCESS, NO_ACCESS
    }

    companion object {
        // TODO make secure
        private const val AUTH_LOGIN_KEY = "auid"
        private const val AUTH_USER_KEY = "ukey"
    }
}