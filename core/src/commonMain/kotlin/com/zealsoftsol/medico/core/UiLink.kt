package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.extensions.Logger
import com.zealsoftsol.medico.core.extensions.logger
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.UiNavigator
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import org.kodein.di.DI
import org.kodein.di.instance

object UiLink {

    fun appStart(
        context: Any,
        useMocks: Boolean,
        loggerLevel: Logger.Level,
    ): AppStartResult {
        logger = logger.copy(level = loggerLevel)
        val di = startKodein(context, useMocks)
        val navigator = directDI.instance<Navigator>()
        val eventCollector = directDI.instance<EventCollector>()
        navigator.setCurrentScope(eventCollector.getStartingScope())
        return AppStartResult(di, navigator)
    }

    data class AppStartResult(val di: DI, val navigator: UiNavigator)
}

