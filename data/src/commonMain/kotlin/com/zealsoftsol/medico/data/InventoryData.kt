package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class InventoryData(
    val stockStatusData: StocksStatusData?,
    val onlineStatusData: OnlineStatusData?,
    val stockExpiredData: StockExpiredData?
)

@Serializable
data class StocksStatusData(
    val inStock: Int,
    val outOfStock : Int,
    val limitedStock : Int
)

@Serializable
data class OnlineStatusData(
    val onlineProductsCount: Int,
    val offlineProductCount: Int
)

@Serializable
data class StockExpiredData(
    val expired: Int,
    val lessThan6Months : Int,
    val moreThan6Months: Int
)