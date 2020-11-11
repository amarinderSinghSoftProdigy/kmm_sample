package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.viewmodel.AuthViewModel
import com.zealsoftsol.medico.core.viewmodel.AuthViewModelFacade
import org.kodein.di.DI
import org.kodein.di.instance

class SwiftDI(private val di: DI) {
    val mainViewModel: AuthViewModelFacade by di.instance<AuthViewModel>()
}