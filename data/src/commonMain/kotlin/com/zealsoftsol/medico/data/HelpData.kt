package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val salesPhoneNumber: String
)