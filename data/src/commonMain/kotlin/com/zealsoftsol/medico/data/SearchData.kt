package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val facets: List<Facet>,
    val products: List<ProductSearch>,
    val totalResults: Int,
)

@Serializable
data class Facet(
    val displayName: String,
    val queryId: String,
    val values: List<Value>,
)

@Serializable
data class Value(
    val count: Int,
    val value: String,
)

@Serializable
data class ProductSearch(
    val buyingOption: BuyingOption,
    val compositions: List<String>,
    val formattedMrp: String,
    val formattedPrice: String?,
    val id: String,
    val manufacturer: String,
    val manufacturerId: String,
    val marginPercent: String?,
    val name: String,
    val productCategoryName: String,
    val shortName: String,
    val code: String,
    val stockInfo: StockInfo?,
    val uomName: String,
)

@Serializable
data class SellerInfo(
    val address: Address,
    val tradeName: String,
    val unitCode: String,
    val stockInfo: StockInfo,
)

@Serializable
data class Address(
    val distance: Double,
    val formattedDistance: String,
    val location: String,
    @SerialName("originPoints")
    val geoPoints: GeoPoints,
    val pincode: String,
    @SerialName("townOrCity")
    val city: String,
)

@Serializable
data class StockInfo(
    val availableQty: Int,
    val expireDate: String,
    val formattedStatus: String,
    val status: StockStatus,
)

enum class StockStatus {
    LOW_STOCK,
    IN_STOCK,
    OUT_OF_STOCK;
}

enum class BuyingOption {
    BUY,
    QUOTE;
}

@Serializable
data class AutoComplete(
    val query: String,
    @SerialName("suggester")
    val details: String,
    val suggestion: String,
)

data class Filter(
    val name: String,
    val queryId: String,
    val options: List<Option>,
)

sealed class Option {

    data class StringValue(
        val value: String,
        val isSelected: Boolean,
        val isVisible: Boolean = true
    ) : Option()

    object ViewMore : Option()
}