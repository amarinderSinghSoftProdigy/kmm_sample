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
    val subscribeStatus: String? = null,
) : ManagementItem