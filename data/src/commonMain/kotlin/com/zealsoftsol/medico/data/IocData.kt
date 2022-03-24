package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IOCResponse(
    val totalResults: Int,
    val results: List<RetailerData>,
)

@Serializable
data class AddInvoiceResponse(
    val info: String,
)


@Serializable
data class RetailerData(
    val unitCode: String,
    val tradeName: String,
    val cityOrTown: String,
    val pincode: String,
    val gstin: String,
    val panNumber: String,
    val drugLicenseNo1: String,
    val drugLicenseNo2: String,
    val paymentMethod: String,
    val customerType: String,
    val status: String
)

@Serializable
data class AddInvoice(
    val unitCode: String,
    val invoiceNo: String,
    val invoiceDate: Long,
    val invoiceTotal: Double,
    val outstandingAmt: Double,
    val invoiceCdnUrl: String,
    val documentId: String,
    val documentType: String
)

@Serializable
data class InvoiceData(
    val date: String,
    val amount: String,
    val type: String
)


@Serializable
data class SellerUsersData(
    val results: List<InvUserData>,
    val totalResults: Int
)

@Serializable
data class InvUserData(
    val unitCode: String,
    val tradeName: String,
    val customerType: String,
    val paidAmount: FormattedData<Double>,
    val outstandingAmount: FormattedData<Double>,
    val totalAmount: FormattedData<Double>,
    val totalInvoices: Int
)

@Serializable
data class InvListingData(
    val amountReceived: FormattedData<Double>,
    val outstandingAmount: FormattedData<Double>,
    val totalInvoices: Int,
    val buyerDetails: BuyerDetails,
)

@Serializable
data class BuyerDetails(
    val results: List<BuyerDetailsData>,
    val totalResults: Int
)

@Serializable
data class BuyerDetailsData(
    val unitCode: String,
    val tradeName: String,
    val invoiceNo: String,
    val invoiceAmount: FormattedData<Double>,
    val viewInvoiceUrl: String,
    val viewStatus: String,
    val invoiceId: String,
    val invoiceDate: FormattedData<Long>
)

@Serializable
data class InvoiceDetails(
    val unitCode: String,
    val tradeName: String,
    val invoiceNo: String,
    val invoiceDate: FormattedData<Long>,
    val invoiceAmount: FormattedData<Double>,
    val invoiceOutstdAmount: FormattedData<Double>,
    val viewInvoiceUrl: String,
    val invoiceId: String,
    val iocCollections: List<InvContactDetails>
)

@Serializable
data class InvContactDetails(
    val lineMan: String,
    val mobileNumber: String,
    val paymentType: String,
    val collectionDate: FormattedData<Long>,
    val collectionAmount: FormattedData<Double>
)


@Serializable
data class UpdateInvoiceRequest(
    val invoiceDate: Long,
    val invoiceAmount: Double,
    val paymentType: String,
    val invoiceId: String
)


