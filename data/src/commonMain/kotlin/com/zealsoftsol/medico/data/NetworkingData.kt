package com.zealsoftsol.medico.data

import kotlinx.datetime.Clock
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
) {
    private val createdAt = Clock.System.now().epochSeconds

    fun expiresAt() = (createdAt + expiresIn) * 1000L
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

@Serializable
data class ValidatedResponseBody<T, V>(
    private val body: T? = null,
    val validation: V? = null,
    val type: String,
) {
    val isSuccess: Boolean
        get() = type == "success"

    fun getBodyOrNull(): T? = body?.takeIf { isSuccess }
}

@Serializable
data class JustResponseBody(
    val type: String,
    val message: String,
) {
    val isSuccess: Boolean
        get() = type == "success"
}