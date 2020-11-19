package com.zealsoftsol.medico.core

import com.zealsoftsol.medico.core.network.NetworkClient
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.utils.PhoneEmailVerifier
import com.zealsoftsol.medico.core.viewmodel.AuthViewModel
import com.zealsoftsol.medico.core.viewmodel.TestAuthViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

fun startKodein(context: Any) = DI {
    platformDependencies(context)
    bind<NetworkClient>() with singleton { NetworkClient(instance()) }
    bind<AuthViewModel>() with singleton { AuthViewModel(instance()) }
    bind<TestAuthViewModel>() with singleton { TestAuthViewModel(instance()) }
    bind<UserRepo>() with singleton { UserRepo(instance<NetworkClient>(), instance(), instance()) }
    bind<PhoneEmailVerifier>() with singleton { PhoneEmailVerifier() }
}

expect fun DI.MainBuilder.platformDependencies(context: Any)