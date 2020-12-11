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
    val addressLine1: String,
    @SerialName("cityTown")
    val cityTown: String,
    @SerialName("district")
    val district: String,
    @SerialName("latitidue")
    val latitidue: Double,
    @SerialName("location")
    val location: String,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("pincode")
    val pincode: Int,
    @SerialName("placeId")
    val placeId: String,
    @SerialName("state")
    val state: String
)

@Serializable
data class CustomerMetaData(
    @SerialName("activated")
    val activated: Boolean,
    @SerialName("pageType")
    val pageType: String,
    @SerialName("userRole")
    val userRole: String
)