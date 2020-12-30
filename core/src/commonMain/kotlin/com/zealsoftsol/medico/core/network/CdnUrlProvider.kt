package com.zealsoftsol.medico.core.network

object CdnUrlProvider {
    private const val BASE_URL = "https://dev-cdn.medicostores.com"

    enum class Size(val dimension: String) {
        Px123("123")
    }

    fun urlFor(medicineId: String, size: Size) =
        "${BASE_URL}/img/p/$medicineId/${medicineId}_${size.dimension}x${size.dimension}.jpg"
}