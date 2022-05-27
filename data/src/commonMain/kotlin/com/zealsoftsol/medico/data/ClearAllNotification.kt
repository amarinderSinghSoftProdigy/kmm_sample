package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClearAllNotification(
    @SerialName("body")
    val body: Body ?= null,
    @SerialName("type")
    val type: String ?= null
)

@Serializable
data class Body(
    val results: List<NotificationData>,
    val totalResults: Int
)