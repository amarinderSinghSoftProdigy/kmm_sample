package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope

class WelcomeScope(val fullName: String) : Scope.Host.Regular() {

    fun accept() = EventCollector.sendEvent(Event.Action.Registration.AcceptWelcome)
}