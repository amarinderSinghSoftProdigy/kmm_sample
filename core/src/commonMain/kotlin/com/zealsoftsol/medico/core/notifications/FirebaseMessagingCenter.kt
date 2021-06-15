package com.zealsoftsol.medico.core.notifications

import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.network.createJson
import com.zealsoftsol.medico.core.repository.NotificationRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.NotificationData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class FirebaseMessagingCenter(
    private val userRepo: UserRepo,
    private val notificationRepo: NotificationRepo,
) : FirebaseMessaging {

    override val notificationMessage: DataSource<NotificationMessage?> = DataSource(null)
    private val scope = CoroutineScope(compatDispatcher + SupervisorJob())
    private val json by lazy { createJson() }

    override fun handleMessage(data: Map<String, Any>) {
        val notificationJson = data["NOTIFICATIONS"] as String
        (data["unreadNotifications"] as String).toIntOrNull()
            ?.let(notificationRepo::updateUnreadMessages)
        runCatching {
            json.decodeFromString(NotificationData.serializer(), notificationJson)
        }.getOrNull()?.let {
            notificationMessage.value = NotificationMessage(it.id, it.title, it.body)
        }
    }

    override fun handleNewToken(token: String) {
        scope.launch { userRepo.sendFirebaseToken(token) }
    }

    override fun messageClicked(id: String) {
        if (notificationMessage.value?.id == id) {
            notificationMessage.value = null
        }
        EventCollector.sendEvent(Event.Transition.Notifications)
    }
}

data class NotificationMessage(
    val id: String,
    val title: String,
    val body: String,
)

interface FirebaseMessaging {

    val notificationMessage: DataSource<NotificationMessage?>

    fun handleMessage(data: Map<String, Any>)

    fun handleNewToken(token: String)

    fun messageClicked(id: String)
}