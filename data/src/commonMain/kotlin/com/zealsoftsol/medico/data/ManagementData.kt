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
    val pincode: String,
    @SerialName("sellerPoints")
    val sellerGeoPoints: GeoPoints,
    @SerialName("townOrCity")
    val city: String,
    val traderName: String,
    val unitCode: String? = null,
    private val subscribeStatus: String? = null,
) : ManagementItem {

    fun getSubscriptionStatus() = when (subscribeStatus) {
        SubscriptionStatus.PENDING.serverValue -> SubscriptionStatus.PENDING
        SubscriptionStatus.SUBSCRIBED.serverValue -> SubscriptionStatus.SUBSCRIBED
        else -> null
    }
}

@Serializable
data class SubscribeRequest(
    val buyerUnitCode: String,
    val sellerUnitCode: String,
    val paymentMethod: String,
    val noOfCreditDays: Int,
    val customerType: String
)