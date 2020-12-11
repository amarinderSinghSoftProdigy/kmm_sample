package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector

sealed class MainScope : BaseScope() {

    /**
     * Transition to [LogInScope] if successful
     */
    fun tryLogOut() = EventCollector.sendEvent(Event.Action.Auth.LogOut)

    data class LimitedAccess(val isDocumentUploaded: Boolean) : MainScope()

    class FullAccess : MainScope()
}