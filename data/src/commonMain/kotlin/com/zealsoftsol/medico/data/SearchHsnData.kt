package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class SearchDataItem(
    val hsncode: String,
    val hsncodeId: String,
    val gstRate: String,
    val effectiveDate: String,
    val revisedDate: String,
    val description: String,
    val displayValue: String,
    val rate: Rate,
    var checked: Boolean = false
)

@Serializable
data class Rate(
    val value: String,
    val formattedValue: String
)
