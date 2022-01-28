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
    val name: String,
    val size: String,
    val mimeType: String,
    val documentType: String,
    val documentData: String
)

@Serializable
data class ProfileResponseData(
    val id: String = "",
    val documentType: String = "",
    val cdnUrl: String = ""
)