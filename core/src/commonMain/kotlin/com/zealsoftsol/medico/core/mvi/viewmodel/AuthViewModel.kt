package com.zealsoftsol.medico.core.mvi.viewmodel

import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.ForgetPasswordScope.AwaitVerification.Companion.RESEND_TIMER
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState
import com.zealsoftsol.medico.data.UserRegistration
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel(
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

    suspend fun changePassword(phoneNumber: String, newPassword: String) =
        userRepo.changePassword(phoneNumber, newPassword)

    fun stopResetPasswordTimer() {
        resetPasswordTimerJob?.cancel()
    }

    fun startResetPasswordTimer() {
        stopResetPasswordTimer()
        resetPasswordTimerJob = GlobalScope.launch(compatDispatcher) {
            var remainingTime = RESEND_TIMER
            while (remainingTime > 0) {
                resendTimer.value = remainingTime.logIt()
                delay(1000)
                remainingTime -= 1000
            }
        }
    }

    // SIGN UP =====================================================================================

    suspend fun trySignUp(userRegistration: UserRegistration) {
        val validation = userRepo.signUpPartially(userRegistration)
        //            when (userRegistration) {
//                is UserRegistration1 -> {
//                    validation?.let {
//
//                    } ?: navigator.withScope<BaseScope.SignUp.PersonalData> {  }
//                }
//                is UserRegistration2 -> {}
//                is UserRegistration3 -> {}
//            }
    }
}