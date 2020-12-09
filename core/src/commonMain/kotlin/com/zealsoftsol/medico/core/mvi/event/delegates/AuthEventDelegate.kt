package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.BooleanEvent
import com.zealsoftsol.medico.core.extensions.ifTrue
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.AuthState

internal class AuthEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Auth>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Auth) = when (event) {
        is Event.Action.Auth.LogIn -> authTryLogin()
        is Event.Action.Auth.LogOut -> authTryLogOut()
        is Event.Action.Auth.UpdateAuthCredentials -> authUpdateCredentials(
            event.emailOrPhone,
            event.password
        )
    }

    private suspend fun authTryLogin() {
        navigator.withScope<LogInScope> {
            if (withProgress { userRepo.login(it.credentials.value) }) {
                clearQueue()
                setCurrentScope(
                    MainScope(
                        isLimitedAppAccess = userRepo.authState == AuthState.PENDING_VERIFICATION,
                    )
                )
            } else {
                setCurrentScope(it.copy(success = BooleanEvent.`false`))
            }
        }
    }

    private suspend fun authTryLogOut() {
        navigator.withProgress {
            userRepo.logout()
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