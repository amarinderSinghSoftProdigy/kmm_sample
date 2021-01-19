package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.ProductSearch

class SearchScope(
    val productSearch: DataSource<String> = DataSource(""),
    val manufacturerSearch: DataSource<String> = DataSource(""),
    val isFilterOpened: DataSource<Boolean> = DataSource(false),
    val filters: DataSource<List<Filter>> = DataSource(emptyList()),
    val products: DataSource<List<ProductSearch>> = DataSource(emptyList()),
    internal var currentProductPage: Int = 0,
    internal var totalProducts: Int = 0,
    private var clickedProductIndex: Int = 0,
) : Scope.Host.Regular(), CommonScope.CanGoBack {

    init {
        EventCollector.sendEvent(Event.Action.Search.SearchProduct(""))
    }

    fun canLoadMore() = (currentProductPage + 1) * DEFAULT_ITEMS_PER_PAGE < totalProducts

    fun getVisibleProductIndex() = clickedProductIndex

    fun toggleFilter() {
        isFilterOpened.value = !isFilterOpened.value
    }

    fun selectFilter(filter: Filter, option: Option<String>) =
        EventCollector.sendEvent(Event.Action.Search.SelectFilter(filter, option))

    fun clearFilter(filter: Filter?) =
        EventCollector.sendEvent(Event.Action.Search.ClearFilter(filter))

    fun searchManufacturer(input: String): Boolean {
        return if (input.isNotBlank() || manufacturerSearch.value.isNotBlank()) {
            EventCollector.sendEvent(Event.Action.Search.SearchManufacturer(input))
        } else false
    }

    fun searchProduct(input: String): Boolean {
        return if (input.isNotBlank() || productSearch.value.isNotBlank()) {
            EventCollector.sendEvent(Event.Action.Search.SearchProduct(input))
        } else false
    }

    fun selectProduct(product: ProductSearch, index: Int) {
        clickedProductIndex = index
        EventCollector.sendEvent(Event.Action.Product.Select(product.productCode))
    }

    fun loadMoreProducts() =
        EventCollector.sendEvent(Event.Action.Search.LoadMoreProducts)

    companion object {
        internal const val DEFAULT_ITEMS_PER_PAGE = 20
    }
}