package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SortOption

interface BaseSearchScope : Scopable {
    val enableButton: DataSource<Boolean>
    val productSearch: DataSource<String>
    val isFilterOpened: DataSource<Boolean>
    val isBatchSelected: DataSource<Boolean>
    val showToast: DataSource<Boolean>
    val cartData: DataSource<CartData?>
    val checkedProduct: DataSource<ProductSearch?>
    val filtersManufactures: DataSource<List<Filter>>
    val filters: DataSource<List<Filter>>
    val filterSearches: DataSource<Map<String, String>>
    val autoComplete: DataSource<List<AutoComplete>>
    val products: DataSource<List<ProductSearch>>
    val sortOptions: DataSource<List<SortOption>>
    val selectedSortOption: DataSource<SortOption?>
    val activeFilterIds: DataSource<List<String>>
    val freeQty: DataSource<Double>
    val productId: DataSource<String>
    val totalResults: DataSource<Int>

    // store search if present
    val unitCode: String?

    // searches without loading if false
    val supportsAutoComplete: Boolean
    val pagination: Pagination

    fun selectBatch(check: Boolean, product: ProductSearch) =
        EventCollector.sendEvent(Event.Action.Search.SelectBatch(check, product))

    fun reset() = EventCollector.sendEvent(Event.Action.Search.Reset)

    fun resetButton(check: Boolean) =
        EventCollector.sendEvent(Event.Action.Search.ResetButton(check))

    fun updateFree(qty: Double, id: String) =
        EventCollector.sendEvent(Event.Action.Search.UpdateFree(qty, id))

    fun toggleFilter() = EventCollector.sendEvent(Event.Action.Search.ToggleFilter)

    fun selectAutoComplete(autoComplete: AutoComplete) =
        EventCollector.sendEvent(Event.Action.Search.SelectAutoComplete(autoComplete))

    fun selectFilter(filter: Filter, option: Option) =
        EventCollector.sendEvent(Event.Action.Search.SelectFilter(filter, option))

    fun clearFilter(filter: Filter?) =
        EventCollector.sendEvent(Event.Action.Search.ClearFilter(filter))

    fun getFilterNameById(id: String) = filters.value.first { it.queryId == id }

    fun selectSortOption(option: SortOption?) =
        EventCollector.sendEvent(Event.Action.Search.SelectSortOption(option))

    fun searchFilter(filter: Filter, input: String): Boolean {
        return trimInput(input, filterSearches.value[filter.queryId].orEmpty()) {
            EventCollector.sendEvent(Event.Action.Search.SearchFilter(filter, it))
        }
    }

    fun searchProduct(input: String, withAutoComplete: Boolean, sellerUnitCode: String): Boolean {
        return trimInput(input, productSearch.value) {
            val event = if (withAutoComplete) {
                Event.Action.Search.SearchAutoComplete(it, sellerUnitCode)
            } else {
                Event.Action.Search.SearchInput(isOneOf = false, search = input)
            }
            EventCollector.sendEvent(event)
        }
    }

    fun selectProduct(product: ProductSearch) =
        EventCollector.sendEvent(Event.Action.Product.SelectFromSearch(product.code))

    fun loadMoreProducts() =
        EventCollector.sendEvent(Event.Action.Search.LoadMoreProducts)

    fun addToCart(product: ProductSearch) =
        EventCollector.sendEvent(Event.Action.Search.AddToCart(product))

    fun buy(product: ProductSearch) = product.buyingOption?.let {
        EventCollector.sendEvent(
            Event.Action.Product.BuyProduct(
                product,
                it,
            )
        )
    } ?: false
}

class SearchScope(
    autoCompleteDashboard: AutoComplete?,
    override val enableButton: DataSource<Boolean> = DataSource(false),
    override val productSearch: DataSource<String> = DataSource(""),
    override val isFilterOpened: DataSource<Boolean> = DataSource(false),
    override val isBatchSelected: DataSource<Boolean> = DataSource(false),
    override val showToast: DataSource<Boolean> = DataSource(false),
    override val checkedProduct: DataSource<ProductSearch?> = DataSource(null),
    override val cartData: DataSource<CartData?> = DataSource(null),
    override val filtersManufactures: DataSource<List<Filter>> = DataSource(emptyList()),
    override val filters: DataSource<List<Filter>> = DataSource(emptyList()),
    override val filterSearches: DataSource<Map<String, String>> = DataSource(emptyMap()),
    override val autoComplete: DataSource<List<AutoComplete>> = DataSource(emptyList()),
    override val products: DataSource<List<ProductSearch>> = DataSource(emptyList()),
    override val sortOptions: DataSource<List<SortOption>> = DataSource(emptyList()),
    override val selectedSortOption: DataSource<SortOption?> = DataSource(null),
    override val activeFilterIds: DataSource<List<String>> = DataSource(emptyList()),
    override val freeQty: DataSource<Double> = DataSource(0.0),
    override val productId: DataSource<String> = DataSource(""),
    override val totalResults: DataSource<Int> = DataSource(0)
) : Scope.Child.TabBar(), BaseSearchScope {

    override val unitCode: String? = null
    override val supportsAutoComplete: Boolean = true
    override val pagination: Pagination = Pagination(Pagination.ITEMS_PER_PAGE_30)
    val showNoStockistAlert = DataSource(false)

    init {
        //if there is already an autocomplete item start search based on brand manufacturer else perform normal search
        if (autoCompleteDashboard != null) {
            if (autoCompleteDashboard.query == "suggest") {
                EventCollector.sendEvent(
                    Event.Action.Search.SelectAutoCompleteGlobal(autoCompleteDashboard)
                )
            } else {
                EventCollector.sendEvent(
                    Event.Action.Search.SelectAutoComplete(
                        autoCompleteDashboard
                    )
                )
            }
        } else {
            EventCollector.sendEvent(Event.Action.Search.GetLocalSearchData)
        }
    }

    fun startSearch(check: Boolean) {
        EventCollector.sendEvent(
            Event.Action.Search.SearchInput(
                isOneOf = check,
                search = null
            )
        )
    }

    fun manageAlertVisibility(value: Boolean) {
        showNoStockistAlert.value = value
    }

    fun showConnectedStockist(code: String, imageCode: String) {
        EventCollector.sendEvent(Event.Action.Search.LoadStockist(code, imageCode))
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
        return TabBarInfo.ActiveSearch(productSearch, activeFilterIds)
    }

}