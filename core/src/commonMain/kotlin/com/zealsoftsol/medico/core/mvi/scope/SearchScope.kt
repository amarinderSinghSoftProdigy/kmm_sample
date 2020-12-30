package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.Product

data class SearchScope(
    val productSearch: DataSource<String> = DataSource(""),
    val manufacturerSearch: DataSource<String> = DataSource(""),
    val isFilterOpened: DataSource<Boolean> = DataSource(false),
    val filters: DataSource<List<Filter>> = DataSource(emptyList()),
    val products: DataSource<List<Product>> = DataSource(emptyList()),
    internal var currentProductPage: Int = 0,
    internal var totalProducts: Int = 0,
) : BaseScope(), CanGoBack {

    init {
        searchProduct("")
    }

    fun canLoadMore() = (currentProductPage + 1) * DEFAULT_ITEMS_PER_PAGE < totalProducts

    fun toggleFilter() {
        isFilterOpened.value = !isFilterOpened.value
    }

    fun selectFilter(filter: Filter, option: Option<String>) =
        EventCollector.sendEvent(Event.Action.Search.SelectFilter(filter, option))

    fun clearFilter(filter: Filter?) =
        EventCollector.sendEvent(Event.Action.Search.ClearFilter(filter))

    fun searchManufacturer(input: String) =
        EventCollector.sendEvent(Event.Action.Search.SearchManufacturer(input))

    fun searchProduct(input: String) =
        EventCollector.sendEvent(Event.Action.Search.SearchProduct(input))

    fun selectProduct(product: Product) =
        EventCollector.sendEvent(Event.Action.Search.SelectProduct(product))

    fun loadMoreProducts() =
        EventCollector.sendEvent(Event.Action.Search.LoadMoreProducts)

    companion object {
        internal const val DEFAULT_ITEMS_PER_PAGE = 20
    }
}