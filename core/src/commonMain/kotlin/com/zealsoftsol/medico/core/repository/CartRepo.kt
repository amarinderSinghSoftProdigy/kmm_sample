package com.zealsoftsol.medico.core.repository

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.BodyResponse
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartConfirmData
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.CartIdentifier
import com.zealsoftsol.medico.data.CartOrderRequest
import com.zealsoftsol.medico.data.CartRequest
import com.zealsoftsol.medico.data.CartSubmitResponse
import com.zealsoftsol.medico.data.SellerCart
import com.zealsoftsol.medico.data.Total
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CartRepo(
    private val cartStoresScope: NetworkScope.Cart,
) {
    val entries: MutableStateFlow<List<SellerCart>> = MutableStateFlow(emptyList())
    val total: MutableStateFlow<Total?> = MutableStateFlow(null)
    val isContinueEnabled = MutableStateFlow(false)

    private var cartId = ""

    suspend fun loadCartFromServer(unitCode: String) {
        cartStoresScope.getCart(unitCode).handleResponse()
    }

    suspend fun addCartItem(
        unitCode: String,
        sellerUnitCode: String?,
        productCode: String,
        buyingOption: BuyingOption,
        id: CartIdentifier?,
        quantity: Double,
        freeQuantity: Double,
    ) = cartStoresScope.addCartEntry(
        CartRequest(
            unitCode,
            sellerUnitCode,
            productCode,
            buyingOption,
            id,
            quantity,
            freeQuantity,
        )
    )
        .handleResponse()

    suspend fun updateCartItem(
        unitCode: String,
        sellerUnitCode: String,
        productCode: String,
        buyingOption: BuyingOption,
        id: CartIdentifier,
        quantity: Double,
        freeQuantity: Double,
    ) = cartStoresScope.updateCartEntry(
        CartRequest(
            unitCode,
            sellerUnitCode,
            productCode,
            buyingOption,
            id,
            quantity,
            freeQuantity,
        )
    )
        .handleResponse()

    suspend fun removeCartItem(
        unitCode: String,
        sellerUnitCode: String,
        productCode: String,
        buyingOption: BuyingOption,
        id: CartIdentifier,
    ) = cartStoresScope.deleteCartEntry(
        CartRequest(
            unitCode,
            sellerUnitCode,
            productCode,
            buyingOption,
            id,
        )
    )
        .handleResponse()

    suspend fun removeSellerItems(
        unitCode: String,
        sellerUnitCode: String,
    ) = cartStoresScope.deleteSellerCart(unitCode, cartId, sellerUnitCode)
        .handleResponse()

    suspend fun clearCart(
        unitCode: String,
    ) = cartStoresScope.deleteCart(unitCode, cartId).also {
        if (it.isSuccess) {
            clearCartLocally()
        }
    }

    suspend fun confirmCart(unitCode: String): BodyResponse<CartConfirmData> {
        val result = cartStoresScope.confirmCart(CartOrderRequest(cartId, unitCode))
        BodyResponse(body = result.getBodyOrNull()?.cartData, type = "").handleResponse()
        return result
    }

    suspend fun submitCart(unitCode: String): BodyResponse<CartSubmitResponse> {
        val result = cartStoresScope.submitCart(CartOrderRequest(cartId, unitCode))
        if (result.getBodyOrNull() != null) {
            clearCartLocally()
        }
        return result
    }

    private fun clearCartLocally() {
        cartId = ""
        entries.value = emptyList()
        total.value = null
    }

    private inline fun BodyResponse<CartData>.handleResponse(): BodyResponse<CartData> {
        val body = getBodyOrNull()
        if (body != null) {
            cartId = body.cartId
            entries.value = body.sellerCarts
            total.value = body.total
            isContinueEnabled.value =
                body.sellerCarts.flatMap { it.items }.none { it.quotedData?.isAvailable == false }
        }
        return this
    }
}

internal inline fun CartRepo.getEntriesCountDataSource(): ReadOnlyDataSource<Int> =
    ReadOnlyDataSource(
        total.map { it?.itemCount ?: 0 }.stateIn(GlobalScope, SharingStarted.Eagerly, 0)
    )