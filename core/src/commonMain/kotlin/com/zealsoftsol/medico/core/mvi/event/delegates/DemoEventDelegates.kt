package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.nested.OffersScope
import com.zealsoftsol.medico.core.mvi.scope.regular.DemoScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo

internal class DemoEventDelegates(
    navigator: Navigator,
    private val networkDemoScope: NetworkScope.DemoData
) : EventDelegate<Event.Action>(navigator) {
    override suspend fun handleEvent(event: Event.Action) {
        when (event) {
            is Event.Action.Demo.MyDemo -> demoData()
        }
    }

    private suspend fun demoData() {
        navigator.withScope<DemoScope> {
            withProgress {
                networkDemoScope.getDemoData().onSuccess { body ->
                    it.demoData?.value = body
                }
            }.onError(navigator)
        }
    }
}