package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.viewmodel.AuthViewModel
import com.zealsoftsol.medico.core.viewmodel.AuthViewModelFacade
import com.zealsoftsol.medico.core.viewmodel.TestAuthViewModel
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance

class SwiftDI(private val di: DI) {
    val mainViewModel: AuthViewModelFacade = di.direct.instance<AuthViewModel>()
    val testViewModel: TestAuthViewModel = di.direct.instance<TestAuthViewModel>()
}