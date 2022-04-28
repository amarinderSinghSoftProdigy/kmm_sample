package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class AllBanners(
    val results: List<BannerItemData>,
    val totalResults: Int,
)

@Serializable
data class BannerItemData(
    val id: String,
    val sellerUnitCode: String,
    val productCode: String,
    val mnfrCode: String,
    val quantity: Double,
    val free: Double,
    val spid: String,
    val url: String,
    val documentId: String,
    val productName: String? = null
)
