package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.nested.CartOrderCompletedScope
import com.zealsoftsol.medico.core.mvi.scope.nested.CartPreviewScope
import com.zealsoftsol.medico.core.mvi.scope.nested.CartScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartIdentifier
import com.zealsoftsol.medico.data.ErrorCode

internal class CartEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val cartRepo: CartRepo,
) : EventDelegate<Event.Action.Cart>(navigator), CommonScope.CanGoBack {

    override suspend fun handleEvent(event: Event.Action.Cart) = when (event) {
        is Event.Action.Cart.AddItem -> event.run {
            addItem(
                sellerUnitCode,
                productCode,
                buyingOption,
                id,
                quantity
            )
        }
        is Event.Action.Cart.UpdateItem -> event.run {
            updateItem(
                sellerUnitCode,
                productCode,
                buyingOption,
                id,
                quantity
            )
        }
        is Event.Action.Cart.RemoveItem -> event.run {
            removeItem(
                sellerUnitCode,
                productCode,
                buyingOption,
                id,
            )
        }
        is Event.Action.Cart.RemoveSellerItems -> event.run { removeSellerItems(sellerUnitCode) }
        is Event.Action.Cart.ClearCart -> clearCart()
        is Event.Action.Cart.PreviewCart -> previewCart()
        is Event.Action.Cart.ConfirmCartOrder -> confirmCartOrder()
        is Event.Action.Cart.PlaceCartOrder -> placeCartOrder(event.checkForQuotedItems)
    }

    private suspend fun addItem(
        sellerUnitCode: String?,
        productCode: String,
        buyingOption: BuyingOption,
        id: CartIdentifier?,
        quantity: Int,
    ) = async {
        val error = navigator.withProgress {
            cartRepo.addCartItem(
                userRepo.requireUser().unitCode,
                sellerUnitCode,
                productCode,
                buyingOption,
                id,
                quantity,
            )
        }
        if (error == null) {
            navigator.setScope(
                CartScope(
                    items = ReadOnlyDataSource(cartRepo.entries),
                    total = ReadOnlyDataSource(cartRepo.total),
                )
            )
        } else {
            navigator.setHostError(error)
        }
    }

    private suspend fun updateItem(
        sellerUnitCode: String,
        productCode: String,
        buyingOption: BuyingOption,
        id: CartIdentifier,
        quantity: Int,
    ) = async {
        navigator.withScope<CartScope> {
            cartRepo.updateCartItem(
                userRepo.requireUser().unitCode,
                sellerUnitCode,
                productCode,
                buyingOption,
                id,
                quantity
            )?.let { setHostError(it) }
        }
    }

    private suspend fun removeItem(
        sellerUnitCode: String,
        productCode: String,
        buyingOption: BuyingOption,
        id: CartIdentifier,
    ) = async {
        navigator.withScope<CartScope> {
            cartRepo.removeCartItem(
                userRepo.requireUser().unitCode,
                sellerUnitCode,
                productCode,
                buyingOption,
                id,
            )?.let { setHostError(it) }
        }
    }

    private suspend fun removeSellerItems(sellerUnitCode: String) = async {
        navigator.withScope<CartScope> {
            cartRepo.removeSellerItems(userRepo.requireUser().unitCode, sellerUnitCode)
                ?.let { setHostError(it) }
        }
    }

    private suspend fun clearCart() = async {
        navigator.withScope<CartScope> {
            cartRepo.clearCart(userRepo.requireUser().unitCode)?.let { setHostError(it) }
        }
    }

    private fun previewCart() {
        navigator.withScope<CartScope> {
            setScope(CartPreviewScope(it.items, it.total))
        }
    }

    private suspend fun confirmCartOrder() {
        navigator.withScope<CartPreviewScope> {
            val modifiedEntries =
                withProgress { cartRepo.confirmCart(userRepo.requireUser().unitCode) }
            if (modifiedEntries != null) {
                if (modifiedEntries.isEmpty()) {
                    placeCartOrder(checkQuoted = true)
                } else {
                    it.notifications.value = CartPreviewScope.OrderModified(modifiedEntries)
                }
            } else {
                setHostError(ErrorCode())
            }
        }
    }

    private suspend fun placeCartOrder(checkQuoted: Boolean) {
        navigator.withScope<CartPreviewScope> {
            if (checkQuoted && it.items.flow.value.any { seller -> seller.items.any { item -> item.buyingOption == BuyingOption.QUOTE } }) {
                it.notifications.value = CartPreviewScope.OrderWithQuotedItems
            } else {
                it.dismissNotification()
                val response = withProgress { cartRepo.submitCart(userRepo.requireUser().unitCode) }
                if (response != null) {
                    setScope(CartOrderCompletedScope(response, it.items.flow.value, response.total))
                } else {
                    setHostError(ErrorCode())
                }
            }
        }
    }

    private suspend inline fun async(crossinline block: suspend () -> Unit) {
//        loadJob?.cancel()
//        loadJob = coroutineContext.toScope().launch {
        block()
//        }
    }
}