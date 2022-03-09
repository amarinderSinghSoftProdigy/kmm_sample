package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class QrCodeData(
    val qrCode: String,
    val qrCodeUrl: String
)