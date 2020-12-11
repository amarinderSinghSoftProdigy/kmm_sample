package com.zealsoftsol.medico.data

data class AuthCredentials(
    val phoneNumberOrEmail: String,
    val type: Type?,
    val password: String,
) {
//    fun getPhoneNumber(): String? = phoneNumberOrEmail.takeIf { type == Type.PHONE }
//    fun getEmail(): String? = phoneNumberOrEmail.takeIf { type == Type.EMAIL }

    enum class Type {
        EMAIL, PHONE;
    }
}

enum class UserType(val serverValue: String) {
    STOCKIST("stockist"),
    RETAILER("retailer"),
    SEASON_BOY("season_boy"),
    HOSPITAL("hospital");
}

data class AadhaarData(
    val cardNumber: String,
    val shareCode: String,
)