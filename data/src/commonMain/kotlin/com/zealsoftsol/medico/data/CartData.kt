package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartRequest(
    val buyerUnitCode: String,
    val sellerUnitCode: String? = null,
    val productCode: String,
    val buyingOption: BuyingOption,
    @SerialName("cartIdentifier")
    val id: CartIdentifier? = null,
    val quantity: Double? = null,
    @SerialName("freeQty")
    val freeQuantity: Double? = null,
)

@Serializable
data class CartOrderRequest(
    val cartId: String,
    val buyerUnitCode: String,
)

@Serializable
data class CartConfirmData(
    val cartData: CartData,
//    val modifiedEntries
//    val addressData: CustomerAddressData
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
    val total: Total,
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
    @SerialName("totalPrice")
    val subtotalPrice: FormattedData<Double>,
    val productCode: String,
    val productName: String,
    val quantity: FormattedData<Double>,
    @SerialName("freeQty")
    val freeQuantity: FormattedData<Double>,
    val sellerId: String,
    val standardUnit: String,
    @SerialName("stock")
    val stockInfo: StockInfo? = null,
    val type: String,
    @SerialName("sbRetailer")
    val seasonBoyRetailer: SeasonBoyRetailer? = null,
    @SerialName("quoteMessage")
    val quotedData: QuotedData? = null,
    val isPromotionActive: Boolean = false,
    val promotionData: PromotionsData? = null,
    val imageCode: String,
    val mrp: FormattedData<Double>
)

@Serializable
data class PromotionsData(
    val type: String,
    val code: String,
    val buy: FormattedData<Double>,
    val free: FormattedData<Double>,
    val productDiscount: FormattedData<Double>,
    val displayLabel: String,
    val offerPrice: FormattedData<Double>,
    val validity: String
)

@Serializable
data class CartIdentifier(
    val spid: String? = null,
    val seasonBoyRetailerId: String? = null,
)

@Serializable
data class CartInfo(
    val quantity: FormattedData<Double>,
    @SerialName("freeQty")
    val freeQuantity: FormattedData<Double>,
)

@Serializable
data class CartSubmitResponse(
    @SerialName("buyerEmail")
    val email: String,
    val orderDate: String,
    val orderTime: String,
    val sellersOrder: List<SellerOrder>,
    val total: Total,
)

@Serializable
data class SellerOrder(
    val orderId: String,
    @SerialName("sellerUnitCode")
    val unitCode: String,
    @SerialName("sbRetailerTradeName")
    val seasonBoyRetailerName: String?,
    val tradeName: String,
    @SerialName("type")
    val paymentMethod: PaymentMethod,
    val total: Total,
)

@Serializable
data class QuotedData(
    val message: String,
    val isAvailable: Boolean,
)