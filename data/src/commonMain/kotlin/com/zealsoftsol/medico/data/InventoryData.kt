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
    val id: String? = null,
    val vendorProductName: String? = null,
    val vendorMnfrName: String? = null,
    val spid: String? = null,
    val mrp: MrpData? = null,
    val ptr: MrpData? = null,
    val expiryDate: MrpData? = null,
    val availableQty: MrpData? = null,
    val stock: MrpData? = null,
    val batchNo: String? = null,
    val hsncode: String? = null,
    val manufacturerCode: String? = null,
    val mfgDate: String? = null,
    val warehouseUnitCode: String? = null,
    val warehouseCode: String? = null,
    val status: String? = null,
    val productCode: String? = null,
    val stockStatus: String? = null,
    val stockStatusCode: String? = null,
)


@Serializable
data class MrpData(
    val value: Double,
    val formattedValue: String
)