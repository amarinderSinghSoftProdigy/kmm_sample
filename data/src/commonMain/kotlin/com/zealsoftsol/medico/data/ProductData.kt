package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val alternateProducts: List<AlternateProductData>,
    val product: ProductSearch?,
)

@Serializable
data class AlternateProductData(
    @SerialName("displayName")
    val name: String,
    @SerialName("queryId")
    val query: String,
    val baseProductName: String,
    val priceRange: String,
    val manufacturerName: String,
    val availableVariants: String,
)

@Serializable
data class ProductBuyResponse(
    @SerialName("productData")
    val product: ProductSearch,
    val sellerInfo: List<SellerInfo>,
)

@Serializable
data class ProductSeasonBoyRetailerSelectResponse(
    @SerialName("productData")
    val product: ProductSearch,
    val sellerInfo: SellerInfo,
    @SerialName("sbRetailers")
    val retailers: List<SeasonBoyRetailer>,
)

@Serializable
data class SeasonBoyRetailer(
    @SerialName("seasonBoyRetailerId")
    val id: String? = null,
    override val tradeName: String,
    @SerialName("cityOrTown")
    val city: String,
    val location: String,
    val pincode: String,
) : WithTradeName {
    fun fullAddress() = "$city $pincode"
}