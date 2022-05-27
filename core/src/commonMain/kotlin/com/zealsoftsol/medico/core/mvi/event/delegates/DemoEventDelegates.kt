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
) : EventDelegate<Event.Action.Demo>(navigator) {
    override suspend fun handleEvent(event: Event.Action.Demo) {
        when (event) {
            is Event.Action.Demo.MyDemo -> demoData()
            is Event.Action.Demo.OpenVideo -> openVideoScope(event.url)
            is Event.Action.Demo.ReleasePlayer -> releasePlayer()
        }
    }

    private fun releasePlayer() {
        navigator.withScope<DemoScope.DemoPlayer> {
            it.releasePlayer.value = true
        }
    }

    private fun openVideoScope(url: String) {
        navigator.withScope<DemoScope.DemoListing> {
            navigator.setScope(DemoScope.DemoPlayer(url))
        }
    }

    private suspend fun demoData() {
        navigator.withScope<DemoScope.DemoListing> {
            withProgress {
                networkDemoScope.getDemoData().onSuccess { body ->
                    it.demoData.value = body
                }
            }.onError(navigator)
        }
    }
}