package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.ProductSearch

class SearchScope(
    val productSearch: DataSource<String> = DataSource(""),
    val isFilterOpened: DataSource<Boolean> = DataSource(false),
    val filters: DataSource<List<Filter>> = DataSource(emptyList()),
    val filterSearches: DataSource<Map<String, String>> = DataSource(emptyMap()),
    val autoComplete: DataSource<List<AutoComplete>> = DataSource(emptyList()),
    val products: DataSource<List<ProductSearch>> = DataSource(emptyList()),
) : Scope.Host.Regular(),
    CommonScope.CanGoBack {
    val pagination: Pagination = Pagination()

    init {
        EventCollector.sendEvent(Event.Action.Search.SearchInput())
    }

    fun toggleFilter() {
        isFilterOpened.value = !isFilterOpened.value
    }

    fun selectAutoComplete(autoComplete: AutoComplete) =
        EventCollector.sendEvent(Event.Action.Search.SelectAutoComplete(autoComplete))

    fun selectFilter(filter: Filter, option: Option) =
        EventCollector.sendEvent(Event.Action.Search.SelectFilter(filter, option))

    fun clearFilter(filter: Filter?) =
        EventCollector.sendEvent(Event.Action.Search.ClearFilter(filter))

    fun searchFilter(filter: Filter, input: String): Boolean {
        return trimInput(input, filterSearches.value[filter.queryId].orEmpty()) {
            EventCollector.sendEvent(Event.Action.Search.SearchFilter(filter, it))
        }
    }

    fun searchProduct(input: String): Boolean {
        return trimInput(input, productSearch.value) {
            EventCollector.sendEvent(Event.Action.Search.SearchAutoComplete(it))
        }
    }

    fun selectProduct(product: ProductSearch) =
        EventCollector.sendEvent(Event.Action.Product.Select(product.code))

    fun loadMoreProducts() =
        EventCollector.sendEvent(Event.Action.Search.LoadMoreProducts)
}