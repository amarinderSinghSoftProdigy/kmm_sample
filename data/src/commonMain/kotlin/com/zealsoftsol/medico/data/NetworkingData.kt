package com.zealsoftsol.medico.data

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val username: String,
    val password: String,
)

@Serializable
data class PasswordResetRequest(
    val mobileno: String,
    val password: String,
    val verifyPassword: String,
)

@Serializable
data class PasswordValidation(
    val password: String? = null
)

@Serializable
@Deprecated("should be removed from server")
data class TempOtpRequest(
    val mobileno: String,
)

@Serializable
data class OtpRequest(
    val mobileNumber: String,
)

@Serializable
data class VerifyOtpRequest(
    val mobileNumber: String,
    val otp: String,
)

@Serializable
data class UserInfo(
    val token: String,
    val tokenType: String,
    val userType: String,
    val traderHeader: String,
    val traderFooter: String,
    val expiresIn: Int,
    val stockistLogo: String,
    val retailerLogo: String,
    val seasonBoyLogo: String,
    val medicoStoresLogo: String,
)

@Serializable
data class TokenInfo(
    val token: String,
    private val expiresIn: Long,
    val tokenType: String,
    val id: String,
) {
    private val createdAt = Clock.System.now().epochSeconds

    fun expiresAt() = (createdAt + expiresIn) * 1000L
}

sealed class UserRegistration
sealed class UserValidation

@Serializable
data class UserRegistration1(
    @SerialName("customerType")
    val userType: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    @SerialName("mobileNumber")
    val phoneNumber: String = "",
    val password: String = "",
    val verifyPassword: String = "",
) : UserRegistration()

@Serializable
data class UserValidation1(
    val email: String? = null,
    @SerialName("mobileNumber")
    val phoneNumber: String? = null,
    val password: String? = null,
    val verifyPassword: String? = null,
) : UserValidation()

@Serializable
data class UserRegistration2(
    val pincode: String = "",
    val addressLine1: String = "",
    val location: String = "",
    val cityTown: String = "",
    val district: String = "",
    val state: String = "",
) : UserRegistration()

@Serializable
data class UserValidation2(
    val pincode: String? = null,
    val addressLine1: String? = null,
    val location: String? = null,
    val cityTown: String? = null,
    val district: String? = null,
    val state: String? = null,
) : UserValidation()

@Serializable
data class UserRegistration3(
    val tradeName: String = "",
    val gstin: String = "",
    val panNumber: String = "",
    val drugLicenseNo1: String = "",
    val drugLicenseNo2: String = "",
) : UserRegistration()

@Serializable
data class UserValidation3(
    val tradeName: String? = null,
    val gstin: String? = null,
    val panNumber: String? = null,
    val drugLicenseNo1: String? = null,
    val drugLicenseNo2: String? = null,
) : UserValidation()


@Serializable
data class ResponseBody<T>(
    private val body: T? = null,
    val type: String,
) {
    val isSuccess: Boolean
        get() = type == "success"

    fun getBodyOrNull(): T? = body?.takeIf { isSuccess }
}

@Serializable
data class ValidatedResponseBody<T, V>(
    private val body: T? = null,
    private val validations: V? = null,
    val type: String,
) {
    val isSuccess: Boolean
        get() = type == "success"

    fun getBodyOrNull(): T? = body?.takeIf { isSuccess }

    fun getValidationData(): ValidationData<V> = ValidationData(validations, isSuccess)
}

data class ValidationData<V>(val validation: V?, val isSuccess: Boolean)

@Serializable
data class JustResponseBody(
    val type: String,
    val message: String? = null,
) {
    val isSuccess: Boolean
        get() = type == "success"
}