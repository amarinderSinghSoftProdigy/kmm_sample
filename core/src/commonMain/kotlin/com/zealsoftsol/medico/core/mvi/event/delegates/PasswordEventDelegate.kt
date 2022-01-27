package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.nested.PasswordScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo

internal class PasswordEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.ResetPassword>(navigator) {

    override suspend fun handleEvent(event: Event.Action.ResetPassword) = when (event) {
        is Event.Action.ResetPassword.ConfirmCurrent -> verifyPassword(event.password)
        is Event.Action.ResetPassword.ConfirmNew -> changePassword(event.password)
        is Event.Action.ResetPassword.Finish -> finishResetPassword()
    }

    private suspend fun verifyPassword(password: String) {
        navigator.withScope<PasswordScope.VerifyCurrent> {
            val result = withProgress {
                userRepo.verifyPassword(password)
            }

            result.onSuccess {
                setScope(PasswordScope.EnterNew(phoneNumber = null))
            }.onError(navigator)

            it.passwordValidation.value = result.validations
        }
    }

    private suspend fun changePassword(password: String) {
        navigator.withScope<PasswordScope.EnterNew> {
            val result = withProgress {
                userRepo.changePassword(it.phoneNumber, password)
            }

            result.onSuccess { _ ->
                it.notifications.value = PasswordScope.EnterNew.PasswordChangedSuccessfully
            }.onError(navigator)

            it.passwordValidation.value = result.validations
        }
    }

    private fun finishResetPassword() {
        navigator.withScope<PasswordScope.EnterNew> {
            it.dismissNotification()
            EventCollector.sendEvent(Event.Action.Auth.LogOut(true))
        }
    }
}