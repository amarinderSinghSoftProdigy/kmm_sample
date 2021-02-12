package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal interface ManagementItem

@Serializable
data class EntityInfo(
    override val tradeName: String,
    @SerialName("mobileNumber")
    override val phoneNumber: String,
    override val gstin: String,
    val panNumber: String,
    val unitCode: String,
    @SerialName("geoPoints")
    override val geoData: GeoData,
    val subscriptionData: SubscriptionData? = null,
    val seasonBoyRetailerData: SeasonBoyRetailerData? = null,
) : ManagementItem, PreviewItem

@Serializable
data class SeasonBoyRetailerData(
    val orders: Int,
)

@Serializable
data class SubscriptionData(
    @SerialName("subscribeStatus")
    val status: SubscriptionStatus,
    val paymentMethod: PaymentMethod,
    val noOfCreditDays: Int,
    val orders: Int,
)

@Serializable
data class GeoData(
    val location: String,
    val pincode: String,
    @SerialName("townOrCity")
    val city: String,
    val distance: String,
    @SerialName("originPoints")
    val origin: GeoPoints,
    @SerialName("destinationPoints")
    val destination: GeoPoints,
) {
    fun fullAddress() = "$location $pincode"
}

@Serializable
data class SubscribeRequest(
    val buyerUnitCode: String,
    val sellerUnitCode: String,
    val paymentMethod: String,
    val noOfCreditDays: Int,
    val customerType: String,
)