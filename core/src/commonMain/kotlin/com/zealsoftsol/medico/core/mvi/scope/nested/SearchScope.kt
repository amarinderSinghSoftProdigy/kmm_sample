package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.ProductSearch

interface BaseSearchScope : Scopable {
    val productSearch: DataSource<String>
    val isFilterOpened: DataSource<Boolean>
    val filters: DataSource<List<Filter>>
    val filterSearches: DataSource<Map<String, String>>
    val autoComplete: DataSource<List<AutoComplete>>
    val products: DataSource<List<ProductSearch>>

    // store search if present
    val unitCode: String?

    // searches without loading if false
    val supportsAutoComplete: Boolean
    val pagination: Pagination

    fun reset() = EventCollector.sendEvent(Event.Action.Search.Reset)

    fun toggleFilter() = EventCollector.sendEvent(Event.Action.Search.ToggleFilter)

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

    fun searchProduct(input: String, withAutoComplete: Boolean): Boolean {
        return trimInput(input, productSearch.value) {
            val event = if (withAutoComplete) {
                Event.Action.Search.SearchAutoComplete(it)
            } else {
                Event.Action.Search.SearchInput(isOneOf = false, search = input)
            }
            EventCollector.sendEvent(event)
        }
    }

    fun selectProduct(product: ProductSearch) =
        EventCollector.sendEvent(Event.Action.Product.Select(product.code))

    fun loadMoreProducts() =
        EventCollector.sendEvent(Event.Action.Search.LoadMoreProducts)
}

class SearchScope(
    override val productSearch: DataSource<String> = DataSource(""),
    override val isFilterOpened: DataSource<Boolean> = DataSource(false),
    override val filters: DataSource<List<Filter>> = DataSource(emptyList()),
    override val filterSearches: DataSource<Map<String, String>> = DataSource(emptyMap()),
    override val autoComplete: DataSource<List<AutoComplete>> = DataSource(emptyList()),
    override val products: DataSource<List<ProductSearch>> = DataSource(emptyList()),
) : Scope.Child.TabBar(), CommonScope.CanGoBack, BaseSearchScope {

    override val unitCode: String? = null
    override val supportsAutoComplete: Boolean = true
    override val pagination: Pagination = Pagination()

    init {
        EventCollector.sendEvent(Event.Action.Search.SearchInput(isOneOf = true))
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo): TabBarInfo {
        return TabBarInfo.ActiveSearch(productSearch)
    }

    override fun goBack(): Boolean {
        reset()
        return super.goBack()
    }
}