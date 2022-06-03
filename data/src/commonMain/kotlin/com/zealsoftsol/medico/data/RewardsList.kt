package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class RewardsList(
    val pageableData: RewardPageableData,
    val totalAmount: FormattedData<Double>,
    val totalPoints: Double
)

@Serializable
data class RewardPageableData(
    val results: List<RewardItem>,
    val totalResults: Int
)

@Serializable
data class RewardItem(
    val orderId: String,
    val amount: FormattedData<Double>,
    val rewardAmount: FormattedData<Double>,
    val rewardPercent: FormattedData<Double>,
    val date: String,
    val expiresIn: String
)