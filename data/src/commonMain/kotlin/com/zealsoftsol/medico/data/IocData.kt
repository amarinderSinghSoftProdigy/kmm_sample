package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IOCResponse(
    val totalResults: Int,
    val results: List<RetailerData>,
)


@Serializable
data class RetailerData(
    val unitCode: String,
    val tradeName: String,
    val cityOrTown: String,
    val pincode: String,
    val gstin: String,
    val panNumber: String,
    val drugLicenseNo1: String,
    val drugLicenseNo2: String,
    val paymentMethod: String,
    val customerType: String,
    val status: String
)


