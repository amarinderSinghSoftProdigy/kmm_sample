package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.EnterNewPasswordScope
import com.zealsoftsol.medico.core.mvi.scope.OtpScope
import com.zealsoftsol.medico.core.mvi.scope.PhoneVerificationEntryPoint
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.core.mvi.scope.WithErrors
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.ErrorCode
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
        navigator.withCommonScope<PhoneVerificationEntryPoint> {
            val (errorCode, isSuccess) = withProgress {
                userRepo.sendOtp(phoneNumber = phoneNumber)
            }
            if (isSuccess) {
                val nextScope = OtpScope.AwaitVerification(phoneNumber = phoneNumber)
                startResetPasswordTimer(nextScope.resendTimer)
                setCurrentScope(nextScope)
            } else {
                (it as? WithErrors)?.errors?.let { errors ->
                    errors.value = errorCode ?: ErrorCode()
                }
            }
        }
    }

    private suspend fun submitOtp(otp: String) {
        navigator.withScope<OtpScope.AwaitVerification> {
            val (errorCode, isSuccess) = withProgress {
                userRepo.submitOtp(it.phoneNumber, otp)
            }
            if (isSuccess) {
                stopResetPasswordTimer()
                dropCurrentScope(updateDataSource = false)
                when (searchQueueFor<PhoneVerificationEntryPoint>()) {
                    is OtpScope.PhoneNumberInput -> {
                        setCurrentScope(
                            EnterNewPasswordScope(
                                phoneNumber = it.phoneNumber,
                                passwordValidation = DataSource(null),
                            )
                        )
                    }
                    is SignUpScope.LegalDocuments -> {
                        EventCollector.sendEvent(Event.Action.Registration.SignUp)
                    }
                    else -> throw UnsupportedOperationException("unknown subtype of PhoneVerificationEntryPoint")
                }
            } else {
                it.errors.value = errorCode ?: ErrorCode()
                it.attemptsLeft.value = it.attemptsLeft.value - 1
            }
        }
    }

    private suspend fun resendOtp() {
        navigator.withScope<OtpScope.AwaitVerification> {
            val (errorCode, isSuccess) = userRepo.resendOtp(it.phoneNumber)
            if (isSuccess) {
                startResetPasswordTimer(it.resendTimer)
            } else {
                it.errors.value = errorCode ?: ErrorCode()
            }
        }
    }

    private fun stopResetPasswordTimer() {
        resetPasswordTimerJob?.cancel()
    }

    private fun startResetPasswordTimer(timer: DataSource<Long>) {
        stopResetPasswordTimer()
        resetPasswordTimerJob = GlobalScope.launch(compatDispatcher) {
            timer.value = OtpScope.AwaitVerification.RESEND_TIMER
            var remainingTime = timer.value
            while (remainingTime > 0) {
                delay(1000)
                remainingTime -= 1000
                timer.value = remainingTime
            }
        }
    }
}