package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomerData(
    @SerialName("aadhaarCardNo")
    val aadhaarCardNo: String,
    @SerialName("customerAddressData")
    val customerAddressData: CustomerAddressData,
    @SerialName("customerMetaData")
    val customerMetaData: CustomerMetaData,
    @SerialName("customerType")
    val customerType: String,
    @SerialName("customerTypeLogo")
    val customerTypeLogo: String,
    @SerialName("drugLicenseNo1")
    val drugLicenseNo1: String,
    @SerialName("drugLicenseNo2")
    val drugLicenseNo2: String,
    @SerialName("drugLicenseUrl")
    val drugLicenseUrl: String?,
    val isDocumentUploaded: Boolean,
    @SerialName("email")
    val email: String,
    @SerialName("firstName")
    val firstName: String,
    @SerialName("gstin")
    val gstin: String,
    @SerialName("lastName")
    val lastName: String,
    @SerialName("medicoStoresLogo")
    val medicoStoresLogo: String,
    @SerialName("message")
    val message: String,
    @SerialName("mobileNumber")
    val phoneNumber: String,
    @SerialName("panNumber")
    val panNumber: String,
    @SerialName("traderFooter")
    val traderFooter: String,
    @SerialName("traderHeader")
    val traderHeader: String,
    @SerialName("traderName")
    val traderName: String
)

@Serializable
data class CustomerAddressData(
    @SerialName("addressLine1")
    val address: String,
    @SerialName("cityTown")
    val city: String,
    val district: String,
    val latitidue: Double,
    val location: String,
    val longitude: Double,
    val pincode: Int,
    val placeId: String,
    val state: String,
)

@Serializable
data class CustomerMetaData(
    val activated: Boolean,
    val pageType: String,
    val userRole: String,
)