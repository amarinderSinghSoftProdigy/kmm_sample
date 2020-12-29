package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.SearchScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.Facet
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.Option

internal class SearchEventDelegate(
    navigator: Navigator,
    private val networkSearchScope: NetworkScope.Search,
) : EventDelegate<Event.Action.Search>(navigator) {

    private val activeFilters = hashMapOf<String, Option<String>>()

    override suspend fun handleEvent(event: Event.Action.Search) = when (event) {
        is Event.Action.Search.SearchProduct -> searchProduct(event.value)
        is Event.Action.Search.SearchManufacturer -> searchManufacturer(event.value)
        is Event.Action.Search.SelectFilter -> selectFilter(event.filter, event.option)
        is Event.Action.Search.ClearFilter -> clearFilter(event.filter)
    }

    private suspend fun searchProduct(value: String) {
        navigator.withScope<SearchScope.Query> {
            it.productSearch.value = value
            it.search()
        }
    }

    private suspend fun searchManufacturer(value: String) {
        navigator.withScope<SearchScope.Query> {
            it.manufacturerSearch.value = value
            it.search()
        }
    }

    private suspend fun selectFilter(filter: Filter, option: Option<String>) {
        navigator.withScope<SearchScope.Query> {
            it.filters.value = it.filters.value.map { f ->
                if (filter.name == f.name) {
                    f.copy(options = f.options.map { op ->
                        when {
                            option.value == op.value -> {
                                val newOption = op.copy(isSelected = !op.isSelected)
                                if (newOption.isSelected) {
                                    activeFilters[f.queryName] = newOption
                                } else {
                                    activeFilters.remove(f)
                                }
                                newOption
                            }
                            op.isSelected -> op.copy(isSelected = false)
                            else -> op
                        }
                    })
                } else {
                    f
                }
            }
            it.search()
        }
    }

    private suspend fun clearFilter(filter: Filter?) {
        navigator.withScope<SearchScope.Query> {
            it.filters.value = it.filters.value.map { f ->
                when {
                    filter == null || filter.name == f.name -> {
                        f.copy(options = f.options.map { op ->
                            if (op.isSelected) {
                                activeFilters.remove(f)
                                op.copy(isSelected = false)
                            } else {
                                op
                            }
                        })
                    }
                    else -> f
                }
            }
            activeFilters.clear()
            it.search()
        }
    }

    private suspend fun SearchScope.Query.search() {
        val (result, isSuccess) = networkSearchScope.search(
            productSearch.value,
            manufacturerSearch.value,
            activeFilters.map { (queryName, option) -> queryName to option.value },
        )
        if (isSuccess && result != null) {
            filters.value = result.facets.toFilter()
            products.value = result.products
            "facets".warnIt()
            result.facets.forEach {
                it.logIt()
            }
            "products".warnIt()
            result.products.forEach {
                it.logIt()
            }
        }
    }

    private inline fun List<Facet>.toFilter(): List<Filter> {
        return map { facet ->
            Filter(
                facet.displayName,
                facet.queryId,
                facet.values.map { v ->
                    Option(
                        v.value,
                        activeFilters[facet.queryId]?.takeIf { v.value == it.value }?.isSelected
                            ?: false
                    )
                },
            )
        }
    }
}