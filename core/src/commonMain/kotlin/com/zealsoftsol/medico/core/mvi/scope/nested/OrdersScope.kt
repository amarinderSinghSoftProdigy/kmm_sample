package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.DateRange
import com.zealsoftsol.medico.data.Order
import com.zealsoftsol.medico.data.OrderType

class OrdersScope(val type: OrderType) : Scope.Child.TabBar(), Loadable<Order> {

    override val isRoot: Boolean = true

    override val items: DataSource<List<Order>> = DataSource(emptyList())
    override val totalItems: DataSource<Int> = DataSource(0)
    override val searchText: DataSource<String> = DataSource("")
    override val pagination: Pagination = Pagination()

    val isFilterOpened: DataSource<Boolean> = DataSource(false)
    val dateRange: DataSource<DateRange?> = DataSource(null)

    init {
        EventCollector.sendEvent(Event.Action.Orders.Load(isFirstLoad = true))
    }

    fun loadItems() =
        EventCollector.sendEvent(Event.Action.Orders.Load(isFirstLoad = false))

    fun search(value: String): Boolean {
        return if (searchText.value != value) {
            EventCollector.sendEvent(Event.Action.Orders.Search(value))
        } else {
            false
        }
    }

    fun selectItem(item: Order) =
        EventCollector.sendEvent(Event.Action.Orders.Select(item))

    fun setFrom(fromMs: Long) {
        if (type == OrderType.RECEIVED) throw UnsupportedOperationException("can not set range for received orders")

        this.dateRange.value = dateRange.value?.copy(fromMs = fromMs) ?: DateRange(fromMs = fromMs)
        EventCollector.sendEvent(Event.Action.Orders.Load(isFirstLoad = true))
    }

    fun setTo(toMs: Long) {
        if (type == OrderType.RECEIVED) throw UnsupportedOperationException("can not set range for received orders")

        this.dateRange.value = dateRange.value?.copy(toMs = toMs) ?: DateRange(toMs = toMs)
        EventCollector.sendEvent(Event.Action.Orders.Load(isFirstLoad = true))
    }

    fun clearFilters() {
        dateRange.value = null
        EventCollector.sendEvent(Event.Action.Orders.Load(isFirstLoad = true))
    }

    fun toggleFilter() {
        isFilterOpened.value = !isFilterOpened.value
    }
}

class ViewOrderScope(val order: DataSource<Order>) : Scope.Child.TabBar() {

}