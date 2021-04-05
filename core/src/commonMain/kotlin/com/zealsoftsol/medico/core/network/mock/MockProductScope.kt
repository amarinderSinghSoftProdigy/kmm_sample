package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.ProductBuyResponse
import com.zealsoftsol.medico.data.ProductResponse
import com.zealsoftsol.medico.data.Response

class MockProductScope : NetworkScope.Product {

    init {
        "USING MOCK PRODUCT SCOPE".logIt()
    }

    override suspend fun getProductData(productCode: String): Response.Wrapped<ProductResponse> =
        mockResponse {
            Response.Wrapped(null, false)
        }

    override suspend fun buyProductInfo(productCode: String): Response.Wrapped<ProductBuyResponse> =
        mockResponse {
            Response.Wrapped(null, false)
        }
}