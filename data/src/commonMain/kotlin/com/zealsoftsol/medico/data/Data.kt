package com.zealsoftsol.medico.data

data class AuthCredentials(
    val phoneNumberOrEmail: String,
    val type: Type?,
    val password: String,
) {
    fun getPhoneNumber(): String? = phoneNumberOrEmail.takeIf { type == Type.PHONE }
    fun getEmail(): String? = phoneNumberOrEmail.takeIf { type == Type.EMAIL }

    enum class Type {
        EMAIL, PHONE
    }
}

enum class AuthState {
    AUTHORIZED, NOT_AUTHORIZED
}

enum class UserType {
    STOCKIST, RETAILER, SEASON_BOY;
}