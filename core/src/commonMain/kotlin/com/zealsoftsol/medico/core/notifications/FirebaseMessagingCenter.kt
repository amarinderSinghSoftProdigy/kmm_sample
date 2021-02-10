package com.zealsoftsol.medico.core.notifications

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.repository.UserRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class FirebaseMessagingCenter(private val userRepo: UserRepo) : FirebaseMessaging {

    override val notifications: DataSource<NotificationMessage?> = DataSource(null)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun handleMessage(data: Map<String, String>) {
        when (data[TYPE_KEY]) {
            "req" -> requestReceived(data["user"])
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

    private fun requestReceived(user: String?) {
        notifications.value = NotificationMessage("id", "title", "body")
    }

    companion object {
        private const val TYPE_KEY = ""
    }
}

data class NotificationMessage(
    val id: String,
    val title: String,
    val body: String,
)

interface FirebaseMessaging {
    val notifications: DataSource<NotificationMessage?>

    fun handleMessage(data: Map<String, String>)

    fun handleNewToken(token: String)

    fun dismissMessage(id: String)
}