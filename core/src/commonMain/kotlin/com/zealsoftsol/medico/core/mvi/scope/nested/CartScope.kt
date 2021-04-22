package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.data.CartItem
import com.zealsoftsol.medico.data.SellerCart
import com.zealsoftsol.medico.data.Total

class CartScope(
    val items: ReadOnlyDataSource<List<SellerCart>>,
    val total: ReadOnlyDataSource<Total?>,
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
                    item.spid,
                    quantity.coerceIn(0, item.quantity.value.toInt())
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
                item.spid
            )
        )

    fun removeSellerItems(sellerCart: SellerCart) =
        EventCollector.sendEvent(Event.Action.Cart.RemoveSellerItems(sellerCart.sellerCode))

    fun clearCart() = EventCollector.sendEvent(Event.Action.Cart.ClearCart)

    fun continueWithCart(): Boolean = TODO("not implemented")
}