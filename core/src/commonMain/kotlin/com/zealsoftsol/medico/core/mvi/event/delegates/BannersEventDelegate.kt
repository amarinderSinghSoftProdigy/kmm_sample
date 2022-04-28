package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.regular.BannersScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartIdentifier
import com.zealsoftsol.medico.data.CartRequest

internal class BannersEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val bannersRepo: NetworkScope.BannersStore
) : EventDelegate<Event.Action.Banners>(navigator) {
    override suspend fun handleEvent(event: Event.Action.Banners) = when (event) {
        is Event.Action.Banners.GetAllBanners -> getAllBanners(event.page, event.search)
        is Event.Action.Banners.AddItemToCart -> addItemToCart(
            event.sellerUnitCode, event.productCode,
            event.buyingOption, event.id, event.quantity, event.freeQuantity
        )
        is Event.Action.Banners.ZoomImage -> zoomImage(event.url)
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
    ) {
        navigator.withScope<BannersScope> {
            val result = withProgress {
                bannersRepo.addCartEntry(
                    CartRequest(
                        userRepo.requireUser().unitCode,
                        sellerUnitCode,
                        productCode,
                        buyingOption,
                        id,
                        quantity,
                        freeQuantity,
                    )
                )
            }
            result.onSuccess { _ ->
                it.updateAlertVisibility(true)
            }.onError(navigator)
        }
    }

    /**
     * get all banners from server
     */
    private suspend fun getAllBanners(page: Int, search: String) {
        navigator.withScope<BannersScope> {
            val result = withProgress {
                bannersRepo.getAllBanners(page, search)
            }

            result.onSuccess { body ->
                it.updateBanners(body.results)
                it.totalItems = body.totalResults
            }.onError(navigator)
        }
    }

}