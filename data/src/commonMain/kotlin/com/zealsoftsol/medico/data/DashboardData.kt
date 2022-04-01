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
    val brands: List<BrandsData> = emptyList(),
    val categories: List<BrandsData> = emptyList(),
    val banners: List<BannerData> = emptyList(),
    val offers: List<OffersData> = emptyList(),
)

@Serializable
data class OffersData(
    val total: Int, val status: OfferStatus
)


enum class OfferStatus {
    CREATED, ENDED, RUNNING
}

@Serializable
data class BannerData(
    val cdnUrl: String, val name: String? = null
)

@Serializable
data class BrandsData(
    val imageUrl: String, val field: String, val searchTerm: String, val name: String? = null
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
    val totalSubscribed: Int,
    val connected: Int,
    val pending: Int,
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
) {
    val isSkeletonItem: Boolean
        get() = productName == "__skeleton__"

    companion object {
        val skeleton = ProductSold(-1, "__skeleton__")
    }
}