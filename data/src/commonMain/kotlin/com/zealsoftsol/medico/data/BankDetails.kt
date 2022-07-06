package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BankDetails(
    val name: String,
    val accountNumber: String,
    @SerialName("ifsc")
    val ifscCode: String,
    @SerialName("phone")
    val mobileNumber: String,
    val id: String = "",
    val isAccountCreated: Boolean = false,
)

@Serializable
data class UpiDetails(
    val id: String,
    val isAccountCreated: Boolean,
    val vpa: String,
    val name: String
)
