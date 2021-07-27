package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.environment
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.core.utils.StringResource

sealed class OtpScope(private val titleId: String) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(titleId))
    }

    class PhoneNumberInput private constructor(
        val phoneNumber: DataSource<String>,
        internal val isForRegisteredUsersOnly: Boolean,
    ) : OtpScope("password_reset"),
        CommonScope.PhoneVerificationEntryPoint {

        fun changePhoneNumber(phoneNumber: String) {
            this.phoneNumber.value = phoneNumber
        }

        /**
         * Phone number should be formatted with E164 format and should not contain "+"
         * Transition to [AwaitVerification] if successful
         */
        fun sendOtp(phoneNumber: String) =
            EventCollector.sendEvent(Event.Action.Otp.Send(phoneNumber))

        companion object {
            fun get(
                phoneNumber: DataSource<String>,
                isForRegisteredUsersOnly: Boolean,
            ): TabBarScope {
                return TabBarScope(
                    childScope = PhoneNumberInput(phoneNumber, isForRegisteredUsersOnly),
                    initialNavigationSection = null,
                    initialTabBarInfo = TabBarInfo.Simple(icon = ScopeIcon.BACK, title = null)
                )
            }
        }
    }

    class AwaitVerification(
        val phoneNumber: String,
        val resendTimer: DataSource<Long> = DataSource(environment.otp.resendTimer),
        val resendActive: DataSource<Boolean> = DataSource(false),
        val attemptsLeft: DataSource<Int> = DataSource(environment.otp.maxResendAttempts + 1),
    ) : OtpScope("phone_verification") {

        /**
         * Transition to [EnterNewPasswordScope] if successful
         * drops this scope in the process
         */
        fun submitOtp(otp: String) =
            EventCollector.sendEvent(Event.Action.Otp.Submit(otp))

        fun resendOtp() = EventCollector.sendEvent(Event.Action.Otp.Resend)
    }
}