package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phoneNumber: String,
    val type: UserType,
    val isVerified: Boolean,
    val documentUrl: String?
) {
    fun fullName() = "$firstName $lastName"
}