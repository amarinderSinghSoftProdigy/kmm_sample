package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.NavigationOption
import com.zealsoftsol.medico.core.mvi.NavigationSection
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.regular.TabBarScope
import com.zealsoftsol.medico.data.User

/**
 * Entry scope for authorized activated users
 */
class DashboardScope private constructor(
    val unreadNotifications: ReadOnlyDataSource<Int>,
) : Scope.Child.TabBar() {

    override val isRoot: Boolean = true

    fun goToNotifications() = EventCollector.sendEvent(Event.Transition.Notifications)

    companion object {
        fun get(
            user: User,
            userDataSource: ReadOnlyDataSource<User>,
            unreadNotifications: ReadOnlyDataSource<Int>,
            cartItemsCount: ReadOnlyDataSource<Int>,
        ) = TabBarScope(
            childScope = DashboardScope(unreadNotifications),
            initialTabBarInfo = TabBarInfo.Search(cartItemsCount = cartItemsCount),
            initialNavigationSection = NavigationSection(
                userDataSource,
                NavigationOption.default(user.type),
                NavigationOption.footer()
            ),
        )
    }
}