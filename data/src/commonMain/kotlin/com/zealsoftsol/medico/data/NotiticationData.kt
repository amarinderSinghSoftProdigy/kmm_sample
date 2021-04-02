package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationData(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val actions: List<NotificationAction>,
    val selectedAction: NotificationAction? = null,
    val status: NotificationStatus,
    val sentAt: Long,
)

@Serializable
data class UnreadNotifications(
    @SerialName("totalNotifications")
    val unreadNotifications: Int,
    // totalOrders
)

@Serializable
data class NotificationDetails(
    val customerData: CustomerData? = null,
    val subscriptionOption: NotificationOption.Subscription? = null,
) {

    sealed class TypeSafe {
        abstract val option: NotificationOption?

        abstract fun withNewOption(option: NotificationOption): TypeSafe

        data class Subscription(
            val isReadOnly: Boolean,
            val customerData: CustomerData,
            override val option: NotificationOption.Subscription,
        ) : TypeSafe() {

            override fun withNewOption(option: NotificationOption): TypeSafe =
                copy(option = option as NotificationOption.Subscription)
        }
    }
}

@Serializable
data class NotificationActionRequest(
    val action: NotificationAction,
    val subscriptionOption: NotificationOption.Subscription? = null,
)

sealed class NotificationOption {

    @Serializable
    data class Subscription(
        val paymentMethod: PaymentMethod,
        val discountRate: String,
        val creditDays: String,
    ) : NotificationOption()
}


enum class NotificationType(val buttonStringId: String) {
    SUBSCRIBE_REQUEST("subscribe_request_button"),
    SUBSCRIBE_DECISION("subscribe_request_button"),
    ORDER_REQUEST("order_request_button"),
}

enum class NotificationAction(
    val isHighlighted: Boolean,
    val actionStringId: String,
    val completedActionStringId: String
) {
    ACCEPT(true, "accept", "accepted"),
    DECLINE(false, "decline", "declined"),
}

enum class NotificationStatus(val stringId: String) {
    READ("read"),
    UNREAD("unread"),
}