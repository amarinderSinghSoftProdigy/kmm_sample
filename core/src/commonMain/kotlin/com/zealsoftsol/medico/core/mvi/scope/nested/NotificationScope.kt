package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.NotificationAction
import com.zealsoftsol.medico.data.NotificationData
import com.zealsoftsol.medico.data.NotificationDetails
import com.zealsoftsol.medico.data.NotificationFilter
import com.zealsoftsol.medico.data.NotificationOption

sealed class NotificationScope : Scope.Child.TabBar() {

    class All(
        override val items: DataSource<List<NotificationData>> = DataSource(emptyList()),
        override val totalItems: DataSource<Int> = DataSource(0),
        override val searchText: DataSource<String> = DataSource(""),
        val filter: DataSource<NotificationFilter> = DataSource(NotificationFilter.ALL),
    ) : NotificationScope(), Loadable<NotificationData> {

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) = TabBarInfo.OnlyBackHeader("notifications")

        val allFilters: List<NotificationFilter> = NotificationFilter.values().toList()

        init {
            EventCollector.sendEvent(Event.Action.Notification.Load(isFirstLoad = true))
        }

        override val pagination: Pagination = Pagination()

        fun selectFilter(filter: NotificationFilter) =
            EventCollector.sendEvent(Event.Action.Notification.SelectFilter(filter))

        fun selectItem(item: NotificationData): Boolean {
            if (item.selectedAction != null) return false
            return EventCollector.sendEvent(Event.Action.Notification.Select(item))
        }

        fun deleteNotification(item: String) {
            EventCollector.sendEvent(Event.Action.Notification.DeleteNotification(item))
        }

        fun search(value: String) =
            EventCollector.sendEvent(Event.Action.Notification.Search(value))

        fun loadItems() =
            EventCollector.sendEvent(Event.Action.Notification.Load(isFirstLoad = false))

        fun clearAll() = EventCollector.sendEvent(Event.Action.Notification.ClearAll)
    }

    sealed class Preview<T : NotificationDetails.TypeSafe, O : NotificationOption>(
        val notification: NotificationData,
        val details: DataSource<T?> = DataSource(null)
    ) : NotificationScope() {

        fun changeOptions(option: O) =
            EventCollector.sendEvent(Event.Action.Notification.ChangeOptions(option))

        fun selectAction(action: NotificationAction) =
            EventCollector.sendEvent(Event.Action.Notification.SelectAction(action))

        class SubscriptionRequest(
            notification: NotificationData,
        ) : Preview<NotificationDetails.TypeSafe.Subscription, NotificationOption.Subscription>(
            notification
        )
    }
}

internal typealias GenericNotificationScopePreview = NotificationScope.Preview<NotificationDetails.TypeSafe, NotificationOption>