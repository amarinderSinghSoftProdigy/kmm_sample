package com.zealsoftsol.medico.core

import org.kodein.di.DI

object UiLink {

    fun appStart(context: Any, isDebug: Boolean): AppStartResult {
        return AppStartResult(startKodein(context, isDebug))
    }

    data class AppStartResult(val di: DI)
}

