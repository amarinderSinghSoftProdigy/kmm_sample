package com.zealsoftsol.medico.core.data

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
