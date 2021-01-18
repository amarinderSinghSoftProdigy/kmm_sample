package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.PasswordScope
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.ErrorCode

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
            val (validation, isSuccess) = withProgress {
                userRepo.verifyPassword(password)
            }
            if (isSuccess) {
                setScope(PasswordScope.EnterNew(phoneNumber = null))
            }
            it.passwordValidation.value = validation
        }
    }

    private suspend fun changePassword(password: String) {
        navigator.withScope<PasswordScope.EnterNew> {
            val (validation, isSuccess) = withProgress {
                userRepo.changePassword(it.phoneNumber, password)
            }
            if (isSuccess) {
                it.notifications.value = PasswordScope.EnterNew.PasswordChangedSuccessfully
            }
            it.passwordValidation.value = validation
        }
    }

    private suspend fun finishResetPassword() {
        navigator.withScope<PasswordScope.EnterNew> {
            it.dismissNotification()
            withProgress {
                if (userRepo.getUserAccess() == UserRepo.UserAccess.NO_ACCESS || userRepo.logout()) {
                    dropScope(Navigator.DropStrategy.ALL, updateDataSource = false)
                    setScope(LogInScope(DataSource(userRepo.getAuthCredentials())))
                } else {
                    setHostError(ErrorCode())
                }
            }
        }
    }
}