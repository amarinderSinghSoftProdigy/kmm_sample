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




