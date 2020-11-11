package com.zealsoftsol.medico.core

import org.kodein.di.DI

object UiLink {

    fun appStart(context: Any): AppStartResult {
        return AppStartResult(startKodein(context))
    }

    data class AppStartResult(val di: DI)
}

