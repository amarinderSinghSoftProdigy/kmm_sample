package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class InventoryCompanies(
    val results: List<CompanyData>,
    val totalResults: Int
)

@Serializable
data class CompanyData(
    val name: String,
    val products: List<ProductData>
)

@Serializable
data class ProductData(
    val name: String,
    val standardUnit: String
)