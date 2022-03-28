package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.nested.IocBuyerScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.BuyerDetailsData
import com.zealsoftsol.medico.data.InvUserData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

internal class IOCBuyerEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkStoresScope: NetworkScope.IOCBuyerStore,
) : EventDelegate<Event.Action.IOCBuyer>(navigator) {

    private var searchJobUsers: Job? = null

    override suspend fun handleEvent(event: Event.Action.IOCBuyer) = when (event) {
        is Event.Action.IOCBuyer.LoadUsers -> loadUsers(event.search)
        is Event.Action.IOCBuyer.OpenIOCDetails -> openDetails(event.item)
        is Event.Action.IOCBuyer.OpenIOCListing -> openListing(event.item)
        is Event.Action.IOCBuyer.LoadMoreUsers -> loadMoreUsers()
        is Event.Action.IOCBuyer.LoadInvListing -> getRetailerInvoiceListing(event.unitCode)
        is Event.Action.IOCBuyer.LoadInvDetails -> getInvoiceDetails(event.invoiceId)
        is Event.Action.IOCBuyer.OpenPaymentMethod -> openPaymentMethod(event.item)
        is Event.Action.IOCBuyer.OpenPayNow -> openPayNow(event.item, event.type)
    }


    private fun openListing(item: InvUserData) {
        navigator.withScope<IocBuyerScope.InvUserListing> {
            setScope(
                IocBuyerScope.InvListing(item)
            )
        }
    }

    private fun openDetails(item: BuyerDetailsData) {
        navigator.withScope<IocBuyerScope.InvListing> {
            setScope(
                IocBuyerScope.InvDetails(item)
            )
        }
    }


    private fun openPayNow(item: BuyerDetailsData, type: IocBuyerScope.PaymentTypes) {
        navigator.withScope<IocBuyerScope.IOCPaymentMethod> {
            setScope(
                IocBuyerScope.IOCPayNow(item, type)
            )
        }
    }

    private fun openPaymentMethod(item: BuyerDetailsData) {
        navigator.withScope<IocBuyerScope.InvDetails> {
            setScope(
                IocBuyerScope.IOCPaymentMethod(item)
            )
        }
    }

    //Methods for InvUserListing
    private suspend fun loadUsers(search: String?) {
        navigator.withScope<IocBuyerScope.InvUserListing> {
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
        navigator.withScope<IocBuyerScope.InvUserListing> {
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

    private suspend inline fun IocBuyerScope.InvUserListing.search(
        addPage: Boolean,
        withDelay: Boolean,
        withProgress: Boolean,
        extraFilters: String? = "",
        crossinline onEnd: () -> Unit = {}
    ) {
        searchUsersAsync(withDelay = withDelay, withProgress = withProgress) {
            val user = userRepo.requireUser()
            networkStoresScope.getBuyers(
                unitCode = user.unitCode,
                search = extraFilters,
                pagination = pagination,
            ).onSuccess { body ->
                if (body.totalResults == 0) {
                    items.value = ArrayList()
                } else {
                    pagination.setTotal(body.totalResults)
                    items.value = if (!addPage) body.results else items.value + body.results
                }
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
        navigator.withScope<IocBuyerScope.InvListing> {
            navigator.withProgress {
                networkStoresScope.buyerInvoiceListing(
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
        navigator.withScope<IocBuyerScope.InvDetails> {
            navigator.withProgress {
                networkStoresScope.buyerInvoiceDetails(
                    invoiceId,
                )
            }.onSuccess { body ->
                it.data.value = body
                it.items.value = body.iocCollections
            }.onError(navigator)
        }
    }

}