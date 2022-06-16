package com.zealsoftsol.medico.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Store(
    val distance: Double,
    val formattedDistance: String,
    val gstin: String = "",
    val location: String = "",
    val manufacturersInCatalogue: Int = 0,
    val mobileNumber: String = "",
    val panNumber: String = "",
    val paymentMethod: String = "",
    val pincode: String = "",
    val productsInCatalogue: Int = 0,
    val sellerUnitCode: String,
    @SerialName("subscribeStatus")
    val status: SubscriptionStatus? = null,
    @SerialName("townOrCity")
    val city: String = "",
    val tradeName: String = "",
    val tradeNameUrl: String? = null,
    @SerialName("unitGeoPoints")
    val geoPoints: GeoPoints? = null,
    val drugLicenseNo1: String? = null,
    val drugLicenseNo2: String? = null
) {
    fun fullAddress() = "$city $pincode"
}