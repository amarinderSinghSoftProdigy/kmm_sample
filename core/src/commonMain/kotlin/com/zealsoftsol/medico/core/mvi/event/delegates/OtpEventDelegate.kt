package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.environment
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.CommonScope.WithErrors
import com.zealsoftsol.medico.core.mvi.scope.EnterNewPasswordScope
import com.zealsoftsol.medico.core.mvi.scope.OtpScope
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
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
        navigator.withCommonScope<CommonScope.PhoneVerificationEntryPoint> {
            if (it is OtpScope.PhoneNumberInput && it.isForRegisteredUsersOnly) {
                val (errorCode, isSuccess) = withProgress {
                    userRepo.checkCanResetPassword(phoneNumber)
                }
                if (!isSuccess) {
                    it.errors.value = errorCode ?: ErrorCode()
                    return
                }
            }
            val (errorCode, isSuccess) = withProgress {
                userRepo.sendOtp(phoneNumber = phoneNumber)
            }
            if (isSuccess) {
                val nextScope = OtpScope.AwaitVerification(phoneNumber = phoneNumber)
                startResetPasswordTimer(nextScope)
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
                when (searchQueueFor<CommonScope.PhoneVerificationEntryPoint>()) {
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
            }
        }
    }

    private suspend fun resendOtp() {
        navigator.withScope<OtpScope.AwaitVerification> {
            it.resendActive.value = false
            val (errorCode, isSuccess) = userRepo.resendOtp(it.phoneNumber)
            if (isSuccess) {
                startResetPasswordTimer(it)
            } else {
                it.resendActive.value = true
                it.errors.value = errorCode ?: ErrorCode()
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