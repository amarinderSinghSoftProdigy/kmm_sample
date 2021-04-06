package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.regular.BaseSearchScope
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

    private val activeFilters = hashMapOf<String, Option.StringValue>()
    private var searchJob: Job? = null

    override suspend fun handleEvent(event: Event.Action.Search) = when (event) {
        is Event.Action.Search.SearchInput -> searchInput(event.isOneOf, event.search, event.query)
        is Event.Action.Search.SearchAutoComplete -> searchAutoComplete(event.value)
        is Event.Action.Search.SearchFilter -> searchFilter(event.filter, event.value)
        is Event.Action.Search.SelectAutoComplete -> selectAutocomplete(event.autoComplete)
        is Event.Action.Search.SelectFilter -> selectFilter(event.filter, event.option)
        is Event.Action.Search.ClearFilter -> clearFilter(event.filter)
        is Event.Action.Search.LoadMoreProducts -> loadMoreProducts()
        is Event.Action.Search.Reset -> reset()
    }

    private suspend fun searchInput(isOneOf: Boolean, search: String?, query: Map<String, String>) {
        navigator.withScope<BaseSearchScope> {
            it.pagination.reset()
            if (search != null) it.productSearch.value = search
            it.autoComplete.value = emptyList()
            query.forEach { (key, value) ->
                activeFilters[key] = Option.StringValue(value, false)
            }
            val isWildcardSearch = search == null && query.isEmpty()
            it.search(
                addPage = false,
                withDelay = false,
                withProgress = if (it.supportsAutoComplete) !isWildcardSearch else false,
                onEnd = {
                    if (isOneOf) {
                        query.keys.forEach { key -> activeFilters.remove(key) }
                    }
                },
            )
        }
    }

    private fun searchFilter(filter: Filter, value: String) {
        navigator.withScope<BaseSearchScope> {
            it.pagination.reset()
            val oldSearches = it.filterSearches.value.toMutableMap()
            oldSearches[filter.queryId] = value
            it.filterSearches.value = oldSearches

            it.filters.value = it.filters.value.map { f ->
                if (f.queryId == filter.queryId) {
                    var anyInvisible = false
                    val newOptions =
                        f.options.filterIsInstance<Option.StringValue>().mapIndexed { index, o ->
                            val vis = if (value.isNotEmpty()) {
                                o.value.contains(value, ignoreCase = true)
                            } else {
                                index < MAX_OPTIONS
                            }
                            anyInvisible = anyInvisible || !vis
                            o.copy(isVisible = vis)
                        }
                    f.copy(options = if (value.isEmpty() || anyInvisible) newOptions + Option.ViewMore else newOptions)
                } else {
                    f
                }
            }
        }
    }

    private suspend fun searchAutoComplete(value: String) {
        navigator.withScope<BaseSearchScope> {
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
        navigator.withScope<BaseSearchScope> {
            it.productSearch.value = autoComplete.suggestion
            activeFilters[autoComplete.query] = Option.StringValue(autoComplete.suggestion, false)
            it.pagination.reset()
            it.search(
                addPage = false,
                withDelay = false,
                withProgress = true,
                onEnd = {
                    activeFilters.remove(autoComplete.query)
                    it.autoComplete.value = emptyList()
                }
            )
        }
    }

    private suspend fun selectFilter(filter: Filter, option: Option) {
        navigator.withScope<BaseSearchScope> {
            when (option) {
                is Option.ViewMore -> {
                    it.filters.value = it.filters.value.map { f ->
                        if (filter.queryId == f.queryId) {
                            f.copy(
                                options = f.options.filterIsInstance<Option.StringValue>()
                                    .map { o -> o.copy(isVisible = true) })
                        } else {
                            f
                        }
                    }
                }
                is Option.StringValue -> {
                    it.pagination.reset()
                    it.filters.value = it.filters.value.map { f ->
                        if (filter.queryId == f.queryId) {
                            f.copy(options = f.options.map { op ->
                                when {
                                    op is Option.StringValue && option.value == op.value -> {
                                        val newOption = op.copy(isSelected = !op.isSelected)
                                        if (newOption.isSelected) {
                                            activeFilters[f.queryId] = newOption
                                        } else {
                                            activeFilters.remove(f.queryId)
                                        }
                                        newOption
                                    }
                                    op is Option.StringValue && op.isSelected -> op.copy(isSelected = false)
                                    else -> op
                                }
                            })
                        } else {
                            f
                        }
                    }
                    it.search(
                        addPage = false,
                        withDelay = false,
                        withProgress = false,
                    )
                }
            }
        }
    }

    private suspend fun clearFilter(filter: Filter?) {
        navigator.withScope<BaseSearchScope> {
            it.pagination.reset()
            it.filters.value = it.filters.value.map { f ->
                when {
                    filter?.queryId == f.queryId -> {
                        it.filterSearches.value = it.filterSearches.value.toMutableMap().apply {
                            remove(f.queryId)
                        }
                        f.copy(
                            options = f.options.map { op ->
                                if (op is Option.StringValue && op.isSelected) {
                                    activeFilters.remove(f.queryId)
                                    op.copy(isSelected = false)
                                } else {
                                    op
                                }
                            }
                        )
                    }
                    else -> f
                }
            }
            if (filter == null) {
                activeFilters.clear()
                it.productSearch.value = ""
                it.filterSearches.value = emptyMap()
            }
            it.search(
                addPage = false,
                withDelay = false,
                withProgress = true,
            )
        }
    }

    private suspend fun loadMoreProducts() {
        navigator.withScope<BaseSearchScope> {
            if (!navigator.scope.value.isInProgress.value && it.pagination.canLoadMore()) {
                setHostProgress(true)
                it.search(
                    addPage = true,
                    withDelay = false,
                    withProgress = true,
                )
            }
        }
    }

    private fun reset() {
        searchJob?.cancel()
        activeFilters.clear()
    }

    private suspend inline fun BaseSearchScope.search(
        addPage: Boolean,
        withDelay: Boolean,
        withProgress: Boolean,
        crossinline onEnd: () -> Unit = {}
    ) {
        searchAsync(withDelay = withDelay, withProgress = withProgress) {
            val address = userRepo.requireUser().addressData
            val (result, isSuccess) = networkSearchScope.search(
                activeFilters.map { (queryName, option) -> queryName to option.value },
                unitCode,
                address.latitude,
                address.longitude,
                pagination,
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

    private inline fun List<Facet>.toFilter(): List<Filter> {
        return map { facet ->
            val options = facet.values.mapIndexed { index, v ->
                Option.StringValue(
                    v.value,
                    isSelected = activeFilters[facet.queryId]
                        ?.takeIf { v.value == it.value }?.isSelected
                        ?: false,
                    isVisible = index < MAX_OPTIONS,
                )
            }
            Filter(
                name = facet.displayName,
                queryId = facet.queryId,
                options = if (facet.values.size > MAX_OPTIONS) options + Option.ViewMore else options,
            )
        }
    }

    companion object {
        private const val MAX_OPTIONS = 5
    }
}