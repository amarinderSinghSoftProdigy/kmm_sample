package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.ifTrue
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.ErrorCode

internal class AuthEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Auth>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Auth) = when (event) {
        is Event.Action.Auth.LogIn -> authTryLogin()
        is Event.Action.Auth.LogOut -> authTryLogOut(event.notifyServer)
        is Event.Action.Auth.UpdateAuthCredentials -> authUpdateCredentials(
            event.emailOrPhone,
            event.password
        )
    }

    private suspend fun authTryLogin() {
        navigator.withScope<LogInScope> {
            val (error, isSuccess) = withProgress {
                userRepo.login(
                    it.credentials.value.phoneNumberOrEmail,
                    it.credentials.value.password,
                )
            }
            if (isSuccess) {
                val user = withProgress { userRepo.loadUserFromServer() }
                if (user != null) {
                    clearQueue()
                    setCurrentScope(
                        if (user.isVerified)
                            MainScope.Dashboard(DataSource(user))
                        else
                            MainScope.LimitedAccess.from(user)
                    )
                } else {
                    it.errors.value = ErrorCode()
                }
            } else {
                it.errors.value = error ?: ErrorCode()
            }
        }
    }

    private suspend fun authTryLogOut(notifyServer: Boolean) {
        navigator.withProgress {
            if (notifyServer) userRepo.logout() else true
        }.ifTrue {
            navigator.clearQueue()
            navigator.setCurrentScope(LogInScope(DataSource(userRepo.getAuthCredentials())))
        }
    }

    private fun authUpdateCredentials(emailOrPhone: String, password: String) {
        navigator.withScope<LogInScope> {
            it.credentials.value =
                userRepo.updateAuthCredentials(it.credentials.value, emailOrPhone, password)
        }
    }
}