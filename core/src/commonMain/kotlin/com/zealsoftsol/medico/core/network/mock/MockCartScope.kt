package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.CartConfirmData
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.CartOrderRequest
import com.zealsoftsol.medico.data.CartRequest
import com.zealsoftsol.medico.data.CartSubmitResponse

class MockCartScope : NetworkScope.Cart {

    init {
        "USING MOCK CART SCOPE".logIt()
    }

    override suspend fun getCart(unitCode: String) = mockResponse<CartData> {
        null
    }

    override suspend fun deleteCart(unitCode: String, cartId: String) =
        mockResponse {
            mockEmptyMapBody()
        }

    override suspend fun addCartEntry(request: CartRequest) =
        mockResponse<CartData> {
            null
        }

    override suspend fun updateCartEntry(request: CartRequest) =
        mockResponse<CartData> {
            null
        }

    override suspend fun deleteCartEntry(request: CartRequest) =
        mockResponse<CartData> {
            null
        }

    override suspend fun deleteSellerCart(
        unitCode: String,
        cartId: String,
        sellerUnitCode: String
    ) = mockResponse<CartData> {
        null
    }

    override suspend fun confirmCart(request: CartOrderRequest) =
        mockResponse<CartConfirmData> {
            null
        }

    override suspend fun submitCart(request: CartOrderRequest) =
        mockResponse<CartSubmitResponse> {
            null
        }
}