package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val type: UserType,
    val isVerified: Boolean,
    val isDocumentUploaded: Boolean,
) {
    fun fullName() = "$firstName $lastName"
}