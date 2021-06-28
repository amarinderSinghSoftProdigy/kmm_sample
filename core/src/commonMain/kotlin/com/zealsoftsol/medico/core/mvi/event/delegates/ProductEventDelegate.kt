package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.nested.BuyProductScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ProductInfoScope
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.TapModeHelper
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.WithTradeName

internal class ProductEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkProductScope: NetworkScope.Product,
    private val tapModeHelper: TapModeHelper,
) : EventDelegate<Event.Action.Product>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Product) = when (event) {
        is Event.Action.Product.SelectFromSearch -> selectProduct(event.productCode)
        is Event.Action.Product.SelectAlternative -> selectAlternative(event.data)
        is Event.Action.Product.BuyProduct -> buyProduct(event.productCode, event.buyingOption)
        is Event.Action.Product.FilterBuyProduct -> filterProduct(event.filter)
        is Event.Action.Product.SelectSeasonBoyRetailer -> selectSeasonBoyRetailer(
            event.productCode,
            event.sellerInfo
        )
    }

    private suspend fun selectProduct(productCode: String) {
        navigator.withProgress {
            networkProductScope.getProductData(productCode)
        }.onSuccess { body ->
            val sellerUnitCode =
                navigator.searchQueuesFor<StoresScope.StorePreview>()?.store?.sellerUnitCode
                    ?.takeIf { userRepo.requireUser().type != UserType.SEASON_BOY }
            navigator.setScope(
                ProductInfoScope(
                    sellerUnitCode = sellerUnitCode,
                    product = body.product!!,
                    alternativeBrands = body.alternateProducts,
                )
            )
        }.onError(navigator)
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

    private suspend fun buyProduct(productCode: String, buyingOption: BuyingOption) {
        navigator.withProgress {
            when (buyingOption) {
                BuyingOption.BUY -> networkProductScope.buyProductInfo(productCode)
                BuyingOption.QUOTE -> networkProductScope.getQuotedProductData(productCode)
            }
        }.onSuccess { body ->
            val isSeasonBoy = userRepo.requireUser().type == UserType.SEASON_BOY
            val nextScope = when (buyingOption) {
                BuyingOption.BUY -> BuyProductScope.ChooseStockist(
                    isSeasonBoy = isSeasonBoy,
                    product = body.product,
                    sellersInfo = DataSource(body.sellerInfo),
                    tapModeHelper = tapModeHelper,
                )
                BuyingOption.QUOTE -> BuyProductScope.ChooseQuote(
                    isSeasonBoy = isSeasonBoy,
                    product = body.product,
                    sellersInfo = DataSource(body.sellerInfo),
                    tapModeHelper = tapModeHelper,
                )
            }
            navigator.setScope(nextScope)
        }.onError(navigator)
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

    private suspend fun selectSeasonBoyRetailer(productCode: String, sellerInfo: SellerInfo?) {
        navigator.withScope<BuyProductScope<WithTradeName>> {
            withProgress {
                networkProductScope.buyProductSelectSeasonBoyRetailer(
                    productCode,
                    userRepo.requireUser().unitCode,
                    sellerInfo?.unitCode
                )
            }.onSuccess { body ->
                setScope(
                    BuyProductScope.ChooseRetailer(
                        product = body.product,
                        sellerInfo = body.sellerInfo,
                        retailers = DataSource(body.retailers),
                        tapModeHelper = tapModeHelper,
                    )
                )
            }.onError(navigator)
        }
    }
}