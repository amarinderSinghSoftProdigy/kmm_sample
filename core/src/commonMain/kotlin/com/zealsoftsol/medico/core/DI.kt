package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.interop.IpAddressFetcher
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.network.NetworkClient
import com.zealsoftsol.medico.core.notifications.FirebaseMessagingCenter
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.storage.TokenStorage
import com.zealsoftsol.medico.core.utils.TapModeHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.bind
import org.kodein.di.direct
import org.kodein.di.instance
import org.kodein.di.singleton

internal lateinit var directDI: DirectDI

fun startKodein(
    context: Any,
    useMocks: Boolean,
    useNavigatorSafeCasts: Boolean,
    useNetworkInterceptor: Boolean,
    crashOnServerError: Boolean,
    baseUrl: NetworkClient.BaseUrl,
) = DI {
    platformDependencies(context, useMocks)
    bind<NetworkClient>() with singleton {
        NetworkClient(
            instance(),
            instance(),
            useNetworkInterceptor = useNetworkInterceptor,
            crashOnServerError = crashOnServerError,
            baseUrl = baseUrl,
        )
    }

    bind<UserRepo>() with singleton {
        UserRepo(
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
        )
    }
    bind<Navigator>() with singleton { Navigator(useNavigatorSafeCasts) }
    bind<EventCollector>() with singleton {
        EventCollector(
            instance(),
            instance(),
            instance(),
        )
    }
    bind<IpAddressFetcher>() with singleton { IpAddressFetcher() }
    bind<TokenStorage>() with singleton { TokenStorage(instance()) }
    bind<FirebaseMessagingCenter>() with singleton {
        FirebaseMessagingCenter(
            instance(),
        )
    }
    bind<TapModeHelper>() with singleton { TapModeHelper(CoroutineScope(SupervisorJob() + compatDispatcher)) }
}.also {
    directDI = it.direct
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun DI.MainBuilder.platformDependencies(context: Any, useMocks: Boolean)
