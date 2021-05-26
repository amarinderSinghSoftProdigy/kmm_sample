package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OrderType(val path: String) {
    RECEIVED("buyer"), SENT("seller"), HISTORY("history");
}

@Serializable
data class Order(
    @SerialName("sbRetailerTradeName")
    val seasonBoyRetailerName: String? = null,
    val tradeName: String,
    @SerialName("orderInfo")
    val info: OrderInfo,
)

@Serializable
data class OrderEntry(
    @SerialName("orderEntryCode")
    val code: String,
    @SerialName("orderEntryNumber")
    val number: Int,
    val batchNo: String,
    val buyingOption: BuyingOption,
    val drugFormName: String,
    val expiryDate: FormattedData<Long>,
    val mrp: FormattedData<Double>,
    val price: FormattedData<Double>,
    val productCode: String,
    val productName: String,
    val requestedQty: FormattedData<Int>,
    val servedQty: FormattedData<Int>,
    val spid: String,
    val standardUnit: String,
    val total: Total
)

@Serializable
data class OrderResponse(
    @SerialName("orderEntries")
    val entries: List<OrderEntry>,
    @SerialName("orderInfo")
    val info: OrderInfo,
)

@Serializable
data class OrderInfo(
    @SerialName("orderId")
    val id: String,
    @SerialName("orderDate")
    val date: String,
    @SerialName("orderTime")
    val time: String,
    @SerialName("orderStatus")
    val status: String,
    val paymentMethod: PaymentMethod,
    val total: Total,
)