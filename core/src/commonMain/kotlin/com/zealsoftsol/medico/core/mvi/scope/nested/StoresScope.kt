package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SortOption
import com.zealsoftsol.medico.data.Store

// TODO make part of management scope
sealed class StoresScope : Scope.Child.TabBar() {

    class All(
        private val notificationCount: ReadOnlyDataSource<Int>,
        private val cartItemsCount: ReadOnlyDataSource<Int>,
    ) : StoresScope(), Loadable<Store> {

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.NoIconTitle(
                title = "",
                notificationItemsCount = notificationCount,
                cartItemsCount = cartItemsCount
            )
        }

        override val isRoot: Boolean = true

        override val pagination: Pagination = Pagination()
        override val items: DataSource<List<Store>> = DataSource(emptyList())
        override val totalItems: DataSource<Int> = DataSource(0)
        override val searchText: DataSource<String> = DataSource("")

        init {
            EventCollector.sendEvent(Event.Action.Stores.Load(isFirstLoad = true))
        }

        fun selectItem(item: Store) =
            EventCollector.sendEvent(Event.Action.Stores.Select(item))

        fun search(value: String) = EventCollector.sendEvent(Event.Action.Stores.Search(value))

        fun loadItems() =
            EventCollector.sendEvent(Event.Action.Stores.Load(isFirstLoad = false))
    }

    class StorePreview(
        val store: Store,
        private val cartItemsCount: ReadOnlyDataSource<Int>,
        private val notificationCount: ReadOnlyDataSource<Int>,
        override val productSearch: DataSource<String> = DataSource(""),
        override val isFilterOpened: DataSource<Boolean> = DataSource(false),
        override val checkedProduct: DataSource<ProductSearch?> = DataSource(null),
        override val isBatchSelected: DataSource<Boolean> = DataSource(false),
        override val filters: DataSource<List<Filter>> = DataSource(emptyList()),
        override val filterSearches: DataSource<Map<String, String>> = DataSource(emptyMap()),
        override val products: DataSource<List<ProductSearch>> = DataSource(emptyList()),
        override val sortOptions: DataSource<List<SortOption>> = DataSource(emptyList()),
        override val selectedSortOption: DataSource<SortOption?> = DataSource(null),
        override val activeFilterIds: DataSource<List<String>> = DataSource(emptyList()),
    ) : StoresScope(), BaseSearchScope {

        override val autoComplete: DataSource<List<AutoComplete>> = DataSource(emptyList())
        override val pagination: Pagination = Pagination()
        override val unitCode: String = store.sellerUnitCode
        override val supportsAutoComplete: Boolean = false

        init {
            startSearch()
        }

        fun startSearch(){
            EventCollector.sendEvent(Event.Action.Search.SearchInput(isOneOf = true))
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.StoreTitle(
                store = store,
                notificationItemsCount = notificationCount,
                cartItemsCount = cartItemsCount
            )
        }
    }
}