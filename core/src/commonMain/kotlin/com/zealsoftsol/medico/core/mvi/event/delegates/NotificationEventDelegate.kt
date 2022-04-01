package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.nested.GenericNotificationScopePreview
import com.zealsoftsol.medico.core.mvi.scope.nested.NotificationScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.NotificationRepo
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.NotificationAction
import com.zealsoftsol.medico.data.NotificationActionRequest
import com.zealsoftsol.medico.data.NotificationData
import com.zealsoftsol.medico.data.NotificationDetails
import com.zealsoftsol.medico.data.NotificationFilter
import com.zealsoftsol.medico.data.NotificationOption
import com.zealsoftsol.medico.data.NotificationType

internal class NotificationEventDelegate(
    navigator: Navigator,
    private val notificationRepo: NotificationRepo,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.Notification>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Notification) = when (event) {
        is Event.Action.Notification.Load -> load(event.isFirstLoad)
        is Event.Action.Notification.Search -> search(event.value)
        is Event.Action.Notification.Select -> select(event.notification)
        is Event.Action.Notification.SelectAction -> selectAction(event.action)
        is Event.Action.Notification.ChangeOptions -> changeOption(event.option)
        is Event.Action.Notification.SelectFilter -> selectFilter(event.filter)
        is Event.Action.Notification.DeleteNotification -> deleteNotification(event.notificationId)
//        is Event.Action.Notification.UpdateUnreadMessages -> updateUnreadMessages()
    }

    private suspend fun load(isFirstLoad: Boolean) {
        loadHelper.load<NotificationScope.All, NotificationData>(isFirstLoad = isFirstLoad) {
            notificationRepo.getNotifications(
                search = searchText.value,
                pagination = pagination,
                filter = filter.value,
            ).getBodyOrNull()
        }
    }

    private suspend fun search(value: String) {
        loadHelper.search<NotificationScope.All, NotificationData>(searchValue = value) {
            notificationRepo.getNotifications(
                search = searchText.value,
                pagination = pagination,
                filter = filter.value,
            ).getBodyOrNull()
        }
    }

    private suspend fun select(data: NotificationData) {
        navigator.withScope<NotificationScope.All> {
            when (data.type) {
                NotificationType.SUBSCRIBE_REQUEST, NotificationType.SUBSCRIBE_DECISION -> openableNotification(
                    data
                )
                NotificationType.ORDER_REQUEST, NotificationType.INVOICE_REQUEST -> routedNotification(
                    data
                )
            }
        }
    }

    private suspend fun Navigator.openableNotification(data: NotificationData) {
        val nextScope = NotificationScope.Preview.SubscriptionRequest(data)
        setScope(nextScope)
        withProgress {
            notificationRepo.getNotificationDetails(data.id)
        }.onSuccess { body ->
            notificationRepo.decreaseReadMessages()
            when {
                body.customerData != null && body.subscriptionOption != null -> {
                    nextScope.details.value = NotificationDetails.TypeSafe.Subscription(
                        isReadOnly = data.type == NotificationType.SUBSCRIBE_DECISION,
                        customerData = body.customerData!!,
                        option = body.subscriptionOption!!,
                    )
                }
                else -> {
                    dropScope()
                    setHostError(ErrorCode.somethingWentWrong)
                }
            }
        }.onError {
            dropScope()
            setHostError(it)
        }
    }

    private suspend fun Navigator.routedNotification(data: NotificationData) {
        withProgress {
            notificationRepo.getNotificationDetails(data.id)
        }.onSuccess { body ->
            when {
                body.orderOption != null -> EventCollector.sendEvent(
                    Event.Action.Orders.Select(
                        orderId = body.orderOption!!.orderId,
                        type = body.orderOption!!.type,
                    )
                )
                body.invoiceOption != null -> EventCollector.sendEvent(
                    Event.Action.Invoices.Select(
                        invoiceId = body.invoiceOption!!.invoiceId,
                        isPoInvoice = false
                    )
                )
                else -> {
                    setHostError(ErrorCode.somethingWentWrong)
                }
            }
        }.onError(navigator)
    }

    private suspend fun selectAction(action: NotificationAction) {
        navigator.withScope<GenericNotificationScopePreview> {
            withProgress {
                notificationRepo.selectNotificationAction(
                    id = it.notification.id,
                    actionRequest = NotificationActionRequest(
                        action,
                        it.details.value?.option as? NotificationOption.Subscription,
                    ),
                )
            }.onSuccess {
                dropScope()
                load(isFirstLoad = true)
            }.onError(navigator)
        }
    }

    private fun changeOption(option: NotificationOption) {
        navigator.withScope<GenericNotificationScopePreview> {
            it.details.value = it.details.value?.withNewOption(option)
        }
    }

    private suspend fun selectFilter(filter: NotificationFilter) {
        navigator.withScope<NotificationScope.All> {
            it.filter.value = filter
            load(isFirstLoad = true)
        }
    }

//    private suspend fun updateUnreadMessages() {
//        notificationRepo.loadUnreadMessagesFromServer()
//    }

    private suspend fun deleteNotification(id: String) {
        navigator.withScope<NotificationScope> {
            withProgress {
                notificationRepo.deleteNotification(
                    id = id,
                )
            }.onSuccess {
                //dropScope()
                load(isFirstLoad = true)
            }.onError(navigator)
        }
    }
}