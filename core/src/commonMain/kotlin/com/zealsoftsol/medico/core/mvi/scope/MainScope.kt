package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector

data class MainScope(
    override val isInProgress: Boolean = false,
) : BaseScope() {

    /**
     * Transition to [LogInScope] if successful
     */
    fun tryLogOut() = EventCollector.sendEvent(Event.Action.Auth.LogOut)
}