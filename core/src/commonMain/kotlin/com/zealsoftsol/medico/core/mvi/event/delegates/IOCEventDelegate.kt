package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.IocScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.AddInvoice
import com.zealsoftsol.medico.data.BuyerDetailsData
import com.zealsoftsol.medico.data.InvUserData
import com.zealsoftsol.medico.data.RetailerData
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
    private var searchJobUsers: Job? = null

    override suspend fun handleEvent(event: Event.Action.IOC) = when (event) {
        is Event.Action.IOC.LoadUsers -> loadUsers(event.search)
        is Event.Action.IOC.LoadMoreProducts -> loadMoreProducts()
        is Event.Action.IOC.Search -> searchStores(event.value)
        is Event.Action.IOC.Select -> select(event.item)
        is Event.Action.IOC.OpenIOCDetails -> openDetails(event.item)
        is Event.Action.IOC.ShowUploadBottomSheets -> showUploadBottomSheets(event.type)
        is Event.Action.IOC.UploadInvoice -> uploadDocument(event)
        is Event.Action.IOC.SubmitInvoice -> submitInvoice(event.value)
        is Event.Action.IOC.OpenIOCListing -> openListing(event.item)
        is Event.Action.IOC.OpenCreateIOC -> openCreateIOC()
        is Event.Action.IOC.OpenEditIOCBottomSheet -> showEditIOCBottomSheets(event.item)
        is Event.Action.IOC.UpdateIOC -> updateInvoice()
        is Event.Action.IOC.LoadMoreUsers -> loadMoreUsers()
        is Event.Action.IOC.LoadInvListing -> getRetailerInvoiceListing(event.unitCode)
        is Event.Action.IOC.LoadInvDetails -> getInvoiceDetails(event.invoiceId)
    }

    private suspend fun submitInvoice(value: AddInvoice) {
        navigator.withScope<IocScope.IOCCreate> {
            val result = withProgress {
                networkStoresScope.submitInvoice(value)
            }
            result.onSuccess { body ->
                it.dialogMessage.value = body.info
                it.showAlert.value = true
            }.onError(navigator)
        }
    }

    private suspend fun uploadDocument(event: Event.Action.IOC) {
        navigator.withScope<IocScope.IOCCreate> {
            val result = withProgress {
                when (event) {
                    is Event.Action.IOC.UploadInvoice -> {
                        userRepo.uploadProfileImage(
                            size = event.size,
                            fileString = event.asBase64,
                            mimeType = event.fileType.mimeType,
                            type = event.type
                        )
                    }
                    else -> throw UnsupportedOperationException("unsupported event $event for uploadDocument()")
                }

            }
            result.onSuccess { body ->
                when (body.documentType) {
                    "IOC_IMAGE" -> {
                        it.invoiceUpload.value =
                            it.invoiceUpload.value.copy(
                                cdnUrl = body.cdnUrl,
                                id = body.id,
                                documentType = body.documentType
                            )
                        it.validate()
                    }

                }
            }.onError(navigator)
        }
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

    private fun showEditIOCBottomSheets(type: BuyerDetailsData) {
        navigator.withScope<IocScope> {
            val scope = scope.value
            scope.bottomSheet.value = BottomSheet.EditIOC(type)
        }
    }

    private fun updateInvoice() {

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
                withProgress = search.isEmpty(),
                extraFilters = search,
            )
        }
    }

    private fun select(item: RetailerData) {
        navigator.withScope<IocScope.IOCListing> {
            setScope(
                IocScope.IOCCreate(item)
            )
        }
    }

    private fun openListing(item: InvUserData) {
        navigator.withScope<IocScope.InvUserListing> {
            setScope(
                IocScope.InvListing(item)
            )
        }
    }

    private fun openDetails(item: BuyerDetailsData) {
        navigator.withScope<IocScope.InvListing> {
            setScope(
                IocScope.InvDetails(item)
            )
        }
    }

    private fun openCreateIOC() {
        navigator.withScope<IocScope.InvUserListing> {
            setScope(
                IocScope.IOCListing()
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


    //Methods for InvUserListing
    private suspend fun loadUsers(search: String?) {
        navigator.withScope<IocScope.InvUserListing> {
            it.pagination.reset()
            it.searchText.value = search ?: ""
            it.search(
                addPage = false,
                withDelay = false,
                withProgress = search?.isEmpty() ?: true,
                extraFilters = search,
            )
        }
    }

    private suspend fun loadMoreUsers() {
        navigator.withScope<IocScope.InvUserListing> {
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

    private suspend inline fun IocScope.InvUserListing.search(
        addPage: Boolean,
        withDelay: Boolean,
        withProgress: Boolean,
        extraFilters: String? = "",
        crossinline onEnd: () -> Unit = {}
    ) {
        searchUsersAsync(withDelay = withDelay, withProgress = withProgress) {
            val user = userRepo.requireUser()
            networkStoresScope.getUsers(
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

    private suspend fun searchUsersAsync(
        withDelay: Boolean,
        withProgress: Boolean,
        search: suspend () -> Unit
    ) {
        searchJobUsers?.cancel()
        searchJobUsers = coroutineContext.toScope().launch {
            if (withDelay) delay(500)
            if (withProgress) navigator.setHostProgress(true)
            search()
            if (withProgress) navigator.setHostProgress(false)
        }
    }


    //Methods for Retailer Invoice listing
    private suspend fun getRetailerInvoiceListing(unitCode: String) {
        navigator.withScope<IocScope.InvListing> {
            navigator.withProgress {
                networkStoresScope.retailerInvoiceDetails(
                    unitCode,
                )
            }.onSuccess { body ->
                it.data.value = body
                it.items.value = body.buyerDetails.results
            }.onError(navigator)
        }
    }


    //Methods for Retailer Invoice Details
    private suspend fun getInvoiceDetails(invoiceId: String) {
        navigator.withScope<IocScope.InvDetails> {
            navigator.withProgress {
                networkStoresScope.invoiceDetails(
                    invoiceId,
                )
            }.onSuccess { body ->
                it.data.value = body
                it.items.value = body.iocCollections
            }.onError(navigator)
        }
    }

}