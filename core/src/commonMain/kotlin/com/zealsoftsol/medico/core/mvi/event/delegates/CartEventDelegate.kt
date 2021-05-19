package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.nested.CartScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartIdentifier

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

    private suspend inline fun async(crossinline block: suspend () -> Unit) {
//        loadJob?.cancel()
//        loadJob = coroutineContext.toScope().launch {
        block()
//        }
    }
}