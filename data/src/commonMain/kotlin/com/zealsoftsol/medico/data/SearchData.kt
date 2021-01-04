package com.zealsoftsol.medico.data

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
    val baseProduct: String,
    val baseProductId: String,
    val composition: List<String>,
    val drugType: String,
    val drugTypeId: String,
    val formattedMrp: String,
    val formattedPrice: String,
    val hsnCode: String,
    val hsnPercentage: String,
    val id: String,
    val manufacturer: String,
    val manufacturerId: String,
    val medicineId: String,
    val mrp: Double,
    val name: String,
    val packageForm: String,
    val price: Double,
    val productCode: String,
    val ptrPercentage: String,
    val shortName: String,
    val uom: String,
    val uomId: String,
)

data class Filter(
    val name: String,
    val queryName: String,
    val options: List<Option<String>>,
) {
    companion object Ids {
        const val MANUFACTURER_ID = "manufacturers"
    }
}

data class Option<T>(val value: T, val isSelected: Boolean)