package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.regular.DealsScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartIdentifier

internal class DealsEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val cartRepo: CartRepo,
    private val dealsRepo: NetworkScope.DealsStore
) : EventDelegate<Event.Action.Deals>(navigator) {
    override suspend fun handleEvent(event: Event.Action.Deals) = when (event) {
        is Event.Action.Deals.GetAllDeals -> getAllDeals(
            event.page,
            event.search,
            event.unitCode,
            event.promoCode
        )
        is Event.Action.Deals.AddItemToCart -> addItemToCart(
            event.sellerUnitCode, event.productCode,
            event.buyingOption, event.id, event.quantity, event.freeQuantity
        )
        is Event.Action.Deals.ZoomImage -> zoomImage(event.url)
    }

    private fun zoomImage(url: String) {
        navigator.scope.value.bottomSheet.value = BottomSheet.ViewLargeImage(url, null)
    }

    private suspend fun addItemToCart(
        sellerUnitCode: String?,
        productCode: String,
        buyingOption: BuyingOption,
        id: CartIdentifier?,
        quantity: Double,
        freeQuantity: Double,
    ) = async {
        navigator.withScope<DealsScope> {
            cartRepo.addCartItem(
                userRepo.requireUser().unitCode,
                sellerUnitCode,
                productCode,
                buyingOption,
                id,
                quantity,
                freeQuantity,
            ).onSuccess { body ->
                it.showToast.value = true
            }.onError(navigator)
        }

    }

    private suspend inline fun async(crossinline block: suspend () -> Unit) {
        block()
    }

    /**
     * get all deals from server
     */
    private suspend fun getAllDeals(
        page: Int,
        search: String,
        unitCode: String,
        promoCode: String
    ) {
        navigator.withScope<DealsScope> {
            val result = withProgress {
                dealsRepo.getAllDeals(page, search, unitCode, promoCode)
            }

            result.onSuccess { body ->
                if (it.dealsList.value.isEmpty()) {
                    it.offersChoices.value = body.promoTypes
                }
                it.stockistList.value = body.stockists
                it.updateDeals(body.pageableData.results)
                it.totalItems = body.pageableData.totalResults
                it.showNoDeals.value = body.pageableData.results.isEmpty()
            }.onError(navigator)
        }
    }

}