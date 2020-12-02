package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.network.NetworkClient
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.network.mock.MockAuthScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.utils.PhoneEmailVerifier
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

fun startKodein(context: Any, isDebugBuild: Boolean) = DI {
    platformDependencies(context, isDebugBuild)
    bind<NetworkScope.Auth>() with singleton {
        if (!isDebugBuild) {
            NetworkClient(instance())
        } else {
            MockAuthScope()
        }
    }
    bind<UserRepo>() with singleton { UserRepo(instance(), instance(), instance()) }
    bind<PhoneEmailVerifier>() with singleton { PhoneEmailVerifier() }
    bind<Navigator>() with singleton { Navigator() }
    bind<EventCollector>() with singleton { EventCollector(instance(), instance()) }
}

expect fun DI.MainBuilder.platformDependencies(context: Any, isDebugBuild: Boolean)