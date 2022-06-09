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

    val showCredentialError = DataSource(false)
    val errorCode = DataSource("E0036")
    val showToast = DataSource(false)

    fun hideErrorToast(){
        showToast.value = false
    }

    /**
     * Updates current scope, result posted to [credentials]
     */
    fun updateAuthCredentials(emailOrPhone: String, password: String) {
        showCredentialError.value = false
        EventCollector.sendEvent(Event.Action.Auth.UpdateAuthCredentials(emailOrPhone, password))
    }

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

    fun isValidPhone(phone: String): Boolean {
        if (phone.isEmpty()) {
            return true
        }
        return phone.length == 10
    }

    fun isValidPassword(str: String): Boolean {
        if (str.isEmpty()) {
            return true
        }
        val regex = ("^(?=.*[a-z])(?=."
                + "*[A-Z])(?=.*\\d)"
                + "(?=.*[-+_!@#$%^&*., ?]).+$")
        val p = regex.toRegex()
        return p.matches(str) && str.length >= 8
    }

}