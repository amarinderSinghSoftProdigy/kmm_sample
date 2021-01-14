package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.PasswordValidation

// TODO
@Deprecated("create hierarchy of PasswordScope")
class EnterNewPasswordScope(
    internal val phoneNumber: String,
    val passwordValidation: DataSource<PasswordValidation?>,
    override val notifications: DataSource<ScopeNotification?> = DataSource(null),
) : Scope.Child.TabBar(
    TabBarInfo.Simple(ScopeIcon.BACK, "new_password")
),
    CommonScope.CanGoBack,
    CommonScope.WithNotifications {

    fun changePassword(newPassword: String) =
        EventCollector.sendEvent(Event.Action.ResetPassword.Send(newPassword))

    /**
     * Transition to [LogInScope]
     */
    fun finishResetPasswordFlow() =
        EventCollector.sendEvent(Event.Action.ResetPassword.Finish)

    object PasswordChangedSuccessfully : ScopeNotification {
        override val title: String
            get() = "success"
        override val body: String
            get() = "password_change_success"
    }
}