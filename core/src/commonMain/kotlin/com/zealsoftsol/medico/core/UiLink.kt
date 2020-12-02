package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.extensions.Logger
import com.zealsoftsol.medico.core.extensions.logger
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.UiNavigator
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.AuthState
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance

object UiLink {

    fun appStart(context: Any, isDebug: Boolean, loggerLevel: Logger.Level): AppStartResult {
        logger = logger.copy(level = loggerLevel)
        val di = startKodein(context, isDebug)
        val directDI = di.direct
        val navigator = directDI.instance<Navigator>()
        val userRepo = directDI.instance<UserRepo>()
        val eventCollector = directDI.instance<EventCollector>()
        navigator.setCurrentScope(
            if (userRepo.authState == AuthState.AUTHORIZED) {
                MainScope()
            } else {
                LogInScope(DataSource(userRepo.getAuthCredentials()))
            }
        )
        return AppStartResult(di, navigator)
    }

    data class AppStartResult(val di: DI, val navigator: UiNavigator)
}

