package com.zealsoftsol.medico.core.mvi.event

import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.Time
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.delegates.AuthEventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.EventDelegate
import com.zealsoftsol.medico.core.mvi.event.delegates.TransitionEventDelegate
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.mvi.scope.regular.LogInScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.utils.TapModeHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class EventCollector(
    navigator: Navigator,
    private val userRepo: UserRepo,
    tapModeHelper: TapModeHelper,
) {
    private val loadHelperScope = CoroutineScope(compatDispatcher)

    private val delegateMap = mapOf<KClass<*>, EventDelegate<*>>(
        Event.Transition::class to TransitionEventDelegate(
            navigator,
            userRepo
        ),
        Event.Action.Auth::class to AuthEventDelegate(
            navigator,
            userRepo
        ),

    )

    init {
        var lastActionTime = Time.now
        val scope = CoroutineScope(compatDispatcher)
        events.onEach {
            lastActionTime = Time.now
            delegateMap[it.typeClazz]!!.genericHandle(it)
        }.launchIn(scope)
        scope.launch {
            while (isActive) {
                delay(60_000)
                if (Time.now - lastActionTime > userRepo.configFlow.value.sessionTimeout) {
                    sendEvent(Event.Action.Auth.LogOut(notifyServer = true))
                }
            }
        }
    }

    fun getStartingScope(): Scope {
        return when (userRepo.getUserAccess()) {
            UserRepo.UserAccess.FULL_ACCESS -> DashboardScope.get()
            UserRepo.UserAccess.NO_ACCESS -> LogInScope(DataSource(userRepo.getAuthCredentials()))
        }
    }

    fun getAlertToggle(): Boolean {
        return userRepo.getAlertToggle()
    }


    fun updateData() {
        //call the apis to collect data
    }

    companion object {
        private val events = MutableSharedFlow<Event>(1, 0, BufferOverflow.DROP_OLDEST)

        fun sendEvent(event: Event) = events.tryEmit(event)
    }
}