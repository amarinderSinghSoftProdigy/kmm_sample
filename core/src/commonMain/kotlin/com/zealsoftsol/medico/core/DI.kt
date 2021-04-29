package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.interop.IpAddressFetcher
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.network.NetworkClient
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.network.mock.MockAuthScope
import com.zealsoftsol.medico.core.network.mock.MockCartScope
import com.zealsoftsol.medico.core.network.mock.MockCustomerScope
import com.zealsoftsol.medico.core.network.mock.MockHelpScope
import com.zealsoftsol.medico.core.network.mock.MockManagementScope
import com.zealsoftsol.medico.core.network.mock.MockNotificationScope
import com.zealsoftsol.medico.core.network.mock.MockPasswordScope
import com.zealsoftsol.medico.core.network.mock.MockProductScope
import com.zealsoftsol.medico.core.network.mock.MockSearchScope
import com.zealsoftsol.medico.core.network.mock.MockSignUpScope
import com.zealsoftsol.medico.core.network.mock.MockStoresScope
import com.zealsoftsol.medico.core.notifications.FirebaseMessagingCenter
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.NotificationRepo
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

fun startKodein(
    context: Any,
    useMocks: Boolean,
    useNavigatorSafeCasts: Boolean,
    useNetworkInterceptor: Boolean,
    baseUrl: NetworkClient.BaseUrl,
) = DI {
    platformDependencies(context, useMocks)
    bind<NetworkClient>() with singleton {
        NetworkClient(
            instance(),
            instance(),
            useNetworkInterceptor,
            baseUrl,
        )
    }
    bind<NetworkScope.Auth>() with singleton {
        if (!useMocks) {
            instance<NetworkClient>()
        } else {
            MockAuthScope()
        }
    }
    bind<NetworkScope.SignUp>() with singleton {
        if (!useMocks) {
            instance<NetworkClient>()
        } else {
            MockSignUpScope()
        }
    }
    bind<NetworkScope.Password>() with singleton {
        if (!useMocks) {
            instance<NetworkClient>()
        } else {
            MockPasswordScope()
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
    bind<NetworkScope.Management>() with singleton {
        if (!useMocks) {
            instance<NetworkClient>()
        } else {
            MockManagementScope()
        }
    }
    bind<NetworkScope.Notification>() with singleton {
        if (!useMocks) {
            instance<NetworkClient>()
        } else {
            MockNotificationScope()
        }
    }
    bind<NetworkScope.Stores>() with singleton {
        if (!useMocks) {
            instance<NetworkClient>()
        } else {
            MockStoresScope()
        }
    }
    bind<NetworkScope.Cart>() with singleton {
        if (!useMocks) {
            instance<NetworkClient>()
        } else {
            MockCartScope()
        }
    }
    bind<NetworkScope.Help>() with singleton {
        if (!useMocks) {
            instance<NetworkClient>()
        } else {
            MockHelpScope()
        }
    }
    bind<UserRepo>() with singleton {
        UserRepo(
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
        )
    }
    bind<NotificationRepo>() with singleton { NotificationRepo(instance(), instance()) }
    bind<CartRepo>() with singleton { CartRepo(instance()) }
    bind<PhoneEmailVerifier>() with singleton { PhoneEmailVerifier() }
    bind<Navigator>() with singleton { Navigator(useNavigatorSafeCasts) }
    bind<EventCollector>() with singleton {
        EventCollector(
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
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
            instance()
        )
    }
}.also {
    directDI = it.direct
}

expect fun DI.MainBuilder.platformDependencies(context: Any, useMocks: Boolean)