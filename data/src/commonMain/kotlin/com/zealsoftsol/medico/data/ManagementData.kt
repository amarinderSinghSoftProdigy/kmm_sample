package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal interface ManagementItem

@Serializable
data class EntityInfo(
    val tradeName: String,
    @SerialName("mobileNumber")
    val phoneNumber: String?,
    val gstin: String?,
    val panNumber: String?,
    val unitCode: String,
    @SerialName("geoPoints")
    val geoData: GeoData,
    val isVerified: Boolean? = null,
    val subscriptionData: SubscriptionData? = null,
    val seasonBoyRetailerData: SeasonBoyRetailerData? = null,
    val seasonBoyData: SeasonBoyData? = null,
    val drugLicenseNo1: String? = null,
    val drugLicenseNo2: String? = null,
    val tradeNameUrl: String?=null,
    ) : ManagementItem

@Serializable
data class SeasonBoyData(
    val retailers: Int,
    val email: String,
)

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
    val notificationId : String = ""
)

@Serializable
data class SubscribeRequest(
    val buyerUnitCode: String,
    val sellerUnitCode: String,
    val paymentMethod: String,
    val noOfCreditDays: Int,
    val customerType: String,
)