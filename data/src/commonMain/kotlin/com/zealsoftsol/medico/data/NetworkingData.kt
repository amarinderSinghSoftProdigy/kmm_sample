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

@Serializable
data class DrugLicenseUpload(
    @SerialName("mobileNumber")
    val phoneNumber: String,
    @SerialName("uploadDrugLicenseFile")
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
    ZIP("application/zip"),
    XZIP("multipart/x-zip"),
    UNKNOWN("*/*");

    companion object {
        fun forDrugLicense() = arrayOf(PDF, PNG, JPEG, JPG)
        fun forAadhaar() = arrayOf(ZIP, XZIP)

        fun fromExtension(ext: String): FileType {
            return when (ext) {
                "png" -> PNG
                "jpeg" -> JPEG
                "jpg" -> JPG
                "pdf" -> PDF
                "zip" -> ZIP
                else -> UNKNOWN
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