package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.CartConfirmData
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.CartOrderRequest
import com.zealsoftsol.medico.data.CartRequest
import com.zealsoftsol.medico.data.CartSubmitResponse
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.Response

class MockCartScope : NetworkScope.Cart {

    init {
        "USING MOCK CART SCOPE".logIt()
    }

    override suspend fun getCart(unitCode: String): Response.Wrapped<CartData> = mockResponse {
        Response.Wrapped(null, false)
    }

    override suspend fun deleteCart(unitCode: String, cartId: String): Response.Wrapped<ErrorCode> =
        mockResponse {
            Response.Wrapped(null, false)
        }

    override suspend fun addCartEntry(request: CartRequest): Response.Wrapped<CartData> =
        mockResponse {
            Response.Wrapped(null, false)
        }

    override suspend fun updateCartEntry(request: CartRequest): Response.Wrapped<CartData> =
        mockResponse {
            Response.Wrapped(null, false)
        }

    override suspend fun deleteCartEntry(request: CartRequest): Response.Wrapped<CartData> =
        mockResponse {
            Response.Wrapped(null, false)
        }

    override suspend fun deleteSellerCart(
        unitCode: String,
        cartId: String,
        sellerUnitCode: String
    ): Response.Wrapped<CartData> = mockResponse {
        Response.Wrapped(null, false)
    }

    override suspend fun confirmCart(request: CartOrderRequest): Response.Wrapped<CartConfirmData> =
        mockResponse {
            Response.Wrapped(null, false)
        }

    override suspend fun submitCart(request: CartOrderRequest): Response.Wrapped<CartSubmitResponse> =
        mockResponse {
            Response.Wrapped(null, false)
        }
}