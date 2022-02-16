package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class InventoryData(
    val stockStatusData: StocksStatusData?,
    val onlineStatusData: OnlineStatusData?,
    val stockExpiredData: StockExpiredData?,
    val manufacturers: List<ManufacturerData>,
    val results: List<ProductsData>,
    val totalResults: Int
)

@Serializable
data class StocksStatusData(
    val inStock: Int,
    val outOfStock: Int,
    val limitedStock: Int
)

@Serializable
data class OnlineStatusData(
    val onlineProductsCount: Int,
    val offlineProductCount: Int
)

@Serializable
data class StockExpiredData(
    val expired: Int,
    val lessThan6Months: Int,
    val moreThan6Months: Int
)

@Serializable
data class ManufacturerData(
    var isChecked: Boolean = false,
    val code: String,
    val name: String,
    val count: Double
)

@Serializable
data class ProductsData(
    val id: String,
    val vendorProductName: String,
    val vendorMnfrName: String,
    val spid: String,
    val mrp: MrpData,
    val ptr: MrpData,
    val expiryDate: MrpData,
    val availableQty: MrpData,
)

@Serializable
data class MrpData(
    val value: Double,
    val formattedValue: String
)