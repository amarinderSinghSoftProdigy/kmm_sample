package com.zealsoftsol.medico.core.mvi.event

import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.delegates.AuthEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.EventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.PasswordEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.RegistrationEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.TransitionEventDelegate
import com.zealsoftsol.medico.core.mvi.viewmodel.AuthViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

internal class EventCollector(
    private val navigator: Navigator,

    private val authViewModel: AuthViewModel,
) {
    private val delegateMap = hashMapOf<KClass<*>, EventDelegate<*>>().apply {
        put(Event.Action.Auth::class, AuthEventDelegate(navigator, authViewModel))
        put(Event.Action.Password::class, PasswordEventDelegate(navigator, authViewModel))
        put(Event.Action.Registration::class, RegistrationEventDelegate(navigator, authViewModel))
        put(Event.Transition::class, TransitionEventDelegate(navigator, authViewModel))
    }

    init {
        GlobalScope.launch(compatDispatcher) {
            for (event in events.openSubscription()) {
                delegateMap[event.typeClazz]!!.genericHandle(event)
            }
        }
    }

    companion object {
        private val events = ConflatedBroadcastChannel<Event>()

        fun sendEvent(event: Event) = events.offer(event)
    }
}