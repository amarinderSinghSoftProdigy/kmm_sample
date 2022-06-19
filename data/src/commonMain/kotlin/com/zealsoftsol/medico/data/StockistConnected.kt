package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class StockistConnectedData(
    val body: ConnectedStockists ?= null,
    val type: String? = null
)

@Serializable
data class ConnectedStockists(
    val results: List<StockistListItem> = emptyList(),
    val totalResults: Int
)

@Serializable
data class StockistListItem(
    val distance: FormattedData<Double>,
    val key: String,
    val tradeName: String,
    val unitCode: String,
    val url: String
)