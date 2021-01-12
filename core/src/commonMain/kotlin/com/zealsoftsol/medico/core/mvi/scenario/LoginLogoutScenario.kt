package com.zealsoftsol.medico.core.mvi.scenario

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.data.AuthCredentials

class LoginLogoutScenario : Scenario() {

    fun successfulLogin(login: String, pwd: String) = start {
        setScope(LogInScope(DataSource(AuthCredentials("", null, ""))))
        withScope<LogInScope> {
            it.updateAuthCredentials(login, pwd)
            it.tryLogIn()
            pause()
        }
    }
}