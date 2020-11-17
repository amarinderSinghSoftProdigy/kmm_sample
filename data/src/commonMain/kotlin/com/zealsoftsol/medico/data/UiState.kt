package com.zealsoftsol.medico.data

sealed class UiState {
    val id: String = this::class.simpleName.orEmpty()
}

data class UiStateWithProgress<T: UiState>(val uiState: T, val isInProgress: Boolean)

sealed class PasswordReset : UiState() {

    data class Default(
        val phoneNumber: String,
        val success: SuccessEvent = SuccessEvent.none,
    ) : PasswordReset()

    data class AwaitVerification(
        val phoneNumber: String,
        val timeBeforeResend: Long,
        val attemptsLeft: Int,
        val codeValidity: SuccessEvent = SuccessEvent.none,
        val resendSuccess: SuccessEvent = SuccessEvent.none,
    ) : PasswordReset()

    data class EnterNewPassword(
        val phoneNumber: String,
        val success: SuccessEvent = SuccessEvent.none,
    ) : PasswordReset()

    object Done : PasswordReset()
}

class SuccessEvent private constructor(private val isSuccess: Boolean, private var wasAccessed: Boolean = false) {

    val value: Boolean?
        get() {
            if (wasAccessed) return null
            wasAccessed = true
            return isSuccess
        }

    inline val isTrue: Boolean
        get() = value == true
    inline val isFalse: Boolean
        get() = value == false

    companion object {
        val `true`: SuccessEvent
            get() = SuccessEvent(true)
        val `false`: SuccessEvent
            get() = SuccessEvent(false)
        internal val none: SuccessEvent
            get() = SuccessEvent(false, true)
    }
}