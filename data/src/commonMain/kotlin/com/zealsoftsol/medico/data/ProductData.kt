package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
//    @SerialName("alternateBrands")
//    val alternateBrands: List<ProductData>,
//    @SerialName("baseProducts")
//    val baseProducts: List<ProductData>,
    @SerialName("productData")
    val productData: ProductData,
)

@Serializable
data class ProductData(
    @SerialName("active")
    val active: Boolean,
    @SerialName("code")
    val code: String,
    @SerialName("compositionsData")
    val compositionsData: List<CompositionsData>,
    @SerialName("drugTypeData")
    val drugTypeData: CodeName,
    @SerialName("formattedPrice")
    val formattedPrice: String,
    @SerialName("hsnCode")
    val hsnCode: String,
    @SerialName("hsnPercentage")
    val hsnPercentage: String,
    @SerialName("id")
    val id: String,
    @SerialName("isPrescriptionRequired")
    val isPrescriptionRequired: Boolean,
    @SerialName("manufacturer")
    val manufacturer: CodeName,
    @SerialName("medicineId")
    val medicineId: String,
    @SerialName("mfgDivision")
    val mfgDivision: String,
    @SerialName("mrp")
    val mrp: Double,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Double,
    @SerialName("productData")
    val miniProductData: MiniProductData,
    @SerialName("score")
    val score: Double,
    @SerialName("shortName")
    val shortName: String,
    @SerialName("standardUnit")
    val standardUnit: String,
    @SerialName("unitOfMeasureData")
    val unitOfMeasureData: CodeName
)

@Serializable
data class MiniProductData(
    val code: String,
    val manufacture: CodeName,
    val name: String,
)

@Serializable
data class CompositionsData(
    @SerialName("composition")
    val composition: CodeName,
    @SerialName("strength")
    val strength: CodeName
)

@Serializable
data class CodeName(
    @SerialName("code")
    internal val code: String,
    @SerialName("name")
    val name: String
)