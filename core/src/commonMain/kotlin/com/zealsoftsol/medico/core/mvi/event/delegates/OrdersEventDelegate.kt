package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.ConfirmOrderScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OrdersScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SelectableOrderEntry
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewOrderScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.ConfirmOrderRequest
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.Order
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.OrderNewQtyRequest
import com.zealsoftsol.medico.data.OrderType

internal class OrdersEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkOrdersScope: NetworkScope.Orders,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.Orders>(navigator), CommonScope.CanGoBack {

    override suspend fun handleEvent(event: Event.Action.Orders) = when (event) {
        is Event.Action.Orders.Load -> loadOrders(event.isFirstLoad)
        is Event.Action.Orders.Search -> searchOrders(event.value)
        is Event.Action.Orders.Select -> selectOrder(event.item)
        is Event.Action.Orders.ViewOrderAction -> viewOrderAction(
            event.action,
            event.fromNotification,
        )
        is Event.Action.Orders.SelectEntry -> selectEntry(event.entry)
        is Event.Action.Orders.ToggleCheckEntry -> toggleCheckEntry(event.entry)
        is Event.Action.Orders.SaveEntryQty -> saveEntryQty(event.entry, event.quantity)
        is Event.Action.Orders.Confirm -> confirmOrder()
    }

    private suspend fun loadOrders(isFirstLoad: Boolean) {
        loadHelper.load<OrdersScope, Order>(isFirstLoad = isFirstLoad) {
            val user = userRepo.requireUser()
            val (result, isSuccess) = networkOrdersScope.getOrders(
                type = type,
                unitCode = user.unitCode,
                search = searchText.value,
                from = dateRange.value?.fromMs,
                to = dateRange.value?.toMs,
                pagination = pagination,
            )
            if (isSuccess) result else null
        }
    }

    private suspend fun searchOrders(search: String) {
        loadHelper.search<OrdersScope, Order>(searchValue = search) {
            val user = userRepo.requireUser()
            val (result, isSuccess) = networkOrdersScope.getOrders(
                type = type,
                unitCode = user.unitCode,
                search = searchText.value,
                from = dateRange.value?.fromMs,
                to = dateRange.value?.toMs,
                pagination = pagination,
            )
            if (isSuccess) result else null
        }
    }

    private suspend fun selectOrder(item: Order) {
        navigator.withScope<OrdersScope> {
            val (result, isSuccess) = withProgress {
                networkOrdersScope.getOrder(it.type, userRepo.requireUser().unitCode, item.info.id)
            }
            if (isSuccess && result != null) {
                setScope(
                    ViewOrderScope(
                        canEdit = it.type == OrderType.RECEIVED,
                        DataSource(item),
                        DataSource(result.unitData.data),
                        DataSource(result.entries)
                    )
                )
            } else {
                setHostError(ErrorCode())
            }
        }
    }

    private fun viewOrderAction(action: ViewOrderScope.Action, fromNotification: Boolean) {
        navigator.withScope<ViewOrderScope> {
            val nextScope = when (action) {
                ViewOrderScope.Action.REJECT_ALL -> {
                    if (fromNotification) {
                        it.dismissNotification()
                        ConfirmOrderScope(
                            order = it.order,
                            acceptedEntries = emptyList(),
                            rejectedEntries = it.entries.value,
                        )
                    } else {
                        it.notifications.value = ViewOrderScope.RejectAll(action)
                        null
                    }
                }
                ViewOrderScope.Action.ACCEPT_ALL -> {
                    if (fromNotification || it.entries.value.none { it.buyingOption == BuyingOption.QUOTE }) {
                        it.dismissNotification()
                        ConfirmOrderScope(
                            order = it.order,
                            acceptedEntries = it.entries.value,
                            rejectedEntries = emptyList(),
                        )
                    } else {
                        it.notifications.value = ViewOrderScope.ServeQuotedProduct(action)
                        null
                    }
                }
                ViewOrderScope.Action.ACCEPT -> {
                    if (fromNotification || it.checkedEntries.value.none { it.buyingOption == BuyingOption.QUOTE }) {
                        it.dismissNotification()
                        ConfirmOrderScope(
                            order = it.order,
                            acceptedEntries = it.checkedEntries.value,
                            rejectedEntries = it.entries.value - it.checkedEntries.value,
                        )
                    } else {
                        it.notifications.value = ViewOrderScope.ServeQuotedProduct(action)
                        null
                    }
                }
            }

            if (nextScope != null) {
                setScope(nextScope)
            }
        }
    }

    private fun selectEntry(orderEntry: OrderEntry) {
        navigator.withScope<ViewOrderScope> {
            navigator.scope.value.bottomSheet.value = BottomSheet.ModifyOrderEntry(
                orderEntry,
                isChecked = DataSource(orderEntry in it.checkedEntries.value),
                canEdit = it.canEdit,
            )
        }
    }

    private fun toggleCheckEntry(orderEntry: OrderEntry) {
        navigator.withScope<SelectableOrderEntry> {
            if (it.checkedEntries.value.contains(orderEntry)) {
                it.checkedEntries.value = it.checkedEntries.value.filter { it != orderEntry }
            } else {
                it.checkedEntries.value = it.checkedEntries.value + orderEntry
            }
            when (it) {
                is ViewOrderScope -> {
                    it.actions.value =
                        if (it.checkedEntries.value.isEmpty()) ViewOrderScope.Action.all else ViewOrderScope.Action.onlyAccept
                }
                is ConfirmOrderScope -> {
                    val action = when {
                        it.checkedEntries.value.isEmpty() -> ConfirmOrderScope.Action.CONFIRM
                        it.activeTab.value == ConfirmOrderScope.Tab.REJECTED -> ConfirmOrderScope.Action.ACCEPT
                        it.activeTab.value == ConfirmOrderScope.Tab.ACCEPTED -> ConfirmOrderScope.Action.REJECT
                        else -> null
                    }
                    if (action != null) {
                        it.actions.value = listOf(action)
                    }
                }
            }
        }
    }

    private suspend fun saveEntryQty(orderEntry: OrderEntry, qty: Int) {
        navigator.withScope<ViewOrderScope> {
            val (result, isSuccess) = withProgress {
                networkOrdersScope.saveNewOrderQty(
                    OrderNewQtyRequest(
                        orderId = it.order.value.info.id,
                        orderEntryId = orderEntry.id,
                        unitCode = userRepo.requireUser().unitCode,
                        servedQty = qty,
                    )
                )
            }

            if (result != null && isSuccess) {
                scope.value.dismissBottomSheet()
                it.order.value = it.order.value.copy(info = result.info)
                it.checkedEntries.value = emptyList()
                it.actions.value = ViewOrderScope.Action.onlyAccept
                it.entries.value = result.entries
            } else {
                setHostError(ErrorCode())
            }
        }
    }

    private suspend fun confirmOrder() {
        navigator.withScope<ConfirmOrderScope> {
            val (error, isSuccess) = withProgress {
                networkOrdersScope.confirmOrder(
                    ConfirmOrderRequest(
                        orderId = it.order.value.info.id,
                        sellerUnitCode = userRepo.requireUser().unitCode,
                        acceptedEntries = it.acceptedEntries.map { it.id },
                    )
                )
            }

            if (isSuccess) {
                dropScope(updateDataSource = false)
                dropScope()
            } else {
                setHostError(error ?: ErrorCode())
            }
        }
    }
}