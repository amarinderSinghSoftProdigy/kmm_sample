package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*@Serializable
data class ProductRequestResponse(
    val body: ProductRequest? = null,
    val type: String? = null
)*/

@Serializable
data class ProductRequestResponse(
    val results: List<ProductSearch>,
    val totalResults: Int
)

@Serializable
data class ProductRequested(
    val baseProductName: String,
    val buyingOption: String,
    val code: String,
    val compositions: List<String>,
    val drugFormName: String,
    val formattedMrp: String,
    val formattedPrice: String,
    val hsnCode: String,
    val hsnPercentValue: String,
    val id: String,
    val imageCode: String,
    val manufacturer: String,
    val manufacturerId: String,
    val marginPercent: String,
    val mnfDivisionName: String,
    val mrp: Double,
    val name: String,
    val prescriptionRequired: Boolean,
    val price: Double,
    val productCategoryName: String,
    val scheduleCodeName: String,
    val sellerInfo: SellerInfo? = null,
    val shortName: String,
    val sourceType: String,
    val standardUnit: String,
    val stockInfo: StockInfo? = null,
    val uomName: String,
    val vendorMnfrId: String,
    val vendorProductId: String,
    val viewStockist: List<ConnectedStockist>
)