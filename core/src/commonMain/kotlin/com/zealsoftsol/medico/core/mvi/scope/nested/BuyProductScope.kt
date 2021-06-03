package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.utils.TapModeHelper
import com.zealsoftsol.medico.data.CartIdentifier
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SeasonBoyRetailer
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.TapMode
import com.zealsoftsol.medico.data.WithTradeName

sealed class BuyProductScope<T : WithTradeName>(
    val product: ProductSearch,
    val items: DataSource<List<T>>,
    val itemsFilter: DataSource<String> = DataSource(""),
    val quantities: DataSource<Map<T, Int>> = DataSource(mapOf()),
    private val tapModeHelper: TapModeHelper,
) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    internal val allItems = items.value

    abstract fun select(item: T): Boolean
    abstract fun ensureMaxQuantity(item: T, count: Int): Boolean

    fun inc(mode: TapMode, item: T) {
        var count = quantities.value[item] ?: 0
        if (!ensureMaxQuantity(item, count)) return
        tapModeHelper.action(mode) {
            count = quantities.value[item] ?: count
            quantities.value = quantities.value.toMutableMap().also {
                it[item] = count + 1
            }
            ensureMaxQuantity(item, count + 1)
        }
    }

    fun dec(mode: TapMode, item: T) {
        tapModeHelper.action(mode) {
            var changedCount = 0
            quantities.value = quantities.value
                .mapValues { (info, count) ->
                    if (item == info) {
                        changedCount = (count - 1).coerceAtLeast(0)
                        changedCount
                    } else {
                        count
                    }
                }
            changedCount > 0
        }
    }

    fun filterItems(filter: String) =
        EventCollector.sendEvent(Event.Action.Product.FilterBuyProduct(filter))

    class ChooseQuote(
        val isSeasonBoy: Boolean,
        val selectedOption: DataSource<Option> = DataSource(Option.EXISTING_STOCKIST),
        val chosenSeller: DataSource<SellerInfo?> = DataSource(null),
        product: ProductSearch,
        sellersInfo: DataSource<List<SellerInfo>>,
        tapModeHelper: TapModeHelper,
    ) : BuyProductScope<SellerInfo>(product, sellersInfo, tapModeHelper = tapModeHelper) {

        fun toggleOption(option: Option) {
            selectedOption.value = option
        }

        fun chooseSeller(sellerInfo: SellerInfo) {
            chosenSeller.value = sellerInfo
        }

        override fun ensureMaxQuantity(item: SellerInfo, count: Int): Boolean = true

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

        fun selectAnyone(): Boolean {
            val event = if (isSeasonBoy) {
                Event.Action.Product.SelectSeasonBoyRetailer(
                    product.code,
                    sellerInfo = null,
                )
            } else {
                Event.Action.Cart.AddItem(
                    sellerUnitCode = null,
                    product.code,
                    product.buyingOption,
                    id = null,
                    quantities.value[SellerInfo.anyone]!!,
                )
            }
            return EventCollector.sendEvent(event)
        }

        enum class Option {
            EXISTING_STOCKIST, ANYONE
        }
    }

    class ChooseStockist(
        val isSeasonBoy: Boolean,
        product: ProductSearch,
        sellersInfo: DataSource<List<SellerInfo>>,
        tapModeHelper: TapModeHelper,
    ) : BuyProductScope<SellerInfo>(product, sellersInfo, tapModeHelper = tapModeHelper) {

        init {
            if (!isSeasonBoy) {
                quantities.value = allItems.filter { it.cartInfo != null }
                    .map { it to it.cartInfo!!.quantity.value.toInt() }.toMap()
            }
        }

        override fun ensureMaxQuantity(item: SellerInfo, count: Int): Boolean =
            item.stockInfo?.let {
                count < it.availableQty
            } ?: true

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
        val sellerInfo: SellerInfo?,
        retailers: DataSource<List<SeasonBoyRetailer>>,
        tapModeHelper: TapModeHelper,
    ) : BuyProductScope<SeasonBoyRetailer>(product, retailers, tapModeHelper = tapModeHelper) {

        init {
            quantities.value = allItems.filter { it.cartInfo != null }
                .map { it to it.cartInfo!!.quantity.value.toInt() }.toMap()
        }

        override fun ensureMaxQuantity(item: SeasonBoyRetailer, count: Int): Boolean =
            sellerInfo?.stockInfo?.let {
                count < it.availableQty
            } ?: true

        override fun select(item: SeasonBoyRetailer) = EventCollector.sendEvent(
            Event.Action.Cart.AddItem(
                sellerInfo?.unitCode,
                product.code,
                product.buyingOption,
                CartIdentifier(spid = sellerInfo?.spid, seasonBoyRetailerId = item.id),
                quantities.value[item]!!
            )
        )
    }
}