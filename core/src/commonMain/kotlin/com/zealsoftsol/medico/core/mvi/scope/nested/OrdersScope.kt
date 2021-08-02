package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.B2BData
import com.zealsoftsol.medico.data.DateRange
import com.zealsoftsol.medico.data.Order
import com.zealsoftsol.medico.data.OrderEntry
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
        EventCollector.sendEvent(Event.Action.Orders.Select(item.info.id, type))

    fun setFrom(fromMs: Long) {
        this.dateRange.value = dateRange.value?.copy(fromMs = fromMs) ?: DateRange(fromMs = fromMs)
        EventCollector.sendEvent(Event.Action.Orders.Load(isFirstLoad = true))
    }

    fun setTo(toMs: Long) {
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

class ViewOrderScope(
    override val canEdit: Boolean,
    override val order: DataSource<Order>,
    val b2bData: DataSource<B2BData>,
    val entries: DataSource<List<OrderEntry>>,
) : Scope.Child.TabBar(), SelectableOrderEntry, CommonScope.WithNotifications {

    override val checkedEntries = DataSource(listOf<OrderEntry>())
    override val notifications: DataSource<ScopeNotification?> = DataSource(null)
    val actions = DataSource(listOf(Action.REJECT_ALL, Action.ACCEPT_ALL))

    fun selectEntry(entry: OrderEntry) =
        EventCollector.sendEvent(Event.Action.Orders.SelectEntry(entry))

    fun acceptAction(action: Action) =
        EventCollector.sendEvent(Event.Action.Orders.ViewOrderAction(action, false))

    internal fun calculateActions() {
        actions.value = if (checkedEntries.value.isEmpty()) Action.all else Action.onlyAccept
    }

    enum class Action(
        val stringId: String,
        val weight: Float,
        val bgColorHex: String,
        val textColorHex: String = "#003657"
    ) {
        REJECT_ALL("reject_all", 0.5f, "#ed5152", "#FFFFFF"),
        ACCEPT_ALL("accept_all", 0.5f, "#FFD600"),
        ACCEPT("accept", 1f, "#FFD600");

        internal companion object {
            val all = listOf(REJECT_ALL, ACCEPT_ALL)
            val onlyAccept = listOf(ACCEPT)
        }
    }

    data class ServeQuotedProduct(val continueAction: Action) : ScopeNotification {
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = false
        override val title: String? = null
        override val body: String = "serve_quoted"

        fun `continue`() =
            EventCollector.sendEvent(Event.Action.Orders.ViewOrderAction(continueAction, true))
    }

    data class RejectAll(val continueAction: Action) : ScopeNotification {
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = false
        override val title: String? = null
        override val body: String = "sure_reject_all"

        fun `continue`() =
            EventCollector.sendEvent(Event.Action.Orders.ViewOrderAction(continueAction, true))
    }
}

class ConfirmOrderScope(
    override val order: DataSource<Order>,
    internal var acceptedEntries: List<OrderEntry>,
    internal var rejectedEntries: List<OrderEntry>,
    override val notifications: DataSource<ScopeNotification?> = DataSource(null),
) : Scope.Child.TabBar(), SelectableOrderEntry, CommonScope.WithNotifications {

    val actions = DataSource(listOf(Action.CONFIRM))
    val entries = DataSource(acceptedEntries)
    override val checkedEntries = DataSource(emptyList<OrderEntry>())
    val tabs = listOf(Tab.ACCEPTED, Tab.REJECTED)
    val activeTab = DataSource(Tab.ACCEPTED)
    override val canEdit: Boolean = true

    fun acceptAction(action: Action) {
        when (action) {
            Action.REJECT -> {
                rejectedEntries = rejectedEntries + checkedEntries.value
                acceptedEntries = acceptedEntries - checkedEntries.value
                refreshEntries()
            }
            Action.ACCEPT -> {
                acceptedEntries = acceptedEntries + checkedEntries.value
                rejectedEntries = rejectedEntries - checkedEntries.value
                refreshEntries()
            }
            Action.CONFIRM -> EventCollector.sendEvent(Event.Action.Orders.Confirm(fromNotification = false))
        }
    }

    fun selectTab(tab: Tab) {
        activeTab.value = tab
        refreshEntries()
    }

    private fun refreshEntries() {
        checkedEntries.value = emptyList()
        entries.value = when (activeTab.value) {
            Tab.ACCEPTED -> acceptedEntries
            Tab.REJECTED -> rejectedEntries
        }
        actions.value = listOf(Action.CONFIRM)
    }

    enum class Action(
        val stringId: String,
        val weight: Float,
        val bgColorHex: String,
        val textColorHex: String = "#003657"
    ) {
        REJECT("reject_selected", 1f, "#ed5152", "#FFFFFF"),
        ACCEPT("accept_selected", 1f, "#0084D4", "#FFFFFF"),
        CONFIRM("confirm", 1f, "#FFD600");
    }

    enum class Tab(val stringId: String, val bgColorHex: String) {
        REJECTED("rejected", "#ED5152"),
        ACCEPTED("accepted", "#0084D4");
    }

    object AreYouSure : ScopeNotification {
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = false
        override val title: String? = null
        override val body: String? = "sure_confirm_order"

        fun confirm() =
            EventCollector.sendEvent(Event.Action.Orders.Confirm(fromNotification = true))
    }
}

class OrderPlacedScope(val order: Order) : Scope.Child.TabBar() {

    override fun goHome() = EventCollector.sendEvent(Event.Transition.Back)
}

interface SelectableOrderEntry : Scopable {
    val order: DataSource<Order>
    val checkedEntries: DataSource<List<OrderEntry>>
    val canEdit: Boolean

    fun toggleCheck(entry: OrderEntry) =
        EventCollector.sendEvent(Event.Action.Orders.ToggleCheckEntry(entry))
}