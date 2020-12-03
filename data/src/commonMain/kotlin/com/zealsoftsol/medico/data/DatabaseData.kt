package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email: String?,
    val phoneNumber: String,
    val token: String,
    val type: String,
    val password: String,
)