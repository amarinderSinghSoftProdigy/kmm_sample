package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.BooleanEvent
import com.zealsoftsol.medico.core.extensions.ifTrue
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.viewmodel.AuthViewModel
import com.zealsoftsol.medico.core.mvi.withProgress

internal class AuthEventDelegate(
    navigator: Navigator,
    private val authViewModel: AuthViewModel,
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
        val isSuccess = navigator.withProgress {
            authViewModel.tryLogIn()
        }
        navigator.withScope<LogInScope> {
            if (isSuccess) {
                transitionTo(MainScope())
            } else {
                setCurrentScope(it.copy(success = BooleanEvent.`false`))
            }
        }
    }

    private suspend fun authTryLogOut() {
        navigator.withProgress {
            authViewModel.tryLogOut()
        }.ifTrue {
            navigator.transitionTo(LogInScope(authViewModel.credentials))
        }
    }

    private fun authUpdateCredentials(emailOrPhone: String, password: String) {
        authViewModel.updateAuthCredentials(emailOrPhone, password)
    }
}