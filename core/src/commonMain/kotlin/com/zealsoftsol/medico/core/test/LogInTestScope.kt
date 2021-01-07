package com.zealsoftsol.medico.core.test

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.ErrorCode

class LogInTestScope : BaseTestScope() {

    inline fun logIn(
        credentials: AuthCredentials,
        error: ErrorCode?
    ) {
        nav.setScope(
            LogInScope(
                credentials = DataSource(credentials),
            )
        )
        nav.setHostError(error)
    }
    
}
