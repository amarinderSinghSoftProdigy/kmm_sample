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
    val totalResults: Int,
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
class Promotions(
    val endDate: FormattedData<Double>,
    val free: FormattedData<Double>,
    val startDate: FormattedData<Double>,
    val buy: FormattedData<Double>,
    val productDiscount: FormattedData<Double>,
    val manufacturerCode: String,
    val manufacturerName: String,
    val offer: String,
    val productCode: String,
    val productName: String,
    val promoCode: String,
    val promoStatus: String,
    val status: String,
    val active: Boolean,
    val promotionCategory: PromotionCategory,
    val promotionTypeData: PromotionType,
    val customerPageData: CustomerPageData
)