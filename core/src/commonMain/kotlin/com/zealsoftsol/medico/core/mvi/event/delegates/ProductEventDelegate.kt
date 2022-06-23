package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.BuyProductScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ProductInfoScope
import com.zealsoftsol.medico.core.mvi.scope.nested.RequestedQuotesScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SearchScope
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.getEntriesCountDataSource
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.TapModeHelper
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartIdentifier
import com.zealsoftsol.medico.data.ConnectedStockist
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.WithTradeName

internal class ProductEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkProductScope: NetworkScope.Product,
    private val tapModeHelper: TapModeHelper,
    private val cartRepo: CartRepo
) : EventDelegate<Event.Action.Product>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Product) = when (event) {
        is Event.Action.Product.SelectFromSearch -> selectProduct(event.productCode)
        is Event.Action.Product.SelectAlternative -> selectAlternative(event.data)
        is Event.Action.Product.BuyProduct -> buyProduct(event.product, event.buyingOption)
        is Event.Action.Product.FilterBuyProduct -> filterProduct(event.filter)
        is Event.Action.Product.SelectSeasonBoyRetailer -> selectSeasonBoyRetailer(
            event.productCode,
            event.sellerInfo
        )
        is Event.Action.Product.PreviewStockistBottomSheet -> previewStockistBottomSheet(event.sellerInfo)
        is Event.Action.Product.ShowLargeImage -> selectProductLargeImage(event.url)
        is Event.Action.Product.ShowStockist -> showConnectedStockist(event.stockist)
    }

    /**
     *  load the searched stockists on a bottom sheet
     */
    private fun showConnectedStockist(stockist: List<ConnectedStockist>) {
        navigator.withScope<ProductInfoScope> {
            val hostScope = scope.value
            hostScope.bottomSheet.value = BottomSheet.ShowConnectedStockist(stockist)
        }
    }

    /**
     * show zoomable image
     */
    private fun selectProductLargeImage(item: String) {
        navigator.scope.value.bottomSheet.value = BottomSheet.ViewLargeImage(item, null)
    }

    private suspend fun selectProduct(productCode: String) {
        navigator.withProgress {
            networkProductScope.getProductData(productCode)
        }.onSuccess { body ->
            navigator.setScope(
                ProductInfoScope(
                    product = body.product!!,
                    alternativeBrands = body.alternateProducts,
                    variants = body.variants.filter { it.code != body.product!!.code },
                    cartItemsCount = cartRepo.getEntriesCountDataSource(),
                    userType = userRepo.userV2Flow.value!!.type
                )
            )
        }.onError(navigator)
    }

    private fun selectAlternative(product: AlternateProductData) {
        navigator.setScope(SearchScope(null))
            EventCollector.sendEvent(
                Event.Action.Search.SearchInput(
                    isOneOf = false,
                    product.name,
                    hashMapOf(product.query to product.baseProductName),
                )
            )
    }

    /*private suspend fun buyProduct(product: ProductSearch, buyingOption: BuyingOption) {

        navigator.searchQueuesFor<StoresScope.StorePreview>()?.store?.let {
            addToCartItems(product)
            return
        }

        navigator.searchQueuesFor<ProductInfoScope>()?.cartData?.let {
            addToCartItems(product)
            return
        }

        navigator.withProgress {
            val address = userRepo.requireUser()//userRepo.requireUser().addressData
            when (buyingOption) {
                BuyingOption.BUY -> networkProductScope.buyProductInfo(
                    product.code,
                    address.latitude,
                    address.longitude
                )
                BuyingOption.QUOTE -> networkProductScope.getQuotedProductData(product.code)
            }
        }.onSuccess { body ->
            val isSeasonBoy = userRepo.requireUser().type == UserType.SEASON_BOY
            val nextScope = when (buyingOption) {
                BuyingOption.BUY -> {
                    BuyProductScope.ChooseStockist(
                        isSeasonBoy = isSeasonBoy,
                        product = body.product,
                        sellersInfo = DataSource(body.sellerInfo),
                        tapModeHelper = tapModeHelper,
                    )
                }
                BuyingOption.QUOTE -> BuyProductScope.ChooseQuote(
                    isSeasonBoy = isSeasonBoy,
                    product = body.product,
                    sellersInfo = DataSource(body.sellerInfo),
                    tapModeHelper = tapModeHelper,
                )
            }
            navigator.setScope(nextScope)
        }.onError(navigator)
    }*/

    private suspend fun buyProduct(product: ProductSearch, buyingOption: BuyingOption) {

        navigator.searchQueuesFor<StoresScope.StorePreview>()?.store?.let {
            addToCartItems(product)
            return
        }
        navigator.searchQueuesFor<RequestedQuotesScope>()?.productData?.let {
            if (buyingOption == BuyingOption.BUY) {
                addToCartItems(product)
                return
            }
        }

        navigator.searchQueuesFor<ProductInfoScope>()?.cartData?.let {
            addToCartItems(product)
            return
        }

        val address = userRepo.requireUser()//userRepo.requireUser().addressData
        when (buyingOption) {
            BuyingOption.BUY -> navigator.withProgress {
                networkProductScope.buyProductInfo(
                    product.code,
                    address.latitude,
                    address.longitude
                )
            }.onSuccess { body ->
                val isSeasonBoy = userRepo.requireUser().type == UserType.SEASON_BOY

                val nextScope = BuyProductScope.ChooseStockist(
                    isSeasonBoy = isSeasonBoy,
                    product = body.product,
                    sellersInfo = DataSource(body.sellerInfo),
                    tapModeHelper = tapModeHelper,
                )
                navigator.setScope(nextScope)
            }.onError(navigator)

            BuyingOption.QUOTE -> navigator.withProgress {
                networkProductScope.getRequestedProductData(product.code)
            }.onSuccess { body ->
                val nextScope = RequestedQuotesScope(product, DataSource(body.results))
                navigator.setScope(nextScope)
            }.onError(navigator)
        }
    }

    private suspend fun addToCartItems(product: ProductSearch) {
        if (userRepo.requireUser().type != UserType.SEASON_BOY) {
            product.sellerInfo?.spid?.let { spid ->
                EventCollector.sendEvent(
                    Event.Action.Cart.AddItem(
                        product.sellerInfo?.unitCode,
                        product.code,
                        product.buyingOption!!,
                        CartIdentifier(spid),
                        product.quantity,
                        product.freeQuantity,
                    )
                )
            }
        } else {
            selectSeasonBoyRetailer(product.code, product.sellerInfo!!)
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

    private suspend fun selectSeasonBoyRetailer(productCode: String, sellerInfo: SellerInfo?) {
        navigator.withScope<Scopable> {
            withProgress {
                networkProductScope.buyProductSelectSeasonBoyRetailer(
                    productCode,
                    userRepo.requireUser().unitCode,
                    sellerInfo?.unitCode,
                    userRepo.requireUser().latitude,
                    userRepo.requireUser().latitude,
                    //userRepo.requireUser().addressData.latitude,
                    //userRepo.requireUser().addressData.longitude,
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

    private fun previewStockistBottomSheet(sellerInfo: SellerInfo) {
        navigator.scope.value.bottomSheet.value = BottomSheet.PreviewStockist(sellerInfo)
    }
}