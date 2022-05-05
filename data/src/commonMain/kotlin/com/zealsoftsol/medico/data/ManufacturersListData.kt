package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class ManufacturersListData(
    val manufacturers: List<ManufacturerItem>,
    val totalManufacturers: Int
)

@Serializable
data class ManufacturerItem(
    val id: String,
    val code: String,
    val name: String,
    val mnfrCode: String,
    val mnfrShortName: String,
    val totalBaseProducts: Double,
    val totalVariantProducts: Double,
    val score: Double,
    val searchkey: String
)