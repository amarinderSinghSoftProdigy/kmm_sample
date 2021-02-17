package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class NotificationData(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val actions: List<NotificationAction>,
    val selectedAction: NotificationAction?,
    val status: NotificationStatus,
    val sentAt: Long,
)

enum class NotificationType(val buttonStringId: String) {
    SUBSCRIBE_REQUEST("subscribe_request_button"),
    ORDER_REQUEST("order_request_button"),
}

enum class NotificationAction(val actionStringId: String, val completedActionStringId: String) {
    ACCEPT("accept", "accepted"),
    DECLINE("decline", "declined"),
}

enum class NotificationStatus(val stringId: String) {
    READ("read"),
    UNREAD("unread"),
}