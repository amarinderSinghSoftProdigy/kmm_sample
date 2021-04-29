package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartRequest(
    val buyerUnitCode: String,
    val sellerUnitCode: String,
    val productCode: String,
    val buyingOption: BuyingOption,
    @SerialName("cartIdentifier")
    val id: CartIdentifier,
    val quantity: Int? = null,
)

@Serializable
data class Total(
    val formattedPrice: String,
    val price: Double,
    val itemCount: Int,
)

@Serializable
data class CartData(
    val cartId: String,
    val customerId: String,
    val sellerCarts: List<SellerCart>,
    val total: Total,
)

@Serializable
data class SellerCart(
    @SerialName("cartEntries")
    val items: List<CartItem>,
    @SerialName("paymentType")
    val paymentMethod: PaymentMethod,
    val sellerCode: String,
    val sellerName: String,
)

@Serializable
data class CartItem(
    @SerialName("cartIdentifier")
    val id: CartIdentifier,
    val buyerId: String,
    val buyingOption: BuyingOption,
    @SerialName("cartEntryId")
    val entryId: String,
    val entryNumber: Int,
    val gst: FormattedData<Double>,
    val hsnCode: String,
    val manufacturerCode: String,
    val manufacturerName: String,
    val price: FormattedData<Double>,
    val productCode: String,
    val productName: String,
    val quantity: FormattedData<Double>,
    val sellerId: String,
    val standardUnit: String,
    @SerialName("stock")
    val stockInfo: StockInfo? = null,
    val type: String,
    @SerialName("sbRetailer")
    val seasonBoyRetailer: SeasonBoyRetailer? = null,
)

@Serializable
data class CartIdentifier(
    val spid: String,
    val seasonBoyRetailerId: String? = null,
)