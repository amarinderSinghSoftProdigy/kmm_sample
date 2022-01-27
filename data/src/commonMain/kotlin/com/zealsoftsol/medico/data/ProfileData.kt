package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileImageData(
    val tradeProfile: String = "",
    val userProfile: String = ""
)

@Serializable
data class ProfileImageUpload(
    @SerialName("documentType")
    val documentType: String,
    val fileString: String,
    val mimeType: String,
)