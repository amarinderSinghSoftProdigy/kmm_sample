package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

data class AuthCredentials(
    val phoneNumberOrEmail: String,
    val type: Type?,
    val password: String,
) {
    inline val isEmpty: Boolean
        get() = phoneNumberOrEmail.isEmpty() && password.isEmpty()

    enum class Type {
        EMAIL, PHONE;
    }
}

enum class UserType(val serverValue: String) {
    STOCKIST("STOCKIST"),
    RETAILER("RETAILER"),
    SEASON_BOY("SEASON_BOY"),
    HOSPITAL("HOSPITAL");

    val stringId: String
        get() = serverValue.toLowerCase()

    companion object {
        fun parse(value: String): UserType? = when (value) {
            STOCKIST.serverValue -> STOCKIST
            RETAILER.serverValue -> RETAILER
            SEASON_BOY.serverValue -> SEASON_BOY
            HOSPITAL.serverValue -> HOSPITAL
            else -> null
        }
    }
}

data class AadhaarData(
    val cardNumber: String,
    val shareCode: String,
)

@Serializable
data class GeoPoints(
    val latitude: Int,
    val longitude: Int
)