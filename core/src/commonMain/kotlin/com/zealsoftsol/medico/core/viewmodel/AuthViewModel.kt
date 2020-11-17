package com.zealsoftsol.medico.core.viewmodel

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.utils.UiStateHandler
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState
import com.zealsoftsol.medico.data.PasswordReset
import com.zealsoftsol.medico.data.UiStateWithProgress
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class AuthViewModel(
    private val userRepo: UserRepo,
) : BaseViewModel(), AuthViewModelFacade {

    override val credentials: DataSource<AuthCredentials> = DataSource(userRepo.getAuthCredentials())
    override val authState: DataSource<AuthState?> = DataSource(if (userRepo.isLoggedIn) AuthState.SUCCESS else null)

    private val resetPasswordStateHandler: UiStateHandler<PasswordReset> = UiStateHandler(PasswordReset.Default(credentials.value.getPhoneNumber().orEmpty()))
    override val resetPasswordUiState: DataSource<UiStateWithProgress<PasswordReset>> = resetPasswordStateHandler.dataSource
    private var resetPasswordTimerJob: Job? = null

    // LOG IN ======================================================================================
    override fun tryLogIn() {
        uniqueJob("login") {
            launch {
                authState.value = AuthState.IN_PROGRESS
                authState.value = if (userRepo.login(credentials.value)) {
                    AuthState.SUCCESS
                } else {
                    AuthState.ERROR
                }
            }
        }
    }

    override fun logOut() {
        uniqueJob("logout") {
            launch {
                authState.value = AuthState.IN_PROGRESS
                authState.value = if (userRepo.logout()) {
                    null
                } else {
                    AuthState.SUCCESS
                }
            }
        }
    }

    override fun updateAuthCredentials(emailOrPhone: String, password: String) {
        credentials.value = userRepo.updateAuthCredentials(credentials.value, emailOrPhone, password)
    }

    override fun clearAuthState() {
        authState.value = null
    }

    // PASSWORD RESET ==============================================================================

    override fun sendOtp(phoneNumber: String) {
        uniqueJob("sendOtp") {
            launch {
                resetPasswordStateHandler.setProgress(true)
                if (userRepo.sendOtp(phoneNumber)) {
                    resetPasswordStateHandler.setProgress(false)
                    resetPasswordStateHandler.newState(
                        PasswordReset.AwaitVerification(
                            phoneNumber = phoneNumber,
                            timeBeforeResend = RESEND_TIMER,
                            attemptsLeft = MAX_RESEND_ATTEMPTS,
                            isCodeValid = true,
                        )
                    )
                    resetPasswordTimerJob?.cancel()
                    resetPasswordTimerJob = launch {
                        var remainingTime = RESEND_TIMER
                        while (remainingTime > 0) {
                            delay(1000)
                            remainingTime -= 1000
                            val isSuccess = resetPasswordStateHandler.updateUiState<PasswordReset.AwaitVerification> {
                                copy(timeBeforeResend = remainingTime)
                            }
                            if (!isSuccess) cancel()
                        }
                    }
                } else {
                    resetPasswordStateHandler.setProgress(false)
                }
            }
        }
    }

    override fun submitOtp(otp: String) {
        uniqueJob("submitOtp") {
            launch {
                resetPasswordStateHandler.setProgress(true)
                val phoneNumber = (resetPasswordStateHandler.dataSource.value.uiState as PasswordReset.AwaitVerification).phoneNumber
                val newUiState = if (userRepo.submitOtp(phoneNumber, otp)) {
                    resetPasswordTimerJob?.cancel()
                    resetPasswordStateHandler.dropCurrentState()
                    PasswordReset.EnterNewPassword(phoneNumber)
                } else {
                    (resetPasswordStateHandler.dataSource.value.uiState as PasswordReset.AwaitVerification).let {
                        it.copy(isCodeValid = false, attemptsLeft = it.attemptsLeft - 1)
                    }
                }
                resetPasswordStateHandler.newState(newUiState)
            }
        }
    }

    override fun resendOtp() {
        uniqueJob("resendOtp") {
            launch {
                (resetPasswordStateHandler.dataSource.value.uiState as? PasswordReset.AwaitVerification)?.let {
                    userRepo.resendOtp(it.phoneNumber)
                }
            }
        }
    }

    override fun changePassword(newPassword: String) {
        uniqueJob("changePassword") {
            launch {
                resetPasswordStateHandler.setProgress(true)
                val phoneNumber = (resetPasswordStateHandler.dataSource.value.uiState as PasswordReset.EnterNewPassword).phoneNumber
                if (userRepo.changePassword(phoneNumber, newPassword)) {
                    resetPasswordStateHandler.newState(PasswordReset.Done)
                } else {
                    resetPasswordStateHandler.setProgress(false)
                }
            }
        }
    }

    override fun clearPasswordResetState() {
        resetPasswordTimerJob?.cancel()
        resetPasswordTimerJob = null
        resetPasswordStateHandler.clear()
    }

    override fun previousPasswordResetScreen(): Boolean {
        return resetPasswordStateHandler.goBack() != null
    }

    companion object {
        private const val RESEND_TIMER = 2 * 60 * 1000L
        private const val MAX_RESEND_ATTEMPTS = 3
    }
}