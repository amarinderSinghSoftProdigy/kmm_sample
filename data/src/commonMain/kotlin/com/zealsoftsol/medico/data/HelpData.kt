package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HelpType {
    TERMS_AND_CONDITIONS(),
    PRIVACY_POLICY();
}


@Serializable
data class HelpData(
    val contactUs: ContactUs,
    @SerialName("privacy")
    val privacyPolicyUrl: String,
    @SerialName("terms")
    val tosUrl: String
)

@Serializable
data class ContactUs(
    @SerialName("customerCare")
    val customerCarePhoneNumber: String,
    val email: String,
    @SerialName("sales")
    val salesPhoneNumber: String,
    @SerialName("whatsappNo")
    val whatsAppPhoneNumber: String
)