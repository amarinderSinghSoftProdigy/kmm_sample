package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.HelpScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.ErrorCode

internal class HelpEventDelegate(
    navigator: Navigator,
    private val networkHelpScope: NetworkScope.Help,
) : EventDelegate<Event.Action.Help>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Help) = when (event) {
        is Event.Action.Help.GetHelp -> getHelp()
    }

    private suspend fun getHelp() {
        val (result, isSuccess) = navigator.withProgress { networkHelpScope.getHelp() }
        if (isSuccess && result != null) {
            navigator.setScope(HelpScope(result))
        } else {
            navigator.setHostError(ErrorCode())
        }
    }
}