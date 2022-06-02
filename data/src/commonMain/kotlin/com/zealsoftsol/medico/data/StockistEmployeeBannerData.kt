package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class StockistEmployeeBannerData(
    val body: List<EmployeeBannerData>,
    val type: String
)

@Serializable
data class EmployeeBannerData(
    val name: String,
    val url: String
)