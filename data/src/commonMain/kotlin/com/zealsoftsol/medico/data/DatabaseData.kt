package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val type: UserType,
    val details: Details,
    val isVerified: Boolean,
    val isDocumentUploaded: Boolean,
) {
    fun fullName() = "$firstName $lastName"

    @Serializable
    sealed class Details {
        @Serializable
        data class DrugLicense(val url: String?) : Details()

        @Serializable
        data class Aadhaar(val cardNumber: String, val shareCode: String) : Details()
    }
}