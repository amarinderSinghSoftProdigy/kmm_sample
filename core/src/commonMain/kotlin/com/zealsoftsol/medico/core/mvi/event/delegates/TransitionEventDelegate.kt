package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.DashboardScope
import com.zealsoftsol.medico.core.repository.UserRepo

internal class TransitionEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Transition>(navigator) {

    override suspend fun handleEvent(event: Event.Transition) {
        navigator.apply {
            when (event) {
                is Event.Transition.Back -> dropScope()
                is Event.Transition.Refresh -> refresh()
                is Event.Transition.Dashboard -> {
                    dropScope(Navigator.DropStrategy.All, updateDataSource = false)
                    setScope(DashboardScope.get())
                }
            }
        }
    }
}