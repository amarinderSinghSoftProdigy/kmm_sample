package com.zealsoftsol.medico.core.network

object CdnUrlProvider {
    private const val BASE_URL = "https://images.medicostores.com"

    enum class Size(val dimension: String) {
        Px123("123"), Px320("320")
    }

    fun urlFor(medicineId: String, size: Size) =
        "${BASE_URL}/img/vp/${medicineId}.jpg"

    fun urlForM(medicineId: String?) =
        "${BASE_URL}/img/m/$medicineId.JPG"
}