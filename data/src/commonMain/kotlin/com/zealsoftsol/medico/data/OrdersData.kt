package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OrderType(val path: String) {
    PURCHASE_ORDER("/po/"),
    ORDER("/"),
    HISTORY("/po/history/"),
    PREVIEW("/orders/tax/po/preview");
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
    @SerialName("entryStatus")
    val status: Status,
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
    val freeQty: FormattedData<Double>,
    val spid: String,
    val standardUnit: String,
    val totalAmount: FormattedData<Double>,
    val hsnCode: String,
    val manufacturerName: String,
    val discount: FormattedData<Double>,
    val cgstTax: Tax,
    val sgstTax: Tax,
    val igstTax: Tax,
    val reason: String,
) {

    enum class Status {
        ACCEPTED, REJECTED, DECLINED;
    }
}


@Serializable
data class OrderTax(
    @SerialName("orderTaxInfo")
    val info: OrderInfo,
    val tradeName: String,
    @SerialName("sbRetailerTradeName")
    val seasonBoyRetailerName: String? = null,
)
@Serializable
data class OrderTaxInvoice(
    @SerialName("orderTaxInfo")
    val info: OrderTaxInfo,
    val tradeName: String,
    @SerialName("sbRetailerTradeName")
    val seasonBoyRetailerName: String? = null,
)

@Serializable
data class OrderResponse(
    @SerialName("orderEntries")
    val entries: List<OrderEntry>,
    @SerialName("orderTax")
    val order: OrderTax,
    @SerialName("unitInfoData")
    val unitData: UnitData,
    val declineReasons: List<DeclineReason>,
    val isDeliveryAvailable: Boolean
)

@Serializable
data class OrderResponseInvoice(
    @SerialName("orderEntries")
    val entries: List<OrderEntry>,
    @SerialName("orderTax")
    val order: OrderTaxInvoice,
    @SerialName("unitInfoData")
    val unitData: UnitData,
    val declineReasons: List<DeclineReason>,
    val isDeliveryAvailable: Boolean
)

@Serializable
data class DeclineReason(val code: String, val name: String)

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
    val gstin: String?,
    @SerialName("mobileNumber")
    val phoneNumber: String,
    val panNumber: String,
    val tradeName: String,
    val tradeProfile: String = ""
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
    val status: OrderStatus,
    val paymentMethod: PaymentMethod,
    val total: Total,
    val taxType: TaxType? = null,
    val discount: FormattedData<Double>? = null
)

enum class OrderStatus(val stringValue: String) {
    CANCELLED("Cancelled"), COMPLETED("Completed"), NEW("New"), PENDING_PAYMENT("Pending Payment"), PENDING_DELIVERY(
        "Pending Delivery"
    );
}

@Serializable
data class OrderNewQtyRequest(
    val orderId: String,
    val orderEntryId: String,
    @SerialName("sellerUnitCode")
    val unitCode: String,
    val servedQty: Double,
    val freeQty: Double,
    val price: Double,
    val batchNo: String,
    val expiryDate: String,
    val discount: Double? = null,
    val mrp: Double? = null,
    val hsnCode: String? = null
)

@Serializable
data class ConfirmOrderRequest(
    val orderId: String,
    var sellerUnitCode: String? = null,
    val acceptedEntries: List<String>,
    val reasonCode: String? = null,
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
    @SerialName("sellerInvoiceId")
    val sellerId: String,
    @SerialName("invoiceDate")
    val date: String,
    @SerialName("invoiceTime")
    val time: String,
    val paymentMethod: PaymentMethod,
    val total: Total,
)

@Serializable
data class InvoiceResponse(
    @SerialName("sellerUnitInfo")
    val sellerData: B2BData,
    @SerialName("buyerUnitInfo")
    val buyerData: B2BData,
    val invoiceEntries: List<InvoiceEntry>,
    @SerialName("invoiceTaxInfo")
    val taxInfo: TaxInfo,
)

@Serializable
data class InvoiceEntry(
    val productName: String,
    val productCode: String,
    val manufacturerName: String,
    val standardUnit: String,
    val price: FormattedData<Double>,
    val totalAmount: FormattedData<Double>,
    val quantity: FormattedData<Double>,
    val freeQty: FormattedData<Double>,
    val discount: FormattedData<Double>,

    val cgstTax: Tax,
    val igstTax: Tax,
    val sgstTax: Tax,
    val taxType: TaxType,
    val gstTaxRate: TaxRate,
    val totalTaxAmount: FormattedData<Double>,
)

@Serializable
data class Tax(
    @SerialName("gstTaxRate")
    val rate: TaxRate,
    @SerialName("percentTax")
    val percent: FormattedData<Double>,
    @SerialName("taxAmt")
    val amount: FormattedData<Double>,
)

@Serializable
enum class TaxRate(val string: String) {
    ZERO_TAX("0%"),
    TWELVE_PERCENT_TAX("12%"),
    EIGHTEEN_PERCENT_TAX("18%"),
    FIVE_PERCENT_TAX("5%"),
}

@Serializable
enum class TaxType(val string: String) {
    SGST("SGST"),
    IGST("IGST"),
}

@Serializable
data class TaxInfo(
    val totalTaxRates: List<TotalTaxRate>,
    val totalTaxAmount: FormattedData<Double>,
    val total: PriceData,
    val totalCGST: FormattedData<Double>,
    val totalIGST: FormattedData<Double>,
    val totalSGST: FormattedData<Double>,
    val totalDiscountAmt: FormattedData<Double>,
    val invoiceDiscount: FormattedData<Double>,
    val grossAmount: FormattedData<Double>,
    val freight: FormattedData<Double>,
    val discount: FormattedData<Double>,
    val adjWithoutRounded: FormattedData<Double>,
    val adjRounded: FormattedData<Double>,
    @SerialName("taxType")
    val type: TaxType,
    @SerialName("qrCodeDownloadUrl")
    val qrCodeUrl: String,
    @SerialName("downloadUrl")
    val invoiceUrl: String,
    val netAmount: FormattedData<Double>,
    val noOfItems: Int,
    val noOfUnits: Double,
    val invoiceId: String,
    val invoiceTime: String,
    val invoiceDate: String,
    val b2bUnitInvoiceId: String,
    val amountInWords: String,
    val paymentMethod: PaymentMethod,
)

@Serializable
data class TotalTaxRate(
    val gstDisplayName: String,
    val cgstTotalAmt: FormattedData<Double>,
    val igstTotalAmt: FormattedData<Double>,
    val sgstTotalAmt: FormattedData<Double>,
    val totalTaxableAmount: FormattedData<Double>,
    val cgstTaxPercent: FormattedData<Double>,
    val sgstTaxPercent: FormattedData<Double>,
    val igstTaxPercentt: FormattedData<Double>
)

@Serializable
data class OrderTaxInfo(
    val orderDate: String,
    val orderTime: String,
    val paymentMethod: String,
    val orderId: String,
    val total: Total,
    val orderStatus: String,
    val discount: FormattedData<Double>,
    val orderDiscount: FormattedData<Double>,
    val totalTaxRates: List<GstData>,
    val grossAmount: FormattedData<Double>,
    val totalDiscountAmt: FormattedData<Double>,
    val totalTaxableAmount: FormattedData<Double>,
    val totalTaxAmount: FormattedData<Double>,
    val totalCGST: FormattedData<Double>,
    val totalSGST: FormattedData<Double>,
    val totalIGST: FormattedData<Double>,
    val noOfItems: Double,
    val noOfUnits: Double,
    val adjRounded: FormattedData<Double>,
    val adjWithoutRounded: FormattedData<Double>,
    val netAmount: FormattedData<Double>,
    val amountInWords: String,
    val taxType: String
)

@Serializable
data class GstData(
    val gstDisplayName: String,
    val totalTaxableAmount: FormattedData<Double>,
    val cgstTotalAmt: FormattedData<Double>,
    val sgstTotalAmt: FormattedData<Double>,
    val igstTotalAmt: FormattedData<Double>
)