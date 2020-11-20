package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.viewmodel.AuthViewModel
import com.zealsoftsol.medico.core.viewmodel.AuthViewModelImpl
import com.zealsoftsol.medico.core.viewmodel.TestAuthViewModel
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance

class SwiftDI(private val di: DI) {
    val authViewModel: AuthViewModel = di.direct.instance<AuthViewModelImpl>()
    val testViewModel: TestAuthViewModel = di.direct.instance<TestAuthViewModel>()
    val navigator: UiNavigator = di.direct.instance<UiNavigator>()
}