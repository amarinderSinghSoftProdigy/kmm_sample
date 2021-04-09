package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.nested.BuyProductScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ProductInfoScope
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.ErrorCode

internal class ProductEventDelegate(
    navigator: Navigator,
    private val networkProductScope: NetworkScope.Product,
) : EventDelegate<Event.Action.Product>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Product) = when (event) {
        is Event.Action.Product.Select -> selectProduct(event.productCode)
        is Event.Action.Product.SelectAlternative -> selectAlternative(event.data)
        is Event.Action.Product.BuyProduct -> buyProduct(event.productCode)
        is Event.Action.Product.FilterBuyProduct -> filterProduct(event.filter)
    }

    private suspend fun selectProduct(productCode: String) {
        val (response, isSuccess) = navigator.withProgress {
            networkProductScope.getProductData(productCode)
        }
        if (isSuccess && response?.product != null) {
            val isFromStores = navigator.searchQueuesFor<StoresScope.StorePreview>() != null
            navigator.setScope(
                if (isFromStores) {
                    ProductInfoScope.getAsNested(
                        product = response.product!!,
                        fromStoresPage = isFromStores,
                        alternativeBrands = response.alternateProducts,
                    )
                } else {
                    ProductInfoScope.getAsRegular(
                        product = response.product!!,
                        fromStoresPage = isFromStores,
                        alternativeBrands = response.alternateProducts,
                    )
                }
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
            navigator.setScope(BuyProductScope.get(result.product, result.sellerInfo))
        } else {
            navigator.setHostError(ErrorCode())
        }
    }

    private fun filterProduct(filter: String) {
        navigator.withScope<BuyProductScope> {
            it.sellersFilter.value = filter
            it.sellersInfo.value = if (filter.isNotEmpty()) {
                it.allSellers.filter { seller ->
                    seller.tradeName.contains(filter, ignoreCase = true)
                }
            } else {
                it.allSellers
            }
        }
    }
}