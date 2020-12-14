package com.zealsoftsol.medico.data

data class AuthCredentials(
    val phoneNumberOrEmail: String,
    val type: Type?,
    val password: String,
) {
    enum class Type {
        EMAIL, PHONE;
    }
}

enum class UserType(val serverValue: String) {
    STOCKIST("stockist"),
    RETAILER("retailer"),
    SEASON_BOY("season_boy"),
    HOSPITAL("hospital");

    companion object {
        fun parse(value: String): UserType = when (value) {
            STOCKIST.serverValue -> STOCKIST
            RETAILER.serverValue -> RETAILER
            SEASON_BOY.serverValue -> SEASON_BOY
            HOSPITAL.serverValue -> HOSPITAL
            else -> throw UnsupportedOperationException("unknown user type string")
        }
    }
}

data class AadhaarData(
    val cardNumber: String,
    val shareCode: String,
)