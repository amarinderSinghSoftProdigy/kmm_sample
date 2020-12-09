package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.EnterNewPasswordScope
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo

internal class PasswordEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.ResetPassword>(navigator) {

    override suspend fun handleEvent(event: Event.Action.ResetPassword) = when (event) {
        is Event.Action.ResetPassword.Send -> changePassword(event.newPassword)
        is Event.Action.ResetPassword.Finish -> finishResetPassword()
    }

    private suspend fun changePassword(newPassword: String) {
        navigator.withScope<EnterNewPasswordScope> {
            val (validation, isSuccess) = withProgress {
                userRepo.changePassword(it.phoneNumber, newPassword)
            }
            if (isSuccess) {
                it.notifications.value = EnterNewPasswordScope.PasswordChangedSuccessfully
            }
            it.passwordValidation.value = validation
        }
    }

    private fun finishResetPassword() {
        navigator.withScope<EnterNewPasswordScope> {
            clearQueue()
            setCurrentScope(LogInScope(DataSource(userRepo.getAuthCredentials())))
        }
    }
}