package com.zealsoftsol.medico.core.viewmodel

import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState
import com.zealsoftsol.medico.data.PasswordReset
import com.zealsoftsol.medico.data.UiStateWithProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default
    protected val uniqueJobs = hashMapOf<String, Job>()

    protected inline fun uniqueJob(key: String, block: CoroutineScope.() -> Job) {
        if (uniqueJobs[key]?.isActive == true) {
            "can't start the job with key $key, it is already in progress".warnIt()
        } else {
            uniqueJobs[key] = block()
        }
    }
}

interface AuthViewModelFacade {
    val credentials: DataSource<AuthCredentials>
    val authState: DataSource<AuthState?>
    val resetPasswordUiState: DataSource<UiStateWithProgress<PasswordReset>>

    fun updateAuthCredentials(emailOrPhone: String, password: String)

    fun tryLogIn()

    fun logOut()

    fun sendOtp(phoneNumber: String)

    fun submitOtp(otp: String)

    fun resendOtp()

    fun changePassword(newPassword: String)

    fun previousPasswordResetScreen(): Boolean

    fun clearAuthState()

    fun clearPasswordResetState()
}