package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface ManagementItem

@Serializable
data class EntityInfo(
    @SerialName("buyerPoints")
    val buyerGeoPoints: GeoPoints,
    val distance: String,
    val gstin: String,
    val location: String,
    val panNumber: String,
    @SerialName("mobileNumber")
    val phoneNumber: String,
    val pincode: String,
    @SerialName("sellerPoints")
    val sellerGeoPoints: GeoPoints,
    @SerialName("townOrCity")
    val city: String,
    val traderName: String,
    val unitCode: String,
    val subscriptionData: SubscriptionData? = null,
) : ManagementItem

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