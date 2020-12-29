package com.zealsoftsol.medico.core.mvi

import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector

data class NavigationSection(val main: List<NavigationOption>, val footer: List<NavigationOption>)

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