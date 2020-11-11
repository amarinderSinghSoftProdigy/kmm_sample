package com.zealsoftsol.medico.core.viewmodel.mock

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.viewmodel.AuthViewModelFacade
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState

class MockAuthViewModel : AuthViewModelFacade {
    override val credentials: DataSource<AuthCredentials> = DataSource(AuthCredentials("", ""))
    override val state: DataSource<AuthState?> = DataSource(null)

    override fun tryLogIn() {

    }
    override fun updateAuthCredentials(credentials: AuthCredentials) {
    }

    override fun clearState() {
    }
}