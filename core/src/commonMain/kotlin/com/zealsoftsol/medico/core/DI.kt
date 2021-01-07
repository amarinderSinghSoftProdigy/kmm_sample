package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.interop.IpAddressFetcher
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.network.NetworkClient
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.network.mock.MockAuthScope
import com.zealsoftsol.medico.core.network.mock.MockCustomerScope
import com.zealsoftsol.medico.core.network.mock.MockProductScope
import com.zealsoftsol.medico.core.network.mock.MockSearchScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.storage.TokenStorage
import com.zealsoftsol.medico.core.utils.PhoneEmailVerifier
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.bind
import org.kodein.di.direct
import org.kodein.di.instance
import org.kodein.di.singleton

internal lateinit var directDI: DirectDI

fun startKodein(context: Any, useMocks: Boolean, navigatorSafeCasts: Boolean) = DI {
    platformDependencies(context, useMocks)
    bind<NetworkClient>() with singleton { NetworkClient(instance(), instance()) }
    bind<NetworkScope.Auth>() with singleton {
        if (!useMocks) {
            instance<NetworkClient>()
        } else {
            MockAuthScope()
        }
    }
    bind<NetworkScope.Customer>() with singleton {
        if (!useMocks) {
            instance<NetworkClient>()
        } else {
            MockCustomerScope()
        }
    }
    bind<NetworkScope.Search>() with singleton {
        if (!useMocks) {
            instance<NetworkClient>()
        } else {
            MockSearchScope()
        }
    }
    bind<NetworkScope.Product>() with singleton {
        if (!useMocks) {
            instance<NetworkClient>()
        } else {
            MockProductScope()
        }
    }
    bind<UserRepo>() with singleton {
        UserRepo(
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance()
        )
    }
    bind<PhoneEmailVerifier>() with singleton { PhoneEmailVerifier() }
    bind<Navigator>() with singleton { Navigator(navigatorSafeCasts) }
    bind<EventCollector>() with singleton { EventCollector(instance(), instance(), instance()) }
    bind<IpAddressFetcher>() with singleton { IpAddressFetcher() }
    bind<TokenStorage>() with singleton { TokenStorage(instance()) }
}.also {
    directDI = it.direct
}

expect fun DI.MainBuilder.platformDependencies(context: Any, useMocks: Boolean)