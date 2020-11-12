package com.zealsoftsol.medico.data

data class AuthCredentials(
    val phoneNumberOrEmail: String,
    val password: String
) {
    fun getPhoneNumber(): String? = phoneNumberOrEmail.takeIf { !it.contains("@") }
    fun getEmail(): String? = phoneNumberOrEmail.takeIf { it.contains("@") }
}

enum class AuthState {
    SUCCESS, IN_PROGRESS, ERROR
}

enum class UserType {
    STOCKIST, RETAILER, SEASON_BOY;
}