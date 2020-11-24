package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.viewmodel.AuthViewModelImpl
import com.zealsoftsol.medico.core.viewmodel.interfaces.AuthViewModel
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance

class SwiftDI(private val di: DI) {
    val authViewModel: AuthViewModel = di.direct.instance<AuthViewModelImpl>()
    val navigator: UiNavigator = di.direct.instance<UiNavigator>()
}