package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val alternateProducts: List<AlternateProductData>,
    val productData: ProductSearch,
)

@Serializable
data class AlternateProductData(
    val productCode: String,
    val productName: String,
    val priceRange: String,
    val manufacturerName: String,
    val availableVariants: String,
)

//@Serializable
//data class ProductData(
//    val active: Boolean,
//    val code: String,
//    val compositionsData: List<CompositionsData>,
//    val drugTypeData: CodeName,
//    val formattedPrice: String,
//    val hsnCode: String,
//    val hsnPercentage: String,
//    val ptr: String,
//    val id: String,
//    val isPrescriptionRequired: Boolean,
//    val manufacturer: CodeName,
//    val medicineId: String,
//    val mfgDivision: String,
//    val mrp: Double,
//    val name: String,
//    val price: Double,
//    @SerialName("productData")
//    val miniProductData: MiniProductData,
//    val score: Double,
//    val shortName: String,
//    val standardUnit: String,
//    val unitOfMeasureData: CodeName
//)
//
//@Serializable
//data class MiniProductData(
//    val code: String,
//    val manufacture: CodeName,
//    val name: String,
//)
//
//@Serializable
//data class CompositionsData(
//    val composition: CodeName,
//    val strength: CodeName
//)
//
//@Serializable
//data class CodeName(
//    internal val code: String,
//    val name: String
//)