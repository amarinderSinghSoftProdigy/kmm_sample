package com.zealsoftsol.medico.core.mvi.event

import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.delegates.AuthEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.EventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.OtpEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.PasswordEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.RegistrationEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.SearchEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.TransitionEventDelegate
import com.zealsoftsol.medico.core.mvi.scope.BaseScope
import com.zealsoftsol.medico.core.mvi.scope.LogInScope
import com.zealsoftsol.medico.core.mvi.scope.MainScope
import com.zealsoftsol.medico.core.mvi.scope.NavAndSearchMainScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

internal class EventCollector(
    private val navigator: Navigator,

    private val networkSearchScope: NetworkScope.Search,
    private val userRepo: UserRepo,
) {
    private val delegateMap = mapOf<KClass<*>, EventDelegate<*>>(
        Event.Transition::class to TransitionEventDelegate(navigator),
        Event.Action.Auth::class to AuthEventDelegate(navigator, userRepo),
        Event.Action.Otp::class to OtpEventDelegate(navigator, userRepo),
        Event.Action.ResetPassword::class to PasswordEventDelegate(navigator, userRepo),
        Event.Action.Registration::class to RegistrationEventDelegate(navigator, userRepo),
        Event.Action.Search::class to SearchEventDelegate(navigator, userRepo, networkSearchScope),
    )

    init {
        GlobalScope.launch(compatDispatcher) {
            for (event in events.openSubscription()) {
                delegateMap[event.typeClazz]!!.genericHandle(event)
            }
        }
    }

    fun getStartingScope(): BaseScope {
        val startScope = when (userRepo.getUserAccess()) {
            UserRepo.UserAccess.FULL_ACCESS -> MainScope.Dashboard(
                user = DataSource(userRepo.user!!)
            )
            UserRepo.UserAccess.LIMITED_ACCESS -> MainScope.LimitedAccess.from(userRepo.user!!)
            UserRepo.UserAccess.NO_ACCESS -> LogInScope(DataSource(userRepo.getAuthCredentials()))
        }
        if (startScope is MainScope && startScope is NavAndSearchMainScope) GlobalScope.launch(
            compatDispatcher
        ) {
            userRepo.loadUserFromServer()?.let {
                startScope.user.value = it
            }
        }

        return startScope
    }

    companion object {
        @Deprecated("use SharedFlow when available")
        private val events = ConflatedBroadcastChannel<Event>()

        fun sendEvent(event: Event) = events.offer(event)
    }
}