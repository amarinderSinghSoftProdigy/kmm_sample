package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.environment
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.PasswordValidation

sealed class OtpScope : BaseScope(), CommonScope.CanGoBack {

    data class PhoneNumberInput(
        val phoneNumber: DataSource<String>,
        internal val isForRegisteredUsersOnly: Boolean,
        override val errors: DataSource<ErrorCode?> = DataSource(null),
    ) : OtpScope(), CommonScope.WithErrors, CommonScope.PhoneVerificationEntryPoint {

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
        val resendTimer: DataSource<Long> = DataSource(environment.otp.resendTimer),
        val resendActive: DataSource<Boolean> = DataSource(false),
        val attemptsLeft: DataSource<Int> = DataSource(environment.otp.maxResendAttempts + 1),
        override val errors: DataSource<ErrorCode?> = DataSource(null),
    ) : OtpScope(), CommonScope.WithErrors {

        /**
         * Transition to [EnterNewPasswordScope] if successful
         * drops this scope in the process
         */
        fun submitOtp(otp: String) =
            EventCollector.sendEvent(Event.Action.Otp.Submit(otp))

        fun resendOtp() = EventCollector.sendEvent(Event.Action.Otp.Resend)
    }
}

data class EnterNewPasswordScope(
    internal val phoneNumber: String,
    val passwordValidation: DataSource<PasswordValidation?>,
    override val notifications: DataSource<ScopeNotification?> = DataSource(null),
) : BaseScope(), CommonScope.CanGoBack, CommonScope.WithNotifications {

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