package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.OffersScope
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.PromotionType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

internal class OffersEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkOffersScope: NetworkScope.OffersStore,
) : EventDelegate<Event.Action.Offers>(navigator), CommonScope.CanGoBack {
    private var searchJob: Job? = null

    override suspend fun handleEvent(event: Event.Action.Offers) = when (event) {
        is Event.Action.Offers.ShowBottomSheet -> showBottomSheet(event.promotionType,event.name)
        is Event.Action.Offers.GetOffers -> getOffers(event.search, event.query)
        is Event.Action.Offers.UpdateOffer -> updateOffer(event.promotionType)
        is Event.Action.Offers.LoadMoreProducts -> loadMoreProducts()
    }

    private suspend fun getOffers(search: String?, query: Map<String, String>) {
        navigator.withScope<OffersScope> {
            it.pagination.reset()
            //if (search != null) it.productSearch.value = search
            it.search(
                addPage = false,
                withDelay = false,
                withProgress = true,
                extraFilters = "",
            )
        }
    }

    private suspend fun loadMoreProducts() {
        navigator.withScope<OffersScope> {
            if (!navigator.scope.value.isInProgress.value && it.pagination.canLoadMore()) {
                setHostProgress(true)
                it.search(
                    addPage = true,
                    withDelay = false,
                    withProgress = true,
                    ""
                )
            }
        }
    }


    private suspend fun updateOffer(promotionType: PromotionType?) {
        val user = userRepo.requireUser()
        networkOffersScope.updateOffer(
            user.unitCode, promotionType?.code ?: ""
        ).onSuccess { body ->
        }.onError(navigator)
    }

    private fun showBottomSheet(promotionType: PromotionType?,name:String) {
        navigator.withScope<OffersScope> {
            val hostScope = scope.value
            hostScope.bottomSheet.value = BottomSheet.UpdateOfferStatus(
                promotionType,name
            )
        }

    }

    private suspend inline fun OffersScope.search(
        addPage: Boolean,
        withDelay: Boolean,
        withProgress: Boolean,
        extraFilters: String,
        crossinline onEnd: () -> Unit = {}
    ) {
        searchAsync(withDelay = withDelay, withProgress = withProgress) {
            val user = userRepo.requireUser()
            networkOffersScope.getOffersData(
                search = extraFilters,
                unitCode = user.unitCode,
                pagination = pagination,
            ).onSuccess { body ->
                pagination.setTotal(body.totalResults)
                statuses.value = body.promotionStatusDatas
                manufacturer.value = body.manufacturers
                items.value = if (!addPage) body.promotions else items.value + body.promotions
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