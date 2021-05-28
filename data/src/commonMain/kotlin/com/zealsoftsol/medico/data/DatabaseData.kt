package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val unitCode: String,
    val type: UserType,
    val details: Details,
    val isActivated: Boolean,
    val isDocumentUploaded: Boolean,
    val addressData: AddressData,
) {
    fun fullName() = "$firstName $lastName"

    @Serializable
    sealed class Details {
        @Serializable
        data class DrugLicense(
            val tradeName: String,
            val gstin: String,
            val license1: String,
            val license2: String,
            val url: String?,
        ) : Details()

        @Serializable
        data class Aadhaar(val cardNumber: String, val shareCode: String) : Details()
    }
}