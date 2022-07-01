package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.core.utils.Loadable
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SortOption
import com.zealsoftsol.medico.data.StockistListItem
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.Value

// TODO make part of management scope
sealed class StoresScope : Scope.Child.TabBar() {

    val showNoAlternateProdToast = DataSource(false)

    fun hideAlternateProdToastWarning(){
        showNoAlternateProdToast.value = false
    }

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

        override val isRoot: Boolean = false

        override val pagination: Pagination = Pagination()
        override val items: DataSource<List<Store>> = DataSource(emptyList())
        override val totalItems: DataSource<Int> = DataSource(0)
        override val searchText: DataSource<String> = DataSource("")

        init {
            EventCollector.sendEvent(Event.Action.Stores.Load(isFirstLoad = true))
        }

        fun selectItem(item: Store) {
            EventCollector.sendEvent(Event.Action.Stores.Select(item))
        }

        fun search(value: String) = EventCollector.sendEvent(Event.Action.Stores.Search(value))

        fun loadItems() =
            EventCollector.sendEvent(Event.Action.Stores.Load(isFirstLoad = false))
    }

    class StorePreview(
        var store: DataSource<Store>,
        private val cartItemsCount: ReadOnlyDataSource<Int>,
        private val notificationCount: ReadOnlyDataSource<Int>,
        override val productSearch: DataSource<String> = DataSource(""),
        override val isFilterOpened: DataSource<Boolean> = DataSource(false),
        override val showToast: DataSource<Boolean> = DataSource(false),
        override val cartData: DataSource<CartData?> = DataSource(null),
        override val checkedProduct: DataSource<ProductSearch?> = DataSource(null),
        override val isBatchSelected: DataSource<Boolean> = DataSource(false),
        override val filters: DataSource<List<Filter>> = DataSource(emptyList()),
        override val filtersManufactures: DataSource<List<Value>> = DataSource(emptyList()),
        override val filterSearches: DataSource<Map<String, String>> = DataSource(emptyMap()),
        override val products: DataSource<List<ProductSearch>> = DataSource(emptyList()),
        override val sortOptions: DataSource<List<SortOption>> = DataSource(emptyList()),
        override val selectedSortOption: DataSource<SortOption?> = DataSource(null),
        override val activeFilterIds: DataSource<List<String>> = DataSource(emptyList()),
        override val enableButton: DataSource<Boolean> = DataSource(false),
        override val freeQty: DataSource<Double> = DataSource(0.0),
        override val productId: DataSource<String> = DataSource(""),
        override val selectedStockist: DataSource<String> = DataSource(""),
        override val selectedTradename: DataSource<String> = DataSource(""),
        override val totalResults: DataSource<Int> = DataSource(0),
        override var showNoProducts: DataSource<Boolean> = DataSource(false),
        override val connectedStockist: DataSource<List<StockistListItem>> = DataSource(emptyList())
    ) : StoresScope(), BaseSearchScope, ToastScope {

        override val autoComplete: DataSource<List<AutoComplete>> = DataSource(emptyList())
        override val pagination: Pagination = Pagination(Pagination.ITEMS_PER_PAGE_10)
        override var unitCode: String? = store.value.sellerUnitCode
        override val supportsAutoComplete: Boolean = true
        val selectedFilters = DataSource(emptyList<Value>())

        init {
            startSearch(true)
        }

        fun selectItem(item: String) {
            val url = CdnUrlProvider.urlFor(
                item, CdnUrlProvider.Size.Px320
            )
            EventCollector.sendEvent(Event.Action.Stores.ShowLargeImage(url))
        }

        fun startSearch(check: Boolean, search: String? = null) {
            productSearch.value = ""
            EventCollector.sendEvent(
                Event.Action.Search.SearchInput(
                    isOneOf = check,
                    search = search
                )
            )
        }

        override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
            return TabBarInfo.StoresSearch(
                search = productSearch,
                activeFilterIds = activeFilterIds,
                pagination = pagination,
                productSearch = productSearch,
                sellerUnitCode = selectedStockist,
                cartItemsCount = cartItemsCount
            )
        }

        fun updateView(item: StockistListItem) {
            store.value = Store(
                sellerUnitCode = item.unitCode,
                tradeName = item.tradeName,
                distance = item.distance.value,
                formattedDistance = item.distance.formatted,
            )
            selectedStockist.value = item.unitCode
            unitCode = item.unitCode
            startSearch(true, "")
        }

        fun openManufacturersFilter() =
            EventCollector.sendEvent(Event.Action.Stores.ShowManufacturers(filtersManufactures.value))

        fun showAlternateProducts(code: String) =
            EventCollector.sendEvent(Event.Action.Stores.ShowAltProds(code, null))
    }
}