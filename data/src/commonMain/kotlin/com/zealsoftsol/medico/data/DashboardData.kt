package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardData(
    @SerialName("connectedUserData")
    val userData: ConnectedUserData,
    @SerialName("notificationCount")
    val notificationsCount: Int,
    val ordersCount: Int,
    @SerialName("recentProductInfo")
    val productInfo: RecentProductInfo? = null,
    val stockStatusData: StockStatusData? = null,
)

@Serializable
data class ConnectedUserData(
    val stockist: CountData,
    val retailer: CountData? = null,
    val hospital: CountData? = null,
    val seasonBoy: CountData? = null,
)

@Serializable
data class CountData(
    val connected: Int,
    val pending: Int
)

@Serializable
data class StockStatusData(
    val inStock: Int,
    val limitedStock: Int,
    val outOfStock: Int
)

@Serializable
data class RecentProductInfo(
    @SerialName("mostPurchasedProducts")
    val mostPurchased: List<ProductSold>,
    @SerialName("mostSearchedProducts")
    val mostSearched: List<ProductSold>,
    @SerialName("mostSoldProducts")
    val mostSold: List<ProductSold>
)

@Serializable
data class ProductSold(
    val count: Int,
    val productName: String
)