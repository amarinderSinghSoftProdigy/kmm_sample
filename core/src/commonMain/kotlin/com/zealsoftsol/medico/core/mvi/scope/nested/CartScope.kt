package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.CartItem
import com.zealsoftsol.medico.data.CartSubmitResponse
import com.zealsoftsol.medico.data.SellerCart
import com.zealsoftsol.medico.data.Total

class CartScope(
    val items: ReadOnlyDataSource<List<SellerCart>>,
    val total: ReadOnlyDataSource<Total?>,
    val isContinueEnabled: ReadOnlyDataSource<Boolean>,
    val unreadNotifications: ReadOnlyDataSource<Int>,
    val cartCount: ReadOnlyDataSource<Int>,
) : Scope.Child.TabBar(), CommonScope.CanGoBack {

    val isPreviewEnabled: DataSource<Boolean> = DataSource(false)
    private val isBackButtonEnabled = DataSource(false)

    fun updatePreviewStatus(boolean: Boolean) {
        isPreviewEnabled.value = boolean
        isBackButtonEnabled.value = boolean
    }


    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) = TabBarInfo.NoIconTitle(
        "", unreadNotifications, cartCount, isBackButtonEnabled
    )

    fun placeOrder(scope: Scope) =
        EventCollector.sendEvent(Event.Action.Cart.ConfirmCartOrder(scope))

    init {
        EventCollector.sendEvent(Event.Action.Cart.LoadCart)
    }

    fun openBottomSheet(
        qtyInitial: Double,
        freeQtyInitial: Double,
        sellerCart: SellerCart,
        item: CartItem,
        cartScope: CartScope
    ) =
        EventCollector.sendEvent(
            Event.Action.Cart.OpenEditCartItem(
                qtyInitial,
                freeQtyInitial,
                sellerCart,
                item,
                cartScope
            )
        )

    fun updateItemCount(
        sellerCart: SellerCart,
        item: CartItem,
        quantity: Double,
        freeQuantity: Double
    ): Boolean {
        if (quantity < 0 || freeQuantity < 0) return false
        return if (quantity == 0.0 && freeQuantity == 0.0) {
            removeItem(sellerCart, item)
        } else {
            EventCollector.sendEvent(
                Event.Action.Cart.UpdateItem(
                    sellerCart.sellerCode,
                    item.productCode,
                    item.buyingOption,
                    item.id,
                    quantity,
                    freeQuantity,
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

    fun placeOrder(scope: Scope) =
        EventCollector.sendEvent(Event.Action.Cart.ConfirmCartOrder(scope))

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
    val total: Total,
) : Scope.Child.TabBar() {

    override val isRoot: Boolean = true

    fun goToOrders(): Boolean = EventCollector.sendEvent(Event.Transition.Orders)
}