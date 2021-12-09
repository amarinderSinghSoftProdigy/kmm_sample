package com.zealsoftsol.medico.data

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InStoreSeller(
    @SerialName("buyerTradeName")
    val tradeName: String,
    @SerialName("cityOrTown")
    val city: String,
    @SerialName("mobileNumber")
    val phoneNumber: String,
    @SerialName("totalAmount")
    val total: FormattedData<Double>,
    val items: Int,
    @SerialName("buyerUnitCode")
    val unitCode: String,
)

@Serializable
data class InStoreProduct(
    val id: String,
    val code: String,
    val name: String,
    val shortName: String,
    val manufacturer: String,
    val standardUnit: String,
    val spid: String,
    val priceInfo: PriceInfo,
    val stockInfo: StockInfo,
    @SerialName("inStoreOrder")
    val order: InStoreOrder?,
    val isPromotionActive: Boolean,
    val promotionData: InStorePromotionData?,
)

@Serializable
data class InStoreOrder(
    val quantity: FormattedData<Double>,
    val freeQty: FormattedData<Double>,
) {
    fun isEmpty() = quantity.value == 0.0 && freeQty.value == 0.0
}

@Serializable
data class InStorePromotionData(
    val type: String,
    val code: String,
    val buy: FormattedData<Double>,
    val free: FormattedData<Double>,
    val productDiscount: FormattedData<Double>,
    val displayLabel: String,
    val offerPrice: FormattedData<Double>,
    val validity: String?,
)

@Serializable
data class InStoreUser(
    val sellerUnitCode: String,
    val buyerUnitCode: String,
    val tradeName: String,
    val drugLicenseNo1: String,
    val drugLicenseNo2: String,
    val gstin: String,
    val panNumber: String,
    val mobileNumber: String,
    val addressData: AddressData,
    val status: String
)

@Serializable
data class InStoreUserRegistration(
    @Required
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    @Required
    val b2bUnitCode: String = "",
    @SerialName("mobileNumber")
    @Required
    val phoneNumber: String = "",
    @Required
    val tradeName: String = "",
    @Required
    val gstin: String = "",
    @Required
    val panNumber: String = "",
    @Required
    val drugLicenseNo1: String = "",
    @Required
    val drugLicenseNo2: String = "",
    @Required
    val pincode: String = "",
    @Required
    val addressLine1: String = "",
    @Required
    val location: String = "",
    @Required
    val landmark: String = "",
    @Required
    @SerialName("cityTown")
    val city: String = "",
    @Required
    val district: String = "",
    @Required
    val state: String = "",
) {
    fun isNotEmpty() =
        tradeName.isNotEmpty() &&
                (gstin.isNotEmpty() || panNumber.isNotEmpty()) &&
                drugLicenseNo1.isNotEmpty() &&
                drugLicenseNo2.isNotEmpty() &&
                pincode.isNotEmpty() &&
                addressLine1.isNotEmpty() &&
                location.isNotEmpty() &&
                landmark.isNotEmpty() &&
                city.isNotEmpty() &&
                district.isNotEmpty() &&
                state.isNotEmpty()
}

@Serializable
data class InStoreCart(
    val id: String?,
    val buyerUnitCode: String,
    val buyerTradeName: String,
    @SerialName("cityOrTown")
    val city: String,
    val mobileNumber: String,
    val paymentType: String,
    val totalQty: FormattedData<Double>,
    val totalFreeQty: FormattedData<Double>,
    val total: Total,
    @SerialName("inStoreOrderEntries")
    val entries: List<InStoreCartEntry>
)

@Serializable
data class InStoreCartEntry(
    @SerialName("entryId")
    val id: String,
    val entryNumber: Int,
    val productCode: String,
    val productName: String,
    val manufacturerCode: String,
    val manufacturerName: String,
    val quantity: FormattedData<Double>,
    val freeQty: FormattedData<Double>,
    val price: FormattedData<Double>,
    val totalPrice: FormattedData<Double>,
    val spid: String,
    val standardUnit: String
)

@Serializable
data class InStoreCartRequest(
    val buyerUnitCode: String,
    val productCode: String,
    val spid: String,
    val quantity: Double,
    val freeQty: Double,
)