package com.zealsoftsol.medico.core.mvi.viewmodel

import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.ForgetPasswordScope.AwaitVerification.Companion.RESEND_TIMER
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState
import com.zealsoftsol.medico.data.PasswordValidation
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3
import com.zealsoftsol.medico.data.ValidationData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class AuthViewModel(
    private val userRepo: UserRepo,
) : BaseViewModel() {

    private var resetPasswordTimerJob: Job? = null

    val credentials: DataSource<AuthCredentials> = DataSource(userRepo.getAuthCredentials())
    val resendTimer: DataSource<Long> = DataSource(RESEND_TIMER)

    val isAuthorized: Boolean
        get() = userRepo.authState == AuthState.AUTHORIZED

    // LOG IN ======================================================================================

    suspend fun tryLogIn(): Boolean = userRepo.login(credentials.value)

    suspend fun tryLogOut(): Boolean = userRepo.logout()

    fun updateAuthCredentials(emailOrPhone: String, password: String) {
        credentials.value =
            userRepo.updateAuthCredentials(credentials.value, emailOrPhone, password)
    }

    // PASSWORD RESET ==============================================================================

    suspend fun sendOtp(phoneNumber: String): Boolean = userRepo.sendOtp(phoneNumber)

    suspend fun submitOtp(phoneNumber: String, otp: String): Boolean =
        userRepo.submitOtp(phoneNumber, otp)

    suspend fun resendOtp(phoneNumber: String): Boolean = userRepo.resendOtp(phoneNumber)

    suspend fun changePassword(
        phoneNumber: String,
        newPassword: String
    ): ValidationData<PasswordValidation> =
        userRepo.changePassword(phoneNumber, newPassword)

    fun stopResetPasswordTimer() {
        resetPasswordTimerJob?.cancel()
    }

    fun startResetPasswordTimer() {
        stopResetPasswordTimer()
        resetPasswordTimerJob = GlobalScope.launch(compatDispatcher) {
            var remainingTime = RESEND_TIMER
            while (remainingTime > 0) {
                resendTimer.value = remainingTime
                delay(1000)
                remainingTime -= 1000
            }
        }
    }

    // SIGN UP =====================================================================================

    suspend fun signUpPart1(userRegistration1: UserRegistration1): ValidationData<UserValidation1> {
        return userRepo.signUpPart1(userRegistration1)
    }

    suspend fun signUpPart2(userRegistration2: UserRegistration2): ValidationData<UserValidation2> {
        return userRepo.signUpPart2(userRegistration2)
    }

    suspend fun signUpPart3(userRegistration3: UserRegistration3): ValidationData<UserValidation3> {
        return userRepo.signUpPart3(userRegistration3)
    }
}