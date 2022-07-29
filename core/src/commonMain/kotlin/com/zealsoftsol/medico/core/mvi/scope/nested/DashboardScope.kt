package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope

/**
 * Entry scope for authorized activated users
 */
class DashboardScope private constructor() : Scope.Child.TabBar() {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        TabBarInfo.NoHeader("")

    override val isRoot: Boolean = true

    init {
        EventCollector.sendEvent(Event.Action.Auth.UpdateDashboard)
    }


    fun sendEvent(event: Event) = EventCollector.sendEvent(event)

    companion object {
        fun get() = TabBarScope(
            childScope = DashboardScope(),
            initialTabBarInfo = TabBarInfo.NoHeader(),
            initialNavigationSection = NavigationSection(
                NavigationOption.default(),
                NavigationOption.footer()
            ),
        )
    }
}