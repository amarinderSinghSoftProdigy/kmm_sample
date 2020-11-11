package com.zealsoftsol.medico.core.viewmodel

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default
}

interface AuthViewModelFacade {
    val credentials: DataSource<AuthCredentials>
    val state: DataSource<AuthState?>

    fun updateAuthCredentials(credentials: AuthCredentials)

    fun tryLogIn()

    fun clearState()
}