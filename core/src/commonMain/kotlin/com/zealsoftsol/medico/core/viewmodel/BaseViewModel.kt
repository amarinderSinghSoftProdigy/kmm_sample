package com.zealsoftsol.medico.core.viewmodel

import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.Platform
import com.zealsoftsol.medico.core.interop.platform
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState
import com.zealsoftsol.medico.data.PasswordReset
import com.zealsoftsol.medico.data.UiStateWithProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default
    protected val uniqueJobs = hashSetOf<String>()

    // TODO iOS implementation does not guarantee uniqueness
    protected inline fun uniqueJob(key: String, crossinline block: suspend () -> Unit) {
        when (platform) {
            Platform.Android -> {
                if (!uniqueJobs.add(key)) {
                    "can't start the job with key $key, it is already in progress".warnIt()
                } else {
                    launch(Dispatchers.Default) {
                        block()
                        uniqueJobs.remove(key)
                    }
                }
            }
            Platform.iOS -> launch(Dispatchers.Main) {
                block()
            }
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