package com.zealsoftsol.medico.core.viewmodel

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState
import kotlinx.coroutines.launch

internal class AuthViewModel(
    private val userRepo: UserRepo
) : BaseViewModel(), AuthViewModelFacade {

    override val credentials: DataSource<AuthCredentials> = DataSource(userRepo.getAuthCredentials())
    override val state: DataSource<AuthState?> = DataSource(if (userRepo.isLoggedIn) AuthState.SUCCESS else null)

    override fun tryLogIn() {
        uniqueJob("login") {
            launch {
                state.value = AuthState.IN_PROGRESS
                state.value = if (userRepo.login(credentials.value)) {
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
                state.value = AuthState.IN_PROGRESS
                state.value = if (userRepo.logout()) {
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

    override fun clearState() {
        state.value = null
    }
}