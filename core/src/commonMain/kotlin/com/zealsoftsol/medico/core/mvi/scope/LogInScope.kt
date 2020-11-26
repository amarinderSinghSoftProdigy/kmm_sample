package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.BooleanEvent
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.AuthCredentials

data class LogInScope(
    val credentials: DataSource<AuthCredentials>,
    val success: BooleanEvent = BooleanEvent.none,
    override val isInProgress: Boolean = false,
) : BaseScope() {

    /**
     * Updates current scope, result posted to [credentials]
     */
    fun updateAuthCredentials(emailOrPhone: String, password: String) =
        EventCollector.sendEvent(Event.Action.Auth.UpdateAuthCredentials(emailOrPhone, password))

    /**
     * Transition to [MainScope] if successful
     */
    fun tryLogIn() = EventCollector.sendEvent(Event.Action.Auth.LogIn)

    /**
     * Transition to [ForgetPasswordScope.PhoneNumberInput]
     */
    fun goToForgetPassword() = EventCollector.sendEvent(Event.Transition.ForgetPassword)

    /**
     * Transition to [SignUpScope.SelectUserType]
     */
    fun goToSignUp() = EventCollector.sendEvent(Event.Transition.SignUp)
}