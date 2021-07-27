package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.nested.HelpScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope

internal class HelpEventDelegate(
    navigator: Navigator,
    private val networkHelpScope: NetworkScope.Help,
) : EventDelegate<Event.Action.Help>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Help) = when (event) {
        is Event.Action.Help.GetHelp -> getHelp()
    }

    private suspend fun getHelp() {
        navigator.withProgress { networkHelpScope.getHelp() }
            .onSuccess { body ->
                navigator.setScope(HelpScope(body))
            }.onError(navigator)
    }
}