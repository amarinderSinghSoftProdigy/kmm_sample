package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.data.CartIdentifier
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SeasonBoyRetailer
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.WithTradeName

sealed class BuyProductScope<T : WithTradeName>(
    val product: ProductSearch,
    val items: DataSource<List<T>>,
    val itemsFilter: DataSource<String> = DataSource(""),
    val quantities: DataSource<Map<T, Int>> = DataSource(mapOf()),
) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    internal val allItems = items.value

    abstract fun select(item: T): Boolean
    abstract fun ensureMaxQuantity(item: T, count: Int): Boolean

    fun inc(item: T) {
        val count = quantities.value[item] ?: 0
        if (!ensureMaxQuantity(item, count)) return
        quantities.value = quantities.value.toMutableMap().also {
            it[item] = count + 1
        }
    }

    fun dec(item: T) {
        quantities.value = quantities.value
            .mapValues { (info, count) ->
                if (item == info) (count - 1).coerceAtLeast(0) else count
            }
    }

    fun filterItems(filter: String) =
        EventCollector.sendEvent(Event.Action.Product.FilterBuyProduct(filter))

    class ChooseStockist(
        val isSeasonBoy: Boolean,
        product: ProductSearch,
        sellersInfo: DataSource<List<SellerInfo>>
    ) : BuyProductScope<SellerInfo>(product, sellersInfo) {

        override fun ensureMaxQuantity(item: SellerInfo, count: Int): Boolean =
            count < item.stockInfo.availableQty

        override fun select(item: SellerInfo): Boolean {
            val event = if (isSeasonBoy) {
                Event.Action.Product.SelectSeasonBoyRetailer(
                    product.code,
                    item,
                )
            } else {
                Event.Action.Cart.AddItem(
                    item.unitCode,
                    product.code,
                    product.buyingOption,
                    CartIdentifier(item.spid),
                    quantities.value[item]!!
                )
            }
            return EventCollector.sendEvent(event)
        }
    }

    class ChooseRetailer(
        product: ProductSearch,
        val sellerInfo: SellerInfo,
        retailers: DataSource<List<SeasonBoyRetailer>>,
    ) : BuyProductScope<SeasonBoyRetailer>(product, retailers) {

        override fun ensureMaxQuantity(item: SeasonBoyRetailer, count: Int): Boolean =
            count < sellerInfo.stockInfo.availableQty

        override fun select(item: SeasonBoyRetailer) = EventCollector.sendEvent(
            Event.Action.Cart.AddItem(
                sellerInfo.unitCode,
                product.code,
                product.buyingOption,
                CartIdentifier(sellerInfo.spid, seasonBoyRetailerId = item.id),
                quantities.value[item]!!
            )
        )
    }
}