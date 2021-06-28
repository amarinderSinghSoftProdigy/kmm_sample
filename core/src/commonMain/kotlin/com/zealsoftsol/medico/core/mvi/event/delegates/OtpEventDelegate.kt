package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.environment
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OtpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.PasswordScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class OtpEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Otp>(navigator) {

    private var resetPasswordTimerJob: Job? = null

    override suspend fun handleEvent(event: Event.Action.Otp) = when (event) {
        is Event.Action.Otp.Send -> sendOtp(event.phoneNumber)
        is Event.Action.Otp.Submit -> submitOtp(event.otp)
        is Event.Action.Otp.Resend -> resendOtp()
    }

    private suspend fun sendOtp(phoneNumber: String) {
        navigator.withScope<CommonScope.PhoneVerificationEntryPoint> {
            if (it is OtpScope.PhoneNumberInput && it.isForRegisteredUsersOnly) {
                withProgress {
                    userRepo.checkCanResetPassword(phoneNumber)
                }.onSuccess {
                    withProgress {
                        userRepo.sendOtp(phoneNumber = phoneNumber)
                    }.onSuccess {
                        val nextScope = OtpScope.AwaitVerification(phoneNumber = phoneNumber)
                        startResetPasswordTimer(nextScope)
                        setScope(nextScope)
                    }.onError(navigator)
                }.onError(navigator)
            }
        }
    }

    private suspend fun submitOtp(otp: String) {
        navigator.withScope<OtpScope.AwaitVerification> {
            withProgress {
                userRepo.submitOtp(it.phoneNumber, otp)
            }.onSuccess { _ ->
                stopResetPasswordTimer()
                dropScope(updateDataSource = false)
                when (searchQueuesFor<CommonScope.PhoneVerificationEntryPoint>()) {
                    is OtpScope.PhoneNumberInput -> {
                        setScope(PasswordScope.EnterNew(it.phoneNumber))
                    }
                    is SignUpScope.LegalDocuments -> {
                        EventCollector.sendEvent(Event.Action.Registration.SignUp)
                    }
                    else -> throw UnsupportedOperationException("unknown subtype of PhoneVerificationEntryPoint")
                }
            }.onError(navigator)
        }
    }

    private suspend fun resendOtp() {
        navigator.withScope<OtpScope.AwaitVerification> {
            it.resendActive.value = false
            userRepo.resendOtp(it.phoneNumber)
                .onSuccess { _ ->
                    startResetPasswordTimer(it)
                }.onError { error ->
                    it.resendActive.value = true
                    setHostError(error)
                }
        }
    }

    private fun stopResetPasswordTimer() {
        resetPasswordTimerJob?.cancel()
    }

    private fun startResetPasswordTimer(scope: OtpScope.AwaitVerification) {
        stopResetPasswordTimer()
        scope.resendActive.value = false
        scope.attemptsLeft.value = scope.attemptsLeft.value - 1
        val timer = scope.resendTimer
        resetPasswordTimerJob = GlobalScope.launch(compatDispatcher) {
            timer.value = environment.otp.resendTimer
            var remainingTime = timer.value
            while (remainingTime > 0) {
                delay(1000)
                remainingTime -= 1000
                timer.value = remainingTime
            }
            scope.resendActive.value = true
        }
    }
}