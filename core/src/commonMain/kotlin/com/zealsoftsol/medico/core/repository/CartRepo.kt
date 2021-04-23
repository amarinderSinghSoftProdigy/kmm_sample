package com.zealsoftsol.medico.core.repository

import com.zealsoftsol.medico.core.interop.ReadOnlyDataSource
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.BuyingOption
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.CartRequest
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.Response
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

    private var cartId = ""

    suspend fun loadCartFromServer(unitCode: String) {
        cartStoresScope.getCart(unitCode).handleResponse()
    }

    suspend fun addCartItem(
        unitCode: String,
        sellerUnitCode: String,
        productCode: String,
        buyingOption: BuyingOption,
        spid: String,
        quantity: Int,
    ) = cartStoresScope.addToCart(
        CartRequest(
            unitCode,
            sellerUnitCode,
            productCode,
            buyingOption,
            spid,
            quantity
        )
    )
        .handleResponse()

    suspend fun updateCartItem(
        unitCode: String,
        sellerUnitCode: String,
        productCode: String,
        buyingOption: BuyingOption,
        spid: String,
        quantity: Int,
    ) = cartStoresScope.updateCartEntry(
        CartRequest(
            unitCode,
            sellerUnitCode,
            productCode,
            buyingOption,
            spid,
            quantity
        )
    )
        .handleResponse()

    suspend fun removeCartItem(
        unitCode: String,
        sellerUnitCode: String,
        productCode: String,
        buyingOption: BuyingOption,
        spid: String,
    ) = cartStoresScope.deleteCartEntry(
        CartRequest(
            unitCode,
            sellerUnitCode,
            productCode,
            buyingOption,
            spid
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
            cartId = ""
            entries.value = emptyList()
            total.value = null
        }
    }.entity

    private inline fun Response.Wrapped<CartData>.handleResponse(): ErrorCode? {
        return if (isSuccess && entity != null) {
            val cartData = entity!!
            cartId = cartData.cartId
            entries.value = cartData.sellerCarts
            total.value = cartData.total
            null
        } else {
            ErrorCode()
        }
    }
}

internal inline fun CartRepo.getEntriesCountDataSource(): ReadOnlyDataSource<Int> =
    ReadOnlyDataSource(
        total.map { it?.itemCount ?: 0 }.stateIn(GlobalScope, SharingStarted.Eagerly, 0)
    )