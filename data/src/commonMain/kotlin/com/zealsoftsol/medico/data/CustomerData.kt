package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomerData(
    val aadhaarCardNo: String?,
    val addressData: AddressData,
    val metaData: CustomerMetaData?,
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
data class CustomerMetaData(
    val activated: Boolean,
    val pageType: String,
    val userRole: String,
)