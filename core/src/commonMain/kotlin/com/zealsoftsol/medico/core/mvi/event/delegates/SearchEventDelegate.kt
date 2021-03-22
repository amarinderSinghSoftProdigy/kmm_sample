package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.regular.SearchScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.AutoComplete
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
        is Event.Action.Search.SearchInput -> searchProduct(event.value)
        is Event.Action.Search.SearchAutoComplete -> searchAutoComplete(event.value)
        is Event.Action.Search.SearchManufacturer -> searchManufacturer(event.value)
        is Event.Action.Search.SelectAutoComplete -> selectAutocomplete(event.autoComplete)
        is Event.Action.Search.SelectFilter -> selectFilter(event.filter, event.option)
        is Event.Action.Search.ClearFilter -> clearFilter(event.filter)
        is Event.Action.Search.LoadMoreProducts -> loadMoreProducts()
    }

    private suspend fun searchProduct(value: String) {
        navigator.withScope<SearchScope> {
            it.pagination.reset()
            it.productSearch.value = value
            updateQuery(search = value)
            it.search()
        }
    }

    private suspend fun searchManufacturer(value: String) {
        navigator.withScope<SearchScope> {
            it.pagination.reset()
            it.manufacturerSearch.value = value
            updateQuery(manufacturer = value)
            it.search()
        }
    }

    private suspend fun searchAutoComplete(value: String) {
        navigator.withScope<SearchScope> {
            it.productSearch.value = value
            searchAsync(withDelay = true, withProgress = false) {
                val (result, isSuccess) = networkSearchScope.autocomplete(value)
                if (isSuccess && result != null) {
                    it.autoComplete.value = result
                    if (value.isNotEmpty() && result.isEmpty() && it.products.value.isNotEmpty()) {
                        it.products.value = emptyList()
                    }
                }
            }
        }
    }

    private suspend fun selectAutocomplete(autoComplete: AutoComplete) {
        navigator.withScope<SearchScope> {
            it.productSearch.value = autoComplete.suggestion
            activeFilters[autoComplete.query] = Option(autoComplete.suggestion, false)
            it.pagination.reset()
            it.search(
                onEnd = {
                    activeFilters.remove(autoComplete.query)
                    it.autoComplete.value = emptyList()
                }
            )
        }
    }

    private suspend fun selectFilter(filter: Filter, option: Option<String>) {
        navigator.withScope<SearchScope> {
            it.pagination.reset()
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
            it.pagination.reset()
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
            if (!it.isInProgress.value && it.pagination.canLoadMore()) {
                setHostProgress(true)
                it.search(addPage = true)
            }
        }
    }

    private suspend inline fun SearchScope.search(
        addPage: Boolean = false,
        crossinline onEnd: () -> Unit = {}
    ) {
        searchAsync(withDelay = !addPage, withProgress = addPage) {
            val address = userRepo.requireUser().addressData
            val (result, isSuccess) = networkSearchScope.search(
                pagination,
                address.latitude,
                address.longitude,
                activeFilters.map { (queryName, option) -> queryName to option.value },
            )
            if (isSuccess && result != null) {
                pagination.setTotal(result.totalResults)
                filters.value = result.facets.toFilter()
                products.value = if (!addPage) result.products else products.value + result.products
            }
            onEnd()
        }
    }

    private suspend fun searchAsync(
        withDelay: Boolean,
        withProgress: Boolean,
        search: suspend () -> Unit
    ) {
        searchJob?.cancel()
        searchJob = coroutineContext.toScope().launch {
            if (withDelay) delay(500)
            if (withProgress) navigator.setHostProgress(true)
            search()
            if (withProgress) navigator.setHostProgress(false)
        }
    }

    private fun updateQuery(search: String? = null, manufacturer: String? = null) {
        search?.let {
            activeFilters["search"] = Option(it, false)
        }
        manufacturer?.let {
            activeFilters[Filter.MANUFACTURER_ID] = Option(it, false)
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