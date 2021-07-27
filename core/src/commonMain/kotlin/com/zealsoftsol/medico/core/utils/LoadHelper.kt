package com.zealsoftsol.medico.core.utils

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.data.PaginatedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class LoadHelper(
    val _navigator: Navigator,
    private val scope: CoroutineScope,
) {

    private var loadJob: Job? = null

    inline fun <reified L : Loadable<T>, T> load(
        isFirstLoad: Boolean,
        crossinline onLoad: suspend L.() -> PaginatedData<T>?,
    ) {
        _navigator.withScope<L> {
            val canLoad = if (isFirstLoad) {
                it.pagination.reset()
                true
            } else {
                !scope.value.isInProgress.value && it.pagination.canLoadMore()
            }
            if (canLoad) {
                it.loadInternal(
                    withProgress = true,
                    addPage = !isFirstLoad,
                    debounce = 0,
                    loader = onLoad
                )
            }
        }
    }

    inline fun <reified L : Loadable<T>, T> search(
        searchValue: String,
        crossinline onLoad: suspend L.() -> PaginatedData<T>?,
    ) {
        _navigator.withScope<L> {
            it.pagination.reset()
            it.searchText.value = searchValue
            it.loadInternal(withProgress = false, addPage = false, debounce = 500, loader = onLoad)
        }
    }

    private inline fun <reified L : Loadable<T>, T> L.loadInternal(
        withProgress: Boolean,
        addPage: Boolean,
        debounce: Long,
        crossinline loader: suspend L.() -> PaginatedData<T>?,
    ) {
        if (withProgress) _navigator.setHostProgress(true)
        loadJob?.cancel()
        loadJob = scope.launch {
            if (debounce > 0) delay(debounce)
            if (!addPage) {
                totalItems.value = 0
            }
            loader()?.let {
                pagination.setTotal(it.total)
                items.value = if (addPage) items.value + it.data else it.data
                totalItems.value = it.total
            }
            if (withProgress) _navigator.setHostProgress(false)
        }
    }
}

interface Loadable<T> : Scopable {
    val items: DataSource<List<T>>
    val totalItems: DataSource<Int>
    val searchText: DataSource<String>
    val pagination: Pagination
}