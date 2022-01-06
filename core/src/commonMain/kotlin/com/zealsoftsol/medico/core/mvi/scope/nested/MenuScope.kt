package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.User

class MenuScope(
    val user: User, val unreadNotifications: ReadOnlyDataSource<Int>,
) : Scope.Child.TabBar() {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo =
        TabBarInfo.NoIconTitle(
            title = "",
            notificationItemsCount = unreadNotifications
        )

    /**
     * Handle events
     */
    fun sendEvent(action: Event.Action? = null, transition: Event.Transition? = null) {

        if (action != null) {
            EventCollector.sendEvent(action)
        } else if (transition != null) {
            EventCollector.sendEvent(transition)
        }
    }
}