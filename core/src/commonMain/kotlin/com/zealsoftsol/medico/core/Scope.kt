package com.zealsoftsol.medico.core

sealed class Scope {
    abstract val isInProgress: Boolean
    val queueId: String = this::class.simpleName.orEmpty()

    internal fun changeProgress(value: Boolean): Scope {
        return when (this) {
            is LogIn -> copy(isInProgress = value)
            is Main -> copy(isInProgress = value)
            is ForgetPassword.PhoneNumberInput -> copy(isInProgress = value)
            is ForgetPassword.AwaitVerification -> copy(isInProgress = value)
            is ForgetPassword.EnterNewPassword -> copy(isInProgress = value)
        }
    }

    data class LogIn(
        val success: BooleanEvent = BooleanEvent.none,
        override val isInProgress: Boolean = false,
    ) : Scope() {

        internal fun goToMain() {
            Navigator.requests.offer(Navigator.Request.GoTo(Main()))
        }

        fun goToForgetPassword() {
            Navigator.requests.offer(Navigator.Request.GoTo(ForgetPassword.PhoneNumberInput()))
        }

        fun goToSignUp() {

        }
    }

    data class Main(
        override val isInProgress: Boolean = false,
    ) : Scope() {

        internal fun goToLogin() {
            Navigator.requests.offer(Navigator.Request.GoTo(LogIn()))
        }
    }

    sealed class ForgetPassword : Scope() {

        fun goBack() {
            Navigator.requests.offer(Navigator.Request.GoBack)
        }

        data class PhoneNumberInput(
            val success: BooleanEvent = BooleanEvent.none,
            override val isInProgress: Boolean = false,
        ) : ForgetPassword() {

            internal fun goToAwaitVerification(phoneNumber: String) {
                Navigator.requests.offer(Navigator.Request.GoTo(AwaitVerification(phoneNumber)))
            }
        }

        data class AwaitVerification(
            val phoneNumber: String,
            val timeBeforeResend: Long = RESEND_TIMER,
            val attemptsLeft: Int = MAX_RESEND_ATTEMPTS,
            val codeValidity: BooleanEvent = BooleanEvent.none,
            val resendSuccess: BooleanEvent = BooleanEvent.none,
            override val isInProgress: Boolean = false,
        ) : ForgetPassword() {

            internal fun goToEnterNewPassword(phoneNumber: String) {
                Navigator.requests.offer(
                    Navigator.Request.GoTo(
                        EnterNewPassword(phoneNumber),
                        replaceScope = true
                    )
                )
            }

            companion object {
                const val RESEND_TIMER = 3 * 60 * 1000L
                private const val MAX_RESEND_ATTEMPTS = 3
            }
        }

        data class EnterNewPassword(
            val phoneNumber: String,
            val success: BooleanEvent = BooleanEvent.none,
            override val isInProgress: Boolean = false,
        ) : ForgetPassword() {

            internal fun goToLogin() {
                Navigator.requests.offer(Navigator.Request.GoTo(LogIn()))
            }
        }
    }
}