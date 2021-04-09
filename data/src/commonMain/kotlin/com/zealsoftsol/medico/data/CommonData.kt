package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeoPoints(
    val latitude: Double,
    val longitude: Double,
)

@Serializable
data class LocationData(
    val locations: List<String>,
    @SerialName("cityTowns")
    val cities: List<String>,
    val district: String,
    val state: String,
)

@Serializable
data class GeoData(
    val location: String,
    val pincode: String,
    @SerialName("townOrCity")
    val city: String,
    val distance: Double,
    val formattedDistance: String,
    @SerialName("originPoints")
    val origin: GeoPoints,
    @SerialName("destinationPoints")
    val destination: GeoPoints? = null,
) {
    fun fullAddress() = "$location $pincode"
}

@Serializable
data class PaginatedData<T>(
    @SerialName("results")
    val data: List<T>,
    @SerialName("totalResults")
    val total: Int,
)

enum class PaymentMethod(val serverValue: String) {
    CREDIT("CREDIT"),
    CASH("CASH"),
}

enum class SubscriptionStatus(val serverValue: String) {
    PENDING("Pending"),
    SUBSCRIBED("Subscribed"),
    REJECTED("Rejected"),
}

@Deprecated("out of scope usage")
interface PreviewItem {
    val phoneNumber: String
    val tradeName: String
    val gstin: String?
    val panNumber: String?
    val geoData: GeoData
    val isVerified: Boolean?
}

// BASE

@Serializable
sealed class Response {
    abstract val type: String

    val isSuccess: Boolean
        get() = type == "success"

    @Serializable
    class Status(override val type: String) : Response()

    @Serializable
    data class Body<T, V>(
        private val body: T? = null,
        val error: ErrorCode? = null,
        val validations: V? = null,
        override val type: String,
    ) : Response() {

        fun getBodyOrNull(): T? = body.takeIf { isSuccess }

        fun getWrappedBody(): Wrapped<T> = Wrapped(body, isSuccess)

        inline fun getWrappedValidation(): Wrapped<V> = Wrapped(validations, isSuccess)

        inline fun getWrappedError(): Wrapped<ErrorCode> = Wrapped(error, isSuccess)
    }

    data class Wrapped<V>(val entity: V?, val isSuccess: Boolean)
}

typealias SimpleResponse<T> = Response.Body<T, MapBody>

typealias MapBody = Map<String, String>

@Serializable
data class ErrorCode(val title: String = "error", val body: String = "something_went_wrong")