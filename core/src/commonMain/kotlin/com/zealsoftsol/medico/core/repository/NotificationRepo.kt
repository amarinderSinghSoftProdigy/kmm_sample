package com.zealsoftsol.medico.core.repository

import com.russhwolf.settings.Settings
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.network.NetworkScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class NotificationRepo(
    private val settings: Settings,
    notificationNetworkScope: NetworkScope.Notification
) : NetworkScope.Notification by notificationNetworkScope {

    val unreadMessages: MutableStateFlow<Int> =
        MutableStateFlow(settings.getInt(UNREAD_MESSAGES, 0))

    suspend fun loadUnreadMessagesFromServer() {
        kotlin.runCatching { }.onSuccess { }
        getUnreadNotifications().getBodyOrNull()?.unreadNotifications?.let(::updateUnreadMessages)
    }

    fun updateUnreadMessages(value: Int) {
        unreadMessages.value = value
        settings.putInt(UNREAD_MESSAGES, value)
    }

    fun decreaseReadMessages() {
        val newUnread = (settings.getInt(UNREAD_MESSAGES, 0) - 1).coerceAtLeast(0)
        updateUnreadMessages(newUnread)
    }

    companion object {
        private const val UNREAD_MESSAGES = "unr_mes"
    }
}

internal inline fun NotificationRepo.getUnreadMessagesDataSource(): ReadOnlyDataSource<Int> =
    ReadOnlyDataSource(
        unreadMessages.stateIn(GlobalScope, SharingStarted.Eagerly, 0)
    )