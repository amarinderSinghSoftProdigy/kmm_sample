package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.network.CdnUrlProvider
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SellerInfo

class RequestedQuotesScope(
    val productData: ProductSearch,
    val requestedData: DataSource<List<ProductSearch>>
) : Scope.Child.TabBar() ,ToastScope{
    val sellerInfoLocal: DataSource<SellerInfo?> = DataSource(null)

    override val showToast: DataSource<Boolean> = DataSource(false)
    override val cartData: DataSource<CartData?> = DataSource(null)

    fun setSellerInfo(sellInfo: SellerInfo?) {
        this.sellerInfoLocal.value = sellInfo
    }

    fun selectItem(item: String) {
        val url = CdnUrlProvider.urlFor(
            item, CdnUrlProvider.Size.Px320
        )
        EventCollector.sendEvent(Event.Action.Stores.ShowLargeImage(url))
    }

    fun buy(buyingOption: BuyingOption) {
        productData.sellerInfo = this.sellerInfoLocal.value
        productData.buyingOption = buyingOption
        EventCollector.sendEvent(
            Event.Action.Product.BuyProduct(
                productData,
                buyingOption,
            )
        )
    }
}