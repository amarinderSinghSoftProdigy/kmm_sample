package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.BooleanEvent
import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.ForgetPasswordScope
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class PasswordEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Password>(navigator) {

    private var resetPasswordTimerJob: Job? = null

    override suspend fun handleEvent(event: Event.Action.Password) = when (event) {
        is Event.Action.Password.SendOtp -> sendOtp(event.phoneNumber)
        is Event.Action.Password.SubmitOtp -> submitOtp(event.otp)
        is Event.Action.Password.ResendOtp -> resendOtp()
        is Event.Action.Password.ChangePassword -> changePassword(event.newPassword)
    }

    private suspend fun sendOtp(phoneNumber: String) {
        navigator.withScope<ForgetPasswordScope.PhoneNumberInput> {
            if (withProgress { userRepo.sendOtp(phoneNumber) }) {
                val nextScope = ForgetPasswordScope.AwaitVerification(phoneNumber = phoneNumber)
                startResetPasswordTimer(nextScope.resendTimer)
                setCurrentScope(it.copy(phoneNumber = phoneNumber), false)
                setCurrentScope(nextScope)
            } else {
                setCurrentScope(it.copy(success = BooleanEvent.`false`))
            }
        }
    }

    private suspend fun submitOtp(otp: String) {
        navigator.withScope<ForgetPasswordScope.AwaitVerification> {
            if (withProgress { userRepo.submitOtp(it.phoneNumber, otp) }) {
                stopResetPasswordTimer()
                dropCurrentScope(updateDataSource = false)
                setCurrentScope(
                    ForgetPasswordScope.EnterNewPassword(phoneNumber = it.phoneNumber)
                )
            } else {
                setCurrentScope(
                    it.copy(codeValidity = BooleanEvent.`false`, attemptsLeft = it.attemptsLeft - 1)
                )
            }
        }
    }

    private suspend fun resendOtp() {
        navigator.withScope<ForgetPasswordScope.AwaitVerification> {
            val isSuccess = userRepo.resendOtp(it.phoneNumber)
            if (isSuccess) startResetPasswordTimer(it.resendTimer)
            setCurrentScope(it.copy(resendSuccess = BooleanEvent.of(isSuccess)))
        }
    }

    private suspend fun changePassword(newPassword: String) {
        navigator.withScope<ForgetPasswordScope.EnterNewPassword> {
            val (validation, isSuccess) = withProgress {
                userRepo.changePassword(it.phoneNumber, newPassword)
            }
            setCurrentScope(
                it.copy(
                    success = BooleanEvent.of(isSuccess),
                    passwordValidation = validation,
                )
            )
            if (isSuccess) {
                clearQueue()
                setCurrentScope(LogInScope(DataSource(userRepo.getAuthCredentials())))
            }
        }
    }

    private fun stopResetPasswordTimer() {
        resetPasswordTimerJob?.cancel()
    }

    private fun startResetPasswordTimer(timer: DataSource<Long>) {
        stopResetPasswordTimer()
        resetPasswordTimerJob = GlobalScope.launch(compatDispatcher) {
            timer.value = ForgetPasswordScope.AwaitVerification.RESEND_TIMER
            var remainingTime = timer.value
            while (remainingTime > 0) {
                delay(1000)
                remainingTime -= 1000
                timer.value = remainingTime
            }
        }
    }
}