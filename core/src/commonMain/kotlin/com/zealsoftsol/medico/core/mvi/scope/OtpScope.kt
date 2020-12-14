package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.PasswordValidation

sealed class OtpScope : BaseScope(), CanGoBack {

    data class PhoneNumberInput(
        val phoneNumber: DataSource<String>,
        override val errors: DataSource<ErrorCode?> = DataSource(null),
    ) : OtpScope(), WithErrors, CommonScope.PhoneVerificationEntryPoint {

        fun changePhoneNumber(phoneNumber: String) {
            this.phoneNumber.value = phoneNumber
        }

        /**
         * Phone number should be formatted with E164 format and should not contain "+"
         * Transition to [AwaitVerification] if successful
         */
        fun sendOtp(phoneNumber: String) =
            EventCollector.sendEvent(Event.Action.Otp.Send(phoneNumber))
    }

    data class AwaitVerification(
        val phoneNumber: String,
        val resendTimer: DataSource<Long> = DataSource(RESEND_TIMER),
        val resendActive: DataSource<Boolean> = DataSource(false),
        val attemptsLeft: DataSource<Int> = DataSource(MAX_RESEND_ATTEMPTS + 1),
        override val errors: DataSource<ErrorCode?> = DataSource(null),
    ) : OtpScope(), WithErrors {

        /**
         * Transition to [EnterNewPasswordScope] if successful
         * drops this scope in the process
         */
        fun submitOtp(otp: String) =
            EventCollector.sendEvent(Event.Action.Otp.Submit(otp))

        fun resendOtp() = EventCollector.sendEvent(Event.Action.Otp.Resend)

        companion object {
            const val RESEND_TIMER = 1 * 60 * 1000L
            private const val MAX_RESEND_ATTEMPTS = 3
        }
    }
}

data class EnterNewPasswordScope(
    internal val phoneNumber: String,
    val passwordValidation: DataSource<PasswordValidation?>,
    override val notifications: DataSource<ScopeNotification?> = DataSource(null),
) : BaseScope(), CanGoBack, WithNotifications {

    fun changePassword(newPassword: String) =
        EventCollector.sendEvent(Event.Action.ResetPassword.Send(newPassword))

    /**
     * Transition to [LogInScope]
     */
    fun finishResetPasswordFlow() =
        EventCollector.sendEvent(Event.Action.ResetPassword.Finish)

    object PasswordChangedSuccessfully : ScopeNotification {
        override val title: String
            get() = "success"
        override val body: String
            get() = "password_change_success"
    }
}