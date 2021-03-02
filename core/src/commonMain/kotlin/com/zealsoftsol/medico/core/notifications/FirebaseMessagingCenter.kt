package com.zealsoftsol.medico.core.notifications

import com.zealsoftsol.medico.core.compatDispatcher
import com.zealsoftsol.medico.core.extensions.log
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.network.createJson
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.NotificationData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class FirebaseMessagingCenter(private val userRepo: UserRepo) : FirebaseMessaging {

    override val notifications: DataSource<NotificationMessage?> = DataSource(null)
    private val scope = CoroutineScope(compatDispatcher + SupervisorJob())
    private val json by lazy { createJson() }

    override fun handleMessage(data: Map<String, Any>) {
        data.log("handle message")
        val notificationJson = data["NOTIFICATIONS"] as String
        runCatching {
            json.decodeFromString(NotificationData.serializer(), notificationJson)
        }.getOrNull()?.let {
            notifications.value = NotificationMessage(it.id, it.title, it.body)
        }
    }

    override fun handleNewToken(token: String) {
        scope.launch { userRepo.sendFirebaseToken(token) }
    }

    override fun dismissMessage(id: String) {
        if (notifications.value?.id == id) {
            notifications.value = null
        }
    }
}

data class NotificationMessage(
    val id: String,
    val title: String,
    val body: String,
)

interface FirebaseMessaging {

    val notifications: DataSource<NotificationMessage?>

    fun handleMessage(data: Map<String, Any>)

    fun handleNewToken(token: String)

    fun dismissMessage(id: String)
}