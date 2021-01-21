package com.zealsoftsol.medico.data

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

enum class FileType(val mimeType: String, val isMandatory: Boolean) {
    PNG("image/png", true),
    JPEG("image/jpeg", true),
    JPG("image/jpg", true),
    PDF("application/pdf", true),
    ZIP("application/zip", true),
    XZIP("multipart/x-zip", false),
    UNKNOWN("*/*", false);

    companion object Utils {
        fun forDrugLicense() = arrayOf(PDF, PNG, JPEG, JPG)
        fun forAadhaar() = arrayOf(ZIP, XZIP)

        fun fromExtension(ext: String): FileType {
            return when (ext) {
                "png" -> PNG
                "jpeg" -> JPEG
                "jpg" -> JPG
                "pdf" -> PDF
                "zip" -> ZIP
                else -> UNKNOWN
            }
        }
    }
}