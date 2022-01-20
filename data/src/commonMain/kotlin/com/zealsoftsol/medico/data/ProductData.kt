package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val alternateProducts: List<AlternateProductData>,
    val product: ProductSearch?,
    val variants: List<ProductVariant>,
)

@Serializable
data class ProductVariant(
    val code: String,
    val name: String,
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
    val type: String,
)

@Serializable
data class ProductSeasonBoyRetailerSelectResponse(
    @SerialName("productData")
    val product: ProductSearch,
    val sellerInfo: SellerInfo? = null,
    @SerialName("sbRetailers")
    val retailers: List<SeasonBoyRetailer>,
)

@Serializable
data class SeasonBoyRetailer(
    @SerialName("seasonBoyRetailerId")
    val id: String? = null,
    override val tradeName: String,
    val geoData: GeoData,
    val cartInfo: CartInfo? = null,
) : WithTradeName