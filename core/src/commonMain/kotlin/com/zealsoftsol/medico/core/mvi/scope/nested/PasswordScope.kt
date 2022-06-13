package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.StringResource
import com.zealsoftsol.medico.data.PasswordValidation

sealed class PasswordScope(
    private val titleId: String,
    val password: DataSource<String> = DataSource(""),
    val passwordValidation: DataSource<PasswordValidation?> = DataSource(null),
) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo? {
        return (tabBarInfo as? TabBarInfo.Simple)?.copy(title = StringResource.Static(titleId))
    }

    fun changePassword(password: String) {
        this.password.value = password
    }

    abstract fun submit(): Boolean

    class VerifyCurrent : PasswordScope("change_password") {

        override fun submit() =
            EventCollector.sendEvent(Event.Action.ResetPassword.ConfirmCurrent(password.value))
    }

    class EnterNew(
        internal val phoneNumber: String?,
        val confirmPassword: DataSource<String> = DataSource(""),
        override val notifications: DataSource<ScopeNotification?> = DataSource(null),
    ) : PasswordScope("new_password"),
        CommonScope.WithNotifications {

        fun isValidPassword(str: String): Boolean {
            if (str.isEmpty()) {
                return true
            }
            val regex = ("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$")
            val p = regex.toRegex()
            return p.matches(str) && str.length >= 8
        }

        fun changeConfirmPassword(password: String) {
            confirmPassword.value = password
        }

        override fun submit() =
            EventCollector.sendEvent(Event.Action.ResetPassword.ConfirmNew(password.value))

        object PasswordChangedSuccessfully : ScopeNotification {
            override val dismissEvent: Event = Event.Action.ResetPassword.Finish
            override val isSimple: Boolean = true
            override val isDismissible: Boolean = true
            override val title: String? = "success"
            override val body: String = "password_change_success"
        }
    }
}

