package com.zealsoftsol.medico.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Required
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
    @Required
    val userType: String = "",
    @Required
    val firstName: String = "",
    @Required
    val lastName: String = "",
    @Required
    val email: String = "",
    @SerialName("mobileNumber")
    @Required
    val phoneNumber: String = "",
    @Required
    val password: String = "",
    @Required
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
    @Required
    val pincode: String = "",
    @Required
    val addressLine1: String = "",
    @Required
    val location: String = "",
    @SerialName("cityTown")
    @Required
    val city: String = "",
    @Required
    val district: String = "",
    @Required
    val state: String = "",
) : UserRegistration()

@Serializable
data class UserValidation2(
    val addressLine1: String? = null,
    val location: String? = null,
    @SerialName("cityTown")
    val city: String? = null,
    val district: String? = null,
    val state: String? = null,
) : UserValidation()

@Serializable
data class UserRegistration3(
    @Required
    val tradeName: String = "",
    @Required
    val gstin: String = "",
    @Required
    val panNumber: String = "",
    @Required
    val drugLicenseNo1: String = "",
    @Required
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
data class AadhaarUpload(
    @SerialName("aadhaarCardNumber")
    val cardNumber: String,
    val consent: Boolean = true,
    val email: String,
    @SerialName("mobileNumber")
    val phoneNumber: String,
    val shareCode: String,
    @SerialName("uploadAadhaarFile")
    val fileString: String,
)

sealed class Location {
    @Serializable
    data class Data(
        val locations: List<String>,
        @SerialName("cityTowns")
        val cities: List<String>,
        val district: String,
        val state: String,
    ) : Location()

    object Unknown : Location()
}

@Serializable
data class ResponseBody<T>(
    private val body: T? = null,
    val type: String,
) {
    val isSuccess: Boolean
        get() = type == "success"

    fun getBodyOrNull(): T? = body?.takeIf { isSuccess }
}

typealias MapBody = Map<String, String>

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