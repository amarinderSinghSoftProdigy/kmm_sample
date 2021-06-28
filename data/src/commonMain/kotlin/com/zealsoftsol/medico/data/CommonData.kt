package com.zealsoftsol.medico.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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
    val landmark: String,
    @SerialName("cityOrTown")
    val city: String,
    val distance: Double,
    val formattedDistance: String,
    @SerialName("originPoints")
    val origin: GeoPoints,
    @SerialName("destinationPoints")
    val destination: GeoPoints? = null,
) {
    fun fullAddress() = "$city $pincode"
    fun fullLandmark() = "$landmark, $city"
}

@Serializable
data class AddressData(
    @SerialName("addressLine1")
    val address: String,
    val landmark: String,
    @SerialName("cityOrTown")
    val city: String,
    val district: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val location: String,
    val pincode: Int,
    val placeId: String? = null,
    val state: String,
) {
    fun fullAddress() = "$city $pincode"
}

@Serializable
data class FormattedData<T>(
    @SerialName("formattedValue")
    val formatted: String,
    val value: T,
)

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

interface WithTradeName {
    val tradeName: String
}

// BASE

@Serializable
@OptIn(ExperimentalContracts::class)
data class Response<T, V>(
    private val body: T? = null,
    private val error: ErrorCode? = null,
    val validations: V? = null,
    val type: String,
) {
    val isSuccess: Boolean
        get() = type == "success"

    fun getBodyOrNull(): T? = body.takeIf { isSuccess }

    fun provideError() = error ?: ErrorCode.generic

    inline fun onSuccess(action: (value: T) -> Unit): Response<T, V> {
        contract {
            callsInPlace(action, InvocationKind.AT_MOST_ONCE)
        }
        val body = getBodyOrNull()
        if (body != null) action(body)
        return this
    }

    inline fun onError(action: (error: ErrorCode) -> Unit): Response<T, V> {
        contract {
            callsInPlace(action, InvocationKind.AT_MOST_ONCE)
        }
        val body = getBodyOrNull()
        if (body == null) action(provideError())
        return this
    }
}

typealias BodyResponse<T> = Response<T, MapBody>
typealias AnyResponse = Response<MapBody, MapBody>
typealias ValidationResponse<V> = Response<MapBody, V>

typealias MapBody = Map<String, String>

@Serializable
class ErrorCode private constructor(
    val title: String = "error",
    val body: String = "something_went_wrong"
) {

    companion object {
        internal val generic = ErrorCode()
        val uploadFileTooBig = ErrorCode("error", "upload_file_too_big")
        val somethingWentWrong = generic
    }
}