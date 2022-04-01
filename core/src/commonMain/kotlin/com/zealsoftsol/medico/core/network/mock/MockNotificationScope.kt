package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.interop.Time
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.NotificationAction
import com.zealsoftsol.medico.data.NotificationActionRequest
import com.zealsoftsol.medico.data.NotificationData
import com.zealsoftsol.medico.data.NotificationDetails
import com.zealsoftsol.medico.data.NotificationFilter
import com.zealsoftsol.medico.data.NotificationOption
import com.zealsoftsol.medico.data.NotificationStatus
import com.zealsoftsol.medico.data.NotificationType
import com.zealsoftsol.medico.data.PaginatedData
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.UnreadNotifications
import com.zealsoftsol.medico.data.UserType
import kotlin.random.Random

class MockNotificationScope : NetworkScope.Notification {

    private val rnd = Random(Time.now)

    init {
        "USING MOCK NOTIFICATION SCOPE".logIt()
    }

    override suspend fun sendFirebaseToken(token: String) = mockResponse { mockEmptyMapBody() }

    override suspend fun getNotifications(
        search: String,
        filter: NotificationFilter,
        pagination: Pagination,
    ) = mockResponse {
        longPaginatedData(20, rnd)
    }

    override suspend fun getUnreadNotifications() =
        mockResponse {
            UnreadNotifications(10)
        }

//    override suspend fun markNotification(
//        id: String,
//        status: NotificationStatus
//    ): Response.Wrapped<ErrorCode> = mockResponse {
//        null
//    }

    override suspend fun getNotificationDetails(id: String) =
        mockResponse {
            NotificationDetails(
                customerData = MockCustomerScope.getMockCustomerDataV2(UserType.RETAILER),
                subscriptionOption = NotificationOption.Subscription(
                    PaymentMethod.CREDIT,
                    "0",
                    "0"
                ),
            )
        }

    override suspend fun selectNotificationAction(
        id: String,
        actionRequest: NotificationActionRequest
    ) = mockResponse {
        mockEmptyMapBody()
    }

    override suspend fun deleteNotification(
        id: String,
    ) = mockResponse {
        mockEmptyMapBody()
    }
}

private fun longPaginatedData(size: Int, rnd: Random) =
    PaginatedData(
        (0 until size)
            .map {
                NotificationData(
                    id = Time.now.toString(),
                    title = "Notification $it",
                    body = "Notification body",
                    type = if (rnd.nextBoolean()) NotificationType.SUBSCRIBE_REQUEST else NotificationType.ORDER_REQUEST,
                    actions = NotificationAction.values().toList(),
                    selectedAction = NotificationAction.values()
                        .getOrNull(rnd.nextInt(NotificationAction.values().size + 1)),
                    status = if (rnd.nextBoolean()) NotificationStatus.READ else NotificationStatus.UNREAD,
                    sentAt = Time.now - 1000 * 60 * 60 * rnd.nextInt(1, 12),
                )
            },
        9999999,
    )