package com.zealsoftsol.medico.data

sealed class UiState {
    val id: String = this::class.simpleName.orEmpty()
}

data class UiStateWithProgress<T: UiState>(val uiState: T, val isInProgress: Boolean)

sealed class PasswordReset : UiState() {

    data class Default(val phoneNumber: String) : PasswordReset()

    data class AwaitVerification(
        val phoneNumber: String,
        val timeBeforeResend: Long,
        val attemptsLeft: Int,
        val isCodeValid: Boolean,
    ) : PasswordReset()

    data class EnterNewPassword(val phoneNumber: String) : PasswordReset()

    object Done : PasswordReset()
}