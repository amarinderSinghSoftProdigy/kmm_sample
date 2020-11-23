package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.extensions.Logger
import com.zealsoftsol.medico.core.extensions.logger
import org.kodein.di.DI

object UiLink {

    fun appStart(context: Any, isDebug: Boolean, loggerLevel: Logger.Level): AppStartResult {
        logger = logger.copy(level = loggerLevel)
        return AppStartResult(startKodein(context, isDebug))
    }

    data class AppStartResult(val di: DI)
}

