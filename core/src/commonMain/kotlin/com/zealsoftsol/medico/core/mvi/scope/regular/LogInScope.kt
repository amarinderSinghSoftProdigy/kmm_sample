package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.RegisterGlobal

class LogInScope(
    val credentials: DataSource<AuthCredentials>,
) : Scope.Host() {

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
     * Transition to [OtpScope.PhoneNumberInput]
     */
    fun goToForgetPassword() = EventCollector.sendEvent(Event.Transition.Otp)

    /**
     * Transition to [SignUpScope.SelectUserType]
     */
    fun goToSignUp() {
        SignUpScope.registerGlobal.value = RegisterGlobal()
        EventCollector.sendEvent(Event.Transition.SignUp)
    }
}