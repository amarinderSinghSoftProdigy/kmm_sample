package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.IocScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

internal class IOCEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkStoresScope: NetworkScope.IOCStore,
) : EventDelegate<Event.Action.IOC>(navigator) {

    private var searchJob: Job? = null

    override suspend fun handleEvent(event: Event.Action.IOC) = when (event) {
        is Event.Action.IOC.Load -> loadStores(event.search)
        is Event.Action.IOC.LoadMoreProducts -> loadMoreProducts()
        is Event.Action.IOC.Search -> searchStores(event.value)
        is Event.Action.IOC.Select -> select(event.item)
        is Event.Action.IOC.ShowUploadBottomSheets -> showUploadBottomSheets(event.type)
    }

    private fun showUploadBottomSheets(type: String) {
        navigator.withScope<CommonScope.UploadDocument> {
            val scope = scope.value
            scope.bottomSheet.value = BottomSheet.UploadInvoiceData(
                type,
                it.supportedFileTypes,
            )
        }
    }

    private suspend fun loadStores(search: String?) {
        navigator.withScope<IocScope.IOCListing> {
            it.pagination.reset()
            it.searchText.value = search ?: ""
            it.search(
                addPage = false,
                withDelay = false,
                withProgress = true,
                extraFilters = search,
            )
        }
    }

    private suspend fun loadMoreProducts() {
        navigator.withScope<IocScope.IOCListing> {
            if (!navigator.scope.value.isInProgress.value && it.pagination.canLoadMore()) {
                setHostProgress(true)
                it.search(
                    addPage = true,
                    withDelay = false,
                    withProgress = true,
                    "",
                )
            }
        }
    }


    private suspend fun searchStores(search: String) {
        navigator.withScope<IocScope.IOCListing> {
            it.pagination.reset()
            it.searchText.value = search
            it.search(
                addPage = false,
                withDelay = false,
                withProgress = false,
                extraFilters = search,
            )
        }
    }

    private fun select(item: String) {
        navigator.withScope<IocScope.IOCListing> {
            setScope(
                IocScope.IOCCreate(item)
            )
        }
    }

    private suspend inline fun IocScope.IOCListing.search(
        addPage: Boolean,
        withDelay: Boolean,
        withProgress: Boolean,
        extraFilters: String? = "",
        crossinline onEnd: () -> Unit = {}
    ) {
        searchAsync(withDelay = withDelay, withProgress = withProgress) {
            val user = userRepo.requireUser()
            networkStoresScope.getRetailers(
                unitCode = user.unitCode,
                search = extraFilters,
                pagination = pagination,
            ).onSuccess { body ->
                pagination.setTotal(body.totalResults)
                items.value = if (!addPage) body.results else items.value + body.results
            }.onError(navigator)
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

}