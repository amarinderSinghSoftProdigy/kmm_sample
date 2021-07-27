package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.mvi.environment
import com.zealsoftsol.medico.data.MapBody
import com.zealsoftsol.medico.data.Response
import kotlinx.coroutines.delay

internal suspend fun <T, V> mockFullResponse(
    delay: Long = environment.requireMocks().delay,
    response: () -> Pair<T?, V>,
): Response<T, V> {
    delay(delay)
    val (t, v) = response()
    return Response(body = t, error = null, validations = v, type = "success")
}

internal suspend fun <T> mockResponse(
    delay: Long = environment.requireMocks().delay,
    response: () -> T?,
): Response<T, MapBody> {
    delay(delay)
    return Response(body = response(), error = null, validations = null, type = "success")
}

internal suspend fun <V> mockValidationResponse(
    delay: Long = environment.requireMocks().delay,
    response: () -> V,
): Response<MapBody, V> {
    delay(delay)
    return Response(body = mapOf(), error = null, validations = response(), type = "success")
}

internal inline fun mockEmptyMapBody() = mapOf<String, String>()