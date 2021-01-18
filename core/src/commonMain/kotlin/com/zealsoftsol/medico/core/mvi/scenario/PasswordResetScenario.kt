package com.zealsoftsol.medico.core.mvi.scenario

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Environment
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.environment
import com.zealsoftsol.medico.core.mvi.scope.nested.OtpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.PasswordScope
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.data.AuthCredentials

class PasswordResetScenario : Scenario() {

    init {
        Environment.Override.otp(Environment.Otp(resendTimer = 10 * 1000L))
    }

    fun successfulReset(phoneNumber: String) = start {
        startingScenario(phoneNumber)
        withScope<OtpScope.AwaitVerification> {
            it.submitOtp("123456")
        }
        pause()
        withScope<PasswordScope.EnterNew> {
            it.changePassword("Qwerty12345")
            pause()
            require(it.notifications.value == PasswordScope.EnterNew.PasswordChangedSuccessfully)
            it.finishPasswordFlow()
        }
        pause()
        withScope<LogInScope> {}
    }

    fun successfulResetWithResend(phoneNumber: String) = start {
        startingScenario(phoneNumber)
        withScope<OtpScope.AwaitVerification> {
            require(it.resendTimer.value != 0L)
            require(!it.resendActive.value)
            pause(environment.otp.resendTimer)
            require(it.resendTimer.value == 0L)
            require(it.resendActive.value)
            val attempts = it.attemptsLeft.value
            it.resendOtp()
            pause()
            require(it.resendTimer.value != 0L)
            require(!it.resendActive.value)
            require(attempts == it.attemptsLeft.value + 1)
            it.submitOtp("123456")
        }
        pause()
        withScope<PasswordScope.EnterNew> {
            it.changePassword("Qwerty12345")
            pause()
            require(it.notifications.value == PasswordScope.EnterNew.PasswordChangedSuccessfully)
            it.finishPasswordFlow()
        }
        pause()
        withScope<LogInScope> {}
    }

    private suspend fun Navigator.startingScenario(phoneNumber: String) {
        setScope(LogInScope(DataSource(AuthCredentials("", null, ""))))
        pause()
        withScope<LogInScope> { it.goToForgetPassword() }
        withScope<OtpScope.PhoneNumberInput> {
            it.changePhoneNumber(phoneNumber)
            pause()
            it.sendOtp(phoneNumber)
        }
        pause(longDelay)
    }
}