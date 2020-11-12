package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val username: String,
    val password: String
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
    val medicoStoresLogo: String
)

@Serializable
data class ResponseBody<T>(
    private val body: T,
    val status: String,
    val message: String
) {
    val isSuccess: Boolean
        get() = status == "success"

    fun getBodyOrNull(): T? = body.takeIf { isSuccess }
}