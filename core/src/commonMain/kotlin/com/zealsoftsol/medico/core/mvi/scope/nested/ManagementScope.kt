package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.ManagementItem

sealed class ManagementScope<T : ManagementItem>(
    val tabs: List<Tab>,
    internal val getLoadAction: (Tab?) -> Event.Action.Management,
) : Scope.Child.TabBar(TabBarInfo.Search(ScopeIcon.HAMBURGER)) {

    val pagination: Pagination = Pagination()
    val items: DataSource<List<T>> = DataSource(emptyList())
    val activeTab: DataSource<Tab?> = DataSource(tabs.firstOrNull())
    val searchText: DataSource<String> = DataSource("")

    init {
        loadItems()
    }

    fun selectTab(tab: Tab) {
        pagination.reset()
        items.value = emptyList()
        activeTab.value = tab
        loadItems()
    }

    fun selectItem(item: T) = EventCollector.sendEvent(Event.Action.Management.Select(item))

    fun search(value: String?) = EventCollector.sendEvent(Event.Action.Management.Filter(value))

    fun loadItems() = EventCollector.sendEvent(getLoadAction(activeTab.value))

    class Stockist : ManagementScope<EntityInfo>(
        tabs = listOf(Tab.YOUR_STOCKISTS, Tab.ALL_STOCKISTS),
        getLoadAction = {
            when (it) {
                Tab.YOUR_STOCKISTS -> Event.Action.Management.LoadSubscribedStockists
                Tab.ALL_STOCKISTS -> Event.Action.Management.LoadAllStockists
                else -> throw UnsupportedOperationException("unsupported tab")
            }
        }
    )

    enum class Tab(val stringId: String) {
        YOUR_RETAILERS("your_retailers"),
        YOUR_SEASON_BOYS("your_season_boys"),
        YOUR_STOCKISTS("your_stockists"),
        ALL_STOCKISTS("all_stockists"),
        YOUR_HOSPITALS("your_hospitals");
    }
}