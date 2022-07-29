package com.zealsoftsol.medico.core.data

data class AuthCredentials(
    val phoneNumberOrEmail: String,
    val password: String,
) {
    inline val isEmpty: Boolean
        get() = phoneNumberOrEmail.isEmpty() && password.isEmpty()
}

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


data class DateRange(val fromMs: Long? = null, val toMs: Long? = null)

enum class TapMode {
    CLICK, LONG_PRESS, RELEASE;
}

enum class NotificationFilter(val serverValue: String, val stringId: String) {
    ALL("", "all"), READ("READ", "read"), UNREAD("UNREAD", "unread");
}