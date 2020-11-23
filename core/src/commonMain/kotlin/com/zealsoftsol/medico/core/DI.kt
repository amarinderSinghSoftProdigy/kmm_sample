package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.network.NetworkClient
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.network.mock.MockAuthScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.utils.PhoneEmailVerifier
import com.zealsoftsol.medico.core.viewmodel.AuthViewModelImpl
import com.zealsoftsol.medico.core.viewmodel.TestAuthViewModel
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
    bind<AuthViewModelImpl>() with singleton {
        AuthViewModelImpl(
            instance(),
            instance<UiNavigator>() as Navigator
        )
    }
    bind<TestAuthViewModel>() with singleton { TestAuthViewModel(instance()) }
    bind<UserRepo>() with singleton { UserRepo(instance(), instance(), instance()) }
    bind<PhoneEmailVerifier>() with singleton { PhoneEmailVerifier() }
    bind<UiNavigator>() with singleton { Navigator(instance()) }
}

expect fun DI.MainBuilder.platformDependencies(context: Any, isDebugBuild: Boolean)