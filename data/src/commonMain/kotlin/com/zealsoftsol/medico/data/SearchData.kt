package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val facets: List<Facet>,
    val products: List<ProductSearch>,
    val totalResults: Int,
)

@Serializable
data class Facet(
    val displayName: String,
    val queryId: String,
    val values: List<Value>,
)

@Serializable
data class Value(
    val count: Int,
    val value: String,
)

@Serializable
data class ProductSearch(
    val buyingOption: BuyingOption,
    val compositions: List<String>,
    val formattedMrp: String,
    val formattedPrice: String? = null,
    val id: String,
    val manufacturer: String,
    val marginPercent: String? = null,
    val name: String,
    val shortName: String,
    val code: String,
    val stockInfo: StockInfo? = null,
    val uomName: String,
    val standardUnit: String? = null,
    val sellerInfo: SellerInfo? = null,
)

@Serializable
data class SellerInfo(
    @SerialName("address")
    val geoData: GeoData,
    override val tradeName: String,
    val unitCode: String,
    val spid: String,
    val stockInfo: StockInfo?,
    val priceInfo: PriceInfo?,
    val cartInfo: CartInfo? = null,
) : WithTradeName {

    companion object Anyone {
        val anyone = SellerInfo(
            geoData = GeoData(
                distance = 0.0,
                formattedDistance = "",
                origin = GeoPoints(0.0, 0.0),
                destination = GeoPoints(0.0, 0.0),
                location = "",
                pincode = "",
                city = "",
                landmark = "",
            ),
            tradeName = "",
            unitCode = "",
            spid = "",
            stockInfo = StockInfo(
                availableQty = 0,
                expiry = Expiry(
                    date = 0,
                    formattedDate = "",
                    color = "",
                ),
                formattedStatus = "",
                status = StockStatus.OUT_OF_STOCK,
            ),
            priceInfo = PriceInfo(
                price = PriceData(0.0, ""),
                mrp = PriceData(0.0, ""),
                marginPercent = "",
            )
        )
    }
}

@Serializable
data class PriceInfo(
    val price: PriceData,
    val mrp: PriceData,
    val marginPercent: String,
)

@Serializable
data class PriceData(
    val price: Double,
    val formattedPrice: String
)

@Serializable
data class StockInfo(
    val availableQty: Int,
    @SerialName("expiryDate")
    val expiry: Expiry,
    val formattedStatus: String,
    val status: StockStatus,
)

@Serializable
data class Expiry(
    @SerialName("expiryDate")
    val date: Long,
    @SerialName("formattedExpiryDate")
    val formattedDate: String,
    @SerialName("hexCode")
    val color: String,
)

enum class StockStatus {
    LIMITED_STOCK,
    IN_STOCK,
    OUT_OF_STOCK;
}

enum class BuyingOption {
    BUY,
    QUOTE;
}

@Serializable
data class AutoComplete(
    val query: String,
    @SerialName("suggester")
    val details: String,
    val suggestion: String,
)

data class Filter(
    val name: String,
    val queryId: String,
    val options: List<Option>,
)

sealed class Option {

    data class StringValue(
        val value: String,
        val isSelected: Boolean,
        val isVisible: Boolean = true
    ) : Option()

    object ViewMore : Option()
}