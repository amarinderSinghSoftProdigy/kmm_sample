package com.zealsoftsol.medico.data

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
data class PasswordResetRequest2(
    val newPassword: String,
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
data class RefreshTokenRequest(
    val refreshToken: String,
)

@Serializable
data class TokenInfo(
    val token: String,
    val expiresAt: Long,
    val tokenType: String,
    val id: String,
    val refreshToken: String,
)

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
    val email: String,
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