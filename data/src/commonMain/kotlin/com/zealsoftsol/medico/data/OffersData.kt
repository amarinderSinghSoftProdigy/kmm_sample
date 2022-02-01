package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class OfferData(
    @SerialName("manufacturers")
    val manufacturers: List<Manufacturer>,

    @SerialName("promotionStatusDatas")
    val promotionStatusDatas: List<PromotionStatusData>,

    @SerialName("promotions")
    val promotions: List<Promotions>,

    @SerialName("totalResults")
    val totalResults: String,
)

@Serializable
data class PromotionCategory(
    val code: String,
    val name: String
)

@Serializable
data class CustomerPageData(
    val customerDatas: List<CustomerDatas>,
    val totalCustomers: Int
)

@Serializable
data class CustomerDatas(
    val cityOrTown: String,
    val pincode: String,
    val tradeName: String
)

@Serializable
data class PromotionType(
    val code: String,
    val description: String,
    val name: String
)

@Serializable
data class Manufacturer(
    val code: String,
    val count: Int,
    val name: String
)

@Serializable
data class PromotionStatusData(
    val status: String,
    val total: Int,
)

@Serializable
class Promotions {
    val endDate: FormattedData<Double>? = null
    val free: FormattedData<Double>? = null
    val startDate: FormattedData<Double>? = null
    val buy: FormattedData<Double>? = null
    val productDiscount: FormattedData<Double>? = null
    val manufacturerCode: String? = null
    val manufacturerName: String? = null
    val offer: String? = null
    val productCode: String? = null
    val productName: String? = null
    val promoCode: String? = null
    val promoStatus: String? = null
    val status: String? = null
    val active: Boolean? = null
    val promotionCategory: PromotionCategory? = null
    val promotionTypeData: PromotionType? = null
    val customerPageData: CustomerPageData? = null
}