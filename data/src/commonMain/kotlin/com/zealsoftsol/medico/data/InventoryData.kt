package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class InventoryData(
    val stockStatusData: StocksStatusData?,
    val onlineStatusData: OnlineStatusData?,
    val stockExpiredData: StockExpiredData?,
    val manufacturers: List<ManufacturerData>?,
    val productData: List<ProductsData>?
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
    val code: String,
    val name: String,
    val count: Int
)

@Serializable
data class ProductsData(
    val id: String,
    val vendorProductName: String,
    val manufacturerCode: String,
    val variantProductCode: String,
    val vendorMnfrName: String,
    val b2bUnit: String,
    val batchNo: String,
    val inventoryStatus: String,
    val stockName: String,
    val hsnCode: String,
    val vendorStdUnit: String,
    val vendorProductId: String,
    val vendorMnfrId:String,
    val promotionActive: Boolean,
    val offerAllUsers: Boolean,
    val promoTypeCode: String,
    val promoCode: String,
    val promoStock: Double,
    val validFrom: Double,
    val productDiscount: Double,
    val validUntil: Double,
    val spid: String,
    val mrp: MrpData,
    val ptr: MrpData,
    val pts: MrpData,
    val expiryDate: MrpData,
    val availableQty: MrpData,
    val freeQty: MrpData,
    val promoBuy: MrpData,
    val promoFree: MrpData,
)

@Serializable
data class MrpData(
    val value: Double,
    val formattedValue: String
)