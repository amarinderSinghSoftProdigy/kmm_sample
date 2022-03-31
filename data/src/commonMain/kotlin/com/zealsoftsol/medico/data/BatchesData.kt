package com.zealsoftsol.medico.data

import kotlinx.serialization.Serializable

@Serializable
data class BatchesData(
    val results: List<Batches>,
    val totalResults: Int = 0,
)

@Serializable
data class Batches(
    val batches: List<Batch>,
    val totalStock: FormattedData<Double>,
    val standardUnit: String,
    val productName: String,
    val productCode: String,
    val manufacturerName: String
)

@Serializable
data class Batch(
    val mrp: FormattedData<Double>,
    val ptr: FormattedData<Double>,
    val stock: FormattedData<Double>,
    val batchNo: String,
    val expiryDate: String,
    val hsncode: String,
    val spid: String,
    val promotionData: BatchPromotionData,
    val status: Status,
    val marginPercent: String,
) {
    enum class Status {
        ONLINE, OFFLINE
    }
}


@Serializable
data class BatchPromotionData(
    val type: String,
    val promotionActive: Boolean,
    val promoStatus: String,
    val promoCode: String,
    val productDiscount: String,
    val free: String,
    val buy: String,
    val connectedUsers: List<String>,
    val displayOffer: String
)

@Serializable
data class BatchUpdateRequest(
    val productCode: String,
    val manufacturerCode: String,
    val hsnCode: String,
    val vendorProductName: String,
    val spid: String,
    val stock: Double,
    val expiryDate: String,
    val ptr: Double,
    val mrp: Double,
    val batchLotNo: String,
    val mfgDate: String,
    val warehouseUnitCode: String,
    val warehouseCode: String,
    val status: String
)

@Serializable
data class BatchStatusUpdateRequest(
    val productCode: String,
    val manufacturerCode: String,
    val hsnCode: String,
    val spid: String,
    val warehouseCode: String,
    val status: String
)