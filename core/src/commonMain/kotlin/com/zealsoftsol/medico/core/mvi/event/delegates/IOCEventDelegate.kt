package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.IocScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OffersScope
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.NotificationRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.getEntriesCountDataSource
import com.zealsoftsol.medico.core.repository.getUnreadMessagesDataSource
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.AnyResponse
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.Store
import com.zealsoftsol.medico.data.UserRegistration1
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

internal class IOCEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val cartRepo: CartRepo,
    private val notificationRepo: NotificationRepo,
    private val networkStoresScope: NetworkScope.IOCStore,
    private val loadHelper: LoadHelper,
) : EventDelegate<Event.Action.IOC>(navigator) {

    private var searchJob: Job? = null

    override suspend fun handleEvent(event: Event.Action.IOC) = when (event) {
        is Event.Action.IOC.Load -> loadStores(event.search, event.query)
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

    private suspend fun loadStores(search: String?, query: ArrayList<String>) {
        navigator.withScope<IocScope.IOCListing> {
            it.pagination.reset()
            //it.productSearch.value = search ?: ""
            //if (!query.isNullOrEmpty()) it.manufacturerSearch.value = query
            val isWildcardSearch = search.isNullOrBlank()
            it.search(
                addPage = false,
                withDelay = false,
                withProgress = isWildcardSearch,
                extraFilters = search,
                manufacturers = query
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
                    ArrayList()
                )
            }
        }
    }


    private suspend fun searchStores(search: String) {
        /* loadHelper.search<StoresScope.All, Store>(searchValue = search) {
             val user = userRepo.requireUser()
             networkStoresScope.getStores(
                 unitCode = user.unitCode,
                 search = searchText.value,
                 pagination = pagination,
             ).getBodyOrNull()
         }*/
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
        manufacturers: ArrayList<String>?,
        crossinline onEnd: () -> Unit = {}
    ) {
        searchAsync(withDelay = withDelay, withProgress = withProgress) {
            val user = userRepo.requireUser()
            networkStoresScope.getRetailers(
                unitCode = user.unitCode,
                search = extraFilters,
                pagination = pagination,
            ).onSuccess { body ->
                /*pagination.setTotal(body.totalResults)
                statuses.value = body.promotionStatusDatas
                manufacturer.value = body.manufacturers
                items.value = if (!addPage) body.promotions else items.value + body.promotions*/
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