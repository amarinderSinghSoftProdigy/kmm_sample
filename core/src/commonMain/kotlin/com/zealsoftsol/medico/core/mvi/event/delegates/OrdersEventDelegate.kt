package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.mvi.scope.nested.ConfirmOrderScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OrderHsnEditScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OrderPlacedScope
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
import com.zealsoftsol.medico.data.DeclineReason
import com.zealsoftsol.medico.data.Order
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.OrderNewQtyRequest
import com.zealsoftsol.medico.data.OrderType
import com.zealsoftsol.medico.data.TaxType

internal class OrdersEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkOrdersScope: NetworkScope.Orders,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.Orders>(navigator), CommonScope.CanGoBack {

    override suspend fun handleEvent(event: Event.Action.Orders) = when (event) {
        is Event.Action.Orders.Load -> loadOrders(event.isFirstLoad)
        is Event.Action.Orders.Search -> searchOrders(event.value)
        is Event.Action.Orders.Select -> selectOrder(event.orderId, event.type)
        is Event.Action.Orders.ViewOrderAction -> viewOrderAction(
            event.action,
            event.fromNotification,
        )
        is Event.Action.Orders.SelectEntry -> selectEntry(
            event.taxType,
            event.retailerName,
            event.canEditOrderEntry,
            event.orderId,
            event.declineReason,
            event.entry,
            event.index
        )
        is Event.Action.Orders.ToggleCheckEntry -> toggleCheckEntry(event.entry)
        is Event.Action.Orders.SaveEntryQty -> saveEntryQty(
            event.entry,
            event.quantity,
            event.freeQuantity,
            event.ptr,
            event.batch,
            event.expiry
        )
        is Event.Action.Orders.Confirm -> confirmOrder(event.fromNotification)
    }

    private suspend fun loadOrders(isFirstLoad: Boolean) {
        loadHelper.load<OrdersScope, Order>(isFirstLoad = isFirstLoad) {
            val user = userRepo.requireUser()
            networkOrdersScope.getOrders(
                type = activeTab.value.orderType,
                unitCode = user.unitCode,
                search = searchText.value,
                from = dateRange.value?.fromMs,
                to = dateRange.value?.toMs,
                pagination = pagination,
            ).getBodyOrNull()
        }
    }

    private suspend fun searchOrders(search: String) {
        loadHelper.search<OrdersScope, Order>(searchValue = search) {
            val user = userRepo.requireUser()
            networkOrdersScope.getOrders(
                type = activeTab.value.orderType,
                unitCode = user.unitCode,
                search = searchText.value,
                from = dateRange.value?.fromMs,
                to = dateRange.value?.toMs,
                pagination = pagination,
            ).getBodyOrNull()
        }
    }

    private suspend fun selectOrder(orderId: String, type: OrderType) {
        navigator.withScope<Scopable> {
            withProgress {
                networkOrdersScope.getOrder(type, userRepo.requireUser().unitCode, orderId)
            }.onSuccess { body ->
                setScope(
                    ViewOrderScope(
                        canEdit = type == OrderType.PURCHASE_ORDER,
                        DataSource(body.order),
                        DataSource(body.unitData.data),
                        DataSource(body.entries),
                        DataSource(body.declineReasons)
                    )
                )
            }.onError(navigator)
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

    private fun selectEntry(
        taxType: TaxType,
        retailerName: String,
        canEditOrderEntry: Boolean,
        orderId: String,
        declineReason: List<DeclineReason>,
        orderEntry: List<OrderEntry>,
        index: Int
    ) {
        navigator.withScope<ViewOrderScope> {
            navigator.setScope(
                OrderHsnEditScope(
                    taxType = taxType,
                    retailerName = retailerName,
                    canEditOrderEntry = canEditOrderEntry,
                    orderID = orderId,
                    declineReason = declineReason,
                    orderEntries = orderEntry as MutableList<OrderEntry>,
                    index = index
                )
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
                    it.calculateActions()
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

    private suspend fun saveEntryQty(
        orderEntry: OrderEntry,
        qty: Double,
        freeQty: Double,
        ptr: Double,
        batch: String,
        expiry: String,
    ) {
        navigator.withScope<ViewOrderScope> {
            withProgress {
                networkOrdersScope.saveNewOrderQty(
                    OrderNewQtyRequest(
                        orderId = it.order.value.info.id,
                        orderEntryId = orderEntry.id,
                        unitCode = userRepo.requireUser().unitCode,
                        servedQty = qty,
                        freeQty, ptr, batch, expiry,
                    )
                )
            }.onSuccess { body ->
                scope.value.dismissBottomSheet()
                it.order.value = body.order
//                it.checkedEntries.value = emptyList()
                it.entries.value = body.entries
                it.calculateActions()
            }.onError(navigator)
        }
    }

    private suspend fun confirmOrder(fromNotification: Boolean) {
        navigator.withScope<ConfirmOrderScope> {
            if (!fromNotification) {
                it.notifications.value = ConfirmOrderScope.AreYouSure
            } else {
                it.notifications.value = null
                withProgress {
                    networkOrdersScope.confirmOrder(
                        ConfirmOrderRequest(
                            orderId = it.order.value.info.id,
                            sellerUnitCode = userRepo.requireUser().unitCode,
                            acceptedEntries = it.acceptedEntries.map { it.id },
                        )
                    )
                }.onSuccess { _ ->
                    dropScope(updateDataSource = false)
                    dropScope(updateDataSource = false)
                    setScope(OrderPlacedScope(it.order.value))
                }.onError(navigator)
            }
        }
    }
}