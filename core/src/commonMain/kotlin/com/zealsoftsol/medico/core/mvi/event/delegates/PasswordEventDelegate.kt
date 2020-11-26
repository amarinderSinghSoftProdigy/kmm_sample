package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.BooleanEvent
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.ForgetPasswordScope
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.core.mvi.viewmodel.AuthViewModel
import com.zealsoftsol.medico.core.mvi.withProgress

internal class PasswordEventDelegate(
    navigator: Navigator,
    private val authViewModel: AuthViewModel,
) : EventDelegate<Event.Action.Password>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Password) = when (event) {
        is Event.Action.Password.SendOtp -> sendOtp(event.phoneNumber)
        is Event.Action.Password.SubmitOtp -> sumbitOtp(event.otp)
        is Event.Action.Password.ResendOtp -> resendOtp()
        is Event.Action.Password.ChangePassword -> changePassword(event.newPassword)
    }

    private suspend fun sendOtp(phoneNumber: String) {
        navigator.withScope<ForgetPasswordScope.PhoneNumberInput> {
            if (withProgress { authViewModel.sendOtp(phoneNumber) }) {
                authViewModel.startResetPasswordTimer()
                setCurrentScope(it.copy(phoneNumber = phoneNumber), false)
                transitionTo(
                    ForgetPasswordScope.AwaitVerification(
                        phoneNumber = phoneNumber,
                        resendTimer = authViewModel.resendTimer,
                    )
                )
            } else {
                setCurrentScope(it.copy(success = BooleanEvent.`false`))
            }
        }
    }

    private suspend fun sumbitOtp(otp: String) {
        navigator.withScope<ForgetPasswordScope.AwaitVerification> {
            if (withProgress { authViewModel.submitOtp(it.phoneNumber, otp) }) {
                authViewModel.stopResetPasswordTimer()
                transitionTo(
                    ForgetPasswordScope.EnterNewPassword(phoneNumber = it.phoneNumber),
                    replaceScope = true
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
            val isSuccess = authViewModel.resendOtp(it.phoneNumber)
            if (isSuccess) authViewModel.startResetPasswordTimer()
            setCurrentScope(it.copy(resendSuccess = BooleanEvent.of(isSuccess)))
        }
    }

    private suspend fun changePassword(newPassword: String) {
        navigator.withScope<ForgetPasswordScope.EnterNewPassword> {
            val (validation, isSuccess) = withProgress {
                authViewModel.changePassword(it.phoneNumber, newPassword)
            }
            setCurrentScope(
                it.copy(
                    success = BooleanEvent.of(isSuccess),
                    passwordValidation = validation,
                )
            )
            if (isSuccess) {
                navigator.transitionTo(LogInScope(authViewModel.credentials), replaceScope = true)
            }
        }
    }
}