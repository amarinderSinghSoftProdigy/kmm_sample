package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface ManagementItem

@Serializable
data class EntityInfo(
    @SerialName("buyerPoints")
    val buyerGeoPoints: GeoPoints,
    override val distance: String,
    override val gstin: String,
    override val location: String,
    val panNumber: String,
    @SerialName("mobileNumber")
    override val phoneNumber: String,
    val pincode: String,
    @SerialName("sellerPoints")
    val sellerGeoPoints: GeoPoints,
    @SerialName("townOrCity")
    override val city: String,
    override val traderName: String,
    val unitCode: String,
    val subscriptionData: SubscriptionData? = null,
) : ManagementItem, PreviewItem {

    override val geo: GeoPoints
        get() = sellerGeoPoints
}

@Serializable
data class SubscribeRequest(
    val buyerUnitCode: String,
    val sellerUnitCode: String,
    val paymentMethod: String,
    val noOfCreditDays: Int,
    val customerType: String
)

@Serializable
data class SubscriptionData(
    @SerialName("subscribeStatus")
    val status: SubscriptionStatus,
    val paymentMethod: PaymentMethod,
    val noOfCreditDays: Int,
    val orders: String,
)