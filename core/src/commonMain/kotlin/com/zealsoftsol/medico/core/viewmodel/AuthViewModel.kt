package com.zealsoftsol.medico.core.viewmodel

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class AuthViewModel(private val userRepo: UserRepo) : BaseViewModel(), AuthViewModelFacade {

    override val credentials: DataSource<AuthCredentials> = DataSource(userRepo.getAuthCredentials())
    override val state: DataSource<AuthState?> = DataSource(null)

    override fun tryLogIn() {
        launch {
            delay(1000)
            state.value = AuthState.ERROR
        }
    }

    override fun updateAuthCredentials(credentials: AuthCredentials) {
        this.credentials.value = credentials
    }

    override fun clearState() {
        state.value = null
    }
}