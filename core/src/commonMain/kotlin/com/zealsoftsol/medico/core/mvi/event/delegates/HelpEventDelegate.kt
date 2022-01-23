package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.nested.HelpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope

internal class HelpEventDelegate(
    navigator: Navigator,
    private val networkHelpScope: NetworkScope.Help,
) : EventDelegate<Event.Action.Help>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Help) = when (event) {
        is Event.Action.Help.GetHelp -> getHelp()
        is Event.Action.Help.ChangeTab -> getHelp(event.index)
        is Event.Action.Help.GetContactUs -> getContactUs()
        is Event.Action.Help.GetTandC -> getTandC()
    }

    private suspend fun getHelp() {
        navigator.withProgress { networkHelpScope.getHelp() }
            .onSuccess { body ->
                navigator.setScope(HelpScope(body))
            }.onError(navigator)
    }

    private fun getHelp(index: String) {
        navigator.withScope<HelpScope.TandC> {
            it.loadUrl.value = index
        }
    }

    private suspend fun getTandC() {
        navigator.withProgress { networkHelpScope.getHelp() }
            .onSuccess { body ->
                navigator.setScope(HelpScope.TandC(body))
            }.onError(navigator)
    }

    private suspend fun getContactUs() {
        navigator.withProgress { networkHelpScope.getHelp() }
            .onSuccess { body ->
                navigator.setScope(HelpScope.ContactUs(body))
            }.onError(navigator)
    }

}