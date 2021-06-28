package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.nested.LimitedAccessScope
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.NotificationRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.getEntriesCountDataSource
import com.zealsoftsol.medico.core.repository.getUnreadMessagesDataSource
import com.zealsoftsol.medico.core.repository.getUserDataSource
import com.zealsoftsol.medico.core.repository.requireUser

internal class AuthEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val notificationRepo: NotificationRepo,
    private val cartRepo: CartRepo,
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
            withProgress {
                userRepo.login(
                    it.credentials.value.phoneNumberOrEmail,
                    it.credentials.value.password,
                )
            }.onSuccess { _ ->
                withProgress { userRepo.loadUserFromServer() }
                    .onSuccess {
                        withProgress {
                            userRepo.sendFirebaseToken()
                            notificationRepo.loadUnreadMessagesFromServer()
                            cartRepo.loadCartFromServer(userRepo.requireUser().unitCode)
                        }
                        dropScope(Navigator.DropStrategy.All, updateDataSource = false)
                        val user = userRepo.requireUser()
                        setScope(
                            if (user.isActivated)
                                DashboardScope.get(
                                    user = user,
                                    userDataSource = userRepo.getUserDataSource(),
                                    unreadNotifications = notificationRepo.getUnreadMessagesDataSource(),
                                    cartItemsCount = cartRepo.getEntriesCountDataSource(),
                                )
                            else
                                LimitedAccessScope.get(user, userRepo.getUserDataSource())
                        )
                    }.onError(navigator)
            }.onError(navigator)
        }
    }

    private suspend fun authTryLogOut(notifyServer: Boolean) {
        if (notifyServer) {
            userRepo.logout()
                .onSuccess {
                    navigator.dropScope(Navigator.DropStrategy.All, updateDataSource = false)
                    navigator.setScope(LogInScope(DataSource(userRepo.getAuthCredentials())))
                }.onError(navigator)
        }
    }

    private fun authUpdateCredentials(emailOrPhone: String, password: String) {
        navigator.withScope<LogInScope> {
            it.credentials.value =
                userRepo.updateAuthCredentials(it.credentials.value, emailOrPhone, password)
        }
    }
}