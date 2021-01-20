package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.regular.SearchScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.Facet
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.Option
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

internal class SearchEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkSearchScope: NetworkScope.Search,
) : EventDelegate<Event.Action.Search>(navigator) {

    private val activeFilters = hashMapOf<String, Option<String>>()
    private var searchJob: Job? = null

    override suspend fun handleEvent(event: Event.Action.Search) = when (event) {
        is Event.Action.Search.SearchProduct -> searchProduct(event.value)
        is Event.Action.Search.SearchManufacturer -> searchManufacturer(event.value)
        is Event.Action.Search.SelectFilter -> selectFilter(event.filter, event.option)
        is Event.Action.Search.ClearFilter -> clearFilter(event.filter)
        is Event.Action.Search.LoadMoreProducts -> loadMoreProducts()
    }

    private suspend fun searchProduct(value: String) {
        navigator.withScope<SearchScope> {
            it.pagination.currentPage = 0
            it.productSearch.value = value
            it.search()
        }
    }

    private suspend fun searchManufacturer(value: String) {
        navigator.withScope<SearchScope> {
            it.pagination.currentPage = 0
            it.manufacturerSearch.value = value
            it.search()
        }
    }

    private suspend fun selectFilter(filter: Filter, option: Option<String>) {
        navigator.withScope<SearchScope> {
            it.pagination.currentPage = 0
            it.filters.value = it.filters.value.map { f ->
                if (filter.name == f.name) {
                    f.copy(options = f.options.map { op ->
                        when {
                            option.value == op.value -> {
                                val newOption = op.copy(isSelected = !op.isSelected)
                                if (newOption.isSelected) {
                                    activeFilters[f.queryName] = newOption
                                } else {
                                    activeFilters.remove(f.queryName)
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
        navigator.withScope<SearchScope> {
            it.pagination.currentPage = 0
            it.filters.value = it.filters.value.map { f ->
                when {
                    filter == null || filter.name == f.name -> {
                        f.copy(options = f.options.map { op ->
                            if (op.isSelected) {
                                if (f.queryName == Filter.MANUFACTURER_ID) {
                                    it.manufacturerSearch.value = ""
                                }
                                activeFilters.remove(f.queryName)
                                op.copy(isSelected = false)
                            } else {
                                op
                            }
                        })
                    }
                    else -> f
                }
            }
            if (filter == null) it.productSearch.value = ""
            it.search()
        }
    }

    private suspend fun loadMoreProducts() {
        navigator.withScope<SearchScope> {
            if (it.canLoadMore()) {
                setHostProgress(true)
                it.pagination.currentPage++
                it.search(addPage = true)
            }
        }
    }

    private suspend fun SearchScope.search(addPage: Boolean = false) {
        searchJob?.cancel()
        searchJob = coroutineContext.toScope().launch {
            if (!addPage) delay(500)
            if (addPage) navigator.setHostProgress(true)
            val (result, isSuccess) = networkSearchScope.search(
                productSearch.value,
                manufacturerSearch.value,
                pagination.currentPage,
                activeFilters.map { (queryName, option) -> queryName to option.value },
            )
            if (isSuccess && result != null) {
                pagination.totalItems = result.totalResults
                filters.value = result.facets.toFilter()
                products.value = if (!addPage) result.products else products.value + result.products
            }
            if (addPage) navigator.setHostProgress(false)
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