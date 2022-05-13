package com.zealsoftsol.medico.data.dashboard

data class DashBoardManufacturersData(
    val type: String,
    val body: List<ManufacturerData>
)

data class ManufacturerData(
    val code: String,
    val name: String,
    val count: Int
)