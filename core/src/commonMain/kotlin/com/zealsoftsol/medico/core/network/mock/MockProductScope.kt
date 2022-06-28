package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.BodyResponse
import com.zealsoftsol.medico.data.ProductBuyResponse
import com.zealsoftsol.medico.data.ProductRequestResponse
import com.zealsoftsol.medico.data.ProductResponse
import com.zealsoftsol.medico.data.ProductSeasonBoyRetailerSelectResponse

class MockProductScope : NetworkScope.Product {

    init {
        "USING MOCK PRODUCT SCOPE".logIt()
    }

    override suspend fun getProductData(productCode: String) =
        mockResponse<ProductResponse> {
            null
        }

    override suspend fun buyProductInfo(productCode: String, latitude: Double, longitude: Double) =
        mockResponse<ProductBuyResponse> {
            null
        }

    override suspend fun buyProductSelectSeasonBoyRetailer(
        productCode: String,
        unitCode: String,
        sellerUnitCode: String?,
        latitude: Double,
        longitude: Double
    ) = mockResponse<ProductSeasonBoyRetailerSelectResponse> {
        null
    }

    override suspend fun getQuotedProductData(productCode: String) =
        mockResponse<ProductBuyResponse> {
            null
        }

    override suspend fun getRequestedProductData(productCode: String): BodyResponse<ProductRequestResponse> {
        TODO("Not yet implemented")
    }
}