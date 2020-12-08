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
    val mimeType: String,
)

@Serializable
data class DrugLicenseUpload(
    @SerialName("mobileNumber")
    val phoneNumber: String,
    val fileString: String,
    val mimeType: String,
)

@Serializable
data class StorageKeyResponse(
    @SerialName("uploadStorageKey")
    val key: String,
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

enum class FileType(val mimeType: String) {
    PNG("image/png"),
    JPEG("image/jpeg"),
    JPG("image/jpg"),
    PDF("application/pdf"),
    ZIP("application/zip");

    companion object {
        fun forDrugLicense() = arrayOf(PDF, PNG, JPEG, JPG)
        fun forAadhaar() = arrayOf(ZIP)

        fun fromExtension(ext: String): FileType? {
            return when (ext) {
                "png" -> PNG
                "jpeg" -> JPEG
                "jpg" -> JPG
                "pdf" -> PDF
                "zip" -> ZIP
                else -> null
            }
        }
    }
}

// BASE

@Serializable
sealed class Response {
    abstract val type: String

    val isSuccess: Boolean
        get() = type == "success"

    @Serializable
    class Status(override val type: String) : Response()

    @Serializable
    class Body<T, V>(
        private val body: T? = null,
        val error: ErrorCode? = null,
        val validations: V? = null,
        override val type: String,
    ) : Response() {

        fun getBodyOrNull(): T? = body.takeIf { isSuccess }

        fun getWrappedBody(): Wrapped<T> = Wrapped(body, isSuccess)

        inline fun getWrappedValidation(): Wrapped<V> = Wrapped(validations, isSuccess)

        inline fun getWrappedError(): Wrapped<ErrorCode> = Wrapped(error, isSuccess)
    }

    data class Wrapped<V>(val entity: V?, val isSuccess: Boolean)
}

typealias SimpleBody<T> = Response.Body<T, MapBody>

typealias MapBody = Map<String, String>

@Serializable
data class ErrorCode(val title: String = "error", val body: String = "something_went_wrong")