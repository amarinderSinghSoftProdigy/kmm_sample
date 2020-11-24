package com.zealsoftsol.medico.core.viewmodel

import com.zealsoftsol.medico.core.BooleanEvent
import com.zealsoftsol.medico.core.Navigator
import com.zealsoftsol.medico.core.Scope
import com.zealsoftsol.medico.core.Scope.ForgetPassword.AwaitVerification.Companion.RESEND_TIMER
import com.zealsoftsol.medico.core.extensions.ifTrue
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.viewmodel.interfaces.AuthViewModel
import com.zealsoftsol.medico.core.withProgress
import com.zealsoftsol.medico.data.AuthCredentials
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class AuthViewModelImpl(
    private val userRepo: UserRepo,
    private val navigator: Navigator,
) : BaseViewModel(), AuthViewModel {

    override val credentials: DataSource<AuthCredentials> = DataSource(userRepo.getAuthCredentials())

    private var resetPasswordTimerJob: Job? = null

    // LOG IN ======================================================================================

    override fun tryLogIn() {
        uniqueJob("tryLogIn") {
            val isSuccess = navigator.withProgress {
                userRepo.login(credentials.value)
            }
            navigator.withScope<Scope.LogIn> {
                if (isSuccess) {
                    goToMain()
                } else {
                    navigator.updateScope(copy(success = BooleanEvent.`false`))
                }
            }
        }
    }

    override fun logOut() {
        uniqueJob("logout") {
            navigator.withProgress {
                userRepo.logout()
            }.ifTrue {
                navigator.withScope<Scope.Main> { goToLogin() }
            }
        }
    }

    override fun updateAuthCredentials(emailOrPhone: String, password: String) {
        credentials.value = userRepo.updateAuthCredentials(credentials.value, emailOrPhone, password)
    }

    // PASSWORD RESET ==============================================================================

    override fun sendOtp(phoneNumber: String) {
        uniqueJob("sendOtp") {
            val isSuccess = navigator.withProgress {
                userRepo.sendOtp(phoneNumber)
            }
            navigator.withScope<Scope.ForgetPassword.PhoneNumberInput> {
                if (isSuccess) {
                    goToAwaitVerification(phoneNumber)
                    startResetPasswordTimer()
                } else {
                    navigator.updateScope(copy(success = BooleanEvent.`false`))
                }
            }
        }
    }

    override fun submitOtp(otp: String) {
        uniqueJob("submitOtp") {
            navigator.withScope<Scope.ForgetPassword.AwaitVerification> {
                val isSuccess = navigator.withProgress {
                    userRepo.submitOtp(phoneNumber, otp)
                }
                if (isSuccess) {
                    resetPasswordTimerJob?.cancel()
                    goToEnterNewPassword(phoneNumber)
                } else {
                    navigator.updateScope(copy(codeValidity = BooleanEvent.`false`, attemptsLeft = attemptsLeft - 1))
                }
            }
        }
    }

    override fun resendOtp() {
        uniqueJob("resendOtp") {
            navigator.withScope<Scope.ForgetPassword.AwaitVerification> {
                val isSuccess = userRepo.resendOtp(phoneNumber)
                if (isSuccess) startResetPasswordTimer()
                navigator.updateScope(copy(resendSuccess = BooleanEvent.of(isSuccess)))
            }
        }
    }

    override fun changePassword(newPassword: String) {
        uniqueJob("changePassword") {
            navigator.withScope<Scope.ForgetPassword.EnterNewPassword> {
                val isSuccess = navigator.withProgress {
                    userRepo.changePassword(phoneNumber, newPassword)
                }
                navigator.updateScope(copy(success = BooleanEvent.of(isSuccess)))
                if (isSuccess) goToLogin()
            }
        }
    }

    private fun startResetPasswordTimer() {
        resetPasswordTimerJob?.cancel()
        resetPasswordTimerJob = launch {
            var remainingTime = RESEND_TIMER
            while (remainingTime > 0) {
                delay(1000)
                remainingTime -= 1000
                val isSuccess = navigator.withScope<Scope.ForgetPassword.AwaitVerification > {
                    navigator.updateScope(copy(timeBeforeResend = remainingTime))
                }
                if (!isSuccess) cancel()
            }
        }
    }
}