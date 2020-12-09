package com.zealsoftsol.medico.data

data class AuthCredentials(
    val phoneNumberOrEmail: String,
    val type: Type?,
    val password: String,
) {
    fun getPhoneNumber(): String? = phoneNumberOrEmail.takeIf { type == Type.PHONE }
    fun getEmail(): String? = phoneNumberOrEmail.takeIf { type == Type.EMAIL }

    enum class Type {
        EMAIL, PHONE;
    }
}

enum class AuthState(val key: String) {
    AUTHORIZED("au"), PENDING_VERIFICATION("pe"), NOT_AUTHORIZED("na");

    companion object {
        fun fromKey(key: String): AuthState {
            return when (key) {
                AUTHORIZED.key -> AUTHORIZED
                PENDING_VERIFICATION.key -> PENDING_VERIFICATION
                NOT_AUTHORIZED.key -> NOT_AUTHORIZED
                else -> throw UnsupportedOperationException("unknown auth key $key")
            }
        }
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