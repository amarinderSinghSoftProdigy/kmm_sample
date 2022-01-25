package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class ProfileImageData(
    val tradeProfile: String = "",
    val userProfile: String = ""
)
