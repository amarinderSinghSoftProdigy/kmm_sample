package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.BooleanEvent
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.PasswordValidation

sealed class ForgetPasswordScope : BaseScope(), CanGoBack {

    data class PhoneNumberInput(
        val phoneNumber: String,
        val success: BooleanEvent = BooleanEvent.none,
        override val isInProgress: Boolean = false,
    ) : ForgetPasswordScope() {

        /**
         * Phone number should be formatted with E164 format and should not contain "+"
         * Transition to [AwaitVerification] if successful
         */
        fun sendOtp(phoneNumber: String) =
            EventCollector.sendEvent(Event.Action.Password.SendOtp(phoneNumber))
    }

    data class AwaitVerification(
        val phoneNumber: String,
        val resendTimer: DataSource<Long>,
        val attemptsLeft: Int = MAX_RESEND_ATTEMPTS,
        val codeValidity: BooleanEvent = BooleanEvent.none,
        val resendSuccess: BooleanEvent = BooleanEvent.none,
        override val isInProgress: Boolean = false,
    ) : ForgetPasswordScope() {

        /**
         * Transition to [EnterNewPassword] if successful
         * drops this scope in the process
         */
        fun submitOtp(otp: String) =
            EventCollector.sendEvent(Event.Action.Password.SubmitOtp(otp))

        /**
         * Update current scope, result posted to [resendSuccess]
         */
        fun resendOtp() = EventCollector.sendEvent(Event.Action.Password.ResendOtp)

        companion object {
            const val RESEND_TIMER = 3 * 60 * 1000L
            private const val MAX_RESEND_ATTEMPTS = 3
        }
    }

    data class EnterNewPassword(
        internal val phoneNumber: String,
        val passwordValidation: PasswordValidation? = null,
        val success: BooleanEvent = BooleanEvent.none,
        override val isInProgress: Boolean = false,
    ) : ForgetPasswordScope() {

        /**
         * Transition to [LogInScope] if successful
         */
        fun changePassword(newPassword: String) =
            EventCollector.sendEvent(Event.Action.Password.ChangePassword(newPassword))
    }
}