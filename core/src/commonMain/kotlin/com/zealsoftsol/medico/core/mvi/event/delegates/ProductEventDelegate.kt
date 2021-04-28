package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.nested.BuyProductScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ProductInfoScope
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.WithTradeName

internal class ProductEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkProductScope: NetworkScope.Product,
) : EventDelegate<Event.Action.Product>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Product) = when (event) {
        is Event.Action.Product.SelectFromSearch -> selectProduct(event.productCode)
        is Event.Action.Product.SelectAlternative -> selectAlternative(event.data)
        is Event.Action.Product.BuyProduct -> buyProduct(event.productCode)
        is Event.Action.Product.FilterBuyProduct -> filterProduct(event.filter)
        is Event.Action.Product.SelectSeasonBoyRetailer -> selectSeasonBoyRetailer(
            event.productCode,
            event.sellerInfo
        )
    }

    private suspend fun selectProduct(productCode: String) {
        val (response, isSuccess) = navigator.withProgress {
            networkProductScope.getProductData(productCode)
        }
        if (isSuccess && response?.product != null) {
            val sellerUnitCode =
                navigator.searchQueuesFor<StoresScope.StorePreview>()?.store?.sellerUnitCode
            navigator.setScope(
                ProductInfoScope(
                    sellerUnitCode = sellerUnitCode,
                    product = response.product!!,
                    alternativeBrands = response.alternateProducts,
                )
            )
        } else {
            navigator.setHostError(ErrorCode())
        }
    }

    private fun selectAlternative(product: AlternateProductData) {
        navigator.dropScope()
        EventCollector.sendEvent(
            Event.Action.Search.SearchInput(
                isOneOf = true,
                product.name,
                hashMapOf(product.query to product.baseProductName)
            )
        )
    }

    private suspend fun buyProduct(productCode: String) {
        val (result, isSuccess) = navigator.withProgress {
            networkProductScope.buyProductInfo(productCode)
        }
        if (isSuccess && result != null) {
            navigator.setScope(
                BuyProductScope.ChooseStockist(
                    isSeasonBoy = userRepo.requireUser().type == UserType.SEASON_BOY,
                    product = result.product,
                    sellersInfo = DataSource(result.sellerInfo)
                ),
            )
        } else {
            navigator.setHostError(ErrorCode())
        }
    }

    private fun filterProduct(filter: String) {
        navigator.withScope<BuyProductScope<WithTradeName>> {
            it.itemsFilter.value = filter
            it.items.value = if (filter.isNotEmpty()) {
                it.allItems.filter { seller ->
                    seller.tradeName.contains(filter, ignoreCase = true)
                }
            } else {
                it.allItems
            }
        }
    }

    private suspend fun selectSeasonBoyRetailer(productCode: String, sellerInfo: SellerInfo) {
        navigator.withScope<BuyProductScope<WithTradeName>> {
            val (result, isSuccess) = withProgress {
                networkProductScope.buyProductSelectSeasonBoyRetailer(
                    productCode,
                    userRepo.requireUser().unitCode,
                    sellerInfo.unitCode
                )
            }
            if (isSuccess && result != null) {
                setScope(
                    BuyProductScope.ChooseRetailer(
                        product = it.product,
                        sellerInfo = sellerInfo,
                        retailers = DataSource(result.retailers),
                    )
                )
            } else {
                setHostError(ErrorCode())
            }
        }
    }
}