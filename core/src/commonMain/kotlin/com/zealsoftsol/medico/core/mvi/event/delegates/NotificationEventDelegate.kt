package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
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
//        is Event.Action.Notification.UpdateUnreadMessages -> updateUnreadMessages()
    }

    private suspend fun load(isFirstLoad: Boolean) {
        loadHelper.load<NotificationScope.All, NotificationData>(isFirstLoad = isFirstLoad) {
            val (result, isSuccess) = notificationRepo.getNotifications(
                search = searchText.value,
                pagination = pagination,
            )
            if (isSuccess) result else null
        }
    }

    private suspend fun search(value: String) {
        loadHelper.search<NotificationScope.All, NotificationData>(searchValue = value) {
            val (result, isSuccess) = notificationRepo.getNotifications(
                search = searchText.value,
                pagination = pagination,
            )
            if (isSuccess) result else null
        }
    }

    private suspend fun select(data: NotificationData) {
        require(data.actions.isNotEmpty()) { "can not select notification with no actions" }

        navigator.withScope<NotificationScope.All> {
            val nextScope = when (data.type) {
                NotificationType.SUBSCRIBE_REQUEST ->
                    NotificationScope.Preview.SubscriptionRequest(data)
                NotificationType.SUBSCRIBE_DECISION ->
                    throw UnsupportedOperationException("subscription decision is not previewable")
                NotificationType.ORDER_REQUEST -> TODO("not implemented")
            } as GenericNotificationScopePreview
            setScope(nextScope)
            val (result, isSuccess) = withProgress {
//                notificationRepo.markNotification(data.id, NotificationStatus.READ)
                notificationRepo.getNotificationDetails(data.id)
            }
            if (isSuccess && result != null) {
                notificationRepo.decreaseReadMessages()
                when {
                    result.customerData != null && result.subscriptionOption != null -> {
                        nextScope.details.value = NotificationDetails.TypeSafe.Subscription(
                            result.customerData!!,
                            result.subscriptionOption!!
                        )
                    }
                    else -> {
                        dropScope()
                        setHostError(ErrorCode())
                    }
                }
            } else {
                dropScope()
                setHostError(ErrorCode())
            }
        }
    }

    private suspend fun selectAction(action: NotificationAction) {
        navigator.withScope<GenericNotificationScopePreview> {
            val (error, isSuccess) = withProgress {
                notificationRepo.selectNotificationAction(
                    id = it.notification.id,
                    actionRequest = NotificationActionRequest(
                        action,
                        it.details.value?.option as? NotificationOption.Subscription,
                    ),
                )
            }
            if (isSuccess) {
                dropScope()
                load(isFirstLoad = true)
            } else {
                setHostError(error ?: ErrorCode())
            }
        }
    }

    private fun changeOption(option: NotificationOption) {
        navigator.withScope<GenericNotificationScopePreview> {
            it.details.value = it.details.value?.withNewOption(option)
        }
    }

//    private suspend fun updateUnreadMessages() {
//        notificationRepo.loadUnreadMessagesFromServer()
//    }
}