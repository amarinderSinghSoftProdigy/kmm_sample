package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class SearchData(
    val results: List<SearchDataItem>?,
    val totalResults: String = ""
)

@Serializable
data class SearchDataItem(
    val hsncode: String,
    val hsncodeId: String,
    val gstRate: String,
    val effectiveDate: String,
    val revisedDate: String,
    val description: String,
    val displayValue: String,
    val rate: Rate
)

@Serializable
data class Rate(
    val value: String,
    val formattedValue: String
)
