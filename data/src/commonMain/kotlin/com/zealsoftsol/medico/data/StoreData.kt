package com.zealsoftsol.medico.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Store(
    val distance: Double,
    val formattedDistance: String,
    val gstin: String,
    val location: String,
    val manufacturersInCatalogue: Int,
    val mobileNumber: String,
    val panNumber: String,
    val paymentMethod: String,
    val pincode: String,
    val productsInCatalogue: Int,
    val sellerUnitCode: String,
    @SerialName("subscribeStatus")
    val status: SubscriptionStatus,
    @SerialName("townOrCity")
    val city: String,
    val tradeName: String,
    @SerialName("unitGeoPoints")
    val geoPoints: GeoPoints,
    val drugLicenseNo1: String? = null,
    val drugLicenseNo2: String? = null
) {
    fun fullAddress() = "$city $pincode"
}