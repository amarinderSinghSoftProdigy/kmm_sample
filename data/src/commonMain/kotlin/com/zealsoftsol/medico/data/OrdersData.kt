package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OrderType(val path: String) {
    PURCHASE_ORDER("/po/"),
    ORDER("/"),
    HISTORY("/history/");
}

@Serializable
data class Order(
    @SerialName("orderInfo")
    val info: OrderInfo,
    val tradeName: String,
    @SerialName("sbRetailerTradeName")
    val seasonBoyRetailerName: String? = null,
)

@Serializable
data class OrderEntry(
    @SerialName("orderEntryId")
    val id: String,
    @SerialName("orderEntryNumber")
    val number: Int,
    val batchNo: String,
    val buyingOption: BuyingOption,
    val drugFormName: String,
    val expiryDate: FormattedData<Long>?,
    val mrp: FormattedData<Double>,
    val price: FormattedData<Double>,
    val productCode: String,
    val productName: String,
    val requestedQty: FormattedData<Double>,
    val servedQty: FormattedData<Double>,
    val spid: String,
    val standardUnit: String,
    val totalAmount: FormattedData<Double>,
)

@Serializable
data class OrderResponse(
    @SerialName("orderEntries")
    val entries: List<OrderEntry>,
    val order: Order,
    @SerialName("unitInfoData")
    val unitData: UnitData,
)

@Serializable
data class UnitData(
    @SerialName("b2BUnitData")
    val data: B2BData,
)

@Serializable
data class B2BData(
    val addressData: AddressData,
    val drugLicenseNo1: String,
    val drugLicenseNo2: String,
    val gstin: String,
    @SerialName("mobileNumber")
    val phoneNumber: String,
    val panNumber: String,
    val tradeName: String,
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

@Serializable
data class OrderNewQtyRequest(
    val orderId: String,
    val orderEntryId: String,
    @SerialName("sellerUnitCode")
    val unitCode: String,
    val servedQty: Int,
)

@Serializable
data class ConfirmOrderRequest(
    val orderId: String,
    val sellerUnitCode: String,
    val acceptedEntries: List<String>,
)

@Serializable
data class Invoice(
    @SerialName("invoiceInfo")
    val info: InvoiceInfo,
    val tradeName: String,
)

@Serializable
data class InvoiceInfo(
    @SerialName("invoiceId")
    val id: String,
    @SerialName("invoiceDate")
    val date: String,
    @SerialName("invoiceTime")
    val time: String,
    val paymentMethod: PaymentMethod,
    val total: Total,
)

@Serializable
data class InvoiceResponse(
    @SerialName("b2bUnitData")
    val data: B2BData,
    val invoice: Invoice,
    val invoiceEntries: List<InvoiceEntry>,
)

@Serializable
data class InvoiceEntry(
    val productName: String,
    val manufacturerName: String,
    val price: FormattedData<Double>,
    val totalAmount: FormattedData<Double>,
    val quantity: FormattedData<Double>,
)