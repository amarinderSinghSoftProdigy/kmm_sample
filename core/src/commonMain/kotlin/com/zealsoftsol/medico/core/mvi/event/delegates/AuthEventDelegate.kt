package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

internal class AuthEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo
) : EventDelegate<Event.Action.Auth>(navigator) {

    private var dashboardJob: Job? = null

    override suspend fun handleEvent(event: Event.Action.Auth) = when (event) {
        is Event.Action.Auth.LogIn -> authTryLogin()
        is Event.Action.Auth.LogOut -> authTryLogOut(event.notifyServer)
        is Event.Action.Auth.UpdateAuthCredentials -> authUpdateCredentials(
            event.emailOrPhone,
            event.password
        )
        is Event.Action.Auth.UpdateDashboard -> updateDashboard()
    }

    private suspend fun authTryLogin() {
        navigator.withScope<LogInScope> {
            withProgress {
                userRepo.login(
                    it.credentials.value.phoneNumberOrEmail,
                    it.credentials.value.password,
                )
            }.onSuccess { _ ->
                setScope(DashboardScope.get())
            }.onError { error ->
                it.errorCode.value = error.body
                it.showCredentialError.value = true
                it.showToast.value = true
            }
        }
    }

    private suspend fun authTryLogOut(notifyServer: Boolean) {

    }

    private fun authUpdateCredentials(emailOrPhone: String, password: String) {
        navigator.withScope<LogInScope> {
            it.credentials.value =
                userRepo.updateAuthCredentials(it.credentials.value, emailOrPhone, password)
        }
    }

    private suspend fun updateDashboard() {
        dashboardJob?.cancel()
        dashboardJob = coroutineContext.toScope().launch {  }
    }
}