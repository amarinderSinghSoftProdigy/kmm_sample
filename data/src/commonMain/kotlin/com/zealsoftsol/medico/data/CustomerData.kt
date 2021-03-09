package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomerData(
    val aadhaarCardNo: String?,
    val customerAddressData: CustomerAddressData,
    val customerMetaData: CustomerMetaData?,
    val customerType: String,
    val drugLicenseNo1: String?,
    val drugLicenseNo2: String?,
    val drugLicenseUrl: String?,
    val isDocumentUploaded: Boolean,
    val email: String,
    val firstName: String,
    val gstin: String?,
    val lastName: String,
    @SerialName("mobileNumber")
    val phoneNumber: String,
    val panNumber: String?,
    val tradeName: String,
    val unitCode: String?,
)

@Serializable
data class CustomerAddressData(
    @SerialName("addressLine1")
    val address: String,
    @SerialName("cityTown")
    val city: String,
    val district: String,
    @SerialName("latitidue")
    val latitude: Double,
    val location: String,
    val longitude: Double,
    val pincode: Int,
    val placeId: String?,
    val state: String,
)

@Serializable
data class CustomerMetaData(
    val activated: Boolean,
    val pageType: String,
    val userRole: String,
)