package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class WhatsappData(
    val whatsappLanguages: List<WhatsappLanguagesItem>?,
    val selectedLanguage: String = "",
    val mobileCountryCode: String = "",
    val mobileNo: String = ""
)

@Serializable
data class WhatsappLanguagesItem(val code: String = "", val name: String = "")
