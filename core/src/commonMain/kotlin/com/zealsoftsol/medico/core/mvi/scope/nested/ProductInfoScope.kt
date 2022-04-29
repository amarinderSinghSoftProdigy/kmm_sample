package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.AlternateProductData
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.ConnectedStockist
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.ProductVariant
import com.zealsoftsol.medico.data.UserType

class ProductInfoScope(
    val product: ProductSearch,
    val variants: List<ProductVariant>,
    val alternativeBrands: List<AlternateProductData>,
    val isDetailsOpened: DataSource<Boolean> = DataSource(false),
    override val showToast: DataSource<Boolean> = DataSource(false),
    override val cartData: DataSource<CartData?> = DataSource(null),
    val cartItemsCount: ReadOnlyDataSource<Int>,
    val userType: UserType,
) : Scope.Child.TabBar(), CommonScope.CanGoBack, ToastScope {

    val showButton: DataSource<Boolean> = DataSource(false)
    val enableButton: DataSource<Boolean> = DataSource(false)

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        TabBarInfo.NoIconTitle("", null, cartItemsCount)

    fun buy(product: ProductSearch) = EventCollector.sendEvent(
        Event.Action.Product.BuyProduct(
            product,
            product.buyingOption!!
        )
    )

    fun toggleDetails() {
        isDetailsOpened.value = !isDetailsOpened.value
    }

    fun selectAlternativeProduct(product: AlternateProductData) =
        EventCollector.sendEvent(Event.Action.Product.SelectAlternative(product))

    fun selectVariantProduct(variant: ProductVariant) =
        EventCollector.sendEvent(Event.Action.Product.SelectFromSearch(variant.code))

    fun zoomImage(imageCode: String) {
        val url = CdnUrlProvider.urlFor(
            imageCode, CdnUrlProvider.Size.Px320
        )
        EventCollector.sendEvent(Event.Action.Product.ShowLargeImage(url))
    }

    fun openButtons() {
        showButton.value = !showButton.value
    }

    fun enableAddToCart(value: String) {
        if (value.isNotEmpty()) {
            enableButton.value = value != "0"
        } else {
            enableButton.value = false
        }
    }

    fun showConnectedStockist(stockistList: List<ConnectedStockist>) {
        EventCollector.sendEvent(Event.Action.Product.ShowStockist(stockistList))
    }
}