package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.CartOrderCompletedScope
import com.zealsoftsol.medico.core.mvi.scope.nested.CartPreviewScope
import com.zealsoftsol.medico.core.mvi.scope.nested.CartScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartIdentifier
import com.zealsoftsol.medico.data.CartItem
import com.zealsoftsol.medico.data.SellerCart

internal class CartEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val cartRepo: CartRepo,
) : EventDelegate<Event.Action.Cart>(navigator), CommonScope.CanGoBack {

    override suspend fun handleEvent(event: Event.Action.Cart) = when (event) {
        is Event.Action.Cart.AddItem -> event.run {
            if (quantity + freeQuantity > 0.0) {
                addItem(
                    sellerUnitCode,
                    productCode,
                    buyingOption,
                    id,
                    quantity,
                    freeQuantity,
                )
            } else if (id != null && sellerUnitCode != null) {
                removeItem(
                    sellerUnitCode,
                    productCode,
                    buyingOption,
                    id,
                    checkContains = true,
                )
            }
        }
        is Event.Action.Cart.UpdateItem -> event.run {
            updateItem(
                sellerUnitCode,
                productCode,
                buyingOption,
                id,
                quantity,
                freeQuantity,
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
        is Event.Action.Cart.LoadCart -> loadCart()
        is Event.Action.Cart.GetAlertToggle -> getAlertToggle()
        is Event.Action.Cart.ClearCart -> clearCart()
        is Event.Action.Cart.PreviewCart -> previewCart()
        is Event.Action.Cart.ConfirmCartOrder -> confirmCartOrder(event.cartScope)
        is Event.Action.Cart.PlaceCartOrder -> placeCartOrder(event.checkForQuotedItems)
        is Event.Action.Cart.OpenEditCartItem -> openCartItemBottomSheet(
            event.qtyInitial,
            event.freeQtyInitial,
            event.sellerCart,
            event.item,
            event.cartScope
        )
        is Event.Action.Cart.HideBackButton -> hideBackButton()
        is Event.Action.Cart.SubmitReward -> submitReward(event.rewardId)
    }

    /**
     * submit scratched reward to server
     */
    private suspend fun submitReward(rewardId: String) {
        navigator.withScope<CartOrderCompletedScope> {
            withProgress { cartRepo.submitReward(rewardId) }
                .onSuccess { _ ->
                    it.isOfferSwiped.value = true
                }.onError(navigator)
        }
    }

    /**
     * Hide back button on cart header
     */
    private fun hideBackButton() {
        navigator.withScope<CartScope> {
            it.updatePreviewStatus(false)
        }
    }

    private fun openCartItemBottomSheet(
        qtyInitial: Double,
        freeQtyInitial: Double,
        sellerCart: SellerCart,
        item: CartItem,
        cartScope: CartScope
    ) {
        navigator.scope.value.bottomSheet.value =
            BottomSheet.EditCartItem(qtyInitial, freeQtyInitial, sellerCart, item, cartScope)
    }

    private suspend fun addItem(
        sellerUnitCode: String?,
        productCode: String,
        buyingOption: BuyingOption,
        id: CartIdentifier?,
        quantity: Double,
        freeQuantity: Double,
    ) = async {
        navigator.withProgress {
            cartRepo.addCartItem(
                userRepo.requireUser().unitCode,
                sellerUnitCode,
                productCode,
                buyingOption,
                id,
                quantity,
                freeQuantity,
            )
        }.onSuccess { body ->
            EventCollector.sendEvent(Event.Action.Search.showToast("success", cartData = body))
//            navigator.setScope(
//                CartScope(
//                    items = ReadOnlyDataSource(cartRepo.entries),
//                    total = ReadOnlyDataSource(cartRepo.total),
//                    isContinueEnabled = ReadOnlyDataSource(cartRepo.isContinueEnabled),
//                    tapModeHelper = tapModeHelper,
//                )
//            )
        }.onError(navigator)
    }

    private suspend fun updateItem(
        sellerUnitCode: String,
        productCode: String,
        buyingOption: BuyingOption,
        id: CartIdentifier,
        quantity: Double,
        freeQuantity: Double,
    ) = async {
        navigator.withScope<CartScope> {
            withProgress {
                cartRepo.updateCartItem(
                    userRepo.requireUser().unitCode,
                    sellerUnitCode,
                    productCode,
                    buyingOption,
                    id,
                    quantity,
                    freeQuantity,
                )
            }.onError(navigator)
        }
    }

    private suspend fun removeItem(
        sellerUnitCode: String,
        productCode: String,
        buyingOption: BuyingOption,
        id: CartIdentifier,
        checkContains: Boolean = false,
    ) = async {
        if (!checkContains || cartRepo.entries.value.flatMap { it.items }
                .any { it.id.spid == id.spid }) {
            navigator.withScope<Scopable> {
                withProgress {
                    cartRepo.removeCartItem(
                        userRepo.requireUser().unitCode,
                        sellerUnitCode,
                        productCode,
                        buyingOption,
                        id,
                    )
                }.onError(navigator)
            }
        }
    }

    private suspend fun removeSellerItems(sellerUnitCode: String) = async {
        navigator.withScope<CartScope> {
            withProgress {
                cartRepo.removeSellerItems(userRepo.requireUser().unitCode, sellerUnitCode)
            }.onError(navigator)
        }
    }

    private suspend fun clearCart() = async {
        navigator.withScope<CartScope> {
            withProgress {
                cartRepo.clearCart(userRepo.requireUser().unitCode)
            }.onError(navigator)
        }
    }

    private fun previewCart() {
        navigator.withScope<CartScope> {
            setScope(CartPreviewScope(it.items, it.total))
        }
    }

    private suspend fun confirmCartOrder(scope: Scope) {
        if (scope is CartPreviewScope) {
            navigator.withScope<CartPreviewScope> {
                withProgress { cartRepo.confirmCart(userRepo.requireUser().unitCode) }
                    .onSuccess {
                        placeCartOrder(checkQuoted = true)
                    }.onError(navigator)
            }
        } else {
            navigator.withScope<CartScope> {
                withProgress { cartRepo.confirmCart(userRepo.requireUser().unitCode) }
                    .onSuccess {
                        placeCartOrderDirect()
                    }.onError(navigator)
            }
        }
    }

    private suspend fun placeCartOrderDirect() {
        navigator.withScope<CartScope> {
            withProgress { cartRepo.submitCart(userRepo.requireUser().unitCode) }
                .onSuccess { body ->
                    setScope(CartOrderCompletedScope(body, body.total))
                }.onError(navigator)

        }
    }

    private suspend fun placeCartOrder(checkQuoted: Boolean) {
        navigator.withScope<CartPreviewScope> {
            if (checkQuoted && it.items.flow.value.any { seller -> seller.items.any { item -> item.buyingOption == BuyingOption.QUOTE } }) {
                it.notifications.value = CartPreviewScope.OrderWithQuotedItems
            } else {
                it.dismissNotification()
                withProgress { cartRepo.submitCart(userRepo.requireUser().unitCode) }
                    .onSuccess { body ->
                        setScope(CartOrderCompletedScope(body, body.total))
                    }.onError(navigator)
            }
        }
    }

    private suspend fun loadCart() {
        cartRepo.loadCartFromServer(userRepo.requireUser().unitCode)
    }

    private suspend fun getAlertToggle() {
        navigator.withScope<CommonScope.AlertScope> {
            it.isOrderAlert.value = userRepo.getAlertToggle()
        }
    }

    private suspend inline fun async(crossinline block: suspend () -> Unit) {
//        loadJob?.cancel()
//        loadJob = coroutineContext.toScope().launch {
        block()
//        }
    }
}