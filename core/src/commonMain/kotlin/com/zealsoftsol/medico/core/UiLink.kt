package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.extensions.Logger
import com.zealsoftsol.medico.core.extensions.logger
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.UiNavigator
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
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
            SignUpScope.LegalDocuments.DrugLicense(
                UserRegistration1(phoneNumber = "12345"),
                UserRegistration2(),
                UserRegistration3(),
            )
//            if (userRepo.authState == AuthState.AUTHORIZED) {
//                MainScope()
//            } else {
//                LogInScope(DataSource(userRepo.getAuthCredentials()))
//            }
        )
        return AppStartResult(di, navigator)
    }

    data class AppStartResult(val di: DI, val navigator: UiNavigator)
}

