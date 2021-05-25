package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OrdersScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ViewOrderScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.Order

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
    }

    private suspend fun loadOrders(isFirstLoad: Boolean) {
        loadHelper.load<OrdersScope, Order>(isFirstLoad = isFirstLoad) {
            val user = userRepo.requireUser()
            val (result, isSuccess) = networkOrdersScope.getOrders(
                type = type,
                unitCode = user.unitCode,
                search = searchText.value,
                from = null,
                to = null,
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

    private fun selectOrder(item: Order) {
        navigator.withScope<OrdersScope> {
            setScope(ViewOrderScope(DataSource(item)))
        }
    }
}