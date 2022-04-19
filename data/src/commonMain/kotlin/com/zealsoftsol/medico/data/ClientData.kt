package com.zealsoftsol.medico.data

data class AuthCredentials(
    val phoneNumberOrEmail: String,
    val password: String,
) {
    inline val isEmpty: Boolean
        get() = phoneNumberOrEmail.isEmpty() && password.isEmpty()
}

enum class UserType(
    val serverValue: String,
    val serverValueSimple: String,
) {
    STOCKIST("STOCKIST", "stockist"),
    RETAILER("RETAILER", "retailer"),
    SEASON_BOY("SEASON_BOY", "seasonboy"),
    HOSPITAL("HOSPITAL", "hospital"),
    EMPLOYEE("EMPLOYEE", "employee"),
    PARTNER("PARTNER", "partner"),
    EMPLOYEE_STOCKIST("EMPLOYEE_STOCKIST", "employee_stockist"),
    EMPLOYEE_RETAILER("EMPLOYEE_RETAILER", "employee_retailer");

    val stringId: String
        get() = serverValue.lowercase()

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
        fun forProfile() = arrayOf(PNG, JPEG, JPG)

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

enum class ManagementCriteria(val serverValue: String) {
    ALL("ALL"),
    PERSONAL("YOUR");
}

data class DateRange(val fromMs: Long? = null, val toMs: Long? = null)

enum class TapMode {
    CLICK, LONG_PRESS, RELEASE;
}

enum class NotificationFilter(val serverValue: String, val stringId: String) {
    ALL("", "all"), READ("READ", "read"), UNREAD("UNREAD", "unread");
}