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
data class PromotionTypeData(
    @SerialName("promotionCategories")
    val promotionCategories: List<PromotionCategory>,

    @SerialName("promotionTypes")
    val promotionTypes: List<PromotionType>
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
data class PromotionUpdateRequest(
    val promoCode: String,
    val active: Boolean,
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

@Serializable
class OfferProduct(
    val mrp: FormattedData<Double>,
    val ptr: FormattedData<Double>,
    val availableQty: FormattedData<Double>,
    val batch: String,
    val expiryDate: String,
    val standardUnit: String,
    val hsnCode: String,
    val name: String,
    val id: String,
    val shortName: String,
    val code: String,
    val manufacturerCode: String,
    val manufacturerName: String,
    val spid: String,
)

@Serializable
class OfferProductRequest {
    var promotionType: String? = null
    var productCode: String? = null
    var manufacturerCode: String? = null
    var spid: String? = null
    var buy: Double? = null
    var free: Double? = null
    var active: Boolean? = null
    var isOfferForAllUsers: Boolean? = null
    var connectedUsers: ArrayList<String>? = null
    var discount: Double? = null
    var stock: Double? = null
    var startDate: Long? = null
    var endDate: Long? = null
}

