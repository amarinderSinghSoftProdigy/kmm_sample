package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.utils.TapModeHelper
import com.zealsoftsol.medico.data.CartItem
import com.zealsoftsol.medico.data.CartSubmitResponse
import com.zealsoftsol.medico.data.SellerCart
import com.zealsoftsol.medico.data.Total

class CartScope(
    val items: ReadOnlyDataSource<List<SellerCart>>,
    val total: ReadOnlyDataSource<Total?>,
    val tapModeHelper: TapModeHelper,
) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    fun updateItemCount(sellerCart: SellerCart, item: CartItem, quantity: Int): Boolean {
        if (quantity < 0) return false
        return if (quantity == 0) {
            removeItem(sellerCart, item)
        } else {
            EventCollector.sendEvent(
                Event.Action.Cart.UpdateItem(
                    sellerCart.sellerCode,
                    item.productCode,
                    item.buyingOption,
                    item.id,
                    quantity.coerceIn(0, item.stockInfo?.availableQty ?: Int.MAX_VALUE),
                )
            )
        }
    }

    fun removeItem(sellerCart: SellerCart, item: CartItem) =
        EventCollector.sendEvent(
            Event.Action.Cart.RemoveItem(
                sellerCart.sellerCode,
                item.productCode,
                item.buyingOption,
                item.id,
            )
        )

    fun removeSellerItems(sellerCart: SellerCart) =
        EventCollector.sendEvent(Event.Action.Cart.RemoveSellerItems(sellerCart.sellerCode))

    fun clearCart() = EventCollector.sendEvent(Event.Action.Cart.ClearCart)

    fun continueWithCart() = EventCollector.sendEvent(Event.Action.Cart.PreviewCart)
}

class CartPreviewScope(
    val items: ReadOnlyDataSource<List<SellerCart>>,
    val total: ReadOnlyDataSource<Total?>,
    override val notifications: DataSource<ScopeNotification?> = DataSource(null),
) : Scope.Child.TabBar(), CommonScope.WithNotifications {

    fun placeOrder() = EventCollector.sendEvent(Event.Action.Cart.ConfirmCartOrder)

    object OrderWithQuotedItems : ScopeNotification {
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = false
        override val title: String? = null
        override val body: String = "order_with_quote_body"

        fun placeOrder() =
            EventCollector.sendEvent(Event.Action.Cart.PlaceCartOrder(checkForQuotedItems = false))
    }

    class OrderModified(val modified: List<SellerCart>) : ScopeNotification {
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = false
        override val title: String? = null
        override val body: String = "order_modified_body"

        fun placeOrder() =
            EventCollector.sendEvent(Event.Action.Cart.PlaceCartOrder(checkForQuotedItems = true))
    }
}

class CartOrderCompletedScope(
    val order: CartSubmitResponse,
    val items: List<SellerCart>,
    val total: Total,
) : Scope.Child.TabBar() {

    override val isRoot: Boolean = true

    fun goToOrders(): Boolean = EventCollector.sendEvent(Event.Transition.Orders)
}