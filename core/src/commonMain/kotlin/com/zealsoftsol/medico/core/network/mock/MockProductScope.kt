package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.ProductResponse
import com.zealsoftsol.medico.data.Response

class MockProductScope : NetworkScope.Product {

    override suspend fun getProductData(productCode: String): Response.Wrapped<ProductResponse> =
        mockResponse {
            Response.Wrapped(null, false)
        }
}