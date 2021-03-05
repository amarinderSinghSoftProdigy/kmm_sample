package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.User

class DashboardScope private constructor(
    val unreadNotifications: ReadOnlyDataSource<Int>,
) : Scope.Child.TabBar(TabBarInfo.Search(ScopeIcon.HAMBURGER)) {

    fun goToNotifications() = EventCollector.sendEvent(Event.Transition.Notifications)

    companion object {
        fun get(
            user: User,
            userDataSource: ReadOnlyDataSource<User>,
            unreadNotifications: ReadOnlyDataSource<Int>,
        ) = Host.TabBar(
            childScope = DashboardScope(unreadNotifications),
            navigationSectionValue = NavigationSection(
                userDataSource,
                NavigationOption.default(user.type),
                NavigationOption.footer()
            ),
        )
    }
}