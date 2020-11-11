package com.zealsoftsol.medico.data

data class AuthCredentials(
    val phoneNumberOrEmail: String,
    val password: String
)

enum class AuthState {
    SUCCESS, ERROR
}

enum class UserType {
    STOCKIST, RETAILER, SEASON_BOY;
}