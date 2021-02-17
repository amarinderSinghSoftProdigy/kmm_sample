package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.NotificationScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.NotificationData

internal class NotificationEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkNotificationScope: NetworkScope.Notification,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.Notification>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Notification) = when (event) {
        is Event.Action.Notification.Load -> load(event.isFirstLoad)
        is Event.Action.Notification.Search -> search(event.value)
        is Event.Action.Notification.Select -> select(event.notification)
    }

    private suspend fun load(isFirstLoad: Boolean) {
        loadHelper.load<NotificationScope.All, NotificationData>(isFirstLoad = isFirstLoad) {
            val (result, isSuccess) = networkNotificationScope.getNotifications(
                search = searchText.value,
                pagination = pagination,
            )
            if (isSuccess) result else null
        }
    }

    private suspend fun search(value: String) {
        loadHelper.search<NotificationScope.All, NotificationData>(searchValue = value) {
            val (result, isSuccess) = networkNotificationScope.getNotifications(
                search = searchText.value,
                pagination = pagination,
            )
            if (isSuccess) result else null
        }
    }

    private suspend fun select(data: NotificationData) {

    }
}