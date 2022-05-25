package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class DemoClass(
    val body: List<DemoResponse> = emptyList(),
    val type: String
)

@Serializable
data class DemoResponse(
    val description: String,
    val id: String,
    val name: String,
    val url: String
)