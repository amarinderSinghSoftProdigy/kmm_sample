package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.ifTrue
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.getUserDataSource
import com.zealsoftsol.medico.core.repository.requireUser
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
                if (withProgress { userRepo.loadUserFromServer() }) {
                    userRepo.sendFirebaseToken()
                    dropScope(Navigator.DropStrategy.All, updateDataSource = false)
                    val user = userRepo.requireUser()
                    setScope(
                        if (user.isActivated)
                            DashboardScope.get(user, userRepo.getUserDataSource())
                        else
                            LimitedAccessScope.get(user, userRepo.getUserDataSource())
                    )
                } else {
                    setHostError(ErrorCode())
                }
            } else {
                setHostError(error ?: ErrorCode())
            }
        }
    }

    private suspend fun authTryLogOut(notifyServer: Boolean) {
        navigator.withProgress {
            if (notifyServer) userRepo.logout() else true
        }.ifTrue {
            navigator.dropScope(Navigator.DropStrategy.All, updateDataSource = false)
            navigator.setScope(LogInScope(DataSource(userRepo.getAuthCredentials())))
        }
    }

    private fun authUpdateCredentials(emailOrPhone: String, password: String) {
        navigator.withScope<LogInScope> {
            it.credentials.value =
                userRepo.updateAuthCredentials(it.credentials.value, emailOrPhone, password)
        }
    }
}