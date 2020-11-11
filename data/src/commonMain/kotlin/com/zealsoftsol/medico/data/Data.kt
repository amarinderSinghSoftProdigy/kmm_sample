package com.zealsoftsol.medico.data

data class AuthCredentials(
    val phoneNumberOrEmail: String,
    val password: String
)

sealed class AuthState {
    object Success : AuthState()

    sealed class Error : AuthState() {
        object SomeError : Error()
    }
}

enum class UserType {
    STOCKIST, RETAILER, SEASON_BOY;
}