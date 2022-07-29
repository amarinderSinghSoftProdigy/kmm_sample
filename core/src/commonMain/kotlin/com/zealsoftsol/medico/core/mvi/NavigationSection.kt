package com.zealsoftsol.medico.core.mvi

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector

data class NavigationSection(
    val main: List<NavigationOption> = NavigationOption.empty(),
    val footer: List<NavigationOption> = NavigationOption.empty(),
)

sealed class NavigationOption(private val event: Event, val stringId: String) {

    fun select() = EventCollector.sendEvent(event)

    object Dashboard : NavigationOption(Event.Transition.Dashboard, "dashboard")
    object LogOut : NavigationOption(Event.Action.Auth.LogOut(true), "log_out")


    companion object {
        internal fun empty() = emptyList<NavigationOption>()
        internal fun limited() = listOfNotNull("")

        internal fun default() = listOfNotNull(
            Dashboard,
        )

        internal fun footer() = listOf(
            LogOut
        )
    }
}