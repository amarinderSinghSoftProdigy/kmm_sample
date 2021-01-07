package com.zealsoftsol.medico.core.mvi

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.User

data class NavigationSection(
    val user: ReadOnlyDataSource<User>,
    val main: List<NavigationOption> = NavigationOption.empty(),
    val footer: List<NavigationOption> = NavigationOption.empty(),
)

sealed class NavigationOption(private val event: Event) {

    fun select() = EventCollector.sendEvent(event)

    object Settings : NavigationOption(Event.Transition.Back)
    object LogOut : NavigationOption(Event.Action.Auth.LogOut(true))

    companion object {
        internal fun empty() = emptyList<NavigationOption>()
        internal fun limited() = listOf(
            Settings,
        )

        internal fun default() = listOf(
            Settings,
        )

        internal fun footer() = listOf(
            LogOut
        )
    }
}